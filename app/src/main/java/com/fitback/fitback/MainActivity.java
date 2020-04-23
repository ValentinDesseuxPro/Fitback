package com.fitback.fitback;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.fitback.fitback.Class.Internet;
import com.fitback.fitback.Class.Profile;
import com.fitback.fitback.Class.User;
import com.fitback.fitback.Fragment.ActivitiesFragment;
import com.fitback.fitback.Fragment.MapsFragment;
import com.fitback.fitback.Fragment.ProfileFragment;
import com.fitback.fitback.Fragment.SessionDisplayFragment;
import com.fitback.fitback.Fragment.SessionFragment;
import com.fitback.fitback.Fragment.StatisticFragment;
import com.fitback.fitback.Fragment.TrainingFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = "MainActivity";
    private FirebaseUser user;
    private CircularImageView user_icon;
    private TextView user_name;
    private TextView user_mail;
    private boolean pressed = false;
    private Boolean internet_form = false;
    private Boolean gps_form = false;
    private Boolean mLocationPermissionsGranted = false;
    private Boolean gps_granted = false;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private ArrayList<String> listSession = new ArrayList<>();
    public static Profile profileCurrentUser = null;

    public ArrayList<String> getListSession() {
        return listSession;
    }

    public void setListSession(ArrayList<String> listSession) {
        this.listSession = listSession;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Internet internet = new Internet(getApplicationContext());
        if (!internet.isConnected()) {
            buildAlertMessageNoInternet();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        user = FirebaseAuth.getInstance().getCurrentUser();
        Button fab = (Button) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getListSession().clear();
                if (MapsFragment.session_isSelected) {
                    FirebaseDatabase.getInstance().getReference("Session").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(MapsFragment.selected_activity).child(MapsFragment.selected_session)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int i = 0;
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        if (i == 2) {
                                            String s = snapshot.getValue().toString();
                                            String[] spliter = s.split("\\[");
                                            spliter = spliter[1].split("\\]");
                                            String res = spliter[0];
                                            String[] valuesString = res.split(", ");

                                            for (int j = 0; j < valuesString.length; j++) {
                                                getListSession().add(valuesString[j]);
                                            }
                                        }
                                        i++;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }

                final String map_choice = MapsFragment.need_map.get(MapsFragment.selected_activity).toString();
                Log.d(TAG, "besoin de map : " + map_choice);
                LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
                View progressDialogBox = layoutInflater.inflate(R.layout.dialog_pain_progressebar, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setView(progressDialogBox);
                final AlertDialog dialog = builder.show();
                final CheckBox rbB = (CheckBox) progressDialogBox.findViewById(R.id.FeelingBack);
                final CheckBox rbF = (CheckBox) progressDialogBox.findViewById(R.id.FeelingFeet);
                final CheckBox rbL = (CheckBox) progressDialogBox.findViewById(R.id.FeelingLungs);
                final CheckBox rbBr = (CheckBox) progressDialogBox.findViewById(R.id.FeelingBrain);
                final CheckBox rbM = (CheckBox) progressDialogBox.findViewById(R.id.FeelingMuscle);
                final SeekBar bar = (SeekBar) progressDialogBox.findViewById(R.id.seekBar);
                final TextView progress = (TextView) progressDialogBox.findViewById(R.id.ProgressValue);

                rbB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rbB.isChecked())
                            rbB.setButtonDrawable(R.mipmap.ic_backpain_color);
                        else
                            rbB.setButtonDrawable(R.mipmap.ic_backpain);
                    }
                });
                rbF.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rbF.isChecked())
                            rbF.setButtonDrawable(R.mipmap.ic_feetpain_color);
                        else
                            rbF.setButtonDrawable(R.mipmap.ic_feetpain);
                    }
                });
                rbL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rbL.isChecked())
                            rbL.setButtonDrawable(R.mipmap.ic_lungspain_color);
                        else
                            rbL.setButtonDrawable(R.mipmap.ic_lungspain);
                    }
                });
                rbBr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rbBr.isChecked())
                            rbBr.setButtonDrawable(R.mipmap.ic_brainpain_color);
                        else
                            rbBr.setButtonDrawable(R.mipmap.ic_braipain);
                    }
                });

                rbM.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rbM.isChecked())
                            rbM.setButtonDrawable(R.mipmap.ic_musclespain_color);
                        else
                            rbM.setButtonDrawable(R.mipmap.ic_musclespain);
                    }
                });

                bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        progress.setText(String.valueOf(i) + "/10");

                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }

                });


                Button validateButton = (Button) progressDialogBox.findViewById(R.id.ValidateButton);
                validateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String pain = "";
                        String painLevel = progress.getText().toString();
                        if (rbB.isChecked()) {
                            pain += "Dos";
                        }
                        if (rbBr.isChecked()) {
                            if (pain.length() > 0) pain += ", ";
                            pain += "Tête";
                        }
                        if (rbF.isChecked()) {
                            if (pain.length() > 0) pain += ", ";
                            pain += "Pieds/Jambes";
                        }
                        if (rbM.isChecked()) {
                            if (pain.length() > 0) pain += ", ";
                            pain += "Bras";
                        }
                        if (rbL.isChecked()) {
                            if (pain.length() > 0) pain += ", ";
                            pain += "Respiratoire";
                        }
                        if (!rbL.isChecked() && !rbM.isChecked() && !rbF.isChecked() && !rbBr.isChecked() && !rbB.isChecked()) {
                            pain = "Aucune";
                        }

                        Intent intent = new Intent(MainActivity.this, ChronometreActivity.class);
                        intent.putExtra("painSelected", pain);
                        intent.putExtra("painLevel", painLevel);
                        intent.putExtra("mapNeeded", map_choice);
                        intent.putExtra("session", MapsFragment.session_isSelected);
                        if (MapsFragment.session_isSelected)
                            intent.putExtra("steps", getListSession());
                        dialog.dismiss();
                        startActivity(intent);

                    }

                });
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getLocationPermission();
        if (this.mLocationPermissionsGranted && statusCheck()) {
            MapsFragment map = new MapsFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, map).commit();
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerview = navigationView.getHeaderView(0);

        this.user_icon = (CircularImageView) headerview.findViewById(R.id.drawer_image);
        this.user_name = (TextView) headerview.findViewById(R.id.drawer_name);
        this.user_mail = (TextView) headerview.findViewById(R.id.drawer_mail);

        FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Profile profile = user.getProfile();
                profileCurrentUser = user.getProfile();
                String name = user.getFirstname() + " " + user.getLastname().toUpperCase();
                user_name.setText(name);
                user_mail.setText(user.getEmail());
                Picasso.with(getApplicationContext()).load(profile.getProfileImgUrl()).into(user_icon);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Button logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                for (UserInfo userInfo : user.getProviderData()) {
                    if (userInfo.getProviderId().equals("facebook.com")) {
                        LoginManager.getInstance().logOut();
                    }
                }
                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            Log.d("ICILA", Integer.toString(getSupportFragmentManager().getBackStackEntryCount()));
            pressed = true;
            super.onBackPressed();
            for (int f = 0; f < getSupportFragmentManager().getBackStackEntryCount(); f++) {
                getSupportFragmentManager().popBackStack();
            }
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            int size = navigationView.getMenu().size();
            for (int i = 0; i < size; i++) {
                navigationView.getMenu().getItem(i).setChecked(false);
            }
        } else {
            AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
            ab.setTitle("Quitter Fitback ?");
            ab.setMessage("Voulez-vous quitter l'application ?");
            ab.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    //if you want to kill app . from other then your main avtivity.(Launcher)
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);

                    //if you want to finish just current activity
                    MainActivity.this.finish();
                }
            });
            ab.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            ab.show();
            //super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            for (UserInfo userInfo : user.getProviderData()) {
                if (userInfo.getProviderId().equals("facebook.com")) {
                    LoginManager.getInstance().logOut();
                }
            }
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_map) {
            getLocationPermission();
            if (this.mLocationPermissionsGranted && statusCheck()) {
                // Handle the camera action
                fragment = new MapsFragment();
            } else
                fragment = null;
        } else if (id == R.id.nav_profile) {
            fragment = new ProfileFragment();
        } else if (id == R.id.nav_training) {
            fragment = new TrainingFragment();

        } else if (id == R.id.nav_stats) {
            fragment = new StatisticFragment();
        } else if (id == R.id.nav_session) {
            fragment = new SessionDisplayFragment();
        } else if (id == R.id.nav_activities) {
            fragment = new ActivitiesFragment();
        } else if (id == R.id.nav_sessionTraining) {
            fragment = new SessionFragment();
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction mFragmentTransaction = fragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.frame_container, fragment).addToBackStack(null).commit();
            //mFragmentTransaction.addToBackStack(null);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Votre GPS semble désactivé, voulez-vous l'activer ?")
                .setCancelable(false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        gps_form = true;
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    private void buildAlertMessageNoInternet() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Votre connexion Internet n'est pas disponible, voulez-vous l'activer ?")
                .setCancelable(false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        internet_form = true;
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.gps_form) {
            finish();
            startActivity(getIntent());
            this.gps_form = false;
        }
        if (this.gps_granted) {
            getLocationPermission();
            if (this.mLocationPermissionsGranted) {
                finish();
                startActivity(getIntent());
                this.gps_granted = false;
            }
        }
        if (this.internet_form) {
            finish();
            startActivity(getIntent());
            this.internet_form = false;
        }
    }


    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                this.mLocationPermissionsGranted = true;
                return;
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
                this.gps_granted = true;
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            this.gps_granted = true;
        }
        this.mLocationPermissionsGranted = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        this.mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            this.mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    this.mLocationPermissionsGranted = true;
                    return;
                }
            }
        }
    }
}
