package com.aix.mpagents.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aix.mpagents.databinding.ItemProductVariantBinding;
import com.aix.mpagents.interfaces.VariantInterface;
import com.aix.mpagents.models.Variant;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;
import java.util.List;

public class VariantAdapter extends RecyclerView.Adapter<VariantAdapter.ViewHolder> {

    private VariantInterface variantInterface;
    private Context context;
    private List<Variant> variants;

    public VariantAdapter(VariantInterface variantInterface, List<Variant> variants) {
        this.variantInterface = variantInterface;
        this.variants = variants;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemProductVariantBinding binding = ItemProductVariantBinding.inflate(layoutInflater,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.updateData(variants.get(position), variantInterface, position);
    }

    @Override
    public int getItemCount() {
        return variants.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemProductVariantBinding binding;
        public ViewHolder(ItemProductVariantBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void updateData(Variant variant, VariantInterface variantInterface, int position) {
            binding.setVariant(variant);
            binding.setVariantInterface(variantInterface);
            binding.getRoot().setOnClickListener(v ->
                    variantInterface.onVariantClick(position,variant));
        }

        public void updateData(Variant variant, VariantInterface variantInterface) {
            binding.setVariant(variant);
            binding.setVariantInterface(variantInterface);
            binding.getRoot().setOnClickListener(v ->
                    variantInterface.onVariantClick(variant));
        }
    }
}