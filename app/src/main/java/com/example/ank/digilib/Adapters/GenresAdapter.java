package com.example.ank.digilib.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ank.digilib.Activities.BookListActivity;
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
        private ImageView genreImage;

        private Genre mGenre;
        private String name;

        public MenuHolder(View v) {
            super(v);

            nameTextView = (TextView) v.findViewById(R.id.genre_name);
            genreImage = (ImageView) v.findViewById(R.id.genre_image);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mGenre = mGenres.get(position);

            if(mGenre!=null){
                name = mGenre.getName();
            }

            Intent i=new Intent(v.getContext(),BookListActivity.class);
            i.putExtra("genreName", name);
            v.getContext().startActivity(i);
        }

        public void bindIndustry(String name, String backgroundImage) {
            nameTextView.setText(name);
            Glide.with(genreImage.getContext()).load(backgroundImage).into(genreImage);
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
        String backgroundImage = mGenres.get(position).getBackgroundImage();
        holder.bindIndustry(name, backgroundImage);
    }

    @Override
    public int getItemCount() {
        return mGenres.size();
    }
}

