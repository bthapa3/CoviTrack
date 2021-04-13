package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {


    // r_id represents the user ID value stored in the database. It helps to uniquely identify a person.
    String r_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    // user reference is the database reference for user names and their related data. It is used when we need to access or delete the groups from database.
    DatabaseReference userreference = FirebaseDatabase.getInstance().getReference().child("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Datasnapshot) {

                //populating text box with the user groups data.
                Users student = Datasnapshot.child(r_id).getValue(Users.class);
                Switch simpleSwitch = (Switch) findViewById(R.id.statusswitch);

                //Setting the Covid-19 infection status true or false based on the value on database.
                if(student.infected.equals(true)){
                    simpleSwitch.setChecked(true);
                }
                if(student.infected.equals(false)){
                    simpleSwitch.setChecked(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //asking location permission for the first time.
        //If the location is denied user will have to grant permission
        //Note for self- more work to be done here regrading conditions when permission is denied
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION},1);

        //waking  up a background class that tracks the location of the user
        Intent intent =new Intent (this, BackgroundLocationTracker.class);
        intent.setAction("Location_tracker");

        //Inorder to wake up background process after certain interval so that it can track the location and go
        // back to sleep.
        PendingIntent pendingIntent= PendingIntent.getBroadcast(this,0,intent,0);
        AlarmManager alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 0, 1,pendingIntent);

    }

    public void Logout(View view){

        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    public void GotoGroup(View view){
        startActivity(new Intent(getApplicationContext(),Groups.class));
        finish();
    }


    // this function changes the covid infection status
    public void ChangeStatus(View view){
        userreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Datasnapshot) {

                Switch simpleSwitch = (Switch) findViewById(R.id.statusswitch);
                Boolean switchState = simpleSwitch.isChecked();
                //getting the user's information from the database that needs to modified and storing it as Users class.
                Users user = Datasnapshot.child(r_id).getValue(Users.class);

                if(switchState.equals(true)){
                    user.infected=true;
                    userreference.child(r_id).setValue(user);
                }
                else{
                    user.infected=false;
                    userreference.child(r_id).setValue(user);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                //In order to display database errors
                Toast.makeText(MainActivity.this, "Error with the database", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
