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
import com.aix.mpagents.databinding.FragmentProductListBinding;
import com.aix.mpagents.interfaces.ProductInterface;
import com.aix.mpagents.models.ProductInfo;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.view_models.ProductViewModel;
import com.aix.mpagents.views.adapters.ProductsFirestoreAdapter;


public class ProductListFragment extends Fragment implements ProductInterface {

    private FragmentProductListBinding binding;
    private ProductViewModel productViewModel;
    private ProductsFirestoreAdapter productsFirestoreAdapter;
    private ProductsBottomSheetDialog productsBottomSheetDialog;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProductListBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
        navController = Navigation.findNavController(view);
        initProductsRecyclerView();
    }

    private void initProductsRecyclerView(){
        productsFirestoreAdapter = new ProductsFirestoreAdapter(productViewModel.getProductRecyclerOptions(),this,requireContext());
        productsFirestoreAdapter.setHasStableIds(true);

        binding.recyclerViewProducts.setAdapter(productsFirestoreAdapter);
        binding.recyclerViewProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        //temporary fix for recyclerview
        binding.recyclerViewProducts.setItemAnimator(null);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(productsFirestoreAdapter!=null) {
            productsFirestoreAdapter.startListening();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(productsFirestoreAdapter!=null) {
            productsFirestoreAdapter.stopListening();
        }
    }

    @Override
    public void onProductClick(ProductInfo productInfo) {
        ErrorLog.WriteDebugLog("On Product Click "+ productInfo.getProduct_name());
        productViewModel.getSelectedProduct().setValue(productInfo);
        productsBottomSheetDialog = new ProductsBottomSheetDialog(this);
        productsBottomSheetDialog.show(getChildFragmentManager(),"PRODUCTS BOTTOM SHEET DIALOG");

    }

    @Override
    public void onEditProduct(ProductInfo productInfo) {
        productsBottomSheetDialog.dismiss();
        navController.navigate(R.id.action_productListFragment_to_editProductFragment);
    }
}