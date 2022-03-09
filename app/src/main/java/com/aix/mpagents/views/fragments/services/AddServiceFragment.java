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
import com.aix.mpagents.views.fragments.dialogs.AddVariantDialog;
import com.aix.mpagents.views.fragments.dialogs.ProgressDialogFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddServiceFragment extends Fragment implements AddProductInterface, VariantInterface {

    private FragmentAddServiceBinding binding;
    private ProductViewModel productViewModel;
    private ServiceViewModel serviceViewModel;
    private List<String> photoList = new ArrayList<>();
    private NavController navController;
    private Category categoryModel;
    private AddProductPhotoViewAdapter addProductPhotoViewAdapter;
    private ProgressDialogFragment progressDialogFragment;
    private ProductType productTypeModel;
    private VariantAdapter variantAdapter;
    private List<Variant> variants = new ArrayList<>();

    private ActivityResultLauncher<Intent> chooseImageActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();

                        ClipData clipData = data.getClipData();
                        if (clipData != null) {
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Uri imageUri = clipData.getItemAt(i).getUri();
                                // your code for multiple image selection
                                ErrorLog.WriteDebugLog("DATA RECEIVED "+imageUri);
                                photoList.add(String.valueOf(imageUri));
                                initImageRecyclerview(photoList);

                            }
                        } else {
                            Uri uri = data.getData();
                            // your codefor single image selection
                            ErrorLog.WriteDebugLog("DATA RECEIVED "+uri);
                            photoList.add(String.valueOf(uri));
                            initImageRecyclerview(photoList);

                        }

                        ErrorLog.WriteDebugLog( "PHOTO LIST "+ photoList.size());
                    }
                }
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddServiceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
        serviceViewModel = new ViewModelProvider(requireActivity()).get(ServiceViewModel.class);
        progressDialogFragment = new ProgressDialogFragment();
        navController = Navigation.findNavController(view);
        initObservers();
        initVariantFirestoreOptions();
        initListeners();
    }

    private void initObservers() {
        serviceViewModel.isProductSaved().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    navController.popBackStack(R.id.addServiceFragment,true);
                    if(progressDialogFragment!=null){
                        progressDialogFragment.dismiss();
                    }
                    serviceViewModel.isProductSaved().setValue(false);
                }
            }
        });

        serviceViewModel.getSelectedCategory().observe(getViewLifecycleOwner(), new Observer<Category>() {
            @Override
            public void onChanged(Category category) {
                binding.textViewCategoryValue.setText(category.getCategory_name());
                categoryModel = category;
            }
        });

        productViewModel.getProductType("Service").observe(getViewLifecycleOwner(), new Observer<ProductType>() {
            @Override
            public void onChanged(ProductType productType) {
                if(productType != null) {
//                    binding.textViewProductTypeValue.setText(productType.getName());
                    productTypeModel = productType;
                    productViewModel.getSelectedProductType().setValue(productType);
                }
            }
        });
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
        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addService();
            }
        });

        binding.buttonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productViewModel.chooseImage(chooseImageActivityResult);
            }
        });

        binding.textViewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(productTypeModel != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("product_type", "Service");
                    navController.navigate(R.id.action_addServiceFragment_to_categoryFragment,bundle);
                }else{
                    Toast.makeText(requireContext(),"Please select product type",Toast.LENGTH_LONG).show();
                }
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

        String product_name, description, category, product_type;
        double product_price = 0;
        product_name = String.valueOf(binding.editTextProductName.getText()).trim();
        if(!String.valueOf(binding.editTextPrice.getText()).isEmpty()) {
            product_price = Double.parseDouble(String.valueOf(binding.editTextPrice.getText()).trim());
        }
        description = String.valueOf(binding.editTextDescription.getText()).trim();
        category = String.valueOf(binding.textViewCategoryValue.getText()).trim();
        product_type = productTypeModel.getName();


        if(!isEmptyFields(product_name,description,category, photoList, product_type)) {
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
            serviceViewModel.addService(serviceInfo, photoList, variants);
            showProgressDialog();
        }
    }

    private void showProgressDialog(){
        progressDialogFragment.show(getChildFragmentManager(),"ADD PRODUCT PROGRESS DIALOG");
    }

    private boolean isEmptyFields(String product_name, String description, String category, List<String> photoList, String product_type){

        if (TextUtils.isEmpty(product_name)){
            Toast.makeText(requireContext(), "Empty Product Name", Toast.LENGTH_LONG).show();
            return true;
        }else if (String.valueOf(binding.editTextPrice.getText()).isEmpty()){
            Toast.makeText(requireContext(), "Empty Prodcut Price", Toast.LENGTH_LONG).show();
            return true;
        }else if (TextUtils.isEmpty(description)) {
            Toast.makeText(requireContext(), "Empty Description", Toast.LENGTH_LONG).show();
            return true;
        }else if (TextUtils.isEmpty(category)) {
            Toast.makeText(requireContext(), "Empty Category", Toast.LENGTH_LONG).show();
            return true;
        }else if (photoList.isEmpty()) {
            Toast.makeText(requireContext(), "No product photo", Toast.LENGTH_LONG).show();
            return true;
        }else if (TextUtils.isEmpty(product_type)) {
            Toast.makeText(requireContext(), "Empty Product Type", Toast.LENGTH_LONG).show();
            return true;
        }else {
            return false;
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
    public void onImageRemove(int photoPosition) {
        photoList.remove(photoPosition);
        addProductPhotoViewAdapter.notifyItemRemoved(photoPosition);
    }
}