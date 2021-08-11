package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**/
/*
 *  CLASS DESCRIPTION:
 *      Overall Stats of the organization and the user himself.
 *
 *  PURPOSE:
 *      The main purpose of this class is to show the user the overall status of the organization
 *      by showing the daily count of the covid positive patients for last seven days.
 *      This data is shown in graphs and user can also see if there was any alert of
 *      covid risk for him. User can also changes his covid positive status.
 *
 *  AUTHOR:
 *      Bishal Thapa
 *
 *  DATE
 *       4/27/2021
 *
 *  Help taken from: https://stackoverflow.com/questions/21232984/difference-between-setrepeating-and-setinexactrepeating-of-alarmmanager
 */
/**/

public class MainActivity extends ToolbarActivity implements View.OnClickListener {

    //this is the time before the first background location process starts.
    public static final int INITIALTRIGGERWAIT = 0;
    //m_userid represents the user ID value stored in the database. It helps to uniquely identify a person.
    private String m_userid=FirebaseAuth.getInstance().getCurrentUser().getUid();

    // user reference is the database reference for user names and their related data. It is used when we need to access or delete the groups from database.
    private DatabaseReference m_userreference = FirebaseDatabase.getInstance().getReference().child("Users");

    //Reference for Firestore document that stores last date when user received a notification from app.
    private DocumentReference m_datesref=FirebaseFirestore.getInstance().collection("latest-dates").document(m_userid);

    //Reference for Firestore document object that stores the daily positive count of a week.
    private DocumentReference m_weeklystatref=FirebaseFirestore.getInstance().collection("stats").document("weekly");

    //Linechart object to show graph of the weekly positive count
    private LineChart m_mChart;
    //Toolbar at the top of the page.
    private Toolbar m_toolbar;

