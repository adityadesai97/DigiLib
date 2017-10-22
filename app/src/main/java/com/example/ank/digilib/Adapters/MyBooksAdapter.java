package com.example.ank.digilib.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ank.digilib.Activities.BookActivity;
import com.example.ank.digilib.Activities.ViewBookActivity;
import com.example.ank.digilib.Objects.Book;
import com.example.ank.digilib.R;

import java.util.ArrayList;

/**
 * Created by adityadesai on 22/10/17.
 */

public class MyBooksAdapter extends RecyclerView.Adapter<MyBooksAdapter.MenuHolder> {

    private static ArrayList<Book> mBooks;

    public static class MenuHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView nameTextView;
        private TextView authorTextView;
        private ImageView coverImageView;

        private Book mBook;
        private String bookName;
        private String genreName;
        private String downloadUrl;
        private String bookId;

        public MenuHolder(View v) {
            super(v);

            nameTextView = (TextView) v.findViewById(R.id.book_name);
            authorTextView = (TextView) v.findViewById(R.id.book_author);
            coverImageView = (ImageView) v.findViewById(R.id.book_cover);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mBook = mBooks.get(position);

            if(mBook!=null){
                bookName = mBook.getName();
                genreName = mBook.getGenreName();
                downloadUrl = mBook.getDownload_url();
            }

            Intent i=new Intent(v.getContext(), ViewBookActivity.class);
            i.putExtra("bookName", bookName);
            i.putExtra("genreName", genreName);
            i.putExtra("download_url", downloadUrl);
            v.getContext().startActivity(i);
        }

        public void bindIndustry(String bookName, String bookAuthor, String bookCoverURL) {
            nameTextView.setText(bookName);
            authorTextView.setText("- "+bookAuthor);
            Glide.with(coverImageView.getContext()).load(bookCoverURL).into(coverImageView);
            coverImageView.setImageAlpha(175);
        }
    }

    public MyBooksAdapter(ArrayList<Book> books) {
        mBooks = books;
    }

    @Override
    public MyBooksAdapter.MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item, parent, false);
        return new MenuHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(MyBooksAdapter.MenuHolder holder, int position) {
        String bookName = mBooks.get(position).getName();
        String bookAuthor = mBooks.get(position).getAuthor();
        String bookCoverURL = mBooks.get(position).getCoverImageURL();
        holder.bindIndustry(bookName, bookAuthor, bookCoverURL);
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }
}
