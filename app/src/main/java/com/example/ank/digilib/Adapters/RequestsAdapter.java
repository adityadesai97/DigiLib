package com.example.ank.digilib.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ank.digilib.Activities.BookActivity;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.data;

/**
 * Created by adityadesai on 01/10/17.
 */

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.MenuHolder> {

    private static ArrayList<User> mRequests;

    public static class MenuHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView nameTextView;
        private ImageView profilePicture;
        private Button acceptButton;
        private Button rejectButton;

        FirebaseDatabase firebaseDatabase;
        FirebaseAuth firebaseAuth;
        DatabaseReference databaseReference;
        FirebaseUser user;

        public MenuHolder(View v) {
            super(v);

            nameTextView = (TextView) v.findViewById(R.id.name_text_view);
            profilePicture = (ImageView) v.findViewById(R.id.profile_picture);
            acceptButton = (Button) v.findViewById(R.id.accept_button);
            rejectButton = (Button) v.findViewById(R.id.reject_button);

            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference().child("users");
            firebaseAuth = FirebaseAuth.getInstance();
            user = firebaseAuth.getCurrentUser();
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }

        public void bindIndustry(final String name, final String profilePictureURL, final String uid, final String email) {
            nameTextView.setText(name);
            Glide.with(profilePicture.getContext()).load(profilePictureURL).into(profilePicture);
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(final DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                if(snapshot.child("uid").getValue().equals(user.getUid())) {
                                    databaseReference.child(snapshot.getKey()).child("friends").push().setValue(new User(name, uid, email, profilePictureURL, null));
                                    databaseReference.child(snapshot.getKey()).child("requests").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for(DataSnapshot snapshot1: dataSnapshot.getChildren()) {
                                                if(snapshot1.child("uid").getValue().equals(uid)) {
                                                    databaseReference.child(snapshot.getKey()).child("requests").child(snapshot1.getKey()).removeValue();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                                if(snapshot.child("uid").getValue().equals(uid)) {
                                    databaseReference.child(snapshot.getKey()).child("friends").push().setValue(new User(user.getDisplayName(), user.getUid(), user.getEmail(), user.getPhotoUrl().toString(), null));
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

    public RequestsAdapter(ArrayList<User> requests) {
        mRequests = requests;
    }

    @Override
    public RequestsAdapter.MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_item, parent, false);
        return new MenuHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(RequestsAdapter.MenuHolder holder, int position) {
        String name = mRequests.get(position).getName();
        String profilePictureURL = mRequests.get(position).getProfile_url();
        String uid = mRequests.get(position).getUid();
        String email = mRequests.get(position).getMail_id();
        holder.bindIndustry(name, profilePictureURL, uid, email);
    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }
}
