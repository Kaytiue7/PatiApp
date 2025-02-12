package com.example.patiapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.patiapp.databinding.FragmentBarinakArama2Binding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class BarinakArama extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private FragmentBarinakArama2Binding binding;
    private ArrayList<Post4> ilanArrayList;
    private Adapter5 adapter;
    private FirebaseFirestore firebaseFirestore;
    private String kullaniciadi; // Kullanıcı adını tutacak değişken

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_barinak_arama2);

        ilanArrayList = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();

        binding = FragmentBarinakArama2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter5(ilanArrayList);
        binding.recyclerView.setAdapter(adapter);

        // SearchView'deki metni değiştirdiğinde kullanıcı adını güncelle
        binding.SearchViewArama.setOnQueryTextListener(this);
        getData2();
    }

    // Verileri getir
    public void getData() {
        // Kullanıcı adına göre sorgu yap
        firebaseFirestore.collection("Barinak").whereEqualTo("kurumisim", kullaniciadi)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@androidx.annotation.Nullable QuerySnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            ilanArrayList.clear(); // Listenin her veri çekilişinde temizlenmesi önemli.
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                Map<String, Object> data = snapshot.getData();

                                assert data != null;
                                String acikadres = (String) data.get("acikadres");
                                String biyografi = (String) data.get("biyografi");
                                String eposta = (String) data.get("eposta");
                                String ilce = (String) data.get("ilce");
                                String kurulusyili = (String) data.get("kurulusyili");
                                String kurumisim = (String) data.get("kurumisim");
                                String profil_foto = (String) data.get("profil_foto");
                                String sehir = (String) data.get("sehir");
                                String telno = (String) data.get("telno");


                                Post4 ilan = new Post4(acikadres, biyografi, eposta, ilce, kurulusyili, kurumisim, profil_foto, sehir, telno);
                                ilanArrayList.add(ilan);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    public void getData2() {
        // Kullanıcı adına göre sorgu yap
        firebaseFirestore.collection("Barinak")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@androidx.annotation.Nullable QuerySnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            ilanArrayList.clear(); // Listenin her veri çekilişinde temizlenmesi önemli.
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                Map<String, Object> data = snapshot.getData();

                                assert data != null;
                                String acikadres = (String) data.get("acikadres");
                                String biyografi = (String) data.get("biyografi");
                                String eposta = (String) data.get("eposta");
                                String ilce = (String) data.get("ilce");
                                String kurulusyili = (String) data.get("kurulusyili");
                                String kurumisim = (String) data.get("kurumisim");
                                String profil_foto = (String) data.get("profil_foto");
                                String sehir = (String) data.get("sehir");
                                String telno = (String) data.get("telno");


                                Post4 ilan = new Post4(acikadres, biyografi, eposta, ilce, kurulusyili, kurumisim, profil_foto, sehir, telno);
                                ilanArrayList.add(ilan);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu); // Menüyü oluştur
        MenuItem searchItem = menu.findItem(R.id.action_search); // SearchView'i al
        SearchView searchView = (SearchView) searchItem.getActionView(); // SearchView'i bul

        // SearchView'in metnini değiştirdiğinde bu sınıftaki onQueryTextChange metodu çalışacak
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // SearchView'deki metni kullanıcı adına ata
        kullaniciadi = newText;
        if (newText.isEmpty()) {
            getData2();
        } else {
            getData();
        }
        return false;
    }
}
