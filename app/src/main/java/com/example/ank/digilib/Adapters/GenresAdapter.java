package com.example.ank.digilib.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ank.digilib.Objects.Book;
import com.example.ank.digilib.Objects.Genre;
import com.example.ank.digilib.R;

import java.util.ArrayList;

/**
 * Created by adityadesai on 01/08/17.
 */

public class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.MenuHolder>{

    private static ArrayList<Genre> mGenres;

    public static class MenuHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView nameTextView;
        private TextView authorTextView;

        public MenuHolder(View v) {
            super(v);

            nameTextView = (TextView) v.findViewById(R.id.genre_name);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }

        public void bindIndustry(String name) {
            nameTextView.setText(name);
        }
    }

    public GenresAdapter(ArrayList<Genre> genre) {
        mGenres = genre;
    }

    @Override
    public GenresAdapter.MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.genre_item, parent, false);
        return new MenuHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(GenresAdapter.MenuHolder holder, int position) {
        String name = mGenres.get(position).getName();
        holder.bindIndustry(name);
    }

    @Override
    public int getItemCount() {
        return mGenres.size();
    }
}

