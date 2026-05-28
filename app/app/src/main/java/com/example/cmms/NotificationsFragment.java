package com.example.cmms;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cmms.ui.notifications.NotificationsViewModel;
import com.example.cmms.utils.NetworkUtils;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel viewModel;
    private NotificationsAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyView;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            Toast.makeText(requireContext(), "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();
        }

        recyclerView = view.findViewById(R.id.rv_notifications);
        emptyView = view.findViewById(R.id.tv_empty_view);
        SwipeRefreshLayout swipe = view.findViewById(R.id.main);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new NotificationsAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(NotificationsViewModel.class);

        adapter.setOnNotificationClickListener(notification -> {
            viewModel.markAsRead(notification.getId());
        });

        viewModel.getNotifications().observe(getViewLifecycleOwner(), notifications -> {
            if (notifications == null || notifications.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                adapter.setNotifications(notifications);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            if (swipe != null) {
                swipe.setRefreshing(loading != null && loading);
            }
        });

        viewModel.loadNotifications();

        if (swipe != null) {
            swipe.setOnRefreshListener(viewModel::loadNotifications);
        }
    }
}
