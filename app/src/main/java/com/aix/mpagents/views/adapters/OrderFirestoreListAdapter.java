package com.aix.mpagents.views.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.ItemOrderBinding;
import com.aix.mpagents.interfaces.OrderInterface;
import com.aix.mpagents.models.Order;
import com.aix.mpagents.utilities.DateHelper;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class OrderFirestoreListAdapter extends FirestoreRecyclerAdapter<Order, OrderFirestoreListAdapter.ViewHolder> {

        private Context context;
        private OrderInterface orderInterface;

        public OrderFirestoreListAdapter(@NonNull FirestoreRecyclerOptions<Order> options, Context context, OrderInterface orderInterface) {
            super(options);
            this.context = context;
            this.orderInterface = orderInterface;
        }

        @Override
        protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Order model) {
            holder.binding.setOrderInfo(model);
            holder.binding.setOrderInterface(orderInterface);
            holder.binding.textViewOrderDate.setText(DateHelper.formatDate(model.getOrder_date()));

            Glide.with(context).load(Uri.parse(model.getPreview_image()))
    //                .apply(requestOptions)
    //                .transition(DrawableTransitionOptions.withCrossFade())
                    .centerInside()
                    .error(R.drawable.ic_baseline_photo_24).into(holder.binding.imageView);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            ItemOrderBinding binding = ItemOrderBinding.inflate(layoutInflater,parent,false);
            return new ViewHolder(binding);
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            ItemOrderBinding binding;
            public ViewHolder(ItemOrderBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
}
