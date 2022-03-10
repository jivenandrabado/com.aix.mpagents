package com.aix.mpagents.views.fragments.base;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.aix.mpagents.view_models.AccountInfoViewModel;

public abstract class BaseItemsFragment extends BaseFragment{

    protected ActivityResultLauncher<Intent> onShareResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK) showToast("Product shared!");
            }
    );

    public BaseItemsFragment(int contentLayoutId) {
        super(contentLayoutId);
    }
}
