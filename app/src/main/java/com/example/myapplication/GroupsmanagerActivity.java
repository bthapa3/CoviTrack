package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
/**/
/*
 *  CLASS DESCRIPTION:
 *     Allows the admin user to create new groups or delete the groups
 *      inside an organization so that the normal users can join them.
 *
 *  PURPOSE:
 *
 *      The main purpose of this activity is to allow the admin users to manage groups.
 *      With this activity they can create any new group. They can also view all the groups
 *      in the recycler view and delete them if needed.
 *
 *
 *  AUTHOR:
 *      Bishal Thapa
 *
 *  DATE
 *       4/27/2021
 */
/**/

public class GroupsmanagerActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText m_addgroup,m_removegroup;
    private Button m_addbutton, m_removebutton;
    private RecyclerView m_grouprecview;
    private GroupsAdapter GroupAdapter;
    private DatabaseReference m_groupreference = FirebaseDatabase.getInstance().getReference().child("Groups");


    /**/
    /*
     *   NAME
     *      protected void onCreate
     *
     *   SYNOPSIS
     *      protected void onCreate(Bundle savedInstanceState)
     *      Bundle savedInstanceState---->reference to a Bundle object
     *
     *   DESCRIPTION
     *     The onCreate function stores the reference to UI buttons,toolbars,Image_button and
     *     sets up the on click listener's as required.It also initializes the recycler view
     *     in order to show users group list. On-click listener for add group and delete group
     *     buttons are used which changes the value on the database as needed.
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
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupsmanager);
        //assigning xml view objects reference to variable.
        m_addgroup=findViewById(R.id.addgroup);
        m_removegroup=findViewById(R.id.removegroup);
        m_addbutton=findViewById(R.id.addbutton);
        m_removebutton=findViewById(R.id.removebutton);
        m_grouprecview =findViewById(R.id.grouprecview);

        //Image buttons for navigating through the 5 main activities.
        ImageButton assessbutton=findViewById(R.id.assessButton);
        ImageButton homebutton=findViewById(R.id.homeButton);
        ImageButton resourcebutton=findViewById(R.id.resourcesButton);
        ImageButton uploadbutton=findViewById(R.id.uploadButton);
        ImageButton groupsbutton=findViewById(R.id.groupButton);


        //on click listener that helps to determine the next activity that the user wants
        // to navigate to.
        assessbutton.setOnClickListener(this);
        homebutton.setOnClickListener(this);
        resourcebutton.setOnClickListener(this);
        uploadbutton.setOnClickListener(this);
        groupsbutton.setOnClickListener(this);

        //setting up recycler view by feeding Usergroups objects from FirebaseDatabase
        try {
            FirebaseRecyclerOptions<UserGroups> options =
                    new FirebaseRecyclerOptions.Builder<UserGroups>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Groups"), UserGroups.class)
                            .build();
            //LinearLayoutManager llm = new LinearLayoutManager(this);
           // m_grouprecview.setLayoutManager(llm);
            m_grouprecview.setLayoutManager(new LinearLayoutManager(this));
            GroupAdapter=new GroupsAdapter(options);
            m_grouprecview.setAdapter(GroupAdapter);
        }catch (Exception e)
        {
            //logging error on the database.
            System.out.println("Error encountered"+e);
        }

        //On click listener for the add button which allows the admin to add group by
        // entering the group in the EditText.
        m_addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                m_groupreference.child(m_addgroup.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            //database error
                            Toast.makeText(GroupsmanagerActivity.this, "Database Error", Toast.LENGTH_SHORT).show();

                        }
                        else {
                            if(task.getResult().getValue()==(null)){
                                UserGroups newgroup=new UserGroups(m_addgroup.getText().toString());
                                m_groupreference.child(m_addgroup.getText().toString()).setValue(newgroup);
                                Toast.makeText(GroupsmanagerActivity.this, "Group Added to Database", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(GroupsmanagerActivity.this, task.getResult().getValue().toString()+ "  already present", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                });
            }
        });

        //this on-click listener helps to remove the group name manually by entering the name in text field.
        //rather than searching for the group name in the list.
        //This helps the user to delete the groups faster if they already know the name of the group they need to delete.
        m_removebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                m_groupreference.child(m_removegroup.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            //database error
                            Toast.makeText(GroupsmanagerActivity.this, task.getResult().getValue().toString(), Toast.LENGTH_SHORT).show();

                        }
                        else {
                            if(task.getResult().getValue()==(null)){
                                Toast.makeText(GroupsmanagerActivity.this, "Group not found on Database", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                m_groupreference.child(m_removegroup.getText().toString()).setValue(null);
                                Toast.makeText(GroupsmanagerActivity.this, task.getResult().getValue().toString()+ " deleted", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                });


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
            GroupAdapter.startListening();
        }catch (Exception e){
            System.out.println("Error inside onstart");
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
            GroupAdapter.stopListening();
        }
        catch (Exception e){
            System.out.println("error inside stop"+e);
        }
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
                startActivity(new Intent(getApplicationContext(), AssesmentActivity.class));
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

            case R.id.groupButton:
                startActivity(new Intent(getApplicationContext(),GroupsActivity.class));
                finish();
                break;

            default:
                return;
        }
    }

}