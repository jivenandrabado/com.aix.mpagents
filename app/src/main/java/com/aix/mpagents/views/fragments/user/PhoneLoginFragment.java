package com.aix.mpagents.views.fragments.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.FragmentPhoneLoginBinding;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.view_models.LoginViewModel;
import com.aix.mpagents.views.fragments.base.BaseLoginFragment;


public class PhoneLoginFragment extends BaseLoginFragment {

    private FragmentPhoneLoginBinding binding;

    public PhoneLoginFragment() {
        super(R.layout.fragment_phone_login);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        navController = Navigation.findNavController(view);
        binding = FragmentPhoneLoginBinding.bind(getView());
        initObservers();
        initListeners();
        super.onViewCreated(view, savedInstanceState);
    }

    private void initObservers() {
        getLoginViewModel().getVerificationId().observe(getViewLifecycleOwner(), result -> {
            navController.navigate(R.id.action_phoneLoginFragment_to_phoneVerificationFragment);
        });
    }

    private void initListeners() {
        binding.editTextPhoneNumber.setOnFocusChangeListener((v,isFocus) -> {
            EditText editText = (EditText) v;
            if(isFocus){
                if(editText.getText().toString().length() == 0){
                    editText.setText("+63");
                }
            }
        });

        binding.editTextPhoneNumber.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_DONE) toVerificationPage();
            return true;
        });

        binding.buttonLogin.setOnClickListener( v -> toVerificationPage());
    }

    private void toVerificationPage() {
        String number = binding.editTextPhoneNumber.getText().toString();
        if(number.equalsIgnoreCase("+63")
                || number.length() < 11){
            Toast.makeText(requireContext(), "Not a valid mobile number!", Toast.LENGTH_SHORT).show();
        }else {
            ErrorLog.WriteDebugLog("toVerificationPage "+ number);
            getLoginViewModel().phoneVerificationSetup(number, requireActivity());
        }
    }
}