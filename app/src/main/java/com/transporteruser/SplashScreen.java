package com.transporteruser;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.transporteruser.databinding.SplashScreenActivityBinding;


public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       SplashScreenActivityBinding splash = SplashScreenActivityBinding.inflate(getLayoutInflater());
        setContentView(splash.getRoot());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(InternetUtility.isNetworkConnected(SplashScreen.this)) {
                    parseIntent();
                }
                else{
                    AlertDialog ab = new AlertDialog.Builder(SplashScreen.this)
                            .setCancelable(false)
                            .setTitle("Network Not Connected")
                            .setMessage("Please check your network connection")
                            .setPositiveButton("Retry", null)
                            .show();
                    Button positive = ab.getButton(AlertDialog.BUTTON_POSITIVE);
                    positive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(InternetUtility.isNetworkConnected(SplashScreen.this))
                                parseIntent();
                        }
                    });
                }
            }
        },2500);
    }
    private void parseIntent(){
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}