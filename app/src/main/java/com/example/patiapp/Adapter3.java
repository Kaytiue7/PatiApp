package com.example.patiapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patiapp.databinding.RecyclerKullaniciBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter3 extends RecyclerView.Adapter<Adapter3.PostHolder> {


    private ArrayList<Post3> arrayList;

    public Adapter3(ArrayList<Post3> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerKullaniciBinding binding=RecyclerKullaniciBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PostHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.binding.textView10.setText(arrayList.get(position).username);
        Picasso.get().load(arrayList.get(position).profil_foto).into(holder.binding.imageView14);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.itemView.getContext(), KullaniciDetay.class);
                intent.putExtra("username",arrayList.get(position).username);
                intent.putExtra("ad",arrayList.get(position).ad);
                intent.putExtra("soyad",arrayList.get(position).soyad);
                holder.itemView.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {

        return arrayList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{
        RecyclerKullaniciBinding binding;
        public PostHolder(RecyclerKullaniciBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
