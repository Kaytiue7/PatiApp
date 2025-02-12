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

public class AdapterYedek extends RecyclerView.Adapter<AdapterYedek.PostHolder> {


    private ArrayList<Post> arrayList;

    public AdapterYedek(ArrayList<Post> arrayList) {
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

        holder.binding.textView15.setText(arrayList.get(position).ilanbaslik);
        holder.binding.textView21.setText(arrayList.get(position).ilanturu);
        holder.binding.textView30.setText(arrayList.get(position).date);
        holder.binding.textView32.setText(arrayList.get(position).sehir);
        holder.binding.textView31.setText(arrayList.get(position).foto);

        Picasso.get().load(arrayList.get(position).dowloandurl).into(holder.binding.imageView18);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayList.get(position).ilanturu.equals("Mama Bağışı")){
                    Intent intent=new Intent(holder.itemView.getContext(), MamaBagisiDetay.class);
                    intent.putExtra("ilanbaslik",arrayList.get(position).ilanbaslik);
                    intent.putExtra("ilanturu",arrayList.get(position).ilanturu);
                    intent.putExtra("sehir",arrayList.get(position).sehir);
                    intent.putExtra("hesapturu",arrayList.get(position).hesapturu);
                    intent.putExtra("date",arrayList.get(position).date);
                    intent.putExtra("dowloandurl",arrayList.get(position).dowloandurl);

                    holder.itemView.getContext().startActivity(intent);
                }
                else{
                    Intent intent=new Intent(holder.itemView.getContext(), IlanDetay.class);
                    intent.putExtra("ilanbaslik",arrayList.get(position).ilanbaslik);
                    intent.putExtra("ilanturu",arrayList.get(position).ilanturu);
                    intent.putExtra("sehir",arrayList.get(position).sehir);
                    intent.putExtra("date",arrayList.get(position).date);
                    intent.putExtra("hesapturu",arrayList.get(position).hesapturu);
                    intent.putExtra("dowloandurl",arrayList.get(position).dowloandurl);
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
