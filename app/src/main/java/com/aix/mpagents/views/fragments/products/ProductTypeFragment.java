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


public class ProductTypeFragment extends Fragment implements ProductTypeInterface {

    private FragmentProductTypeBinding binding;
    private ProductViewModel productViewModel;
    private ProductTypeFirestoreAdapter productTypeFirestoreAdapter;
    private NavController navController;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductTypeBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);

        initProductType();

    }

    private void initProductType(){
        productTypeFirestoreAdapter = new ProductTypeFirestoreAdapter(productViewModel.getProductTypeRecyclerOptions(),this);
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
        productViewModel.getSelectedProductType().setValue(productType);
        navController.popBackStack(R.id.productTypeFragment,true);
    }
}