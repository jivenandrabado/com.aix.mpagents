package com.aix.mpagents.views.fragments.account;

import static com.aix.mpagents.utilities.AgentStatusENUM.*;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.aix.mpagents.R;
import com.aix.mpagents.databinding.FragmentVerificationDetailsBinding;
import com.aix.mpagents.utilities.AgentStatusENUM;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.view_models.AccountInfoViewModel;
import com.aix.mpagents.view_models.UserSharedViewModel;
import com.aix.mpagents.views.adapters.AgentFeaturesAdapter;
import com.aix.mpagents.views.fragments.dialogs.IdTypeDialog;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Objects;

public class VerificationDetailsFragment extends Fragment {

    private FragmentVerificationDetailsBinding binding;
    private NavController navController;
    private HashMap<String, Integer> features = new HashMap<>();
    private AgentFeaturesAdapter adapter;
    private AccountInfoViewModel accountInfoViewModel;
    private UserSharedViewModel userSharedViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVerificationDetailsBinding.inflate(inflater,container,false);
        binding.setView(this);
        binding.setLifecycleOwner(this);
        userSharedViewModel = new ViewModelProvider(this).get(UserSharedViewModel.class);
        accountInfoViewModel = new ViewModelProvider(this).get(AccountInfoViewModel.class);
        initFeatures();
        initListeners();
        return binding.getRoot();
    }

    private void initFeatures() {
        features.put("Add Product", R.drawable.ic_baseline_library_add_24);
        features.put("View Product", R.drawable.ic_baseline_backpack_24);
        features.put("View Orders", R.drawable.ic_baseline_shopping_basket_24);
        features.put("Return Order", R.drawable.ic_baseline_keyboard_return_24);

        adapter = new AgentFeaturesAdapter(features);
        binding.recyclerViewFeatures.setAdapter(adapter);
        binding.recyclerViewFeatures.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false)
        );

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    private void initListeners() {

        userSharedViewModel.isUserLoggedin().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    if(!Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isAnonymous()) {
                        ErrorLog.WriteDebugLog("Display user profile");
                        //init user info
//                        accountInfoViewModel.getShopInfo();

                        accountInfoViewModel.addAccountInfoSnapshot();
                    }else{
                        ErrorLog.WriteDebugLog("Init anonymous user");
                        accountInfoViewModel.detachAccountInfoListener();
                    }
                }else{
                    ErrorLog.WriteDebugLog("No User");
                }
            }
        });

        accountInfoViewModel.getAgentStatus().observe(getViewLifecycleOwner(), result -> {
            String agentStatus = "";
            switch (result){
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
            binding.textViewAgentVerificationStatus.setText(agentStatus);
        });

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        accountInfoViewModel.detachAccountInfoListener();
    }
}