package com.example.pulikesitemplates;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class Utils {


        public static boolean isNetworkOnline(Context con){
            boolean status = false;
            try
            {
                ConnectivityManager cm = (ConnectivityManager) con
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getNetworkInfo(0);

                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                    status = true;
                } else {
                    netInfo = cm.getNetworkInfo(1);

                    if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                        status = true;
                    } else {
                        status = false;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return status;
        }

        public static void toast(String msg){
            Toast.makeText(MyApplication.getInstance(), msg, Toast.LENGTH_SHORT).show();
        }

        public static void snackBar(View view, String msg){
        Snackbar snackbar = Snackbar
                .make(view, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
       }


}
