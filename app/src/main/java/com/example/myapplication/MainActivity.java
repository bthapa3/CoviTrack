package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    // r_id represents the user ID value stored in the database. It helps to uniquely identify a person.
    String r_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    // user reference is the database reference for user names and their related data. It is used when we need to access or delete the groups from database.
    DatabaseReference user_reference = FirebaseDatabase.getInstance().getReference().child("Users");
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference datesref = db.collection("latest-dates").document(r_id);
    private static final String TAG="MainActivity";

    private LineChart mChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChart=(LineChart) findViewById(R.id.covidchart);


        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);

        ArrayList<Entry> yvalues=new ArrayList<>();
        yvalues.add(new Entry(1,60f));
        yvalues.add(new Entry(2,70f));
        yvalues.add(new Entry(3,80f));
        yvalues.add(new Entry(4,50f));
        yvalues.add(new Entry(5,60f));
        yvalues.add(new Entry(6,40f));
        yvalues.add(new Entry(7,40f));
        LineDataSet set1=new LineDataSet(yvalues,"Total Positive Recorded For Last Week");
        set1.setFillAlpha(110);
        set1.setColors(Color.RED);
        set1.setLineWidth(3f);
        set1.setValueTextSize(15);
        set1.setValueTextColor(Color.BLUE);
        mChart.getXAxis().setDrawLabels(false);
        YAxis yAxis = mChart.getAxisLeft();
        YAxis rightaxis = mChart.getAxisRight();
        rightaxis.setDrawLabels(false);


        yAxis.setTextSize(15);

        ArrayList<ILineDataSet> dataSets=new ArrayList<>();
        dataSets.add(set1);
        LineData data=new LineData(dataSets);
        mChart.setData(data);


        Button mark_risk= (Button) findViewById(R.id.riskread);
        mark_risk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               user_reference.child(r_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<DataSnapshot> task) {
                       Users user = task.getResult().getValue(Users.class);
                       user.transferrisk=false;
                       user_reference.child(r_id).setValue(user);
                   }
               });
            }
        });


        user_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Datasnapshot) {

                //populating text box with the user groups data.
                Users student = Datasnapshot.child(r_id).getValue(Users.class);
                Switch simpleSwitch = (Switch) findViewById(R.id.statusswitch);
                TextView Notification = (TextView) findViewById(R.id.risknotification);
                Button risk_button= (Button) findViewById(R.id.riskread);

                //Setting the Covid-19 infection status true or false based on the value on database.
                if(student.infected.equals(true)){
                    simpleSwitch.setChecked(true);
                }
                if(student.infected.equals(false)){
                    simpleSwitch.setChecked(false);
                }
                if(student.transferrisk.equals(true)){
                    risk_button.setVisibility(View.VISIBLE);
                    Notification.setText("Alert: Someone in your close contact tested positive recently");
                }

                if(student.transferrisk.equals(false)){
                    risk_button.setVisibility(View.INVISIBLE);
                    Notification.setText("Our system does not indicate any risk of COVID-19 for you at the moment");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //getting latest data of transmission
        datesref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    //capturing the document that holds the timestamp object
                    //if there was no document for the id there was no record of threat from covid
                    //does not need to display dates from recent threat so this does not run
                    if (document.exists()) {

                        //storing document on map inorder to access timestamp easily
                        Map<String, Object> map = document.getData();

                        //Type cast needed as document does not initially pass as timestamp object.
                        Timestamp timestamp = (Timestamp) map.get("timestamp");
                        //getting date format from the timestamp
                        Date date = timestamp.toDate();

                        //converting to user readable format to display to user
                        SimpleDateFormat simpleformat = new SimpleDateFormat("yyyy-MM-dd");
                        String alertdate = simpleformat.format(date);

                        //Using Lastalert textview to display the latest date that system detected threat for user.

                        TextView Lastalert= (TextView) findViewById(R.id.dateview);
                        Lastalert.setText("Last Alert was on: "+ alertdate.toString());
                        Lastalert.setTextSize(20);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error with the database!Try again", Toast.LENGTH_SHORT).show();
                }
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
        user_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Datasnapshot) {

                Switch simpleSwitch = (Switch) findViewById(R.id.statusswitch);
                Boolean switchState = simpleSwitch.isChecked();
                //getting the user's information from the database that needs to modified and storing it as Users class.
                Users user = Datasnapshot.child(r_id).getValue(Users.class);

                if(switchState.equals(true)){
                    user.infected=true;
                    user_reference.child(r_id).setValue(user);
                }
                else{
                    user.infected=false;
                    user_reference.child(r_id).setValue(user);
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
