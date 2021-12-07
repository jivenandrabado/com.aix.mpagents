package com.aix.mpagents.repositories;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.aix.mpagents.models.Order;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.utilities.FirestoreConstants;
import com.aix.mpagents.utilities.OrderStatus;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import kotlin.collections.CollectionsKt;

public class FirebaseOrderRepo {

    private FirebaseFirestore db;
    private String userId;
    private ListenerRegistration orderRegistration;
    private MutableLiveData<Order> getOrderSnapshot = new MutableLiveData<>();
    private final MutableLiveData<Integer> pendingOrderStatusLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> completedOrderStatusLiveData = new MutableLiveData<>();
    private static FirebaseOrderRepo instance;
    private ListenerRegistration pendingOrderListener;
    private int pending, completed;

    public static FirebaseOrderRepo getInstance() {
        if(instance == null){
            instance = new FirebaseOrderRepo();
        }
        return instance;
    }

    public FirebaseOrderRepo() {
        db = FirebaseFirestore.getInstance();
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    }

    public FirestoreRecyclerOptions getOrderFirestoreRecyclerOptions() {
        Query query = db.collection(FirestoreConstants.MPARTNER_ORDER)
                .whereEqualTo("merchant_id", userId)
                .orderBy("order_date");
        return new FirestoreRecyclerOptions.Builder<Order>()
                .setQuery(query, Order.class)
                .build();
    }

    public void addOrderSnapShotListener(String order_id) {

        try{
            orderRegistration = db.collection(FirestoreConstants.MPARTNER_ORDER).document(order_id)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(value!=null){
                                if(value.exists()){
                                    getOrderSnapshot.setValue(value.toObject(Order.class));
                                }
                            }
                        }
                    });
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }

    public MutableLiveData<Order> getGetOrderSnapshot(){
        return getOrderSnapshot;
    }

    public void detachOrderListener(){
        if(orderRegistration!=null){
            orderRegistration.remove();
        }
    }

    public void updateOrderStatus(String order_id, String status) {
        try{

            Map<String,Object> orderStatus = new HashMap<>();
            orderStatus.put("order_status", status);
            if(status.equalsIgnoreCase(OrderStatus.ToPack.toString())){
                orderStatus.put("order_date_topack", new Date());
            }else if(status.equalsIgnoreCase(OrderStatus.ToShip.toString())){
                orderStatus.put("order_date_toship", new Date());
            }else if(status.equalsIgnoreCase(OrderStatus.Delivered.toString())){
                orderStatus.put("order_date_delivered", new Date());
            }else if(status.equalsIgnoreCase(OrderStatus.Completed.toString())){
                orderStatus.put("order_date_completed", new Date());
            }

            db.collection(FirestoreConstants.MPARTNER_ORDER).document(order_id).update(orderStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        ErrorLog.WriteDebugLog("ORDER STATUS UPDATED");
                    }else{
                        ErrorLog.WriteErrorLog(task.getException());
                    }
                }
            });


        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }
    }
    public FirestoreRecyclerOptions getOrderStatusOptions(String order_status) {
        ErrorLog.WriteDebugLog("ON UPDATE OPTIONS "+order_status);
        Query query = db.collection(FirestoreConstants.MPARTNER_ORDER)
                .whereEqualTo("merchant_id", userId)
                .whereEqualTo("order_status",order_status)
                .orderBy("order_date");
        return new FirestoreRecyclerOptions.Builder<Order>()
                .setQuery(query, Order.class)
                .build();
    }

    public void addOrderSnapshotListenerPending(){

        pending = 0;
        completed = 0;
        List<Order> orderList = new ArrayList<>();

        pendingOrderListener = db.collection(FirestoreConstants.MPARTNER_ORDER)
                .whereEqualTo("merchant_id", userId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value != null) {
                            if (!value.isEmpty()) {
                                 for(DocumentChange dc : value.getDocumentChanges()) {
                                     Order order = dc.getDocument().toObject(Order.class);
                                     switch (dc.getType()) {
                                         case ADDED:
                                             orderList.add(dc.getDocument().toObject(Order.class));
                                             break;
                                         case MODIFIED:
                                             Order order1 = dc.getDocument().toObject(Order.class);
                                             if (dc.getOldIndex() == dc.getNewIndex()) {
                                                 // Item changed but remained in same position
                                                 orderList.set(dc.getOldIndex(),order1);
                                             }else {
                                                 // Item changed and changed position
                                                 orderList.remove(dc.getOldIndex());
                                                 orderList.add(dc.getNewIndex(), order1);
                                             }

                                             break;
                                         case REMOVED:
                                             orderList.remove(dc.getOldIndex());
                                             break;

                                     }
                                 }

                                 pending = CollectionsKt.filter(orderList, s -> s.getOrder_status().equalsIgnoreCase(String.valueOf(OrderStatus.Pending))).size();
                                 completed= CollectionsKt.filter(orderList, s -> s.getOrder_status().equalsIgnoreCase(String.valueOf(OrderStatus.Completed))).size();

                                 pendingOrderStatusLiveData.setValue(pending);
                                 completedOrderStatusLiveData.setValue(completed);

                            }
                        }
                    }
                });


    }

    public void detachPendingOrdersListener() {
        if(pendingOrderListener != null){
            pendingOrderListener.remove();
        }
    }

    public MutableLiveData<Integer> getPendingOrderLiveData() {
        return pendingOrderStatusLiveData;
    }

    public MutableLiveData<Integer> getCompletedOrderLiveData() {
        return completedOrderStatusLiveData;
    }
}
