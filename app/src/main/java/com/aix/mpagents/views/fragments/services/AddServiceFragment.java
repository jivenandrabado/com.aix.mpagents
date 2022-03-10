package com.aix.mpagents.views.fragments.services;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

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

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.FragmentAddServiceBinding;
import com.aix.mpagents.interfaces.AddProductInterface;
import com.aix.mpagents.interfaces.EditProductInterface;
import com.aix.mpagents.interfaces.VariantInterface;
import com.aix.mpagents.models.Category;
import com.aix.mpagents.models.ProductInfo;
import com.aix.mpagents.models.ProductType;
import com.aix.mpagents.models.ServiceInfo;
import com.aix.mpagents.models.Variant;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.view_models.ProductViewModel;
import com.aix.mpagents.view_models.ServiceViewModel;
import com.aix.mpagents.views.adapters.AddProductPhotoViewAdapter;
import com.aix.mpagents.views.adapters.VariantAdapter;
import com.aix.mpagents.views.fragments.base.BaseAddEditServiceItemFragment;
import com.aix.mpagents.views.fragments.base.BaseFragment;
import com.aix.mpagents.views.fragments.base.BaseServiceFragment;
import com.aix.mpagents.views.fragments.dialogs.AddVariantDialog;
import com.aix.mpagents.views.fragments.dialogs.ProgressDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddServiceFragment extends BaseAddEditServiceItemFragment {

    private FragmentAddServiceBinding binding;

    private List<String> photoList = new ArrayList<>();

    private Category categoryModel;

    private AddProductPhotoViewAdapter addProductPhotoViewAdapter;

    private VariantAdapter variantAdapter;

    private List<Variant> variants = new ArrayList<>();

    public AddServiceFragment() {
        super(R.layout.fragment_add_service);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentAddServiceBinding.bind(getView());

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

        navController.popBackStack(R.id.addServiceFragment,true);

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
        binding.buttonSubmit.setOnClickListener(view -> addService());

        binding.buttonAddImage.setOnClickListener(view -> chooseImage());

        binding.textViewCategory.setOnClickListener(view -> {
            if(getProductType() != null) {
                Bundle bundle = new Bundle();
                bundle.putString("product_type", "Service");
                navController.navigate(R.id.action_addServiceFragment_to_categoryFragment,bundle);
            }else{
                Toast.makeText(requireContext(),"Please select product type",Toast.LENGTH_LONG).show();
            }
        });

        binding.textViewAddVariant.setOnClickListener(v -> {
            new AddVariantDialog(this).show(requireActivity().getSupportFragmentManager(), "ADD_VARIANT");
        });

    }

    private void initImageRecyclerview(List<String> photoList){

        addProductPhotoViewAdapter = new AddProductPhotoViewAdapter(photoList,requireContext(),this);

        addProductPhotoViewAdapter.setHasStableIds(true);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        binding.recyclerView.setAdapter(addProductPhotoViewAdapter);

    }

    private void addService(){
        showLoading(true);

        String product_name, description, category;

        double product_price = 0;

        product_name = String.valueOf(binding.editTextProductName.getText()).trim();

        if(!String.valueOf(binding.editTextPrice.getText()).isEmpty()) {
            product_price = Double.parseDouble(String.valueOf(binding.editTextPrice.getText()).trim());
        }

        description = String.valueOf(binding.editTextDescription.getText()).trim();

        category = String.valueOf(binding.textViewCategoryValue.getText()).trim();

        if(!isEmptyFields(product_name,description,category, String.valueOf(product_price), photoList, getProductType().getName())) {
            ServiceInfo serviceInfo = new ServiceInfo();
            serviceInfo.setService_name(product_name);
            serviceInfo.setService_price(product_price);
            serviceInfo.setService_desc(description);
            serviceInfo.setDateCreated(new Date());
            serviceInfo.setCategory_name(category);
            serviceInfo.setRating(0);
            serviceInfo.setCategory_id(categoryModel.getCategory_id());
            serviceInfo.setIs_deleted(false);
            serviceInfo.setService_status(ProductInfo.Status.DRAFT);
            serviceInfo.setSearch_name(product_name.toLowerCase() + " " + description.toLowerCase() );
            getServiceViewModel().addService(serviceInfo, photoList, variants);
        }
    }



    @Override
    public void onVariantAdd(Variant variant) {
        variants.add(variant);
        variantAdapter.notifyItemInserted(variants.size()-1);
    }

    @Override
    public void onVariantClick(Variant variant) {

    }

    @Override
    public void onVariantClick(int position, Variant variant) {
        new AddVariantDialog(this,variant, position)
                .show(requireActivity().getSupportFragmentManager(), "UPDATE_VARIANT");
    }

    @Override
    public void onVariantDelete(Variant variant) {

    }

    @Override
    public void onVariantDelete(int position, Variant variant) {
        variants.remove(position);

        variantAdapter.notifyItemRemoved(position);

        variantAdapter.notifyItemRangeChanged(0, position);
    }

    @Override
    public void onVariantUpdate(Variant variant) {

    }

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
        addProductPhotoViewAdapter.notifyItemRemoved(position);
    }
}