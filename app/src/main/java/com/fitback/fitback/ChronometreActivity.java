package com.fitback.fitback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fitback.fitback.Fragment.GPSTracker;
import com.fitback.fitback.Fragment.MapsFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ChronometreActivity extends AppCompatActivity {


    final static String TAG = "ChronoActivity";
    public static Boolean startActivity = false;
    public static Boolean breakActivity = false;
    public static Boolean stopActivity = false;
    public static Bitmap bitmap;
    public static Bitmap snapshot;
    public static Activity act;
    private static String painSelectedAfter = "Aucune";
    private static String painLevelAfter = "0/10";
    private long timeWhenStopped = 0;
    private float distance = 0;
    private float speed = 0;
    private float calories = 0;
    private boolean session;
    private String painSelected = "";
    private String painLevel = "";
    private String mapNeeded;
    private ArrayList<String> steps;
    private LatLng startPoint = null;
    private LatLng endPoint = null;
    private LatLngBounds.Builder buildr;
    private LatLngBounds bounds;

    public static void setPainSelectedAfter(String pain) {
        ChronometreActivity.painSelectedAfter = pain;
    }

    public static void setPainLevelAfter(String pain) {
        ChronometreActivity.painLevelAfter = pain;
    }

    public static String getPainSelectedAfter() {
        return painSelectedAfter;
    }

    public static String getPainLevelAfter() {
        return painLevelAfter;
    }

    public String getMapNeeded() {
        return mapNeeded;
    }

    public void setMapNeeded(String mapNeeded) {
        this.mapNeeded = mapNeeded;
    }

    public void setTimeWhenStopped(long timeWhenStopped) {
        this.timeWhenStopped = timeWhenStopped;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public void setPainSelected(String pain) {
        this.painSelected = pain;
    }

    public void setPainLevel(String painL) {
        this.painLevel = painL;
    }

    public void setSession(boolean sess) {
        this.session = sess;
    }

    public void setSteps(ArrayList<String> steps) {
        this.steps = steps;
    }

    public long getTimeWhenStopped() {
        return this.timeWhenStopped;
    }

    public float getDistance() {
        return this.distance;
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getCalories() {
        return this.calories;
    }

    public String getPainSelected() {
        return this.painSelected;
    }

    public String getPainLevel() {
        return this.painLevel;
    }

    public boolean getSession() {
        return this.session;
    }

    public ArrayList<String> getSteps() {
        return steps;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.act = this;
        setContentView(R.layout.activity_chronometre);
        Intent intent = getIntent();
        Bundle bund = intent.getExtras();
        if (bund != null) {
            this.setPainSelected((String) bund.get("painSelected"));
            this.setPainLevel((String) bund.get("painLevel"));
            this.setMapNeeded((String) bund.get("mapNeeded"));
            this.setSession((boolean) bund.get("session"));
            if (getSession()) {
                this.setSteps((ArrayList<String>) bund.get("steps"));
                Log.d("FFFFFFFFFFF", bund.get("steps").toString());
            }
        }

        final DecimalFormat df = new DecimalFormat("0.00");
        final ImageButton StartButton = (ImageButton) findViewById(R.id.StartButton);
        final ImageButton BreakButton = (ImageButton) findViewById(R.id.BreakButton);
        final LinearLayout LayoutRestart = (LinearLayout) findViewById(R.id.LayoutRestart);
        final ImageButton RestartButton = (ImageButton) findViewById(R.id.RestartButton);
        final ImageButton QuitButton = (ImageButton) findViewById(R.id.QuitButton);
        final TextView speed = (TextView) findViewById(R.id.Speed);
        final TextView distance = (TextView) findViewById(R.id.Distance);
        final TextView calories = (TextView) findViewById(R.id.Calories);
        final Chronometer simpleChronometer = (Chronometer) findViewById(R.id.Chrono);

        /*ICI MODIF CHRONO*/
        final TextView indication = (TextView) findViewById(R.id.indication);
        final List<String> tabActivities = new ArrayList<>();
        final List<String> tabTime = new ArrayList<>();
        if (!this.getSession()) indication.setVisibility(View.GONE);
        else {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            Long initialTime = 0l;
            Date sum;
            String strTime1;
            for (int i = 0; i < getSteps().size(); i++) {
                String args[] = getSteps().get(i).split(" - ");
                tabActivities.add(args[0]);
                strTime1 = args[1];
                try {
                    Date date1 = dateFormat.parse(strTime1);
                    if (i == 0) initialTime = date1.getTime();
                    else initialTime += date1.getTime();
                    sum = new Date(initialTime);
                    tabTime.add(dateFormat.format(sum));

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            tabActivities.add("Keep going !");
            indication.setText(tabActivities.get(0));
            tabActivities.remove(0);
        }

        /*FIN MODIF CHRONO*/

        simpleChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            private long mTicks = 0;
            private Location pos = GPSTracker.getPosition();

            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                int h = (int) (time / 3600000);
                int m = (int) (time - h * 3600000) / 60000;
                int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                String t = (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":" + (s < 10 ? "0" + s : s);
                chronometer.setText(t);
                if (tabActivities.size() > 0) {
                    if (chronometer.getText().equals(tabTime.get(0))) {
                        indication.setText(tabActivities.get(0));
                        tabActivities.remove(0);
                        tabTime.remove(0);
                    }
                }
                //speed.setText((df.format(GPSTracker.speed*3.6f))+" Km/H");
                distance.setText((df.format(GPSTracker.distance / 1000f)) + " Km");
                calories.setText((df.format(GPSTracker.calories)) + " Kcal");

                /* Modif pour set la speed à 0 quand on ne bouge pas pdt 2s*/
                if(mTicks%10==0 && pos == GPSTracker.getPosition()){
                    speed.setText("0,00 Km/H");
                } else {
                    speed.setText((df.format(GPSTracker.speed * 3.6f)) + " Km/H");
                }
                pos = GPSTracker.getPosition();
                /*Fin modif */

                mTicks++;
            }
        });
        simpleChronometer.setBase(SystemClock.elapsedRealtime());
        simpleChronometer.setText("00:00:00");


        StartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleChronometer.setBase(SystemClock.elapsedRealtime() + getTimeWhenStopped());
                simpleChronometer.start();
                StartButton.setVisibility(View.GONE);
                BreakButton.setVisibility(View.VISIBLE);
                if (getMapNeeded().equals("0")) GPSTracker.setStart(false);
                else {
                    GPSTracker.setStart(true);
                    //startPoint = new LatLng(48.8534, 2.3488);
                    startPoint = new LatLng(GPSTracker.position.getLatitude(), GPSTracker.position.getLongitude());
                    MapsFragment.getmMap().addMarker(new MarkerOptions().position(startPoint).title("Départ").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }
            }
        });
        BreakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimeWhenStopped(simpleChronometer.getBase() - SystemClock.elapsedRealtime());
                simpleChronometer.stop();
                BreakButton.setVisibility(View.GONE);
                LayoutRestart.setVisibility(View.VISIBLE);
                GPSTracker.setStart(false);
                /*Si on se met en pause on set la vitesse à 0 car on ne bouge plus*/
                speed.setText("0,00 Km/H");

            }
        });

        RestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleChronometer.setBase(SystemClock.elapsedRealtime() + getTimeWhenStopped());
                simpleChronometer.start();
                LayoutRestart.setVisibility(View.GONE);
                BreakButton.setVisibility(View.VISIBLE);
                if (getMapNeeded().equals("0")) GPSTracker.setStart(false);
                else GPSTracker.setStart(true);
                breakActivity = true;
            }
        });

        QuitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimeWhenStopped(simpleChronometer.getBase() - SystemClock.elapsedRealtime());
                simpleChronometer.stop();
                LayoutInflater layoutInflater = LayoutInflater.from(view.getContext());
                View progressDialogBoxafter = layoutInflater.inflate(R.layout.dialog_pain_after, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setView(progressDialogBoxafter);
                final AlertDialog dial = builder.show();
                final CheckBox rbBack = (CheckBox) progressDialogBoxafter.findViewById(R.id.FeelingBack);
                final CheckBox rbFeet = (CheckBox) progressDialogBoxafter.findViewById(R.id.FeelingFeet);
                final CheckBox rbL = (CheckBox) progressDialogBoxafter.findViewById(R.id.FeelingLungs);
                final CheckBox rbBr = (CheckBox) progressDialogBoxafter.findViewById(R.id.FeelingBrain);
                final CheckBox rbM = (CheckBox) progressDialogBoxafter.findViewById(R.id.FeelingMuscle);
                final SeekBar bar = (SeekBar) progressDialogBoxafter.findViewById(R.id.seekBar);
                final TextView progress = (TextView) progressDialogBoxafter.findViewById(R.id.ProgressValue);

                rbBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rbBack.isChecked())
                            rbBack.setButtonDrawable(R.mipmap.ic_backpain_color);
                        else
                            rbBack.setButtonDrawable(R.mipmap.ic_backpain);
                    }
                });
                rbFeet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rbFeet.isChecked())
                            rbFeet.setButtonDrawable(R.mipmap.ic_feetpain_color);
                        else
                            rbFeet.setButtonDrawable(R.mipmap.ic_feetpain);
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

                Button validateButton = (Button) progressDialogBoxafter.findViewById(R.id.ValidateButton);
                validateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String pain = "";
                        String painLevel = progress.getText().toString();
                        if (rbBack.isChecked()) {
                            pain += "Dos";
                        }
                        if (rbBr.isChecked()) {
                            if (pain.length() > 0) pain += ", ";
                            pain += "Tête";
                        }
                        if (rbFeet.isChecked()) {
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
                        if (!rbL.isChecked() && !rbM.isChecked() && !rbFeet.isChecked() && !rbBr.isChecked() && !rbBack.isChecked()) {
                            pain = "Aucune";
                        }

                        setPainLevelAfter(painLevel);
                        setPainSelectedAfter(pain);
                        dial.dismiss();
                        Intent intent = new Intent(ChronometreActivity.this, SaveActivity.class);
                        intent.putExtra("painSelected", getPainSelected());
                        intent.putExtra("painLevel", getPainLevel());
                        intent.putExtra("distance", distance.getText());
                        intent.putExtra("calories", calories.getText());
                        intent.putExtra("time", simpleChronometer.getText().toString());
                        intent.putExtra("painSelectedAfter", getPainSelectedAfter());
                        intent.putExtra("painLevelAfter", getPainLevelAfter());
                        intent.putExtra("activity_selected", MapsFragment.selected_activity);
                        intent.putExtra("mapNeeded", getMapNeeded());
                        intent.putExtra("isSession", getSession());
                        if (getSession())
                            intent.putExtra("sessionName", MapsFragment.selected_session);
                        startActivity(intent);
                        GPSTracker.setStart(false);
                        if (getMapNeeded().equals("1")) {
                            endPoint = new LatLng(GPSTracker.position.getLatitude(), GPSTracker.position.getLongitude());
                            MapsFragment.getmMap().addMarker(new MarkerOptions().position(endPoint).title("Arrivée").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            buildr = new LatLngBounds.Builder();
                            buildr.include(startPoint);
                            buildr.include(endPoint);
                            bounds = buildr.build();
                            int width = getResources().getDisplayMetrics().widthPixels;
                            int height = getResources().getDisplayMetrics().heightPixels;
                            int padding = (int) (width * 0.10);
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                            MapsFragment.getmMap().moveCamera(cu);
                            MapsFragment.getmMap().moveCamera(CameraUpdateFactory.zoomTo(MapsFragment.getmMap().getCameraPosition().zoom - 1f));
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        Log.d("chrono", "end activity");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder ab = new AlertDialog.Builder(ChronometreActivity.this);
        ab.setTitle("Abandonner votre activité ?");
        ab.setMessage("Êtes-vous sûr de vouloir quitter votre activité ?");
        ab.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ChronometreActivity.this.finish();
                MapsFragment.getmMap().clear();
                GPSTracker.calories = 0f;
                GPSTracker.speed = 0f;
                GPSTracker.distance = 0f;
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
