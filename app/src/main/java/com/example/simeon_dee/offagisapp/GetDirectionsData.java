package com.example.simeon_dee.offagisapp;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by SIMEON_DEE on 9/8/2017.
 */

public class GetDirectionsData extends AsyncTask<Object,String,String> {
    GoogleMap mMap;
    String url;
    String googleDirectionsData;
    String duration = "";
    String distance = "";
    LatLng latLng = null;

    @Override
    protected String doInBackground(Object... params) {
        mMap = (GoogleMap) params[0];
        url = (String) params[1];
        latLng = (LatLng) params[2];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {

            googleDirectionsData = downloadUrl.readUrl(url);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return googleDirectionsData;

    }

    @Override
    protected void onPostExecute(String s)
    {

        HashMap<String,String> directionsList = null;
        DataParser parser = new DataParser();
        directionsList = parser.parseDirections(s);

        duration = directionsList.get("duration");
        distance = directionsList.get("distance");

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng)
                .draggable(true)
                .title("Duration = " + duration)
                .snippet("Distance = " + distance);
        mMap.addMarker(markerOptions);


    }
}
