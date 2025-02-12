package com.example.patiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.patiapp.databinding.ActivityBarinakHesapOlusturBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BarinakHesapOlustur extends AppCompatActivity {
    private ActivityBarinakHesapOlusturBinding binding;
    private FirebaseAuth auth;
    String secilenSehir;
    private Uri selectedImageUri = null;  // Seçilen resmin URI'sini saklamak için
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBarinakHesapOlusturBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth = FirebaseAuth.getInstance();
        initDatePicker();
        binding.editTextDate.setText(getTodaysDate());
        binding.imageView15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);  // 1, bu işlem için tanımlanmış request kodudur
            }
        });

        //Şehir
        String[] sehir = getResources().getStringArray(R.array.ilListeleme);
        ArrayAdapter<String> adapterItems4 = new ArrayAdapter<>(BarinakHesapOlustur.this, R.layout.dropdown_item,sehir);
        binding.autoCompleteTextView2.setAdapter(adapterItems4);
        binding.autoCompleteTextView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                secilenSehir=item;

            }
        });
        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }


    private String getTodaysDate() {
        Calendar calendar=Calendar.getInstance();
        int year= calendar.get(Calendar.YEAR);
        int month= calendar.get(Calendar.MONTH);
        month+=1;
        int day= calendar.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day,month,year);
    }

    public void kayit(View view) {
        String kurumisim = binding.editTextText8.getText().toString().trim();
        String kurulusyili = binding.editTextDate.getText().toString().trim();
        String biyografi = binding.editTextText15.getText().toString().trim();
        String eposta = binding.editTextTextEmailAddress2.getText().toString().trim();
        String sifre = binding.editTextTextPassword.getText().toString().trim();
        String sehir = secilenSehir;
        String ilce = binding.editTextText18.getText().toString().trim();
        String acikadres = binding.editTextText20.getText().toString().trim();
        String telno = binding.editTextPhone.getText().toString().trim();

        if (kurumisim.isEmpty() || kurulusyili.isEmpty() || biyografi.isEmpty() || eposta.isEmpty() || sifre.isEmpty() || telno.isEmpty() || sehir.isEmpty() || acikadres.isEmpty() || ilce.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Tüm alanları doldurduğunuzdan ve bir profil fotoğrafı seçtiğinizden emin olun", Toast.LENGTH_SHORT).show();
        } else {
            uploadImageToFirebaseStorage(selectedImageUri);

            Intent intent=new Intent(BarinakHesapOlustur.this,HesapGiris.class);
            startActivity(intent);
        }
    }
    public void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month+=1;
                String date=makeDateString(day,month,year);
                binding.editTextDate.setText(date);


            }


        };
        Calendar calendar=Calendar.getInstance();
        int year= calendar.get(Calendar.YEAR);
        int month= calendar.get(Calendar.MONTH);
        int day= calendar.get(Calendar.DAY_OF_MONTH);

        int style= AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog=new DatePickerDialog(this,style,dateSetListener,year,month,day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());


    }
    public void openDatePicker(View view){
        datePickerDialog.show();


    }
    private String makeDateString(int day, int month, int year) {
        return getMounthFormat(month) + " "+day+" "+ year;
    }
    public String getMounthFormat(int month){
        if (month==1){
            return "Ocak";
        }
        if (month==2){
            return "Şubat";
        }
        if (month==3){
            return "Mart";
        } if (month==4){
            return "Nisan";
        }
        if (month==5){
            return "Mayıs";
        }
        if (month==6){
            return "Haziran";
        }
        if (month==7){
            return "Temmuz";
        }
        if (month==8){
            return "Ağustos";
        }
        if (month==9){
            return "Eylül";
        }
        if (month==10){
            return "Ekim";
        }
        if (month==11){
            return "Kasım";
        }
        if (month==12){
            return "Aralık";
        }
        return "Mayıs";

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            binding.imageView15.setImageURI(selectedImageUri);  // Seçilen resmi ImageView'da göster
        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profilephoto/" + UUID.randomUUID().toString());
        storageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        createUserProfile(imageUrl);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(BarinakHesapOlustur.this, "Fotoğraf yüklenirken bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createUserProfile(String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("kurumisim", binding.editTextText8.getText().toString().trim());
        userProfile.put("kurulusyili", binding.editTextDate.getText().toString().trim());
        userProfile.put("biyografi", binding.editTextText15.getText().toString().trim());
        userProfile.put("eposta", binding.editTextTextEmailAddress2.getText().toString().trim());
        userProfile.put("sifre", binding.editTextTextPassword.getText().toString().trim());
        userProfile.put("sehir", secilenSehir);
        userProfile.put("ilce", binding.editTextText18.getText().toString().trim());
        userProfile.put("acikadres", binding.editTextText20.getText().toString().trim());
        userProfile.put("telno", binding.editTextPhone.getText().toString().trim());
        userProfile.put("profil_foto", imageUrl);  // Fotoğraf URL'si ekleniyor

        auth.createUserWithEmailAndPassword(binding.editTextTextEmailAddress2.getText().toString().trim(), binding.editTextTextPassword.getText().toString().trim())
                .addOnSuccessListener(authResult -> db.collection("Barinak").document(authResult.getUser().getUid())
                        .set(userProfile)
                        .addOnSuccessListener(aVoid -> {
                            Intent intent = new Intent(BarinakHesapOlustur.this, HesapGiris.class);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(BarinakHesapOlustur.this, "Veri kaydedilirken bir hata oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show())
                )
                .addOnFailureListener(e -> Toast.makeText(BarinakHesapOlustur.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
    }
    public void back(View view){
        Intent intent=new Intent(BarinakHesapOlustur.this,HesapKayitSorgu.class);
        startActivity(intent);
        finish();
    }

}