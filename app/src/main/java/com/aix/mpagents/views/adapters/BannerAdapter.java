package com.aix.mpagents.views.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aix.mpagents.databinding.ItemBannerBinding;
import com.bumptech.glide.Glide;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {

    private List<String> links;
    public BannerAdapter(List<String> links){
        this.links = links;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemBannerBinding binding = ItemBannerBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.updateUI(links.get(position));
    }

    @Override
    public int getItemCount() {
        return links.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemBannerBinding binding;
        public ViewHolder(@NonNull ItemBannerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void updateUI(String s) {
            Glide.with(binding.getRoot().getContext())
                    .load(s)
                    .into(binding.imageViewBanner);
        }
    }
}
