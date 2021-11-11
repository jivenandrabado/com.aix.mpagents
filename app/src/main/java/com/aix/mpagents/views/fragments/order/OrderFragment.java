package com.aix.mpagents.views.fragments.order;

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
import com.aix.mpagents.databinding.FragmentOrdersBinding;
import com.aix.mpagents.interfaces.OrderInterface;
import com.aix.mpagents.models.Order;
import com.aix.mpagents.view_models.OrderViewModel;
import com.aix.mpagents.views.adapters.OrderFirestoreListAdapter;
import com.google.android.material.tabs.TabLayout;


public class OrderFragment extends Fragment implements OrderInterface {
    private FragmentOrdersBinding binding;
    private NavController navController;
    private OrderViewModel orderViewModel;
    private OrderFirestoreListAdapter orderFirestoreListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrdersBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        initProductsRecyclerView();

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.recyclerViewOrder.removeAllViews();
                if(tab.getPosition()>0) {
                    orderFirestoreListAdapter.updateOptions(orderViewModel.orderStatusOptions(String.valueOf(tab.getText())));
                }else{
                    orderFirestoreListAdapter.updateOptions(orderViewModel.orderFirestoreRecyclerOptions());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



    }

    private void initProductsRecyclerView(){
        orderFirestoreListAdapter = new OrderFirestoreListAdapter(orderViewModel.orderFirestoreRecyclerOptions(),requireContext(),this);
        orderFirestoreListAdapter.setHasStableIds(true);

        binding.recyclerViewOrder.setAdapter(orderFirestoreListAdapter);
        binding.recyclerViewOrder.setLayoutManager(new LinearLayoutManager(requireContext()));
        //temporary fix for recyclerview
        binding.recyclerViewOrder.setItemAnimator(null);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(orderFirestoreListAdapter!=null) {
            orderFirestoreListAdapter.startListening();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(orderFirestoreListAdapter!=null) {
            orderFirestoreListAdapter.stopListening();
        }
    }

    @Override
    public void onOrderClick(Order order) {
        orderViewModel.getSelectedOrder().setValue(order);
        navController.navigate(R.id.action_orderFragment_to_orderDetailsFragment);
    }
}