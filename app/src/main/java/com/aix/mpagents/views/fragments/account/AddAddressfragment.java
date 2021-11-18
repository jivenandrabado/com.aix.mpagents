package com.aix.mpagents.views.fragments.account;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddAddressfragment extends Fragment {

    private FragmentAddAddressfragmentBinding binding;
    private AccountInfoViewModel accountInfoViewModel;
    private boolean is_business;
    private NavController navController;
    private String address;
    private GeoPoint latLong;
    private List<ShopAddress> addresses = new ArrayList<>();

    private ActivityResultLauncher<Intent> placesActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK){
                    Place place = Autocomplete.getPlaceFromIntent(result.getData());
                    binding.editTextAddress.setText(place.getName());
                    latLong = new GeoPoint(place.getLatLng().latitude, place.getLatLng().longitude);
                }
            }
    );

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
        Places.initialize(requireContext().getApplicationContext(), requireContext().getString(R.string.places_api_key));
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

        accountInfoViewModel.getAllAddresses().observe(getViewLifecycleOwner(), result -> {
            addresses = result;
        });

        binding.editTextAddress.setOnClickListener(v -> {
            List<Place.Field> fieldList = Arrays.asList(
                    Place.Field.ADDRESS,
                    Place.Field.LAT_LNG,
                    Place.Field.NAME);
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN,
                    fieldList
            ).build(requireContext());
            placesActivityResult.launch(intent);
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
            shopAddress.setLatLng(latLong);
            shopAddress.setIs_business(is_business);
            shopAddress.setIs_deleted(false);

            isAddressDefault(shopAddress);
            accountInfoViewModel.saveAddress(shopAddress);
        }
    }

    private void updateAddress(ShopAddress shopAddress){
        ErrorLog.WriteDebugLog("UPDATE ADDRESS");
        address = String.valueOf(binding.editTextAddress.getText()).trim();
        is_business = binding.switchBusinessAddress.isChecked();

        if(!isEmptyFields(address)) {
            shopAddress.setAddress_name(address);
            shopAddress.setLatLng(latLong);
            shopAddress.setIs_business(is_business);
            isAddressDefault(shopAddress);
            accountInfoViewModel.updateAddress(shopAddress);
        }
    }

    private void isAddressDefault(ShopAddress shopAddress) {
        if(shopAddress.is_business){
            for(ShopAddress address : addresses){
                address.setIs_business(false);
                accountInfoViewModel.updateAddressForDefault(address);
            }
        }
    }

    private boolean isEmptyFields(String address){

        if (TextUtils.isEmpty(address)){
            Toast.makeText(requireContext(), "Empty Address", Toast.LENGTH_LONG).show();
            return true;
        }else{
            return false;
        }

    }
}