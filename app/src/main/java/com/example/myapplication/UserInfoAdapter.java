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

/**/
/*
 *  CLASS DESCRIPTION:
 *      Adapter for the UploadsManagementActivity's recycler viw which displays users
 *      uploaded files to the admin.
 *
 *  PURPOSE:
 *      UserInfoAdapter class creates an individual adapter for recycler view with user
 *      profile picture, basic information like name and contact and then also displays the
 *      file uploaded if there is any. It also has on-click listener setup for each
 *      instance of the adapter so that uploaded files can be viewed or downloaded
 *      easily.
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

    /**/
    /*
     *   NAME
     *      protected void onBindViewHolder
     *
     *   SYNOPSIS
     *      protected void onBindViewHolder(@NonNull myviewholder a_holder, int a_position, @NonNull UserGroups a_model)
     *      myviewholder a_holder---> myviewholder object that contains reference to individual adapters.
     *      int a_position---> position of the individual adapter in the array of adapters.
     *      User a_model--> User object related to each adapter.
     *
     *   DESCRIPTION
     *      onBindViewHolder Binds the User object values like name, contact and
     *      and images to specific adapters and also sets up an onclick listener to each of them.
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
    protected void onBindViewHolder(@NonNull myviewholder a_holder, int a_position, @NonNull Users a_model) {

        FirebaseStorage m_storage = FirebaseStorage.getInstance();
        StorageReference m_storageRef = m_storage.getReference();
        String picture_id=  a_model.getUserID();
        //m_inventory variable helps to find the type of the inventory which will be shown in the database
        //we have insurance or test results inventory
        StorageReference a_fileseref = m_storageRef.child(m_inventorytype+ "/" +picture_id);
        StorageReference a_profilepicref = m_storageRef.child("profilepic/" +picture_id);

        //binding Username , contact and file to each adapter.
        a_fileseref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                a_holder.insurancestat.setText(m_inventorytype + " : Uploaded");
                a_holder.userid.setText(a_model.getUserID());
                Glide.with(m_context)
                        .load(uri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(a_holder.insurance);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                a_holder.insurancestat.setText(m_inventorytype + " : Not Uploaded");
            }
        });

        //binding profile pic of the user to adapter.
        a_profilepicref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Glide.with(m_context)
                        .load(uri)
                        .centerCrop()
                        .circleCrop()
                        .placeholder(R.drawable.personz)
                        .into(a_holder.userpic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
              //user picture is not found
            }
        });


        //binding name and phone
        a_holder.name.setText("Name: " + a_model.getFull_name());
        a_holder.phone.setText("Phone: "+a_model.getPhone_number().toString());

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
     *     onCreateViewHolder creates a new child singleuserrow inside the parent View group.
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

        View view = LayoutInflater.from(a_parent.getContext()).inflate(R.layout.singleuserrow,a_parent,false);
        return new myviewholder(view,m_OnNoteListener);

    }

    /**/
    /*
     *   NAME
     *       class myviewholder
     *
     *   SYNOPSIS
     *       class myviewholder extends RecyclerView.ViewHolder implements View.OnClickListener
     *          no parameters.
     *
     *   DESCRIPTION
     *      myviewholder class gets the Imageview's and  textview's id for each adapter and
     *      saves the reference. It also sets up OnNoteListner to setup on-click for
     *      each adapter.
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
            itemView.setOnClickListener(this);
            this.onNoteListener=onNoteListener;



        }

        @Override
        public void onClick(View a_view) {

            TextView id= (TextView) a_view.findViewById(R.id.storeid);
            String ids=id.getText().toString();
            onNoteListener.onNoteClick(getAdapterPosition(),ids,m_inventorytype);

        }


    }

    /**/
    /*
     *   NAME
     *       public interface OnNoteListener
     *
     *   SYNOPSIS
     *       public interface OnNoteListener
     *          no parameters.
     *
     *   DESCRIPTION
     *      This function helps to call OnNoteClick function located inside UploadsManagementActivity
     *      class. It also passes Position of adapter in array of adapters, id of the user file and
     *      filetype which helps UploadsManagementActivity to find file from database.
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
    public interface OnNoteListener{
        void onNoteClick(int position,String ids,String m_inventorytype);
    }
}
