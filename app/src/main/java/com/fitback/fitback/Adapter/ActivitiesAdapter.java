package com.fitback.fitback.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fitback.fitback.Class.Activity;
import com.fitback.fitback.R;

import java.util.List;

public class ActivitiesAdapter extends BaseAdapter {
    private Context context;
    private List<Activity> activities;

    public ActivitiesAdapter(Context context, List<Activity> activities) {
        this.context = context;
        this.activities = activities;
    }

    @Override
    public int getCount() {
        return activities.size();
    }

    @Override
    public Object getItem(int position) {
        return activities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_activity, parent, false);
        }

        Activity act = activities.get(position);
        // get view
        TextView name = (TextView) convertView.findViewById(R.id.tvAct);
        ImageView map = (ImageView) convertView.findViewById(R.id.map_need);

        // set view training
        name.setText(act.getName());
        if (act.getMap_needed().equals("1"))
            map.setVisibility(View.VISIBLE);

        return convertView;
    }

    public List<Activity> getactivities() {
        return activities;
    }
}
