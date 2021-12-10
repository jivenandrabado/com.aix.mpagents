package com.aix.mpagents.view_models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aix.mpagents.models.Order;
import com.aix.mpagents.repositories.FirebaseOrderRepo;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class OrderViewModel  extends ViewModel {

    private FirebaseOrderRepo orderRepo = FirebaseOrderRepo.getInstance();
    private MutableLiveData<Order> selectedOrder = new MutableLiveData<>();

    public FirestoreRecyclerOptions orderFirestoreRecyclerOptions(){
        return orderRepo.getOrderFirestoreRecyclerOptions();
    }

    public MutableLiveData<Order> getSelectedOrder(){
        return selectedOrder;
    }

    public void addOrderSnapshotListener(String order_id){
        orderRepo.addOrderSnapShotListener(order_id);
    }

    public void detachOrderListener(){
        orderRepo.detachOrderListener();
    }

    public MutableLiveData<Order> getOrderSnapshot(){
        return orderRepo.getGetOrderSnapshot();
    }

    public void updateOrderStatus(String order_id, String status) {
        orderRepo.updateOrderStatus(order_id, status);
    }

    public FirestoreRecyclerOptions orderStatusOptions(String order_status){
        return orderRepo.getOrderStatusOptions(order_status);
    }
    public void addOrderPendingSnapshotListener(){
        orderRepo.addOrderSnapshotListenerPending();
    }

    public MutableLiveData<Integer> getPendingOrder() {
        return orderRepo.getPendingOrderLiveData();
    }

    public MutableLiveData<Integer> getCompletedOrder() {
        return orderRepo.getCompletedOrderLiveData();
    }

    public void detachPendingOrderListener(){
        orderRepo.detachPendingOrdersListener();
    }
}
