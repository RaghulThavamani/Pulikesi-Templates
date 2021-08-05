package com.meme.pulikesitemplates;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MoviesListHolder extends RecyclerView.ViewHolder {
    CardView cardView;
    ImageView cardImage;
    TextView movieTextView;
    public MoviesListHolder(@NonNull View itemView) {
        super(itemView);

        movieTextView = itemView.findViewById(R.id.movieName);
        cardImage = itemView.findViewById(R.id.cardImage);
        cardView = itemView.findViewById(R.id.cardView);

    }
}
