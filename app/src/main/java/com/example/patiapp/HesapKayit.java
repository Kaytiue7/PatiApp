package com.example.patiapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.patiapp.databinding.ActivityHesapKayitBinding;
import com.example.patiapp.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.HashMap;
import java.util.Map;

public class HesapKayit extends AppCompatActivity {
    private ActivityHesapKayitBinding binding;
    private FirebaseFirestore firebaseFirestore;
    String telno;
    public String fotoyol, kullaniciadi, eposta, sifre;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHesapKayitBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance(); // firebaseFirestore değişkenini başlat

        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HesapKayit.this, HesapKayitSorgu.class);
                startActivity(intent);
            }
        });
    }


    public void giris(View view) {
        String ad = binding.editTextText2.getText().toString();
        String soyad = binding.editTextText3.getText().toString();
        kullaniciadi = binding.editTextText4.getText().toString();
        eposta = binding.editTextTextEmailAddress.getText().toString();
        sifre = binding.editTextTextPassword2.getText().toString();
        telno = binding.telno.getText().toString();


        if (telno.equals("")) {
            telno = "Paylaşılmamış";
        } else {
            if (ad.equals("") || eposta.equals("") || soyad.equals("") || sifre.equals("") || kullaniciadi.equals("")) {
                Toast.makeText(this, "Boş Alan bırakmayınız", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                firebaseFirestore.collection("UserPhoto")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                    if (snapshot.exists()) {
                                        Map<String, Object> data = snapshot.getData();
                                        fotoyol = (String) data.get("FotografYolu");

                                    }
                                }
                            }
                        });

                // Girilen Kullanıcı adına sahip bir kullanıcı varmı kontrol et
                db.collection("users")
                        .whereEqualTo("kullaniciadi", kullaniciadi)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (queryDocumentSnapshots.isEmpty()) {
                                    // Kullanıcı adı mevcut değil, yeni kullanıcı oluştur
                                    auth.createUserWithEmailAndPassword(eposta, sifre)
                                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                @Override
                                                public void onSuccess(AuthResult authResult) {
                                                    // Kullanıcıya doğrulama e-postası gönder
                                                    authResult.getUser().sendEmailVerification()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(HesapKayit.this, "Doğrulama e-postası gönderildi.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(HesapKayit.this, "Doğrulama e-postası gönderilirken hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });

                                                    // Yeni bir kullanıcı profili oluştur
                                                    Map<String, Object> userProfile = new HashMap<>();
                                                    userProfile.put("ad", ad);
                                                    userProfile.put("soyad", soyad);
                                                    userProfile.put("telno", telno);
                                                    userProfile.put("kullaniciadi", kullaniciadi);
                                                    userProfile.put("eposta", eposta);
                                                    userProfile.put("sifre", sifre);
                                                    userProfile.put("profil_foto", fotoyol);

                                                    // Firestore'da kullanıcı ID'si ile bu profili kaydet
                                                    db.collection("users").document(authResult.getUser().getUid())
                                                            .set(userProfile)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Intent intent = new Intent(HesapKayit.this, HesapGiris.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Intent intent = new Intent(HesapKayit.this, HesapGiris.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                    // Toast.makeText(HesapKayit.this, "Veri kaydedilirken bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(HesapKayit.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    // Kullanıcı adı zaten kullanımda
                                    Toast.makeText(HesapKayit.this, "Bu kullanıcı adı zaten kullanımda.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                auth.createUserWithEmailAndPassword(eposta, sifre)
                                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                // Kullanıcıya doğrulama e-postası gönder
                                                authResult.getUser().sendEmailVerification()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(HesapKayit.this, "Doğrulama e-postası gönderildi.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(HesapKayit.this, "Doğrulama e-postası gönderilirken hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                // Yeni bir kullanıcı profili oluştur
                                                Map<String, Object> userProfile = new HashMap<>();
                                                userProfile.put("ad", ad);
                                                userProfile.put("soyad", soyad);
                                                userProfile.put("telno", telno);
                                                userProfile.put("kullaniciadi", kullaniciadi);
                                                userProfile.put("eposta", eposta);
                                                userProfile.put("sifre", sifre);
                                                userProfile.put("profil_foto", fotoyol);

                                                // Firestore'da kullanıcı ID'si ile bu profili kaydet
                                                db.collection("users").document(authResult.getUser().getUid())
                                                        .set(userProfile)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Intent intent = new Intent(HesapKayit.this, HesapGiris.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Intent intent = new Intent(HesapKayit.this, HesapGiris.class);
                                                                startActivity(intent);
                                                                finish();
                                                                Toast.makeText(HesapKayit.this, "Veri kaydedilirken bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(HesapKayit.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });                            }
                        });


            }
        }

    }



    public void back(View view) {
        Intent intent = new Intent(HesapKayit.this, HesapKayitSorgu.class);
        startActivity(intent);
        finish();

    }

}