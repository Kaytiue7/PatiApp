package com.example.patiapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patiapp.databinding.RecyclerItemYedekBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterYedek2 extends RecyclerView.Adapter<AdapterYedek2.PostHolder> {


    private ArrayList<Post6> arrayList;

    public AdapterYedek2(ArrayList<Post6> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemYedekBinding binding=RecyclerItemYedekBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PostHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.binding.textView15.setText(arrayList.get(position).baslik);
        holder.binding.textView21.setText(arrayList.get(position).ilanturu);
        holder.binding.textView30.setText(arrayList.get(position).date);
        holder.binding.textView32.setText(arrayList.get(position).sehir);
        holder.binding.textView31.setText(arrayList.get(position).username);
        Picasso.get().load(arrayList.get(position).foto).into(holder.binding.userpp);

        Picasso.get().load(arrayList.get(position).dowloandurl).into(holder.binding.imageView18);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayList.get(position).ilanturu.equals("Mama Bağışı")){
                    Intent intent=new Intent(holder.itemView.getContext(), MamaBagisiDetay.class);
                    intent.putExtra("ilanbaslik",arrayList.get(position).baslik);
                    intent.putExtra("ilanturu",arrayList.get(position).ilanturu);
                    intent.putExtra("sehir",arrayList.get(position).sehir);
                    intent.putExtra("hesapturu",arrayList.get(position).hesapturu);
                    intent.putExtra("date",arrayList.get(position).date);
                    intent.putExtra("dowloandurl",arrayList.get(position).dowloandurl);


                    holder.itemView.getContext().startActivity(intent);
                }
                else{
                    Intent intent=new Intent(holder.itemView.getContext(), IlanUpdate.class);
                    intent.putExtra("ilanbaslik", arrayList.get(position).baslik);
                    intent.putExtra("dowloandurl", arrayList.get(position).dowloandurl);
                    intent.putExtra("sehir", arrayList.get(position).sehir);
                    intent.putExtra("ilanturu", arrayList.get(position).ilanturu);
                    intent.putExtra("foto", arrayList.get(position).foto);
                    intent.putExtra("username", arrayList.get(position).username);
                    intent.putExtra("hesapturu", arrayList.get(position).hesapturu);
                    intent.putExtra("hayvankategori", arrayList.get(position).hayvankategori);
                    intent.putExtra("hayvancinsi", arrayList.get(position).hayvancinsi);
                    intent.putExtra("cinsiyet", arrayList.get(position).cinsiyet);
                    intent.putExtra("yas", arrayList.get(position).yas);
                    intent.putExtra("saglikdurumu", arrayList.get(position).saglikdurumu);
                    intent.putExtra("aciklama", arrayList.get(position).aciklama);
                    intent.putExtra("ilce", arrayList.get(position).ilce);
                    intent.putExtra("telno", arrayList.get(position).telno);
                    intent.putExtra("date", arrayList.get(position).date);
                    holder.itemView.getContext().startActivity(intent);
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{
        RecyclerItemYedekBinding binding;
        public PostHolder(RecyclerItemYedekBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
