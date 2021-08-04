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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

/**/
/*
 *  CLASS DESCRIPTION:
 *    Helps user upload the necessary files and admins view the files
 *
 *  PURPOSE:
 *     The main purpose of this activity is to allow the employees or the user to
 *      upload digital insurance or the covid test results in case the small business
 *      they are working for needs to verify it. It also gives a link for the admin
 *      to view and verify the documents employees have submitted.
 *
 *  AUTHOR:
 *      Bishal Thapa
 *
 *  DATE
 *       4/27/2021
 */
/**/

public class UploadActivity extends ToolbarActivity implements View.OnClickListener {

    private FirebaseStorage m_storage;
    private StorageReference m_storageRef;
    public ImageView m_resultsview, m_insuranceview;
    private Button m_submitresult , m_submitinsurance ;
    private Uri m_file;
    private String m_pictureid=  FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

/**/
/*
 *   NAME
 *      protected void onCreate
 *
 *   SYNOPSIS
 *      protected void onCreate(Bundle savedInstanceState)
 *      Bundle savedInstanceState---->reference to a Bundle object
 *
 *   DESCRIPTION
 *      On-create method stores the reference for the toolbar and loads
 *      the latest saved instances of insurance and covid-19 results as bitmap.
 *      It also sets up the on-click listeners to allow the user to update the
 *      test results and insurance records.
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
        setContentView(R.layout.activity_upload);

        //setting up the toolbar title
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Uploads and Inventory");

        //storing the reference for views and database
        m_submitresult=findViewById(R.id.submitresult);
        m_resultsview=findViewById(R.id.resultsview);
        m_submitinsurance=findViewById(R.id.submitinsurance);
        m_insuranceview=findViewById(R.id.insuranceview);
        m_storage=FirebaseStorage.getInstance();
        m_storageRef= m_storage.getReference();

        //Image buttons for navigating through the 5 main activities.
        ImageButton assessbutton=findViewById(R.id.assessButton);
        ImageButton homebutton=findViewById(R.id.homeButton);
        ImageButton resourcebutton=findViewById(R.id.resourcesButton);
        ImageButton groupsbutton=findViewById(R.id.groupButton);

        //on click listener that helps to determine the next activity that the user wants
        // to navigate to.
        assessbutton.setOnClickListener(this);
        homebutton.setOnClickListener(this);
        resourcebutton.setOnClickListener(this);
        groupsbutton.setOnClickListener(this);

        // Create a reference with an initial file path and name
        StorageReference resultseref = m_storageRef.child("testresult/" +m_pictureid);
        StorageReference insuranceref = m_storageRef.child("insurance/" +m_pictureid);

        //getting the covid results image and displaying as bitmap to image view.
        try{
            resultseref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    m_submitresult.setText("UPDATE COVID RESULTS");
                    Glide.with(UploadActivity.this)
                            .load(uri)
                            .centerCrop()
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(m_resultsview);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    System.out.println("Sorry! Unable to read");
                }
            });
        }
        catch (Exception e){
            System.out.println("Results does not exist"+ e);
        }

        //getting the insurance image and displaying as bitmap to image view.
        try{
            insuranceref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    m_submitinsurance.setText("UPDATE DIGITAL INSURANCE");
                    Glide.with(UploadActivity.this)
                            .load(uri)
                            .centerCrop()
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(m_insuranceview);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    System.out.println("Sorry! Unable to read");
                }
            });
        }
        catch (Exception e){
            System.out.println("Insurance pic doesnot exist"+ e);
        }

        //onclick listener to choose the new test result
        m_submitresult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               GetResults();
            }
        });

        //on-click listener to choose new insurace record.
        m_submitinsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetInsurance();
            }
        });


    }

/**/
/*
 *   NAME
 *      public void onBackPressed
 *
 *   SYNOPSIS
 *      public void onBackPressed()
 *          no parameters.
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
        Intent homeIntent = new Intent(UploadActivity.this, MainActivity.class);
        startActivity(homeIntent);
        finish();
    }

/**/
/*
 *   NAME
 *      private void GetResults
 *
 *   SYNOPSIS
 *     private void GetResults()
 *          no parameters
 *
 *   DESCRIPTION
 *      GetResults() method allows to get the image content from the file-explorer or the gallery of
 *      the user device. The file is then processed using onActivityResult
 *
 *    RETURNS
 *      Nothing
 *
 *   AUTHOR
 *       Bishal Thapa
 *
 *   DATE
 *       4/27/2021
 *
 */
/**/


