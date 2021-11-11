package com.aix.mpagents.views.fragments.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.aix.mpagents.databinding.FragmentDeleteProductDialogBinding;
import com.aix.mpagents.models.ProductInfo;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.view_models.ProductViewModel;

public class DeleteProductDialog extends DialogFragment {

    private FragmentDeleteProductDialogBinding binding;
    private ProductViewModel productViewModel;
    private ProductInfo productInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDeleteProductDialogBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();

        binding.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteProduct(productInfo);
            }
        });

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        productViewModel.isProductDeleted().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    dismiss();
                }
            }
        });

    }

    private void initViews(){
        try {
            productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
            productInfo = productViewModel.getSelectedProduct().getValue();
            binding.textViewProductName.setText(productInfo.getProduct_name());
        }catch (Exception e){
            ErrorLog.WriteDebugLog(e);
        }
    }

    private void deleteProduct(ProductInfo productInfo){
        productViewModel.deleteProduct(productInfo);
    }
}