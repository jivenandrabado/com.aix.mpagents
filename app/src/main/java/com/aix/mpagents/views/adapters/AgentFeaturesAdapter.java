package com.aix.mpagents.views.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aix.mpagents.databinding.ItemAgentFeaturesBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentFeaturesAdapter extends RecyclerView.Adapter<AgentFeaturesAdapter.ViewHolder> {
    private List<Map.Entry<String,Integer>> features = new ArrayList<>();

    public AgentFeaturesAdapter(HashMap<String, Integer> features) {
        this.features.addAll(features.entrySet());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemAgentFeaturesBinding binding = ItemAgentFeaturesBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map.Entry<String,Integer> feature = features.get(position);
        holder.featureImage.setImageResource(feature.getValue());
        holder.featureLabel.setText(feature.getKey());
    }

    @Override
    public int getItemCount() {
        return features.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView featureImage;
        TextView featureLabel;
        public ViewHolder(@NotNull ItemAgentFeaturesBinding binding) {
            super(binding.getRoot());
            featureLabel = binding.textViewFeatureLabel;
            featureImage = binding.imageViewFeatureImage;
        }
    }
}
