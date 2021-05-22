package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
    public UserInfoAdapter(@NonNull FirebaseRecyclerOptions<Users> options,OnNoteListener onNoteListener) {
        super(options);
        this.m_OnNoteListener=onNoteListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull Users model) {

        FirebaseStorage m_storage = FirebaseStorage.getInstance();
        StorageReference m_storageRef = m_storage.getReference();
        String picture_id=  model.getUserID();
        StorageReference resultseref = m_storageRef.child("testresult/" +picture_id);
        StorageReference insuranceref = m_storageRef.child("insurance/" +picture_id);

        final long ONE_MEGABYTE = 1024 * 1024;
        resultseref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                // Data for "images/island.jpg" is returns, use this as needed
                holder.insurance.setImageBitmap(bitmap);
                holder.insurancestat.setText("Test Results : Found");
                holder.userid.setText(model.getUserID());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
              //  holder.insurance.setImageBitmap(bitmap);
                holder.insurancestat.setText("Test Results: Not Found");
            }
        });



        holder.name.setText("Name: " + model.getFull_name());
        holder.phone.setText("Phone: "+model.getPhone_number());



    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singleuserrow,parent,false);
        return new myviewholder(view,m_OnNoteListener);

    }

    class myviewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name,phone,insurancestat,userid;
        ImageView insurance;
        OnNoteListener onNoteListener;
        public myviewholder(@NonNull View itemView,OnNoteListener onNoteListener) {
            super(itemView);
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
        public void onClick(View v) {
            TextView id= (TextView) v.findViewById(R.id.storeid);
            String ids=id.getText().toString();
            onNoteListener.onNoteClick(getAdapterPosition(),ids);
        }
    }

    public interface OnNoteListener{
        void onNoteClick(int position,String ids);
    }
}
