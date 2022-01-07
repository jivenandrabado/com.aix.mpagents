package com.aix.mpagents.views.fragments.services;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.InputType;
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

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.FragmentServiceListBinding;
import com.aix.mpagents.interfaces.ProductRequirementsInterface;
import com.aix.mpagents.interfaces.ServiceInterface;
import com.aix.mpagents.models.AccountInfo;
import com.aix.mpagents.models.ProductInfo;
import com.aix.mpagents.models.ServiceInfo;
import com.aix.mpagents.utilities.AlertUtils;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.utilities.MenuUtils;
import com.aix.mpagents.view_models.AccountInfoViewModel;
import com.aix.mpagents.view_models.ServiceViewModel;
import com.aix.mpagents.view_models.UserSharedViewModel;
import com.aix.mpagents.views.adapters.ServiceFirestoreAdapter;
import com.aix.mpagents.views.fragments.dialogs.AddProductsRequirementsDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceListFragment extends Fragment implements ServiceInterface, TabLayout.OnTabSelectedListener, ProductRequirementsInterface {

    private FragmentServiceListBinding binding;
    private NavController navController;
    private ServiceViewModel serviceViewModel;
    private ServiceFirestoreAdapter serviceFirestoreAdapter;
    private ArrayAdapter<String> servicesNamesAdapter;
    private List<String> serviceNames = new ArrayList<>();
    private List<ServiceInfo> services = new ArrayList<>();
    private SearchView searchView = null;
    private UserSharedViewModel userSharedViewModel;
    private AccountInfoViewModel accountInfoViewModel;
    private AccountInfo accountInfo;
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
        binding = FragmentServiceListBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        serviceViewModel = new ViewModelProvider(requireActivity()).get(ServiceViewModel.class);
        userSharedViewModel = new ViewModelProvider(requireActivity()).get(UserSharedViewModel.class);
        accountInfoViewModel = new ViewModelProvider(requireActivity()).get(AccountInfoViewModel.class);
        initObservers();
        initProductsRecyclerView();
        initTabs();
        initListeners();
    }

    private void initObservers() {
        userSharedViewModel.isUserLoggedin().observe(getViewLifecycleOwner(), result ->{
            accountInfoViewModel.addAccountInfoSnapshot();
            serviceViewModel.addListener();
        });

        accountInfoViewModel.getAccountInfo().observe(getViewLifecycleOwner(), result -> {
            accountInfo = result;
        });

        serviceViewModel.getAllServicesInfo().observe(getViewLifecycleOwner(), result -> {
            services.clear();
            services.addAll(result);
            updateProductNameList();
        });

        accountInfoViewModel.getAccountInfo().observe(getViewLifecycleOwner(), result -> accountInfo = result);
    }

    private void initProductsRecyclerView() {
        serviceFirestoreAdapter = new ServiceFirestoreAdapter(serviceViewModel.getServiceRecyclerOptions(),this, requireContext());
        serviceFirestoreAdapter.setHasStableIds(true);

        binding.recyclerViewProducts.setAdapter(serviceFirestoreAdapter);
        binding.recyclerViewProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        //temporary fix for recyclerview
        binding.recyclerViewProducts.setItemAnimator(null);

        servicesNamesAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, serviceNames);
        binding.listViewSearchProducts.setAdapter(servicesNamesAdapter);
        binding.listViewSearchProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                toSearchResultPage(((TextView) view).getText().toString());
            }
        });
    }

    private void toSearchResultPage(String s) {
        Bundle bundle = new Bundle();
        bundle.putString("query", s);
        navController.navigate(R.id.action_serviceListFragment_to_searchServiceFragment, bundle);
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

    private void initListeners() {
        binding.buttonAddServices.setOnClickListener(v -> {
            if (accountInfo.hasInfoFillUp())
                navController.navigate(R.id.action_serviceListFragment_to_addServiceFragment);
            else{
                new AddProductsRequirementsDialog(
                        !accountInfo.getEmail().isEmpty(),
                        !accountInfo.getMobile_no().isEmpty(),
                        false,
                        !accountInfo.getGov_id_primary().isEmpty(),
                        this
                ).show(requireActivity().getSupportFragmentManager(), "REQUIREMENTS_DIALOG");
            }
        });
    }

    @Override
    public void onEditProduct(ServiceInfo service) {
        serviceViewModel.getSelectedService().setValue(service);
        navController.navigate(R.id.action_serviceListFragment_to_editServiceFragment);
    }

    @Override
    public void onOnlineProduct(ServiceInfo service) {
        DialogInterface.OnClickListener onclick = (dialog, i) -> {
            if (i == DialogInterface.BUTTON_POSITIVE) {
                serviceViewModel.changeStatus(service, ProductInfo.Status.ACTIVE);
            }
            dialog.dismiss();
        };
        AlertUtils.serviceAlert(requireContext(),service,onclick, ProductInfo.Status.ACTIVE);
    }

    @Override
    public void onInactiveProduct(ServiceInfo service) {
        DialogInterface.OnClickListener onclick = (dialog, i) -> {
            if (i == DialogInterface.BUTTON_POSITIVE) {
                serviceViewModel.changeStatus(service, ProductInfo.Status.INACTIVE);
            }
            dialog.dismiss();
        };
        AlertUtils.serviceAlert(requireContext(),service,onclick, ProductInfo.Status.INACTIVE);
    }

    @Override
    public void onShareProduct(ServiceInfo service) {
        if(!service.getService_status().equals(ProductInfo.Status.ACTIVE)){
            Toast.makeText(requireContext(), "Product is not online.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://sample.url/" + service.getService_id());
        shareIntent.setType("text/plain");

        onShareResult.launch(Intent.createChooser(shareIntent, "Share via..."));
    }

    @Override
    public void onMoreProductOption(ServiceInfo service, View view) {
        PopupMenu.OnMenuItemClickListener menuOnClick = item -> {
            switch (item.getItemId()){
                case R.id.shareProduct:
                    onShareProduct(service);
                    break;
                case R.id.deleteProduct:
                    onDeleteProduct(service);
                    break;
            }
            return true;
        };
        MenuUtils.showMenuWithIcons(requireContext(),view,menuOnClick);
    }

    @Override
    public void onDeleteProduct(ServiceInfo service) {
        if(service.getService_status().equals(ProductInfo.Status.ACTIVE)){
            Toast.makeText(requireContext(), "Unable to delete. Product is online.", Toast.LENGTH_SHORT).show();
            return;
        }
        DialogInterface.OnClickListener onclick = (dialog, i) -> {
            if (i == DialogInterface.BUTTON_POSITIVE)
                serviceViewModel.changeStatus(service, ProductInfo.Status.DELETED);
            dialog.dismiss();
        };
        AlertUtils.serviceAlert(requireContext(), service, onclick, ProductInfo.Status.DELETED,
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
                serviceFirestoreAdapter.updateOptions(serviceViewModel.getServiceRecyclerOptions());
                break;
            case R.id.draft:
                serviceFirestoreAdapter.updateOptions(serviceViewModel.getServiceRecyclerOptions(ProductInfo.Status.DRAFT));
                break;
            case R.id.inactive:
                serviceFirestoreAdapter.updateOptions(serviceViewModel.getServiceRecyclerOptions(ProductInfo.Status.INACTIVE));
                break;
        }
        serviceFirestoreAdapter.notifyDataSetChanged();
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

    private void setOpenSearchRecycler(boolean isOpen) {
        int isSearchOpen = (isOpen) ? View.VISIBLE : View.GONE;
        int isSearchClose = (!isOpen) ? View.VISIBLE : View.INVISIBLE;
        binding.recyclerViewProducts.setVisibility(isSearchClose);
        binding.tabLayout.setVisibility(isSearchClose);
        binding.buttonAddServices.setVisibility(isSearchClose);
        binding.listViewSearchProducts.setVisibility(isSearchOpen);
    }

    private void updateProductNameList(String s) {
        serviceNames.clear();
        for(ServiceInfo service: services){
            if(service.getService_name().toLowerCase().contains(s.toLowerCase()))
                serviceNames.add(service.getService_name());
        }
        if(serviceNames.size() == 0) onEmptyProduct();
        else onNotEmptyProduct();

        ErrorLog.WriteDebugLog("updateProductNameList " + serviceNames.size());
        servicesNamesAdapter.notifyDataSetChanged();
    }

    private void updateProductNameList(){
        updateProductNameList("");
    }

    @Override
    public void onResume() {
        super.onResume();
        if(serviceFirestoreAdapter!=null) {
            serviceFirestoreAdapter.startListening();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(serviceFirestoreAdapter!=null) {
            serviceFirestoreAdapter.stopListening();
        }
        serviceViewModel.detachListener();
        accountInfoViewModel.detachAccountInfoListener();
    }

    @Override
    public void onEditDetails() {
        navController.navigate(R.id.action_serviceListFragment_to_businessProfileFragment);
    }

    @Override
    public void onEditAddress() {

    }
}