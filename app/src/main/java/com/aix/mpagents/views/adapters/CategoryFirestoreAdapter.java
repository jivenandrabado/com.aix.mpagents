package com.aix.mpagents.views.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aix.mpagents.databinding.ItemCategoryBinding;
import com.aix.mpagents.interfaces.CategoryInterface;
import com.aix.mpagents.models.Category;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class CategoryFirestoreAdapter extends FirestoreRecyclerAdapter<Category,CategoryFirestoreAdapter.ViewHolder> {

    private CategoryInterface categoryInterface;

    public CategoryFirestoreAdapter(@NonNull FirestoreRecyclerOptions<Category> options, CategoryInterface categoryInterface) {
        super(options);
        this.categoryInterface = categoryInterface;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Category model) {

        holder.binding.setCategory(model);
        holder.binding.setCategoryInterface(categoryInterface);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(layoutInflater,parent,false);
        return new ViewHolder(binding);
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCategoryBinding binding;
        public ViewHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
