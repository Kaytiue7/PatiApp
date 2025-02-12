package com.example.patiapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.patiapp.databinding.ActivityFiltrelenmisSonuclarBinding;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class FiltrelenmisSonuclar extends AppCompatActivity {
    private ActivityFiltrelenmisSonuclarBinding binding;
    ArrayList<Post> ilanArrayList;
    Adapter adapter;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFiltrelenmisSonuclarBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        ilanArrayList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(FiltrelenmisSonuclar.this));
        adapter = new Adapter(ilanArrayList);
        binding.recyclerView.setAdapter(adapter);
        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Intent intent = getIntent();
        String sehir = intent.getStringExtra("secilenSehir");
        String hayvan = intent.getStringExtra("secilenHayvan");
        String cins = intent.getStringExtra("secilenCins");
        String ilanTuru = intent.getStringExtra("secilenTur");

        System.out.println(sehir + " " + hayvan + " " + cins + " " + ilanTuru);

        // Verileri filtreleme fonksiyonuna gönder
        getDataFiltered(sehir, hayvan, cins, ilanTuru);
    }

    public void getDataFiltered(String sehir, String hayvan, String cins, String ilanTuru) {
        Query query = firebaseFirestore.collection("Ilanlar");

        if (sehir != null && !sehir.isEmpty() && !"bos".equals(sehir)) {
            query = query.whereEqualTo("sehir", sehir);
        }
        // Aynı düzenlemeyi diğer alanlar için de yapın
        if (hayvan != null && !hayvan.isEmpty() && !"bos".equals(hayvan)) {
            query = query.whereEqualTo("hayvankategori", hayvan);
        }
        if (cins != null && !cins.isEmpty() && !"bos".equals(cins)) {
            query = query.whereEqualTo("hayvancinsi", cins);
        }
        if (ilanTuru != null && !ilanTuru.isEmpty() && !"bos".equals(ilanTuru)) {
            query = query.whereEqualTo("ilanturu", ilanTuru);
        }

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(FiltrelenmisSonuclar.this, "Error: " + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                if (value != null) {
                    ilanArrayList.clear();
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();
                        if (data != null) {
                            // Print data to log for debugging
                            System.out.println("Data received: " + data);

                            String ilanbaslik = data.get("ilanbaslik") != null ? data.get("ilanbaslik").toString() : "";
                            String dowloandurl = data.get("dowloandurl") != null ? data.get("dowloandurl").toString() : "";
                            String sehir = data.get("sehir") != null ? data.get("sehir").toString() : "";
                            String hesapturu = data.get("hesapturu") != null ? data.get("hesapturu").toString() : "";
                            String foto = data.get("userpp") != null ? data.get("userpp").toString() : "";
                            String username = data.get("kullaniciadi") != null ? data.get("kullaniciadi").toString() : "";
                            String ilanturu = data.get("ilanturu") != null ? data.get("ilanturu").toString() : "";

                            Post ilan = new Post(ilanbaslik, dowloandurl, sehir, ilanturu, parseDate(data.get("date")),username,foto,hesapturu);
                            ilanArrayList.add(ilan);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (ilanArrayList.isEmpty()) {
                        System.out.println("No data matched the query.");
                    }
                }
            }
        });
    }

    private String parseDate(Object dateObj) {
        String date = null;
        if (dateObj instanceof Timestamp) {
            Timestamp timestamp = (Timestamp) dateObj;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            date = sdf.format(timestamp.toDate());
        } else if (dateObj instanceof String) {
            date = (String) dateObj;
        }
        return date != null ? date : "";
    }

    public void back(View view) {
        Intent intent = new Intent(FiltrelenmisSonuclar.this, MainActivity.class);
        intent.putExtra("nereye", "aramafragment");
        startActivity(intent);
        finish();
    }
}
