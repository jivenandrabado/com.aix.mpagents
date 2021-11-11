package com.aix.mpagents.views.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.ItemImageviewBinding;
import com.aix.mpagents.interfaces.EditProductInterface;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class EditProductPhotoViewAdapter extends RecyclerView.Adapter<EditProductPhotoViewAdapter.ViewHolder> {

    private Context context;
    private List<String> photo_path = new ArrayList<>();
    private EditProductInterface editProductInterface;

    public EditProductPhotoViewAdapter(List<String> photo_path, Context context, EditProductInterface editProductInterface) {
        this.photo_path = photo_path;
        this.context = context;
        this.editProductInterface = editProductInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemImageviewBinding binding = ItemImageviewBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(Uri.parse(photo_path.get(position)))
//                .apply(requestOptions)
//                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .error(R.drawable.ic_baseline_photo_24).into(holder.imageView);

        holder.imageButtonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProductInterface.onImageRemove(holder.getAbsoluteAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return photo_path.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton imageButtonRemove;

        public ViewHolder(@NonNull ItemImageviewBinding binding) {
            super(binding.getRoot());
            imageView = binding.imageViewPhoto;
            imageButtonRemove = binding.imageButtonRemove;
        }
    }
}