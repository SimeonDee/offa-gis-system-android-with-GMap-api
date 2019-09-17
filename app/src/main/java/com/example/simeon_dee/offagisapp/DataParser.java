package com.example.simeon_dee.offagisapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SIMEON_DEE on 9/6/2017.
 */

public class DataParser
{
    private HashMap<String,String> getDuration(JSONArray googleDirectionsjson)
    {
        HashMap<String,String> googleDirectionsMap = new HashMap<>();
        String duration;
        String distance;

        try {
            duration = googleDirectionsjson.getJSONObject(0).getJSONObject("duration").getString("text");
            distance = googleDirectionsjson.getJSONObject(0).getJSONObject("distance").getString("text");

            googleDirectionsMap.put("duration",duration);
            googleDirectionsMap.put("distance",distance);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  googleDirectionsMap;
    }
    private HashMap<String,String> getPlace(JSONObject googlePlacejson)
    {
        HashMap<String,String> googlePlacesMap = new HashMap<>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";
        //String placeType = "";

        try {
            if (!googlePlacejson.isNull("name")) {
                placeName = googlePlacejson.getString("name");
            }
            if (!googlePlacejson.isNull("vicinity")) {
                vicinity = googlePlacejson.getString("vicinity");
            }
            latitude = googlePlacejson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlacejson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlacejson.getString("reference");

            googlePlacesMap.put("place_name", placeName);
            googlePlacejson.put("vicinity", vicinity);
            googlePlacejson.put("lat", latitude);
            googlePlacejson.put("lng", longitude);
            googlePlacejson.put("reference", reference);

        }catch (JSONException e) {
                e.printStackTrace();
        }

        return googlePlacesMap;
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray)
    {
        int count = jsonArray.length();
        List<HashMap<String, String>> placesList = new ArrayList<>();
        HashMap<String,String> placeMap = null;

        for(int i = 0; i < count; i++){
            try {

                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placesList;
    }

    public List<HashMap<String,String>> parse(String jsonData)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject = null;

        try {

            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPlaces(jsonArray);
    }

    public HashMap<String,String> parseDirections(String jsonData)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getDuration(jsonArray);
    }

    //Plotting of Direction Path Codes Start Here
    public String[] parsePlotDirections(String jsonData)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs")
                    .getJSONObject(0).getJSONArray("steps");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getAllPaths(jsonArray);
    }

    public String[] getAllPaths(JSONArray googleStepsJson)
    {
        int count = googleStepsJson.length();
        String[] polylines = new String[count];

        for(int i = 0; i<count; i++)
        {
            try {
                polylines[i] = getPath(googleStepsJson.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return polylines;
    }

    public  String getPath(JSONObject googlePathJson)
    {
        String polyline = "";
        try {
            polyline = googlePathJson.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
         return polyline;
    }

}
