package com.aix.mpagents.views.fragments.products;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.FragmentSearchProductBinding;
import com.aix.mpagents.interfaces.ProductInterface;
import com.aix.mpagents.models.ProductInfo;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.view_models.ProductViewModel;
import com.aix.mpagents.views.adapters.ProductsAdapter;
import com.aix.mpagents.views.adapters.ProductsFirestoreAdapter;
import com.aix.mpagents.views.fragments.base.BaseProductFragment;

import java.util.ArrayList;
import java.util.List;


public class SearchProductFragment extends BaseProductFragment implements ProductInterface {

    private FragmentSearchProductBinding binding;

    private ProductsAdapter productsAdapter;

    private List<ProductInfo> productInfoList = new ArrayList<>();

    private String query = "";

    public SearchProductFragment() {
        super(R.layout.fragment_search_product);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentSearchProductBinding.bind(getView());

        try {
            query = requireArguments().getString("query");
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Search \"" + query + "\"");
            initRecyclerView();
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    private void initRecyclerView() {
        productsAdapter = new ProductsAdapter(productInfoList, this,requireContext());

        productsAdapter.setHasStableIds(true);
//
        binding.recyclerViewProducts.setAdapter(productsAdapter);

        binding.recyclerViewProducts.setLayoutManager(new LinearLayoutManager(requireContext()));

        binding.recyclerViewProducts.setItemAnimator(null);

    }

    @Override
    public void onAllProductsLoaded(List<ProductInfo> list) {
        super.onAllProductsLoaded(list);
        productInfoList.clear();
        for(ProductInfo product: list){
            if(product.getProduct_name().toLowerCase().contains(query.toLowerCase()))
                productInfoList.add(product);
        }
        productsAdapter.notifyDataSetChanged();
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
    public void onMoreProductOption(ProductInfo productInfo, View view) {

    }

    @Override
    public void onDeleteProduct(ProductInfo productInfo) {

    }

    @Override
    public void onEmptyProduct() {

    }

    @Override
    public void onNotEmptyProduct() {

    }

}