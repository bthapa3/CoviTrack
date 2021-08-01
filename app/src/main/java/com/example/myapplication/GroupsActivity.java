package com.example.myapplication;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;


 /**/
 /*
  *  CLASS DESCRIPTION:
  *     Allows the user to edit or select the groups they belong as per their physical
  *     location on work environment.
  *
  *  PURPOSE:
  *     GroupsActivity Class manages all the operations related with user-groups
  *     Users can manually or automatically join the groups using the text fields.
  *     User are displayed groups they belong to.
  *     Default values are null for all 4 groups initially.
  *     User can have up to 4 groups and they can change them.
  *
  *  AUTHOR:
  *      Bishal Thapa
  *
  *  DATE
  *       4/27/2021
  */
 /**/

public class GroupsActivity extends  ToolbarActivity implements View.OnClickListener {

    // Reference for the Editable text views that stores the names of the respective groups
    private EditText m_group1, m_group2, m_group3, m_group4;
    private TextView m_button1,m_button2,m_button3,m_button4, m_managegroups;

    // m_currentuserID represents the user ID value stored in the database. It helps to uniquely identify a person.
    // As the user is currently logged in we can get it using firebase authorization.
    private String m_currentuserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    // user reference is the database reference for user names and their related data.
    // It is used when we need to access or delete the groups from database.
     private DatabaseReference m_userreference = FirebaseDatabase.getInstance().getReference().child("Users");

     // group reference is the database reference for group names. It is used when we need to access or delete the groups from database.
     private DatabaseReference m_groupreference = FirebaseDatabase.getInstance().getReference().child("Groups");

     private GroupselectorAdapter m_myadapter;

     private Toast m_toast;

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
      *     The onCreate function stores the reference to UI buttons,toolbars,Image_button and
      *     sets up the on click listener's as required.It also initializes the recycler view
      *     in order to show users group list.  Manage_groups button's visibility is turned on or
      *     off depending the user's type from the onCreate method itself.
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
        setContentView(R.layout.activity_groups);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Groups");

        //this helps to bring up or hide the keyboard
        InputMethodManager keyboardpopper = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        //button ids from xml file
        m_managegroups=findViewById(R.id.managegroups);
        m_button1=findViewById(R.id.editgroup1);
        m_button2=findViewById(R.id.editgroup2);
        m_button3=findViewById(R.id.editgroup3);
        m_button4=findViewById(R.id.editgroup4);

        //placeholders for group names
        m_group1 = findViewById(R.id.group1view);
        m_group2 = findViewById(R.id.group2view);
        m_group3 = findViewById(R.id.group3view);
        m_group4 = findViewById(R.id.group4view);

        // //Image buttons for navigating through the 5 main activities.
        ImageButton assessbutton=findViewById(R.id.assessButton);
        ImageButton homebutton=findViewById(R.id.homeButton);
        ImageButton resourcebutton=findViewById(R.id.resourcesButton);
        ImageButton uploadbutton=findViewById(R.id.uploadButton);

        //on click listener that helps to determine the next activity that the user wants
        // to navigate to.
        assessbutton.setOnClickListener(this);
        homebutton.setOnClickListener(this);
        resourcebutton.setOnClickListener(this);
        uploadbutton.setOnClickListener(this);

        RecyclerView myrecview=findViewById(R.id.myrecview);
        //setting up recycler view by feeding Usergroups objects from FirebaseDatabase
        try {
            FirebaseRecyclerOptions<UserGroups> options =
                    new FirebaseRecyclerOptions.Builder<UserGroups>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Groups"), UserGroups.class)
                            .build();

            //setting up Layout manager for recycler view.
            myrecview.setLayoutManager(new LinearLayoutManager(this));
            m_myadapter=new GroupselectorAdapter(options,this);
            myrecview.setAdapter(m_myadapter);
        }catch (Exception e)
        {
            System.out.println("Error encountered: "+e);
        }

