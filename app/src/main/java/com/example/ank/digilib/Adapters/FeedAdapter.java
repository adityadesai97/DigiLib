package com.example.ank.digilib.Adapters;

import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ank.digilib.Activities.BookActivity;
import com.example.ank.digilib.Objects.Book;
import com.example.ank.digilib.Objects.FeedEvent;
import com.example.ank.digilib.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by adityadesai on 02/10/17.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MenuHolder> {

    private static ArrayList<FeedEvent> mFeedEvents;

    public static class MenuHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView nameTextView;
        private ImageView profilePicture;
        private TextView bookNameTextView;
        private TextView authorTextView;
        private ImageView coverImageView;

        private FeedEvent mFeedEvent;

        private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        private DatabaseReference databaseReference = firebaseDatabase.getReference().child("books");


        public MenuHolder(View v) {
            super(v);

            nameTextView = (TextView) v.findViewById(R.id.name_text_view);
            profilePicture = (ImageView) v.findViewById(R.id.profile_picture);
            bookNameTextView = (TextView) v.findViewById(R.id.book_name);
            authorTextView = (TextView) v.findViewById(R.id.book_author);
            coverImageView = (ImageView) v.findViewById(R.id.book_cover);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }

        public void bindIndustry(String name, String profilePictureURL, String bookName, String bookAuthor, String coverImageURL) {
            nameTextView.setText(name);
            Glide.with(profilePicture.getContext()).load(profilePictureURL).into(profilePicture);
            bookNameTextView.setText(bookName);
            authorTextView.setText("- " + bookAuthor);
            Glide.with(coverImageView.getContext()).load(coverImageURL).into(coverImageView);
            coverImageView.setImageAlpha(175);
        }
    }

    public FeedAdapter(ArrayList<FeedEvent> feedEvents) {
        mFeedEvents = feedEvents;
    }

    @Override
    public FeedAdapter.MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_item, parent, false);
        return new MenuHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(final FeedAdapter.MenuHolder holder, int position) {
        final String name = mFeedEvents.get(position).getName();
        final String profilePictureURL = mFeedEvents.get(position).getProfile_id();
        String genreName = mFeedEvents.get(position).getGenreName();
        final String bookId = mFeedEvents.get(position).getBookId();
        FirebaseDatabase.getInstance().getReference().child("books").child(genreName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if(snapshot.getKey().equals(bookId)) {
                        String bookName = (String)snapshot.child("name").getValue();
                        String bookAuthor = (String)snapshot.child("author").getValue();
                        String coverImageURL = (String)snapshot.child("coverImageURL").getValue();
                        holder.bindIndustry(name, profilePictureURL, bookName, bookAuthor, coverImageURL);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mFeedEvents.size();
    }

}
