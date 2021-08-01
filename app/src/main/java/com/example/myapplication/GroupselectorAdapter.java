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

/**/
/*
 *  CLASS DESCRIPTION:
 *    Adapter for the GroupsActivity recycler viw which display admin user groups
 *      and a 4 buttons that allow user store group in the given space.
 *
 *  PURPOSE:
 *    GroupsselectorAdapter works as an adapter for individual groups with 4 different button
 *      User can select 4 button(group1 to group4) to save the given groupname in their
 *      profile.This class also check the duplication of groups to stop users from same
 *      group more than once.
 *
 *  AUTHOR:
 *      Bishal Thapa
 *
 *  DATE
 *       4/27/2021
 */
/**/

public class GroupselectorAdapter extends FirebaseRecyclerAdapter<UserGroups,GroupselectorAdapter.myviewholder> {

    // r_id represents the user ID value stored in the database. It helps to uniquely identify a person.
    private String m_currentuserid= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

    // user reference is the database reference for user names and their related data. It is used when we need to access or delete the groups from database.
    private DatabaseReference m_userreference = FirebaseDatabase.getInstance().getReference().child("Users");
    private Context m_context;
    private Toast m_toast;

    //Constructor for the GroupsAdapter Class
    public GroupselectorAdapter(@NonNull FirebaseRecyclerOptions<UserGroups> options, Context a_context) {
        super(options);
        m_context=a_context;
    }

    /**/
    /*
     *   NAME
     *       protected void onBindViewHolder
     *
     *   SYNOPSIS
     *      protected void onBindViewHolder(@NonNull myviewholder a_holder, int a_position, @NonNull UserGroups a_model)
     *      myviewholder a_holder---> myviewholder object that contains reference to individual adapters.
     *      int a_position---> position of the individual adapter in the array of adapters.
     *      UserGroups a_model--> UserGroups object related to each adapter.
     *
     *   DESCRIPTION
     *      onBindViewHolder Binds the UserGroups object value to specific adapters and also
     *      sets up an onclick listener to each of buttons. On-click trigger is handled by
     *      going through check_and_save method for each button clicked.
     *
     *   RETURNS
     *      Nothing
     *
     *   AUTHOR
     *      Bishal Thapa
     *
     *   DATE
     *      4/27/2021
     *
     */
    /**/
    @Override
    protected void onBindViewHolder(@NonNull myviewholder a_holder, int a_position, @NonNull UserGroups a_model) {
        a_holder.text.setText(a_model.getGroup_name());
        //Button1 which saves the groupname in group holder space 1
        a_holder.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckandsetGroup("group1",a_model);
            }
        });
        //Button2 which saves the groupname in group holder space 2
        a_holder.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckandsetGroup("group2",a_model);
            }
        });
        //Button3 which saves the groupname in group holder space 3
        a_holder.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckandsetGroup("group3",a_model);
            }
        });

        //Button4 which saves the groupname in group holder space 4
        a_holder.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckandsetGroup("group4",a_model);
            }
        });
    }


    /**/
    /*
     *   NAME
     *       public void CheckandsetGroup
     *
     *   SYNOPSIS
     *     public void CheckandsetGroup(String a_groupid,UserGroups  a_model)
     *          String a_groupid--> the group holder id from range group1 to group4
     *          UserGroups a_model--> Usergroups object that holds the info about the group.
     *
     *   DESCRIPTION
     *    CheckandsetGroup method gets the user data saved on the database. It gets
     *      users class value from Firebase realtime database and check all the groups
     *      present to ensure no duplication has happened. If duplication has occured
     *      it shows Toast message. Else the group user chose is saved on the location
     *      user chose.
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
     *  //help taken from
     *  //https://stackoverflow.com/questions/6925156/
     *  //on the topic: how-to-avoid-a-toast-if-theres-one-toast-already-being-shown
     *
     */
    /**/
    public void CheckandsetGroup(String a_groupid,UserGroups a_model){
        m_userreference.child(m_currentuserid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Users a_currentuser=task.getResult().getValue(Users.class);
                if(a_currentuser.getGroup1().equals(a_model.getGroup_name()) || a_currentuser.getGroup2().equals(a_model.getGroup_name()) ||
                        a_currentuser.getGroup3().equals(a_model.getGroup_name()) || a_currentuser.getGroup4().equals(a_model.getGroup_name()))
                {
                    //https://stackoverflow.com/questions/6925156/how-to-avoid-a-toast-if-theres-one-toast-already-being-shown
                    if(m_toast!=null){
                       m_toast.cancel();
                    }
                    m_toast=Toast.makeText(m_context,"Group already present in your list",Toast.LENGTH_SHORT);
                    m_toast.show();

                }
                else{
                    m_userreference.child(m_currentuserid).child(a_groupid).setValue(a_model.getGroup_name());
                }
            }
        });

    }

    /**/
    /*
     *   NAME
     *       public myviewholder onCreateViewHolder
     *
     *   SYNOPSIS
     *       public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
     *          ViewGroup parent--->parent viewgroup that contains other children
     *          int viewType--> View type of the parent group.
     *
     *   DESCRIPTION
     *     onCreateViewHolder creates a new child single_group_row inside the parent View group.
     *
     *   RETURNS
     *       myviewholder class object.
     *
     *   AUTHOR
     *       Bishal Thapa
     *
     *   DATE
     *       4/27/2021
     *
     *   Help taken from : https://www.youtube.com/watch?v=sZ8D1-hNeWo for syntax.
     *
     */
    /**/
    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup a_parent, int a_viewType) {
        View view= LayoutInflater.from(a_parent.getContext()).inflate(R.layout.singlegroupoptionsrow,a_parent,false);
        return new myviewholder(view);
    }

    /**/
    /*
     *   NAME
     *       class myviewholder
     *
     *   SYNOPSIS
     *       class myviewholder
     *          no parameters.
     *
     *   DESCRIPTION
     *      myviewholder class gets the Button and textview's id for each adapter and
     *      saves the reference.
     *
     *   RETURNS
     *      Nothing
     *
     *   AUTHOR
     *      Bishal Thapa
     *
     *   DATE
     *      4/27/2021
     *
     */
    /**/
    class myviewholder extends RecyclerView.ViewHolder{
        TextView text;
        Button  button1,button2,button3,button4;
        public myviewholder(@NonNull View itemView) {
            super(itemView);
            text=(TextView) itemView.findViewById(R.id.groupid);
            button1=(Button) itemView.findViewById(R.id.group1button);
            button2=(Button) itemView.findViewById(R.id.group2button);
            button3=(Button) itemView.findViewById(R.id.group3button);
            button4=(Button) itemView.findViewById(R.id.group4button);
        }
    }
}
