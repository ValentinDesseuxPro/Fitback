package com.fitback.fitback.Class;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListViewData {
    public static HashMap<String, List<String>> getData() {
        final HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();
        FirebaseDatabase.getInstance().getReference("Session").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            List<String> session = new ArrayList<String>();
                            Session s = data.getValue(Session.class);
                            session.add(s.getName());
                            expandableListDetail.put(data.getKey(), session);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        return expandableListDetail;
    }
}
