package com.example.ank.digilib.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ank.digilib.Objects.Book;
import com.example.ank.digilib.R;

import java.util.ArrayList;

/**
 * Created by adityadesai on 12/07/17.
 */

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.MenuHolder>{

    private static ArrayList<Book> mBooks;

    public static class MenuHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView nameTextView;
        private TextView authorTextView;

        public MenuHolder(View v) {
            super(v);

            nameTextView = (TextView) v.findViewById(R.id.book_name);
            authorTextView = (TextView) v.findViewById(R.id.book_author);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }

        public void bindIndustry(String bookName, String bookAuthor) {
            nameTextView.setText(bookName);
            authorTextView.setText("- "+bookAuthor);
        }
    }

    public BooksAdapter(ArrayList<Book> books, Context context) {
        mBooks = books;
    }

    @Override
    public BooksAdapter.MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item, parent, false);
        return new MenuHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(BooksAdapter.MenuHolder holder, int position) {
        String bookName = mBooks.get(position).getName();
        String bookAuthor = mBooks.get(position).getAuthor();
        holder.bindIndustry(bookName, bookAuthor);
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }
}