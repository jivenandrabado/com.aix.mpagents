package com.aix.mpagents.views.fragments.products;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.FragmentCategoryBinding;
import com.aix.mpagents.interfaces.CategoryInterface;
import com.aix.mpagents.models.Category;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.view_models.ProductViewModel;
import com.aix.mpagents.views.adapters.CategoryFirestoreAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class CategoryFragment extends Fragment implements CategoryInterface {


    private FragmentCategoryBinding binding;
    private ProductViewModel productViewModel;
    private CategoryFirestoreAdapter categoryFirestoreAdapter;
    private NavController navController;
    private FirestoreRecyclerOptions firestoreRecyclerOptions;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        initProductsRecyclerView();

    }

    private void initProductsRecyclerView(){
        if(productViewModel.getSelectedProductType().getValue() != null){
            String product_type = productViewModel.getSelectedProductType().getValue().getName();
            ErrorLog.WriteDebugLog("PRODUCT TYPE SELECTED " +product_type);
            firestoreRecyclerOptions = productViewModel.getCategoriesRecyclerOptions(product_type);
            categoryFirestoreAdapter = new CategoryFirestoreAdapter(firestoreRecyclerOptions,this);
            categoryFirestoreAdapter.setHasStableIds(true);

            binding.recyclerViewCategory.setAdapter(categoryFirestoreAdapter);
            binding.recyclerViewCategory.setLayoutManager(new LinearLayoutManager(requireContext()));
            //temporary fix for recyclerview
            binding.recyclerViewCategory.setItemAnimator(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(categoryFirestoreAdapter!=null) {
            categoryFirestoreAdapter.startListening();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(categoryFirestoreAdapter!=null) {
            categoryFirestoreAdapter.stopListening();
        }
    }

    @Override
    public void onCategoryClick(Category category) {
        ErrorLog.WriteDebugLog("CATEGORY CLICK "+category.getCategory_name());
        productViewModel.getSelectedCategory().setValue(category);
        navController.popBackStack(R.id.categoryFragment,true);

    }
}