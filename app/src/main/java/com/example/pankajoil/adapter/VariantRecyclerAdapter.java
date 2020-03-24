package com.example.pankajoil.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pankajoil.R;
import com.example.pankajoil.data.Variant;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VariantRecyclerAdapter extends RecyclerView.Adapter<VariantRecyclerAdapter.VHolder> {
    List<Variant> variantList;
    final OnVariantClickListner listner;
    int checkedPosition = 0;

    public VariantRecyclerAdapter(List<Variant> variants, OnVariantClickListner l) {
        this.variantList = variants;
        this.listner = l;
    }

    @NonNull
    @Override
    public VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.variant_item_layout, null);
        return new VHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VHolder holder, int position) {
        holder.bind(variantList.get(position), listner);
    }

    @Override
    public int getItemCount() {
        return variantList.size();
    }

    public class VHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        RelativeLayout relativeLayout;


        public VHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image);
            textView = itemView.findViewById(R.id.item_text);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }

        void bind(Variant variant, final OnVariantClickListner listner) {

            Picasso.get().load(variant.getUrl()).into(imageView);
            textView.setText(variant.getSize() + " ℓ");
            if (checkedPosition == -1) {
                relativeLayout.setBackgroundColor(Color.WHITE);
            } else {
                if (checkedPosition == getAdapterPosition()) {
                    relativeLayout.setBackgroundColor(Color.RED);
                } else {
                    relativeLayout.setBackgroundColor(Color.WHITE);
                }
            }
            itemView.setOnClickListener(v -> {
                listner.onVariantClick(variantList.get(getAdapterPosition()));
                relativeLayout.setBackgroundColor(Color.RED);
                if (checkedPosition != getAdapterPosition()) {
                    notifyItemChanged(checkedPosition);
                    checkedPosition = getAdapterPosition();
                }

            });
        }

    }

//    public Variant getSelected() {
//        if (checkedPosition != -1) {
//            return variants.get(checkedPosition);
//        }
//        return null;
//    }


}
