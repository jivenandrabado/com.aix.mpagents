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
import com.aix.mpagents.databinding.FragmentProductTypeBinding;
import com.aix.mpagents.interfaces.ProductTypeInterface;
import com.aix.mpagents.models.ProductType;
import com.aix.mpagents.view_models.ProductViewModel;
import com.aix.mpagents.views.adapters.ProductTypeFirestoreAdapter;
import com.aix.mpagents.views.fragments.base.BaseProductFragment;


public class ProductTypeFragment extends BaseProductFragment implements ProductTypeInterface {

    private FragmentProductTypeBinding binding;

    private ProductTypeFirestoreAdapter productTypeFirestoreAdapter;

    public ProductTypeFragment() {
        super(R.layout.fragment_product_type);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentProductTypeBinding.bind(getView());

        initProductType();

    }

    private void initProductType(){
        productTypeFirestoreAdapter = new ProductTypeFirestoreAdapter(getProductViewModel().getProductTypeRecyclerOptions(),this);

        productTypeFirestoreAdapter.setHasStableIds(true);

        binding.recyclerViewProductType.setAdapter(productTypeFirestoreAdapter);

        binding.recyclerViewProductType.setLayoutManager(new LinearLayoutManager(requireContext()));
        //temporary fix for recyclerview
        binding.recyclerViewProductType.setItemAnimator(null);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(productTypeFirestoreAdapter!=null) {
            productTypeFirestoreAdapter.startListening();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(productTypeFirestoreAdapter!=null) {
            productTypeFirestoreAdapter.stopListening();
        }
    }

    @Override
    public void onProductTypeClick(ProductType productType) {
        getProductViewModel().getSelectedProductType().setValue(productType);
        navController.popBackStack(R.id.productTypeFragment,true);
    }
}