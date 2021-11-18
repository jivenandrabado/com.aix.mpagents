package com.aix.mpagents.views.fragments.account;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.FragmentAddAddressfragmentBinding;
import com.aix.mpagents.models.ShopAddress;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.view_models.AccountInfoViewModel;

public class AddAddressfragment extends Fragment {

    private FragmentAddAddressfragmentBinding binding;
    private AccountInfoViewModel accountInfoViewModel;
    private boolean is_business;
    private NavController navController;
    private String address;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddAddressfragmentBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accountInfoViewModel = new ViewModelProvider(requireActivity()).get(AccountInfoViewModel.class);
        navController = Navigation.findNavController(view);
        initAddressView();

        accountInfoViewModel.isAddressUpdated().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    Toast.makeText(requireContext(),"Address Updated", Toast.LENGTH_SHORT).show();
                    accountInfoViewModel.isAddressUpdated().setValue(false);
                    navController.popBackStack(R.id.addAddressfragment,true);
                }
            }
        });



    }

    private void initAddressView(){
        try {
            accountInfoViewModel.getSelectedShopAddress().observe(getViewLifecycleOwner(), new Observer<ShopAddress>() {
                @Override
                public void onChanged(ShopAddress shopAddress) {
                    if(shopAddress!=null) {
                        binding.editTextAddress.setText(shopAddress.getAddress_name());
                        binding.textViewDelete.setVisibility(View.VISIBLE);
                        binding.switchBusinessAddress.setChecked(shopAddress.isIs_business());

                        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                updateAddress(shopAddress);
                            }
                        });

                        binding.textViewDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ErrorLog.WriteDebugLog("DELETE ADDRESS");

                                accountInfoViewModel.deleteAddress(shopAddress);
                            }
                        });

                    }else{
                        binding.textViewDelete.setVisibility(View.INVISIBLE);
                        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                saveAddress();
                            }
                        });
                    }

                }
            });

        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    private void saveAddress(){
        ErrorLog.WriteDebugLog("SAVE ADDRESS");

        address = String.valueOf(binding.editTextAddress.getText()).trim();
        is_business = binding.switchBusinessAddress.isChecked();

        if(!isEmptyFields(address)) {
            ShopAddress shopAddress = new ShopAddress();
            shopAddress.setAddress_name(address);
            shopAddress.setLatLng(null);
            shopAddress.setIs_business(is_business);
            shopAddress.setIs_deleted(false);

            accountInfoViewModel.saveAddress(shopAddress);
        }
    }

    private void updateAddress(ShopAddress shopAddress){
        ErrorLog.WriteDebugLog("UPDATE ADDRESS");
        address = String.valueOf(binding.editTextAddress.getText()).trim();
        is_business = binding.switchBusinessAddress.isChecked();

        if(!isEmptyFields(address)) {
            shopAddress.setAddress_name(address);
            shopAddress.setLatLng(null);
            shopAddress.setIs_business(is_business);

            accountInfoViewModel.updateAddress(shopAddress);
        }
    }

    private boolean isEmptyFields(String address){

        if (TextUtils.isEmpty(address)){
            Toast.makeText(requireContext(), "Empty City/Municipality", Toast.LENGTH_LONG).show();
            return true;
        }else{
            return false;
        }

    }
}