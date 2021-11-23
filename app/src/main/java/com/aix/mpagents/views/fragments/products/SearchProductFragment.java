package com.aix.mpagents.views.fragments.products;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aix.mpagents.databinding.FragmentSearchProductBinding;
import com.aix.mpagents.interfaces.ProductInterface;
import com.aix.mpagents.models.ProductInfo;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.view_models.ProductViewModel;
import com.aix.mpagents.views.adapters.ProductsAdapter;
import com.aix.mpagents.views.adapters.ProductsFirestoreAdapter;

import java.util.ArrayList;
import java.util.List;


public class SearchProductFragment extends Fragment implements ProductInterface {

    private FragmentSearchProductBinding binding;
    private ProductsAdapter productsAdapter;
    private ProductViewModel productViewModel;
    private List<ProductInfo> productInfoList = new ArrayList<>();
    private String query = "";

    public SearchProductFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentSearchProductBinding.inflate(inflater, container, false);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        try {
            query = requireArguments().getString("query");
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Search \"" + query + "\"");
            initRecyclerView();
//            getSearchItem(query);
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
        return binding.getRoot();
    }

    private void initRecyclerView() {
        productViewModel.addProductsListener();
        productsAdapter = new ProductsAdapter(productInfoList, this,requireContext());
        productsAdapter.setHasStableIds(true);
//
        binding.recyclerViewProducts.setAdapter(productsAdapter);
        binding.recyclerViewProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewProducts.setItemAnimator(null);

        productViewModel.getAllProductInfo().observe(getViewLifecycleOwner(), result -> {
            productInfoList.clear();
            for(ProductInfo product: result){
                if(product.getProduct_name().toLowerCase().contains(query.toLowerCase()))
                productInfoList.add(product);
            }
            productsAdapter.notifyDataSetChanged();
        });
    }

    private void getSearchItem(String args) {
        ErrorLog.WriteDebugLog("Search: " + args);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        productViewModel.detachProductsListener();
        super.onPause();
    }

    @Override
    public void onProductClick(ProductInfo productInfo) {

    }

    @Override
    public void onEditProduct(ProductInfo productInfo) {

    }

    @Override
    public void onOnlineProduct(ProductInfo productInfo) {

    }

    @Override
    public void onInactiveProduct(ProductInfo productInfo) {

    }

    @Override
    public void onShareProduct(ProductInfo productInfo) {

    }

    @Override
    public void onEmptyProduct() {

    }

    @Override
    public void onNotEmptyProduct() {

    }
}