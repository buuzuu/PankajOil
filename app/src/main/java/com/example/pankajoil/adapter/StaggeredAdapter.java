package com.example.pankajoil.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pankajoil.ProductPage;
import com.example.pankajoil.R;
import com.example.pankajoil.data.Product;
import com.example.pankajoil.utils.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class StaggeredAdapter extends RecyclerView.Adapter<StaggeredAdapter.StaggerViewHolder> implements Filterable {


    private List<Product> list;
    private List<Product> listFull;
    Context ctx;

    public StaggeredAdapter(Context ctx, List<Product> list) {
        this.ctx = ctx;
        this.list = list;
        listFull = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public StaggeredAdapter.StaggerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.staggered_item_layout,
                parent, false);
        return new StaggerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StaggeredAdapter.StaggerViewHolder holder, int position) {
        Product product = list.get(position);
        Picasso.get()
                .load(product.getGeneralUrl()).into(holder.image);
        holder.name.setText(product.getProductName());
        holder.price.setText( "₹"+ String.valueOf(product.getVariants().get(0).getPrice()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Product> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(listFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Product p : listFull) {
                    if (p.getProductName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(p);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
    class StaggerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        TextView name;
        TextView price;

        public StaggerViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View v) {
            Intent i = new Intent(ctx,ProductPage.class);
            i.putExtra("product", list.get(getAdapterPosition()));
            ctx.startActivity(i);

        }
    }
}
