package com.example.pulikesitemplates;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    long mLastClickTime = 0;
    LinearLayout rate,share,feedback,sendSocialMediaPages,about;
    String email,url,subject="Pulikesi Templates";
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        MobileAds.initialize(this, "ca-app-pub-2172283144635538~2165970162");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2172283144635538/5582021285");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        View v = findViewById(R.id.layout);
        if (!Utils.isNetworkOnline(MyApplication.getInstance())){
            Utils.snackBar(v,"Network Connection Not Available!");
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Email");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()){
                   String value = dataSnapshot.child("email").getValue().toString();
                   email = value;
               }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        DatabaseReference myRef1 = database.getReference("AppUrl");

        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String value = dataSnapshot.child("url").getValue().toString();
                    url = value;
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        rate = findViewById(R.id.rate);
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


        share = findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent waIntent = new Intent(Intent.ACTION_SEND);
                waIntent.setType("text/plain");
                waIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        url);
                startActivity(Intent.createChooser(waIntent, "Share with"));
            }
        });

        about = findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(SettingsActivity.this,AboutActivity.class));
            }
        });



        feedback = findViewById(R.id.feedback);
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                intent.putExtra(Intent.EXTRA_SUBJECT,subject);
                intent.putExtra(Intent.EXTRA_TEXT, "FeedBack :");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);

                }
            }
        });


        sendSocialMediaPages = findViewById(R.id.sendSocialMediaPages);
        sendSocialMediaPages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                intent.putExtra(Intent.EXTRA_SUBJECT,subject);
                intent.putExtra(Intent.EXTRA_TEXT, "Add Meme Page Url or Social Site Url Here :");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);

                }
            }
        });



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