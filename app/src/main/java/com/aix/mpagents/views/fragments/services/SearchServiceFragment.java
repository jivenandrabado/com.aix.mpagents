package com.aix.mpagents.views.fragments.services;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
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
import com.aix.mpagents.databinding.FragmentSearchProductBinding;
import com.aix.mpagents.interfaces.ServiceInterface;
import com.aix.mpagents.models.ServiceInfo;
import com.aix.mpagents.utilities.AlertUtils;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.utilities.MenuUtils;
import com.aix.mpagents.view_models.ServiceViewModel;
import com.aix.mpagents.views.adapters.ServiceAdapter;

import java.util.ArrayList;
import java.util.List;


public class SearchServiceFragment extends Fragment implements ServiceInterface {

    private FragmentSearchProductBinding binding;
    private ServiceAdapter serviceAdapter;
    private ServiceViewModel serviceViewModel;
    private List<ServiceInfo> serviceInfoList = new ArrayList<>();
    private NavController navController;
    private String query = "";

    private ActivityResultLauncher<Intent> onShareResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK){
                    Toast.makeText(requireContext(), "Service shared!", Toast.LENGTH_SHORT).show();
                }
            }
    );


    public SearchServiceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentSearchProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        serviceViewModel = new ViewModelProvider(requireActivity()).get(ServiceViewModel.class);
        try {
            query = requireArguments().getString("query");
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Search \"" + query + "\"");
            initRecyclerView();
//            getSearchItem(query);
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    private void initRecyclerView() {
        serviceViewModel.addListener();
        serviceAdapter = new ServiceAdapter(serviceInfoList, this,requireContext());
        serviceAdapter.setHasStableIds(true);
//
        binding.recyclerViewProducts.setAdapter(serviceAdapter);
        binding.recyclerViewProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewProducts.setItemAnimator(null);

        serviceViewModel.getAllServicesInfo().observe(getViewLifecycleOwner(), result -> {
            serviceInfoList.clear();
            for(ServiceInfo service: result){
                if(service.getService_name().toLowerCase().contains(query.toLowerCase()))
                serviceInfoList.add(service);
            }
            serviceAdapter.notifyDataSetChanged();
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
        serviceViewModel.detachListener();
        super.onPause();
    }


    @Override
    public void onEditProduct(ServiceInfo serviceInfo) {
        serviceViewModel.getSelectedService().setValue(serviceInfo);
        navController.navigate(R.id.action_searchServiceFragment_to_editServiceFragment);
    }

    @Override
    public void onOnlineProduct(ServiceInfo serviceInfo) {
        DialogInterface.OnClickListener onclick = (dialog, i) -> {
            if (i == DialogInterface.BUTTON_POSITIVE)
                serviceViewModel.changeStatus(serviceInfo, ServiceInfo.Status.ONLINE);
            dialog.dismiss();
        };
        AlertUtils.serviceAlert(requireContext(), serviceInfo,onclick, ServiceInfo.Status.ONLINE);
    }

    @Override
    public void onInactiveProduct(ServiceInfo serviceInfo) {
        DialogInterface.OnClickListener onclick = (dialog, i) -> {
            if (i == DialogInterface.BUTTON_POSITIVE)
                serviceViewModel.changeStatus(serviceInfo, ServiceInfo.Status.INACTIVE);
            dialog.dismiss();
        };
        AlertUtils.serviceAlert(requireContext(),serviceInfo,onclick, ServiceInfo.Status.INACTIVE);
    }

    @Override
    public void onShareProduct(ServiceInfo serviceInfo) {
        if(!serviceInfo.getService_status().equals(ServiceInfo.Status.ONLINE)){
            Toast.makeText(requireContext(), "Product is not online.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://sample.url/" + serviceInfo.getService_id());
        shareIntent.setType("text/plain");

        onShareResult.launch(Intent.createChooser(shareIntent, "Share via..."));
    }

    @Override
    public void onMoreProductOption(ServiceInfo serviceInfo, View view) {
        PopupMenu.OnMenuItemClickListener menuOnClick = item -> {
            switch (item.getItemId()){
                case R.id.shareProduct:
                    onShareProduct(serviceInfo);
                    break;
                case R.id.deleteProduct:
                    onDeleteProduct(serviceInfo);
                    break;
            }
            return true;
        };
        MenuUtils.showMenuWithIcons(requireContext(),view,menuOnClick);
    }

    @Override
    public void onDeleteProduct(ServiceInfo serviceInfo) {
        if(serviceInfo.getService_status().equals(ServiceInfo.Status.ONLINE)){
            Toast.makeText(requireContext(), "Unable to delete. Product is online.", Toast.LENGTH_SHORT).show();
            return;
        }
        DialogInterface.OnClickListener onclick = (dialog, i) -> {
            if (i == DialogInterface.BUTTON_POSITIVE)
                serviceViewModel.changeStatus(serviceInfo, ServiceInfo.Status.DELETED);
            dialog.dismiss();
        };
        AlertUtils.serviceAlert(requireContext(), serviceInfo, onclick, ServiceInfo.Status.DELETED,
                "Are you sure you want to delete this item?");
    }

    @Override
    public void onEmptyProduct() {

    }

    @Override
    public void onNotEmptyProduct() {

    }
}