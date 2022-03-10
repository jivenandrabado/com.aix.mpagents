package com.aix.mpagents.views.fragments.services;

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
import com.aix.mpagents.databinding.FragmentEditServiceBinding;
import com.aix.mpagents.interfaces.EditProductInterface;
import com.aix.mpagents.interfaces.VariantInterface;
import com.aix.mpagents.models.Category;
import com.aix.mpagents.models.Media;
import com.aix.mpagents.models.ServiceInfo;
import com.aix.mpagents.models.Variant;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.utilities.ToastUtil;
import com.aix.mpagents.view_models.ProductViewModel;
import com.aix.mpagents.view_models.ServiceViewModel;
import com.aix.mpagents.views.adapters.EditProductPhotoViewAdapter;
import com.aix.mpagents.views.adapters.VariantsFirestoreAdapter;
import com.aix.mpagents.views.fragments.base.BaseAddEditServiceItemFragment;
import com.aix.mpagents.views.fragments.base.BaseServiceFragment;
import com.aix.mpagents.views.fragments.dialogs.AddVariantDialog;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditServiceFragment extends BaseAddEditServiceItemFragment {

    private FragmentEditServiceBinding binding;

    private ServiceInfo serviceInfo;

    private List<String> deletePhotoList = new ArrayList<>();

    private Category categoryModel;

    private EditProductPhotoViewAdapter editProductPhotoViewAdapter;

    private VariantsFirestoreAdapter variantsFirestoreAdapter;

    public EditServiceFragment() {
        super(R.layout.fragment_edit_service);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentEditServiceBinding.bind(getView());

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

        navController.popBackStack(R.id.editServiceFragment,true);
    }

    private void initVariantFirestoreOptions() {
        variantsFirestoreAdapter = new VariantsFirestoreAdapter(
                getServiceViewModel().getVariantRecyclerOptions(serviceInfo.getService_id()),
                this
        );

        variantsFirestoreAdapter.setHasStableIds(true);

        binding.recyclerViewVariants.setAdapter(variantsFirestoreAdapter);

        binding.recyclerViewVariants.setLayoutManager(new LinearLayoutManager(requireContext()));

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
            bundle.putString("product_type", "Service");
            navController.navigate(R.id.action_editServiceFragment_to_categoryFragment,bundle);
        });
    }

    private void initView() {
        try {
            serviceInfo = getServiceViewModel().getSelectedService().getValue();

            binding.editTextProductName.setText(serviceInfo.getService_name());

            binding.editTextPrice.setText(String.valueOf(serviceInfo.getService_price()));

            binding.editTextDescription.setText(serviceInfo.getService_desc());

            binding.textViewCategoryValue.setText(serviceInfo.getCategory_name());

        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }

        getServiceViewModel().getMedia(serviceInfo.getService_id());

        getServiceViewModel().getMediaList().observe(getViewLifecycleOwner(), media -> {
            if(media != null) {

                List<String> uris = new ArrayList<>();

                for (int i = 0; i < media.size(); i++) uris.add(media.get(i).getPath());

                initImageRecyclerview(uris);

                getServiceViewModel().getMediaList().setValue(null);
            }
        });
    }

    @Override
    public void onItemUpdated() {
        super.onItemUpdated();

        showToast("Service Updated");

        navController.popBackStack(R.id.editServiceFragment,true);
    }

    private void updateProduct() {

        String product_name, description, category;

        double product_price = 0;

        product_name = String.valueOf(binding.editTextProductName.getText()).trim();

        if(!String.valueOf(binding.editTextPrice.getText()).isEmpty())
            product_price = Double.parseDouble(String.valueOf(binding.editTextPrice.getText()).trim());

        description = String.valueOf(binding.editTextDescription.getText()).trim();

        category = String.valueOf(binding.textViewCategoryValue.getText()).trim();

        if(!isEmptyFields(product_name, description, category, String.valueOf(product_price), editProductPhotoViewAdapter.getItems(), getProductType().getName())) {
            serviceInfo.setService_name(product_name);

            serviceInfo.setService_price(product_price);

            serviceInfo.setService_desc(description);

            serviceInfo.setDateUpdated(new Date());

            if(categoryModel!=null) {
                serviceInfo.setCategory_name(category);
                serviceInfo.setCategory_id(categoryModel.getCategory_id());
            }

            ErrorLog.WriteDebugLog("PRODUCT ID "+ serviceInfo.getService_id());

            getServiceViewModel().updateService(serviceInfo,editProductPhotoViewAdapter.getItems(),deletePhotoList);
        }
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
        if(variantsFirestoreAdapter != null) variantsFirestoreAdapter.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();

        variantsFirestoreAdapter.stopListening();
    }

    @Override
    public void onImageRemove(String uri, int position) {

        deletePhotoList.add(uri);

        editProductPhotoViewAdapter.getItems().remove(position);

        editProductPhotoViewAdapter.notifyItemRemoved(position);

    }

    @Override
    public void onVariantAdd(Variant variant) {
        getServiceViewModel().addVariant(variant, serviceInfo.getService_id());
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
        getServiceViewModel().deleteVariant(variant, serviceInfo.getService_id());
    }

    @Override
    public void onVariantDelete(int position, Variant variant) {

    }

    @Override
    public void onVariantUpdate(Variant variant) {
        getServiceViewModel().updateVariant(variant, serviceInfo.getService_id());
    }

    @Override
    public void onVariantUpdate(int position, Variant variant) {

    }

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

    @Override
    public void onPictureSelected(List<String> uriList) {
        initImageRecyclerview(uriList);
    }

}