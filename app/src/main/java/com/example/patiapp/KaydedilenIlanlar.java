package com.example.patiapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.patiapp.databinding.ActivityKaydedilenIlanlarBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
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

public class KaydedilenIlanlar extends AppCompatActivity {
    private ActivityKaydedilenIlanlarBinding binding;
    FirebaseFirestore firebaseFirestore;
    String username, kullaniciEposta;
    String kayit;
    ArrayList<Post> ilanArrayList;
    Adapter adapter;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKaydedilenIlanlarBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        firebaseFirestore = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        ilanArrayList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(KaydedilenIlanlar.this));
        adapter = new Adapter(ilanArrayList);
        binding.recyclerView.setAdapter(adapter);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance(); // FirebaseAuth nesnesini oluştur
        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        kullaniciEposta = firebaseAuth.getCurrentUser().getEmail(); // Kullanıcı e-postasını al

        firebaseFirestore.collection("Kaydedilenler").whereEqualTo("kaydedenkisi", username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            if (snapshot.exists()) {
                                Map<String, Object> data = snapshot.getData();
                                kayit = (String) data.get("kaydedilenilanID");
                                firebaseFirestore.collection("Ilanlar")
                                        .whereEqualTo("ID", kayit)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                                if (error != null) {
                                                    Toast.makeText(KaydedilenIlanlar.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                    return; // Hata olduğunda işlemi durdur.
                                                }
                                                if (value != null) {
                                                   // ilanArrayList.clear(); // Listenin her veri çekilişinde temizlenmesi önemli.
                                                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                                                        Map<String, Object> data = snapshot.getData();

                                                        assert data != null;
                                                        String baslik = (String) data.get("ilanbaslik");
                                                        String dowloandurl = (String) data.get("dowloandurl");
                                                        String sehir = (String) data.get("sehir");
                                                        String ilanturu = (String) data.get("ilanturu");
                                                        String foto = (String) data.get("userpp");
                                                        String username= (String) data.get("kullaniciadi");
                                                        String hesapturu = (String) data.get("hesapturu");
                                                        String date = null;
                                                        Object dateObj = data.get("date");
                                                        if (dateObj instanceof Timestamp) {
                                                            Timestamp timestamp = (Timestamp) dateObj;
                                                            SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
                                                            date = sdf.format(timestamp.toDate());
                                                        } else if (dateObj instanceof String) {
                                                            // String olarak kaydedilmişse, bu blok çalışacak
                                                            date = (String) dateObj;
                                                        }

                                                        Post ilan = new Post(baslik, dowloandurl, sehir, ilanturu, date, username, foto, hesapturu);
                                                        ilanArrayList.add(ilan);
                                                    }
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }
                                        });


                            } else {
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(KaydedilenIlanlar.this, "Veri yükleme sırasında bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}