package com.example.quizapp4;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Dog_recyclerviewadapter extends RecyclerView.Adapter<Dog_recyclerviewadapter.MyViewHolder> {

    private Context context;
    private ArrayList<gallerymodel> gallerymodels;

    public Dog_recyclerviewadapter(Context context, ArrayList<gallerymodel> gallerymodels) {
        this.context = context;
        this.gallerymodels = gallerymodels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate de layout voor de rij in de RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        gallerymodel item = gallerymodels.get(position);
        holder.textViewName.setText(item.getNameOfDog());

        // Controleer of er een URI aanwezig is; zo niet, gebruik de drawable resource.
        if (item.getImageUri() != null) {
            holder.imageView.setImageURI(item.getImageUri());
        } else {
            holder.imageView.setImageResource(item.getImageResource());
        }

        // Stel een clicklistener in op de delete-knop om het item te verwijderen
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    // Verwijder het item uit de lijst en update de adapter
                    gallerymodels.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    notifyItemRangeChanged(currentPosition, gallerymodels.size());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return gallerymodels.size();
    }

    // ViewHolder-klasse om de rij-layout te beheren
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewName;
        Button buttonDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewName = itemView.findViewById(R.id.textView);
            buttonDelete = itemView.findViewById(R.id.buttondelete);
        }
    }
}
