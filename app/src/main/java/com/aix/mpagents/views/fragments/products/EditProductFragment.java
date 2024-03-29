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
import com.aix.mpagents.views.fragments.dialogs.AddVariantDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditProductFragment extends Fragment implements EditProductInterface, VariantInterface {

    private FragmentEditProductBinding binding;
    private ProductViewModel productViewModel;
    private NavController navController;
    private ProductInfo productInfo;

    private List<String> photoList = new ArrayList<>();
    private List<String> newPhotoList = new ArrayList<>();
    private List<String> deletePhotoList = new ArrayList<>();
    private Category categoryModel;
    private EditProductPhotoViewAdapter editProductPhotoViewAdapter;
    private VariantsFirestoreAdapter variantsFirestoreAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditProductBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
        navController = Navigation.findNavController(view);
        initObservers();
        initListeners();
        initView();
        initVariantFirestoreOptions();
    }

    private void initVariantFirestoreOptions() {
        variantsFirestoreAdapter = new VariantsFirestoreAdapter(
                productViewModel.getVariantRecyclerOptions(productInfo.getProduct_id()),
                this
        );
        variantsFirestoreAdapter.setHasStableIds(true);

        binding.recyclerViewVariants.setAdapter(variantsFirestoreAdapter);
        binding.recyclerViewVariants.setLayoutManager(new LinearLayoutManager(requireContext()));
        //temporary fix for recyclerview
        binding.recyclerViewVariants.setItemAnimator(null);
    }

    private void initObservers() {
        productViewModel.isProductSaved().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    navController.popBackStack(R.id.addProductFragment,true);
                    productViewModel.isProductSaved().setValue(false);
                }
            }
        });

        productViewModel.getSelectedCategory().observe(getViewLifecycleOwner(), new Observer<Category>() {
            @Override
            public void onChanged(Category category) {
                binding.textViewCategoryValue.setText(category.getCategory_name());
                categoryModel = category;
            }
        });
    }

    private void initListeners() {
        binding.textViewAddVariant.setOnClickListener(v -> {
            new AddVariantDialog(this)
                    .show(requireActivity().getSupportFragmentManager(), "ADD_VARIANT");
        });

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProduct();
            }
        });

        binding.buttonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        binding.textViewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("product_type", "Product");
                navController.navigate(R.id.action_editProductFragment_to_categoryFragment,bundle);
            }
        });
    }

    private void initView() {
        try {
            productInfo = productViewModel.getSelectedProduct().getValue();
            binding.editTextProductName.setText(productInfo.getProduct_name());
            binding.editTextPrice.setText(String.valueOf(productInfo.getProduct_price()));
            binding.editTextDescription.setText(productInfo.getProduct_desc());
            binding.textViewCategoryValue.setText(productInfo.getCategory_name());
            binding.editTextQuantity.setText(String.valueOf(productInfo.getProduct_quantity()));
        }catch (Exception e){
            ErrorLog.WriteErrorLog(e);
        }

        productViewModel.getMedia(productInfo.getProduct_id());
        productViewModel.getMediaList().observe(getViewLifecycleOwner(), new Observer<List<Media>>() {
            @Override
            public void onChanged(List<Media> media) {
                if(media != null) {
                    photoList.clear();
                    for (int i = 0; i < media.size(); i++) {
                        if (!photoList.contains(media.get(i).getPath())) {
                            photoList.add(media.get(i).getPath());
                        }
                    }

                    ErrorLog.WriteDebugLog("MEDIA LIST SIZE " + media.size());
                    initImageRecyclerview(photoList);

                    productViewModel.getMediaList().setValue(null);
                }

            }
        });

        productViewModel.isProductUpdated().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    ErrorLog.WriteDebugLog("PRODUCT UPDATED SUCCESS");
                    productViewModel.isProductUpdated().setValue(false);
                    navController.popBackStack(R.id.editProductFragment,true);
                }
            }
        });

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


        if(!isEmptyFields(product_name,description,category)) {
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

//            if(!photoList.isEmpty()){
//                productInfo.setPhoto(photoList);
//            }

            ErrorLog.WriteDebugLog("PRODUCT ID "+ productInfo.getProduct_id());
            productViewModel.updateProduct(productInfo,newPhotoList,deletePhotoList);
        }
    }

    private boolean isEmptyFields(String product_name, String description, String category){

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
        }else{
            return false;
        }

    }


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        chooseImageActivityResult.launch(intent);
    }

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
                                // code for multiple image selection
                                ErrorLog.WriteDebugLog("DATA RECEIVED "+imageUri);
                                newPhotoList.add(String.valueOf(imageUri));
                                photoList.add(String.valueOf(imageUri));
                            }
                            initImageRecyclerview(photoList);

                        } else {
                            Uri uri = data.getData();
                            // your code for single image selection
                            ErrorLog.WriteDebugLog("DATA RECEIVED "+uri);
                            newPhotoList.add(String.valueOf(uri));
                            photoList.add(String.valueOf(uri));
                            initImageRecyclerview(photoList);

                        }

                        ErrorLog.WriteDebugLog("PHOTO LIST "+ photoList.size());
                    }
                }
            }
    );

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
    public void onImageRemove(int photoPosition) {

        newPhotoList.remove(photoList.get(photoPosition));
        deletePhotoList.add(photoList.get(photoPosition));
        photoList.remove(photoPosition);
        editProductPhotoViewAdapter.notifyItemRemoved(photoPosition);


    }

    @Override
    public void onVariantAdd(Variant variant) {
        productViewModel.addVariant(variant, productInfo.getProduct_id());
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
        productViewModel.deleteVariant(variant, productInfo.getProduct_id());
    }

    @Override
    public void onVariantDelete(int position, Variant variant) {

    }

    @Override
    public void onVariantUpdate(Variant variant) {
        productViewModel.updateVariant(variant, productInfo.getProduct_id());
    }

    @Override
    public void onVariantUpdate(int position, Variant variant) {

    }
}