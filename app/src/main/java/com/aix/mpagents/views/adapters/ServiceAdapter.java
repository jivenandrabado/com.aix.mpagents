package com.aix.mpagents.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aix.mpagents.databinding.ItemServiceBinding;
import com.aix.mpagents.interfaces.ServiceInterface;
import com.aix.mpagents.models.ServiceInfo;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceFirestoreAdapter.ViewHolder> {

    private List<ServiceInfo> services;
    private ServiceInterface serviceInterface;
    private Context context;

    public ServiceAdapter(List<ServiceInfo> services, ServiceInterface serviceInterface, Context context){
        this.services = services;
        this.serviceInterface = serviceInterface;
        this.context = context;

    }
    @NonNull
    @Override
    public ServiceFirestoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemServiceBinding binding = ItemServiceBinding.inflate(layoutInflater,parent,false);
        return new ServiceFirestoreAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceFirestoreAdapter.ViewHolder holder, int position) {
        ServiceInfo product = services.get(position);
        holder.updateData(product, serviceInterface);
    }

    @Override
    public int getItemCount() {
        return services.size();
    }


}
