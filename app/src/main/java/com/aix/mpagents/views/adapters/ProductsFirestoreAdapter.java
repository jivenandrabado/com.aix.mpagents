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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ProductsFirestoreAdapter extends FirestoreRecyclerAdapter<ProductInfo, ProductsFirestoreAdapter.ViewHolder> {
    private ProductInterface productInterface;
    private Context context;

    public ProductsFirestoreAdapter(@NonNull FirestoreRecyclerOptions<ProductInfo> options, ProductInterface productInterface, Context context) {
        super(options);
        this.productInterface = productInterface;
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ProductInfo model) {
        holder.updateData(model, productInterface);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemProductsBinding binding = ItemProductsBinding.inflate(layoutInflater,parent,false);
        return new ViewHolder(binding);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemProductsBinding binding;
        public ViewHolder(ItemProductsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void updateData(ProductInfo product, ProductInterface productInterface) {
            binding.setProductInfo(product);
            binding.setProductInterface(productInterface);
//        holder.binding.ratingBar.setRating(model.getRating());
//            binding.imageMoreOptionsProduct.setVisibility(
//                    product.getProduct_status().equalsIgnoreCase(ProductInfo.Status.INACTIVE) ?
//                            View.INVISIBLE : View.VISIBLE
//            );

            Glide.with(binding.getRoot().getContext()).load(Uri.parse(product.getPreview_image()))
//                .apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerInside()
                    .error(R.drawable.ic_baseline_photo_24).into(binding.imageView);
        }
    }

    @Override
    public void onDataChanged() {
        if(getItemCount() == 0) productInterface.onEmptyProduct();
        else productInterface.onNotEmptyProduct();
        super.onDataChanged();
    }
}