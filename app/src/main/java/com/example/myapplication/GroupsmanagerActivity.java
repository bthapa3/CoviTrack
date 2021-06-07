package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class GroupsmanagerActivity extends AppCompatActivity {

    EditText m_addgroup,m_removegroup;
    Button m_addbutton, m_removebutton;
    RecyclerView m_grouprecview;
    GroupsAdapter GroupAdapter;
    private DatabaseReference m_groupreference = FirebaseDatabase.getInstance().getReference().child("Groups");
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupsmanager);
        m_addgroup=findViewById(R.id.addgroup);
        m_removegroup=findViewById(R.id.removegroup);
        m_addbutton=findViewById(R.id.addbutton);
        m_removebutton=findViewById(R.id.removebutton);
        m_grouprecview =findViewById(R.id.grouprecview);



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
            System.out.println("error"+e);
        }



        m_addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // UserGroups group= new UserGroups(m_groupname.getText().toString(),m_roomnumber.getText().toString());

                m_groupreference.child(m_addgroup.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            //database error
                            Toast.makeText(GroupsmanagerActivity.this, "Database Error", Toast.LENGTH_SHORT).show();

                        }
                        else {
                            if(task.getResult().getValue()==(null)){
                                UserGroups newuser=new UserGroups(m_addgroup.getText().toString());
                                m_groupreference.child(m_addgroup.getText().toString()).setValue(newuser);
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

        m_removebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // UserGroups group= new UserGroups(m_groupname.getText().toString(),m_roomnumber.getText().toString());

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

    @Override
    protected void onStart() {
        try {
            super.onStart();
            GroupAdapter.startListening();
        }catch (Exception e){
            System.out.println("Error inside onstart");
        }
    }

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

}