package com.example.patiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.patiapp.databinding.ActivityMamaBagisiOlusturBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MamaBagisiOlustur extends AppCompatActivity {
    private ActivityMamaBagisiOlusturBinding binding;
    String kullaniciadi, userpp;
    String ID;
    Timestamp timestamp;
    String kullaniciEposta;
    String secilenHayvan;
    String secilenSehir;
    String hesapturu;

    private Uri selectedImageUri = null;  // Seçilen resmin URI'sini saklamak için

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMamaBagisiOlusturBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        kullaniciEposta = mAuth.getCurrentUser().getEmail();

        firebaseFirestore.collection("users").whereEqualTo("eposta", kullaniciEposta)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            if (snapshot.exists()) {
                                Map<String, Object> data = snapshot.getData();

                                kullaniciadi = (String) data.get("kullaniciadi");
                                userpp = (String) data.get("profil_foto");
                                hesapturu = "kisisel";


                            } else {


                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MamaBagisiOlustur.this, "Veri yükleme sırasında bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        firebaseFirestore.collection("Barinak").whereEqualTo("eposta", kullaniciEposta)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            if (documentSnapshot.exists()) {
                                Map<String, Object> data = documentSnapshot.getData();
                                kullaniciadi = (String) data.get("kurumisim");
                                hesapturu = "barinak";
                            } else {
                                Toast.makeText(MamaBagisiOlustur.this, "Belirtilen kriterlere uygun kullanıcı bulunamadı.", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(MamaBagisiOlustur.this, "Veri yükleme sırasında bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        //kategori
        String[] hayvankategori = getResources().getStringArray(R.array.kategoriListele2);
        ArrayAdapter<String> adapterItems2 = new ArrayAdapter<>(MamaBagisiOlustur.this, R.layout.dropdown_item, hayvankategori);
        binding.kategoritext.setAdapter(adapterItems2);
        binding.kategoritext.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                secilenHayvan = item;


            }
        });

        //Şehir
        String[] sehir = getResources().getStringArray(R.array.ilListeleme2);
        ArrayAdapter<String> adapterItems4 = new ArrayAdapter<>(MamaBagisiOlustur.this, R.layout.dropdown_item, sehir);
        binding.autoCompleteTextView6.setAdapter(adapterItems4);
        binding.autoCompleteTextView6.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                secilenSehir = item;

            }
        });

        binding.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);  // 1, bu işlem için tanımlanmış request kodudur
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            binding.imageView2.setImageURI(selectedImageUri);  // Seçilen resmi ImageView'da göster
        }
    }

    public void ilanyukle(View view) {
        String ilanbaslik = binding.editTextText12.getText().toString().trim();
        String eposta = (kullaniciEposta != null) ? kullaniciEposta.trim() : "";
        String ilanturu = "Mama Bağışı";
        String sehir = secilenSehir;
        String mamamiktar = binding.mamamiktar.getText().toString();
        String hayvankategori = secilenHayvan;
        String ilce = binding.editTextTexttt.getText().toString().trim();
        String aciklama = binding.editTextTextt.getText().toString().trim();
        String telno = binding.editTextNumber2.getText().toString().trim();

        if (ilanbaslik.isEmpty() || ilanturu.isEmpty() || hayvankategori.isEmpty() || mamamiktar.isEmpty() || telno.isEmpty() || sehir.isEmpty() || aciklama.isEmpty() || ilce.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Tüm alanları doldurduğunuzdan ve bir fotoğraf seçtiğinizden emin olun", Toast.LENGTH_SHORT).show();
        } else {
            uploadImageToFirebaseStorage(selectedImageUri);
            Intent intent = new Intent(MamaBagisiOlustur.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void ilaniyukle(String imageUrl) {


        Map<String, Object> ilan = new HashMap<>();
        ilan.put("ilanbaslik", binding.editTextText12.getText().toString().trim());
        ilan.put("mamamiktar", binding.mamamiktar.getText().toString().trim());
        ilan.put("eposta", kullaniciEposta.trim());
        ilan.put("ilanturu", "Mama Bağışı".trim());
        ilan.put("sehir", secilenSehir.trim());
        ilan.put("hayvankategori", secilenHayvan.trim());
        ilan.put("ilce", binding.editTextTexttt.getText().toString().trim());
        ilan.put("aciklama", binding.editTextTextt.getText().toString().trim());
        ilan.put("telno", binding.editTextNumber2.getText().toString().trim());
        ilan.put("kullaniciadi", kullaniciadi);
        ilan.put("dowloandurl", imageUrl);
        ilan.put("hesapturu", hesapturu);
        ilan.put("userpp", userpp);
        ilan.put("date", FieldValue.serverTimestamp());


        DocumentReference newIlanRef = firebaseFirestore.collection("Ilanlar").document();

        newIlanRef.set(ilan)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // İlan başarıyla kaydedildi, şimdi zaman damgasını al
                        newIlanRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    timestamp = documentSnapshot.getTimestamp("date");
                                    // Zaman damgasını kullan
                                    handleTimestamp(timestamp);


                                    getDataID();
                                }
                            }
                        });

                        Toast.makeText(MamaBagisiOlustur.this, "İlan başarıyla yüklendi", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MamaBagisiOlustur.this, "İlan yüklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Zaman damgasını işleyen metot
    private void handleTimestamp(Timestamp timestamp) {
        // Zaman damgası ile ilgili işlemler
        System.out.println("Zaman damgası: " + timestamp);
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + UUID.randomUUID().toString());
        storageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        ilaniyukle(imageUrl);

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MamaBagisiOlustur.this, "Fotoğraf yüklenirken bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getDataID() {
        firebaseFirestore.collection("Ilanlar").whereEqualTo("date", timestamp)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            if (snapshot.exists()) {
                                Map<String, Object> data = snapshot.getData();
                                //ID'yi aldık şimdi ise ID'yi ilanın içerisinde gömeceğiz

                                ID = snapshot.getId();
                                System.out.println(ID);

                                // Belirli bir dokümanı güncelleme
                                DocumentReference docRef = firebaseFirestore.collection("Ilanlar").document(ID);
                                docRef.update("ID", ID)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Güncelleme başarılı
                                                System.out.println("Doküman başarıyla güncellendi: " + ID);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Güncelleme sırasında hata
                                                System.err.println("Hata! Doküman güncellenemedi: " + e.getLocalizedMessage());
                                            }
                                        });

                            } else {
                                Toast.makeText(MamaBagisiOlustur.this, "Belirtilen kriterlere uygun ilan bulunamadı.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MamaBagisiOlustur.this, "Veri yükleme sırasında bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void back(View view) {
        Intent intent = new Intent(MamaBagisiOlustur.this, IlanYukleme.class);
        startActivity(intent);
        finish();
    }
}