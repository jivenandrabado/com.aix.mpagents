package com.aix.mpagents.views;

import android.app.AlertDialog;
import android.content.Context;
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
import com.aix.mpagents.databinding.FragmentHomeBinding;
import com.aix.mpagents.models.AccountInfo;
import com.aix.mpagents.models.PushNotification;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.view_models.AccountInfoViewModel;
import com.aix.mpagents.view_models.OrderViewModel;
import com.aix.mpagents.view_models.PushNotificationViewModel;
import com.aix.mpagents.view_models.UserSharedViewModel;
import com.aix.mpagents.views.fragments.dialogs.AddProductsRequirementsDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private UserSharedViewModel userSharedViewModel;
    private NavController navController;
    private AccountInfoViewModel accountInfoViewModel;
    private OrderViewModel orderViewModel;
    private PushNotificationViewModel pushNotificationViewModel;
    private AccountInfo mAccountInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false);
//        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userSharedViewModel = new ViewModelProvider(requireActivity()).get(UserSharedViewModel.class);
        navController = Navigation.findNavController(view);
        initPushNotif(requireContext());

        userSharedViewModel.isUserLoggedin().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    ErrorLog.WriteDebugLog("User logged in");
                    if(accountInfoViewModel == null){
                        accountInfoViewModel = new ViewModelProvider(requireActivity()).get(AccountInfoViewModel.class);
                    }
                    initShopInfo();
                    initPendingOrderListener();
                }else{
                    navController.navigate(R.id.action_homeFragment_to_loginFragment);
                    ErrorLog.WriteDebugLog("User logged out");
                }

            }
        });

        binding.buttonAddProducts.setOnClickListener(view1 -> toCreateProduct(getString(R.string.product_type_product)));
        binding.buttonAddServices.setOnClickListener(view1 -> toCreateProduct(getString(R.string.product_type_service)));
        binding.buttonProducts.setOnClickListener(view1 -> toProductList(getString(R.string.product_type_product)));
        binding.buttonServices.setOnClickListener(view1 -> toProductList(getString(R.string.product_type_service)));

        binding.buttonBookings.setOnClickListener(v->{
            navController.navigate(R.id.action_homeFragment_to_bookingListFragment);
        });
        binding.buttonOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_homeFragment_to_ordersFragment3);
            }
        });

        binding.buttonGoToOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_homeFragment_to_ordersFragment3);
            }
        });

    }

    private void toCreateProduct(String productType) {
        if (mAccountInfo.hasInfoFillUp()){
            Bundle bundle = new Bundle();
            bundle.putString("product_type",productType);
            navController.navigate(R.id.action_homeFragment_to_addProductFragment,bundle);
        }
        else{
            new AddProductsRequirementsDialog(
                    !mAccountInfo.getEmail().isEmpty(),
                    !mAccountInfo.getMobile_no().isEmpty(),
                    false,
                    !mAccountInfo.getGov_id_primary().isEmpty(),
                    navController
            ).show(requireActivity().getSupportFragmentManager(), "REQUIREMENTS_DIALOG");
        }
    }

    private void toProductList(String productType) {
        Bundle bundle = new Bundle();
        bundle.putString("product_type",productType);
        navController.navigate(R.id.action_homeFragment_to_productListFragment, bundle);
    }

    private void initShopInfo(){
        accountInfoViewModel.addAccountInfoSnapshot();

        accountInfoViewModel.getAccountInfo().observe(getViewLifecycleOwner(), new Observer<AccountInfo>() {
            @Override
            public void onChanged(AccountInfo accountInfo) {
                if(accountInfo !=null) {
                    mAccountInfo= accountInfo;
                    String first_name = accountInfo.getFirst_name();
                    String middle_name = accountInfo.getMiddle_name();
                    String last_name = accountInfo.getLast_name();
                    String full_name;
                    if(middle_name.isEmpty()){
                        full_name = first_name  + " " + last_name;
                    }else{
                        full_name = first_name + " " + middle_name + " " + last_name;
                    }
                    binding.textviewShopName.setText(full_name);
                    binding.textViewSellerID.setText(accountInfo.getAgent_id());

                    Glide.with(requireContext()).load(Uri.parse(accountInfo.getProfile_pic()))
                            .fitCenter()
                            .error(R.drawable.ic_baseline_photo_24).into((binding.imageViewProfilePic));

                }
            }
        });

    }

    private void initPendingOrderListener(){
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        orderViewModel.addOrderPendingSnapshotListener();

        orderViewModel.getPendingOrder().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer != 0) {
                    binding.textViewPending.setText(String.valueOf(integer));
                }else{
                    binding.textViewPending.setText("0");
                }

            }
        });

        orderViewModel.getCompletedOrder().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer != 0) {
                    binding.textViewCompleted.setText(String.valueOf(integer));
                }else{
                    binding.textViewCompleted.setText("0");
                }
            }
        });
    }

    private void initPushNotif(Context context) {
        pushNotificationViewModel = new ViewModelProvider(requireActivity()).get(PushNotificationViewModel.class);
        pushNotificationViewModel.addSnapshotForPushNotif();
        pushNotificationViewModel.getPushNotif1().observe(requireActivity(), new Observer<PushNotification>() {
            @Override
            public void onChanged(PushNotification pushNotification) {
                if (pushNotification != null) {
                    Glide.with(context).load(Uri.parse(pushNotification.getImage_url()))
                            .fitCenter()
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                            .error(R.drawable.ic_baseline_photo_24).into((binding.imageViewPushAdvertising));
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(orderViewModel!=null) {
            orderViewModel.detachPendingOrderListener();
        }

        if(accountInfoViewModel!= null) {
            accountInfoViewModel.detachAccountInfoListener();
        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_lobby_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}