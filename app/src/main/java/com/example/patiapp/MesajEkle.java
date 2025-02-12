package com.example.patiapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.patiapp.databinding.ActivityIlanYuklemeBinding;
import com.example.patiapp.databinding.ActivityMesajEkleBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MesajEkle extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    public String kullaniciEposta, username, telefon, gonderenemail, profil_picture;
    private ActivityMesajEkleBinding binding;
    Uri ImageData;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> izin;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMesajEkleBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        // Kullanıcı adını aldım
        kullaniciEposta = firebaseAuth.getCurrentUser().getEmail();
        firebaseFirestore = FirebaseFirestore.getInstance(); // Burada düzeltme yapıldı

        firebaseFirestore.collection("users").whereEqualTo("eposta", kullaniciEposta)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            if (snapshot.exists()) {
                                Map<String, Object> data = snapshot.getData();
                                //  ID= snapshot.getId();
                                username = (String) data.get("kullaniciadi");
                                System.out.println(username);
                                profil_picture = (String) data.get("profil_foto");
                            } else {
                                Toast.makeText(MesajEkle.this, "Belirtilen kriterlere uygun ilan bulunamadı.", Toast.LENGTH_SHORT).show();
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
                                //  ID= snapshot.getId();
                                username = (String) data.get("kurumisim");
                                System.out.println(username);
                                profil_picture = (String) data.get("profil_foto");
                            } else {
                                Toast.makeText(MesajEkle.this, "Belirtilen kriterlere uygun ilan bulunamadı.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = firebaseStorage.getReference();

        Intent intent = getIntent();
        String yanıtlanmısmesaj = intent.getStringExtra("gidenveri");

        if (yanıtlanmısmesaj != null) {
            binding.editTextText5.setText(yanıtlanmısmesaj);
            binding.editTextText5.setEnabled(false);
        }
    }

    public void mesajj(View view) {


        //universal uniq id

        String alici = binding.editTextText5.getText().toString();
        String mesajbaslik = binding.editTextText6.getText().toString();
        String mesaj = binding.editTextText.getText().toString();

        HashMap<String, Object> ilanData = new HashMap<>();

        ilanData.put("mesajbaslik", mesajbaslik);
        ilanData.put("mesaj", mesaj);
        ilanData.put("username", username);
        ilanData.put("gonderenemail", kullaniciEposta);
        ilanData.put("alici", alici);
        ilanData.put("profil_picture", profil_picture);
        ilanData.put("onClick", "false");
        ilanData.put("date", FieldValue.serverTimestamp());

        firebaseFirestore.collection("Messages").add(ilanData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                Intent intent = new Intent(MesajEkle.this, MainActivity.class);
                // Verileri intent ile MesajEkle aktivitesine gönder
                startActivity(intent);
                finish();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MesajEkle.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
