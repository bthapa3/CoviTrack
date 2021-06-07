package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends ToolbarActivity {

    TextView m_editname, m_editphone , m_showname, m_showphone, m_showemail, m_showcovidstatus;
    ImageView m_profilepicture;
    private FirebaseStorage m_storage;
    private StorageReference m_storageRef;
    // r_id represents the user ID value stored in the database. It helps to uniquely identify a person.
    String r_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference user_reference = FirebaseDatabase.getInstance().getReference().child("Users");
    Users user;
    private Uri file;
    //StorageReference profileref = m_storageRef.child("profilepic/" +r_id);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Profile");
        m_editname= (TextView) findViewById(R.id.editfullname);
        m_editphone=(TextView) findViewById(R.id.editphone);
        m_showname=(TextView) findViewById(R.id.name);
        m_showemail=(TextView) findViewById(R.id.email);
        m_showphone=(TextView) findViewById(R.id.phone);
        m_showcovidstatus=(TextView) findViewById(R.id.covidpos);
        m_profilepicture=(ImageView) findViewById(R.id.imageView);


        m_storage=FirebaseStorage.getInstance();
        m_storageRef= m_storage.getReference();
        //populating user info on create


        StorageReference pictureref = m_storageRef.child("profilepic/"+ r_id);

        /*final long ImageSize = 2048 * 2048;

        try{
            pictureref.getBytes(ImageSize).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    m_profilepicture.setImageBitmap(bitmap);
                    // Data for "images/island.jpg" is returns, use this as needed
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // toast error status probably file size too big
                    // Handle any errors
                }
            });
        }
        catch (Exception e){
            System.out.println("Image doesnot exist"+ e);
        }*/

        pictureref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Glide.with(ProfileActivity.this)
                        .load(uri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_background)
                        .circleCrop()
                        .into(m_profilepicture);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        user_reference.child(r_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Datasnapshot) {

                //getting the user's information from the database that needs to modified and storing it as Users class.
                 user = Datasnapshot.getValue(Users.class);
                 m_showname.setText(user.getFull_name());
                 m_showemail.setText(user.getEmail());
                 m_showphone.setText(user.getPhone_number());
                 m_showcovidstatus.setText(user.getInfected().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                //In order to display database errors
                Toast.makeText(ProfileActivity.this, "Error with the database", Toast.LENGTH_SHORT).show();
            }
        });


        m_editname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_editname.getText().equals("Edit")) {
                    m_showname.setInputType(InputType.TYPE_CLASS_TEXT);
                    m_showname.requestFocus();
                    m_editname.setText("Save");

                }
                else{
                    m_editname.setText("Edit");
                    m_showname.setInputType(InputType.TYPE_NULL);
                    user.setFull_name(m_showname.getText().toString());
                    user_reference.child(r_id).setValue(user);
                    System.out.println(user.getFull_name());
                }
            }
        });
        m_editphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(m_editphone.getText().equals("Edit")) {
                    m_showphone.setInputType(InputType.TYPE_CLASS_NUMBER);
                    m_showphone.requestFocus();
                    m_editphone.setText("Save");

                }
                else{
                    m_editphone.setText("Edit");
                    m_showname.setInputType(InputType.TYPE_NULL);
                    user.setPhone_number(m_showphone.getText().toString());
                    user_reference.child(r_id).setValue(user);

                }
            }


        });

        m_profilepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Getprofilepic();
            }
        });


    }


    private void Getprofilepic(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null &&data.getData()!=null){
            file=data.getData();
            m_profilepicture.setImageURI(file);
            UploadResults();

        }
    }

    private void UploadResults() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading File>> ");
        pd.show();


        StorageReference pictureref = m_storageRef.child("profilepic/"+ r_id);
        pictureref.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content),"Image uploaded",Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(),"Failed to Update",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progessPercent=(100.00 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                        pd.setMessage("Progress: " + (int) progessPercent+ "%");
                    }

                });


        // Create a reference to 'images/mountains.jpg'
        //StorageReference mountainImagesRef = storageRef.child("images/mountains.jpg");

        // While the file names are the same, the references point to different files
        //mountainsRef.getName().equals(mountainImagesRef.getName());    // true
        //mountainsRef.getPath().equals(mountainImagesRef.getPath())
    }



}