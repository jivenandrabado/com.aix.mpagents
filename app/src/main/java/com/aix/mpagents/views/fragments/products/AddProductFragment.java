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
import com.aix.mpagents.databinding.FragmentAddProductBinding;
import com.aix.mpagents.interfaces.AddProductInterface;
import com.aix.mpagents.models.Category;
import com.aix.mpagents.models.ProductInfo;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.view_models.ProductViewModel;
import com.aix.mpagents.views.adapters.AddProductPhotoViewAdapter;
import com.aix.mpagents.views.fragments.dialogs.ProgressDialogFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddProductFragment extends Fragment implements AddProductInterface {

    private FragmentAddProductBinding binding;
    private ProductViewModel productViewModel;
    private List<String> photoList = new ArrayList<>();
    private NavController navController;
    private Category categoryModel;
    private AddProductPhotoViewAdapter addProductPhotoViewAdapter;
    private ProgressDialogFragment progressDialogFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddProductBinding.inflate(inflater,container,false);
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
                    if(progressDialogFragment!=null){
                        progressDialogFragment.dismiss();
                    }
                    productViewModel.isProductSaved().setValue(false);
                }
            }
        });

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct();
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
                navController.navigate(R.id.action_addProductFragment_to_categoryFragment);
            }
        });

        productViewModel.getSelectedCategory().observe(getViewLifecycleOwner(), new Observer<Category>() {
            @Override
            public void onChanged(Category category) {
                binding.textViewCattegoryValue.setText(category.getCategory_name());
                categoryModel = category;
            }
        });
    }

    private void showProgressDialog(){
        progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.show(getChildFragmentManager(),"ADD PRODUCT PROGRESS DIALOG");
    }

    private void addProduct() {

        String product_name, description, category;
        double product_price = 0;
        product_name = String.valueOf(binding.editTextProductName.getText()).trim();
        if(!String.valueOf(binding.editTextPrice.getText()).isEmpty()) {
            product_price = Double.parseDouble(String.valueOf(binding.editTextPrice.getText()).trim());
        }
        description = String.valueOf(binding.editTextDescription.getText()).trim();
        category = String.valueOf(binding.textViewCattegoryValue.getText()).trim();


        if(!isEmptyFields(product_name,description,category, photoList)) {
            ProductInfo productInfo = new ProductInfo();
            productInfo.setProduct_name(product_name);
            productInfo.setProduct_price(product_price);
            productInfo.setProduct_desc(description);
            productInfo.setDateCreated(new Date());
            productInfo.setCategory_name(category);
            productInfo.setRating(0);
            productInfo.setSold(0);
            productInfo.setCategory_id(categoryModel.getId());
            productInfo.setIs_deleted(false);
            productInfo.setIs_active(true);

            productViewModel.addProduct(productInfo, photoList);
            showProgressDialog();
        }
    }

    private boolean isEmptyFields(String product_name, String description, String category, List<String> photoList){

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
        }else {
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

                        ErrorLog.WriteDebugLog("PHOTO LIST "+ photoList.size());
                    }
                }
            }
    );

    private void initImageRecyclerview(List<String> photoList){

        addProductPhotoViewAdapter = new AddProductPhotoViewAdapter(photoList,requireContext(),this);
        addProductPhotoViewAdapter.setHasStableIds(true);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerView.setAdapter(addProductPhotoViewAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(!photoList.isEmpty()){
            initImageRecyclerview(photoList);
        }

    }

    @Override
    public void onImageRemove(int photoPosition) {
        photoList.remove(photoPosition);
        addProductPhotoViewAdapter.notifyItemRemoved(photoPosition);
    }
}