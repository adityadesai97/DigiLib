package com.example.ank.digilib.Activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.ank.digilib.Adapters.ChatAdapter;
import com.example.ank.digilib.Objects.Chat;
import com.example.ank.digilib.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {


    private String friendUid;
    private String myUid;
    private String myName;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private ListView mMessageListView;
    private ChatAdapter mChatAdapter;
    private EditText mMessageEditText;
    private Button mSendButton;

    private ArrayList<Chat> chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_actionbar, null);

        actionBarLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(actionBarLayout);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        Intent i = getIntent();
        friendUid = i.getStringExtra("frienduid");
        myUid = i.getStringExtra("myuid");
        myName = i.getStringExtra("myname");

        mMessageListView = (ListView) findViewById(R.id.messageListView);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);

        chatList = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("chats");

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            if((snapshot.child("uid1").getValue().equals(myUid) && snapshot.child("uid2").getValue().equals(friendUid)) || (snapshot.child("uid1").getValue().equals(friendUid) && snapshot.child("uid2").getValue().equals(myUid))) {
                                databaseReference.child(snapshot.getKey()).child("messages").push().setValue(new Chat(mMessageEditText.getText().toString(), myName));
                                mMessageEditText.setText("");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        ChatActivity.FetchMessages fM = new ChatActivity.FetchMessages();
        fM.execute();
    }

    public void updateUI() {
        mChatAdapter = new ChatAdapter(this, R.layout.chat_item, chatList);
        mMessageListView.setAdapter(mChatAdapter);
    }

    public class FetchMessages extends AsyncTask<Void,Void,ArrayList<Chat>> {

        @Override
        protected ArrayList<Chat> doInBackground(Void... params) {

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    chatList.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if((snapshot.child("uid1").getValue().equals(myUid) && snapshot.child("uid2").getValue().equals(friendUid)) || (snapshot.child("uid1").getValue().equals(friendUid) && snapshot.child("uid2").getValue().equals(myUid))) {
                            databaseReference.child(snapshot.getKey()).child("messages").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot snapshot1: dataSnapshot.getChildren()) {
                                        String text = (String) snapshot1.child("text").getValue();
                                        String name = (String) snapshot1.child("name").getValue();
                                        if(text != null) {
                                            chatList.add(new Chat(text, name));
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
            });

            return null;
        }
    }
}
