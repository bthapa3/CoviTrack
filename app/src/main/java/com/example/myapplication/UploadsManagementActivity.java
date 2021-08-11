package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

/**/
/*
 *  CLASS DESCRIPTION:
 *    Helps the admin user view and manage the files uploaded by the employees inside small business.
 *
 *  PURPOSE:
 *     The main purpose of this page is to display the insurance of covid-19 results uploaded by
 *      the employees along with their information so that admin can review files easily.
 *      It also allows the administration to download the files if needed.
 *
 *  AUTHOR:
 *      Bishal Thapa
 *
 *  DATE
 *       4/27/2021
 */
/**/
public class UploadsManagementActivity extends ToolbarActivity implements UserInfoAdapter.OnNoteListener,View.OnClickListener {

    private FirebaseStorage m_storage;
    private StorageReference m_storageRef;
    private ImageView m_exit,m_userpic,m_download,m_blankview;
    private RecyclerView m_recview;
    private UserInfoAdapter m_adapter;
    //Either stores type (insurance or covid results)
    private String m_inventorytype;

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
     *      On-create method stores the reference for the toolbar and loads
     *      Recycler view which contains the users profile information and the document
     *      the administration is currently viewing.
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
        setContentView(R.layout.activity_uploads_management);

        //Bundle value sent from the previous activity.
        Bundle bundle = getIntent().getExtras();
        m_inventorytype = ""; // or other values
        if(bundle != null){
            //This helps to display the right types of files the admin is looking for
            //File types are insurance or Covid-19 results.
            m_inventorytype = bundle.getString("inventoryitem");

        }
        else{
            System.out.println("No bundle value found");
        }

        //Storing the reference for the Views from the XML file.
        m_exit= (ImageView) findViewById(R.id.exit);
        m_blankview=(ImageView) findViewById(R.id.backgroundfill);
        m_userpic= (ImageView) findViewById(R.id.userspic);
        m_download=(ImageView) findViewById(R.id.downloadButton);
        m_exit.setVisibility(View.INVISIBLE);
        m_blankview.setVisibility(View.INVISIBLE);
        m_userpic.setVisibility(View.INVISIBLE);
        m_download.setVisibility(View.INVISIBLE);

        // //Image buttons for navigating through the 5 main activities.
        ImageButton assessbutton=findViewById(R.id.assessButton);
        ImageButton groupbutton=findViewById(R.id.groupButton);
        ImageButton resourcebutton=findViewById(R.id.resourcesButton);
        ImageButton uploadbutton=findViewById(R.id.uploadButton);
        ImageButton homebutton=findViewById(R.id.homeButton);

        //on click listener that helps to determine the next activity that the user wants
        // to navigate to.
        assessbutton.setOnClickListener(this);
        groupbutton.setOnClickListener(this);
        resourcebutton.setOnClickListener(this);
        uploadbutton.setOnClickListener(this);
        homebutton.setOnClickListener(this);

