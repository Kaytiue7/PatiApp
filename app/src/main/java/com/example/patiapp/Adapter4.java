package com.example.patiapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.patiapp.databinding.RecyclerItemBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter4 extends RecyclerView.Adapter<Adapter4.PostHolder> {


    private ArrayList<Post> arrayList;

    public Adapter4(ArrayList<Post> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerItemBinding binding=RecyclerItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PostHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.ilanbaslik.setText(arrayList.get(position).ilanbaslik);
        holder.binding.ilanturu.setText(arrayList.get(position).ilanturu);
        holder.binding.sehir.setText(arrayList.get(position).sehir);
        holder.binding.tarih.setText(arrayList.get(position).date);
        Picasso.get().load(arrayList.get(position).dowloandurl).into(holder.binding.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.itemView.getContext(), IlanDuzenle.class);
                intent.putExtra("ilanbaslik",arrayList.get(position).ilanbaslik);
                intent.putExtra("ilanturu",arrayList.get(position).ilanturu);
                intent.putExtra("sehir",arrayList.get(position).sehir);
                intent.putExtra("date",arrayList.get(position).date);
                intent.putExtra("dowloandurl",arrayList.get(position).dowloandurl);
                holder.itemView.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
         return arrayList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{
        RecyclerItemBinding binding;
        public PostHolder(RecyclerItemBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
