package com.fitback.fitback.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.fitback.fitback.Adapter.SessionsAdapter;
import com.fitback.fitback.Class.Session;
import com.fitback.fitback.R;
import com.fitback.fitback.SessionActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SessionDisplayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SessionDisplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SessionDisplayFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListDetail;
    private Map<String, List<String>> sessionList;
    private ListView sessionListView;
    private List<String> sessionAct;
    private FloatingActionButton addSession;

    public SessionDisplayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SessionDisplayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SessionDisplayFragment newInstance(String param1, String param2) {
        SessionDisplayFragment fragment = new SessionDisplayFragment();
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
        this.sessionList = new HashMap<>();
        this.sessionAct = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_session_display, container, false);
        this.addSession = view.findViewById(R.id.addSession);
        /*expandableListView = (ExpandableListView) view.findViewById(R.id.session_display_list);
        expandableListDetail = new HashMap<String, List<String>>();*/
        this.sessionListView = (ListView) view.findViewById(R.id.lvSessionDisplay);
        final TextView nothing = (TextView) view.findViewById(R.id.nothing);
        final List<String> sessions = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("Session").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            FirebaseDatabase.getInstance().getReference("Session").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(data.getKey()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Session session = snapshot.getValue(Session.class);
                                        sessions.add(session.getName());
                                        sessionList.put(session.getName(), session.getSession());
                                        sessionAct.add(session.getActivity());
                                    }
                                    nothing.setVisibility(View.GONE);
                                    sessionListView.setAdapter(new SessionsAdapter(getContext(), sessions, sessionAct));
                                    sessionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                            List<String> session_detail = sessionList.get(sessions.get(position));
                                            if (!session_detail.get(0).contains("Activité"))
                                                session_detail.add(0, "Activité : " + sessionAct.get(position));
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            builder.setTitle("\tDétails de la session :");
                                            builder.setItems(transformList(session_detail), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                            builder.setNegativeButton("SUPPRIMER", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //Toast.makeText(getActivity(), "Cancel clicked", Toast.LENGTH_SHORT).show();
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                                    alert.setTitle("Suppression de la session");
                                                    alert.setMessage("Etes-vous sûr de vouloir supprimer la session ?");
                                                    alert.setCancelable(true);
                                                    alert.setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            FirebaseDatabase.getInstance().getReference("Session").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                    .child(sessionAct.get(position)).child(sessions.get(position)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        getActivity().onBackPressed();
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
                                            builder.create().show();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        if (sessions.size() == 0) {
                            nothing.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        this.addSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SessionActivity.class);
                startActivity(intent);
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

    public String[] transformList(List list) {
        String[] tab = new String[(int) list.size()];

        for (int i = 0; i < list.size(); ++i) {
            tab[i] = list.get(i).toString();
        }

        return tab;
    }
}
