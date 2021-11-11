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
import com.aix.mpagents.models.Category;
import com.aix.mpagents.models.Media;
import com.aix.mpagents.models.ProductInfo;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.view_models.ProductViewModel;
import com.aix.mpagents.views.adapters.EditProductPhotoViewAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditProductFragment extends Fragment implements EditProductInterface {

    private FragmentEditProductBinding binding;
    private ProductViewModel productViewModel;
    private NavController navController;
    private ProductInfo productInfo;

    private List<String> photoList = new ArrayList<>();
    private List<String> newPhotoList = new ArrayList<>();
    private List<String> deletePhotoList = new ArrayList<>();
    private Category categoryModel;
    private EditProductPhotoViewAdapter editProductPhotoViewAdapter;

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

        productViewModel.isProductSaved().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    navController.popBackStack(R.id.addProductFragment,true);
                    productViewModel.isProductSaved().setValue(false);
                }
            }
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

        binding.textViewCattegory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_editProductFragment_to_categoryFragment);
            }
        });

        productViewModel.getSelectedCategory().observe(getViewLifecycleOwner(), new Observer<Category>() {
            @Override
            public void onChanged(Category category) {
                binding.textViewCattegoryValue.setText(category.getCategory_name());
                categoryModel = category;
            }
        });

        initView();
    }

    private void initView() {
        productInfo = productViewModel.getSelectedProduct().getValue();
        binding.editTextProductName.setText(productInfo.getProduct_name());
        binding.editTextPrice.setText(String.valueOf(productInfo.getProduct_price()));
        binding.editTextDescription.setText(productInfo.getProduct_desc());
        binding.textViewCattegoryValue.setText(productInfo.getCategory_name());

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
        category = String.valueOf(binding.textViewCattegoryValue.getText()).trim();


        if(!isEmptyFields(product_name,description,category)) {
            productInfo.setProduct_name(product_name);
            productInfo.setProduct_price(product_price);
            productInfo.setProduct_desc(description);
            productInfo.setDateUpdated(new Date());

            if(categoryModel!=null) {
                productInfo.setCategory_name(category);
                productInfo.setCategory_id(categoryModel.getId());
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
    public void onPause() {
        super.onPause();
//        photoList.clear();
//        newPhotoList.clear();
    }

    @Override
    public void onImageRemove(int photoPosition) {

        newPhotoList.remove(photoList.get(photoPosition));
        deletePhotoList.add(photoList.get(photoPosition));
        photoList.remove(photoPosition);
        editProductPhotoViewAdapter.notifyItemRemoved(photoPosition);


    }
}