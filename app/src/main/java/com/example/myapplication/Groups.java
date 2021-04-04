package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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

public class Groups extends AppCompatActivity {
    EditText group1, group2, group3, group4;
    TextView groupdisplay, showname;
    EditText groupname;


    // r_id represents the user ID value stored in the database. It helps to uniquely identify a person.
    String r_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    // user reference is the database reference for user names and their related data.
    // It is used when we need to access or delete the groups from database.
    DatabaseReference userreference = FirebaseDatabase.getInstance().getReference().child("Users");
    // group reference is the database reference for group names. It is used when we need to access or delete the groups from database.
    DatabaseReference groupreference = FirebaseDatabase.getInstance().getReference().child("Groups");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        userreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Datasnapshot) {

                //populating text box with the user groups data.
                Users student = Datasnapshot.child(r_id).getValue(Users.class);
                group1 = findViewById(R.id.group1view);
                group2 = findViewById(R.id.group2view);
                group3 = findViewById(R.id.group3view);
                group4 = findViewById(R.id.group4view);
                group1.setText(student.group1);
                group2.setText(student.group2);
                group3.setText(student.group3);
                group4.setText(student.group4);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    public void  Create_Group(View view){
        //creating a new group based on the name provided by the username
        groupname=findViewById(R.id.groupname);
        Date date=new Date(2001-01-01);
        UserGroups group= new UserGroups(groupname.getText().toString(), date, 0);
        groupreference.child(groupname.getText().toString()).setValue(group);

    }
    public void GotoMain(View view){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

    public void Search_Group(View view){

        Button creategroup;
        creategroup=findViewById(R.id.create_group);

        userreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Datasnapshot) {


                groupdisplay=findViewById(R.id.display_group);
                groupname=findViewById(R.id.groupname);
                showname=findViewById(R.id.display_name);
                Users student = Datasnapshot.child(r_id).getValue(Users.class);

                groupreference.child(groupname.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            // if group exists displaying group info and showing delete group option based on user type
                            groupdisplay.setText("Group found on the list. Group Name:  " );
                            showname.setText(groupname.getText().toString());
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
                            groupdisplay.setText("Group Not Found! Make sure the name is correct  ");
                            showname.setText(groupname.getText().toString());
                            creategroup.setVisibility(View.INVISIBLE);

                            //Displaying create group option if the user is admin
                            if(student.usertype.equals("admin")){
                                groupdisplay.setText("Group Not Found, Add following group?  ");
                                creategroup.setText("Create Group");
                                creategroup.setVisibility(View.VISIBLE);
                            }

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Groups.this, "Error with the database", Toast.LENGTH_SHORT).show();
                    }
                });

                //Making the group status text visible
                groupdisplay.setVisibility(View.VISIBLE);

                //Event listener for create group button
                creategroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //creating the group if the group name is present and the user type is admin
                        if (creategroup.getText().toString()=="Create Group"  && student.usertype.equals("admin") && (!groupname.getText().equals(""))) {
                            creategroup.setVisibility(View.INVISIBLE);
                            groupdisplay.setVisibility(View.INVISIBLE);
                            Date date = new Date(2001 - 01 - 01);
                            UserGroups group = new UserGroups(groupname.getText().toString(), date, 0);
                            groupreference.child(groupname.getText().toString()).setValue(group);
                            showname.setText("Group added to the list");
                        }

                        //deleting a group based in event from delete button if the user-type is admin
                        if ((!groupname.getText().toString().equals(""))){
                            if (creategroup.getText().toString()=="Delete Group" && student.usertype.equals("admin") ) {
                                creategroup.setVisibility(View.INVISIBLE);
                                groupdisplay.setVisibility(View.INVISIBLE);
                                groupreference.child(groupname.getText().toString()).setValue(null);
                                showname.setText("Group deleted from the list");
                            }
                        }
                        else{
                            // In case user tries to create a group without entering the group name
                            Toast.makeText(Groups.this, "Cannot Create empty group", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(Groups.this, "Error with the database", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void EditGroup(View view, Button editgroup,EditText groupname, Integer groupnumber){

        //Event listener for the editgroup button to toggle between editing the group and saving the changes.
        editgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userreference.addValueEventListener(new ValueEventListener() {
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
                            Users user = Datasnapshot.child(r_id).getValue(Users.class);

                            //using database reference to check if  the new group name exists in the database.
                            groupreference.child(groupname.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public synchronized void onDataChange(DataSnapshot snapshot) {

                                    if (snapshot.exists()) {
                                        //If the group name entered is valid , group value in user class is updated as required using select Case.
                                        // cases are chosen based on the group number provided in function parameters.

                                        switch (groupnumber) {
                                            case 1:
                                                user.group1 = groupname.getText().toString();
                                                userreference.child(r_id).setValue(user);
                                                break;
                                            case 2:
                                                user.group2 = groupname.getText().toString();
                                                userreference.child(r_id).setValue(user);
                                                break;
                                            case 3:
                                                user.group3 = groupname.getText().toString();
                                                userreference.child(r_id).setValue(user);
                                                break;
                                            case 4:
                                                user.group4 = groupname.getText().toString();
                                                userreference.child(r_id).setValue(user);
                                                break;
                                        }

                                        //letting the user know change was made
                                        Toast.makeText(Groups.this, "Group changed.", Toast.LENGTH_SHORT).show();

                                    }
                                    else{
                                        //Since the group was not available, displaying related information through toast.
                                        //Also showing the group name in text box that was present before modification.
                                        groupname.setText(user.group4);
                                        Toast.makeText(Groups.this, "Non-existent group name", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public synchronized void onCancelled(@NonNull DatabaseError error) {

                                    //In order to display database errors
                                    Toast.makeText(Groups.this, "Error with the database", Toast.LENGTH_SHORT).show();
                                }
                            });
                            editgroup.setText("Edit");

                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        //In order to display database errors
                        Toast.makeText(Groups.this, "Error with the database", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }



    public void Edit_Group1(View view){
        // It helps to capture the events from the Edit group 1 button from the UI
        Button editgroup1=findViewById(R.id.editgroup1);

        //helps to capture events for text box that occupies the group name(1/4th group)
        group1 = findViewById(R.id.group1view);

        // Once the edit button is pressed text box is editable and the button in renamed"SAVE" as users can save the contents now
        editgroup1.setText("Save");
        group1.setFocusableInTouchMode(true);
        group1.setFocusable(true);

        // calling the function that will help to store the contents of the text box into the database.
        EditGroup(view, editgroup1,group1,1);
    }

    public void Edit_Group2(View view){
        // It helps to capture the events from the Edit groups 2 button from the UI
        Button editgroup2=findViewById(R.id.editgroup2);

        //helps to capture events for text box that occupies the group name(2/4th group)
        group2 = findViewById(R.id.group2view);

        // Once the edit button is pressed text box is editable and the button in renamed"SAVE" as users can save the contents now
        editgroup2.setText("Save");
        group2.setFocusableInTouchMode(true);
        group2.setFocusable(true);

        // calling the function that will help to store the contents of the text box into the database.
        EditGroup(view, editgroup2,group2,2);
    }


    public void Edit_Group3(View view){
        // It helps to capture the events from the Edit groups 3 button from the UI
        Button editgroup3=findViewById(R.id.editgroup3);

        //helps to capture events for text box that occupies the group name(3/4th group).
        group3 = findViewById(R.id.group3view);

        // Once the edit button is pressed text box is editable and the button in renamed"SAVE" as users can save the contents now
        editgroup3.setText("Save");
        group3.setFocusableInTouchMode(true);
        group3.setFocusable(true);

        // calling the function that will help to store the contents of the text box into the database.
        EditGroup(view, editgroup3,group3,3);
    }

    // changes the group name for one of the user groups.
    public void Edit_Group4(View view){

        // It helps to capture the events from the Edit groups 4 button from the UI
        Button editgroup=findViewById(R.id.editgroup4);

        //helps to capture events for text box that occupies the group name(4/4th group).
        group4 = findViewById(R.id.group4view);

        // Once the edit button is pressed text box is editable and the button in renamed"SAVE" as users can save the contents now.
        editgroup.setText("Save");
        group4.setFocusableInTouchMode(true);
        group4.setFocusable(true);

        // calling the function that will help to store the contents of the text box into the database.
        EditGroup(view, editgroup,group4,4);

    }
}
