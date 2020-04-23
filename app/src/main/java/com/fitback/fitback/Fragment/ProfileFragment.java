package com.fitback.fitback.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fitback.fitback.Class.User;
import com.fitback.fitback.ProfileEditActivity;
import com.fitback.fitback.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "ProfileFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private CircularImageView civPhoto;
    private ImageView ivSexe;
    private TextView tvName;
    private TextView tvAge;
    private TextView tvHeight;
    private TextView tvWeight;
    private TextView tvIMC;
    private TextView tvCalories;
    private TextView tvDistance;
    private User currentUser;
    private OnFragmentInteractionListener mListener;
    private DecimalFormat df = new DecimalFormat("#.##");

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        civPhoto = (CircularImageView) view.findViewById(R.id.civPhoto);
        tvName = (TextView) view.findViewById(R.id.tvName);
        tvAge = (TextView) view.findViewById(R.id.tvAge);
        tvHeight = (TextView) view.findViewById(R.id.tvHeight);
        tvWeight = (TextView) view.findViewById(R.id.tvWeight);
        tvCalories = (TextView) view.findViewById(R.id.tvCalories);
        tvDistance = (TextView) view.findViewById(R.id.tvDistance);
        tvIMC = (TextView) view.findViewById(R.id.tvIMC);
        ivSexe = (ImageView) view.findViewById(R.id.ivSexe);

        DatabaseReference dbRef = database.getReference("Users").child(firebaseUser.getUid());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    currentUser = user;
                    setViewProfile(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Erreur", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }


    public void setViewProfile(User user) {
        String name = user.getFirstname() + " " + user.getLastname().toUpperCase();
        tvName.setText(name);
        tvAge.setText(String.valueOf(user.getProfile().getAge()));
        tvWeight.setText(String.valueOf(user.getProfile().getWeight()));
        tvHeight.setText(String.valueOf(user.getProfile().getHeight()));
        tvIMC.setText(String.valueOf(user.getProfile().getIMC()));
        tvCalories.setText(String.valueOf(user.getProfile().getTotalCalories()));
        tvDistance.setText(String.valueOf(df.format(user.getProfile().getTotalDistance())));
        String sexe = user.getProfile().getSexe();
        if (!sexe.equals("null")) {
            if (sexe.equals("M")) {
                ivSexe.setImageDrawable(getResources().getDrawable(R.drawable.male));
            } else {
                ivSexe.setImageDrawable(getResources().getDrawable(R.drawable.female));
            }
        }
        String img = user.getProfile().getProfileImgUrl();
        if (!img.equals("null")) {
            Picasso.with(getContext()).load(img).into(civPhoto);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting: {
                if (currentUser != null) {
                    Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
                    intent.putExtra("user", new Gson().toJson(currentUser));
                    startActivity(intent);
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.setting);
        if (item != null)
            item.setVisible(true);
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
