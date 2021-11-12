package com.aix.mpagents.views.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aix.mpagents.databinding.ItemProductTypeBinding;
import com.aix.mpagents.interfaces.ProductTypeInterface;
import com.aix.mpagents.models.ProductType;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ProductTypeFirestoreAdapter extends FirestoreRecyclerAdapter<ProductType,ProductTypeFirestoreAdapter.ViewHolder> {

    private ProductTypeInterface productTypeInterface;

    public ProductTypeFirestoreAdapter(@NonNull FirestoreRecyclerOptions<ProductType> options, ProductTypeInterface productTypeInterface) {
        super(options);
        this.productTypeInterface = productTypeInterface;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemProductTypeBinding binding = ItemProductTypeBinding.inflate(layoutInflater,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ProductType model) {
        holder.binding.setProductType(model);
        holder.binding.setProductTypeInterface(productTypeInterface);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemProductTypeBinding binding;
        public ViewHolder(ItemProductTypeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