        //populating groups names for all the groups on create.
        m_userreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Datasnapshot) {


                // Getting the reference for group names text views and populating text
                //  box with the user groups name using the users object stored in database.
                Users user = Datasnapshot.child(m_currentuserID).getValue(Users.class);

                m_group1.setText(user.getGroup1());
                m_group2.setText(user.getGroup2());
                m_group3.setText(user.getGroup3());
                m_group4.setText(user.getGroup4());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //as mentioned on Firebase website onCancelled will be triggered in the event that
                // this listener either failed at the server, or is removed as a result of the security and Firebase Database rules.
                Toast.makeText(GroupsActivity.this, "Error with the database", Toast.LENGTH_SHORT).show();
            }

        });

        //Onclick listener help to collect and update the user groups based on user inputs.
        //m_button 1 listens to on clicks for first user group displayed on the screen.
        m_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_button1.getText().equals("Edit")){

                    //Help taken from:  https://stackoverflow.com/questions/6217378/place-cursor-at-the-end-of-text-in-edittext
                    //this helps to put the keyboard cursor at the last letter instead of the first letter of a word.
                    m_group1.setSelection(m_group1.getText().length());
                    //ChangeState shows the save button and pops up the keyboard automatically for the user to type in.
                    ChangeState(m_button1,m_group1,keyboardpopper);
                }
                else{

                    //Save states saves input data to database and hides the keyboard
                    SaveState(m_group1,"group1",m_button1,keyboardpopper);
                }
            }
        });

        //m_button 2 listens to on clicks for second user group displayed on the screen.
        m_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_button2.getText().equals("Edit")){
                    m_group2.setSelection(m_group2.getText().length());
                    //ChangeState shows the save button and pops up the keyboard automatically for the user to type in.
                    ChangeState(m_button2,m_group2,keyboardpopper);
                }
                else{
                    //Save states saves input data to database and hides the keyboard
                    SaveState(m_group2,"group2",m_button2,keyboardpopper);
                }
            }
        });

        //m_button3 listens to on clicks for third user group displayed on the screen.
        m_button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_button3.getText().equals("Edit")){
                    m_group3.setSelection(m_group3.getText().length());
                    //ChangeState shows the save button and pops up the keyboard automatically for the user to type in.
                    ChangeState(m_button3,m_group3,keyboardpopper);
                }
                else{
                    //Save states saves input data to database and hides the keyboard
                    SaveState(m_group3,"group3",m_button3,keyboardpopper);
                }
            }
        });

        //m_button4 listens to on clicks for fourth user group displayed on the screen.
        m_button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_button4.getText().equals("Edit")){
                    m_group4.setSelection(m_group4.getText().length());
                    //ChangeState shows the save button and pops up the keyboard automatically for the user to type in.
                    ChangeState(m_button4,m_group4,keyboardpopper);
                }
                else{
                    //Save states saves input data to database and hides the keyboard
                    SaveState(m_group4,"group4",m_button4,keyboardpopper);
                }
            }
        });

        //gets the user class object from the database to check if the user is "admin" or "member" .
        m_userreference.child(m_currentuserID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    //database error
                    Toast.makeText(GroupsActivity.this, "Database error", Toast.LENGTH_SHORT).show();

                }
                else {
                    if(task.getResult().getValue().toString().equals("admin")){
                        //if the user is admin button is visible
                        //visibility is set to invisible by default in xml layout.
                        m_managegroups.setVisibility(View.VISIBLE);
                    }
                    else{
                        //no privilege to edit groups
                    }
                }
            }
        });

        //Takes the admin users to another activity to modify, add or remove the existing groups.
        m_managegroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GroupsmanagerActivity.class));
                finish();
            }
        });

    }


 /**/
 /*
  *   NAME
  *     public void ChangeState
  *
  *   SYNOPSIS
  *     public void ChangeState(TextView a_button, EditText a_groupholder,InputMethodManager a_keyboardmanager)
  *             Textview a_button---> The button which causes the on-click event to trigger while the user enters the value
  *                                 and saves it.
  *             InputMethodManager a_keyboardmanager--> Keyboard manager object which allows the soft keyboard to be displayed
  *                                and be hidden based on the user input expectation.
  *             EditText a_groupholder--> the name of the group that user has entered on EditText field manually
  *
  *
  *   DESCRIPTION
  *     ChangeState function works to modify the state of button and the keyboard to
  *     allow users to input the groups values.
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
    public void ChangeState(TextView a_button, EditText a_groupholder,InputMethodManager a_keyboardmanager){
        //Text changed from Edit to Save
        a_button.setText("Save");
        //Uneditable EditText changed to Editable
        a_groupholder.setFocusableInTouchMode(true);
        a_groupholder.setFocusable(true);
        //Cursor is moved to the EditText automatically
        a_groupholder.requestFocus();
        //Keyboard pops up automatically to help user input text faster.
        a_keyboardmanager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }


 /**/
 /*
  *   NAME
  *     public void SaveState
  *
  *   SYNOPSIS
  *     public void SaveState(EditText a_groupholder, String a_groupid,TextView a_button,InputMethodManager a_keyboardmanager)
  *         EditText a_groupholder--> the name of the group that user has entered on EditText field manually
  *         String a_groupid---> Groupnumber between 1-4 where the groupname will be stored.
  *         Textview a_button--->The button which causes the on-click event to trigger while the user enters the value
  *                             and saves it.
  *         InputMethodManager a_keyboardmanager--> Keyboard manager object which allows the soft keyboard to be displayed
  *                             and be hidden based on the user input expectation.
  *
  *   DESCRIPTION
  *     SaveState function allows the users to enter the groups they want to the EditText field and save
  *     it manually. The user input group name is than checked and if it exists on the groups list,
  *     the new group is matched with his old groups and than saved on the database.Also the state of
  *     buttons and the keyboard are also changed to convey the state of the user input process.
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
    public void SaveState(EditText a_groupholder, String a_groupid,TextView a_button,InputMethodManager a_keyboardmanager){

        m_groupreference.child(a_groupholder.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    //database error
                    Toast.makeText(GroupsActivity.this, "Database error", Toast.LENGTH_SHORT).show();

                }
                else {
                    if(task.getResult().getValue()!=(null)){
                        //If the value is not null that means group is present in the database.
                        //saving the user group on the database for the user.
                        CheckandsetGroup(a_groupid,a_groupholder.getText().toString());
                       // m_userreference.child(m_currentuserID).child(groupid).setValue(groupholder.getText().toString());
                    }
                    else{
                        //If the value is null that means group is not present in the database.
                        Toast.makeText(GroupsActivity.this,  " Sorry group does not exist!!!", Toast.LENGTH_SHORT).show();
                    }
                }
                //Changing status of the button to edit and making the placeholder uneditable before user presses edit button.
                a_button.setText("Edit");
                a_groupholder.setFocusableInTouchMode(false);
                a_groupholder.setFocusable(false);
                //hiding the keyboard after the group is saved
                a_keyboardmanager.hideSoftInputFromWindow(a_groupholder.getWindowToken(), 0);
            }
        });



    }

 /**/
 /*
  *   NAME
  *     public void CheckandsetGroup
  *
  *   SYNOPSIS
  *     public void CheckandsetGroup(String a_groupid,String a_groupname)
  *         String a_groupid--->the group in which the selected groupname is to be stored.
  *                     can range between group1 to group4
  *         String a_groupname--->the group which the user has chosen to be placed on.
  *
  *   DESCRIPTION
  *     This function ensures that the groupname that the user chooses is not already present in
  *     his groups list. If the new group chosen is not already present or duplicated, it will
  *     be saved in one of the four spots for the groups.The groups list are also updated
  *     on the database.If the user enters repetitive groups a toast message is shown
  *     in order to notify the user.
  *
  *     Help taken from
  *     //https://stackoverflow.com/questions/6925156/
  *     on the topic how-to-avoid-a-toast-if-theres-one-toast-already-being-shown?
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

     public void CheckandsetGroup(String a_groupid,String a_groupname){
         m_userreference.child(m_currentuserID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<DataSnapshot> task) {
                 Users currentuser=task.getResult().getValue(Users.class);
                 if(currentuser.getGroup1().equals(a_groupname) || currentuser.getGroup2().equals(a_groupname) ||
                         currentuser.getGroup3().equals(a_groupname) || currentuser.getGroup4().equals(a_groupname))
                 {
                     // Help taken from: https://stackoverflow.com/questions/6925156/how-to-avoid-a-toast-if-theres-one-toast-already-being-shown
                     if(m_toast!=null){
                         m_toast.cancel();
                     }
                     m_toast=Toast.makeText(GroupsActivity.this,"Group already present in your list",Toast.LENGTH_SHORT);
                     m_toast.show();

                     //restarting the activity so that the text box for group name gets reloaded.
                     finish();
                     startActivity(getIntent());

                 }
                 else{
                     Toast.makeText(GroupsActivity.this,  "Group Updated " , Toast.LENGTH_SHORT).show();
                     m_userreference.child(m_currentuserID).child(a_groupid).setValue(a_groupname);
                 }
             }
         });

     }

 /**/
 /*
  *   NAME
  *          protected void onStart
  *
  *   SYNOPSIS
  *         protected void onStart()
  *         no parameters
  *
  *   DESCRIPTION
  *         This function helps the adapter to start binding or populating the adapters
  *         with the values from the database.
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
     protected void onStart() {
         try {
             super.onStart();
             m_myadapter.startListening();
         }catch (Exception e){
             System.out.println("Error encountered"+ e);
         }
     }
 /**/
 /*
  *   NAME
  *          protected void onStop
  *
  *   SYNOPSIS
  *         protected void onStop()
  *         no parameters
  *
  *   DESCRIPTION
  *         This function helps the adapter to stop binding or populating the adapters
  *         with the values from the database.
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
     protected void onStop() {
         try{
             super.onStop();
             m_myadapter.stopListening();
         }
         catch (Exception e){
             System.out.println("Error encountered"+e);
         }
     }

/**/
/*
*   NAME
*       public void onBackPressed
*
*   SYNOPSIS
*       public void onBackPressed()
*       no parameters
*
*   DESCRIPTION
*       This function takes the user back to main activity instead of exiting an app when back button is
*       pressed.
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
     public void onBackPressed()
     {
         Intent homeIntent = new Intent(GroupsActivity.this, MainActivity.class);
         startActivity(homeIntent);
         finish();
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

             case R.id.assessButton: /** Start a new Activity MyCards.java */
                 startActivity(new Intent(getApplicationContext(), GroupsActivity.class));
                 finish();
                 break;

             case R.id.homeButton: /**erDialog when click on Exit */
                 startActivity(new Intent(getApplicationContext(), MainActivity.class));
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
         }
     }

 }
