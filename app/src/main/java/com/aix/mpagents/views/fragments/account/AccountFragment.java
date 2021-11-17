package com.aix.mpagents.views.fragments.account;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.FragmentAccountBinding;
import com.aix.mpagents.interfaces.AccountInterface;
import com.aix.mpagents.models.AccountInfo;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.utilities.LayoutViewHelper;
import com.aix.mpagents.view_models.AccountInfoViewModel;
import com.aix.mpagents.view_models.UserSharedViewModel;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class AccountFragment extends Fragment implements AccountInterface {

    private FragmentAccountBinding binding;
    private NavController navController;
    private AccountInfoViewModel accountInfoViewModel;
    private UserSharedViewModel userSharedViewModel;
    private LayoutViewHelper layoutViewHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        userSharedViewModel = new ViewModelProvider(requireActivity()).get(UserSharedViewModel.class);
        accountInfoViewModel = new ViewModelProvider(requireActivity()).get(AccountInfoViewModel.class);
        layoutViewHelper = new LayoutViewHelper(requireActivity());

        userSharedViewModel.isUserLoggedin().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    if(!Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isAnonymous()) {
                        ErrorLog.WriteDebugLog("Display user profile");
                        accountInfoViewModel.getShopInfo();

                    }else{
                        ErrorLog.WriteDebugLog("Init anonymous user");
                    }
//                    setHasOptionsMenu(true);

                }else{
                    ErrorLog.WriteDebugLog("No User");
                    accountInfoViewModel.getProfileObservable().setValue(null);
//                    setHasOptionsMenu(false);

                }
            }
        });


        accountInfoViewModel.getProfileObservable().observe(getViewLifecycleOwner(), new Observer<AccountInfo>() {
            @Override
            public void onChanged(AccountInfo accountInfo) {
                if(accountInfo !=null) {

                    binding.textviewShopName.setText(accountInfo.getShop_name());
                    binding.textViewSellerID.setText(accountInfo.getShop_id());

                    initUserTypeViews(accountInfo);
                    initVerificationStatus(accountInfo);
                    Glide.with(requireContext()).load(Uri.parse(accountInfo.getLogo()))
                            .fitCenter()
                            .error(R.drawable.ic_baseline_photo_24).into((binding.imageViewProfilePic));
                }
            }
        });

        binding.textViewBusinessInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_accountFragment2_to_profileFragment);
            }
        });

        binding.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_accountFragment2_to_profileFragment);
            }
        });

        binding.textViewVerifyAccount.setOnClickListener(v -> {
            navController.navigate(R.id.action_accountFragment2_to_verificationDetailsFragment);
        });

        binding.textViewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_accountFragment2_to_shopAddressFragment);
            }
        });

        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountInfoViewModel.logoutUser(requireActivity());
//                navController.popBackStack(R.id.accountFragment2,true);

            }
        });

        binding.textViewOrganization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_accountFragment2_to_organizationFragment);
            }
        });
    }

    private void initVerificationStatus(AccountInfo accountInfo) {
        if(!accountInfo.getGov_id_primary().isEmpty()){}
    }

    private void initUserTypeViews(AccountInfo accountInfo) {

        if(accountInfo.isIs_agent()) {
            binding.textViewOrganization.setVisibility(View.VISIBLE);
            binding.view4.setVisibility(View.VISIBLE);
        }else{
            binding.textViewOrganization.setVisibility(View.GONE);
            binding.view4.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        // Add the new menu items
//        inflater.inflate(R.menu.menu_profile, menu);

        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onAccountSettingsClick() {

    }

    @Override
    public void onBankAccountClick() {

    }

    @Override
    public void onBusinessInfoClick() {

    }

    @Override
    public void onShopAddressClick() {

    }

    @Override
    public void onResume() {
        super.onResume();
//        layoutViewHelper.hideActionBar();

    }

    @Override
    public void onStop() {
        super.onStop();
//        layoutViewHelper.showActionBar();

    }

}