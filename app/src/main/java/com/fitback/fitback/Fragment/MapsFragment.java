package com.fitback.fitback.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.fitback.fitback.Class.Activity;
import com.fitback.fitback.Class.Internet;
import com.fitback.fitback.Class.Session;
import com.fitback.fitback.Class.Training;
import com.fitback.fitback.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsFragment extends Fragment {
    private static final String TAG = "MapActivity";
    private static final float DEFAULT_ZOOM = 15f;
    public static GoogleMap mMap;
    private Training child;
    public static Bitmap bitmap;
    public static GoogleMap.SnapshotReadyCallback callback;
    private List activityList;
    private List activityListIcon;
    private List sessionList;
    public static String selected_activity;
    public static String selected_session;
    public static Map need_map;
    public static Fragment act;
    public static Boolean session_isSelected = false;
    private View view;
    private GPSTracker gpsTracker;
    private Location mLocation;
    private Internet internet;
    private Button button;
    private Button ibutton;
    private Button session_button;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;
    private int checkedItem;
    private FloatingActionButton startButton;
    private FloatingActionButton stopButton;
    private FirebaseAuth mAuth;
    private ImageButton signal;
    private String mapUrlChild;

    public String getMapUrlChild() {
        return mapUrlChild;
    }

    public void setMapUrlChild(String mapUrlChild) {
        this.mapUrlChild = mapUrlChild;
    }

    public static GoogleMap getmMap() {
        return mMap;
    }

    public static void setmMap(GoogleMap mMap) {
        MapsFragment.mMap = mMap;
    }

    public GPSTracker getGpsTracker() {
        return gpsTracker;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mAuth = FirebaseAuth.getInstance();
        this.mStorageRef = FirebaseStorage.getInstance().getReference("images").child(mAuth.getCurrentUser().getUid());
        this.internet = new Internet(getContext());
        this.act = this;
        this.activityList = new ArrayList();
        this.activityListIcon = new ArrayList();
        this.sessionList = new ArrayList();
        selected_session = "Aucune";

        activityList.add("Course");
        activityList.add("Marche à pied");
        activityList.add("Randonnée");
        activityList.add("Marche nordique");
        activityList.add("Natation");
        activityList.add("Cyclisme");
        activityListIcon.add("running");
        activityListIcon.add("walking");
        activityListIcon.add("trekking");
        activityListIcon.add("hiking");
        activityListIcon.add("swim");
        activityListIcon.add("cyclism");
        sessionList.add("Aucune");
        selected_activity = activityList.get(0).toString();
        need_map = new HashMap();
        need_map.put("Course", "1");
        need_map.put("Marche à pied", "1");
        need_map.put("Randonnée", "1");
        need_map.put("Marche nordique", "1");
        need_map.put("Natation", "0");
        need_map.put("Cyclisme", "1");
    }

    public static int getResId(String resName, Class<?> c) {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (!internet.isConnected()) {
            view = inflater.inflate(R.layout.activity_main, container, false);
            return view;
        }
        // Read from the database
        FirebaseDatabase.getInstance().getReference().child("Activity").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Activity activity = snapshot.getValue(Activity.class);
                    if (!activityList.contains(activity.getName())) {
                        activityList.add(activity.getName());
                        activityListIcon.add(activity.getIcon());
                        need_map.put(activity.getName(), activity.getMap_needed());
                    }
                }
                FirebaseDatabase.getInstance().getReference().child("Training").child(mAuth.getCurrentUser().getUid()).orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            setMapUrlChild(snapshot.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

        if (sessionList.size() == 1) {
            FirebaseDatabase.getInstance().getReference("Session").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("Course").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Session session = snapshot.getValue(Session.class);
                        if (!sessionList.contains(session.getName())) {
                            sessionList.add(session.getName());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        view = inflater.inflate(R.layout.activity_maps, container, false);
        this.signal = (ImageButton) getActivity().findViewById(R.id.map_signal);
        this.signal.setVisibility(View.VISIBLE);
        this.button = (Button) getActivity().findViewById(R.id.fab);
        this.button.setVisibility(View.VISIBLE);
        this.session_button = (Button) getActivity().findViewById(R.id.session_button);
        this.session_button.setVisibility(View.VISIBLE);
        this.ibutton = (Button) getActivity().findViewById(R.id.activity_button);
        this.ibutton.setVisibility(View.VISIBLE);
        this.ibutton.setText(activityList.get(0).toString());
        this.ibutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session_button.setText("Aucune");
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("\tChoisis une activité");
                builder.setSingleChoiceItems(transformList(activityList), checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedItem = which;
                    }

                });
                builder.setNeutralButton("Créer une activité", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialog();
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected_activity = activityList.get(checkedItem).toString();
                        Log.d(TAG, "item choisis" + checkedItem);
                        Toast.makeText(getActivity(), "Vous avez choisi : " + activityList.get(checkedItem).toString(), Toast.LENGTH_SHORT).show();
                        if (selected_activity.length() > 8) {
                            String s = activityList.get(checkedItem).toString().substring(0, 8);
                            ibutton.setText(s);
                        } else
                            ibutton.setText(selected_activity);
                        String activity = activityListIcon.get(checkedItem).toString();
                        int id = getResId(activity, R.drawable.class);
                        ibutton.setCompoundDrawablesWithIntrinsicBounds(id, 0, 0, 0);
                        sessionList.clear();
                        sessionList.add("Aucune");
                        FirebaseDatabase.getInstance().getReference("Session").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(selected_activity).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Session session = snapshot.getValue(Session.class);
                                    sessionList.add(session.getName());
                                    Log.d(TAG, "activité " + sessionList.toString());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });
                builder.create().show();
            }
        });
        this.session_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session_button.setText("Aucune");
                if (sessionList.size() > 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("\tChoisis une session");
                    builder.setSingleChoiceItems(transformList(sessionList), checkedItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkedItem = which;
                        }

                    });
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selected_session = sessionList.get(checkedItem).toString();
                            if (selected_session.length() > 8) {
                                String s = sessionList.get(checkedItem).toString().substring(0, 8);
                                session_button.setText(s);
                            } else
                                session_button.setText(selected_session);

                            if (!selected_session.equals("Aucune"))
                                session_isSelected = true;
                            else
                                session_isSelected = false;
                        }
                    });
                    builder.create().show();
                } else {
                    Toast.makeText(getActivity(), "Aucune session n'est disponible pour cet activité", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //--------------------------start/stop--------------------------//

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                setmMap(mMap);
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                gpsTracker = new GPSTracker(getContext(), mMap, getActivity());
                mLocation = gpsTracker.getLocation();
                if (mLocation != null) {
                    LatLng pos = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pos, 10);
                    mMap.moveCamera(cameraUpdate);
                }

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                callback = new GoogleMap.SnapshotReadyCallback() {

                    @Override
                    public void onSnapshotReady(Bitmap snapshot) {
                        final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                                R.style.AppTheme_Dark_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Snapshot en cours...");
                        progressDialog.show();
                        String map_name = getMapUrlChild().replaceAll("\\s+", "");
                        final StorageReference mountainsRef = mStorageRef.child(map_name + ".jpg");
                        bitmap = snapshot;
                        Bitmap cropMap;
                        try {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            cropMap = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() / 2 - bitmap.getWidth() / 2, bitmap.getWidth(), bitmap.getWidth());
                            bitmap = cropMap;
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            Log.d(TAG, "snapshot de la map");
                            UploadTask uploadTask = mountainsRef.putBytes(data);
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    mountainsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String url = uri.toString();
                                            FirebaseDatabase.getInstance().getReference().child("Training").child(mAuth.getCurrentUser().getUid()).child(getMapUrlChild()).child("urlMap").setValue(url)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            progressDialog.dismiss();
                                                            //Toast.makeText(getContext(), "Snapshot de la map crée !", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    });
                                }
                            });

                        } catch (Exception e) {
                            Log.d(TAG, "maaaap ");
                            e.printStackTrace();
                        }
                    }
                };
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        this.button = (Button) getActivity().findViewById(R.id.fab);
        this.button.setVisibility(View.GONE);
        this.ibutton = (Button) getActivity().findViewById(R.id.activity_button);
        this.ibutton.setVisibility(View.GONE);
        this.signal = (ImageButton) getActivity().findViewById(R.id.map_signal);
        this.signal.setVisibility(View.GONE);
        this.session_button = (Button) getActivity().findViewById(R.id.session_button);
        this.session_button.setVisibility(View.GONE);
        super.onDestroyView();
    }

    public void showDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_layout_dialog, null);
        final EditText etName = (EditText) alertLayout.findViewById(R.id.etName);
        final CheckBox cbMap = (CheckBox) alertLayout.findViewById(R.id.cbMap);
        final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Ajout de l'activité...");
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Crée ton activité !");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (etName.getText().toString().equals(""))
                    Toast.makeText(getActivity(), "Echec, veuillez saisir le nom de l'activité", Toast.LENGTH_SHORT).show();
                else {
                    String map_needed = (cbMap.isChecked()) ? "1" : "0";
                    Activity activity = new Activity(etName.getText().toString(), "bottle", map_needed);
                    progressDialog.show();
                    FirebaseDatabase.getInstance().getReference("Activity")
                            .child(mAuth.getCurrentUser().getUid()).child(activity.getName())
                            .setValue(activity).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }
}