    /**/
    /*
     *   NAME
     *      protected void onCreate
     *
     *   SYNOPSIS
     *      protected void onCreate(Bundle a_savedInstanceState)
     *      Bundle a_savedInstanceState---->reference to a Bundle object
     *
     *   DESCRIPTION
     *      Oncreate method stores the reference for the toolbar, Imagebuttons and buttons.
     *      During the on-create function weekly covid positive count is loaded from
     *      database and used with m-chart library to display visually to the user.On-click listerner
     *      is also set up so that user can change his current covid status. On the other hand backgroud
     *      process to track the user location is also started from the on-create function.
     *      In order to be able to access location pop-up is shown asking for location
     *      and other permissions that are necessary to run the features successfully.
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
    protected void onCreate(Bundle a_savedInstanceState) {
        super.onCreate(a_savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup for toolbar at the top of the homepage
        m_toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(m_toolbar);
        m_toolbar.setTitle("Home");

        // //Image buttons for navigating through the 5 main activities.
        ImageButton assessbutton=findViewById(R.id.assessButton);
        ImageButton groupbutton=findViewById(R.id.groupButton);
        ImageButton resourcebutton=findViewById(R.id.resourcesButton);
        ImageButton uploadbutton=findViewById(R.id.uploadButton);

        //on click listener that helps to determine the next activity that the user wants
        // to navigate to.
        assessbutton.setOnClickListener(this);
        groupbutton.setOnClickListener(this);
        resourcebutton.setOnClickListener(this);
        uploadbutton.setOnClickListener(this);


        //Reading array list from the database that contains the weekly cound of covid positive employees.
        m_weeklystatref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {

                //If the exception is thrown from the Firestore Firebase.
                if (e != null) {
                    System.out.println(" Listen Failed");
                    return;
                }

                //After receiving the snapshot of array from the database using it to make line chart.
                if (snapshot != null && snapshot.exists()) {
                    //countmap is a map object that holds the snapshot map object from firebase database.
                    Map<String, Object> countmap= snapshot.getData();

                    //converting the map object to arraylist
                    ArrayList<Long> dailycount=(ArrayList)countmap.get("positive_count");

                    //setting up mchart to display values properly
                    m_mChart=(LineChart) findViewById(R.id.covidchart);
                    m_mChart.setDragEnabled(true);
                    m_mChart.setScaleEnabled(false);

                    //Modifying legend properties of the line chart
                    Legend chartLegend = m_mChart.getLegend();
                    chartLegend.setTextSize(20f);
                    chartLegend.setTextColor(Color.BLACK);
                    chartLegend.setForm(Legend.LegendForm.CIRCLE);

                    //array list holds date of last 7 days and count for those dates
                    ArrayList<Entry> chartvalues=new ArrayList<>();
                    chartvalues.add(new Entry(-6,dailycount.get(0)));
                    chartvalues.add(new Entry(-5,dailycount.get(1)));
                    chartvalues.add(new Entry(-4,dailycount.get(2)));
                    chartvalues.add(new Entry(-3,dailycount.get(3)));
                    chartvalues.add(new Entry(-2,dailycount.get(4)));
                    chartvalues.add(new Entry(-1,dailycount.get(5)));
                    chartvalues.add(new Entry(0,dailycount.get(6)));

                    //inserting chartvalues into LineDataset
                    LineDataSet coviddataset=new LineDataSet(chartvalues,"Total positive count for each day");

                    //setting values for the covid dataset
                    coviddataset.setValueTextSize(15);
                    coviddataset.setCircleColor(001);
                    coviddataset.setColors(Color.RED);
                    coviddataset.setLineWidth(2f);
                    coviddataset.setValueTextColor(Color.BLUE);

                    //Setting text size of the Xaxis and Y-axis labels and modyfying label values
                    m_mChart.getXAxis().setTextSize(15);
                    m_mChart.setExtraTopOffset(5);
                    m_mChart.setExtraRightOffset(35);
                    m_mChart.getAxisLeft().setTextSize(15);
                    m_mChart.getAxisRight().setDrawLabels(false);

                    //converting the numerical values of days in x-axis to date in format mm-dd
                    //dates are set for last 7 days as there are only 7 points of data saved.
                    m_mChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {
                            value=Math.abs(value);
                            LocalDate locdate = LocalDate.now();
                            locdate=locdate.minusDays((long)value);
                            Integer month= locdate.getMonthValue();
                            return (month.toString() + "-"+ locdate.getDayOfMonth()); // yVal is a string array
                        }
                    });


                    //Attaching datasets value with the mchart to be displayed on the screen.
                    ArrayList<ILineDataSet> dataSets=new ArrayList<>();
                    dataSets.add(coviddataset);
                    LineData data=new LineData(dataSets);
                    m_mChart.setData(data);
                    //In order to refresh the chart
                    m_mChart.invalidate();
                    m_mChart.refreshDrawableState();

                } else {
                    //In case there is no snapshot of data found on the database.
                    Toast.makeText(MainActivity.this, "Error with the database", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Input button to mark the notification as read.
        Button mark_risk= (Button) findViewById(R.id.riskmarked);

        //Once the button is read notification is no more shown to the user
        //this works by modifying the transfer risk value to false.
        mark_risk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               m_userreference.child(m_userid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<DataSnapshot> task) {
                       Users user = task.getResult().getValue(Users.class);
                       user.setTransferrisk(false);
                       m_userreference.child(m_userid).setValue(user);
                   }
               });
            }
        });


        //gets the user data from the database and allows for modification
        //can modify covid infection state and save it back to database
        m_userreference.child(m_userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Datasnapshot) {

                //populating text box with the user groups data.
                Users student = Datasnapshot.getValue(Users.class);
                Switch simpleSwitch =  findViewById(R.id.statusswitch);
                TextView Notification = findViewById(R.id.risknotification);
                Button risk_button=  findViewById(R.id.riskmarked);

                //Setting the Covid-19 infection status true or false based on the value on database.
                if(student.getInfected().equals(true)){
                    simpleSwitch.setChecked(true);
                }
                if(student.getInfected().equals(false)){
                    simpleSwitch.setChecked(false);
                }

                //Displaying the notification results based on transfer risk value which can be true or false.
                //If the risk is there user can read it and mark it as read.
                if(student.getTransferrisk().equals(true)){
                    risk_button.setVisibility(View.VISIBLE);
                    Notification.setText("Alert: Someone in your close contact tested positive recently");
                    Notification.setTextColor(Color.parseColor("#F33E51" ));
                }

                //Hiding the mark as read button in case there is no notification to read.
                if(student.getTransferrisk().equals(false)){
                    risk_button.setVisibility(View.INVISIBLE);
                    Notification.setText("Our system does not indicate any risk of COVID-19 for you at the moment");
                    Notification.setTextColor(Color.parseColor("#23cba7"));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error with the database", Toast.LENGTH_SHORT).show();
            }
        });

        //If there is a record of risk detected on the past, the date when risk was assessed will be displayed.
        m_datesref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                        Lastalert.setTextColor(Color.parseColor("#F33E51" ));
                        Lastalert.setTextSize(20);
                    }
                    else {
                        //There has been no record of risk assessed for this user in the past.
                        TextView Lastalert= (TextView) findViewById(R.id.dateview);
                        Lastalert.setText("No record of alert found from past!!");
                        Lastalert.setTextColor(Color.parseColor("#23cba7"));
                        Lastalert.setTextSize(20);
                    }
                }
            }
        });

        //asking location permission for the first time.
        //If the location is denied user will have to grant permission
        //Note for self- more work to be done here regrading conditions when permission is denied
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION},1);

        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE},1);

        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.INTERNET},1);

        //waking  up a background class that tracks the location of the user
        Intent intent =new Intent (this, BackgroundLocationTracker.class);
        intent.setAction("Location_tracker");

        //Inorder to wake up background process after certain interval so that it can track the location and go
        // back to sleep.
        PendingIntent pendingIntent= PendingIntent.getBroadcast(this,0,intent,0);
        // Hopefully your alarm will have a lower frequency than this!

        AlarmManager alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //used setInexact instead of setRepeating as it consumes less resource.
        //Repeating alarm time will wont be accurate but approx to 30 minutes.
        //https://stackoverflow.com/questions/21232984/difference-between-setrepeating-and-setinexactrepeating-of-alarmmanager
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + INITIALTRIGGERWAIT, AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);


    }

    /**/
    /*
     *   NAME
     *      public void onBackPressed
     *
     *   SYNOPSIS
     *      public void onBackPressed()
     *      no parameters.
     *
     *   DESCRIPTION
     *     This function allows the user to exit the app when user tries to go back from the main activity page.
     *      It moves main activity to the back of the activity stack.
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
    public void onBackPressed()

    {
        moveTaskToBack(true);
    }

    /**/
    /*
     *   NAME
     *      public void onClick
     *
     *   SYNOPSIS
     *      public void onClick(View a_view)
     *      a_view   --> view object passes the reference to the Image button which triggered the
     *                  on-click method.
     *
     *   DESCRIPTION
     *     This function allows the user to navigate through four different activities of the application.
     *      It takes View v as an input parameter and captures the ID of the button pressed to
     *      start the new activity.
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
    public void onClick(View a_view) {

        switch(a_view.getId()){

            case R.id.assessButton:
                startActivity(new Intent(getApplicationContext(), AssesmentActivity.class));
                finish();
                break;

            case R.id.groupButton:
                startActivity(new Intent(getApplicationContext(), GroupsActivity.class));
                finish();
                break;

            case R.id.uploadButton:
                startActivity(new Intent(getApplicationContext(), UploadActivity.class));
                finish();
                break;

            case R.id.resourcesButton:
                startActivity(new Intent(getApplicationContext(), ResourcesActivity.class));
                finish();
                break;

            default:
                return;
        }
    }


    /**/
    /*
     *   NAME
     *       public void ChangeStatus
     *
     *   SYNOPSIS
     *       public void ChangeStatus(View a_view)
     *          a_view   --> view object passes the reference to the status switch for covid status.
     *
     *   DESCRIPTION
     *     This function is called whenever the user interacts with the switch button. The current
     *     user covid status is read from the database and switched. The changes are also saved
     *      back to the database.
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

    public void ChangeStatus(View view){
        m_userreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Datasnapshot) {

                Switch simpleSwitch = (Switch) findViewById(R.id.statusswitch);
                Boolean switchState = simpleSwitch.isChecked();
                //getting the user's information from the database that needs to modified and storing it as Users class.
                Users user = Datasnapshot.child(m_userid).getValue(Users.class);

                if(switchState.equals(true)){
                    user.setInfected(true);
                    m_userreference.child(m_userid).setValue(user);
                }
                else{
                    user.setInfected(false);
                    m_userreference.child(m_userid).setValue(user);
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
