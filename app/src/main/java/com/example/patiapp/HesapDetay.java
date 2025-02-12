package com.example.patiapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.patiapp.databinding.ActivityHesapDetayBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;

public class HesapDetay extends AppCompatActivity {
    private ActivityHesapDetayBinding binding;
    private FirebaseAuth mAuth; // FirebaseAuth nesnesi için tek referans kullanılacak
    private FirebaseFirestore db; // Firestore için tek referans
    private FirebaseUser currentUser;

    private String ad, soyad, email, kullaniciadi, sifre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHesapDetayBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Firebase Authentication ve Firestore başlatma
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser(); // Kullanıcı bilgilerini al
        if (currentUser == null) {
            Toast.makeText(this, "Kullanıcı bilgileri yüklenemedi.", Toast.LENGTH_LONG).show();
            return;
        }

        email = currentUser.getEmail(); // Kullanıcı e-postasını al
        if (email != null) {
            db.collection("users").whereEqualTo("eposta", email)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                if (snapshot.exists()) {
                                    Map<String, Object> data = snapshot.getData();
                                    kullaniciadi = (String) data.get("kullaniciadi");
                                    ad = (String) data.get("ad");
                                    soyad = (String) data.get("soyad");
                                    email = (String) data.get("email");
                                    sifre = (String) data.get("sifre");

                                    binding.editTextText2.setText(ad);
                                    binding.editTextText4.setText(kullaniciadi);
                                    binding.editTextText3.setText(soyad);
                                }
                            }
                        }
                    }).addOnFailureListener(e -> Toast.makeText(HesapDetay.this, "Veri yüklenirken hata: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }

        // E-posta ve kullanıcı adı alanını değiştirilemez yap
        binding.editTextTextEmailAddress.setText(email);
        binding.editTextTextEmailAddress.setEnabled(false);
        binding.editTextText4.setEnabled(false);

        binding.buttonBack.setOnClickListener(v -> onBackPressed());

        binding.kayitol.setOnClickListener(v -> {
            // EditText alanlarından güncellenmiş verileri al
            String updatedAd = binding.editTextText2.getText().toString();
            String updatedSoyad = binding.editTextText3.getText().toString();

            // Firestore'da kullanıcı verilerini güncelle
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());
            Map<String, Object> updatedUserData = new HashMap<>();
            updatedUserData.put("ad", updatedAd);
            updatedUserData.put("soyad", updatedSoyad);

            userRef.set(updatedUserData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Toast.makeText(HesapDetay.this, "Kullanıcı verileri başarıyla güncellendi!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(HesapDetay.this, "Kullanıcı verileri güncellenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    public void onClick(View v) {
        Intent intent = new Intent(HesapDetay.this, SifreDegis.class);
        startActivity(intent);
    }
}
