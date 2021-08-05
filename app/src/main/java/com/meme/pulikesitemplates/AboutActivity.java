package com.meme.pulikesitemplates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {
    long mLastClickTime = 0;
    TextView privacyPolicy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Objects.requireNonNull(getSupportActionBar()).setTitle("About");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        View v = findViewById(R.id.layout);
        if (!Utils.isNetworkOnline(MyApplication.getInstance())){
            Utils.snackBar(v,"Network Connection Not Available!");
        }


        privacyPolicy = findViewById(R.id.privacyPolicy);
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("PrivacyPolicy");

                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String value = Objects.requireNonNull(dataSnapshot.child("url").getValue()).toString();
                            Uri uri = Uri.parse(value); // missing 'http://' will cause crashed
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Failed to read value

                    }
                });

            }
        });
    }
}