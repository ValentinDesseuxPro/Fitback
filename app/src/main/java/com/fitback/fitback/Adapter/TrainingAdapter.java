package com.fitback.fitback.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fitback.fitback.Class.Training;
import com.fitback.fitback.R;

import java.util.List;

public class TrainingAdapter extends BaseAdapter {
    private Context context;
    private List<Training> trainings;

    public TrainingAdapter(Context context, List<Training> trainings) {
        this.context = context;
        this.trainings = trainings;
    }

    @Override
    public int getCount() {
        return trainings.size();
    }

    @Override
    public Object getItem(int position) {
        return trainings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }
        Training training = trainings.get(position);
        // get view
        TextView name = (TextView) convertView.findViewById(R.id.tvTrainingAdapter);
        TextView duration = (TextView) convertView.findViewById(R.id.tvDurationAdapter);
        TextView date = (TextView) convertView.findViewById(R.id.tvDateAdapter);

        // set view training
        name.setText(training.getName());
        date.setText(training.getDate());
        duration.setText(String.valueOf(training.getDuration()));
        return convertView;
    }

    public List<Training> getTrainings() {
        return trainings;
    }
}
