package com.fitback.fitback.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fitback.fitback.R;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionsAdapter extends BaseAdapter {
    private Context context;
    private List<String> sessions;
    private Map<String, String> mapSelectActivity;
    private List<String> session_activity;


    public SessionsAdapter(Context context, List<String> activities, List<String> session_activity) {
        this.context = context;
        this.sessions = activities;
        this.mapSelectActivity = new HashMap<>();
        mapSelectActivity.put("Course", "running");
        mapSelectActivity.put("Marche à pied", "walking");
        mapSelectActivity.put("Randonnée", "trekking");
        mapSelectActivity.put("Marche nordique", "hiking");
        mapSelectActivity.put("Natation", "swim");
        mapSelectActivity.put("Cyclisme", "cyclism");
        this.session_activity = session_activity;
    }

    @Override
    public int getCount() {
        return sessions.size();
    }

    @Override
    public Object getItem(int position) {
        return sessions.get(position);
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
        // get view
        TextView name = (TextView) convertView.findViewById(R.id.tvAct);
        ImageView map = (ImageView) convertView.findViewById(R.id.map_need);

        // set view training
        name.setText(sessions.get(position));

        return convertView;
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

    public List<String> getactivities() {
        return sessions;
    }
}
