package com.fitback.fitback;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fitback.fitback.Class.Training;
import com.fitback.fitback.Class.User;
import com.fitback.fitback.Fragment.GPSTracker;
import com.fitback.fitback.Fragment.MapsFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SaveActivity extends AppCompatActivity {

    private String TAG = "SaveMap";
    private static String calories = "";
    private static String distance = "";
    private static String time = "";
    private static String painSelected = "Aucune";
    private static String painLevel = "0/10";
    private static String painSelectedAfter = "Aucune";
    private static String painLevelAfter = "0/10";
    private static String feeling = "3";
    private static String name = "";
    private static String remarks = "";
    private boolean isSession;
    private String mapNeeded;
    private Bitmap snapshot = null;
    private String activity_selected;
    private String session_selected = "";
    private FirebaseAuth mAuth;
    private User user;


    public static String getDistance() {
        return distance;
    }

    public static String getCalories() {
        return calories;
    }

    public static String getTime() {
        return time;
    }

    public static String getPainSelected() {
        return painSelected;
    }

    public static String getPainLevel() {
        return painLevel;
    }

    public static String getPainSelectedAfter() {
        return painSelectedAfter;
    }

    public static String getPainLevelAfter() {
        return painLevelAfter;
    }

    public static String getFeeling() {
        return feeling;
    }

    public static String getName() {
        return name;
    }

    public static String getRemarks() {
        return remarks;
    }

    public String getMapNeeded() {
        return this.mapNeeded;
    }

    public String getActivity_selected() {
        return activity_selected;
    }

    public boolean getIsSession() {
        return this.isSession;
    }

    public String getSession_selected() {
        return session_selected;
    }

    public static void setCalories(String calories) {
        SaveActivity.calories = calories;
    }

    public static void setDistance(String distance) {
        SaveActivity.distance = distance;
    }

    public static void setTime(String time) {
        SaveActivity.time = time;
    }

    public static void setPainSelected(String pain) {
        SaveActivity.painSelected = pain;
    }

    public static void setPainLevel(String pain) {
        SaveActivity.painLevel = pain;
    }

    public static void setPainSelectedAfter(String pain) {
        SaveActivity.painSelectedAfter = pain;
    }

    public static void setPainLevelAfter(String pain) {
        SaveActivity.painLevelAfter = pain;
    }

    public static void setFeeling(String feel) {
        SaveActivity.feeling = feel;
    }

    public static void setName(String name) {
        SaveActivity.name = name;
    }

    public static void setRemarks(String rq) {
        SaveActivity.remarks = rq;
    }

    public void setMapNeeded(String mapNeeded) {
        this.mapNeeded = mapNeeded;
    }

    public void setActivity_selected(String activity_selected) {
        this.activity_selected = activity_selected;
    }

    public void setIsSession(boolean isSession) {
        this.isSession = isSession;
    }

    public void setSession_selected(String session_selected) {
        this.session_selected = session_selected;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //ChronometreActivity.act.finish();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Erreur", Toast.LENGTH_SHORT).show();
            }
        });

        this.mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        Intent intent = getIntent();
        Bundle bund = intent.getExtras();

        if (bund != null) {
            String[] cal = ((String) bund.get("calories")).split(" ");
            String[] dist = ((String) bund.get("distance")).split(" ");
            this.setCalories(cal[0] + "\nKcal");
            this.setDistance(dist[0] + "\nKm");
            this.setTime((String) bund.get("time") + "\nTemps");
            this.setPainSelected((String) bund.get("painSelected"));
            this.setPainLevel((String) bund.get("painLevel"));
            this.setPainLevelAfter((String) bund.get("painLevelAfter"));
            this.setPainSelectedAfter((String) bund.get("painSelectedAfter"));
            setActivity_selected((String) bund.get("activity_selected"));
            this.setMapNeeded((String) bund.get("mapNeeded"));
            this.setIsSession((boolean) bund.get("isSession"));
            this.setSession_selected((String) bund.get("sessionName"));
        }
        Log.d(TAG, getActivity_selected());
        final RadioButton rbVB = (RadioButton) findViewById(R.id.FeelingReallyBad);
        final RadioButton rbB = (RadioButton) findViewById(R.id.FeelingBad);
        final RadioButton rbF = (RadioButton) findViewById(R.id.FeelingFine);
        final RadioButton rbW = (RadioButton) findViewById(R.id.FeelingWell);
        final RadioButton rbVW = (RadioButton) findViewById(R.id.FeelingVeryWell);
        final TextView calView = (TextView) findViewById(R.id.CaloriesSave);
        final TextView distanceView = (TextView) findViewById(R.id.DistanceSave);
        final TextView timeView = (TextView) findViewById(R.id.TimeSave);
        final Button saveButton = (Button) findViewById(R.id.saveButton);
        final TextView painLevelView = (TextView) findViewById(R.id.PainLevelBefore);
        final TextView painLevelAfterView = (TextView) findViewById(R.id.PainLevelAfter);
        final EditText remarksView = (EditText) findViewById(R.id.remarkActivity);
        final EditText nameView = (EditText) findViewById(R.id.nameActivity);

        calView.setText(this.getCalories());
        distanceView.setText(this.getDistance());
        timeView.setText(this.getTime());
        painLevelView.setText(this.getPainLevel());
        painLevelAfterView.setText(this.getPainLevelAfter());

        rbVB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbVB.setBackgroundResource(R.drawable.ic_sentiment_very_dissatisfied_black_24dpcolor);
                rbB.setBackgroundResource(R.drawable.ic_sentiment_dissatisfied_black_24dp);
                rbF.setBackgroundResource(R.drawable.ic_sentiment_neutral_black_24dp);
                rbW.setBackgroundResource(R.drawable.ic_sentiment_satisfied_black_24dp);
                rbVW.setBackgroundResource(R.drawable.ic_sentiment_very_satisfied_black_24dp);

            }
        });
        rbB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbVB.setBackgroundResource(R.drawable.ic_sentiment_very_dissatisfied_black_24dp);
                rbB.setBackgroundResource(R.drawable.ic_sentiment_dissatisfied_black_24dpcolor);
                rbF.setBackgroundResource(R.drawable.ic_sentiment_neutral_black_24dp);
                rbW.setBackgroundResource(R.drawable.ic_sentiment_satisfied_black_24dp);
                rbVW.setBackgroundResource(R.drawable.ic_sentiment_very_satisfied_black_24dp);
            }
        });
        rbF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbVB.setBackgroundResource(R.drawable.ic_sentiment_very_dissatisfied_black_24dp);
                rbB.setBackgroundResource(R.drawable.ic_sentiment_dissatisfied_black_24dp);
                rbF.setBackgroundResource(R.drawable.ic_sentiment_neutral_black_24dpcolor);
                rbW.setBackgroundResource(R.drawable.ic_sentiment_satisfied_black_24dp);
                rbVW.setBackgroundResource(R.drawable.ic_sentiment_very_satisfied_black_24dp);
            }
        });

        rbW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbVB.setBackgroundResource(R.drawable.ic_sentiment_very_dissatisfied_black_24dp);
                rbB.setBackgroundResource(R.drawable.ic_sentiment_dissatisfied_black_24dp);
                rbF.setBackgroundResource(R.drawable.ic_sentiment_neutral_black_24dp);
                rbW.setBackgroundResource(R.drawable.ic_sentiment_satisfied_black_24dpcolor);
                rbVW.setBackgroundResource(R.drawable.ic_sentiment_very_satisfied_black_24dp);
            }
        });


        rbVW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbVB.setBackgroundResource(R.drawable.ic_sentiment_very_dissatisfied_black_24dp);
                rbB.setBackgroundResource(R.drawable.ic_sentiment_dissatisfied_black_24dp);
                rbF.setBackgroundResource(R.drawable.ic_sentiment_neutral_black_24dp);
                rbW.setBackgroundResource(R.drawable.ic_sentiment_satisfied_black_24dp);
                rbVW.setBackgroundResource(R.drawable.ic_sentiment_very_satisfied_black_24dpcolor);

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if (getMapNeeded().equals("1")) {
                    MapsFragment.getmMap().setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            MapsFragment.getmMap().snapshot(MapsFragment.callback);
                            MapsFragment.getmMap().clear();
                            GPSTracker.calories = 0f;
                            GPSTracker.speed = 0f;
                            GPSTracker.distance = 0f;
                        }
                    });
                }

                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy, HH:mm:ss");
                SimpleDateFormat title_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String title = title_date.format(c);
                String todayDate = df.format(c);

                if (rbVB.isChecked()) setFeeling("1");
                else if (rbB.isChecked()) setFeeling("2");
                else if (rbF.isChecked()) setFeeling("3");
                else if (rbW.isChecked()) setFeeling("4");
                else if (rbVW.isChecked()) setFeeling("5");
                if (remarksView.getText().length() == 0) setRemarks("Aucune remarque");
                else setRemarks(remarksView.getText().toString());

                if (nameView.getText().length() > 0) setName(nameView.getText().toString());
                else {
                    if (getIsSession()) setName(getSession_selected());
                    else setName(getActivity_selected());
                }
                String[] temps = getTime().split("\n");
                String[] kcal = getCalories().split("\n");
                String[] dist = getDistance().split("\n");

                Training training = new Training(getName(), getActivity_selected(), kcal[0], dist[0], temps[0], "", todayDate, getFeeling(), getPainLevelAfter(), getPainLevel(), getPainSelectedAfter(), getPainSelected(), getRemarks(), getIsSession(), getSession_selected());
                FirebaseDatabase.getInstance().getReference("Training")
                        .child(mAuth.getCurrentUser().getUid()).child(todayDate)
                        .setValue(training).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ChronometreActivity.act.finish();
                            finish();
                        }
                    }
                });
                if (user != null) {
                    user.getProfile().setTotalCalories(user.getProfile().getTotalCalories() + Double.parseDouble(kcal[0].replace(",", ".")));
                    user.getProfile().setTotalDistance(user.getProfile().getTotalDistance() + Double.parseDouble(dist[0].replace(",", ".")));
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).setValue(user);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.trash_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Gson gson = new Gson();
        final Training training = gson.fromJson(getIntent().getStringExtra("training"), Training.class);
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            /**/
            AlertDialog.Builder ab = new AlertDialog.Builder(SaveActivity.this);
            ab.setTitle("Abandonner cet entraînement ?");
            ab.setMessage("Êtes-vous sûr de vouloir abandonner votre entraînement ?");
            ab.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ChronometreActivity.act.finish();
                    MapsFragment.getmMap().clear();
                    GPSTracker.calories = 0f;
                    GPSTracker.speed = 0f;
                    GPSTracker.distance = 0f;
                    finish();
                }
            });
            ab.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            ab.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
