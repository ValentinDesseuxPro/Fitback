package com.fitback.fitback;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fitback.fitback.Class.Profile;
import com.fitback.fitback.Class.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileEditActivity extends AppCompatActivity {
    private static final String TAG = "ProfileEditActivity";
    private TextInputLayout etLastname;
    private TextInputLayout etFirstname;
    private TextInputLayout etHeight;
    private TextInputLayout etWeight;
    private TextInputLayout etAge;
    private CircularImageView civPhoto;
    private RadioGroup radioGroup;
    private RadioButton rbMale;
    private RadioButton rbFemale;
    private Button btnSave;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    private User currentUser;
    private Profile currentProfile;
    private Uri selectedImageUri;
    private final int REQUEST_GET_SINGLE_FILE = 1;
    private boolean isProfilePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        civPhoto = (CircularImageView) findViewById(R.id.civPhoto);
        etLastname = (TextInputLayout) findViewById(R.id.etLastname);
        etFirstname = (TextInputLayout) findViewById(R.id.etFirstname);
        etAge = (TextInputLayout) findViewById(R.id.etAge);
        etHeight = (TextInputLayout) findViewById(R.id.etHeight);
        etWeight = (TextInputLayout) findViewById(R.id.etWeight);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        rbMale = (RadioButton) findViewById(R.id.rbMale);
        rbFemale = (RadioButton) findViewById(R.id.rbFemale);
        btnSave = (Button) findViewById(R.id.btnSave);
        isProfilePhoto = false;
        currentUser = new Gson().fromJson(getIntent().getStringExtra("user"), User.class);
        currentProfile = currentUser.getProfile();
        setViewProfile(currentUser);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("images").child(mAuth.getCurrentUser().getUid());
        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        civPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromGallery();
            }
        });
    }

    private void pickFromGallery() {
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Selectionner une image"), REQUEST_GET_SINGLE_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_GET_SINGLE_FILE) {
                    selectedImageUri = data.getData();
                    // Get the path from the Uri
                    final String path = getPathFromURI(selectedImageUri);
                    if (path != null) {
                        File f = new File(path);
                        selectedImageUri = Uri.fromFile(f);
                    }
                    // Set the image in ImageView
                    civPhoto.setImageURI(selectedImageUri);
                }
            }
        } catch (Exception e) {
            Log.e("FileSelectorActivity", "File select error", e);
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }


    private void setViewProfile(User user) {
        etFirstname.getEditText().setText(user.getFirstname());
        etLastname.getEditText().setText(user.getLastname());
        etAge.getEditText().setText(String.valueOf(user.getProfile().getAge()));
        etWeight.getEditText().setText(String.valueOf(user.getProfile().getWeight()));
        etHeight.getEditText().setText(String.valueOf(user.getProfile().getHeight()));
        if (user.getProfile().getSexe().equals("M")) {
            rbMale.setChecked(true);
        } else {
            rbFemale.setChecked(false);
        }
        String img = user.getProfile().getProfileImgUrl();
        if (!img.equals("null")) {
            Picasso.with(getApplicationContext()).load(img).into(civPhoto);
        }
    }

    private void save() {
        btnSave.setEnabled(false);
        String lastname = etLastname.getEditText().getText().toString().trim();
        String firstname = etFirstname.getEditText().getText().toString().trim();
        double height = Double.parseDouble(etHeight.getEditText().getText().toString().trim());
        double weight = Double.parseDouble(etWeight.getEditText().getText().toString().trim());
        int age = Integer.parseInt(etAge.getEditText().getText().toString().trim());
        RadioButton checkedSexe = (RadioButton) this.findViewById(radioGroup.getCheckedRadioButtonId());
        String sexe = checkedSexe != null ? checkedSexe.getText().toString().trim() : "null";
        double IMC = currentProfile.calculIMC(height, weight);
        Profile updateProfile = new Profile(currentProfile.getTotalDistance(),
                currentProfile.getTotalCalories(), IMC,
                weight, height, age, sexe, "null");
        User updateUser = new User(lastname, firstname, currentUser.getEmail(), updateProfile);
        final ProgressDialog progressDialog = new ProgressDialog(ProfileEditActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sauvegarde...");
        progressDialog.show();
        if (selectedImageUri != null) {
            isProfilePhoto = true;
            uploadProfileImage(progressDialog);
        }

        mDatabase.setValue(updateUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (!isProfilePhoto) {
                        progressDialog.dismiss();
                        finish();
                    }
                } else {
                    progressDialog.dismiss();
                    btnSave.setEnabled(true);
                    Toast.makeText(ProfileEditActivity.this, "Échec", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void uploadProfileImage(final ProgressDialog progressDialog) {
        final StorageReference fileRef = mStorageRef.child(mAuth.getCurrentUser().getUid() + "." + getFileExtension(selectedImageUri));
        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(
                    selectedImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bmp = BitmapFactory.decodeStream(imageStream);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        UploadTask uploadTask = fileRef.putBytes(byteArray);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mDatabase.child("profile").child("profileImgUrl").setValue(uri.toString());
                    }
                });
                progressDialog.dismiss();
                finish();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "Erreur upload", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