        //setting up the recycler view.
        m_recview=(RecyclerView) findViewById(R.id.myrecview);
        m_recview.setLayoutManager(new LinearLayoutManager(this));
        try {
            FirebaseRecyclerOptions<Users> options =
                    new FirebaseRecyclerOptions.Builder<Users>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Users"), Users.class)
                            .build();

            m_adapter = new UserInfoAdapter(options, this, this, m_inventorytype);
            m_recview.setAdapter(m_adapter);
        }
        catch (Exception e){
            System.out.println("Exception occured: "+e);
        }

    }

    /**/
    /*
     *   NAME
     *          protected void onStart
     *
     *   SYNOPSIS
     *         protected void onStart()
     *         no parameters
     *
     *   DESCRIPTION
     *         This function helps the adapter to start binding or populating the adapters
     *         with the values from the database.
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
    protected void onStart() {
        try {
            super.onStart();
            m_adapter.startListening();
        }catch (Exception e){
            System.out.println("Error inside onstart");
        }
    }

    /**/
    /*
     *   NAME
     *          protected void onStop
     *
     *   SYNOPSIS
     *         protected void onStop()
     *         no parameters
     *
     *   DESCRIPTION
     *         This function helps the adapter to stop binding or populating the adapters
     *         with the values from the database.
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
    protected void onStop() {
        try{
        super.onStop();
        m_adapter.stopListening();
        }
        catch (Exception e){
            System.out.println("error inside stop"+e);
        }
    }

    /**/
    /*
     *   NAME
     *          public void DownloadImage
     *
     *   SYNOPSIS
     *        public void DownloadImage(String a_inventorytype, String a_id)
     *              String a_inventorytype ---> The type of file to be downloaded from database.
     *              String a_id ---> unique id related to the file to be downloaded.
     *
     *   DESCRIPTION
     *         This function initiates the downloading of the image from the databasee.
     *         It uses picture_id reference and gets the file from the firebase database.
     *          It then calls DownloadManager to save file to the device.
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
    public void DownloadImage(String a_inventorytype, String a_id){

        String picture_id=  FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

        m_storage=FirebaseStorage.getInstance();
        m_storageRef= m_storage.getReference();

        // Create a reference with an initial file path and name
        StorageReference resultseref = m_storageRef.child(a_inventorytype+ "/" +a_id);

        resultseref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url=uri.toString();
                DownloadManager(UploadsManagementActivity.this,a_inventorytype+ picture_id,".jpg",DIRECTORY_DOWNLOADS,url);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Error with the Download URL");
            }
        });


        }
    /**/
    /*
     *   NAME
     *         public void DownloadManager
     *
     *   SYNOPSIS
     *         public void DownloadManager(Context a_context, String a_fileName, String a_fileExtension, String a_destinationDirectory, String a_url)
     *         Context a_context ----> context from which the function is called
     *         String a_filename ----> name of the file to be saved
     *         String a_fileExtension ----> extension of the file to be saved.
     *         String a_destinationDirectory ----> Directory of the image to be saved.
     *         String a_url ----> image uri changed to string form
     *
     *   DESCRIPTION
     *        This function uses the parameters passed like filename, fileExtension, String url
     *        to create a file to save with appropriate name, format and directory.
     *        It then downloads the file at the requested directory and notifies when completed.
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
    public void DownloadManager(Context a_context, String a_fileName, String a_fileExtension, String a_destinationDirectory, String a_url){
        DownloadManager downloadManager=(DownloadManager) a_context.getSystemService(a_context.DOWNLOAD_SERVICE);
        Uri uri=Uri.parse(a_url);
        DownloadManager.Request request=new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(a_context,a_destinationDirectory,a_fileName+a_fileExtension);
        downloadManager.enqueue(request);
    }

    /**/
    /*
     *   NAME
     *      public void onBackPressed
     *
     *   SYNOPSIS
     *      public void onBackPressed()
     *      no parameters.
     *
     *   DESCRIPTION
     *     This function takes the user to homepage after immediate back button press.
     *      It helps the application to prevent the user from exiting the app with
     *      single back-button press.
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
    public void onBackPressed()
    {
        //this makes sure image view is closed on backpress instead of exiting the activity for single back press.
        if(m_exit.getVisibility()==View.VISIBLE){
            m_exit.setVisibility(View.INVISIBLE);
            m_userpic.setVisibility(View.INVISIBLE);
            m_download.setVisibility(View.INVISIBLE);
            m_blankview.setVisibility(View.INVISIBLE);
            return;
        }
        Intent homeIntent = new Intent(UploadsManagementActivity.this, UploadActivity.class);
        startActivity(homeIntent);
        finish();
    }

    /**/
    /*
     *   NAME
     *      public void onClick
     *
     *   SYNOPSIS
     *      public void onClick(View a_view)
     *      a_view   --> view object passes the reference to the Image button which triggered the
     *                  on-click method.
     *
     *   DESCRIPTION
     *     This function allows the user to navigate through four different activities of the application.
     *      It takes View v as an input parameter and captures the ID of the button pressed to
     *      start the new activity.
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
    public void onClick(View a_view) {

        switch(a_view.getId()){

            case R.id.assessButton:
                startActivity(new Intent(getApplicationContext(), AssesmentActivity.class));
                finish();
                break;

            case R.id.homeButton:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;

            case R.id.groupButton:
                startActivity(new Intent(getApplicationContext(), GroupsActivity.class));
                finish();
                break;

            case R.id.uploadButton:
                startActivity(new Intent(getApplicationContext(), UploadActivity.class));
                finish();
                break;

            case R.id.resourcesButton:
                startActivity(new Intent(getApplicationContext(), ResourcesActivity.class));
                finish();
                break;

        }
    }

    /**/
    /*
     *   NAME
     *      public void HideImageview
     *
     *   SYNOPSIS
     *      public void HideImageview(View a_view)
     *      View a_view  --> view object passes the reference to the exit,download button from the XML file.
     *
     *   DESCRIPTION
     *     This function hides the image_view and buttons from the screen when the user is not
     *      viewing a specific image from the recycler view.
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

    public void HideImageview(View a_view){

        m_exit.setVisibility(a_view.INVISIBLE);
        m_userpic.setVisibility(a_view.INVISIBLE);
        m_download.setVisibility(a_view.INVISIBLE);
        m_blankview.setVisibility(a_view.INVISIBLE);
    }


    /**/
    /*
     *   NAME
     *      public void onNoteClick
     *
     *   SYNOPSIS
     *      public void onNoteClick(int position,String ids,String inventorytype) {
     *      int position  ---> position of the adapter in the recycler view.
     *      String ids ---> user id of the user clicked inside recycler view.
     *      String inventorytype ---> type of the file the user is currently viewing(between insurace and test results)
     *
     *   DESCRIPTION
     *     This function uses the user id of the specific adapter and then displays the photo
     *      uploaded by the user utilizing the id.It also sets up the visibility of the photo
     *      viewer window when function is called.
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
    public void onNoteClick(int a_position,String a_id,String a_inventorytype) {

        //checking if the user is not present as further steps cannot be procedded without userID.
        if(a_id.equals("null")){
            return;
        }

        FirebaseStorage m_storage = FirebaseStorage.getInstance();
        StorageReference m_storageRef = m_storage.getReference();
        StorageReference fileref = m_storageRef.child(a_inventorytype+ "/" +a_id);



        fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'

                Glide.with(UploadsManagementActivity.this)
                        .load(uri)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(m_userpic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                System.out.println("Error"+exception);
            }
        });
        //Toast.makeText(UploadsManagementActivity.this,"this runs",Toast.LENGTH_LONG).show();
        m_exit.setVisibility(View.VISIBLE);
        m_userpic.setVisibility(View.VISIBLE);
        m_download.setVisibility(View.VISIBLE);
        m_blankview.setVisibility(View.VISIBLE);

        //downloading image if the user clicks download button using on-click listener.
        m_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadImage(a_inventorytype, a_id);
            }
        });

    }
}