package com.example.patiapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patiapp.databinding.FragmentKullaniciAramaBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class KullaniciArama extends Fragment implements SearchView.OnQueryTextListener {
    private FragmentKullaniciAramaBinding binding;
    private RecyclerView recyclerView;
    private ArrayList<Post3> ilanArrayList;
    private Adapter3 adapter;
    private FirebaseFirestore firebaseFirestore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        ilanArrayList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentKullaniciAramaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Adapter3(ilanArrayList);
        recyclerView.setAdapter(adapter);

        // SearchView'a erişim için binding'i kullan
        binding.SearchViewArama.setOnQueryTextListener(this);

        getData();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String searchQuery = newText.trim().toLowerCase(Locale.getDefault());
        if (searchQuery.isEmpty()) {
            getData();
        } else {
            firebaseFirestore.collection("users")
                    .orderBy("kullaniciadi")
                    .startAt(searchQuery)
                    .endAt(searchQuery + "\uf8ff")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value != null) {
                                ilanArrayList.clear();
                                for (DocumentSnapshot snapshot : value.getDocuments()) {
                                    Map<String, Object> data = snapshot.getData();

                                    assert data != null;
                                    String username = (String) data.get("kullaniciadi");
                                    String ad = (String) data.get("ad");
                                    String soyad = (String) data.get("soyad");
                                    String profil_foto = (String) data.get("profil_foto");

                                    Post3 ilan = new Post3(username, ad, soyad,profil_foto);
                                    ilanArrayList.add(ilan);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
        return false;
    }

    private void getData() {
        firebaseFirestore.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            ilanArrayList.clear();
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                Map<String, Object> data = snapshot.getData();

                                assert data != null;
                                String username = (String) data.get("kullaniciadi");
                                String ad = (String) data.get("ad");
                                String soyad = (String) data.get("soyad");
                                String profil_foto = (String) data.get("profil_foto");

                                Post3 ilan = new Post3(username, ad, soyad,profil_foto);
                                ilanArrayList.add(ilan);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
