package com.example.cmms;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import com.example.cmms.ui.notifications.NotificationsViewModel;
import com.example.cmms.utils.NetworkUtils;
import com.example.cmms.utils.NotificationHelper;
import com.example.cmms.utils.SessionManager;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private TextView tvOfflineBanner;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private boolean wasOffline = false;

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        NotificationHelper.createNotificationChannels(this);
        requestNotificationPermission();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SessionManager.reset();
        SessionManager.getSessionExpired().observe(this, expired -> {
            if (expired != null && expired) {
                Toast.makeText(this, "Sesja wygasła. Zaloguj się ponownie.", Toast.LENGTH_LONG).show();
                Intent loginIntent = new Intent(this, LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();
            }
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

        tvOfflineBanner = findViewById(R.id.tv_offline_banner);
        setupNetworkMonitor();

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int id = destination.getId();
            if (id == R.id.machineDetailsFragment || id == R.id.workOrderDetailsFragment
                    || id == R.id.addMachineFragment || id == R.id.addWorkOrderFragment) {
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

        vm.loadNotifications();

        vm.getUnreadCount().observe(this, count -> {
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

    private void setupNetworkMonitor() {
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        if (!NetworkUtils.isNetworkAvailable(this)) {
            tvOfflineBanner.setVisibility(View.VISIBLE);
            wasOffline = true;
        }

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                runOnUiThread(() -> {
                    tvOfflineBanner.setVisibility(View.GONE);
                    if (wasOffline) {
                        Toast.makeText(MainActivity.this, R.string.connection_restored, Toast.LENGTH_SHORT).show();
                        wasOffline = false;
                    }
                });
            }

            @Override
            public void onLost(@NonNull Network network) {
                runOnUiThread(() -> {
                    if (!NetworkUtils.isNetworkAvailable(MainActivity.this)) {
                        tvOfflineBanner.setVisibility(View.VISIBLE);
                        wasOffline = true;
                    }
                });
            }
        };

        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        connectivityManager.registerNetworkCallback(request, networkCallback);
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectivityManager != null && networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}
