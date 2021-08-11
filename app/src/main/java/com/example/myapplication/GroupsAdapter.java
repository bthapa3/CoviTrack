package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**/
/*
 *  CLASS DESCRIPTION:
 *    Adapter for the GroupsManagerActivity recycler viw which display admin user groups
 *      and a button to delete any groups.
 *
 *  PURPOSE:
 *    Groups Adapter class creates an individual adapter for recycler view with a group
 *      name and a delete button.Each button has on-click listener that helps to delete
 *      the group from the database.
 *
 *  AUTHOR:
 *      Bishal Thapa
 *
 *  DATE
 *       4/27/2021
 *
 *  Help taken from : https://www.youtube.com/watch?v=sZ8D1-hNeWo for syntax.
 */
/**/
public class  GroupsAdapter extends FirebaseRecyclerAdapter<UserGroups,GroupsAdapter.myviewholder>
{
    private DatabaseReference m_groupreference = FirebaseDatabase.getInstance().getReference().child("Groups");
    //Constructor for the GroupsAdapter Class
    public GroupsAdapter(@NonNull FirebaseRecyclerOptions<UserGroups> options) {
        super(options);
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
     *      sets up an onclick listener to each of them.
     *
     *   RETURNS
     *      Nothing
     *
     *   AUTHOR
     *      Bishal Thapa
     *
     *   DATE
     *      07/25/2021
     *
     */
    /**/

    @Override
    protected void onBindViewHolder(@NonNull myviewholder a_holder, int a_position, @NonNull UserGroups a_model) {

        a_holder.m_text.setText(a_model.getGroup_name());
        a_holder.m_deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View a_view) {
             m_groupreference.child(a_model.getGroup_name().toString()).setValue(null);
            }
        });

    }

    /**/
    /*
     *   NAME
     *       public myviewholder onCreateViewHolder
     *
     *   SYNOPSIS
     *       public myviewholder onCreateViewHolder(@NonNull ViewGroup a_parent, int a_viewType)
     *          ViewGroup a_parent--->parent viewgroup that contains other children
     *          int a_viewType--> View type of the parent group.
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
     */
    /**/
    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup a_parent, int a_viewType) {
        View view= LayoutInflater.from(a_parent.getContext()).inflate(R.layout.singlegrouprow,a_parent,false);
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
     *    myviewholder class gets the Button and textview's id for each adapter and
     *      saves the reference.
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
    class myviewholder extends RecyclerView.ViewHolder{

        TextView m_text;
        Button m_deletebutton;
        public myviewholder(@NonNull View a_itemView) {
            super(a_itemView);
            m_text=(TextView) itemView.findViewById(R.id.groupid);
            m_deletebutton=(Button) itemView.findViewById(R.id.buttonid);

        }
    }

}
