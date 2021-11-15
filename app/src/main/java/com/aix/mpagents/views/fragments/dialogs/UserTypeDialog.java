package com.aix.mpagents.views.fragments.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.aix.mpagents.databinding.FragmentUserTypeDialogBinding;
import com.aix.mpagents.view_models.AccountInfoViewModel;

import java.util.HashMap;
import java.util.Map;

public class UserTypeDialog extends DialogFragment {

    private FragmentUserTypeDialogBinding binding;
    private AccountInfoViewModel accountInfoViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentUserTypeDialogBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accountInfoViewModel = new ViewModelProvider(requireActivity()).get(AccountInfoViewModel.class);


    }
}