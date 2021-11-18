package com.aix.mpagents.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aix.mpagents.databinding.ItemShopAddressBinding;
import com.aix.mpagents.interfaces.ShopAddressInterface;
import com.aix.mpagents.models.ShopAddress;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ShopAddressFirestoreAdapter extends FirestoreRecyclerAdapter<ShopAddress, ShopAddressFirestoreAdapter.ViewHolder> {

    private Context context;
    private ShopAddressInterface shopAddressInterface;

    public ShopAddressFirestoreAdapter(@NonNull FirestoreRecyclerOptions<ShopAddress> options, Context context, ShopAddressInterface shopAddressInterface) {
        super(options);
        this.context = context;
        this.shopAddressInterface = shopAddressInterface;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ShopAddress shopAddress) {
        holder.binding.setShopAddress(shopAddress);
        holder.binding.setShopAddressInterface(shopAddressInterface);
        String street = shopAddress.getShop_blkbldgnumber() + " " + shopAddress.getShop_street();
        String barangay = "Barangay " + shopAddress.getShop_barangay();
        String city = shopAddress.getShop_citymun();
        String addressType;
        holder.binding.textViewBlkNumAndStreet.setText(street);
        holder.binding.textViewBarangay.setText(barangay);
        holder.binding.textViewCity.setText(city);

        if(shopAddress.isIs_business()){
            addressType= "(Business)";
        }else{
            addressType = "";
        }
        holder.binding.textViewAddressType.setText(addressType);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemShopAddressBinding binding = ItemShopAddressBinding.inflate(layoutInflater,parent,false);
        return new ViewHolder(binding);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemShopAddressBinding binding;
        public ViewHolder(ItemShopAddressBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
