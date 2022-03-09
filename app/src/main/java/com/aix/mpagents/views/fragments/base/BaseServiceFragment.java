package com.aix.mpagents.views.fragments.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.aix.mpagents.models.ServiceInfo;
import com.aix.mpagents.view_models.ServiceViewModel;

import java.util.List;

public abstract class BaseServiceFragment extends BaseItemsFragment{

    private ServiceViewModel serviceViewModel;
    public BaseServiceFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        serviceViewModel = new ViewModelProvider(requireActivity()).get(ServiceViewModel.class);

        serviceViewModel.getAllServicesInfo().observe(getViewLifecycleOwner(), this::onAllServiceLoaded);
    }

    public void onAllServiceLoaded(List<ServiceInfo> list){}

    @Override
    public void isUserLogin(Boolean isLogin) {
        super.isUserLogin(isLogin);
        if(isLogin) serviceViewModel.addListener();
    }

    public ServiceViewModel getServiceViewModel(){
        return serviceViewModel;
    }

    @Override
    public void onPause() {
        super.onPause();
        serviceViewModel.detachListener();
    }
}
