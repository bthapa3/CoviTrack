package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserInfoAdapter extends FirebaseRecyclerAdapter<Users,UserInfoAdapter.myviewholder> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    private OnNoteListener m_OnNoteListener;
    private Context m_context;
    private String m_inventorytype;
    public UserInfoAdapter(@NonNull FirebaseRecyclerOptions<Users> options, OnNoteListener onNoteListener, Context a_context,String a_inventorytype) {
        super(options);
        this.m_OnNoteListener=onNoteListener;
        this.m_context = a_context;
        this.m_inventorytype=a_inventorytype;
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull Users model) {

        FirebaseStorage m_storage = FirebaseStorage.getInstance();
        StorageReference m_storageRef = m_storage.getReference();
        String picture_id=  model.getUserID();
        //m_inventory variable helps to find the type of the inventory which will be shown in the database
        //we have insurance or test results inventory
        StorageReference resultseref = m_storageRef.child(m_inventorytype+ "/" +picture_id);
        StorageReference profilepicref = m_storageRef.child("profilepic/" +picture_id);

        resultseref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                holder.insurancestat.setText("Test Results : Found");
                holder.userid.setText(model.getUserID());
                Glide.with(m_context)
                        .load(uri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(holder.insurance);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                holder.insurancestat.setText("Test Results: Not Found");
            }
        });

        profilepicref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Glide.with(m_context)
                        .load(uri)
                        .centerCrop()
                        .circleCrop()
                        .placeholder(R.drawable.personz)
                        .into(holder.userpic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
              //user picture is not found
            }
        });


        holder.name.setText("Name: " + model.getFull_name());
        holder.phone.setText("Phone: "+model.getPhone_number().toString());

        holder.insurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(m_context, holder.name.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleuserrow,parent,false);
        return new myviewholder(view,m_OnNoteListener);

    }

    class myviewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name,phone,insurancestat,userid;
        ImageView insurance,userpic;
        OnNoteListener onNoteListener;
        public myviewholder(@NonNull View itemView,OnNoteListener onNoteListener) {
            super(itemView);

            userpic=(ImageView) itemView.findViewById(R.id.userspic);
            name=(TextView) itemView.findViewById(R.id.Name);
            insurancestat=(TextView) itemView.findViewById(R.id.Insurance);
            phone=(TextView) itemView.findViewById(R.id.Phone);
            insurance=(ImageView) itemView.findViewById(R.id.insuranceview);
            userid=(TextView) itemView.findViewById(R.id.storeid);
            userid.setVisibility(View.INVISIBLE);
            insurance.setOnClickListener(this);
            this.onNoteListener=onNoteListener;



        }

        @Override
        public void onClick(View v) {

            TextView id= (TextView) v.findViewById(R.id.storeid);
            String ids=id.getText().toString();
            onNoteListener.onNoteClick(getAdapterPosition(),ids,m_inventorytype);

        }


    }

    public interface OnNoteListener{
        void onNoteClick(int position,String ids,String m_inventorytype);
    }
}
