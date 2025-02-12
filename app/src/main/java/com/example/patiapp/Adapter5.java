package com.example.patiapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patiapp.databinding.RecyclerBarinakBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter5 extends RecyclerView.Adapter<Adapter5.PostHolder> {


    private ArrayList<Post4> arrayList;
    //bas

    public Adapter5(ArrayList<Post4> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerBarinakBinding binding=RecyclerBarinakBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PostHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.binding.textView10.setText(arrayList.get(position).kurumisim);
        Picasso.get().load(arrayList.get(position).profil_foto).into(holder.binding.imageView14);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(holder.itemView.getContext(), BarinakKullaniciDetay.class);

                intent.putExtra("acikadres",arrayList.get(position).acikadres);
                intent.putExtra("biyografi",arrayList.get(position).biyografi);
                intent.putExtra("eposta",arrayList.get(position).eposta);
                intent.putExtra("ilce",arrayList.get(position).ilce);
                intent.putExtra("kurumisim",arrayList.get(position).kurumisim);
                intent.putExtra("kurulusyili",arrayList.get(position).kurulusyili);
                intent.putExtra("profil_foto",arrayList.get(position).profil_foto);
                intent.putExtra("sehir",arrayList.get(position).sehir);
                intent.putExtra("telno",arrayList.get(position).telno);
                System.out.println("SOLOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO"+arrayList.get(position).telno);
                holder.itemView.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {

        return arrayList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{
        RecyclerBarinakBinding binding;
        public PostHolder(RecyclerBarinakBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
