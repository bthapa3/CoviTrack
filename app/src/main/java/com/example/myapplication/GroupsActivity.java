package com.example.myapplication;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;


 /**
  * GroupsActivity Class manages all the operations related with user-groups
  * Admin can search and create groups if it does not already exist
  *
  * Admin can also delete and join groups
  * Other users can search and join  groups but cannot create or delete
  *
  * User are displayed groups they belong to.
  * Default values are null for all 4 groups initially.
  *
  * User can have up to 4 groups and they can change them.
  * Edit options are available for user to change the group names
  *
  * home button, assessment button and other buttons available at the bottom of
  * groups activity each have a function that redirects user to the specific activities.
  *
 **/

public class GroupsActivity extends AppCompatActivity {

    // Reference for the Editable text views that stores the names of the respective groups
    private EditText m_group1, m_group2, m_group3, m_group4;

    // m_display status stores the Text view that displays the status of the group searched(i.e: group exists or group doesnot exist)
    // m_showgroupname stores the Text view that displays the name of the group that user searched.
    private TextView m_displaystatus, m_showgroupname;

    //m_group name holds the name of the group that user searches.
    private EditText m_groupname;

    // m_currentuserID represents the user ID value stored in the database. It helps to uniquely identify a person.
    // As the user is currently logged in we can get it using firebase authorization.
    private String m_currentuserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    // user reference is the database reference for user names and their related data.
    // It is used when we need to access or delete the groups from database.
     private DatabaseReference m_userreference = FirebaseDatabase.getInstance().getReference().child("Users");

