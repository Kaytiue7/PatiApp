package com.example.patiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.patiapp.databinding.ActivityHesapGirisBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class HesapGiris extends AppCompatActivity {
    private ActivityHesapGirisBinding binding;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHesapGirisBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance(); // FirebaseAuth nesnesini oluştur


        if (user != null) {

            Intent intent = new Intent(HesapGiris.this, MainActivity.class);
            startActivity(intent);
            finish();
        }


    }

    //kayıt yönlendirme
    public void kayitol(View view) {
        Intent intent = new Intent(HesapGiris.this, HesapKayitSorgu.class);
        startActivity(intent);
        finish();
    }

    //Giriş
    public void giris(View view) {
        String eposta = binding.editTextText8.getText().toString();
        String sifre = binding.editTextText1.getText().toString();

        if (eposta.equals("") || sifre.equals("")) {
            Toast.makeText(this, "Boş alan bırakmayınız!", Toast.LENGTH_SHORT).show();
        } else {
            auth.signInWithEmailAndPassword(eposta, sifre).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent = new Intent(HesapGiris.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(HesapGiris.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }
    }
}