package com.example.patiapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.patiapp.databinding.ActivityIlanUpdateBinding;
import com.example.patiapp.databinding.ActivityIlanYuklemeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class IlanUpdate extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    Timestamp timestamp;
    private FirebaseStorage firebaseStorage;
    private ActivityIlanUpdateBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    public String kullaniciEposta2, NickName, ID;
    ArrayList<Post> ilanArrayList;
    Adapter adapter;
    public String baslik;
    public String dowloandurl;
    public String sehir;
    public String ilanturu;
    public String foto;
    public String username;
    public String hesapturu;
    public String hayvankategori;
    public String hayvancinsi;
    public String cinsiyet;
    public String yas;
    public String saglikdurumu;
    public String ilce;
    public String telno;
    public String date;
    public String aciklama;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIlanUpdateBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        firebaseAuth = FirebaseAuth.getInstance();


        //Kullanıcı adını aldım
        kullaniciEposta2 = firebaseAuth.getCurrentUser().getEmail();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users").whereEqualTo("eposta", kullaniciEposta2)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            if (snapshot.exists()) {
                                Map<String, Object> data = snapshot.getData();
                                //  ID= snapshot.getId();
                                String kullaniciadi = (String) data.get("kullaniciadi");
                                NickName = kullaniciadi;
                                System.out.println(NickName);
                            } else {
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(IlanUpdate.this, "Veri yükleme sırasında bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();


        Intent intent = getIntent();
        baslik = intent.getStringExtra("ilanbaslik");
        dowloandurl = intent.getStringExtra("dowloandurl");
        sehir = intent.getStringExtra("sehir");
        ilanturu = intent.getStringExtra("ilanturu");
        foto = intent.getStringExtra("foto");
        username = intent.getStringExtra("username");
        hesapturu = intent.getStringExtra("hesapturu");
        hayvankategori = intent.getStringExtra("hayvankategori");
        hayvancinsi = intent.getStringExtra("hayvancinsi");
        cinsiyet = intent.getStringExtra("cinsiyet");
        yas = intent.getStringExtra("yas");
        saglikdurumu = intent.getStringExtra("saglikdurumu");
        ilce = intent.getStringExtra("ilce");
        telno = intent.getStringExtra("telno");
        aciklama = intent.getStringExtra("aciklama");
        date = intent.getStringExtra("date");


        binding.editTextText12.setText(baslik);
        binding.kategoritext.setText(hayvankategori);
        binding.autoCompleteTextView3.setText(hayvancinsi);
        binding.autoCompleteTextView4.setText(cinsiyet);
        binding.editTextNumber.setText(yas);
        binding.editTextText14.setText(saglikdurumu);
        binding.editTextTextt.setText(aciklama);
        binding.autoCompleteTextView6.setText(sehir);
        binding.editTextTexttt.setText(ilce);
        binding.editTextNumber2.setText(telno);

        Picasso.get().load(dowloandurl).into(binding.imageView2);


        binding.guncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, Object> ilanData = new HashMap<>();
                ilanData.put("ilanbaslik", binding.editTextText12.getText().toString().trim());
                ilanData.put("yas", binding.editTextNumber.getText().toString().trim());
                ilanData.put("saglikdurumu", binding.editTextText14.getText().toString().trim());
                ilanData.put("ilce", binding.editTextTexttt.getText().toString().trim());
                ilanData.put("aciklama", binding.editTextTextt.getText().toString().trim());
                ilanData.put("telno", binding.editTextNumber2.getText().toString().trim());
                ilanData.put("saglik", binding.editTextText14.getText().toString().trim());


                firebaseFirestore.collection("Ilanlar")
                        .whereEqualTo("ilanbaslik", baslik)
                        .whereEqualTo("aciklama", aciklama)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                    // Belgeyi yeni verilerle güncelle
                                    snapshot.getReference().update(ilanData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Güncelleme başarılı
                                                    Toast.makeText(IlanUpdate.this, "İlan başarıyla güncellendi", Toast.LENGTH_SHORT).show();
                                                    Toast.makeText(IlanUpdate.this, "İlan başarıyla silindi", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(IlanUpdate.this, MainActivity.class);
                                                    startActivity(intent);
                                                    // İsteğe bağlı olarak, güncellemeden sonra başka işlemler gerçekleştirebilirsiniz
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Başarısızlık durumunu ele alın
                                                    Toast.makeText(IlanUpdate.this, "Güncelleme işlemi başarısız: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Başarısızlık durumunu ele alın
                                Toast.makeText(IlanUpdate.this, "Veri yükleme sırasında bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        binding.sil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore = FirebaseFirestore.getInstance();

                // Verileri alma

                // İlgili belgeyi sorgula ve sil
                firebaseFirestore.collection("Ilanlar").whereEqualTo("ilanbaslik", baslik).whereEqualTo("aciklama", aciklama)
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
                                                    Toast.makeText(IlanUpdate.this, "İlan başarıyla silindi", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(IlanUpdate.this, MainActivity.class);
                                                    startActivity(intent);
                                                    // Silme işleminden sonra belki bir işlem yapmak istersiniz
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Hata durumunda kullanıcıya bilgi verme
                                                    Toast.makeText(IlanUpdate.this, "Silme işlemi başarısız: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Veri yüklenemediği durumda kullanıcıya bilgi verme
                                Toast.makeText(IlanUpdate.this, "Veri yükleme sırasında bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


    }


}