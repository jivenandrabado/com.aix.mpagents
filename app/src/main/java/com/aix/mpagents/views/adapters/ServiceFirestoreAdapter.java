package com.aix.mpagents.views.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.ItemServiceBinding;
import com.aix.mpagents.interfaces.ProductInterface;
import com.aix.mpagents.interfaces.ServiceInterface;
import com.aix.mpagents.models.ServiceInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ServiceFirestoreAdapter extends FirestoreRecyclerAdapter<ServiceInfo, ServiceFirestoreAdapter.ViewHolder> {
    private ServiceInterface serviceInterface;
    private Context context;

    public ServiceFirestoreAdapter(@NonNull FirestoreRecyclerOptions<ServiceInfo> options, ServiceInterface serviceInterface, Context context) {
        super(options);
        this.serviceInterface = serviceInterface;
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ServiceInfo model) {
        holder.updateData(model, serviceInterface);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemServiceBinding binding = ItemServiceBinding.inflate(layoutInflater,parent,false);
        return new ViewHolder(binding);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemServiceBinding binding;
        public ViewHolder(ItemServiceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void updateData(ServiceInfo service, ServiceInterface serviceInterface) {
            binding.setServiceInfo(service);
            binding.setServicesInterface(serviceInterface);
//        holder.binding.ratingBar.setRating(model.getRating());
//            binding.imageMoreOptionsProduct.setVisibility(
//                    product.getProduct_status().equalsIgnoreCase(ProductInfo.Status.INACTIVE) ?
//                            View.INVISIBLE : View.VISIBLE
//            );

            Glide.with(binding.getRoot().getContext()).load(Uri.parse(service.getPreview_image()))
//                .apply(requestOptions)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerInside()
                    .error(R.drawable.ic_baseline_photo_24).into(binding.imageView);
        }
    }

    @Override
    public void onDataChanged() {
        if(getItemCount() == 0) serviceInterface.onEmptyProduct();
        else serviceInterface.onNotEmptyProduct();
        super.onDataChanged();
    }
}