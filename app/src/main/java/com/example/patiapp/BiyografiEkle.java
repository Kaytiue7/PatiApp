package com.example.patiapp;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.patiapp.databinding.ActivityBiyografiEkleBinding;
import com.example.patiapp.databinding.ActivityProfilFotoIstemeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class BiyografiEkle extends AppCompatActivity {

    private ActivityBiyografiEkleBinding binding;
    private FirebaseAuth auth;
    private Uri selectedImageUri = null;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth mAuth;

    //garanti
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBiyografiEkleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth = FirebaseAuth.getInstance();

        mAuth = FirebaseAuth.getInstance(); // FirebaseAuth instance'ını başlatma
        firebaseAuth = FirebaseAuth.getInstance(); // FirebaseAuth nesnesini oluştur
        firebaseFirestore = FirebaseFirestore.getInstance();
        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FirebaseUser user = mAuth.getCurrentUser();
        String useremail = user.getEmail();

        firebaseFirestore.collection("users").whereEqualTo("eposta", useremail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            if (documentSnapshot.exists()) {
                                Map<String, Object> data = documentSnapshot.getData();

                                String bio = (String) data.get("biyografi");


                                if (bio != null) {
                                    binding.ediText.setText(bio);


                                } else {
                                    binding.atla2.setVisibility(View.GONE);
                                }

                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Veriler yüklenemedi!");
                    }
                });


        binding.atla2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String biyo = binding.ediText.getText().toString();
                String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if (currentUserUid != null) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference userRef = db.collection("users").document(currentUserUid);
                    userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                // Kullanıcı verilerini al
                                String ad = documentSnapshot.getString("ad");
                                String soyad = documentSnapshot.getString("soyad");
                                String kullaniciadi = documentSnapshot.getString("kullaniciadi");
                                String eposta = documentSnapshot.getString("eposta");
                                String profil_foto = documentSnapshot.getString("profil_foto");

                                // Profil fotoğrafı güncellemesi
                                Map<String, Object> userProfile = new HashMap<>();
                                userProfile.put("ad", ad);
                                userProfile.put("soyad", soyad);
                                userProfile.put("kullaniciadi", kullaniciadi);
                                userProfile.put("eposta", eposta);
                                userProfile.put("profil_foto", profil_foto);
                                userProfile.put("biyografi", null);

                                // Veritabanına güncellenmiş kullanıcı profili yaz
                                db.collection("users")
                                        .document(currentUserUid)
                                        .set(userProfile)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(BiyografiEkle.this, "Biyofrafi başarıyla Silindi", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(BiyografiEkle.this, MainActivity.class);

                                                startActivity(intent);
                                                finish();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(BiyografiEkle.this, "Biyofrafi silerken bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(BiyografiEkle.this, "Kullanıcı verisi bulunamadı", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(BiyografiEkle.this, "Kullanıcı oturumu açık değil", Toast.LENGTH_SHORT).show();
                }
            }
        });


        binding.onayla2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String biyo = binding.ediText.getText().toString();
                String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if (currentUserUid != null) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference userRef = db.collection("users").document(currentUserUid);
                    userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                // Kullanıcı verilerini al
                                String ad = documentSnapshot.getString("ad");
                                String soyad = documentSnapshot.getString("soyad");
                                String kullaniciadi = documentSnapshot.getString("kullaniciadi");
                                String eposta = documentSnapshot.getString("eposta");
                                String profil_foto = documentSnapshot.getString("profil_foto");

                                // Profil fotoğrafı güncellemesi
                                Map<String, Object> userProfile = new HashMap<>();
                                userProfile.put("ad", ad);
                                userProfile.put("soyad", soyad);
                                userProfile.put("kullaniciadi", kullaniciadi);
                                userProfile.put("eposta", eposta);
                                userProfile.put("profil_foto", profil_foto);
                                userProfile.put("biyografi", biyo);

                                // Veritabanına güncellenmiş kullanıcı profili yaz
                                db.collection("users")
                                        .document(currentUserUid)
                                        .set(userProfile)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(BiyografiEkle.this, "Biyofrafi başarıyla güncellendi", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(BiyografiEkle.this, MainActivity.class);

                                                startActivity(intent);
                                                finish();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(BiyografiEkle.this, "Biyofrafi güncellenirken bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(BiyografiEkle.this, "Kullanıcı verisi bulunamadı", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(BiyografiEkle.this, "Kullanıcı oturumu açık değil", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}
