package com.aix.mpagents.views.fragments.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.DialogAddVariantBinding;
import com.aix.mpagents.interfaces.VariantInterface;
import com.aix.mpagents.models.Variant;

public class AddVariantDialog extends DialogFragment {

    private DialogAddVariantBinding binding;
    private VariantInterface variantInterface;
    private int position;
    private Variant variant;

    public AddVariantDialog(VariantInterface variantInterface){
        this.variantInterface = variantInterface;
    }

    public AddVariantDialog(VariantInterface variantInterface, Variant variant, int position){
        this.variantInterface = variantInterface;
        this.variant = variant;
        this.position = position;
    }

    public AddVariantDialog(VariantInterface variantInterface, Variant variant){
        this.variantInterface = variantInterface;
        this.variant = variant;
    }


    @Override
    public void onStart() {
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddVariantBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initListeners();
        super.onViewCreated(view, savedInstanceState);
    }

    private void initListeners() {
        binding.editTextVariantStock.setText("0");
        if(variant != null){
            binding.editTextVariantStock.setText(String.valueOf(variant.getStock()));
            binding.editTextVariantName.setText(String.valueOf(variant.getVariant_name()));
            binding.buttonDelete.setVisibility(View.VISIBLE);
            binding.buttonAddVariant.setText(getContext().getString(R.string.update));
            binding.buttonDelete.setOnClickListener(v -> {
                if(variant.getVariant_id() != null) variantInterface.onVariantDelete(variant);
                else variantInterface.onVariantDelete(position, variant);
                dismiss();
            });
        }
        binding.buttonAddVariant.setOnClickListener(v -> {
            checkVariantStatus();
        });
        binding.buttonCancel.setOnClickListener(v -> dismiss());
    }

    private void checkVariantStatus() {
        String name, stock;
        name = binding.editTextVariantName.getText().toString();
        stock = binding.editTextVariantStock.getText().toString();
        if(isNotEmpty(name, stock)){
            if(variant != null) updateVariant(name,stock);
            else addVariant(name,stock);
        }
    }

    private void updateVariant(String name, String stock) {
        variant.setVariant_name(name);
        variant.setStock(Integer.parseInt(stock));
        if(variant.getVariant_id() != null)
            variantInterface.onVariantUpdate(variant);
        else
            variantInterface.onVariantUpdate(position,variant);
        dismiss();
    }

    private void addVariant(String name, String stock) {
        Variant variant = new Variant();
        variant.setVariant_name(name);
        variant.setStock(Integer.parseInt(stock));
        variantInterface.onVariantAdd(variant);
        dismiss();
    }

    private boolean isNotEmpty(String variantName, String stock) {
        if(TextUtils.isEmpty(variantName)){
            Toast.makeText(requireContext(), "Variant Desc. is empty.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(TextUtils.isEmpty(stock)){
            Toast.makeText(requireContext(), "Stock is empty.", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }
}
