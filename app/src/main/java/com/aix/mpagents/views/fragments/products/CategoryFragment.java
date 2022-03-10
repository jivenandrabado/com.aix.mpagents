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
import com.aix.mpagents.view_models.ServiceViewModel;
import com.aix.mpagents.views.adapters.CategoryFirestoreAdapter;
import com.aix.mpagents.views.fragments.base.BaseFragment;
import com.aix.mpagents.views.fragments.base.BaseProductFragment;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class CategoryFragment extends BaseProductFragment implements CategoryInterface {

    private FragmentCategoryBinding binding;

    private ServiceViewModel serviceViewModel;

    private CategoryFirestoreAdapter categoryFirestoreAdapter;

    private FirestoreRecyclerOptions firestoreRecyclerOptions;

    private String productType = "";

    public CategoryFragment() {
        super(R.layout.fragment_category);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentCategoryBinding.bind(getView());

        serviceViewModel = new ViewModelProvider(requireActivity()).get(ServiceViewModel.class);

        productType = getArguments().getString("product_type");

        initProductsRecyclerView();
    }

    private void initProductsRecyclerView(){
        ErrorLog.WriteDebugLog("PRODUCT TYPE SELECTED " +productType);
        firestoreRecyclerOptions = getProductViewModel().getCategoriesRecyclerOptions(productType);

        categoryFirestoreAdapter = new CategoryFirestoreAdapter(firestoreRecyclerOptions,this);

        categoryFirestoreAdapter.setHasStableIds(true);

        binding.recyclerViewCategory.setAdapter(categoryFirestoreAdapter);

        binding.recyclerViewCategory.setLayoutManager(new LinearLayoutManager(requireContext()));
        //temporary fix for recyclerview
        binding.recyclerViewCategory.setItemAnimator(null);
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
        if(productType.equalsIgnoreCase("Product"))
        getProductViewModel().getSelectedCategory().setValue(category);
        else serviceViewModel.getSelectedCategory().setValue(category);
        navController.popBackStack(R.id.categoryFragment,true);
    }
}