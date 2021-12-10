package com.aix.mpagents.views.fragments.products;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.FragmentProductListBinding;
import com.aix.mpagents.interfaces.ProductInterface;
import com.aix.mpagents.models.AccountInfo;
import com.aix.mpagents.models.ProductInfo;
import com.aix.mpagents.utilities.AlertUtils;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.utilities.MenuUtils;
import com.aix.mpagents.view_models.AccountInfoViewModel;
import com.aix.mpagents.view_models.ProductViewModel;
import com.aix.mpagents.view_models.UserSharedViewModel;
import com.aix.mpagents.views.adapters.ProductsFirestoreAdapter;
import com.aix.mpagents.views.fragments.dialogs.AddProductsRequirementsDialog;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProductListFragment extends Fragment implements ProductInterface, TabLayout.OnTabSelectedListener {

    private FragmentProductListBinding binding;
    private ProductViewModel productViewModel;
    private ProductsFirestoreAdapter productsFirestoreAdapter;
    private AccountInfoViewModel accountInfoViewModel;
    private UserSharedViewModel userSharedViewModel;
    private ProductsBottomSheetDialog productsBottomSheetDialog;
    private NavController navController;
    private HashMap<Integer,String> tabs = new HashMap<>();
    private List<ProductInfo> products = new ArrayList<>();
    private ArrayAdapter<String> productNamesAdapter;
    private List<String> productNames = new ArrayList<>();
    private SearchView searchView = null;
    private AccountInfo mAccountInfo;
    private String productType = "Product";
    
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
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
        accountInfoViewModel  = new ViewModelProvider(requireActivity()).get(AccountInfoViewModel.class);
        userSharedViewModel = new ViewModelProvider(requireActivity()).get(UserSharedViewModel.class);
        navController = Navigation.findNavController(view);
        initProductsRecyclerView();
        initTabs();
        initObservers();
        initListeners();

    }

    private void initListeners() {
        binding.buttonAddProduct.setOnClickListener(v -> {
            if (mAccountInfo.hasInfoFillUp())
                navController.navigate(R.id.action_productListFragment_to_addProductFragment);
            else{
                new AddProductsRequirementsDialog(
                        !mAccountInfo.getEmail().isEmpty(),
                        !mAccountInfo.getMobile_no().isEmpty(),
                        false,
                        !mAccountInfo.getGov_id_primary().isEmpty(),
                        navController
                ).show(requireActivity().getSupportFragmentManager(), "REQUIREMENTS_DIALOG");
            }
        });
    }

    private void initObservers() {

        userSharedViewModel.isUserLoggedin().observe(getViewLifecycleOwner(), result ->{
            accountInfoViewModel.addAccountInfoSnapshot();
            productViewModel.addProductsListener();
        });

        productViewModel.getAllProductInfo().observe(getViewLifecycleOwner(), result -> {
            products.clear();
            products.addAll(result);
            updateProductNameList();
        });

        accountInfoViewModel.getAccountInfo().observe(getViewLifecycleOwner(), result -> mAccountInfo = result);
    }


    private void initTabs() {
        tabs.put(R.id.draft, "Draft");
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

        for (int i = 0; i < tabs.size(); i++){
            TabLayout.Tab currentTab = binding.tabLayout.getTabAt(i);
            if(currentTab.getId() == R.id.online){
                currentTab.select();
            }
        }
    }

    private void initProductsRecyclerView(){
        productsFirestoreAdapter = new ProductsFirestoreAdapter(productViewModel.getProductRecyclerOptions(ProductInfo.Status.ONLINE),this,requireContext());
        productsFirestoreAdapter.setHasStableIds(true);

        binding.recyclerViewProducts.setAdapter(productsFirestoreAdapter);
        binding.recyclerViewProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        //temporary fix for recyclerview
        binding.recyclerViewProducts.setItemAnimator(null);

        productNamesAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, productNames);
        binding.listViewSearchProducts.setAdapter(productNamesAdapter);
        binding.listViewSearchProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                toSearchResultPage(((TextView) view).getText().toString());
            }
        });

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
        productViewModel.detachProductsListener();
        accountInfoViewModel.detachAccountInfoListener();
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
        AlertUtils.productAlert(requireContext(),productInfo,onclick, ProductInfo.Status.ONLINE);
    }

    @Override
    public void onInactiveProduct(ProductInfo productInfo) {
        DialogInterface.OnClickListener onclick = (dialog, i) -> {
            if (i == DialogInterface.BUTTON_POSITIVE)
                productViewModel.changeProductStatus(productInfo, ProductInfo.Status.INACTIVE);
            dialog.dismiss();
        };
        AlertUtils.productAlert(requireContext(),productInfo,onclick, ProductInfo.Status.INACTIVE);
    }

    @Override
    public void onShareProduct(ProductInfo productInfo) {
        if(!productInfo.getProduct_status().equals(ProductInfo.Status.ONLINE)){
            Toast.makeText(requireContext(), "Product is not online.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://sample.url/" + productInfo.getProduct_id());
        shareIntent.setType("text/plain");

        onShareResult.launch(Intent.createChooser(shareIntent, "Share via..."));
    }

    @Override
    public void onMoreProductOption(ProductInfo productInfo, View view) {
        PopupMenu.OnMenuItemClickListener menuOnClick = item -> {
            switch (item.getItemId()){
                case R.id.shareProduct:
                    onShareProduct(productInfo);
                    break;
                case R.id.deleteProduct:
                    onDeleteProduct(productInfo);
                    break;
            }
            return true;
        };
        MenuUtils.showMenuWithIcons(requireContext(),view,menuOnClick);
    }

    @Override
    public void onDeleteProduct(ProductInfo productInfo) {
        if(productInfo.getProduct_status().equals(ProductInfo.Status.ONLINE)){
            Toast.makeText(requireContext(), "Unable to delete. Product is online.", Toast.LENGTH_SHORT).show();
            return;
        }
        DialogInterface.OnClickListener onclick = (dialog, i) -> {
            if (i == DialogInterface.BUTTON_POSITIVE)
                productViewModel.changeProductStatus(productInfo, ProductInfo.Status.DELETED);
            dialog.dismiss();
        };
        AlertUtils.productAlert(requireContext(), productInfo, onclick, ProductInfo.Status.DELETED,
                "Are you sure you want to delete this item?");
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
            case R.id.draft:
                productsFirestoreAdapter.updateOptions(productViewModel.getProductRecyclerOptions(ProductInfo.Status.DRAFT));
                break;
            case R.id.inactive:
                productsFirestoreAdapter.updateOptions(productViewModel.getProductRecyclerOptions(ProductInfo.Status.INACTIVE));
                break;
        }
        productsFirestoreAdapter.notifyDataSetChanged();
//        binding.recyclerViewProducts.setAdapter(productsFirestoreAdapter);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        try {
            if (searchItem != null) {
                searchView = (SearchView) searchItem.getActionView();
            }
            if (searchView != null) {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
                searchView.setQueryHint(requireContext().getString(R.string.search));
                searchView.setInputType(InputType.TYPE_CLASS_TEXT);
                searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
                searchView.setMaxWidth(Integer.MAX_VALUE);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        toSearchResultPage(s);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        if(s.length() > 0) setOpenSearchRecycler(true);
                        else setOpenSearchRecycler(false);
                        updateProductNameList(s);
                        return false;
                    }
                });
            }
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void toSearchResultPage(String s) {
        Bundle bundle = new Bundle();
        bundle.putString("query", s);
        navController.navigate(R.id.action_productListFragment_to_searchProductFragment, bundle);
    }

    private void updateProductNameList(String s) {
        productNames.clear();
        for(ProductInfo product: products){
            if(product.getProduct_name().toLowerCase().contains(s.toLowerCase()))
                productNames.add(product.getProduct_name());
        }
        if(productNames.size() == 0) onEmptyProduct();
        else onNotEmptyProduct();

        ErrorLog.WriteDebugLog("updateProductNameList " + productNames.size());
        productNamesAdapter.notifyDataSetChanged();
    }

    private void updateProductNameList(){
        updateProductNameList("");
    }

    private void setOpenSearchRecycler(boolean isOpen) {
        int isSearchOpen = (isOpen) ? View.VISIBLE : View.GONE;
        int isSearchClose = (!isOpen) ? View.VISIBLE : View.INVISIBLE;
            binding.recyclerViewProducts.setVisibility(isSearchClose);
            binding.tabLayout.setVisibility(isSearchClose);
            binding.buttonAddProduct.setVisibility(isSearchClose);
            binding.listViewSearchProducts.setVisibility(isSearchOpen);
    }

}