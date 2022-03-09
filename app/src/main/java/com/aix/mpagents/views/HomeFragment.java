package com.aix.mpagents.views;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.FragmentHomeBinding;
import com.aix.mpagents.interfaces.RequirementsDialogListener;
import com.aix.mpagents.models.AccountInfo;
import com.aix.mpagents.models.PushNotification;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.view_models.AccountInfoViewModel;
import com.aix.mpagents.view_models.OrderViewModel;
import com.aix.mpagents.view_models.PushNotificationViewModel;
import com.aix.mpagents.view_models.RegistrationViewModel;
import com.aix.mpagents.view_models.UserSharedViewModel;
import com.aix.mpagents.views.adapters.BannerAdapter;
import com.aix.mpagents.views.fragments.dialogs.AddProductsRequirementsDialog;
import com.aix.mpagents.views.fragments.dialogs.WelcomeMessageDialog;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements RequirementsDialogListener {
    private FragmentHomeBinding binding;
    private UserSharedViewModel userSharedViewModel;
    private NavController navController;
    private AccountInfoViewModel accountInfoViewModel;
    private OrderViewModel orderViewModel;
    private PushNotificationViewModel pushNotificationViewModel;
    private AccountInfo mAccountInfo;
    private BannerAdapter viewPagerAdapter;
    private RegistrationViewModel registrationViewModel;
    private List<String> links = new ArrayList<>();
    private Long slideTimer = 3000L;
    private Handler sliderHandler = new Handler();
    private Runnable sliderRunnable = () -> {
        int nextBanner = binding.viewPagerBannerContainer.getCurrentItem() + 1;
        binding.viewPagerBannerContainer.setCurrentItem(
                links.size() - 1 < nextBanner ? 0 : nextBanner,
                true);
    };

    public static int backCounter = 0;

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
        pushNotificationViewModel = new ViewModelProvider(requireActivity()).get(PushNotificationViewModel.class);
        registrationViewModel = new ViewModelProvider(requireActivity()).get(RegistrationViewModel.class);
        navController = Navigation.findNavController(view);
        viewPagerAdapter = new BannerAdapter(links);
        initPushNotif();
        initViewPager();
        initObservers();
        initListener();
    }

    private void initListener() {
        binding.buttonAddProducts.setOnClickListener(view1 -> toCreateProductOrService(true));
        binding.buttonAddServices.setOnClickListener(view1 -> toCreateProductOrService(false));
        binding.buttonProducts.setOnClickListener(view1 ->
                navController.navigate(R.id.action_homeFragment_to_productListFragment));
        binding.buttonServices.setOnClickListener(view1 ->
                navController.navigate(R.id.action_homeFragment_to_serviceListFragment));

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

    private void toCreateProductOrService(boolean isProduct) {
        if(mAccountInfo.hasInfoFillUp()) {
            if (isProduct) navController.navigate(R.id.action_homeFragment_to_addProductFragment);
            else navController.navigate(R.id.action_homeFragment_to_addServiceFragment);
        } else{

            AddProductsRequirementsDialog.showFragment(
                    !mAccountInfo.getEmail().isEmpty(),
                    !mAccountInfo.getMobile_no().isEmpty(),
                    false,
                    !mAccountInfo.getGov_id_primary().isEmpty(),
                    requireActivity().getSupportFragmentManager(),
                    this
            );
//            new AddProductsRequirementsDialog(
//                    !mAccountInfo.getEmail().isEmpty(),
//                    !mAccountInfo.getMobile_no().isEmpty(),
//                    false,
//                    !mAccountInfo.getGov_id_primary().isEmpty(),
//                    navController
//            ).show(requireActivity().getSupportFragmentManager(), "REQUIREMENTS_DIALOG");
        }
    }

    private void initObservers() {

        registrationViewModel.isRegistered().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    new WelcomeMessageDialog().show(requireActivity().getSupportFragmentManager(), "WELCOME_DIALOG");
                    registrationViewModel.isRegistered().setValue(false);
                }
            }
        });

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
    }

    private void initViewPager() {
        binding.viewPagerBannerContainer.setAdapter(viewPagerAdapter);
        binding.springDotsIndicator.setViewPager2(binding.viewPagerBannerContainer);
        binding.viewPagerBannerContainer.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, slideTimer);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
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

    private void initPushNotif() {
        pushNotificationViewModel.addSnapshotForPushNotifList();
        pushNotificationViewModel.getPushList().observe(requireActivity(), pushNotification -> {
            links.clear();
            for(PushNotification notif: pushNotification){
                links.add(notif.getImage_url());
                viewPagerAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onResume() {
        backCounter = 0;
        sliderHandler.postDelayed(sliderRunnable, slideTimer);
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

    @Override
    public void onNavigateToEditAccount() {
        navController.navigate(R.id.action_homeFragment_to_businessProfileFragment);
    }
}