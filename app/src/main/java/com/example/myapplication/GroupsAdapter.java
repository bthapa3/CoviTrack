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

public class  GroupsAdapter extends FirebaseRecyclerAdapter<UserGroups,GroupsAdapter.myviewholder>
{   private Context m_context;
    private DatabaseReference m_groupreference = FirebaseDatabase.getInstance().getReference().child("Groups");
    public GroupsAdapter(@NonNull FirebaseRecyclerOptions<UserGroups> options) {
        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull UserGroups model) {

        holder.m_text.setText(model.getGroup_name());
        holder.m_deletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             m_groupreference.child(model.getGroup_name().toString()).setValue(null);
            }
        });

    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.singlegrouprow,parent,false);
        return new myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder{

        TextView m_text;
        Button m_deletebutton;
        public myviewholder(@NonNull View itemView) {
            super(itemView);
            m_text=(TextView) itemView.findViewById(R.id.groupid);
            m_deletebutton=(Button) itemView.findViewById(R.id.buttonid);

        }
    }

}
