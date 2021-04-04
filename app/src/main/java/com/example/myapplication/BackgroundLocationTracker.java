package com.example.myapplication;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;
import static androidx.core.app.ActivityCompat.requestPermissions;


public class BackgroundLocationTracker extends BroadcastReceiver implements LocationListener {
    
    LocationManager locationManager;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive (Context context, Intent intent){

        // With out checking Build version checkSelfPermission would crash because it does not exist in some older sdk versions.
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            if (context.getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {

                //getting the current location updates before updating on the database.
                locationManager= (LocationManager) context.getApplicationContext().getSystemService(LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000000,5,BackgroundLocationTracker.this);

            }

            else{
                //Turns out we cannot ask permission from a background process so just asking the user to turn on the location permission by going to the setting.
                Toast.makeText(context, "Please allow location in the background", Toast.LENGTH_SHORT).show();
            }

        }
        else{
            Toast.makeText(context, "Problem tracking the location data", Toast.LENGTH_SHORT).show();
        }

        int interval = 60000; //  saves the location every 10 minutes
        Intent Int = new Intent(context, BackgroundLocationTracker.class);
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        // we can store location coordinates again after an interval
        am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+interval, PendingIntent.getBroadcast(context, 1,  Int, PendingIntent.FLAG_UPDATE_CURRENT));

    }


    @Override
    public void onLocationChanged(@NonNull Location location) {

        //user_id for reference during database access.
        String r_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //reference for the firebase database that helps to connect to the database.
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // using location manager to get latitude and longitude for the user current address.
        GeoPoint geolocation=new GeoPoint(location.getLatitude(),location.getLongitude());


        DocumentReference docRef = db.collection("userlocation").document(r_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Atomically add a new location to the "locations" array field.
                        //if userlocation collection is already present location data will be appended to the location array.
                        db.collection("userlocation").document(r_id).update("locations", FieldValue.arrayUnion(geolocation));
                    }
                    else {

                        //List Array type to store the geolocation coordinates for the user in data type.
                        List<GeoPoint> Location=new ArrayList<GeoPoint>();

                        // Firebase Database needs map object type to insert the String with list array.
                        // String is used to give the name for the array in database.
                        Map <String,List> data=new HashMap<>();

                        Location.add(geolocation);

                        //name of the array and the list array with initial co-ordinates for location.
                        data.put("locations", Location);

                        // creating a new document with value r_id  if document is not present.
                        // adds the data to the userlocation collection
                         db.collection("userlocation").document(r_id)
                                .set(data);
                    }
                }
                else {
                    //could not get the snapshot so printing the exception type
                    Log.d(TAG, "Error Occurred with exception  ", task.getException());
                }
            }
        });

    }

}
