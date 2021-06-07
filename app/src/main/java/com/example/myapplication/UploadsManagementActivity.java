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

public class UploadsManagementActivity extends ToolbarActivity implements UserInfoAdapter.OnNoteListener {

    private FirebaseStorage m_storage;
    private StorageReference m_storageRef;
    private RadioButton Individual,Everyone;
    private RadioGroup RadioGroup;
    private EditText EmailHolder;
    private ImageView m_exit,m_userpic,m_download,m_blankview;
    OutputStream outputStream;
    RecyclerView m_recview;
    UserInfoAdapter m_adapter;
    String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        Bundle b = getIntent().getExtras();
        value = ""; // or other values
        if(b != null){

            value = b.getString("inventoryitem");
            System.out.println("value is"+ value);
            Toast.makeText(UploadsManagementActivity.this, value, Toast.LENGTH_SHORT).show();
        }
        else{
            System.out.println("null");
        }



        //toolbar.setTitle("Management");


        setContentView(R.layout.activity_uploads_management);
        m_exit= (ImageView) findViewById(R.id.exit);
        m_blankview=(ImageView) findViewById(R.id.backgroundfill);
        m_userpic= (ImageView) findViewById(R.id.userspic);
        m_download=(ImageView) findViewById(R.id.downloadButton);
        m_exit.setVisibility(View.INVISIBLE);
        m_blankview.setVisibility(View.INVISIBLE);
        m_userpic.setVisibility(View.INVISIBLE);
        m_download.setVisibility(View.INVISIBLE);

        m_recview=(RecyclerView) findViewById(R.id.myrecview);
        m_recview.setLayoutManager(new LinearLayoutManager(this));
        try {
            FirebaseRecyclerOptions<Users> options =
                    new FirebaseRecyclerOptions.Builder<Users>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Users"), Users.class)
                            .build();

            m_adapter = new UserInfoAdapter(options, this, this, value);
            m_recview.setAdapter(m_adapter);
        }
        catch (Exception e){
            System.out.println("error happens here"+e);
        }

        m_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadImage();
            }
        });

    }


    @Override
    protected void onStart() {
        try {
            super.onStart();
            m_adapter.startListening();
        }catch (Exception e){
            System.out.println("Error inside onstart");
        }
    }

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


    @Override
    public void onBackPressed()
    {
        Intent homeIntent = new Intent(UploadsManagementActivity.this, UploadActivity.class);
        startActivity(homeIntent);
        finish();
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
        m_blankview.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onNoteClick(int position,String ids,String inventorytype) {

        System.out.println(ids);

        if(ids.equals("null")){
            return;
        }
        FirebaseStorage m_storage = FirebaseStorage.getInstance();
        StorageReference m_storageRef = m_storage.getReference();
        StorageReference resultseref = m_storageRef.child(inventorytype+ "/" +ids);



        resultseref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
            }
        });
        m_exit.setVisibility(View.VISIBLE);
        m_userpic.setVisibility(View.VISIBLE);
        m_download.setVisibility(View.VISIBLE);
        m_blankview.setVisibility(View.VISIBLE);
        // System.out.println(Users.get(position).getUser);
       // Intent intent=new Intent(this, MainActivity.class);
        //startActivity(intent);
    }


}