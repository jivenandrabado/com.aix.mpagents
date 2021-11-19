package com.aix.mpagents.views.fragments.products;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.Map;


public class ProductListFragment extends Fragment implements ProductInterface, TabLayout.OnTabSelectedListener {

    private FragmentProductListBinding binding;
    private ProductViewModel productViewModel;
    private ProductsFirestoreAdapter productsFirestoreAdapter;
    private ProductsBottomSheetDialog productsBottomSheetDialog;
    private NavController navController;
    private HashMap<Integer,String> tabs = new HashMap<>();
    
    private ActivityResultLauncher<Intent> onShareResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK){
                    Toast.makeText(requireContext(), "Product shared!", Toast.LENGTH_SHORT).show();
                }
            }
    );

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
        initTabs();


        binding.buttonAddProduct.setOnClickListener(v -> {
            navController.navigate(R.id.action_productListFragment_to_addProductFragment);
        });
    }

    private void initTabs() {
        tabs.put(R.id.online, "Online");
        tabs.put(R.id.inactive, "Inactive");

        binding.tabLayout.setTabMode(TabLayout.MODE_FIXED);
        for(Map.Entry<Integer,String> tab: tabs.entrySet()){
            TabLayout.Tab newTab = binding.tabLayout.newTab();
            newTab.setId(tab.getKey());
            newTab.setText(tab.getValue());
            binding.tabLayout.addTab(newTab);
        }
        binding.tabLayout.addOnTabSelectedListener(this);
        binding.tabLayout.getTabAt(0).select();
    }

    private void initProductsRecyclerView(){
        productsFirestoreAdapter = new ProductsFirestoreAdapter(productViewModel.getProductRecyclerOptions(ProductInfo.Status.ONLINE),this,requireContext());
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
        productViewModel.getSelectedProduct().setValue(productInfo);
        navController.navigate(R.id.action_productListFragment_to_editProductFragment);
    }

    @Override
    public void onOnlineProduct(ProductInfo productInfo) {
        DialogInterface.OnClickListener onclick = (dialog, i) -> {
            if (i == DialogInterface.BUTTON_POSITIVE)
                productViewModel.changeProductStatus(productInfo, ProductInfo.Status.ONLINE);
            dialog.dismiss();
        };
        askAlert(productInfo,onclick, ProductInfo.Status.ONLINE);
    }

    private void askAlert(ProductInfo productInfo, DialogInterface.OnClickListener onclick, String status) {
        new AlertDialog.Builder(requireContext())
                .setTitle(productInfo.getProduct_name())
                .setMessage("Are you sure you want to make this item " + status + "?")
                .setPositiveButton("Ok", onclick)
                .setNegativeButton("Cancel", onclick)
                .show();
    }

    @Override
    public void onInactiveProduct(ProductInfo productInfo) {
        DialogInterface.OnClickListener onclick = (dialog, i) -> {
            if (i == DialogInterface.BUTTON_POSITIVE)
                productViewModel.changeProductStatus(productInfo, ProductInfo.Status.INACTIVE);
            dialog.dismiss();
        };
        askAlert(productInfo,onclick, ProductInfo.Status.INACTIVE);
    }

    @Override
    public void onShareProduct(ProductInfo productInfo) {
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, "https://sample.url/" + productInfo.getProduct_id());
        share.setType("text/plain");
        onShareResult.launch(Intent.createChooser(share, "Share via..."));
    }

    @Override
    public void onEmptyProduct() {
        binding.linearLayoutNoDataAvailable.setVisibility(View.VISIBLE);
    }

    @Override
    public void onNotEmptyProduct() {
        binding.linearLayoutNoDataAvailable.setVisibility(View.GONE);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getId()){
            case R.id.online:
                productsFirestoreAdapter.updateOptions(productViewModel.getProductRecyclerOptions(ProductInfo.Status.ONLINE));
                break;
            case R.id.inactive:
                productsFirestoreAdapter.updateOptions(productViewModel.getProductRecyclerOptions(ProductInfo.Status.INACTIVE));
                break;
        }
        binding.recyclerViewProducts.setAdapter(productsFirestoreAdapter);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}