     // group reference is the database reference for group names. It is used when we need to access or delete the groups from database.
    DatabaseReference m_groupreference = FirebaseDatabase.getInstance().getReference().child("Groups");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        m_userreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Datasnapshot) {

                // Getting the reference for group names text views and populating text
                //  box with the user groups name using the users object stored in database.
                Users user = Datasnapshot.child(m_currentuserID).getValue(Users.class);
                m_group1 = findViewById(R.id.group1view);
                m_group2 = findViewById(R.id.group2view);
                m_group3 = findViewById(R.id.group3view);
                m_group4 = findViewById(R.id.group4view);
                m_group1.setText(user.group1);
                m_group2.setText(user.group2);
                m_group3.setText(user.group3);
                m_group4.setText(user.group4);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //as mentioned on Firebase website onCancelled will be triggered in the event that
                // this listener either failed at the server, or is removed as a result of the security and Firebase Database rules.
                Toast.makeText(GroupsActivity.this, "Error with the database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**/
    /*
    NAME

         public void Logout

    SYNOPSIS

         public void Logout(View view);
         view   --> allows the Logout method to be called on-click with the view instance

    DESCRIPTION

            This function will allow the user to logout of the application. This function
            will sign out the user from Firebase Authorization and redirect the user towards
            Login Activity.

    RETURNS
            Nothing

    AUTHOR

           Bishal Thapa

    DATE
            4/27/2021

    */
    /**/
    public void Logout(View view){

        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

     /**/
    /*
    NAME

        public void Create_Group

    SYNOPSIS

        public void Create_Group(View view)
        view   --> allows the Create_Group method to be called on-click with the view instance
                    also used to find groupname by using view.

    DESCRIPTION
        This function allows the user to create a group if the group does not
        already exist. Users are checked before calling this function so only admins
        can create a group. Create_Group function runs on on-click event with view instance.
        View instance is also used to get the group name entered by user. Uses m_groupreference
        to store group information on database and used groupname as a key.

    RETURNS
            Nothing

    AUTHOR

           Bishal Thapa

    DATE
            4/27/2021

    */
     /**/
    public void Create_Group(View view){

        //creating a new group based on the name provided by the username
        m_groupname=findViewById(R.id.groupname);
        UserGroups group= new UserGroups(m_groupname.getText().toString(), 0);
        m_groupreference.child(m_groupname.getText().toString()).setValue(group);

    }
     /**/
    /*
    NAME

        public void GotoHome

    SYNOPSIS

        public void GotoHome(View view)
        view   --> view instance allows the GotoHome method to be called when Home button
                    is clicked during groups activity.

    DESCRIPTION
       This function redirects the user from group activity to main activity whenever function is
       clicked by using on-click event listener.

    RETURNS
            Nothing

    AUTHOR

           Bishal Thapa

    DATE
            4/27/2021

    */
     /**/

    public void GotoHome(View view){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

     /**/
    /*
    NAME

        public void GotoAssess

    SYNOPSIS

        public void GotoAssess(View view)
        view   --> view instance allows the GotoAssess method to be called when Assessment button
                    is clicked during groups activity.

    DESCRIPTION
       This function redirects the user from group activity to assessment activity whenever function is
       clicked by using on-click event listener.

    RETURNS
            Nothing

    AUTHOR

           Bishal Thapa

    DATE
            4/27/2021

    */
     /**/

    public void GotoAsses(View view){
        startActivity(new Intent(getApplicationContext(),AssesmentActivity.class));
        finish();
    }
     /**/
    /*
    NAME

        public void GotoUpload

    SYNOPSIS

        public void GotoAssess(View view)
        view   --> view instance allows the GotoUpload method to be called when Upload button
                    is clicked during groups activity.

    DESCRIPTION
       This function redirects the user from group activity to Upload activity whenever function is
       clicked by using on-click event listener.

    RETURNS
            Nothing

    AUTHOR

           Bishal Thapa

    DATE
            4/27/2021

    */
     /**/
     public void GotoUpload(View view){
         startActivity(new Intent(getApplicationContext(),UploadActivity.class));
         finish();
     }



     public void GotoResources(View view){
         startActivity(new Intent(getApplicationContext(), ResourcesActivity.class));
         finish();
     }

     public void Search_Group(View view){

        Button creategroup;
        creategroup=findViewById(R.id.create_group);

        m_userreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Datasnapshot) {


                m_displaystatus=findViewById(R.id.display_status);
                m_groupname=findViewById(R.id.groupname);
                m_showgroupname=findViewById(R.id.display_name);
                Users student = Datasnapshot.child(m_currentuserID).getValue(Users.class);
                System.out.println(m_groupname.getText().toString());



                m_groupreference.child(m_groupname.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot mysnapshot) {
                        if (mysnapshot.exists()) {

                            // if group exists displaying group info and showing delete group option based on user type
                            m_displaystatus.setText("Group exists on our list!!!!" );
                            m_showgroupname.setText("Group searched:" + m_groupname.getText().toString());
                            creategroup.setVisibility(View.INVISIBLE);

                            //Displaying a delete group button if the user is admin
                            if(student.usertype.equals("admin")){
                                // TODO foe me:  handle the case where the data already exists
                                creategroup.setText("Delete Group");
                                creategroup.setVisibility(View.VISIBLE);
                            }


                        }
                        else {

                            //Displaying group status to the user. In this case group user does not exist.
                            m_displaystatus.setText("Group Not Found! Make sure the name is correct  ");
                            m_showgroupname.setText("Group searched: "+ m_groupname.getText().toString());
                            creategroup.setVisibility(View.INVISIBLE);

                            //Displaying create group option if the user is admin
                            if(student.usertype.equals("admin")){
                                m_displaystatus.setText("Group Not Found, Add following group?  ");
                                creategroup.setText("Create Group");
                                creategroup.setVisibility(View.VISIBLE);
                            }

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(GroupsActivity.this, "Error with the database", Toast.LENGTH_SHORT).show();
                    }
                });

                //Making the group status text visible
                m_displaystatus.setVisibility(View.VISIBLE);

                //Event listener for create group button
                creategroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //creating the group if the group name is present and the user type is admin
                        if (creategroup.getText().toString()=="Create Group"  && student.usertype.equals("admin") && (!m_groupname.getText().equals(""))) {
                            creategroup.setVisibility(View.INVISIBLE);
                            m_displaystatus.setVisibility(View.INVISIBLE);
                            UserGroups group = new UserGroups(m_groupname.getText().toString(), 0);
                            m_groupreference.child(m_groupname.getText().toString()).setValue(group);
                            m_showgroupname.setText("Group added to the list");
                        }

                        //deleting a group based in event from delete button if the user-type is admin
                        if ((!m_groupname.getText().toString().equals(""))){
                            if (creategroup.getText().toString()=="Delete Group" && student.usertype.equals("admin") ) {
                                creategroup.setVisibility(View.INVISIBLE);
                                m_displaystatus.setVisibility(View.INVISIBLE);
                                m_groupreference.child(m_groupname.getText().toString()).setValue(null);
                                m_showgroupname.setText("Group deleted from the list");
                            }
                        }
                        else{
                            // In case user tries to create a group without entering the group name
                            Toast.makeText(GroupsActivity.this, "Cannot Create empty group", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(GroupsActivity.this, "Error with the database", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void EditGroup(View view, Button editgroup,EditText groupname, Integer groupnumber){

        //Event listener for the editgroup button to toggle between editing the group and saving the changes.
        editgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                m_userreference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot Datasnapshot) {

                        // Modifying UI based on the current value by flipping editable property and text displayed on Button.
                        if(editgroup.getText()=="Edit"){

                            //setFocusableInTouchMode(True) allows the users to edit the edit text field successfully.
                            groupname.setFocusableInTouchMode(true);
                            groupname.setFocusable(true);

                            //sets the button text to save since user need to save contents after editing
                            editgroup.setText("Save");

                        }
                        else{

                            // This sets the edit text field to uneditable since user already save the contents
                            groupname.setFocusable(false);

                            //getting the user's information from the database that needs to modified and storing it as Users class.
                            Users user = Datasnapshot.child(m_currentuserID).getValue(Users.class);

                            //using database reference to check if  the new group name exists in the database.
                            m_groupreference.child(groupname.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public synchronized void onDataChange(DataSnapshot snapshot) {

                                    if (snapshot.exists()) {
                                        //If the group name entered is valid , group value in user class is updated as required using select Case.
                                        // cases are chosen based on the group number provided in function parameters.

                                        switch (groupnumber) {
                                            case 1:
                                                user.group1 = groupname.getText().toString();
                                                m_userreference.child(m_currentuserID).setValue(user);
                                                break;
                                            case 2:
                                                user.group2 = groupname.getText().toString();
                                                m_userreference.child(m_currentuserID).setValue(user);
                                                break;
                                            case 3:
                                                user.group3 = groupname.getText().toString();
                                                m_userreference.child(m_currentuserID).setValue(user);
                                                break;
                                            case 4:
                                                user.group4 = groupname.getText().toString();
                                                m_userreference.child(m_currentuserID).setValue(user);
                                                break;
                                        }

                                        //letting the user know change was made
                                        Toast.makeText(GroupsActivity.this, "Group changed.", Toast.LENGTH_SHORT).show();

                                    }
                                    else{
                                        //Since the group was not available, displaying related information through toast.
                                        //Also showing the group name in text box that was present before modification.
                                        groupname.setText(user.group4);
                                        Toast.makeText(GroupsActivity.this, "Non-existent group name", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public synchronized void onCancelled(@NonNull DatabaseError error) {

                                    //In order to display database errors
                                    Toast.makeText(GroupsActivity.this, "Error with the database", Toast.LENGTH_SHORT).show();
                                }
                            });
                            editgroup.setText("Edit");

                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        //In order to display database errors
                        Toast.makeText(GroupsActivity.this, "Error with the database", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }



    public void Edit_Group1(View view){
        // It helps to capture the events from the Edit group 1 button from the UI
        Button editgroup1=findViewById(R.id.editgroup1);

        //helps to capture events for text box that occupies the group name(1/4th group)
        m_group1 = findViewById(R.id.group1view);

        // Once the edit button is pressed text box is editable and the button in renamed"SAVE" as users can save the contents now
        editgroup1.setText("Save");
        m_group1.setFocusableInTouchMode(true);
        m_group1.setFocusable(true);

        // calling the function that will help to store the contents of the text box into the database.
        EditGroup(view, editgroup1,m_group1,1);
    }

    public void Edit_Group2(View view){
        // It helps to capture the events from the Edit groups 2 button from the UI
        Button editgroup2=findViewById(R.id.editgroup2);

        //helps to capture events for text box that occupies the group name(2/4th group)
        m_group2 = findViewById(R.id.group2view);

        // Once the edit button is pressed text box is editable and the button in renamed"SAVE" as users can save the contents now
        editgroup2.setText("Save");
        m_group2.setFocusableInTouchMode(true);
        m_group2.setFocusable(true);

        // calling the function that will help to store the contents of the text box into the database.
        EditGroup(view, editgroup2,m_group2,2);
    }


    public void Edit_Group3(View view){
        // It helps to capture the events from the Edit groups 3 button from the UI
        Button editgroup3=findViewById(R.id.editgroup3);

        //helps to capture events for text box that occupies the group name(3/4th group).
        m_group3 = findViewById(R.id.group3view);

        // Once the edit button is pressed text box is editable and the button in renamed"SAVE" as users can save the contents now
        editgroup3.setText("Save");
        m_group3.setFocusableInTouchMode(true);
        m_group3.setFocusable(true);

        // calling the function that will help to store the contents of the text box into the database.
        EditGroup(view, editgroup3,m_group3,3);
    }

    // changes the group name for one of the user groups.
    public void Edit_Group4(View view){

        // It helps to capture the events from the Edit groups 4 button from the UI
        Button editgroup=findViewById(R.id.editgroup4);

        //helps to capture events for text box that occupies the group name(4/4th group).
        m_group4 = findViewById(R.id.group4view);

        // Once the edit button is pressed text box is editable and the button in renamed"SAVE" as users can save the contents now.
        editgroup.setText("Save");
        m_group4.setFocusableInTouchMode(true);
        m_group4.setFocusable(true);

        // calling the function that will help to store the contents of the text box into the database.
        EditGroup(view, editgroup,m_group4,4);

    }
}
