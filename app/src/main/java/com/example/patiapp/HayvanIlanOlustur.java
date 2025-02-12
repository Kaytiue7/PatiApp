package com.example.patiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.patiapp.databinding.ActivityIlanYuklemeBinding;
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
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class HayvanIlanOlustur extends AppCompatActivity {
    private com.example.patiapp.databinding.ActivityHayvanIlanOlusturBinding binding;
    String kullaniciadi, userpp;
    String ID;
    Timestamp timestamp;
    String kullaniciEposta;
    private Uri selectedImageUri = null;  // Seçilen resmin URI'sini saklamak için
    String secilenHayvan;
    String secilenTur;
    String secilenCins;
    String secilenSehir;
    String secilenCinsiyet;
    private FirebaseAuth mAuth;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    public String foto2;
    String hesapturu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.example.patiapp.databinding.ActivityHayvanIlanOlusturBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Intent intent=getIntent();
        String tur=intent.getStringExtra("tur");
        if (tur.equals("normal")){
            secilenTur="Ücretsiz Sahiplendirme";
            binding.textView27.setText(secilenTur);
        }
        else{
            secilenTur="Çiftleştirme İlanı";
            binding.textView27.setText(secilenTur);
            binding.textView27.setBackgroundColor(Color.parseColor("#f59e42"));
        }


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
                                System.out.println(hesapturu);

                            } else {


                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Veri Bulunamadı");
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
                                userpp = (String) data.get("profil_foto");
                                hesapturu = "barinak";
                                System.out.println(hesapturu);
                            } else {
                            }
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(HayvanIlanOlustur.this, "Veri yükleme sırasında bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        secilenTur = "Ücretsiz Sahiplendirme";

        //kategori
        String[] hayvankategori = getResources().getStringArray(R.array.kategoriListele2);
        ArrayAdapter<String> adapterItems2 = new ArrayAdapter<>(HayvanIlanOlustur.this, R.layout.dropdown_item, hayvankategori);
        binding.kategoritext.setAdapter(adapterItems2);
        binding.kategoritext.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                secilenHayvan = item;
                //kategori ile beraber çalışan hayvan cinsi kodları
                updateAnimalSpecificInfo();
            }
        });

        //Şehir
        String[] sehir = getResources().getStringArray(R.array.ilListeleme2);
        ArrayAdapter<String> adapterItems4 = new ArrayAdapter<>(HayvanIlanOlustur.this, R.layout.dropdown_item, sehir);
        binding.autoCompleteTextView6.setAdapter(adapterItems4);
        binding.autoCompleteTextView6.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                secilenSehir = item;

            }
        });

        //Cinsiyeti
        String[] cinsiyet = getResources().getStringArray(R.array.cinsiyet);
        ArrayAdapter<String> adapterItems5 = new ArrayAdapter<>(HayvanIlanOlustur.this, R.layout.dropdown_item, cinsiyet);
        binding.autoCompleteTextView4.setAdapter(adapterItems5);
        binding.autoCompleteTextView4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                secilenCinsiyet = item;

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
                Toast.makeText(HayvanIlanOlustur.this, "Fotoğraf yüklenirken bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ilaniyukle(String imageUrl) {


        Map<String, Object> ilan = new HashMap<>();
        ilan.put("ilanbaslik", binding.editTextText12.getText().toString().trim());
        ilan.put("yas", binding.editTextNumber.getText().toString().trim());
        ilan.put("saglikdurumu", binding.editTextText14.getText().toString().trim());
        ilan.put("eposta", kullaniciEposta.trim());
        ilan.put("cinsiyet", secilenCinsiyet.trim());
        ilan.put("ilanturu", secilenTur.trim());
        ilan.put("sehir", secilenSehir.trim());
        ilan.put("hayvankategori", secilenHayvan.trim());
        ilan.put("hayvancinsi", secilenCins.trim());
        ilan.put("ilce", binding.editTextTexttt.getText().toString().trim());
        ilan.put("aciklama", binding.editTextTextt.getText().toString().trim());
        ilan.put("telno", binding.editTextNumber2.getText().toString().trim());
        ilan.put("kullaniciadi", kullaniciadi);
        ilan.put("dowloandurl", imageUrl);
        ilan.put("saglik", binding.editTextText14.getText().toString().trim());
        ilan.put("userpp", userpp);
        ilan.put("hesapturu", hesapturu);
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

                        Toast.makeText(HayvanIlanOlustur.this, "İlan başarıyla yüklendi", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HayvanIlanOlustur.this, "İlan yüklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Zaman damgasını işleyen metot
    private void handleTimestamp(Timestamp timestamp) {
        // Zaman damgası ile ilgili işlemler
        System.out.println("Zaman damgası: " + timestamp);
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
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HayvanIlanOlustur.this, "Veri yükleme sırasında bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void ilanyukle(View view) {
        String ilanbaslik = binding.editTextText12.getText().toString().trim();
        String yas = binding.editTextNumber.getText().toString().trim();
        String saglikdurumu = binding.editTextText14.getText().toString().trim();
        String eposta = (kullaniciEposta != null) ? kullaniciEposta.trim() : "";
        String cinsiyet = (secilenCinsiyet != null) ? secilenCinsiyet.trim() : "";
        String ilanturu = (secilenTur != null) ? secilenTur.trim() : "";
        String sehir = (secilenSehir != null) ? secilenSehir.trim() : "";
        String hayvankategori = (secilenHayvan != null) ? secilenHayvan.trim() : "";
        String hayvancinsi = (secilenCins != null) ? secilenCins.trim() : "";
        String ilce = binding.editTextTexttt.getText().toString().trim();
        String aciklama = binding.editTextTextt.getText().toString().trim();
        String telno = binding.editTextNumber2.getText().toString().trim();

        if (ilanbaslik.isEmpty() || ilanturu.isEmpty() || hayvankategori.isEmpty() || hayvancinsi.isEmpty() || yas.isEmpty() || saglikdurumu.isEmpty() || eposta.isEmpty() || cinsiyet.isEmpty() || telno.isEmpty() || sehir.isEmpty() || aciklama.isEmpty() || ilce.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Tüm alanları doldurduğunuzdan ve bir profil fotoğrafı seçtiğinizden emin olun", Toast.LENGTH_SHORT).show();
        } else {
            uploadImageToFirebaseStorage(selectedImageUri);
            Intent intent = new Intent(HayvanIlanOlustur.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void updateAnimalSpecificInfo() {

        if (secilenHayvan != null) {

            if (secilenHayvan.equals("Herhangi")) {

                binding.myTextInputLayout.setEnabled(false); // TextInputLayout'u devre dışı bırak
            }
            //KEDİ TÜRLERİ
            if (secilenHayvan.equals("Köpek")) {
                System.out.println("Seçilen hayvan: " + secilenHayvan);
                // Köpekler için özel işlemler
                String[] hayvancinsi = getResources().getStringArray(R.array.KopekcinsListele2);
                ArrayAdapter<String> adapterItems3 = new ArrayAdapter<>(HayvanIlanOlustur.this, R.layout.dropdown_item, hayvancinsi);
                binding.autoCompleteTextView3.setAdapter(adapterItems3);
                binding.autoCompleteTextView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        String item = adapterView.getItemAtPosition(position).toString();
                        System.out.println("Seçilen cins: " + item);
                        secilenCins = item;
                    }
                });
                //KÖPEK TÜRLERİ
            } else if (secilenHayvan.equals("Kedi")) {
                System.out.println("Seçilen hayvan: " + secilenHayvan);
                // Kediler için özel işlemler

                String[] hayvancinsi = getResources().getStringArray(R.array.KedicinsListele2);
                ArrayAdapter<String> adapterItems3 = new ArrayAdapter<>(HayvanIlanOlustur.this, R.layout.dropdown_item, hayvancinsi);
                binding.autoCompleteTextView3.setAdapter(adapterItems3);
                binding.autoCompleteTextView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        String item = adapterView.getItemAtPosition(position).toString();
                        System.out.println("Seçilen cins: " + item);
                        secilenCins = item;
                    }
                });
            }
        } else {
            binding.myTextInputLayout.setEnabled(false); // TextInputLayout'u devre dışı bırak
        }


    }


    public void back(View view) {
        Intent intent = new Intent(HayvanIlanOlustur.this, IlanYukleme.class);
        startActivity(intent);
        finish();
    }
}