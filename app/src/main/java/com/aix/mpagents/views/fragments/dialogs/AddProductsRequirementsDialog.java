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
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.DialogAddProductRequirementsBinding;
import com.aix.mpagents.interfaces.RequirementsDialogListener;
import com.aix.mpagents.view_models.AccountInfoViewModel;
import com.aix.mpagents.view_models.UserSharedViewModel;

public class AddProductsRequirementsDialog extends DialogFragment {


    private DialogAddProductRequirementsBinding binding;
    private Boolean isEmailVerified;
    private Boolean hasPhone;
    private Boolean hasBank;
    private Boolean hasGovId;
    private AccountInfoViewModel accountInfoViewModel;
    private UserSharedViewModel userSharedViewModel;
    private RequirementsDialogListener listener;
    private static final String TAG = "AddProductsRequirementsDialog";

    @Override
    public void onDestroy() {
        accountInfoViewModel.detachAccountInfoListener();
        super.onDestroy();
    }

    public AddProductsRequirementsDialog(Boolean isEmailVerified, Boolean hasPhone, Boolean hasBank, Boolean hasGovId, RequirementsDialogListener listener) {
        this.isEmailVerified = isEmailVerified;
        this.hasPhone = hasPhone;
        this.hasBank = hasBank;
        this.hasGovId = hasGovId;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddProductRequirementsBinding.inflate(inflater, container, false);
        binding.setView(this);
        binding.setLifecycleOwner(this);
        accountInfoViewModel = new ViewModelProvider(this).get(AccountInfoViewModel.class);
        userSharedViewModel = new ViewModelProvider(this).get(UserSharedViewModel.class);
        isProcessDone(isEmailVerified, binding.imageViewEmailVerifiedIndicator);
        isProcessDone(hasPhone, binding.imageViewContactNumberIndicator);
        isProcessDone(hasBank, binding.imageViewBankIndicator);
        isProcessDone(hasGovId, binding.imageViewGovIDIndicator);
        initListeners();
        return binding.getRoot();
    }

    private void initListeners() {
        View.OnClickListener toEditAccount = v -> {
            listener.onNavigateToEditAccount();
            dismiss();
        };
        binding.textViewEmailVerified.setOnClickListener(toEditAccount);
        binding.textViewContactNumber.setOnClickListener(toEditAccount);
        binding.textViewGovID.setOnClickListener(toEditAccount);
        binding.textViewBank.setOnClickListener(toEditAccount);

        userSharedViewModel.isUserLoggedin().observe(getViewLifecycleOwner(), result -> {
            if(result) accountInfoViewModel.addAccountInfoSnapshot();
            else accountInfoViewModel.detachAccountInfoListener();
        });

        accountInfoViewModel.getAccountInfo().observe(getViewLifecycleOwner(), result -> {
            String agentStatus = "";
            switch (result.getAccountStatus()){
                case SEMI:
                    agentStatus = "You are a Semi-verified User";
                    break;
                case FULLY:
                    agentStatus = "You are a Fully Verified User";
                    break;
                case BASIC:
                default:
                    agentStatus = "You are a Basic User";
                    break;
            }
            System.out.println("getAgentStatus " + agentStatus);
            binding.textViewAgentStatus.setText(agentStatus);
        });
    }

    public static void showFragment(Boolean isEmailVerified, Boolean hasPhone, Boolean hasBank, Boolean hasGovId, FragmentManager fragmentManager, RequirementsDialogListener listener){
        new AddProductsRequirementsDialog(
                isEmailVerified,
                hasPhone,
                hasBank,
                hasGovId,
                listener
        ).show(fragmentManager, TAG);
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
