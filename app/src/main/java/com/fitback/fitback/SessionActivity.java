package com.fitback.fitback;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.fitback.fitback.Class.Activity;
import com.fitback.fitback.Class.Session;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionActivity extends AppCompatActivity {


    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private FloatingActionButton fabAdd;
    private FloatingActionButton fabSave;
    private Spinner spinnerActivity;
    private Spinner spinnerTimes;
    private List<String> activity;
    private List<String> times;
    private ListView lvActivity;
    private List<String> selected_activity;
    private TextInputEditText etName;
    private TextInputEditText sessionText;
    private ArrayAdapter<String> adapter;
    private Map<String, String> mapTime;
    private List<String> activity_bdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        activity = new ArrayList<>();
        times = new ArrayList<>();
        selected_activity = new ArrayList<>();
        activity_bdd = new ArrayList<>();
        mapTime = new HashMap<>();
        activity.add("Course");
        activity.add("Randonnée");
        activity.add("Natation");
        activity.add("Cyclisme");
        times.add("30sec");
        times.add("1min");
        times.add("2min");
        times.add("3min");
        times.add("4min");
        times.add("5min");
        times.add("10min");
        times.add("15min");
        times.add("20min");
        times.add("30min");
        mapTime.put("30sec", "00:00:30");
        mapTime.put("1min", "00:01:00");
        mapTime.put("2min", "00:02:00");
        mapTime.put("3min", "00:03:00");
        mapTime.put("4min", "00:04:00");
        mapTime.put("5min", "00:05:00");
        mapTime.put("10min", "00:10:00");
        mapTime.put("15min", "00:15:00");
        mapTime.put("20min", "00:20:00");
        mapTime.put("30min", "00:30:00");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        spinnerActivity = findViewById(R.id.spinnerActivity);
        spinnerTimes = findViewById(R.id.spinnerTimes);
        fabAdd = findViewById(R.id.fabAdd);
        etName = findViewById(R.id.etName);
        fabSave = findViewById(R.id.fabSave);
        sessionText = findViewById(R.id.session_text);
        lvActivity = findViewById(R.id.lvActivity);
        DatabaseReference dbRef = database.getReference("Activity").child(firebaseUser.getUid());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Activity a = snapshot.getValue(Activity.class);
                    activity.add(a.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ArrayAdapter<String> adapterActivity = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, activity);
        adapterActivity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivity.setAdapter(adapterActivity);
        ArrayAdapter<String> adapterTimes = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, times);
        adapterTimes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimes.setAdapter(adapterTimes);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, selected_activity);
        lvActivity.setAdapter(adapter);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ses = sessionText.getText().toString();
                if (ses.isEmpty()) {
                    sessionText.setError("Champ vide");
                } else {
                    String activity = ses
                            + " - " + spinnerTimes.getSelectedItem().toString();
                    activity_bdd.add(ses + " - " + mapTime.get(spinnerTimes.getSelectedItem().toString()));
                    selected_activity.add(activity);
                    adapter.notifyDataSetChanged();
                }
                sessionText.setText("");
            }
        });
        lvActivity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                buildAlertDeleteActivity(position);

            }
        });
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();

                if (!name.isEmpty() && selected_activity.size() > 0) {
                    final ProgressDialog progressDialog = new ProgressDialog(SessionActivity.this,
                            R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("sauvegarde...");
                    progressDialog.show();
                    String act = spinnerActivity.getSelectedItem().toString();
                    if (!activity_bdd.isEmpty()) {
                        Session session = new Session(name, act, activity_bdd);
                        database.getReference("Session").child(firebaseUser.getUid()).child(act).child(name)
                                .setValue(session)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            selected_activity.clear();
                                            etName.setText("");
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                    }
                } else {
                    if (name.isEmpty()) {
                        etName.setError("champ vide");
                    } else {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Votre session est vide !", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }

                }
            }
        });
    }

    private void buildAlertDeleteActivity(final int i) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Suppression");
        builder.setMessage("Voulez-vous supprimer votre activité " + selected_activity.get(i) + " ?")
                .setCancelable(false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        selected_activity.remove(i);
                        adapter.notifyDataSetChanged();

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
}
