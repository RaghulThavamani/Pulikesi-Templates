package com.example.pulikesitemplates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerViewMemePage,recyclerViewSocialMedia;
    FirebaseRecyclerAdapter<SocialMediaModel, MoviesListHolder> recyclerAdapter,recyclerAdapter1;
    DatabaseReference databaseReference,databaseReference1;


    long mLastClickTime = 0;
    FloatingActionButton trending,collections,movies,vadiveluMovies;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View v = findViewById(R.id.layout);
        if (!Utils.isNetworkOnline(MyApplication.getInstance())){
            Utils.snackBar(v,"Network Connection Not Available!");
        }

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);


        recyclerViewMemePage = findViewById(R.id.recyclerViewMemePage);
        recyclerViewMemePage.setHasFixedSize(true);

        recyclerViewSocialMedia = findViewById(R.id.recyclerViewSocialMedia);
        recyclerViewSocialMedia.setHasFixedSize(true);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerViewSocialMedia.setLayoutManager(layoutManager);

        final LinearLayoutManager layoutManager2 = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerViewMemePage.setLayoutManager(layoutManager2);



        setRecyclerViewMemePage();
        setRecyclerViewSocialMedia();


        final LinearSnapHelper linearSnapHelper = new LinearSnapHelper();
        linearSnapHelper.attachToRecyclerView(recyclerViewSocialMedia);

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                if (layoutManager.findLastCompletelyVisibleItemPosition() < (recyclerAdapter1.getItemCount() - 1)) {

                    layoutManager.smoothScrollToPosition(recyclerViewSocialMedia, new RecyclerView.State(), layoutManager.findLastCompletelyVisibleItemPosition() + 1);
                }

                else if (layoutManager.findLastCompletelyVisibleItemPosition() == (recyclerAdapter1.getItemCount() - 1)) {

                    layoutManager.smoothScrollToPosition(recyclerViewSocialMedia, new RecyclerView.State(), 0);
                }
            }
        }, 0, 2000);


        final LinearSnapHelper linearSnapHelper1 = new LinearSnapHelper();
        linearSnapHelper1.attachToRecyclerView(recyclerViewMemePage);

        final Timer timer1 = new Timer();
        timer1.schedule(new TimerTask() {

            @Override
            public void run() {

                if (layoutManager2.findLastCompletelyVisibleItemPosition() < (recyclerAdapter.getItemCount() - 1)) {

                    layoutManager2.smoothScrollToPosition(recyclerViewMemePage, new RecyclerView.State(), layoutManager2.findLastCompletelyVisibleItemPosition() + 1);
                }

                else if (layoutManager2.findLastCompletelyVisibleItemPosition() == (recyclerAdapter.getItemCount() - 1)) {

                    layoutManager2.smoothScrollToPosition(recyclerViewMemePage, new RecyclerView.State(), 0);
                }
            }
        }, 0, 2000);


        trending = findViewById(R.id.trending);
        trending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                vibrator.vibrate(50);
                Intent intent = new Intent(MainActivity.this,IndividualMemeActivity.class);
                intent.putExtra("folderName","Trending");
                startActivity(intent);
            }
        });

        collections = findViewById(R.id.collections);
        collections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                vibrator.vibrate(50);
                Intent intent = new Intent(MainActivity.this,IndividualMemeActivity.class);
                intent.putExtra("folderName","Collections");
                startActivity(intent);
            }
        });

        movies = findViewById(R.id.movies);
        movies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                vibrator.vibrate(50);
                Intent intent = new Intent(MainActivity.this,MoviesListActivity.class);
                intent.putExtra("folderName","Movies List");
                intent.putExtra("folderName2","Movies Common");

                startActivity(intent);
            }
        });

        vadiveluMovies = findViewById(R.id.vadiveluMovies);
        vadiveluMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                vibrator.vibrate(50);
                Intent intent = new Intent(MainActivity.this,MoviesListActivity.class);
                intent.putExtra("folderName","Vadivelu List");
                intent.putExtra("folderName2","Vadivelu Movies");
                startActivity(intent);
            }
        });

    }

    private void setRecyclerViewMemePage(){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Meme Pages");
        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<SocialMediaModel>()
                        .setQuery(databaseReference, SocialMediaModel.class)
                        .build();
        recyclerAdapter = new FirebaseRecyclerAdapter<SocialMediaModel, MoviesListHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MoviesListHolder moviesListHolder, int i, @NonNull final SocialMediaModel socialMediaModel) {

                RequestOptions reqOpt = RequestOptions
                        .fitCenterTransform()
                        .dontAnimate().dontTransform()
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // It will cache your image after loaded for first time
                        .priority(Priority.IMMEDIATE)
                        .encodeFormat(Bitmap.CompressFormat.PNG)
                        .format(DecodeFormat.DEFAULT);

                Glide.with(moviesListHolder.cardImage.getContext())
                        .load(socialMediaModel.getImageUrl())
                        .apply(reqOpt)
                        .into(moviesListHolder.cardImage);


                moviesListHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        vibrator.vibrate(50);
                        Intent Getintent = new Intent(Intent.ACTION_VIEW,Uri.parse(socialMediaModel.getChannelUrl()));
                        startActivity(Getintent);
                    }
                });

            }

            @NonNull
            @Override
            public MoviesListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.medias_list_view, parent, false);
                return new MoviesListHolder(view);
            }
        };

        recyclerAdapter.notifyDataSetChanged();
        recyclerAdapter.startListening();
        recyclerViewMemePage.setAdapter(recyclerAdapter);
    }

    private void setRecyclerViewSocialMedia(){
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Social Medias");
        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<SocialMediaModel>()
                        .setQuery(databaseReference1, SocialMediaModel.class)
                        .build();
        recyclerAdapter1 = new FirebaseRecyclerAdapter<SocialMediaModel, MoviesListHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MoviesListHolder moviesListHolder, int i, @NonNull final SocialMediaModel socialMediaModel) {



                RequestOptions reqOpt = RequestOptions
                        .fitCenterTransform()
                        .dontAnimate().dontTransform()
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // It will cache your image after loaded for first time
                        .priority(Priority.IMMEDIATE)
                        .encodeFormat(Bitmap.CompressFormat.PNG)
                        .format(DecodeFormat.DEFAULT);

                Glide.with(moviesListHolder.cardImage.getContext())
                        .load(socialMediaModel.getImageUrl())
                        .apply(reqOpt)
                        .into(moviesListHolder.cardImage);

                moviesListHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        vibrator.vibrate(50);
                        Intent Getintent = new Intent(Intent.ACTION_VIEW,Uri.parse(socialMediaModel.getChannelUrl()));
                        startActivity(Getintent);
                    }
                });

            }

            @NonNull
            @Override
            public MoviesListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.medias_list_view, parent, false);
                return new MoviesListHolder(view);
            }
        };

        recyclerAdapter1.notifyDataSetChanged();
        recyclerAdapter1.startListening();
        recyclerViewSocialMedia.setAdapter(recyclerAdapter1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.your_item_id) {
            vibrator.vibrate(50);
            startActivity(new Intent(MainActivity.this,SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        final androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
        View view1 = getLayoutInflater().inflate(R.layout.dialog_for_exit, null);
        alert.setView(view1);

       FloatingActionButton okBtn = view1.findViewById(R.id.ok);
       FloatingActionButton cancelBtn = view1.findViewById(R.id.cancel);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(50);
                finish();
                alertDialog.dismiss();

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(50);
                alertDialog.dismiss();

            }
        });

        alertDialog.show();
    }
}