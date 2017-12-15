package br.com.actia.e1player;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationStatusCodes;

import java.util.List;

/**
 * Created by Armani on 03/12/2014.
 */
public class GpsController implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationClient.OnAddGeofencesResultListener {

    private LocationClient locationClient = null;
    private Activity activity = null;
    private List<Geofence> lstGeofence = null;

    public GpsController(Activity activity, List<Geofence> lstGeofence) {
        this.activity = activity;
        this.lstGeofence = lstGeofence;

        int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (ConnectionResult.SUCCESS == resp) {
            locationClient = new LocationClient(activity, this, this);
            locationClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        PendingIntent geoFencePendingIntent = PendingIntent.getService(this.activity, 0,
                new Intent(this.activity, StopAlertActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        locationClient.addGeofences(lstGeofence, geoFencePendingIntent, this);
    }

    @Override
    public void onDisconnected() {
        this.locationClient = null;
    }

    @Override
    public void onAddGeofencesResult(int status, String[] strings) {
        if(status != LocationStatusCodes.SUCCESS){
            Log.e(getClass().getName(), "onAddGeofencesResult: " + status);
            return;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
