package com.example.patiapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.patiapp.databinding.FragmentFavBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class FavFragment extends Fragment {
    private FragmentFavBinding binding;
    ArrayList<Post2> messageArrayList;
    Adapter2 adapter;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    public String kullaniciEposta, username;
    public String alici;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageArrayList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        kullaniciEposta = firebaseAuth.getCurrentUser().getEmail();

        firebaseFirestore.collection("users").whereEqualTo("eposta", kullaniciEposta)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            if (snapshot.exists()) {
                                Map<String, Object> data = snapshot.getData();
                                username = (String) data.get("kullaniciadi");
                                getData();
                            }
                        }
                    }
                });

        firebaseFirestore.collection("Barinak").whereEqualTo("eposta", kullaniciEposta)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            if (snapshot.exists()) {
                                Map<String, Object> data = snapshot.getData();
                                username = (String) data.get("kurumisim");
                                getData();
                            }
                        }
                    }
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recyclerView6.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Adapter2(messageArrayList);
        binding.recyclerView6.setAdapter(adapter);

        binding.button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MesajEkle.class);
                startActivity(intent);
            }
        });

        binding.giden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GidenFragment GidenFragment = new GidenFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, GidenFragment);
                transaction.commit();
            }
        });

        binding.gelenKutusuTemizle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore = FirebaseFirestore.getInstance();

                firebaseFirestore.collection("Messages").whereEqualTo("alici", alici)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                    snapshot.getReference().delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getContext(), "Gelen Mesajlar başarıyla silindi", Toast.LENGTH_SHORT).show();
                                                    getData();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Silme işlemi başarısız: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Veri yükleme sırasında bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

        getData();
    }

    public void getData() {
        firebaseFirestore.collection("Messages").whereEqualTo("alici", username)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (value != null) {
                            messageArrayList.clear();
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                Map<String, Object> data = snapshot.getData();
                                String mesajbaslik = (String) data.get("mesajbaslik");
                                String username = (String) data.get("username");
                                String mesaj = (String) data.get("mesaj");
                                String gonderenemail = (String) data.get("gonderenemail");
                                alici = (String) data.get("alici");
                                String profil_picture = (String) data.get("profil_picture");
                                String onClick = (String) data.get("onClick");
                                String date = null;

                                Object dateObj = data.get("date");
                                if (dateObj instanceof Timestamp) {
                                    Timestamp timestamp = (Timestamp) dateObj;
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    date = sdf.format(timestamp.toDate());
                                } else if (dateObj instanceof String) {
                                    date = (String) dateObj;
                                }

                                Post2 ilan = new Post2(mesajbaslik, username, mesaj, gonderenemail, alici, profil_picture, onClick, date);
                                messageArrayList.add(ilan);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}