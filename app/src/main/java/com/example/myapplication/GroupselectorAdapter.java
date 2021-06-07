package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GroupselectorAdapter extends FirebaseRecyclerAdapter<UserGroups,GroupselectorAdapter.myviewholder> {

    // r_id represents the user ID value stored in the database. It helps to uniquely identify a person.
    private String m_currentuserid= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

    // user reference is the database reference for user names and their related data. It is used when we need to access or delete the groups from database.
    private DatabaseReference user_reference = FirebaseDatabase.getInstance().getReference().child("Users");
    private Context m_context;
    private Toast m_toast;

    public GroupselectorAdapter(@NonNull FirebaseRecyclerOptions<UserGroups> options, Context a_context) {
        super(options);
        m_context=a_context;
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull UserGroups model) {
        holder.a_text.setText(model.getGroup_name());
        holder.a_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckandsetGroup("group1",model);
            }
        });
        holder.a_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckandsetGroup("group2",model);
            }
        });
        holder.a_button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckandsetGroup("group3",model);
            }
        });
        holder.a_button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckandsetGroup("group4",model);
            }
        });




    }

    public void CheckandsetGroup(String groupid,UserGroups  model){
        user_reference.child(m_currentuserid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Users a_currentuser=task.getResult().getValue(Users.class);
                if(a_currentuser.getGroup1().equals(model.getGroup_name()) || a_currentuser.getGroup2().equals(model.getGroup_name()) ||
                        a_currentuser.getGroup3().equals(model.getGroup_name()) || a_currentuser.getGroup4().equals(model.getGroup_name()))
                {
                    //https://stackoverflow.com/questions/6925156/how-to-avoid-a-toast-if-theres-one-toast-already-being-shown
                    if(m_toast!=null){
                       m_toast.cancel();
                    }
                    m_toast=Toast.makeText(m_context,"Group already present in your list",Toast.LENGTH_SHORT);
                    m_toast.show();

                }
                else{
                    user_reference.child(m_currentuserid).child(groupid).setValue(model.getGroup_name());
                }
            }
        });

    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.singlegroupoptionsrow,parent,false);
        return new myviewholder(view);
    }


    class myviewholder extends RecyclerView.ViewHolder{
        TextView a_text;
        Button  a_button1,a_button2,a_button3,a_button4;
        public myviewholder(@NonNull View itemView) {
            super(itemView);
            a_text=(TextView) itemView.findViewById(R.id.groupid);
            a_button1=(Button) itemView.findViewById(R.id.group1button);
            a_button2=(Button) itemView.findViewById(R.id.group2button);
            a_button3=(Button) itemView.findViewById(R.id.group3button);
            a_button4=(Button) itemView.findViewById(R.id.group4button);
        }
    }
}
