package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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

public class UploadsManagementActivity extends AppCompatActivity implements UserInfoAdapter.OnNoteListener{

    private FirebaseStorage m_storage;
    private StorageReference m_storageRef;
    private RadioButton Individual,Everyone;
    private RadioGroup RadioGroup;
    private EditText EmailHolder;
    private ImageView m_exit,m_userpic,m_download;
    OutputStream outputStream;
    RecyclerView m_recview;
    UserInfoAdapter m_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_uploads_management);
        m_exit= (ImageView) findViewById(R.id.exit);
        m_userpic= (ImageView) findViewById(R.id.userspic);
        m_download=(ImageView) findViewById(R.id.downloadButton);
        m_exit.setVisibility(View.INVISIBLE);
        m_userpic.setVisibility(View.INVISIBLE);
        m_download.setVisibility(View.INVISIBLE);

        m_recview=(RecyclerView) findViewById(R.id.myrecview);
        m_recview.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Users"), Users.class)
                        .build();

        m_adapter=new UserInfoAdapter(options,this);
        m_recview.setAdapter(m_adapter);


        m_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadImage();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        m_adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        m_adapter.stopListening();
    }

    public void DownloadImage(){

        String picture_id=  FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        //File localFile = File.createTempFile("images", "jpg");
        File storagePath = new File(Environment.getExternalStorageDirectory(), "directory_name");
        //islandRef = storageRef.child("images/island.jpg");
        m_storage=FirebaseStorage.getInstance();
        m_storageRef= m_storage.getReference();

        // Create a reference with an initial file path and name

        StorageReference resultseref = m_storageRef.child("testresult/" +picture_id);
        resultseref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url=uri.toString();
                DownloadManager(UploadsManagementActivity.this,"insurance"+ picture_id,".jpg",DIRECTORY_DOWNLOADS,url);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Error with the Download URL");
            }
        });


    }

    public void DownloadManager(Context context, String fileName, String fileExtension, String destinationDirectory, String url){
        DownloadManager downloadManager=(DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        Uri uri=Uri.parse(url);
        DownloadManager.Request request=new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,destinationDirectory,fileName+fileExtension);
        downloadManager.enqueue(request);
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
    public void GotoUpload(View view){
        startActivity(new Intent(getApplicationContext(), UploadActivity.class));
        finish();
    }
    public void GotoResources(View view){
        startActivity(new Intent(getApplicationContext(), ResourcesActivity.class));
        finish();
    }

    public void HideImageview(View view){
        m_exit.setVisibility(View.INVISIBLE);
        m_userpic.setVisibility(View.INVISIBLE);
        m_download.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onNoteClick(int position,String ids) {

        System.out.println(ids);

        if(ids.equals("null")){
            return;
        }
        m_exit.setVisibility(View.VISIBLE);
        FirebaseStorage m_storage = FirebaseStorage.getInstance();
        StorageReference m_storageRef = m_storage.getReference();
        StorageReference resultseref = m_storageRef.child("testresult/" +ids);

        final long Imagesize = 2048 * 2048;
        resultseref.getBytes(Imagesize).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                // Data for "images/island.jpg" is returns, use this as needed
                m_userpic.setImageBitmap(bitmap);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //  holder.insurance.setImageBitmap(bitmap);

            }
        });

        m_userpic.setVisibility(View.VISIBLE);
        m_download.setVisibility(View.VISIBLE);
        // System.out.println(Users.get(position).getUser);
       // Intent intent=new Intent(this, MainActivity.class);
        //startActivity(intent);
    }
}