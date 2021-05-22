package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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

public class UploadActivity extends AppCompatActivity {

    private FirebaseStorage m_storage;
    private StorageReference m_storageRef;
    public ImageView m_resultsview, m_insuranceview;
    private Button m_submitresult , m_submitinsurance ;
    private Uri file;
    String picture_id=  FirebaseAuth.getInstance().getCurrentUser().getUid().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        m_submitresult=findViewById(R.id.submitresult);
        m_resultsview=findViewById(R.id.resultsview);
        m_submitinsurance=findViewById(R.id.submitinsurance);
        m_insuranceview=findViewById(R.id.insuranceview);
        m_storage=FirebaseStorage.getInstance();
        m_storageRef= m_storage.getReference();

        // Create a reference with an initial file path and name

        StorageReference resultseref = m_storageRef.child("testresult/" +picture_id);
        StorageReference insuranceref = m_storageRef.child("insurance/" +picture_id);

        final long ImageSize = 2048 * 2048;
        resultseref.getBytes(ImageSize).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                m_resultsview.setImageBitmap(bitmap);
                m_submitresult.setText("UPDATE COVID RESULTS");
                // Data for "images/island.jpg" is returns, use this as needed
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // toast error status probably file size too big
                // Handle any errors
            }
        });

        insuranceref.getBytes(ImageSize).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                m_insuranceview.setImageBitmap(bitmap);
                m_submitinsurance.setText("UPDATE DIGITAL INSURANCE");
                // Data for "images/island.jpg" is returns, use this as needed
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // toast error status probably file size too big
                // Handle any errors
            }
        });



        m_submitresult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               GetResults();
            }
        });

        m_submitinsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetInsurance();
            }
        });


    }



    private void GetResults(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
    private void GetInsurance(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null &&data.getData()!=null){
            file=data.getData();
            m_resultsview.setImageURI(file);
            UploadResults("testresult/");

        }
        else if(requestCode==2 && resultCode==RESULT_OK && data!=null &&data.getData()!=null){
            file=data.getData();
            m_insuranceview.setImageURI(file);
            UploadResults("insurance/");

        }
    }

    private void UploadResults(String type) {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading File>> ");
        pd.show();

        // Create a reference to "mountains.jpg"

        StorageReference pictureref = m_storageRef.child(type+picture_id);
        pictureref.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content),"Image uploaded",Snackbar.LENGTH_LONG).show();
                        if(type.equals("testresult/")){  m_submitresult.setText("UPDATE COVID RESULTS");}
                        else{m_submitinsurance.setText("UPDATE DIGITAL INSURANCE");}
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

    public void GotoAsses(View view){
        startActivity(new Intent(getApplicationContext(), AssesmentActivity.class));
        finish();
    }

    public void GotoGroup(View view){
        startActivity(new Intent(getApplicationContext(), GroupsActivity.class));
        finish();
    }

    public void GotoHome(View view){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
    public void GotoResources(View view){
        startActivity(new Intent(getApplicationContext(), ResourcesActivity.class));
        finish();
    }
    public void ManageSubmissions(View view){
        startActivity(new Intent(getApplicationContext(), UploadsManagementActivity.class));
        finish();
    }


}