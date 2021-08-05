package com.meme.pulikesitemplates;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.TextUtils;
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
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

public class IndividualMemeActivity extends AppCompatActivity {
    long mLastClickTime = 0;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<MoviesListModel, ImageViewHolder> recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    Query databaseReference;

    Vibrator vibrator;

    private InterstitialAd mInterstitialAd,mInterstitialAd2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_meme);
        MobileAds.initialize(this, "ca-app-pub-2172283144635538~2165970162");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2172283144635538/8970601951");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd2 = new InterstitialAd(this);
        mInterstitialAd2.setAdUnitId("ca-app-pub-2172283144635538/4129338819");
        mInterstitialAd2.loadAd(new AdRequest.Builder().build());


        progressBar = findViewById(R.id.progressbar);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this,2);

        recyclerView.setLayoutManager(layoutManager);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);


        View v = findViewById(R.id.layout);
        if (!Utils.isNetworkOnline(MyApplication.getInstance())){
            Utils.snackBar(v,"Network Connection Not Available!");
        }

        final Bundle extras = getIntent().getExtras();
        if(extras!=null){
            final String folderName = extras.getString("folderName");
            Objects.requireNonNull(getSupportActionBar()).setTitle(folderName);
            databaseReference = FirebaseDatabase.getInstance().getReference().child(Objects.requireNonNull(folderName)).orderByChild("keyword");
            databaseReference.keepSynced(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        fetch();
    }

    public void fetch(){
        progressBar.setVisibility(View.VISIBLE);
        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<MoviesListModel>()
                        .setQuery(databaseReference, MoviesListModel.class)
                        .build();

        recyclerAdapter = new FirebaseRecyclerAdapter<MoviesListModel, ImageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ImageViewHolder imageViewHolder, final int i, @NonNull final MoviesListModel moviesListModel) {
                RequestOptions reqOpt = RequestOptions
                        .fitCenterTransform()
                        .dontAnimate().dontTransform()
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // It will cache your image after loaded for first time
                        .placeholder(R.drawable.loading)
                        .priority(Priority.HIGH)
                        .encodeFormat(Bitmap.CompressFormat.PNG)
                        .format(DecodeFormat.PREFER_RGB_565);

                Glide.with(imageViewHolder.imageView.getContext())
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
                }).into(imageViewHolder.imageView);





                imageViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        final androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(IndividualMemeActivity.this);
                        View view1 = getLayoutInflater().inflate(R.layout.dialog_media, null);
                        alert.setView(view1);

                        FloatingActionButton saveBtn = view1.findViewById(R.id.save);
                        FloatingActionButton shareBtn = view1.findViewById(R.id.share);

                        final AlertDialog alertDialog = alert.create();
                        alertDialog.setCanceledOnTouchOutside(true);

                        saveBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                vibrator.vibrate(50);
                                if (isStoragePermissionGranted()){
                                    if (mInterstitialAd.isLoaded() && mInterstitialAd != null) {
                                        mInterstitialAd.show();
                                    } else {
                                        new AsyncTaskSaveImage().execute(moviesListModel.getImageUrl());
                                    }

                                    mInterstitialAd.setAdListener(new AdListener() {
                                        @Override
                                        public void onAdClosed() {
                                            mInterstitialAd.loadAd(new AdRequest.Builder().build());
                                        }

                                    });

                                }

                            }
                        });


                        shareBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                vibrator.vibrate(50);
                                if (!TextUtils.isEmpty(moviesListModel.getImageUrl())){
                                    new AsyncTaskShareImage().execute(moviesListModel.getImageUrl());
                                }

                            }
                        });


                        alertDialog.show();
                    }
                });


            }

            @NonNull
            @Override
            public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.imageview, parent, false);
                //  int height = parent.getMeasuredHeight()/2;
                // view.setMinimumHeight(height);
                return new ImageViewHolder(view);
            }
        };

        recyclerAdapter.notifyDataSetChanged();
        recyclerAdapter.startListening();
        recyclerView.setAdapter(recyclerAdapter);
    }

    public class AsyncTaskShareImage  extends AsyncTask<String, String, Bitmap> {
        private final static String TAG = "AsyncTaskLoadImage";


        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(params[0]);
                bitmap = BitmapFactory.decodeStream((InputStream)url.getContent());
            } catch (IOException e) {
                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            }
            return bitmap;
        }
        @SuppressLint("WrongThread")
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            try {
                File cachePath = new File(MyApplication.getInstance().getCacheDir(), "images");
                cachePath.mkdirs(); // don't forget to make the directory
                FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                stream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            File imagePath = new File(MyApplication.getInstance().getCacheDir(), "images");
            File newFile = new File(imagePath, "image.png");
            Uri contentUri = FileProvider.getUriForFile(MyApplication.getInstance(), "com.meme.pulikesitemplates", newFile);

            if (contentUri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                shareIntent.setType("image/png");
                startActivity(Intent.createChooser(shareIntent, "Choose an app"));

            }



        }
    }

    public class AsyncTaskSaveImage  extends AsyncTask<String, String, Bitmap> {
        private final static String TAG = "AsyncTaskLoadImage";


        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(params[0]);
                bitmap = BitmapFactory.decodeStream((InputStream)url.getContent());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            return bitmap;
        }
        @SuppressLint("WrongThread")
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            File filename = null;
            FileOutputStream out = null;
            String destination;

            try {
                File filePath = Environment.getExternalStorageDirectory();
                File directory = new File(filePath.getAbsolutePath()+"/Pulikesi Templates/");
                directory.mkdir();
                File file = new File(directory,"TEMP-"+System.currentTimeMillis()+".jpg");


                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                        out);

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(file));
                sendBroadcast(intent);

                Utils.toast("Image Saved!");


            }
            catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(STORAGE_SERVICE,"Permission is granted");
                return true;
            } else {

                Log.v(STORAGE_SERVICE,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(STORAGE_SERVICE,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.v(STORAGE_SERVICE,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }


    @Override
    public void onBackPressed() {
        if (mInterstitialAd2.isLoaded() && mInterstitialAd2 != null) {
            mInterstitialAd2.show();
        }else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }

        mInterstitialAd2.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd2.loadAd(new AdRequest.Builder().build());
            }

        });
        super.onBackPressed();
    }
}