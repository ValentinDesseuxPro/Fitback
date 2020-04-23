package com.fitback.fitback;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fitback.fitback.Class.Session;
import com.fitback.fitback.Class.Training;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainingDetailActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvDate;
    private TextView tvCalory;
    private TextView tvDistance;
    private TextView tvDuration;
    private ImageView ivMap;
    private ImageView iconFeeling;
    private TextView feeling;
    private TextView painLocationAfter;
    private ImageView iconActivity;
    private TextView beforeLevel;
    private TextView afterLevel;
    private TextView remarks;
    private Button session_detail;
    private Map<String, String> map;
    private Map<String, String> mapSelectActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.training_detail);
        Gson gson = new Gson();
        Training training = gson.fromJson(getIntent().getStringExtra("training"), Training.class);
        this.map = new HashMap<>();
        map.put("1", "very_dissatisfied/Très difficile");
        map.put("2", "dissatisfied/Difficile");
        map.put("3", "neutral/Neutre");
        map.put("4", "satisfied/Satisfaisant");
        map.put("5", "very_satisfied/Très satisfaisant");
        this.mapSelectActivity = new HashMap<>();
        mapSelectActivity.put("Course", "running");
        mapSelectActivity.put("Marche à pied", "walking");
        mapSelectActivity.put("Randonnée", "trekking");
        mapSelectActivity.put("Marche nordique", "hiking");
        mapSelectActivity.put("Natation", "swim");
        mapSelectActivity.put("Cyclisme", "cyclism");
        tvName = (TextView) findViewById(R.id.activity_title);
        tvDate = (TextView) findViewById(R.id.activity_date);
        tvCalory = (TextView) findViewById(R.id.tvCalo);
        tvDistance = (TextView) findViewById(R.id.tvDist);
        tvDuration = (TextView) findViewById(R.id.tvDuree);
        ivMap = (ImageView) findViewById(R.id.ivMap);
        iconFeeling = (ImageView) findViewById(R.id.icon_feeling);
        feeling = (TextView) findViewById(R.id.activity_feeling);
        painLocationAfter = (TextView) findViewById(R.id.activity_pain_after);
        iconActivity = (ImageView) findViewById(R.id.icon_activity);
        beforeLevel = (TextView) findViewById(R.id.before_level);
        afterLevel = (TextView) findViewById(R.id.after_level);
        remarks = (TextView) findViewById(R.id.tvRemarks);
        session_detail = (Button) findViewById(R.id.detail);
        if (training.isSession())
            this.session_detail.setVisibility(View.VISIBLE);
        setView(training);
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
            AlertDialog.Builder ab = new AlertDialog.Builder(TrainingDetailActivity.this);
            ab.setTitle("Supprimer cet entraînement ?");
            ab.setMessage("Êtes-vous sûr de vouloir supprimer votre entraînement ?");
            ab.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    FirebaseDatabase.getInstance().getReference("Training").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(training.getDate()).removeValue();
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

    private void setView(final Training training) {
        tvName.setText(training.getName());
        tvDate.setText(training.getDate());
        tvCalory.setText(String.valueOf(training.getCalory()));
        tvDistance.setText(String.valueOf(training.getDistance()));
        tvDuration.setText(training.getDuration());
        painLocationAfter.setText(training.getPainLocationAfter());
        String icon_value = map.get(training.getFeeling());
        String[] icon = icon_value.split("/");
        String id = "ic_sentiment_" + icon[0] + "_black_24dp";
        iconFeeling.setImageResource(getResId(id, R.drawable.class));
        feeling.setText(icon[1]);
        beforeLevel.setText(training.getPainLevelBefore());
        afterLevel.setText(training.getPainLevelAfter());
        String act_icon_value = mapSelectActivity.get(training.getSelected_activity());
        if (act_icon_value != null)
            iconActivity.setImageResource(getResId(act_icon_value, R.drawable.class));
        else
            iconActivity.setImageResource(getResId("bottle", R.drawable.class));

        remarks.setText(training.getRemarks());

        String url;
        if (!training.getUrlMap().equals(""))
            url = training.getUrlMap();
        else
            url = "https://firebasestorage.googleapis.com/v0/b/fitback-baa10.appspot.com/o/images%2Fmap.jpg?alt=media&token=c5d694ae-ce2b-4478-8053-12a2eb6b34a1";

        Picasso.with(this).load(url).into(ivMap);
        this.session_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Session").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(training.getSelected_activity())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    Session s = data.getValue(Session.class);
                                    List<String> session_detail;
                                    if (training.getSessionName().equals(s.getName())) {
                                        session_detail = s.getSession();
                                        if (session_detail.isEmpty())
                                            session_detail.add("La session " + s.getName() + " a été supprimée !");
                                        AlertDialog.Builder builder = new AlertDialog.Builder(TrainingDetailActivity.this);
                                        builder.setTitle("\tDétails de la session :");
                                        builder.setItems(transformList(session_detail), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        builder.create().show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });
    }

    private int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public String[] transformList(List list) {
        String[] tab = new String[(int) list.size()];
        for (int i = 0; i < list.size(); ++i) {
            tab[i] = list.get(i).toString();
        }
        return tab;
    }
}
