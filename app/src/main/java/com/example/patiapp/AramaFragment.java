package com.example.patiapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.patiapp.databinding.FragmentAramaBinding;
import com.example.patiapp.databinding.FragmentIlanlarBinding;
import com.example.patiapp.databinding.FragmentKullaniciAramaBinding;

public class AramaFragment extends Fragment {

    private FragmentAramaBinding binding;
    String secilenHayvan;
    String secilenTur;
    String secilenCins;
    String secilenSehir;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAramaBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ilan türleri
        String[] ilanturu = getResources().getStringArray(R.array.ilanTurListele);
        ArrayAdapter<String> adapterItems = new ArrayAdapter<>(getContext(), R.layout.dropdown_item,ilanturu);
        binding.autoCompleteTextView2.setAdapter(adapterItems);
        binding.autoCompleteTextView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                secilenTur=item;

            }
        });

        binding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KullaniciArama FragmentKullaniciAramaBinding = new KullaniciArama();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, FragmentKullaniciAramaBinding)
                        .addToBackStack(null)
                        .commit();
            }
        });
        binding.button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*/
                BarinakArama FragmentBarinakArama2Binding = new BarinakArama();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, FragmentBarinakArama2Binding)
                        .addToBackStack(null)
                        .commit();

                 */
                Intent intent = new Intent(getContext(),BarinakArama.class);
                startActivity(intent);

            }
        });


        //kategori
        String[] hayvankategori = getResources().getStringArray(R.array.kategoriListele);
        ArrayAdapter<String> adapterItems2 = new ArrayAdapter<>(getContext(), R.layout.dropdown_item,hayvankategori);
        binding.kategoritext.setAdapter(adapterItems2);
        binding.kategoritext.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                secilenHayvan=item;
                //kategori ile beraber çalışan hayvan cinsi kodları
                updateAnimalSpecificInfo();
            }
        });



        //Şehir
        String[] sehir = getResources().getStringArray(R.array.ilListeleme);
        ArrayAdapter<String> adapterItems4 = new ArrayAdapter<>(getContext(), R.layout.dropdown_item,sehir);
        binding.autoCompleteTextView4.setAdapter(adapterItems4);
        binding.autoCompleteTextView4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                secilenSehir=item;

            }
        });


        binding.ilangor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (secilenTur==null){
                    secilenTur="bos";
                }
                if (secilenHayvan==null){
                    secilenHayvan="bos";
                }
                if (secilenCins==null){
                    secilenCins="bos";
                }
                if (secilenSehir==null){
                    secilenSehir="bos";
                }
                if (secilenCins.equals("bos")&&secilenTur.equals("bos")&&secilenHayvan.equals("bos")&&secilenSehir.equals("bos")){
                    Toast.makeText(getContext(), "En az bir uygulanacak filtre seçiniz", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(getContext(),FiltrelenmisSonuclar.class);
                    intent.putExtra("secilenTur",secilenTur);
                    intent.putExtra("secilenSehir",secilenSehir);
                    intent.putExtra("secilenCins",secilenCins);
                    intent.putExtra("secilenHayvan",secilenHayvan);
                    startActivity(intent);
                }
            }
        });
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
                String[] hayvancinsi = getResources().getStringArray(R.array.KopekcinsListele);
                ArrayAdapter<String> adapterItems3 = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, hayvancinsi);
                binding.autoCompleteTextView3.setAdapter(adapterItems3);
                binding.autoCompleteTextView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        String item = adapterView.getItemAtPosition(position).toString();
                        System.out.println("Seçilen cins: " + item);
                        secilenCins=item;
                    }
                });
                //KÖPEK TÜRLERİ
            } else if (secilenHayvan.equals("Kedi")) {
                System.out.println("Seçilen hayvan: " + secilenHayvan);
                // Kediler için özel işlemler

                String[] hayvancinsi = getResources().getStringArray(R.array.KedicinsListele);
                ArrayAdapter<String> adapterItems3 = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, hayvancinsi);
                binding.autoCompleteTextView3.setAdapter(adapterItems3);
                binding.autoCompleteTextView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        String item = adapterView.getItemAtPosition(position).toString();
                        System.out.println("Seçilen cins: " + item);
                        secilenCins=item;
                    }
                });
            }
        }
        else{
            binding.myTextInputLayout.setEnabled(false); // TextInputLayout'u devre dışı bırak
        }



    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }




}