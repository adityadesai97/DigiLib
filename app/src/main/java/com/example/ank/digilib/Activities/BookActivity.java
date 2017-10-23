package com.example.ank.digilib.Activities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ank.digilib.Objects.ChosenBook;
import com.example.ank.digilib.Objects.FeedEvent;
import com.example.ank.digilib.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BookActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userDatabaseReference;
    private DatabaseReference bookDatabaseReference;
    private DatabaseReference chosenBookDatabaseReference;

    private Button buyButton;
    private Button rentButton;
    private Button downloadButton;

    private String genreName;
    private String bookName;
    private String fileName;
    private String bookBuyPrice;
    private String bookRentPrice;
    private String bookKey;
    private int bookFoundFlag = 0;
    private String downloadLink;

    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_actionbar, null);

        actionBarLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(actionBarLayout);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        Intent i = getIntent();
        genreName = i.getStringExtra("genreName");
        bookName = i.getStringExtra("bookName");
        fileName = i.getStringExtra("fileName");

        setTitle(bookName);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDatabaseReference = firebaseDatabase.getReference().child("users");
        bookDatabaseReference = firebaseDatabase.getReference().child("books").child(genreName);
        chosenBookDatabaseReference = firebaseDatabase.getReference().child("chosenBooks");

        buyButton = (Button)findViewById(R.id.buy_button);
        rentButton = (Button)findViewById(R.id.rent_button);
        downloadButton = (Button)findViewById(R.id.download_button);

        bookDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(final DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.child("name").getValue().toString().equals(bookName)) {
                        chosenBookDatabaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot1: dataSnapshot.getChildren()) {
                                    String bookId = (String)snapshot1.child("key").getValue();
                                    if(snapshot.getKey().equals(bookId)) {
                                        bookFoundFlag = 1;
                                        if(snapshot.hasChild("download_url")) {
                                            downloadLink = snapshot.child("download_url").getValue().toString();
                                            enableDownloadButton();
                                        }
                                        break;
                                    }
                                }
                                if(bookFoundFlag == 0) {
                                    bookBuyPrice = snapshot.child("salePrice").getValue().toString();
                                    bookRentPrice = snapshot.child("rentalPrice").getValue().toString();
                                    bookKey = snapshot.getKey();
                                    if(snapshot.hasChild("download_url")) {
                                        enablePurchaseButtons(bookBuyPrice, bookRentPrice);
                                    }
                                    else {
                                        Toast.makeText(BookActivity.this, "This book is currently not available for download", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void enablePurchaseButtons(String buy, String rent) {
        TextView buy_price = (TextView)findViewById(R.id.buy_price);
        TextView rent_price = (TextView)findViewById(R.id.rent_price);
        buy_price.setText("Rs " + buy);
        rent_price.setText("Rs " + rent);
        buyButton.setEnabled(true);
        rentButton.setEnabled(true);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtractCredits("buy");
            }
        });

        rentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subtractCredits("rent");
            }
        });
    }

    void enableDownloadButton() {
        downloadButton.setEnabled(true);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (ContextCompat.checkSelfPermission(BookActivity.this,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(BookActivity.this,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//
//                    } else {
//                        ActivityCompat.requestPermissions(BookActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
//                    }
//                }
//                else {
//                    downloadFromLink(downloadLink, bookName);
//                }

//                Uri intentUri = Uri.parse(downloadLink);
//
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_VIEW);
//                intent.setData(intentUri);
//                startActivity(intent);

                downloadFile(fileName);
            }
        });
    }

    private void downloadFile(final String fileName) {


        StorageReference islandRef = FirebaseStorage.getInstance().getReference().child(fileName);

        File localFile = new File(Environment.getExternalStorageDirectory() + "/Download", fileName);

        final File finalLocalFile = localFile;
        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Log.v("tag1", finalLocalFile.getAbsolutePath());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.v("tag1", exception.getCause().toString());

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    downloadFromLink(downloadLink, bookName);

                } else {

                }
                return;
            }
        }
    }

    void downloadFromLink(String downloadURL, String name) {
        String url = downloadURL;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "name-of-the-file.ext");
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    void subtractCredits(final String mode) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String formattedDate = df.format(c.getTime());
        userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if(snapshot.child("uid").getValue().toString().equals(user.getUid())) {
                        String key = snapshot.getKey();
                        String uid = (String) snapshot.child("uid").getValue();
                        String name = (String)snapshot.child("name").getValue();
                        String profilePictureURL = (String)snapshot.child("profile_url").getValue();
                        int credits = Integer.parseInt((String) snapshot.child("credits").getValue());
                        if(mode.equals("buy")) {
                            if(credits >= Integer.parseInt(bookBuyPrice)) {
                                Map<String,Object> taskMap = new HashMap<String,Object>();
                                int newCredits = credits - Integer.parseInt(bookBuyPrice);
                                taskMap.put("credits", Integer.toString(newCredits));
                                userDatabaseReference.child(key).updateChildren(taskMap);
                                userDatabaseReference.child(key).child("activity").push().setValue(new FeedEvent(genreName, name, profilePictureURL, bookKey, mode, formattedDate));
                                chosenBookDatabaseReference.child(uid).push().setValue(new ChosenBook(bookKey, genreName, "buy", formattedDate, fileName));
                                Toast.makeText(BookActivity.this, "Thank you!", Toast.LENGTH_SHORT).show();
                                buyButton.setEnabled(false);
                                rentButton.setEnabled(false);

                            }
                            else {
                                Toast.makeText(BookActivity.this, "You do not have enough credits to buy this", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if(mode.equals("rent")) {
                            if(credits >= Integer.parseInt(bookRentPrice)) {
                                Map<String,Object> taskMap = new HashMap<String,Object>();
                                int newCredits = credits - Integer.parseInt(bookRentPrice);
                                taskMap.put("credits", Integer.toString(newCredits));
                                userDatabaseReference.child(key).updateChildren(taskMap);
                                userDatabaseReference.child(key).child("activity").push().setValue(new FeedEvent(genreName, name, profilePictureURL, bookKey, mode, formattedDate));
                                chosenBookDatabaseReference.child(uid).push().setValue(new ChosenBook(bookKey, genreName, "rent", formattedDate, fileName));
                                Toast.makeText(BookActivity.this, "Thank you!", Toast.LENGTH_SHORT).show();
                                buyButton.setEnabled(false);
                                rentButton.setEnabled(false);
                            }
                            else {
                                Toast.makeText(BookActivity.this, "You do not have enough credits to rent this", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
