package com.aix.mpagents.views.fragments.order;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.aix.mpagents.databinding.FragmentOrderDetailsBinding;
import com.aix.mpagents.models.Order;
import com.aix.mpagents.utilities.DateHelper;
import com.aix.mpagents.utilities.OrderStatus;
import com.aix.mpagents.view_models.OrderViewModel;
import com.aix.mpagents.views.fragments.dialogs.OrderCompleteDialog;
import com.bumptech.glide.Glide;

public class OrderDetailsFragment extends Fragment {
    private FragmentOrderDetailsBinding binding;
    private NavController navController;
    private OrderViewModel orderViewModel;
    private Order order;
    private String status;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrderDetailsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        order = orderViewModel.getSelectedOrder().getValue();
        navController = Navigation.findNavController(view);

        initViews();

        binding.buttonDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateOrderStatus(order.getOrder_id(),status);
            }
        });

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.popBackStack(R.id.orderDetailsFragment,true);
            }
        });



    }

    private void initViews(){
        if (order != null) {
            orderViewModel.addOrderSnapshotListener(order.getOrder_id());

            orderViewModel.getOrderSnapshot().observe(getViewLifecycleOwner(), new Observer<Order>() {
                @Override
                public void onChanged(Order order) {
                    if(order!= null){

                        binding.textViewOrderID.setText(order.getOrder_id());
                        binding.textViewOrderDate.setText(DateHelper.formatDate(order.getOrder_date()));
                        binding.textViewOrderStatus.setText(order.getOrder_status());
                        binding.textViewOrderStatusTitle.setText(order.getOrder_status());

                        binding.textViewCustomerName.setText(order.getCustomer_name());
                        binding.textViewShippingAddress.setText(order.getDelivery_address());
                        binding.textViewPhoneNo.setText(order.getContact_number());

                        binding.textViewProductName.setText(order.getProduct_name());
                        binding.textViewProductPrice.setText(String.valueOf(order.getProduct_price()));

                        Glide.with(requireContext()).load(Uri.parse(order.getPreview_image()))
                                .fitCenter()
                                .error(R.drawable.ic_baseline_photo_24).into((binding.imageView));

                        binding.textViewModeofPayment.setText(order.getMop_option());

                        String order_status = order.getOrder_status();
                        if(order_status.equalsIgnoreCase(OrderStatus.Pending.toString())){
                            binding.buttonDefault.setText("Create Package");
                            binding.buttonDefault.setVisibility(View.VISIBLE);

                            status = "ToPack";
                        }else if (order_status.equalsIgnoreCase(OrderStatus.ToPack.toString())){
                            binding.buttonDefault.setText("Ready to Ship");
                            binding.buttonDefault.setVisibility(View.VISIBLE);

                            status = "ToShip";
                        }else if (order_status.equalsIgnoreCase(OrderStatus.ToShip.toString())){
                            binding.buttonDefault.setText("Delivery Complete");
                            binding.buttonDefault.setVisibility(View.VISIBLE);

                            status = "Completed";
                        }else if (order_status.equalsIgnoreCase(OrderStatus.Completed.toString())){
                            binding.buttonDefault.setVisibility(View.INVISIBLE);
                        }


                    }
                }
            });
        }

    }

    private void updateOrderStatus(String order_id,String status){
        orderViewModel.updateOrderStatus(order_id,status);

        if(status.equalsIgnoreCase("completed")){
            OrderCompleteDialog orderCompleteDialog = new OrderCompleteDialog();
            orderCompleteDialog.show(getChildFragmentManager(), "ORDE COMPLETED DIALOG");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        orderViewModel.detachOrderListener();

        orderViewModel.getOrderSnapshot().setValue(null);
    }
}