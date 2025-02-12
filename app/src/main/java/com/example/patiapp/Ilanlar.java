package com.example.patiapp;

import static android.content.Intent.getIntent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.patiapp.databinding.FragmentIlanlarBinding;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

// Other imports...

public class Ilanlar extends Fragment {
    private FragmentIlanlarBinding binding;
    private ArrayList<Post> ilanArrayList;
    private Adapter adapter;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ilanArrayList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        getData(); // Initial data load
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentIlanlarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Adapter(ilanArrayList);
        binding.recyclerView.setAdapter(adapter);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // Setup SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
    }

    public void getData() {
        firebaseFirestore.collection("Ilanlar").orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            swipeRefreshLayout.setRefreshing(false); // Stop refreshing
                            return;
                        }
                        if (value != null) {
                            ilanArrayList.clear(); // Clear the list before adding new data
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                Map<String, Object> data = snapshot.getData();
                                if (data != null) {
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
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy", Locale.getDefault());
                                        date = sdf.format(timestamp.toDate());
                                    } else if (dateObj instanceof String) {
                                        date = (String) dateObj;
                                    }

                                    Post ilan = new Post(baslik, dowloandurl, sehir, ilanturu, date, username, foto, hesapturu);
                                    ilanArrayList.add(ilan);
                                }
                            }
                            adapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false); // Stop refreshing
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