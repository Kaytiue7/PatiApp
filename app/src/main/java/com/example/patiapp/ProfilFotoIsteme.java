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

public class ProfilFotoIsteme extends AppCompatActivity {

    private ActivityProfilFotoIstemeBinding binding;
    private FirebaseAuth auth;
    private Uri selectedImageUri = null;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;
    public String fotoyol;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfilFotoIstemeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth = FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance(); // FirebaseAuth instance'ını başlatma
        firebaseAuth = FirebaseAuth.getInstance(); // FirebaseAuth nesnesini oluştur
        firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        String useremail = user.getEmail();
        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        firebaseFirestore.collection("users").whereEqualTo("eposta", useremail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            if (documentSnapshot.exists()) {
                                Map<String, Object> data = documentSnapshot.getData();

                                String foto= (String) data.get("profil_foto");
                                firebaseFirestore.collection("UserPhoto")
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                                    if (snapshot.exists()) {
                                                        Map<String, Object> data = snapshot.getData();
                                                        fotoyol = (String) data.get("FotografYolu");
                                                        if (foto.equals(fotoyol)) {
                                                            binding.atlabutton.setVisibility(View.GONE);
                                                        } else {
                                                            Picasso.get().load(foto).into(binding.pp);
                                                        }


                                                    }
                                                }
                                            }
                                        });



                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Veriler yüklenemedi!");
                    }
                });


        binding.atlabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("UserPhoto")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                                    if (snapshot.exists()) {
                                        Map<String, Object> data = snapshot.getData();
                                        fotoyol = (String) data.get("FotografYolu");

                                    }
                                }
                            }
                        });

                String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

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

                            // Profil fotoğrafı güncellemesi
                            Map<String, Object> userProfile = new HashMap<>();
                            userProfile.put("ad", ad);
                            userProfile.put("soyad", soyad);
                            userProfile.put("kullaniciadi", kullaniciadi);
                            userProfile.put("eposta", eposta);
                            userProfile.put("profil_foto", fotoyol);

                            // Veritabanına güncellenmiş kullanıcı profili yaz
                            db.collection("users")
                                    .document(currentUserUid)
                                    .set(userProfile)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            DocumentReference userRef = db.collection("Ilanlar").document(currentUserUid);
                                            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot.exists()) {
                                                        // Profil fotoğrafı güncellemesi
                                                        Map<String, Object> userProfile = new HashMap<>();
                                                        userProfile.put("userpp", null);

                                                        // Veritabanına güncellenmiş kullanıcı profili yaz
                                                        db.collection("Ilanlar")
                                                                .document(currentUserUid)
                                                                .set(userProfile)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(ProfilFotoIsteme.this, "Profil fotoğrafı başarıyla silindi", Toast.LENGTH_SHORT).show();
                                                                        Intent intent = new Intent(ProfilFotoIsteme.this, BiyografiEkle.class);
                                                                        startActivity(intent);
                                                                        binding.atlabutton.setText("Devam Edebilirsiniz ");
                                                                        finish();
                                                                    }
                                                                })  // Noktalı virgül eklenmişti
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(ProfilFotoIsteme.this, "Profil fotoğrafı güncellenirken bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });  // Noktalı virgül eklenmişti
                                                    }
                                                }
                                            });  // Noktalı virgül eklenmişti
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ProfilFotoIsteme.this, "Profil fotoğrafı güncellenirken bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(ProfilFotoIsteme.this, "Kullanıcı verisi bulunamadı", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Intent intent = new Intent(ProfilFotoIsteme.this, MainActivity.class);
                startActivity(intent);

                finish();

            }




        });


        binding.pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Galeriye erişim için intent oluştur
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        binding.onayla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImageUri != null) {
                    Toast.makeText(ProfilFotoIsteme.this, "Bu işlem biraz uzun sürebilir", Toast.LENGTH_SHORT).show();
                    uploadImageToFirebaseStorage(selectedImageUri);
                    binding.onayla.setVisibility(View.INVISIBLE);
                    binding.atlabutton.setText("Devam Et");
                } else {
                    Toast.makeText(ProfilFotoIsteme.this, "Lütfen bir fotoğraf seçin", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            binding.pp.setImageURI(selectedImageUri);  // Seçilen resmi ImageView'da göster
        }
    }


    private void uploadImageToFirebaseStorage(Uri imageUri) {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profilephoto/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
            storageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            updateUserProfile(imageUrl);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfilFotoIsteme.this, "Fotoğraf yüklenirken bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void updateUserProfile(String imageUrl) {
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

                        // Profil fotoğrafı güncellemesi
                        Map<String, Object> userProfile = new HashMap<>();
                        userProfile.put("ad", ad);
                        userProfile.put("soyad", soyad);
                        userProfile.put("kullaniciadi", kullaniciadi);
                        userProfile.put("eposta", eposta);
                        userProfile.put("profil_foto", imageUrl);

                        // Veritabanına güncellenmiş kullanıcı profili yaz
                        db.collection("users")
                                .document(currentUserUid)
                                .set(userProfile)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        DocumentReference userRef = db.collection("Ilanlar").document(currentUserUid);
                                        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    // Profil fotoğrafı güncellemesi
                                                    Map<String, Object> userProfile = new HashMap<>();
                                                    userProfile.put("userpp", imageUrl);

                                                    // Veritabanına güncellenmiş kullanıcı profili yaz
                                                    db.collection("Ilanlar")
                                                            .document(currentUserUid)
                                                            .set(userProfile)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(ProfilFotoIsteme.this, "Profil fotoğrafı başarıyla güncellendi", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(ProfilFotoIsteme.this, BiyografiEkle.class);
                                                                    startActivity(intent);
                                                                    binding.atlabutton.setText("Devam Edebilirsiniz ");
                                                                    finish();
                                                                }
                                                            })  // Noktalı virgül eklenmişti
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(ProfilFotoIsteme.this, "Profil fotoğrafı güncellenirken bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });  // Noktalı virgül eklenmişti
                                                }
                                            }
                                        });  // Noktalı virgül eklenmişti
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ProfilFotoIsteme.this, "Profil fotoğrafı güncellenirken bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(ProfilFotoIsteme.this, "Kullanıcı verisi bulunamadı", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(ProfilFotoIsteme.this, "Kullanıcı oturumu açık değil", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(ProfilFotoIsteme.this, "Profil fotoğrafı başarıyla güncellendi", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ProfilFotoIsteme.this, MainActivity.class);
        startActivity(intent);
        binding.atlabutton.setText("Devam Edebilirsiniz ");
        finish();
    }
    private void atlaButton(String imageUrl) {
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

                        // Profil fotoğrafı güncellemesi
                        Map<String, Object> userProfile = new HashMap<>();
                        userProfile.put("ad", ad);
                        userProfile.put("soyad", soyad);
                        userProfile.put("kullaniciadi", kullaniciadi);
                        userProfile.put("eposta", eposta);
                        userProfile.put("profil_foto", imageUrl);

                        // Veritabanına güncellenmiş kullanıcı profili yaz
                        db.collection("users")
                                .document(currentUserUid)
                                .set(userProfile)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        DocumentReference userRef = db.collection("Ilanlar").document(currentUserUid);
                                        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    // Profil fotoğrafı güncellemesi
                                                    Map<String, Object> userProfile = new HashMap<>();
                                                    userProfile.put("userpp", imageUrl);

                                                    // Veritabanına güncellenmiş kullanıcı profili yaz
                                                    db.collection("Ilanlar")
                                                            .document(currentUserUid)
                                                            .set(userProfile)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(ProfilFotoIsteme.this, "Profil fotoğrafı başarıyla güncellendi", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(ProfilFotoIsteme.this, BiyografiEkle.class);
                                                                    startActivity(intent);
                                                                    binding.atlabutton.setText("Devam Edebilirsiniz ");
                                                                    finish();
                                                                }
                                                            })  // Noktalı virgül eklenmişti
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(ProfilFotoIsteme.this, "Profil fotoğrafı güncellenirken bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });  // Noktalı virgül eklenmişti
                                                }
                                            }
                                        });  // Noktalı virgül eklenmişti
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ProfilFotoIsteme.this, "Profil fotoğrafı güncellenirken bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(ProfilFotoIsteme.this, "Kullanıcı verisi bulunamadı", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(ProfilFotoIsteme.this, "Kullanıcı oturumu açık değil", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(ProfilFotoIsteme.this, "Profil fotoğrafı başarıyla güncellendi", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ProfilFotoIsteme.this, MainActivity.class);
        startActivity(intent);
        binding.atlabutton.setText("Devam Edebilirsiniz ");
        finish();
    }


}
