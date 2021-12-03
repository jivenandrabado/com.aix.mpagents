package com.aix.mpagents.views.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.ItemProductVariantBinding;
import com.aix.mpagents.databinding.ItemProductsBinding;
import com.aix.mpagents.interfaces.ProductInterface;
import com.aix.mpagents.interfaces.VariantInterface;
import com.aix.mpagents.models.ProductInfo;
import com.aix.mpagents.models.Variant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class VariantsFirestoreAdapter extends FirestoreRecyclerAdapter<Variant, VariantAdapter.ViewHolder> {
    private VariantInterface variantInterface;
    private Context context;

    public VariantsFirestoreAdapter(@NonNull FirestoreRecyclerOptions<Variant> options, VariantInterface variantInterface) {
        super(options);
        this.variantInterface = variantInterface;
    }

    @Override
    protected void onBindViewHolder(@NonNull VariantAdapter.ViewHolder holder, int position, @NonNull Variant model) {
        holder.updateData(model, variantInterface);
    }

    @NonNull
    @Override
    public VariantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemProductVariantBinding binding = ItemProductVariantBinding.inflate(layoutInflater,parent,false);
        return new VariantAdapter.ViewHolder(binding);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
    }
}