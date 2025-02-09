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
        // Inflate the layout for the row in the RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        gallerymodel item = gallerymodels.get(position);
        holder.textViewName.setText(item.getNameOfDog());

        // Check if a URI is available; if not, use the drawable resource.
        if (item.getImageUri() != null) {
            holder.imageView.setImageURI(item.getImageUri());
        } else {
            holder.imageView.setImageResource(item.getImageResource());
        }

        // Set a click listener on the delete button to remove the item.
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    // Remove the item from the list and update the adapter.
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

    // ViewHolder class to manage the row layout.
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
