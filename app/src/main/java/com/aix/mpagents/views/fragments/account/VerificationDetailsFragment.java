package com.aix.mpagents.views.fragments.account;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.FragmentVerificationDetailsBinding;
import com.aix.mpagents.views.fragments.dialogs.IdTypeDialog;

public class VerificationDetailsFragment extends Fragment {

    private FragmentVerificationDetailsBinding binding;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVerificationDetailsBinding.inflate(inflater,container,false);
        binding.setView(this);
        binding.setLifecycleOwner(this);
        initListeners();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    private void initListeners() {
        binding.buttonVerificationPage.setOnClickListener(v -> {
            navController.navigate(R.id.action_verificationDetailsFragment_to_businessProfileFragment);
        });

        binding.textViewSeeListOfID.setOnClickListener(v -> {
            Dialog dialog = new Dialog(requireContext());
            ListView listView = new ListView(requireContext());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_list_item_1,
                    requireContext().getResources().getStringArray(R.array.id_types));
            listView.setAdapter(adapter);
            dialog.setContentView(listView);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        });
    }
}