package com.fitback.fitback;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fitback.fitback.Class.Internet;
import com.fitback.fitback.Class.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    private TextInputLayout etLastname;
    private TextInputLayout etFirstname;
    private TextInputLayout etEmail;
    private TextInputLayout etPassword;
    private Button btnSignup;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        etLastname = (TextInputLayout) findViewById(R.id.etLastname);
        etFirstname = (TextInputLayout) findViewById(R.id.etFirstname);
        etEmail = (TextInputLayout) findViewById(R.id.etEmail);
        etPassword = (TextInputLayout) findViewById(R.id.etPassword);
        btnSignup = (Button) findViewById(R.id.btnSignup);
        TextView tvLogin = (TextView) findViewById(R.id.tvLogin);
        mAuth = FirebaseAuth.getInstance();
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signup() {
        String lastname = etLastname.getEditText().getText().toString().trim();
        String firstname = etFirstname.getEditText().getText().toString().trim();
        String email = etEmail.getEditText().getText().toString().trim();
        String password = etPassword.getEditText().getText().toString().trim();
        final User user = new User(lastname, firstname, email);
        Internet internet = new Internet(getApplicationContext());
        if (!isValid(user, password) && internet.isConnected()) {
            btnSignup.setEnabled(true);
        } else {
            btnSignup.setEnabled(false);
            final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Création...");
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(mAuth.getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    progressDialog.dismiss();
                                                    startActivity(new Intent(getApplication(), MainActivity.class));
                                                } else {
                                                    progressDialog.dismiss();
                                                    btnSignup.setEnabled(true);
                                                    Toast.makeText(SignupActivity.this, "Échec", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                // If sign in fails
                                progressDialog.dismiss();
                                btnSignup.setEnabled(true);
                                Toast.makeText(SignupActivity.this, "Échec", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public boolean isValid(User user, String password) {
        boolean valid = true;
        if (user.getLastname().isEmpty()) {
            etLastname.setError("champ vide");
            valid = false;
        } else {
            etLastname.setError(null);
        }
        if (user.getLastname().length() < 4) {
            etLastname.setError("minimum 4 caractères ");
            valid = false;
        } else {
            etLastname.setError(null);
        }


        if (user.getFirstname().isEmpty()) {
            etFirstname.setError("champ vide");
            valid = false;
        } else {
            etFirstname.setError(null);
        }
        if (user.getFirstname().length() < 4) {
            etFirstname.setError("minimum 4 caractères ");
            valid = false;
        } else {
            etFirstname.setError(null);
        }
        if (user.getEmail().isEmpty()) {
            etEmail.setError("champ vide");
            valid = false;
        } else {
            etEmail.setError(null);
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(user.getEmail()).matches()) {
            etEmail.setError("email invalide");
            valid = false;
        } else {
            etEmail.setError(null);
        }
        if (password.isEmpty()) {
            etPassword.setError("champ vide");
            valid = false;
        } else {
            etPassword.setError(null);
        }
        if (password.length() < 6) {
            etPassword.setError("minimum 6 caractères");
            valid = false;
        } else {
            etPassword.setError(null);
        }
        return valid;
    }
}
