package com.aix.mpagents.views.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.aix.mpagents.R;
import com.aix.mpagents.utilities.ErrorLog;
import com.aix.mpagents.view_models.AppConfigViewModel;
import com.aix.mpagents.view_models.UserSharedViewModel;
import com.aix.mpagents.views.fragments.dialogs.AppUpdateDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private FirebaseAuth.AuthStateListener authStateListener;
    private UserSharedViewModel userSharedViewModel;
    private NavController.OnDestinationChangedListener onDestinationChangedListener;
    private BottomNavigationView bottomNavigationView;
    private AppConfigViewModel appConfigViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_MPAgents);
        setContentView(R.layout.activity_main);
        initAuthStateListener();
        initNavigationComponent();
        initBottomNav();
        initOnNavigationDestinationChange();
        initAppConfig();

    }

    private void initOnNavigationDestinationChange() {
        onDestinationChangedListener = new NavController.OnDestinationChangedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if(destination.getId() == R.id.loginFragment
                        || destination.getId() == R.id.registrationFragment
                || destination.getId() == R.id.businessProfileFragment
                || destination.getId() == R.id.shopAddressFragment
                || destination.getId() == R.id.addAddressfragment
                || destination.getId() == R.id.productListFragment
                || destination.getId() == R.id.addProductFragment
                || destination.getId() == R.id.editProductFragment
                || destination.getId() == R.id.ordersFragment3
                || destination.getId() == R.id.organizationFragment){
                    bottomNavigationView.setVisibility(View.GONE);
                }else{

                    bottomNavigationView.setVisibility(View.VISIBLE);


                }
            }
        };

    }

    private void initAuthStateListener(){
        userSharedViewModel = new ViewModelProvider(this).get(UserSharedViewModel.class);
        authStateListener = userSharedViewModel.initAuthListener();
    }

    private void initNavigationComponent(){
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this,navController);

//        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph);
//        navGraph.setStartDestination(R.id.loginFragment);
//        navController.setGraph(navGraph);
    }

    private void initAppConfig(){
        appConfigViewModel = new ViewModelProvider(this).get(AppConfigViewModel.class);
        appConfigViewModel.initAppConfig(this);
        AppUpdateDialog appUpdateDialog = new AppUpdateDialog();


        appConfigViewModel.isForceUpdate().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    appUpdateDialog.show(getSupportFragmentManager(),"APP UPDATE DIALOG");
                    ErrorLog.WriteDebugLog("FORCE UPDATE NOW");
                }else{
                    ErrorLog.WriteDebugLog("FORCE UPDATE LATER");
                    if(appUpdateDialog.isVisible()){
                        appUpdateDialog.dismiss();
                    }
                }
            }
        });
    }


    private void initBottomNav(){
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navController.addOnDestinationChangedListener(onDestinationChangedListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        navController.removeOnDestinationChangedListener(onDestinationChangedListener);
    }


    @Override
    public boolean onSupportNavigateUp() {
        navController.navigateUp();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(appConfigViewModel != null){
            appConfigViewModel.detachAppConfigListener();
        }
    }

}