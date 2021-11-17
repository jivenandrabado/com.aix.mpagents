package com.aix.mpagents.views.fragments.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.BottomSheetDialogIdTypeBinding;
import com.aix.mpagents.view_models.AccountInfoViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;


public class IdTypeDialog extends BottomSheetDialogFragment implements AdapterView.OnItemClickListener{

    private ArrayAdapter adapter;
    private BottomSheetDialogIdTypeBinding binding;
    private AccountInfoViewModel accountInfoViewModel;
    private String selectedString;
    private TextInputEditText editTextIdType;

    public IdTypeDialog(ArrayAdapter adapter, TextInputEditText editTextIdType){
        this.adapter = adapter;
        this.editTextIdType = editTextIdType;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetDialogIdTypeBinding.inflate(inflater,container,false);
        binding.setView(this);
        binding.setLifecycleOwner(this);
        accountInfoViewModel = new ViewModelProvider(this).get(AccountInfoViewModel.class);
        binding.listViewIdTypes.setAdapter(adapter);
        binding.listViewIdTypes.setOnItemClickListener(this::onItemClick);
        binding.textViewConfirm.setOnClickListener(v -> {
                editTextIdType.setText(selectedString);
                dismiss();
            }
        );
        return binding.getRoot();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        for(int j = 0 ; j < adapterView.getCount(); j++){
            TextView textViewIdTypeItem = (TextView) adapterView.getChildAt(j);
            textViewIdTypeItem.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
        }
        TextView textViewSelected = (TextView) view;
        textViewSelected.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.silver));
        selectedString = textViewSelected.getText().toString();
    }
}
