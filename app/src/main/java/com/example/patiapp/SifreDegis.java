package com.example.patiapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.patiapp.databinding.ActivityHesapDetayBinding;
import com.example.patiapp.databinding.ActivitySifreDegisBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


public class SifreDegis extends AppCompatActivity {
    private ActivitySifreDegisBinding binding;
    private String  email, sifre;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySifreDegisBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        mAuth = FirebaseAuth.getInstance(); // FirebaseAuth instance'ını başlatma
        firebaseAuth = FirebaseAuth.getInstance(); // FirebaseAuth nesnesini oluştur
        firebaseFirestore = FirebaseFirestore.getInstance();

        email = firebaseAuth.getCurrentUser().getEmail(); // Kullanıcı e-postasını al
        firebaseFirestore.collection("users").whereEqualTo("eposta", email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            if (snapshot.exists()) {
                                Map<String, Object> data = snapshot.getData();

                                sifre = (String) data.get("sifre");
                                Toast.makeText(SifreDegis.this, sifre, Toast.LENGTH_SHORT).show();
                                binding.editTextTextPassword.setText(sifre);

                            }
                        }
                    }
                });
        binding.kayitol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String newPassword = binding.editTextTextPassword.getText().toString();

                user.updatePassword(newPassword)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SifreDegis.this, "Şifre güncellendi.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

            String updatedAd = binding.editTextTextPassword.getText().toString();


        });
    }

    public void onClick(View v) {
        // EditText alanlarından güncellenmiş verileri alın
        String updatedPassword = binding.editTextTextPassword.getText().toString();



        // Firestore'da kullanıcı verilerini güncelleyin
        DocumentReference userRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
        Map<String, Object> updatedUserData = new HashMap<>();
        updatedUserData.put("sifre", updatedPassword);


        userRef.set(updatedUserData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SifreDegis.this, "Şifre başarıyla güncellendi!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SifreDegis.this, "Şifre güncellenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
