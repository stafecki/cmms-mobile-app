package com.example.cmms;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNav, navController);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.dashboardFragment,
                R.id.machineListFragment,
                R.id.workOrderListFragment,
                R.id.notificationsFragment,
                R.id.profileFragment
        ).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        observeUnreadBadge(bottomNav);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.machineDetailsFragment || destination.getId() == R.id.workOrderDetailsFragment) {
                bottomNav.setVisibility(View.GONE);
                getSupportActionBar().show();
            } else {
                bottomNav.setVisibility(View.VISIBLE);
                getSupportActionBar().hide();
            }
        });

    }
    private void observeUnreadBadge(BottomNavigationView bottomNav) {
        NotificationsViewModel vm = new ViewModelProvider(this).get(NotificationsViewModel.class);

        vm.getNotifications().observe(this, notifications ->
                vm.refreshUnreadNotificationsCount()
        );

        vm.getUnreadNotificationsCount().observe(this, count -> {
            BadgeDrawable badge = bottomNav.getOrCreateBadge(R.id.notificationsFragment);
            badge.setBackgroundColor(ContextCompat.getColor(this, R.color.noir));
            badge.setBadgeTextColor(ContextCompat.getColor(this, R.color.oat));
            if (count != null && count > 0) {
                badge.setNumber(count);
                badge.setVisible(true);
            } else {
                badge.setVisible(false);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}