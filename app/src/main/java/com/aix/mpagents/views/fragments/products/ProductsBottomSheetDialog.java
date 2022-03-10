package com.aix.mpagents.views.fragments.products;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.aix.mpagents.databinding.BottomSheetDialogProductsBinding;
import com.aix.mpagents.interfaces.ProductInterface;
import com.aix.mpagents.models.ProductInfo;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.view_models.ProductViewModel;
import com.aix.mpagents.views.fragments.dialogs.DeleteProductDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ProductsBottomSheetDialog extends BottomSheetDialogFragment {

    private ProductViewModel productViewModel;

    private BottomSheetDialogProductsBinding binding;

    private ProductInfo productInfo;

    private DeleteProductDialog deleteProductDialog;

    private ProductInterface productInterface;

    public ProductsBottomSheetDialog(ProductInterface productInterface) {
        this.productInterface = productInterface;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = BottomSheetDialogProductsBinding.inflate(inflater,container,false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();

        binding.textViewDelete.setOnClickListener(view1 -> initProductDeleteDialog());

        binding.textViewEdit.setOnClickListener(view12 -> productInterface.onEditProduct(productViewModel.getSelectedProduct().getValue()));


        productViewModel.isProductDeleted().observe(getViewLifecycleOwner(), aBoolean -> {
            if(aBoolean){
                dismiss();
                productViewModel.isProductDeleted().setValue(false);
            }
        });
    }

    private void initProductDeleteDialog() {
        deleteProductDialog = new DeleteProductDialog();
        deleteProductDialog.show(getChildFragmentManager(),"DELETE PRODUCT DIALOG");
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
}