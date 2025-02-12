package com.example.patiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

public class Loadingscreeen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadingscreeen);

        new CountDownTimer(2000,1000){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Intent intent=new Intent(Loadingscreeen.this,HesapGiris.class);
                startActivity(intent);
                finish();

            }
        }.start();
    }
}