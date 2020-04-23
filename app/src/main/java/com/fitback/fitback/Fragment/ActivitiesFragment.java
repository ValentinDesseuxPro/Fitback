package com.fitback.fitback.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fitback.fitback.Adapter.ActivitiesAdapter;
import com.fitback.fitback.Class.Activity;
import com.fitback.fitback.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ActivitiesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ActivitiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivitiesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ActivitiesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActivitiesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActivitiesFragment newInstance(String param1, String param2) {
        ActivitiesFragment fragment = new ActivitiesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_activities, container, false);
        final ListView lvActivities = (ListView) view.findViewById(R.id.lvActivities);
        final TextView nothing = (TextView) view.findViewById(R.id.nothing);
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Activity").child(FirebaseAuth.getInstance().getUid());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<Activity> activities = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Activity act = data.getValue(Activity.class);
                    activities.add(act);
                }

                if (activities.size() == 0) {
                    nothing.setVisibility(View.VISIBLE);
                }

                lvActivities.setAdapter(new ActivitiesAdapter(getContext(), activities));
                lvActivities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        LayoutInflater inflater = getLayoutInflater();
                        View alertLayout = inflater.inflate(R.layout.custom_layout_dialog, null);
                        final String act_name = activities.get(position).getName();
                        final EditText etName = (EditText) alertLayout.findViewById(R.id.etName);
                        final CheckBox cbMap = (CheckBox) alertLayout.findViewById(R.id.cbMap);
                        etName.setText(activities.get(position).getName());
                        if (activities.get(position).getMap_needed().equals("1"))
                            cbMap.setChecked(true);
                        final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                                R.style.AppTheme_Dark_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Modification de l'activité...");
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setTitle("Mon activité");
                        alert.setView(alertLayout);
                        alert.setCancelable(true);
                        alert.setPositiveButton("MODIFIER", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String map_needed = (cbMap.isChecked()) ? "1" : "0";
                                Activity activity = new Activity(etName.getText().toString(), "bottle", map_needed);
                                progressDialog.show();
                                FirebaseDatabase.getInstance().getReference("Activity").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(act_name).removeValue();
                                FirebaseDatabase.getInstance().getReference("Activity")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(activity.getName())
                                        .setValue(activity).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        });
                        alert.setNegativeButton("SUPPRIMER", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(getActivity(), "Cancel clicked", Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                alert.setTitle("Suppression de l'activité");
                                alert.setMessage("Etes-vous sûr de vouloir supprimer l'activité ?");
                                alert.setCancelable(true);
                                alert.setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        progressDialog.show();
                                        FirebaseDatabase.getInstance().getReference("Activity").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child(act_name).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });
                                    }
                                });
                                alert.setNegativeButton("NON", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                alert.show();
                            }
                        });

                        final AlertDialog dialog = alert.create();
                        dialog.show();
                    }
                });
                final FloatingActionButton addA = (FloatingActionButton) view.findViewById(R.id.addAct);
                addA.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                        alert.setCancelable(true);
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final TextView nothing = (TextView) view.findViewById(R.id.nothing);

                                if (etName.getText().toString().equals(""))
                                    Toast.makeText(getActivity(), "Echec, veuillez saisir le nom de l'activité", Toast.LENGTH_SHORT).show();
                                else {
                                    String map_needed = (cbMap.isChecked()) ? "1" : "0";
                                    Activity activity = new Activity(etName.getText().toString(), "bottle", map_needed);
                                    progressDialog.show();
                                    FirebaseDatabase.getInstance().getReference("Activity")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(activity.getName())
                                            .setValue(activity).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                nothing.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }

                            }
                        });
                        AlertDialog dialog = alert.create();
                        dialog.show();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
