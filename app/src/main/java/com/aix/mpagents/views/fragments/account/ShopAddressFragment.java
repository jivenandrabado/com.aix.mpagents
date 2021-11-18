package com.aix.mpagents.views.fragments.account;

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
import com.aix.mpagents.databinding.FragmentShopAddressBinding;
import com.aix.mpagents.interfaces.ShopAddressInterface;
import com.aix.mpagents.models.ShopAddress;
import com.aix.mpagents.view_models.AccountInfoViewModel;
import com.aix.mpagents.views.adapters.ShopAddressFirestoreAdapter;


public class ShopAddressFragment extends Fragment implements ShopAddressInterface {

    private FragmentShopAddressBinding binding;
    private AccountInfoViewModel accountInfoViewModel;
    private NavController navController;
    private ShopAddressFirestoreAdapter shopAddressFirestoreAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentShopAddressBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accountInfoViewModel = new ViewModelProvider(requireActivity()).get(AccountInfoViewModel.class);
        navController = Navigation.findNavController(view);
        initAddAddress();
        initAddressesRecyclerView();

    }

    private void initAddressesRecyclerView(){

        shopAddressFirestoreAdapter = new ShopAddressFirestoreAdapter(accountInfoViewModel.getShopAddressOptions(),requireContext(), this);
        shopAddressFirestoreAdapter.setHasStableIds(true);

        binding.recyclerViewAddresses.setAdapter(shopAddressFirestoreAdapter);
        binding.recyclerViewAddresses.setLayoutManager(new LinearLayoutManager(requireContext()));
        //temporary fix for recyclerview
        binding.recyclerViewAddresses.setItemAnimator(null);


    }

    private void initAddAddress(){
        binding.buttonAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountInfoViewModel.getSelectedShopAddress().setValue(null);
                navController.navigate(R.id.action_shopAddressFragment_to_addAddressfragment);
            }
        });
    }

//    public Boolean isEmpty(ShopAddress shopAddress){
//        if(shopAddress.getShop_citymun().isEmpty() || shopAddress.getShop_barangay().isEmpty()
//                || shopAddress.getShop_street().isEmpty()|| shopAddress.getShop_blkbldgnumber().isEmpty())
//        {
//            return true;
//        }else{
//            return false;
//        }
//
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(shopAddressFirestoreAdapter!=null) {
            shopAddressFirestoreAdapter.startListening();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(shopAddressFirestoreAdapter!=null) {
            shopAddressFirestoreAdapter.stopListening();
        }
    }

    @Override
    public void onAddressClick(ShopAddress shopAddress) {
        accountInfoViewModel.getSelectedShopAddress().setValue(shopAddress);
        navController.navigate(R.id.action_shopAddressFragment_to_addAddressfragment);

    }
}