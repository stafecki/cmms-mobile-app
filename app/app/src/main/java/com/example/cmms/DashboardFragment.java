package com.example.cmms;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cmms.ui.dashboard.DashboardViewModel;

public class DashboardFragment extends Fragment {

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DashboardViewModel viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        TextView tvOpenJobs = view.findViewById(R.id.tvOpenJobsCount);
        TextView tvCritical = view.findViewById(R.id.tvCriticalJobsCount);
        TextView tvUpcoming = view.findViewById(R.id.tvUpcomingMaintenanceCount);

        SwipeRefreshLayout swipe = view.findViewById(R.id.main);

        viewModel.getDashboardData().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                if (tvOpenJobs != null && data.getWorkOrders() != null) {
                    tvOpenJobs.setText(String.valueOf(data.getWorkOrders().getOpen()));
                }
                if (tvCritical != null && data.getWorkOrders() != null) {
                    tvCritical.setText(String.valueOf(data.getWorkOrders().getCritical()));
                }
                if (tvUpcoming != null) {
                    if (data.getPreventive() != null) {
                        tvUpcoming.setText(String.valueOf(data.getPreventive().getUpcomingIn7Days()));
                    } else {
                        tvUpcoming.setText("—");
                    }
                }
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            if (swipe != null) {
                swipe.setRefreshing(loading != null && loading);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.loadDashboard("month");

        if (swipe != null) {
            swipe.setOnRefreshListener(viewModel::refresh);
        }
    }
}