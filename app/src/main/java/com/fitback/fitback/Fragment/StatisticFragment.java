package com.fitback.fitback.Fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fitback.fitback.Class.Training;
import com.fitback.fitback.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatisticFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatisticFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "StatisticFragment";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LineChart lineChartStats;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private String[] periode = {"Mois", "Semaine", "Jour"};
    private TextView tvNbActivity;
    private TextView tvNbCalory;
    private TextView tvNbDistance;
    private List<Entry> lines;
    private boolean firstPeriode;
    private List<Training> trainings;
    private ArrayAdapter<String> adapterPeriode;
    private Map<Integer, Integer> mapActivities;
    private Map<Integer, Integer> mapNbActivites;

    private OnFragmentInteractionListener mListener;

    public StatisticFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticFragment newInstance(String param1, String param2) {
        StatisticFragment fragment = new StatisticFragment();
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
        setHasOptionsMenu(true);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        lines = new ArrayList<>();
        firstPeriode = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        tvNbActivity = view.findViewById(R.id.tvActivity);
        tvNbDistance = view.findViewById(R.id.tvDistance);
        tvNbCalory = view.findViewById(R.id.tvCalory);
        lineChartStats = view.findViewById(R.id.lineChartStats);
        DatabaseReference dbRef = database.getReference("Activity").child(firebaseUser.getUid());
        adapterPeriode = new ArrayAdapter<>(view.getContext(), R.layout.spinner_layout, periode);
        adapterPeriode.setDropDownViewResource(R.layout.spinner_item);
        lineChartStats.getDescription().setText("Stats");
        lineChartStats.setDrawGridBackground(true);
        trainings = new ArrayList<>();
        Legend l = lineChartStats.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        // Controlling X axis
        XAxis xAxis = lineChartStats.getXAxis();
        // Set the xAxis position to bottom. Default is top
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //Customizing x axis value
        xAxis.setGranularity(1f);
        // Controlling right side of y axis
        YAxis yAxisRight = lineChartStats.getAxisRight();
        yAxisRight.setEnabled(false);
        // Controlling left side of y axis
        YAxis yAxisLeft = lineChartStats.getAxisLeft();
        //   yAxisLeft.setAxisMinimum(0);
        yAxisLeft.setGranularity(1f);
        return view;
    }

    public void getValueForGraph(final String periode) {
        DatabaseReference dbRef = database.getReference("Training").child(firebaseUser.getUid());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    trainings.add(snapshot.getValue(Training.class));
                }
                try {
                    setValueForGraph(trainings, periode);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Erreur", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setValueForGraph(List<Training> trainings, String periode) throws ParseException {
        Log.d(TAG, "setValueForGraph: " + trainings);
        Log.d(TAG, "size trainings: " + trainings.size());
        Log.d(TAG, "periode: " + periode);
        lines = new ArrayList<>();
        mapActivities = new TreeMap<>();
        mapNbActivites = new HashMap<>();
        double calories = 0;
        double distances = 0;
        float max = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
        for (int i = 0; i < trainings.size(); ++i) {
            double cal = format.parse(trainings.get(i).getCalory()).doubleValue();
            double dist = format.parse(trainings.get(i).getDistance()).doubleValue();
            //  int painBefore = Integer.parseInt(trainings.get(i).getPainLevelBefore().substring(0, 1));
            int painAfter = Integer.parseInt(trainings.get(i).getPainLevelAfter().substring(0, 1));
            calories += cal;
            distances += dist;
            Date trainingDate = sdf.parse(trainings.get(i).getDate());
            Log.d(TAG, "dateT: " + trainingDate);
            if (periode.equals("Mois")) {
                int month = trainingDate.getMonth() + 1;
                Log.d(TAG, "month: " + month);
                if (mapActivities.containsKey(month)) {
                    mapNbActivites.put(month, mapNbActivites.get(month) + 1);
                    mapActivities.put(month, ((mapActivities.get(month) + painAfter)));
                } else {
                    mapNbActivites.put(month, 1);
                    mapActivities.put(month, painAfter);
                }
            } else if (periode.equals("Jour")) {
                int day = Integer.parseInt(trainingDate.getDay() + "" + trainingDate.getMonth() + "" + trainingDate.getYear());
                Log.d(TAG, "day: " + day);
                if (mapActivities.containsKey(day)) {
                    mapNbActivites.put(day, mapNbActivites.get(day) + 1);
                    mapActivities.put(day, ((mapActivities.get(day) + painAfter)));
                } else {
                    mapNbActivites.put(day, 1);
                    mapActivities.put(day, painAfter);
                }
            } else {
                int week = Integer.parseInt(getWeekOfYear(trainingDate) + "" + trainingDate.getYear());
                if (mapActivities.containsKey(week)) {
                    mapNbActivites.put(week, mapNbActivites.get(week) + 1);
                    mapActivities.put(week, ((mapActivities.get(week) + painAfter)));
                } else {
                    mapNbActivites.put(week, 1);
                    mapActivities.put(week, painAfter);
                }
            }
            max = Math.max(max, (int) dist);
        }
        Map<Integer, Integer> axisXNb = new HashMap<>();
        for (Map.Entry<Integer, Integer> activity : mapActivities.entrySet()) {
            Log.d(TAG, "setM " + activity);
            boolean isX = false;
            for (Entry l : lines) {
                if (l.getX() == mapNbActivites.get(activity.getKey())) {
                    Log.d(TAG, "same x " + l.getX());
                    axisXNb.put(mapNbActivites.get(activity.getKey()), axisXNb.get(mapNbActivites.get(activity.getKey())) + 1);
                    float previousY = l.getY();
                    lines.add(new Entry(l.getX(), activity.getValue() / l.getX() + previousY));
                    lines.remove(l);
                    isX = true;
                    break;
                }
            }
            if (!isX) {
                lines.add(new Entry(mapNbActivites.get(activity.getKey()), activity.getValue() / mapNbActivites.get(activity.getKey())));
                axisXNb.put(mapNbActivites.get(activity.getKey()), 1);
            }
        }
        for (Entry l : lines) {
            Log.d(TAG, "line: " + l.toString());
            if (axisXNb.containsKey((int) l.getX())) {
                Log.d(TAG, "lx: " + l.getX() + " " + axisXNb.get((int) l.getX()));
                l.setY(l.getY() / (axisXNb.get((int) l.getX()) * 10) * 10);
            }
        }
        Collections.sort(lines, new Comparator<Entry>() {
            @Override
            public int compare(Entry e1, Entry e2) {
                return (int) e1.getX() - (int) e2.getX();
            }
        });

        tvNbActivity.setText(String.valueOf(trainings.size()));
        tvNbCalory.setText(String.valueOf(calories));
        tvNbDistance.setText(String.valueOf(distances));
        lineChartStats.setData(generateLineData());
        lineChartStats.setTouchEnabled(false);
        lineChartStats.animateX(500);
        //refresh
        lineChartStats.invalidate();
    }

    public int getWeekOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    private LineData generateLineData() {
        LineDataSet set = new LineDataSet(lines, "Douleur");
        set.setColor(Color.parseColor("#69BBB6"));
        set.setValueTextColor(Color.parseColor("#000000"));
        set.setDrawFilled(true);
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.graph_gradient);
        set.setFillDrawable(drawable);
        return new LineData(set);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        MenuItem spinner = menu.findItem(R.id.spinner);
        spinner.setVisible(true);
        View v = spinner.getActionView();
        if (v instanceof Spinner) {
            Spinner mainSpinner = (Spinner) v;
            mainSpinner.setAdapter(adapterPeriode);
            mainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "onItemSelected: " + parent.getItemAtPosition(position));
                    trainings = new ArrayList<>();
                    getValueForGraph(parent.getItemAtPosition(position).toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.spinner:
                return false;
            default:
                break;
        }

        return false;
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
