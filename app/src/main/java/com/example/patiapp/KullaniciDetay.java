package com.example.patiapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.patiapp.databinding.ActivityKullaniciDetayBinding;
import com.example.patiapp.databinding.ActivityMesajdetayBinding;
import com.example.patiapp.databinding.FragmentIlanlarBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class KullaniciDetay extends AppCompatActivity {
    ActivityKullaniciDetayBinding binding;
    private Intent intent;
    ArrayList<Post3> ilanArrayList;
    ArrayList<Post> ilanArrayList2;
    AdapterYedek adapter;
    private FirebaseFirestore firebaseFirestore;
    String ID;

    public String username, adsoyad, ad, soyad,telno;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKullaniciDetayBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.wp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageViaWhatsApp();
            }
        });


        ilanArrayList2 = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        getData();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(KullaniciDetay.this));
        adapter = new AdapterYedek(ilanArrayList2);
        binding.recyclerView.setAdapter(adapter);

        firebaseFirestore = FirebaseFirestore.getInstance();

        //verileri alma
        intent = getIntent();

        username = intent.getStringExtra("username");

        binding.kurulus.setText(username);

        // Kullanıcı bilgilerini gösterme
        firebaseFirestore.collection("users").whereEqualTo("kullaniciadi", username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            if (snapshot.exists()) {
                                Map<String, Object> data = snapshot.getData();
                                ID = snapshot.getId();
                                String ad = (String) data.get("ad");
                                String soyad = (String) data.get("soyad");
                                String foto = (String) data.get("profil_foto");
                                String biyografi = (String) data.get("biyografi");
                                String eposta = (String) data.get("eposta");
                                telno = (String) data.get("telno");

                                if (foto != null) {
                                    Picasso.get().load(foto).into(binding.profileHeaderImage);
                                }
                                binding.biyografi.setText(biyografi);
                                // Verileri kullanarak UI güncelleme
                                adsoyad = ad + " " + soyad;
                                binding.kurumisim.setText(adsoyad);
                                binding.telefon.setText(telno);
                                binding.epostatext.setText(eposta);
                            } else {
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(KullaniciDetay.this, "Veri yükleme sırasında bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

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
                Toast.makeText(KullaniciDetay.this, "WhatsApp yüklü değil", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(KullaniciDetay.this, "Telefon numarası bulunamadı", Toast.LENGTH_SHORT).show();
        }
    }
    public void getData() {
        intent = getIntent();

        username = intent.getStringExtra("username");
        firebaseFirestore.collection("Ilanlar").orderBy("date", Query.Direction.DESCENDING).whereEqualTo("kullaniciadi", username)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            ilanArrayList2.clear(); // Listenin her veri çekilişinde temizlenmesi önemli.
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                Map<String, Object> data = snapshot.getData();

                                assert data != null;
                                String baslik = (String) data.get("ilanbaslik");
                                String dowloandurl = (String) data.get("dowloandurl");
                                String sehir = (String) data.get("sehir");
                                String ilanturu = (String) data.get("ilanturu");
                                String foto = (String) data.get("userpp");
                                String username = (String) data.get("kullaniciadi");
                                String hesapturu = (String) data.get("hesapturu");
                                String date = null;

                                Object dateObj = data.get("date");
                                if (dateObj instanceof Timestamp) {
                                    Timestamp timestamp = (Timestamp) dateObj;
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                    date = sdf.format(timestamp.toDate());
                                } else if (dateObj instanceof String) {
                                    // String olarak kaydedilmişse, bu blok çalışacak
                                    date = (String) dateObj;
                                }

                                Post ilan = new Post(baslik, dowloandurl, sehir, ilanturu, date, username, foto, hesapturu);
                                ilanArrayList2.add(ilan);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
    public void mesajgonder(View view){
        Intent intent = new Intent(KullaniciDetay.this, MesajEkle.class);
        // Verileri intent ile MesajEkle aktivitesine gönder
        intent.putExtra("gidenveri", username);
        startActivity(intent);
    }
}
