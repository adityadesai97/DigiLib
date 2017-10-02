package com.example.ank.digilib.Adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

/**
 * Created by adityadesai on 01/10/17.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.MenuHolder> {

    private static ArrayList<User> mFriends;

    public static class MenuHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView nameTextView;
        private ImageView profilePicture;

        FirebaseDatabase firebaseDatabase;
        FirebaseAuth firebaseAuth;
        DatabaseReference databaseReference;
        FirebaseUser user;

        public MenuHolder(View v) {
            super(v);

            nameTextView = (TextView) v.findViewById(R.id.name_text_view);
            profilePicture = (ImageView) v.findViewById(R.id.profile_picture);

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
            Log.v("tag1", name);
            nameTextView.setText(name);
            Glide.with(profilePicture.getContext()).load(profilePictureURL).into(profilePicture);
        }
    }

    public FriendsAdapter(ArrayList<User> friends) {
        mFriends = friends;
    }

    @Override
    public FriendsAdapter.MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_item, parent, false);
        return new MenuHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(FriendsAdapter.MenuHolder holder, int position) {
        String name = mFriends.get(position).getName();
        String profilePictureURL = mFriends.get(position).getProfile_url();
        String uid = mFriends.get(position).getUid();
        String email = mFriends.get(position).getMail_id();
        holder.bindIndustry(name, profilePictureURL, uid, email);
    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }
}
