package com.example.patiapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.patiapp.databinding.FragmentMessagesBinding;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class MessagesFragment extends Fragment {
    private FragmentMessagesBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private ArrayList<Post6> ilanArrayList2;
    private AdapterYedek adapter;
    private AdapterYedek2 adapter2;
    private String kullaniciEposta, kendikullaniciadi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        ilanArrayList2 = new ArrayList<>();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMessagesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        fetchUserData();
        ilanlarim();
        setupButtonListeners();


    }

    private void setupRecyclerView() {
        adapter2 = new AdapterYedek2(ilanArrayList2);
        binding.recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView2.setAdapter(adapter2);
    }


    private void setupButtonListeners() {
        binding.buttonsingout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getContext(), HesapGiris.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        binding.saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), KaydedilenIlanlar.class);
                intent.putExtra("username",kendikullaniciadi);
                startActivity(intent);
            }
        });

        binding.button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HesapDetay.class);
                startActivity(intent);
            }
        });

        binding.imageView13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.add_person_dialog, null);
                builder.setView(dialogView);
                Button goToBio = dialogView.findViewById(R.id.goToBio);
                Button goToProfile = dialogView.findViewById(R.id.goToProfile);
                final AlertDialog dialog = builder.create();
                dialog.show();

                goToProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), ProfilFotoIsteme.class);

                        startActivity(intent);
                    }
                });
                goToBio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), BiyografiEkle.class);
                        startActivity(intent);
                    }
                });
            }
        });


        binding.buttonilanlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ilanlarim();
            }
        });
    }

    public void ilanlarim() {
        firebaseFirestore.collection("Ilanlar").orderBy("date", Query.Direction.DESCENDING)
                .whereEqualTo("eposta", kullaniciEposta)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            System.out.println(error.getLocalizedMessage());
                            return;
                        }
                        if (value != null) {
                            ilanArrayList2.clear();
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                Map<String, Object> data = snapshot.getData();
                                assert data != null;
                                String baslik = (String) data.get("ilanbaslik");
                                String dowloandurl = (String) data.get("dowloandurl");
                                String sehir = (String) data.get("sehir");
                                String ilanturu = (String) data.get("ilanturu");
                                String foto= (String) data.get("userpp");
                                String username= (String) data.get("kullaniciadi");
                                String hesapturu= (String) data.get("hesapturu");
                                String hayvankategori= (String) data.get("hayvankategori");
                                String hayvancinsi= (String) data.get("hayvancinsi");
                                String cinsiyet= (String) data.get("cinsiyet");
                                String yas= (String) data.get("yas");
                                String saglikdurumu= (String) data.get("saglikdurumu");
                                String ilce= (String) data.get("ilce");
                                String telno= (String) data.get("telno");
                                String aciklama= (String) data.get("aciklama");
                                String date = null;
                                Object dateObj = data.get("date");

                                if (dateObj instanceof Timestamp) {
                                    Timestamp timestamp = (Timestamp) dateObj;
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    date = sdf.format(timestamp.toDate());
                                } else if (dateObj instanceof String) {
                                    date = (String) dateObj;


                                }

                                Post6 ilan = new Post6(baslik, dowloandurl, sehir, ilanturu,foto, username, hesapturu, aciklama, hayvankategori, hayvancinsi, cinsiyet, yas, saglikdurumu, ilce, telno,  date);
                                ilanArrayList2.add(ilan);
                            }
                            adapter2.notifyDataSetChanged();
                        }
                    }
                });
    }



    private void fetchUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            kullaniciEposta = user.getEmail();
            firebaseFirestore.collection("users").whereEqualTo("eposta", kullaniciEposta)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                if (snapshot.exists()) {
                                    Map<String, Object> data = snapshot.getData();
                                    kendikullaniciadi = (String) data.get("kullaniciadi");
                                    String bio = (String) data.get("biyografi");
                                    String ad = (String) data.get("ad");
                                    String profilfotoURI = (String) data.get("profil_foto");
                                    String soyad = (String) data.get("soyad");
                                    binding.textView6.setText(kendikullaniciadi);
                                    binding.textView2.setText(ad +" " + soyad);
                                    binding.bio.setText(bio);
                                    Picasso.get().load(profilfotoURI).into(binding.imageView13);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Veri yükleme hatası: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            if (user != null) {
                kullaniciEposta = user.getEmail();
                firebaseFirestore.collection("Barinak").whereEqualTo("eposta", kullaniciEposta)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                    if (snapshot.exists()) {
                                        Map<String, Object> data = snapshot.getData();
                                        kendikullaniciadi = (String) data.get("kurumisim");
                                        String bio = (String) data.get("biyografi");
                                        String profilfotoURI = (String) data.get("profil_foto");
                                        binding.textView6.setText(kendikullaniciadi);
                                        binding.textView2.setText(" ");
                                        binding.bio.setText(bio);
                                        Picasso.get().load(profilfotoURI).into(binding.imageView13);
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Veri yükleme hatası: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }



    }
    @Override
    public void onDestroyView () {
        super.onDestroyView();
        binding = null;
    }
}