package com.example.ank.digilib.Activities;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ank.digilib.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.key;

public class BuyCreditsActivity extends AppCompatActivity {

    private EditText numberEditText;
    private EditText cvvEditText;
    private EditText topUpEditText;
    private Button submitButton;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_credits);

        ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_actionbar, null);

        actionBarLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(actionBarLayout);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        numberEditText = (EditText)findViewById(R.id.number_edit_text);
        cvvEditText = (EditText)findViewById(R.id.cvv_edit_text);
        topUpEditText = (EditText)findViewById(R.id.topup_edit_text);
        submitButton = (Button)findViewById(R.id.submit_button);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users");
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        if(pref.getBoolean("first_time", true)) {
            final SharedPreferences.Editor editor = pref.edit();
            numberEditText.setVisibility(View.VISIBLE);
            cvvEditText.setVisibility(View.VISIBLE);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.putInt("number", Integer.parseInt(numberEditText.getText().toString()));
                    editor.putInt("cvv", Integer.parseInt(cvvEditText.getText().toString()));
                    editor.commit();
                    numberEditText.setVisibility(View.GONE);
                    cvvEditText.setVisibility(View.GONE);
                    topUpEditText.setVisibility(View.VISIBLE);
                    submitButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                        if(snapshot.child("uid").getValue().equals(user.getUid())) {
                                            int credits = Integer.parseInt((String) snapshot.child("credits").getValue());
                                            int newCredits = credits + Integer.parseInt(topUpEditText.getText().toString());
                                            Map<String,Object> taskMap = new HashMap<String,Object>();
                                            taskMap.put("credits", Integer.toString(newCredits));
                                            databaseReference.child(snapshot.getKey()).updateChildren(taskMap);
                                            topUpEditText.setText("");
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            });
            editor.putBoolean("first_time", false);
            editor.commit();
        }
        else {
            topUpEditText.setVisibility(View.VISIBLE);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                if(snapshot.child("uid").getValue().equals(user.getUid())) {
                                    int credits = Integer.parseInt((String) snapshot.child("credits").getValue());
                                    int newCredits = credits + Integer.parseInt(topUpEditText.getText().toString());
                                    Map<String,Object> taskMap = new HashMap<String,Object>();
                                    taskMap.put("credits", Integer.toString(newCredits));
                                    databaseReference.child(snapshot.getKey()).updateChildren(taskMap);
                                    topUpEditText.setText("");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }
    }
}
