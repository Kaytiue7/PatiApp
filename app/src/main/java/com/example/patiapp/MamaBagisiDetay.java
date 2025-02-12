package com.example.patiapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import com.example.patiapp.databinding.ActivityIlanDetayBinding;
import com.example.patiapp.databinding.ActivityMamaBagisiDetayBinding;
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

public class MamaBagisiDetay extends AppCompatActivity {
    private ActivityMamaBagisiDetayBinding binding;
    private FirebaseFirestore firebaseFirestore;
    String ID;
    String kullaniciadii;

    public String username, foto, foto2,telno;

    //cardview'de görünmeyen diğer verileri atadığım Stringleri tanımlama
    String date , kullaniciemail, dowloandurl,  ilanturu, sehir ,ilanbaslik;


    private FirebaseAuth mAuth;
    String hesapturu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMamaBagisiDetayBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mAuth = FirebaseAuth.getInstance(); // FirebaseAuth instance'ını başlatma
        firebaseFirestore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });





        ilanbaslik = intent.getStringExtra("ilanbaslik");
        ilanturu = intent.getStringExtra("ilanturu");
        sehir = intent.getStringExtra("sehir");
        date = intent.getStringExtra("date");
        dowloandurl = intent.getStringExtra("dowloandurl");
        hesapturu = intent.getStringExtra("hesapturu");
        kullaniciemail = mAuth.getCurrentUser().getEmail();
        firebaseFirestore.collection("Barinak").whereEqualTo("eposta", kullaniciemail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                    if (snapshot.exists()) {
                        Map<String, Object> data = snapshot.getData();
                        kullaniciadii = (String) data.get("kurumisim");

                    }
                }
            }
        });
        firebaseFirestore.collection("users").whereEqualTo("eposta", kullaniciemail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                    if (snapshot.exists()) {
                        Map<String, Object> data = snapshot.getData();
                        kullaniciadii = (String) data.get("kullaniciadi");
                        kayitlimi();
                    }
                }
            }
        });


        //Tıklanın ilanın tüm bilgilerine erişiliyor.
        firebaseFirestore.collection("Ilanlar").whereEqualTo("ilanbaslik", ilanbaslik)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            if (snapshot.exists()) {
                                Map<String, Object> data = snapshot.getData();
                                ID = snapshot.getId();
                                System.out.println(ID);
                                kayitlimi();
                                String baslik = (String) data.get("ilanbaslik");

                                String userpp = (String) data.get("userpp");
                                kullaniciemail = (String) data.get("eposta");
                                String aciklamatext = (String) data.get("aciklama");
                                telno = (String) data.get("telno");
                                String ilce = (String) data.get("ilce");
                                String yas = (String) data.get("yas");
                                String mamamiktar = (String) data.get("mamamiktar");
                                String hayvankategori = (String) data.get("hayvankategori");
                                username = (String) data.get("kullaniciadi");
                                foto = (String) data.get("profil_foto");

                                // Verileri kullanarak UI güncelleyin
                                Picasso.get().load(dowloandurl).into(binding.profileHeaderImage);
                                Picasso.get().load(userpp).into(binding.profilephoto);
                                binding.textView20.setText(username);
                                binding.ilanbaslik.setText(baslik);
                                binding.ilanturu.setText(ilanturu);
                                binding.telefon.setText(telno);
                                binding.konum.setText(mamamiktar);
                                binding.tarih.setText(date);  // Bu 'date' değerini de ayrıca yukarıdan almanız gerekecek
                                binding.epostatext.setText(kullaniciemail);
                                binding.aciklama.setText(aciklamatext);

                                binding.kurulus.setText(hayvankategori);

                                binding.konum2.setText(sehir + " / " + ilce);

                            } else {
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MamaBagisiDetay.this, "Veri yükleme sırasında bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        binding.wp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageViaWhatsApp();
            }
        });


    }
    public void profilegit(View view) {
        if (hesapturu.equals("kisisel")) {
            Intent intent = new Intent(MamaBagisiDetay.this, KullaniciDetay.class);
            intent.putExtra("username", username);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MamaBagisiDetay.this, BarinakKullaniciDetay.class);
            intent.putExtra("eposta", kullaniciemail);
            startActivity(intent);
        }

    }

    public void mesajgonder(View view) {
        Intent intent = new Intent(MamaBagisiDetay.this, MesajEkle.class);
        // Verileri intent ile MesajEkle aktivitesine gönder
        intent.putExtra("gidenveri", username);
        startActivity(intent);
    }

    public void kayitlimi() {
        firebaseFirestore.collection("Kaydedilenler").whereEqualTo("kaydedenkisi", kullaniciadii).whereEqualTo("kaydedilenilanID",ID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    if (documentSnapshot.exists()) {

                        Drawable background = ContextCompat.getDrawable(getApplicationContext(), R.drawable.maaaaaaaaaaaaaaaaaaa);
                        binding.kaydetbutton.setBackground(background);

                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void kaydet(View view) {
        if (kullaniciadii == null) {
            System.out.println("Null");
        } else {
            String kaydedilmisID;// kaydedilenler tablomdaki document'in ID'si

            String ilanID = ID; // İlan ID'sini al

            // Firestore'da "Kaydedilenler" koleksiyonunda "kaydedenkisi" ve "kaydedilenilanID" alanlarını sorgula
            firebaseFirestore.collection("Kaydedilenler")
                    .whereEqualTo("kaydedenkisi", kullaniciadii)
                    .whereEqualTo("kaydedilenilanID", ilanID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            // Sorgulama başarılı, sonuçları kontrol et

                            if (task.getResult().isEmpty()) {
                                // Eğer sonuç yoksa, yani daha önce bu veri eklenmemişse yeni veriyi ekle
                                Map<String, Object> kaydedilenler = new HashMap<>();
                                kaydedilenler.put("kaydedenkisi", kullaniciadii);
                                kaydedilenler.put("kaydedilenilanID", ilanID);

                                firebaseFirestore.collection("Kaydedilenler").add(kaydedilenler)
                                        .addOnSuccessListener(documentReference -> {
                                            System.out.println("Eklendi");
                                            Drawable background = ContextCompat.getDrawable(getApplicationContext(), R.drawable.maaaaaaaaaaaaaaaaaaa);
                                            binding.kaydetbutton.setBackground(background);
                                        })
                                        .addOnFailureListener(e -> {
                                            System.out.println("Eklenemedi: " + e.getMessage());
                                        });
                            } else {
                                // Eğer sonuç varsa, yani daha önce bu veri eklenmişse belgeyi sil
                                DocumentSnapshot document = task.getResult().getDocuments().get(0); // İlk belgeyi al
                                firebaseFirestore.collection("Kaydedilenler").document(document.getId()).delete()
                                        .addOnSuccessListener(aVoid -> {
                                            System.out.println("Veri silindi.");
                                            Drawable background = ContextCompat.getDrawable(getApplicationContext(), R.drawable.save);
                                            binding.kaydetbutton.setBackground(background);
                                        })
                                        .addOnFailureListener(e -> {
                                            System.out.println("Silinemedi: " + e.getMessage());
                                        });

                            }
                        } else {
                            // Sorgulama başarısız oldu
                            System.out.println("Sorgulama başarısız: " + task.getException().getMessage());
                        }
                    });
        }


    }
    private boolean appInstalledOrNot(String url) {
        PackageManager packageManager = getPackageManager();
        boolean app_installed;
        try {
            packageManager.getPackageInfo(url, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    private void sendMessageViaWhatsApp() {
        if (telno != null && !telno.isEmpty()) {
            boolean installed = appInstalledOrNot("com.whatsapp");

            if (installed) {
                String formattedNumber = telno.startsWith("+") ? telno : "+90" + telno;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + formattedNumber));
                startActivity(intent);
            } else {
                Toast.makeText(MamaBagisiDetay.this, "WhatsApp yüklü değil", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MamaBagisiDetay.this, "Telefon numarası bulunamadı", Toast.LENGTH_SHORT).show();
        }
    }


}