package com.aix.mpagents.views.fragments.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.DialogAddProductRequirementsBinding;

public class AddProductsRequirementsDialog extends DialogFragment {


    private DialogAddProductRequirementsBinding binding;
    private NavController navController;
    private Boolean isEmailVerified;
    private Boolean hasPhone;
    private Boolean hasBank;
    private Boolean hasGovId;

    public AddProductsRequirementsDialog(Boolean isEmailVerified, Boolean hasPhone, Boolean hasBank, Boolean hasGovId, NavController navController) {
        this.isEmailVerified = isEmailVerified;
        this.hasPhone = hasPhone;
        this.hasBank = hasBank;
        this.hasGovId = hasGovId;
        this.navController = navController;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddProductRequirementsBinding.inflate(inflater, container, false);
        binding.setView(this);
        binding.setLifecycleOwner(this);
        isProcessDone(isEmailVerified, binding.imageViewEmailVerifiedIndicator);
        isProcessDone(hasPhone, binding.imageViewContactNumberIndicator);
        isProcessDone(hasBank, binding.imageViewBankIndicator);
        isProcessDone(hasGovId, binding.imageViewGovIDIndicator);
        initListeners();
        return binding.getRoot();
    }

    private void initListeners() {
        View.OnClickListener toEditAccount = v -> {
            dismiss();
            navController.navigate(R.id.action_homeFragment_to_businessProfileFragment);
        };
        binding.textViewEmailVerified.setOnClickListener(toEditAccount);
        binding.textViewContactNumber.setOnClickListener(toEditAccount);
        binding.textViewGovID.setOnClickListener(toEditAccount);
        binding.textViewBank.setOnClickListener(toEditAccount);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void isProcessDone(boolean isDone, ImageView view){
        view.setImageDrawable(
                ContextCompat.getDrawable(requireContext(),
                        isDone ? R.drawable.ic_baseline_check_circle_outline_24 : R.drawable.ic_baseline_circle_24
                )
        );
    }
}
