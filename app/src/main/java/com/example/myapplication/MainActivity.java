package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.Reference;
import java.util.Date;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    EditText group1,group2,group3,group4;
    TextView groupdisplay,showname;
    EditText groupname;

    // r_id represents the user ID value stored in the database. It helps to uniquely identify a person.
    String r_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Datasnapshot) {
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
    public void Logout(View view){
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(),login.class));
        finish();
    }

    public void  Create_Group(View view){

        groupname=findViewById(R.id.groupname);
        DatabaseReference reference2= FirebaseDatabase.getInstance().getReference().child("Groups");
        Date date=new Date(2001-01-01);
        UserGroups group= new UserGroups(groupname.getText().toString(), date, 0);
        reference2.child(groupname.getText().toString()).setValue(group);


    }

    public void Search_Group(View view){

        Button creategroup;
        creategroup=findViewById(R.id.create_group);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users");
        DatabaseReference reference2= FirebaseDatabase.getInstance().getReference().child("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Datasnapshot) {
                // This method is called once with the initial value and again

                groupdisplay=findViewById(R.id.display_group);
                groupname=findViewById(R.id.groupname);
                showname=findViewById(R.id.display_name);
                Users student = Datasnapshot.child(r_id).getValue(Users.class);
                if(student.total_groups<2){

                    reference2.child(groupname.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.exists()) {

                                groupdisplay.setText("Group found on the list. Group Name:  " );
                                showname.setText(groupname.getText().toString());
                                // TODO: handle the case where the data already exists
                                creategroup.setText("Delete Group");
                                creategroup.setVisibility(View.VISIBLE);


                            }
                            else {

                                groupdisplay.setText("Group Not Found, Add following group?  ");
                                showname.setText(groupname.getText().toString());
                                creategroup.setText("Create Group");
                                creategroup.setVisibility(View.VISIBLE);


                                // TODO: handle the case where the data does not yet exist
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    groupdisplay.setVisibility(View.VISIBLE);

                    creategroup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (creategroup.getText().toString()=="Create Group") {
                                creategroup.setVisibility(View.INVISIBLE);
                                groupdisplay.setVisibility(View.INVISIBLE);
                                Date date = new Date(2001 - 01 - 01);
                                UserGroups group = new UserGroups(groupname.getText().toString(), date, 0);
                                reference2.child(groupname.getText().toString()).setValue(group);
                                showname.setText("Group added to the list");
                            }

                            if (creategroup.getText().toString()=="Delete Group") {
                                creategroup.setVisibility(View.INVISIBLE);
                                groupdisplay.setVisibility(View.INVISIBLE);
                                reference2.child(groupname.getText().toString()).setValue(null);
                                showname.setText("Group deleted from the list");
                            }
                        }
                    });

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error with the database", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void Edit_Groups(View view){
        // It helps to capture the events from the Edit groups button from the UI
        Button editgroup=findViewById(R.id.editgroup);
        // group 1 to 4 help to determine the edit text views from the UI where users groups
        //are displayed and modified
        group1 = findViewById(R.id.group1view);
        group2 = findViewById(R.id.group2view);
        group3 = findViewById(R.id.group3view);
        group4 = findViewById(R.id.group4view);

        // Used to make changes on the UI. Makes the edit text view editable and uneditable based on the users input
        // Once the edit button is presses the button's name is displayed as Save changes

        editgroup.setText("Save changes");
        group1.setFocusableInTouchMode(true);
        group1.setFocusable(true);
        group2.setFocusableInTouchMode(true);
        group2.setFocusable(true);
        group3.setFocusableInTouchMode(true);
        group3.setFocusable(true);
        group4.setFocusableInTouchMode(true);
        group4.setFocusable(true);

        //Event listener for the editgroup button to toggle between editing the group and saving the changes.
        editgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating a database reference to a location at the database where the Users data is going to be stored after modifications on the Uw
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users");

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot Datasnapshot) {

                        //Getting the user's information stores in database as Users class object.
                        Users student = Datasnapshot.child(r_id).getValue(Users.class);
                        group1 = findViewById(R.id.group1view);
                        group2 = findViewById(R.id.group2view);
                        group3 = findViewById(R.id.group3view);
                        group4 = findViewById(R.id.group4view);

                        // Modifying UI based on the current value by flipping editable property and text displayed on Button.
                        if(editgroup.getText()=="Edit Groups"){
                            //setFocusableInTouchMode(True) allows the users to edit the edit text field succesfully.
                            group1.setFocusableInTouchMode(true);
                            group1.setFocusable(true);
                            group2.setFocusableInTouchMode(true);
                            group2.setFocusable(true);
                            group3.setFocusableInTouchMode(true);
                            group3.setFocusable(true);
                            group4.setFocusableInTouchMode(true);
                            group4.setFocusable(true);
                            editgroup.setText("Save changes");
                        }
                        else{
                            // This sets the edit text field to uneditable
                            group1.setFocusable(false);
                            group2.setFocusable(false);
                            group3.setFocusable(false);
                            group4.setFocusable(false);
                            student.group1=group1.getText().toString();
                            student.group2=group2.getText().toString();
                            student.group3=group3.getText().toString();
                            student.group4=group4.getText().toString();
                            reference.child(r_id).setValue(student);
                            editgroup.setText("Edit Groups");
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Error with the database", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

    }


}