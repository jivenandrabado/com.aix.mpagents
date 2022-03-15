package com.aix.mpagents.views.fragments.products;

import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.FragmentAddProductBinding;
import com.aix.mpagents.interfaces.EditProductInterface;
import com.aix.mpagents.interfaces.VariantInterface;
import com.aix.mpagents.models.Category;
import com.aix.mpagents.models.ProductInfo;
import com.aix.mpagents.models.ProductType;
import com.aix.mpagents.models.Variant;
import com.aix.mpagents.utilities.AlertUtils;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.view_models.ProductViewModel;
import com.aix.mpagents.views.adapters.AddProductPhotoViewAdapter;
import com.aix.mpagents.views.adapters.VariantAdapter;
import com.aix.mpagents.views.fragments.base.BaseAddEditProductItemFragment;
import com.aix.mpagents.views.fragments.base.BaseFragment;
import com.aix.mpagents.views.fragments.dialogs.AddVariantDialog;
import com.aix.mpagents.views.fragments.dialogs.ProgressDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddProductFragment extends BaseAddEditProductItemFragment {

    private FragmentAddProductBinding binding;

    private Category categoryModel;

    private AddProductPhotoViewAdapter addProductPhotoViewAdapter;

    private VariantAdapter variantAdapter;

    private List<Variant> variants = new ArrayList<>();

    public AddProductFragment() {
        super(R.layout.fragment_add_product);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AlertUtils.addProductServiceExit(requireContext(), (dialog, which) -> {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            navController.popBackStack(R.id.addProductFragment, true);
                            dialog.dismiss();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            break;
                    }
                }, "Add Product");
            }
        });

        binding = FragmentAddProductBinding.bind(getView());

        initVariantFirestoreOptions();

        initListeners();
    }

    @Override
    public void onCategorySelected(Category category) {
        binding.textViewCategoryValue.setText(category.getCategory_name());
        categoryModel = category;
    }

    @Override
    public void onPictureSelected(List<String> uriList) {
        initImageRecyclerview(uriList);
    }

    @Override
    public void onItemSaved() {
        super.onItemSaved();
        navController.popBackStack(R.id.addProductFragment,true);
        showLoading(false);
    }

    private void initVariantFirestoreOptions() {
        variantAdapter = new VariantAdapter(this, variants);

        variantAdapter.setHasStableIds(true);

        binding.recyclerViewVariants.setAdapter(variantAdapter);

        binding.recyclerViewVariants.setLayoutManager(new LinearLayoutManager(requireContext()));
        //temporary fix for recyclerview
        binding.recyclerViewVariants.setItemAnimator(null);
    }

    private void initListeners() {
        binding.editTextQuantity.setText("0");

        binding.buttonSubmit.setOnClickListener(view -> addProduct());

        binding.buttonAddImage.setOnClickListener(view -> chooseImage());

        binding.textViewCategory.setOnClickListener(view -> {
            if(getProductType() != null) {
                Bundle bundle = new Bundle();
                bundle.putString("product_type", "Product");
                navController.navigate(R.id.action_addProductFragment_to_categoryFragment, bundle);
            }else{
                showToast("Please select product type");
            }
        });

        binding.textViewAddVariant.setOnClickListener(v -> {
            new AddVariantDialog(this).show(requireActivity().getSupportFragmentManager(), "ADD_VARIANT");
        });
    }

    private void addProduct() {
        showLoading(true);

        String product_name, description, category;

        double product_price = 0;

        product_name = String.valueOf(binding.editTextProductName.getText()).trim();

        if(!String.valueOf(binding.editTextPrice.getText()).isEmpty()) {
            try {
                product_price = Double.parseDouble(String.valueOf(binding.editTextPrice.getText()).trim());
            }catch (NumberFormatException e){
                showLoading(false);
                showToast("Invalid Price");
                return;
            }
        }

        description = String.valueOf(binding.editTextDescription.getText()).trim();

        category = String.valueOf(binding.textViewCategoryValue.getText()).trim();

        if(!isEmptyFields(product_name,description,category, String.valueOf(product_price), addProductPhotoViewAdapter.getItems(), getProductType().getName())) {
            ProductInfo productInfo = new ProductInfo();
            productInfo.setProduct_name(product_name);
            productInfo.setProduct_price(product_price);
            productInfo.setProduct_desc(description);
            productInfo.setDateCreated(new Date());
            productInfo.setCategory_name(category);
            productInfo.setRating(0);
            productInfo.setSold(0);
            productInfo.setCategory_id(categoryModel.getCategory_id());
            productInfo.setIs_deleted(false);
            productInfo.setIs_active(true);
            productInfo.setProduct_status(ProductInfo.Status.DRAFT);
            productInfo.setSearch_name(product_name.toLowerCase() + " " + description.toLowerCase() );
            productInfo.setProduct_quantity(Integer.parseInt(String.valueOf(binding.editTextQuantity.getText()).trim()));
            productInfo.setProduct_type_id(getProductType().getProduct_type_id());
            productInfo.setProduct_type(getProductType().getName());

            getProductViewModel().addProduct(productInfo, addProductPhotoViewAdapter.getItems(), variants);

        }else showLoading(false);
    }

    private void initImageRecyclerview(List<String> photoList){

        addProductPhotoViewAdapter = new AddProductPhotoViewAdapter(photoList,requireContext(),this);
        addProductPhotoViewAdapter.setHasStableIds(true);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerView.setAdapter(addProductPhotoViewAdapter);

    }

    @Override
    public void onVariantAdd(Variant variant) {
        variants.add(variant);
        variantAdapter.notifyItemInserted(variants.size()-1);
    }

    @Override
    public void onVariantClick(Variant variant) { }

    @Override
    public void onVariantClick(int position, Variant variant) {
        new AddVariantDialog(this,variant, position)
        .show(requireActivity().getSupportFragmentManager(), "UPDATE_VARIANT");
    }

    @Override
    public void onVariantDelete(Variant variant) { }

    @Override
    public void onVariantDelete(int position, Variant variant) {
        variants.remove(position);
        variantAdapter.notifyItemRemoved(position);
        variantAdapter.notifyItemRangeChanged(0, position);
    }

    @Override
    public void onVariantUpdate(Variant variant) { }

    @Override
    public void onVariantUpdate(int position, Variant variant) {
        variants.get(position).setVariant_name(variant.getVariant_name());
        variants.get(position).setStock(variant.getStock());
        variantAdapter.notifyItemChanged(position);
    }

    @Override
    public Variant getIsVariantDuplicate(String name) {
        Variant variant = null;
        for(Variant variant1: variants){
            if(name.trim().equalsIgnoreCase(variant1.variant_name.trim())){
                variant = variant1;
                break;
            }
        }
        return variant;
    }

    @Override
    public void onImageRemove(String uri, int position) {
        addProductPhotoViewAdapter.getItems().remove(position);
        addProductPhotoViewAdapter.notifyItemRemoved(position);
    }


}