package com.fitback.fitback.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageButton;

import com.fitback.fitback.ChronometreActivity;
import com.fitback.fitback.Class.Profile;
import com.fitback.fitback.MainActivity;
import com.fitback.fitback.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.reflect.Field;

public class GPSTracker extends Service implements LocationListener {

    private static final String TAG = "GPSTracker";
    private final Context context;
    public static Bitmap bitmap;
    private static Boolean start = false;
    public static Float distance;
    public static Float speed;
    private Profile currentUser;
    public static Float calories;
    private final Activity activity;
    private static final Float FV = 1.045f;
    public static Location position = null;
    protected LocationManager locationManager;
    private Location mLocation;
    private ImageButton signal;
    private Float poids = 70f;
    private GoogleMap map;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;

    public static Location getPosition() {
        return position;
    }

    public static void setPosition(Location position) {
        GPSTracker.position = position;
    }

    public static Boolean getStart() {
        return start;
    }

    public static void setStart(Boolean starter) {
        start = starter;
    }

    public Float getPoids() {return poids;}

    public void setPoids(Float poids) {this.poids = poids;}

    public GPSTracker(Context context, GoogleMap map, Activity activity) {
        this.context = context;
        this.map = map;
        this.distance = 0f;
        this.speed = 0f;
        this.calories = 0f;
        map.clear();
        this.poids=70f;
        this.currentUser= MainActivity.profileCurrentUser;
        this.mLocation = null;
        this.activity = activity;
        this.signal = (ImageButton) activity.findViewById(R.id.map_signal);
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

    //Create a GetLocation Method //
    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled) {
                    if (this.position == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                1000,
                                10, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            this.position = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                } else if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            1000,
                            10, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        this.position = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                getGPSStrength();
            }

        } catch (Exception ex) {
        }
        return this.position;
    }

    // followings are the default method if we implement LocationListener //
    public void onLocationChanged(Location location) {
        if (!getStart())
            return;
        if (this.position != location) {
            getGPSStrength();
            LatLng prev = new LatLng(this.position.getLatitude(), this.position.getLongitude());
            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
            map.addPolyline(new PolylineOptions().add(prev, current).width(6).color(Color.RED).visible(true));
            if (ChronometreActivity.breakActivity) {
                this.position = location;
                ChronometreActivity.breakActivity = false;
            }
            Float tmp = this.position.distanceTo(location);
            this.distance += tmp;

            if (location.hasSpeed()) {
                this.speed = location.getSpeed() * 3.6f;
            }
            if(MainActivity.profileCurrentUser != null && MainActivity.profileCurrentUser.getWeight() != 0)this.poids=(float)MainActivity.profileCurrentUser.getWeight();
            this.calories = FV * (this.distance / 1000) * this.poids;
            this.speed = location.getSpeed();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(current, 10);
            map.moveCamera(cameraUpdate);
            this.position = location;
        }
    }

    public void onStatusChanged(String Provider, int status, Bundle extras) {

    }

    public void onProviderEnabled(String Provider) {

    }

    public void onProviderDisabled(String Provider) {

    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void getGPSStrength() {
        if (this.position.hasAccuracy()) {
            Log.d(TAG, "accuracy" + this.position.getAccuracy());
            if (this.position.getAccuracy() < 0) {
                this.signal.setImageResource(R.drawable.signal_null);
            } else if (this.position.getAccuracy() > 163) {
                this.signal.setImageResource(R.drawable.signal_poor);
            } else if (this.position.getAccuracy() > 48) {
                this.signal.setImageResource(R.drawable.signal_average);
            } else {
                this.signal.setImageResource(R.drawable.signal_full);
            }
        }
    }
}