package com.aix.mpagents.views.fragments.products;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.aix.mpagents.databinding.FragmentEditProductBinding;
import com.aix.mpagents.interfaces.EditProductInterface;
import com.aix.mpagents.interfaces.VariantInterface;
import com.aix.mpagents.models.Category;
import com.aix.mpagents.models.Media;
import com.aix.mpagents.models.ProductInfo;
import com.aix.mpagents.models.Variant;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.view_models.ProductViewModel;
import com.aix.mpagents.views.adapters.EditProductPhotoViewAdapter;
import com.aix.mpagents.views.adapters.ProductsFirestoreAdapter;
import com.aix.mpagents.views.adapters.VariantsFirestoreAdapter;
import com.aix.mpagents.views.fragments.base.BaseAddEditProductItemFragment;
import com.aix.mpagents.views.fragments.dialogs.AddVariantDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditProductFragment extends BaseAddEditProductItemFragment{

    private FragmentEditProductBinding binding;

    private ProductInfo productInfo;

    private List<String> deletePhotoList = new ArrayList<>();

    private Category categoryModel;

    private EditProductPhotoViewAdapter editProductPhotoViewAdapter;

    private VariantsFirestoreAdapter variantsFirestoreAdapter;

    public EditProductFragment() {
        super(R.layout.fragment_edit_product);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentEditProductBinding.bind(getView());

        initListeners();

        initView();

        initVariantFirestoreOptions();
    }

    @Override
    public void onCategorySelected(Category category) {
        binding.textViewCategoryValue.setText(category.getCategory_name());
        categoryModel = category;
    }

    @Override
    public void onItemSaved() {
        super.onItemSaved();
        navController.popBackStack(R.id.addProductFragment,true);
    }

    private void initVariantFirestoreOptions() {
        variantsFirestoreAdapter = new VariantsFirestoreAdapter(
                getProductViewModel().getVariantRecyclerOptions(productInfo.getProduct_id()),
                this
        );

        binding.recyclerViewVariants.setAdapter(variantsFirestoreAdapter);

        binding.recyclerViewVariants.setLayoutManager(new LinearLayoutManager(requireContext()));
        //temporary fix for recyclerview
        binding.recyclerViewVariants.setItemAnimator(null);
    }

    private void initListeners() {
        binding.textViewAddVariant.setOnClickListener(v -> {
            new AddVariantDialog(this)
                    .show(requireActivity().getSupportFragmentManager(), "ADD_VARIANT");
        });

        binding.buttonSubmit.setOnClickListener(view -> updateProduct());

        binding.buttonAddImage.setOnClickListener(view -> chooseImage());

        binding.textViewCategory.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("product_type", "Product");
            navController.navigate(R.id.action_editProductFragment_to_categoryFragment,bundle);
        });
    }

    private void initView() {
        try {
            productInfo = getProductViewModel().getSelectedProduct().getValue();
            binding.editTextProductName.setText(productInfo.getProduct_name());
            binding.editTextPrice.setText(String.valueOf(productInfo.getProduct_price()));
            binding.editTextDescription.setText(productInfo.getProduct_desc());
            binding.textViewCategoryValue.setText(productInfo.getCategory_name());
            binding.editTextQuantity.setText(String.valueOf(productInfo.getProduct_quantity()));
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }

        getProductViewModel().getMedia(productInfo.getProduct_id());
        getProductViewModel().getMediaList().observe(getViewLifecycleOwner(), media -> {
            if(media != null) {
                List<String> uris = new ArrayList<>();

                for (int i = 0; i < media.size(); i++) { uris.add(media.get(i).getPath()); }

                initImageRecyclerview(uris);

                getProductViewModel().getMediaList().setValue(null);
            }

        });
    }

    @Override
    public void onItemUpdated() {
        super.onItemUpdated();
        navController.popBackStack(R.id.editProductFragment,true);
    }

    private void updateProduct() {

        String product_name, description, category;

        double product_price = 0;

        product_name = String.valueOf(binding.editTextProductName.getText()).trim();

        if(!String.valueOf(binding.editTextPrice.getText()).isEmpty()) {
            product_price = Double.parseDouble(String.valueOf(binding.editTextPrice.getText()).trim());
        }

        description = String.valueOf(binding.editTextDescription.getText()).trim();

        category = String.valueOf(binding.textViewCategoryValue.getText()).trim();

        if(!isEmptyFields(product_name,description,category, String.valueOf(product_price), editProductPhotoViewAdapter.getItems(), getProductType().getName())) {
            productInfo.setProduct_name(product_name);
            productInfo.setProduct_price(product_price);
            productInfo.setProduct_desc(description);
            productInfo.setDateUpdated(new Date());
            productInfo.setProduct_quantity(Integer.parseInt(String.valueOf(binding.editTextQuantity.getText())));
            productInfo.setCategory_name(category);
            productInfo.setIs_deleted(false);

            if(categoryModel!=null) {
                productInfo.setCategory_name(category);
                productInfo.setCategory_id(categoryModel.getCategory_id());
            }

            getProductViewModel().updateProduct(productInfo,editProductPhotoViewAdapter.getItems(),deletePhotoList);
        }
    }

    @Override
    public void onPictureSelected(List<String> uriList) {
        initImageRecyclerview(uriList);
    }

    private void initImageRecyclerview(List<String> photoList){

        editProductPhotoViewAdapter = new EditProductPhotoViewAdapter(photoList,requireContext(),this);

        editProductPhotoViewAdapter.setHasStableIds(true);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        binding.recyclerView.setAdapter(editProductPhotoViewAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(variantsFirestoreAdapter != null){
            variantsFirestoreAdapter.startListening();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        variantsFirestoreAdapter.stopListening();
    }

    @Override
    public void onImageRemove(String uri, int photoPosition) {
        deletePhotoList.add(uri);
        editProductPhotoViewAdapter.getItems().remove(photoPosition);
        editProductPhotoViewAdapter.notifyItemRemoved(photoPosition);
    }

    @Override
    public void onVariantAdd(Variant variant) {
        getProductViewModel().addVariant(variant, productInfo.getProduct_id());
    }

    @Override
    public void onVariantClick(Variant variant) {
        new AddVariantDialog(this,variant)
                .show(requireActivity().getSupportFragmentManager(), "ADD_VARIANT");
    }

    @Override
    public void onVariantClick(int position, Variant variant) {

    }

    @Override
    public void onVariantDelete(Variant variant) {
        getProductViewModel().deleteVariant(variant, productInfo.getProduct_id());
    }

    @Override
    public void onVariantDelete(int position, Variant variant) {

    }

    @Override
    public void onVariantUpdate(Variant variant) {
        getProductViewModel().updateVariant(variant, productInfo.getProduct_id());
    }

    @Override
    public void onVariantUpdate(int position, Variant variant) { }

    @Override
    public Variant getIsVariantDuplicate(String name) {
        Variant variant = null;
        for(Variant variant1:  variantsFirestoreAdapter.getSnapshots()){
            if(name.trim().equalsIgnoreCase(variant1.variant_name.trim())){
                variant = variant1;
                break;
            }
        }
        return variant;
    }
}