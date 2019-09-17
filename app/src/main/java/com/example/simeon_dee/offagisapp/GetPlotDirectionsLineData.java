package com.example.simeon_dee.offagisapp;

import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by SIMEON_DEE on 9/8/2017.
 */

public class GetPlotDirectionsLineData extends AsyncTask<Object,String,String> {

    GoogleMap mMap;
    String url;
    String googlePlotDirectionsData;
    LatLng latLng = null;

    @Override
    protected String doInBackground(Object... params) {
        mMap = (GoogleMap) params[0];
        url = (String) params[1];
        latLng = (LatLng) params[2];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {

            googlePlotDirectionsData = downloadUrl.readUrl(url);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlotDirectionsData;

    }

    @Override
    protected void onPostExecute(String s)
    {
        String[] plotDirectionsList;
        DataParser parser = new DataParser();
        plotDirectionsList = parser.parsePlotDirections(s);
        displayDirection(plotDirectionsList);

    }

    private void displayDirection(String[] plotDirectionsList)
    {
        int count = plotDirectionsList.length;
        for(int i = 0; i < count; i++)
        {

            PolylineOptions options = new PolylineOptions();
            options.color(Color.RED)
                    .width(10)
                    .geodesic(true)
                    .addAll(decodePolylinePoints(plotDirectionsList[i]));

            mMap.addPolyline(options);
        }

    }

    private ArrayList<LatLng> decodePolylinePoints(String encoded)
    {
        ArrayList<LatLng> poly = new ArrayList<>();
        int index=0, len=encoded.length();
        int lat=0, lng=0;

        while (index < len){
            int b, shift = 0, result = 0;
            do{
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),(((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

}
