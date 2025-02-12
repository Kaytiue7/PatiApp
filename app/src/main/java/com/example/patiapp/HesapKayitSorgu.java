package com.example.patiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.patiapp.databinding.ActivityHesapKayitBinding;
import com.example.patiapp.databinding.ActivityHesapKayitSorguBinding;

public class HesapKayitSorgu extends AppCompatActivity {
    private ActivityHesapKayitSorguBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHesapKayitSorguBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HesapKayitSorgu.this,HesapGiris.class);
                startActivity(intent);
            }
        });
    }
    public void barinak(View view){
        Intent intent=new Intent(HesapKayitSorgu.this,BarinakHesapOlustur.class);
        startActivity(intent);
        finish();
    }
    public void kisisel(View view){
        Intent intent=new Intent(HesapKayitSorgu.this,HesapKayit.class);
        startActivity(intent);
        finish();
    }
    public void back(View view){
        Intent intent=new Intent(HesapKayitSorgu.this,HesapGiris.class);
        startActivity(intent);
        finish();

    }

}