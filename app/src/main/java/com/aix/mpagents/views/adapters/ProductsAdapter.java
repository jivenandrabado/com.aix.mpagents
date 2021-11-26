package com.aix.mpagents.views.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.ItemProductsBinding;
import com.aix.mpagents.interfaces.ProductInterface;
import com.aix.mpagents.models.ProductInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsFirestoreAdapter.ViewHolder> {

    private List<ProductInfo> products;
    private ProductInterface productInterface;
    private Context context;

    public ProductsAdapter(List<ProductInfo> products, ProductInterface productInterface, Context context){
        this.products = products;
        this.productInterface = productInterface;
        this.context = context;

    }
    @NonNull
    @Override
    public ProductsFirestoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemProductsBinding binding = ItemProductsBinding.inflate(layoutInflater,parent,false);
        return new ProductsFirestoreAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsFirestoreAdapter.ViewHolder holder, int position) {
        ProductInfo product = products.get(position);
        holder.updateData(product, productInterface);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }


}
