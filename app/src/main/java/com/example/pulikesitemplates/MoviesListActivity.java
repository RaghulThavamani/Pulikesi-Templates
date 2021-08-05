package com.example.pulikesitemplates;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

public class MoviesListActivity extends AppCompatActivity {
    long mLastClickTime = 0;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<MoviesListModel, MoviesListHolder> recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    Query databaseReference;
    String fol;

    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_list);

        MobileAds.initialize(this, "ca-app-pub-2172283144635538~2165970162");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2172283144635538/1662107519");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });


        progressBar = findViewById(R.id.progressbar);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this,2);

        recyclerView.setLayoutManager(layoutManager);



        View v = findViewById(R.id.layout);
        if (!Utils.isNetworkOnline(MyApplication.getInstance())){
            Utils.snackBar(v,"Network Connection Not Available!");
        }

        final Bundle extras = getIntent().getExtras();
        if(extras!=null){
            final String folderName = extras.getString("folderName");
            String folderName2 = extras.getString("folderName2");
            fol = folderName2;
            Objects.requireNonNull(getSupportActionBar()).setTitle(folderName);
            databaseReference = FirebaseDatabase.getInstance().getReference(folderName).orderByChild("movieName");
            databaseReference.keepSynced(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        fetch();



    }


    private void fetch() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<MoviesListModel>()
                        .setQuery(databaseReference, MoviesListModel.class)
                        .build();

        recyclerAdapter = new FirebaseRecyclerAdapter<MoviesListModel, MoviesListHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MoviesListHolder moviesListHolder, int i, @NonNull final MoviesListModel moviesListModel) {

                moviesListHolder.movieTextView.setText(moviesListModel.getMovieName());
                RequestOptions reqOpt = RequestOptions
                        .centerCropTransform()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate()
                        .dontAnimate()
                        .priority(Priority.IMMEDIATE)
                        .encodeFormat(Bitmap.CompressFormat.PNG)
                        .format(DecodeFormat.DEFAULT);

                Glide.with(moviesListHolder.cardImage.getContext())
                        .load(moviesListModel.getImageUrl())
                        .apply(reqOpt).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(moviesListHolder.cardImage);





                moviesListHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        String name = moviesListHolder.movieTextView.getText().toString().trim();
                        Intent intent = new Intent(MoviesListActivity.this,MoviesMemeActivity.class);
                        intent.putExtra("movieName",name);
                        intent.putExtra("folderName2",fol);
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public MoviesListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.movies_list_view, parent, false);
                return new MoviesListHolder(view);
            }
        };

        recyclerAdapter.notifyDataSetChanged();
        recyclerAdapter.startListening();
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd.isLoaded() && mInterstitialAd != null) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
        super.onBackPressed();
    }
}