    private void GetResults(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

/**/
/*
 *   NAME
 *      private void GetInsurance
 *
 *   SYNOPSIS
 *     private void GetInsurance()
 *     no parameters
 *
 *   DESCRIPTION
 *      GetInsurance() method allows to get the image content from the file-explorer or the gallery of
 *      the user device. The file is then processed using onActivityResult
 *
 *    RETURNS
 *      Nothing
 *
 *   AUTHOR
 *       Bishal Thapa
 *
 *   DATE
 *       4/27/2021
 *
 */
/**/
    private void GetInsurance(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,2);
    }

/**/
/*
 *   NAME
 *      protected void onActivityResult
 *
 *   SYNOPSIS
 *     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
 *     int requestCode  ---> ID for the request processed.
 *      int resultCode ----> result ID for the request processed.
 *      Intent Data ---> Data passed from the Intent
 *
 *   DESCRIPTION
 *      This method allows to choose the image and the data in then passed to the m_file URI.
 *      Then depending on the type of file submitted, UploadResults function is called
 *      to store the m_file to the database.
 *
 *    RETURNS
 *      Nothing
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
        //if the image data is available and the request code is 1 (file is a test result)
        if(a_requestCode==1 && a_resultCode==RESULT_OK && a_data!=null && a_data.getData()!=null){
            m_file=a_data.getData();
            m_resultsview.setImageURI(m_file);
            UploadResults("testresult/");

        }
        //if the image data is available and the request code is 2 (file is a insurance file)
        else if(a_requestCode==2 && a_resultCode==RESULT_OK && a_data!=null && a_data.getData()!=null){
            m_file=a_data.getData();
            m_insuranceview.setImageURI(m_file);
            UploadResults("insurance/");

        }
        //file could not be accessed or some other problem.
        else{
            System.out.println("Error Getting data from the system.");
        }
    }

/**/
/*
 *   NAME
 *      private void UploadResults
 *
 *   SYNOPSIS
 *     private void UploadResults(String a_type)
 *      String a_type ---> type of the file to be uploaded(insurance or test results)
 *
 *   DESCRIPTION
 *      This method helps to upload the image that the user selected to the database.
 *      It also shows progress bar which will be useful if the file size is too large.
 *
 *    RETURNS
 *      Nothing
 *
 *   AUTHOR
 *       Bishal Thapa
 *
 *   DATE
 *       4/27/2021
 *
 */
/**/
    private void UploadResults(String a_type) {
        ProgressDialog progresdialog = new ProgressDialog(this);
        progresdialog.setTitle("Uploading File>> ");
        progresdialog.show();

        //reference to the location in database where the image is to be saved
        //type contains whether it is to be stored in insurance folder or test result folder
        //m_pictureid helps to uniquely identify the user's info while retrieving.
        StorageReference pictureref = m_storageRef.child(a_type+m_pictureid);
        pictureref.putFile(m_file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progresdialog.dismiss();
                        Snackbar.make(findViewById(android.R.id.content),"Image uploaded",Snackbar.LENGTH_LONG).show();
                        if(a_type.equals("testresult/")){  m_submitresult.setText("UPDATE COVID RESULTS");}
                        else{m_submitinsurance.setText("UPDATE DIGITAL INSURANCE");}
                        //reload so that change is reflected in the Imageview bitmap.
                        finish();
                        startActivity(getIntent());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progresdialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Failed to Update",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progessPercent=(100.00 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                        progresdialog.setMessage("Progress: " + (int) progessPercent+ "%");
                    }

                });
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

            case R.id.resourcesButton:
                startActivity(new Intent(getApplicationContext(), ResourcesActivity.class));
                finish();
                break;

            case R.id.groupButton:
                startActivity(new Intent(getApplicationContext(),GroupsActivity.class));
                finish();
                break;
        }
    }

/**/
/*
 *   NAME
 *       public void GotoResultsInventory
 *
 *   SYNOPSIS
 *       public void GotoResultsInventory(View a_view)
 *          a_view   --> view object passes the reference to any Views present in XML code.
 *
 *   DESCRIPTION
 *     This function allows the admin user to access the inventory of the covid results.
 *      It passes a bundle so the new-activity can decide the type of acitvity to be showed.
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

    public void GotoResultsInventory(View a_view){
        Intent intent = new Intent(UploadActivity.this, UploadsManagementActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("inventoryitem", "testresult"); //id and bundle value
        intent.putExtras(bundle); //Putting the id for next Intent
        startActivity(intent);
        finish();

    }

/**/
/*
 *   NAME
 *        public void GotoInsuranceInventory
 *
 *   SYNOPSIS
 *       public void GotoInsuranceInventory(View view)
 *      view   --> view object passes the reference to any Views present in XML code.
 *
 *   DESCRIPTION
 *     This function allows the admin user to access the inventory of the covid results.
 *      It passes a bundle so the new-activity can decide the type of acitvity to be showed.
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

    public void GotoInsuranceInventory(View a_view){
        Intent intent = new Intent(UploadActivity.this, UploadsManagementActivity.class);
        Bundle bundle= new Bundle();
        bundle.putString("inventoryitem", "insurance"); //id and bundle value
        intent.putExtras(bundle); //adding the bundle value
        startActivity(intent);
        finish();

    }
}