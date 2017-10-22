package com.example.ank.digilib.Activities;

import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ank.digilib.Adapters.BooksAdapter;
import com.example.ank.digilib.Adapters.RequestsAdapter;
import com.example.ank.digilib.Objects.Book;
import com.example.ank.digilib.Objects.User;
import com.example.ank.digilib.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private TextView isEmptyMarker;
    LinearLayoutManager linearLayoutManager;
    RequestsAdapter adapter;

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseUser user;
    ValueEventListener valueEventListener;

    ArrayList<User> requestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_actionbar, null);

        actionBarLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(actionBarLayout);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        isEmptyMarker = (TextView) findViewById(R.id.is_empty_marker);

        requestList = new ArrayList<>();

        recyclerView = (RecyclerView)findViewById(R.id.request_list);
        linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users");
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        RequestsActivity.FetchRequestsList fRL = new RequestsActivity.FetchRequestsList();
        fRL.execute();
    }

    public class FetchRequestsList extends AsyncTask<Void,Void,ArrayList<User>> {

        @Override
        protected ArrayList<User> doInBackground(Void... params) {

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    requestList.clear();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        if(snapshot.child("uid").getValue().equals(user.getUid())) {
                            databaseReference.child(snapshot.getKey()).child("requests").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                                        String name = (String) snapshot1.child("name").getValue();
                                        String uid = (String) snapshot1.child("uid").getValue();
                                        String email = (String) snapshot1.child("mail_id").getValue();
                                        String profilepictureURL = (String) snapshot1.child("profile_url").getValue();
                                        if(name != null) {
                                            requestList.add(new User(name, uid, email, profilepictureURL, null));
                                        }
                                    }
                                    updateUI();
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
            };

            databaseReference.addValueEventListener(valueEventListener);
            return null;
        }
    }

    public void updateUI() {
        if(requestList.isEmpty()) {
            isEmptyMarker.setText("You don't have any pending requests!");
        }
        else {
            isEmptyMarker.setText("");
        }
        adapter = new RequestsAdapter(requestList);
        recyclerView.setAdapter(adapter);
    }
}
