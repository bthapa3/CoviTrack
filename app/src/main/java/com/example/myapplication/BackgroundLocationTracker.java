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
import android.os.SystemClock;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
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

/**/
/*
 *  CLASS DESCRIPTION:
 *      Runs on background without any input in order to find location(geo-coordinates of the user)
 *      and store that value on database.
 *
 *  PURPOSE:
 *      This process runs on background to find location co-ordinates of the user on regular time gaps.
 *      It makes use of the LocationManager API to get the location updates.The location used for
 *      this class is taken from GPS_provider which is approximately accurate up to 20 feet(6meters)
 *
 *
 *  AUTHOR:
 *      Bishal Thapa
 *
 *  DATE:
 *       4/27/2021
 */
/**/
public class BackgroundLocationTracker extends BroadcastReceiver implements LocationListener {

    //time period for location updates
    public static final int THIRTYMINUTES_TO_MILLISECONDS = 1800000;
    //distance in meter for the location updates
    public static final int MIN_DISTANCE_METER = 6;
    //approx count of 30 minutes periods employees are expected to stay at work placce. ie 2*7 days*12 hours*2 per hour
    //this is the max location cordinates saved
    public static final int THIRTY_MINUTES_IN_TWO_WEEKS = 336;
    private LocationManager m_locationManager;

    /**/
    /*
     *  NAME
     *      public void onReceive
     *
     *  SYNOPSIS
     *      public void onReceive (Context a_context, Intent a_intent)
     *      a_context---->context for the OnReceive method from BroadcastReceiver.
     *      a_intent---->intent received from the Broadcast receiver
     *
     *  DESCRIPTION
     *      This method checks the user permission and Build version in order to successfully get the
     *      geo-coordinates of the user.If the location cannot be accessed it notifies user through
     *      toast messages.
     *
     *   RETURNS
     *       Nothing
     *
     *   AUTHOR
     *       Bishal Thapa
     *
     *   DATE
     *       4/27/2021
     *
     *   Help taken from - https://stackoverflow.com/questions/5947775/android-locationmanager-requestlocationupdates
     */
    /**/
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive (Context a_context, Intent a_intent){

        // With out checking Build version checkSelfPermission would crash because it does not exist in some older sdk versions.
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            if (a_context.getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {

                //getting the current location updates before updating on the database.
                m_locationManager= (LocationManager) a_context.getApplicationContext().getSystemService(LOCATION_SERVICE);

                //Using GPS provided location instead of network location as it has more coverage and reliability.
                //Minimum distance before updating location is set to 6 meters as we do not need updates if the
                //value has not increased by more than 6 meters.
                m_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,THIRTYMINUTES_TO_MILLISECONDS, MIN_DISTANCE_METER,BackgroundLocationTracker.this);
            }
            else{
                //Turns out we cannot ask permission from a background process so just asking the user to turn on the location permission by going to the setting.
                Toast.makeText(a_context, "Please go to settings and allow location permissions.", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(a_context, "Problem tracking the location data!!!", Toast.LENGTH_SHORT).show();
        }
    }


    /**/
    /*
     *  NAME
     *      public void onLocationChanged
     *
     *  SYNOPSIS
     *      public void onLocationChanged(@NonNull Location location)
     *      location---> a location object that holds the geo-coordinates of the user current location.
     *
     *  DESCRIPTION
     *      The onLocationChanged method takes the location object of the onReceive function and
     *      stores it on the database.If an instance of document with geo coordinates is not present
     *      on the database, a new map Object is created and geo-coordinate is pushed. If the array list is
     *      already present in the database than co-ordinate is pushed using FieldValue.arrayUnion method which
     *      adds the value at the end of the database. It also deletes the old database in order to save the
     *      space on the database. It does that by checking the number of points stored and replacing if the
     *      limit is over.
     *
     *   RETURNS
     *       Nothing
     *
     *   AUTHOR
     *       Bishal Thapa
     *
     *   DATE
     *       4/27/2021
     *
     */
    /**/
    @Override
    public void onLocationChanged(@NonNull Location a_location) {

        //user_id for reference during database access.
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //reference for the firebase database that helps to connect to the database.
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // using location manager to get latitude and longitude for the user current address.
        GeoPoint geolocation=new GeoPoint(a_location.getLatitude(),a_location.getLongitude());

        DocumentReference docRef = db.collection("userlocation").document(userid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Atomically add a new location to the "locations" array field.
                        //if userlocation collection is already present location data will be appended to the location array.

                        List<GeoPoint> Location=new ArrayList<GeoPoint>();
                        Location=(List)document.getData().get("locations");

                        //Checking if the geopoints saved are older than 2 weeks.
                        //No more than 336 data points are needed to be saved in database
                        // we can delete the old points after that.
                        if(Location.size()> THIRTY_MINUTES_IN_TWO_WEEKS){

                            //oldest point we stored in database
                            GeoPoint point_to_delete=Location.get(0);

                            //removing oldest point before storing new point
                            db.collection("userlocation").document(userid).update("locations", FieldValue.arrayRemove(point_to_delete));
                            //adding new value to the database.
                            db.collection("userlocation").document(userid).update("locations", FieldValue.arrayUnion(geolocation));

                        }
                        else{
                            //since there are not enough points we can store without deleting anything.
                            db.collection("userlocation").document(userid).update("locations", FieldValue.arrayUnion(geolocation));
                        }

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
                         db.collection("userlocation").document(userid)
                                .set(data);
                    }
                }
                else {
                    //could not get the snapshot so printing the exception type
                    Log.d(TAG, "Error Occurred with exception: ", task.getException());
                }
            }
        });

    }

}
