package com.example.patiapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.patiapp.databinding.ActivityIlanDetayBinding;
import com.example.patiapp.databinding.ActivityIlanDuzenleBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class IlanDuzenle extends AppCompatActivity {
    ActivityIlanDuzenleBinding binding;
    ArrayList<Post> ilanArrayList;
    private FirebaseFirestore firebaseFirestore;
    String ID;
    String kullaniciadii;


    public String username,kullaniciemail,ilce,telno,aciklamatext,baslik,dowloandurl,sehir,ilanturu,hayvankategori,ilanbaslik,date,hayvancinsi;

    //cardview'de görünmeyen diğer verileri atadığım Stringleri tanımlama



    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ilan_detay);
        binding = ActivityIlanDuzenleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firebaseFirestore = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance(); // FirebaseAuth instance'ını başlatma

        firebaseFirestore = FirebaseFirestore.getInstance();
        //verileri alma
        Intent intent=getIntent();
         ilanbaslik= intent.getStringExtra("ilanbaslik");
        ilanturu= intent.getStringExtra("ilanturu");
         sehir= intent.getStringExtra("sehir");
         date= intent.getStringExtra("date");
        //link olarak alıyor resmi
         dowloandurl= intent.getStringExtra("dowloandurl");



/*
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parsedDate = dateFormat.parse(date);
        Timestamp timestamp = new Timestamp(parsedDate.getTime());*/

        firebaseFirestore.collection("Ilanlar").whereEqualTo("ilanbaslik", ilanbaslik)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            if (snapshot.exists()) {
                                Map<String, Object> data = snapshot.getData();
                                ID= snapshot.getId();
                                System.out.println(ID);
                                  baslik = (String) data.get("ilanbaslik");
                                  dowloandurl = (String) data.get("dowloandurl");
                                  sehir = (String) data.get("sehir");
                                  ilanturu = (String) data.get("ilanturu");
                                  kullaniciemail = (String) data.get("email");
                                  aciklamatext = (String) data.get("aciklama");
                                  telno = (String) data.get("telno");
                                  ilce = (String) data.get("ilce");
                                  hayvankategori=(String) data.get("hayvankategori");
                                  hayvancinsi=(String) data.get("hayvancinsi");
                                username=(String) data.get("kullaniciadi");

                                // Verileri kullanarak UI güncelleyin
                                Picasso.get().load(dowloandurl).into(binding.fotoust);
                                binding.ilaninbaslik.setText(baslik);
                                binding.ilanturu2.setText(ilanturu);
                                binding.tarih2.setText(date);  // Bu 'date' değerini de ayrıca yukarıdan almanız gerekecek
                                binding.eposta.setText(kullaniciemail);
                                binding.buyukmesaj.setText(aciklamatext);
                                binding.telno.setText(telno);
                                binding.kategori.setText(hayvankategori);
                                binding.hayvancinsi.setText(hayvancinsi);
                                binding.sehir2.setText(sehir + " / " + ilce);

                                // Diğer işlemler
                                System.out.println("Veriler başarıyla yüklendi: " + kullaniciemail);
                            } else {
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(IlanDuzenle.this, "Veri yükleme sırasında bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });






        FirebaseUser user = mAuth.getCurrentUser();
        String useremail = user.getEmail();
        firebaseFirestore.collection("users").whereEqualTo("eposta", useremail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            if (documentSnapshot.exists()) {
                                Map<String, Object> data = documentSnapshot.getData();

                                assert data != null;
                                kullaniciadii = (String) data.get("kullaniciadi");

                                System.out.println(kullaniciadii);

                            }

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Veriler yüklenemedi!");

                    }
                });



        binding.duzenle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IlanDuzenle.this, IlanUpdate.class);
                intent.putExtra("username", username);
                intent.putExtra("ilanbaslik", ilanbaslik);
                intent.putExtra("ilanturu", ilanturu);
                intent.putExtra("date", date);
                intent.putExtra("kullaniciemail", kullaniciemail);
                intent.putExtra("aciklamatext", aciklamatext);
                intent.putExtra("telno", telno);
                intent.putExtra("sehir", sehir);
                intent.putExtra("ilce", ilce);
                intent.putExtra("dowloandurl", dowloandurl);
                startActivity(intent);




            }
        });
        binding.sil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore = FirebaseFirestore.getInstance();

                // Verileri alma
                Intent intent=getIntent();
                String ilanbaslik= intent.getStringExtra("ilanbaslik");

                // İlgili belgeyi sorgula ve sil
                firebaseFirestore.collection("Ilanlar").whereEqualTo("ilanbaslik", ilanbaslik)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                    // Belgeyi silme
                                    snapshot.getReference().delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Silme başarılı olduğunda yapılacak işlemler
                                                    Toast.makeText(IlanDuzenle.this, "İlan başarıyla silindi", Toast.LENGTH_SHORT).show();
                                                    // Silme işleminden sonra belki bir işlem yapmak istersiniz
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Hata durumunda kullanıcıya bilgi verme
                                                    Toast.makeText(IlanDuzenle.this, "Silme işlemi başarısız: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Veri yüklenemediği durumda kullanıcıya bilgi verme
                                Toast.makeText(IlanDuzenle.this, "Veri yükleme sırasında bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}