package com.aix.mpagents.views.fragments.base;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.aix.mpagents.models.AccountInfo;
import com.aix.mpagents.utilities.ToastUtil;
import com.aix.mpagents.view_models.AccountInfoViewModel;
import com.aix.mpagents.view_models.UserSharedViewModel;
import com.aix.mpagents.views.fragments.dialogs.ProgressDialog;

public abstract class BaseFragment extends Fragment {

    public BaseFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    private UserSharedViewModel userSharedViewModel;

    private ToastUtil toastUtil;

    private ProgressDialog loading;

    public NavController navController;

    protected AccountInfoViewModel accountInfoViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        userSharedViewModel = new ViewModelProvider(requireActivity()).get(UserSharedViewModel.class);

        accountInfoViewModel = new ViewModelProvider(requireActivity()).get(AccountInfoViewModel.class);

        userSharedViewModel.isUserLoggedin().observe(getViewLifecycleOwner(), this::isUserLogin);

        accountInfoViewModel.getAccountInfo().observe(getViewLifecycleOwner(), this::onInfoLoaded);
    }

    public void showLoading(boolean isLoading){

        if(loading == null) loading = new ProgressDialog(requireContext());

        if(isLoading) loading.show();

        else loading.dismiss();

    }

    public void onInfoLoaded(AccountInfo userInfo){ }

    public void showToast(String message){

        if(toastUtil == null) toastUtil = new ToastUtil();

        toastUtil.makeText(requireContext(), message, Toast.LENGTH_SHORT);

    }

    public void isUserLogin(Boolean isLogin){
        if(isLogin) accountInfoViewModel.addAccountInfoSnapshot();
    }

    @Override
    public void onPause() {

        super.onPause();

        accountInfoViewModel.detachAccountInfoListener();

    }

    public String getSignInMethod(){
        return userSharedViewModel.getSignInMethod();
    }
}
