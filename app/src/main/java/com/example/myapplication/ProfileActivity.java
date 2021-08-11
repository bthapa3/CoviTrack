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

/**/
/*
 *  CLASS DESCRIPTION:
 *      User Information in Profile View
 *
 *  PURPOSE:
 *     The main purpose of the ProfileActivity is to allow the users to view their basic information like
 *     name, address, contact number. It also allows them to save their profile picture. Users can edit and
 *     change their contact number, address, and profile picture as needed using this activity.
 *
 *  AUTHOR:
 *      Bishal Thapa
 *
 *  DATE
 *       4/27/2021
 */
/**/

public class ProfileActivity extends ToolbarActivity {

    private TextView m_editname, m_editphone , m_showname, m_showphone, m_showemail, m_showcovidstatus;
    private ImageView m_profilepicture;
    private FirebaseStorage m_storage;
    private StorageReference m_storageRef;
    // m_id represents the user ID value stored in the database. It helps to uniquely identify a person.
    private String m_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference m_userreference = FirebaseDatabase.getInstance().getReference().child("Users");
    private Users m_user;
    private Uri m_file;

    /**/
    /*
     *   NAME
     *      protected void onCreate
     *
     *   SYNOPSIS
     *      protected void onCreate(Bundle a_savedInstanceState)
     *      Bundle a_savedInstanceState---->reference to a Bundle object
     *
     *   DESCRIPTION
     *      Oncreate method stores the reference for the toolbar,Textviews, Imageviews
     *      Imagebuttons and buttons. Inside the oncreate method profile image of the user
     *      is downloaded from the Database and populated on the screen. User information like,
     *      name, address, contact are also downloaded from the database and populated to the
     *      appropriate fields. After that, on-click listeners are setup inside on-create
     *      methods to save the changes if the user modifies any value from the profile.
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
    protected void onCreate(Bundle a_savedInstanceState) {
        super.onCreate(a_savedInstanceState);
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

        //Database reference to get the profile picture of the current user.
        StorageReference pictureref = m_storageRef.child("profilepic/"+ m_id);

        //getting picture of the user from the database using the URL.
        pictureref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //Glide library is used to get the picture using URL
                //Glide makes it better to load as it saves the results in cache
                //and helps for smooth transition during reloads
                //Also Circle crop can be applied for profile pictures.
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
                //Incase the profile picture is not present.
                Toast.makeText(ProfileActivity.this,"Please upload the profile picture!!!",Toast.LENGTH_SHORT).show();
            }
        });

        //Getting the user class from the database
        //User class has users name,email, phone and covidstatus saved.
        m_userreference.child(m_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Datasnapshot) {

                //getting the user's information from the database that needs to modified and storing it as Users class.
                 m_user = Datasnapshot.getValue(Users.class);
                 m_showname.setText(m_user.getFull_name());
                 m_showemail.setText(m_user.getEmail());
                 m_showphone.setText(m_user.getPhone_number());
                 m_showcovidstatus.setText(m_user.getInfected().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                //In order to display database errors
                Toast.makeText(ProfileActivity.this, "Error with the database", Toast.LENGTH_SHORT).show();
            }
        });

        //If the user edits his name on-click listener will be activated.
        m_editname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //If the user is trying to edit
                //making the field editable with focus
                if(m_editname.getText().equals("Edit")) {
                    m_showname.setInputType(InputType.TYPE_CLASS_TEXT);
                    m_showname.requestFocus();
                    m_editname.setText("Save");

                }
                //If the user saved after changing name
                //making the text field non-editable
                else{
                    m_editname.setText("Edit");
                    m_showname.setInputType(InputType.TYPE_NULL);
                    m_user.setFull_name(m_showname.getText().toString());
                    m_userreference.child(m_id).setValue(m_user);
                }
            }
        });

        //If the user clicks on the edit phone number button
        m_editphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If the user tries to change the number
                if(m_editphone.getText().equals("Edit")) {
                    m_showphone.setInputType(InputType.TYPE_CLASS_NUMBER);
                    m_showphone.requestFocus();
                    m_editphone.setText("Save");

                }
                //if the user tries to save the number.
                else{
                    m_editphone.setText("Edit");
                    m_showname.setInputType(InputType.TYPE_NULL);
                    m_user.setPhone_number(m_showphone.getText().toString());
                    m_userreference.child(m_id).setValue(m_user);

                }
            }


        });

        //If the user clicks on the current profile picture he can upload a new profile picture.
        m_profilepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Getprofilepic();
            }
        });


    }

    /**/
    /*
     *   NAME
     *      public void Getprofilepic
     *
     *   SYNOPSIS
     *      public void Getprofilepic()
     *      no parameters.
     *
     *   DESCRIPTION
     *     This function allows the user to select a new image from the user device by using get content.
     *     The image that user selects will be than uploaded as new profile picture.
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

    private void Getprofilepic(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    /**/
    /*
     *   NAME
     *      protected void onActivityResult
     *
     *   SYNOPSIS
     *      protected void onActivityResult(int a_requestCode, int a_resultCode, @Nullable Intent a_data) {
     *          int a_requestCode   --> The request code of the get content window
     *          int a_resultCode    --> result code stores the result of the activity based on the success of the Action_get_content.
     *          Intent a_data       --> data has the profile picture that the user selected from his device.
     *
     *   DESCRIPTION
     *    This function onActivityResult helps to store the image selected by the user to the file
     *      by checking if the result of Action_get_content was successful and data is not null.
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
    protected void onActivityResult(int a_requestCode, int a_resultCode, @Nullable Intent a_data) {
        super.onActivityResult(a_requestCode, a_resultCode, a_data);
        if(a_requestCode==1 && a_resultCode==RESULT_OK && a_data!=null && a_data.getData()!=null){
            m_file=a_data.getData();
            m_profilepicture.setImageURI(m_file);
            UploadPicture();

        }
    }

    /**/
    /*
     *   NAME
     *      public void UploadPicture
     *
     *   SYNOPSIS
     *      public void UploadPicture()
     *      no parameters
     *
     *   DESCRIPTION
     *      This function takes the image file that the user selected and uploads it to the database.
     *      It also shows the progress percentage to the user so that if the file is too big, user will
     *      be able to view the progress, file upload task has made.
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
    private void UploadPicture() {

        ProgressDialog progressdialog = new ProgressDialog(this);
        progressdialog.setTitle("Uploading File>> ");
        progressdialog.show();

        StorageReference pictureref = m_storageRef.child("profilepic/"+ m_id);
        //It uses the file from OnActivityResults and uploads that using pictureref reference.
        pictureref.putFile(m_file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressdialog.dismiss();
                        Snackbar.make(findViewById(android.R.id.content),"Image uploaded",Snackbar.LENGTH_LONG).show();
                        //restarting to apply changes on image preview.
                        finish();
                        startActivity(getIntent());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressdialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Failed to Update",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progessPercent=(100.00 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                        progressdialog.setMessage("Progress: " + (int) progessPercent+ "%");
                    }

                });
    }
}