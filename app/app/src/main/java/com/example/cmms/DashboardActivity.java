package com.example.cmms;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        DashboardViewModel viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        viewModel.getOpenJobsCount().observe(this, count -> {
            TextView tvCount = findViewById(R.id.tv_open_jobs_count);
            if (tvCount != null) {
                tvCount.setText(String.valueOf(count));
            }
        });

        viewModel.getCriticalJobsCount().observe(this, count -> {
            TextView tvCritical = findViewById(R.id.tvCriticalJobsCount);
            if (tvCritical != null) {
                tvCritical.setText(String.valueOf(count));
            }
        });

        viewModel.loadDashboardData();

        SwipeRefreshLayout swipe = findViewById(R.id.main);
        if (swipe != null) {
            swipe.setOnRefreshListener(() -> {
                viewModel.loadDashboardData();
                swipe.setRefreshing(false);
            });
        }
    }
}