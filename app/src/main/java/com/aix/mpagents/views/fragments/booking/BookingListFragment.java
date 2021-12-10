package com.aix.mpagents.views.fragments.booking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.FragmentBookingListBinding;
import com.aix.mpagents.utilities.ErrorLog;
import com.google.android.material.tabs.TabLayout;

public class BookingListFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    private FragmentBookingListBinding binding;
    private NavController navController;
    private String[] tabs = new String[]{
            "Pending",
            "Validating",
            "Processing",
            "Completed"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBookingListBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        navController = Navigation.findNavController(view);
        initTabs();
        initObservers();
        initListeners();
        super.onViewCreated(view, savedInstanceState);
    }

    private void initTabs() {
        binding.tabLayout.setTabMode(TabLayout.MODE_FIXED);
        for(int i = 0; i < tabs.length; i++){
            TabLayout.Tab newTab = binding.tabLayout.newTab();
            newTab.setId(i);
            newTab.setText(tabs[i]);
            ErrorLog.WriteDebugLog("initTabs: " + tabs[i]);
            binding.tabLayout.addTab(newTab);
        }
        binding.tabLayout.addOnTabSelectedListener(this);

        for (int i = 0; i < tabs.length; i++){
            TabLayout.Tab currentTab = binding.tabLayout.getTabAt(i);
            if(currentTab.getId() == 0){
                currentTab.select();
            }
        }
    }

    private void initObservers() {
    }

    private void initListeners() {
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}