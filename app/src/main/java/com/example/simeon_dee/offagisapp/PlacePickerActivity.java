package com.example.simeon_dee.offagisapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class PlacePickerActivity extends AppCompatActivity {

    private static final int PLACE_PICKER_REQUEST_CODE = 1;
    TextView tvDetails;

    String selectedItem = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);
        tvDetails = (TextView) findViewById(R.id.tvDisplayDetails);
    }

    public void onClickPickPlaceButtonHandler(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {

            Intent intent = builder.build(this);
            startActivityForResult(intent,PLACE_PICKER_REQUEST_CODE);

        } catch (GooglePlayServicesRepairableException e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();

        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        try{
            if(requestCode == PLACE_PICKER_REQUEST_CODE && data != null)
            {
                if (resultCode == RESULT_OK)
                {

                    Place place = PlacePicker.getPlace(this, data);

                    LatLng latLng = place.getLatLng();
                    /*String ans = "Name: " + place.getName().toString() + "\nAddress: " + place
                            .getAddress() + "\nPhone No: " + place
                            .getPhoneNumber() + "\nPlace Location: " + String
                            .valueOf(latLng.latitude) + ", " + String.valueOf(latLng.longitude);

                    tvDetails.setText(ans);
                    Toast.makeText(this, ans, Toast.LENGTH_LONG).show();*/

                    StringBuilder sb = new StringBuilder();

                    sb.append("Place: "+ place.getName().toString());
                    sb.append("\nAddress: " + place.getAddress());
                    sb.append("\nPlace Location: " + String.valueOf(latLng.latitude) +
                            ", " + String.valueOf(latLng.longitude));
                    if(place.getWebsiteUri().toString() != null || !place.getWebsiteUri()
                            .toString().equals(""))
                    { sb.append("\nWebsite: "+ place.getWebsiteUri().toString()); }

                    sb.append("\nPhone No: " + place.getPhoneNumber());

                    Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
                    tvDetails.setText(sb.toString());

                }
                else if (resultCode == PlacePicker.RESULT_ERROR)
                {
                    Status status = PlacePicker.getStatus(this,data);
                    Toast.makeText(this,"Something went wrong \n" +
                            String.format("Result: %s",status.getStatusMessage()),Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(this, "No Place selected", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    public void onShowDialogButtonHandler(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final CharSequence[] someList = {"ATM","bank","hospital","restaurant","school"};

        builder.setTitle("Pick Place Type to Search For")
                .setItems(someList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        selectedItem="";
                        if (which != -1){
                            selectedItem = someList[which].toString();
                        }

                        Toast.makeText(PlacePickerActivity.this,selectedItem+" Selected",Toast.LENGTH_LONG)
                                .show();

                    }
                });

builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();

    }
});

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
