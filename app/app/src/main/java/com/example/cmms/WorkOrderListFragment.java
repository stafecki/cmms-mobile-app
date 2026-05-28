package com.example.cmms;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.cmms.data.repository.AuthRepository;
import com.example.cmms.ui.workorders.WorkOrdersViewModel;
import com.example.cmms.utils.NetworkUtils;
import com.google.android.material.chip.Chip;

public class WorkOrderListFragment extends Fragment {

    private WorkOrdersViewModel viewModel;
    private WorkOrderAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyView;

    public WorkOrderListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_work_order_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            Toast.makeText(requireContext(), "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();
        }

        recyclerView = view.findViewById(R.id.rv_work_orders);
        emptyView = view.findViewById(R.id.tv_empty_view);
        SwipeRefreshLayout swipe = view.findViewById(R.id.main);

        Chip chipAll = view.findViewById(R.id.chip_all);
        Chip chipNew = view.findViewById(R.id.chip_new);
        Chip chipInProgress = view.findViewById(R.id.chip_in_progress);
        Chip chipCompleted = view.findViewById(R.id.chip_completed);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new WorkOrderAdapter(workOrder -> {
            Bundle bundle = new Bundle();
            bundle.putString("workOrderId", workOrder.getId());
            bundle.putString("workOrderTitle", workOrder.getTitle());
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_workOrderListFragment_to_workOrderDetailsFragment, bundle);
        });
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(WorkOrdersViewModel.class);

        viewModel.getWorkOrders().observe(getViewLifecycleOwner(), workOrders -> {
            if (workOrders == null || workOrders.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                adapter.setWorkOrders(workOrders);
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

        chipAll.setOnClickListener(v -> viewModel.loadWorkOrders());
        chipNew.setOnClickListener(v -> viewModel.filterByStatus("NEW"));
        chipInProgress.setOnClickListener(v -> viewModel.filterByStatus("IN_PROGRESS"));
        chipCompleted.setOnClickListener(v -> viewModel.filterByStatus("COMPLETED"));

        viewModel.loadWorkOrders();

        if (swipe != null) {
            swipe.setOnRefreshListener(viewModel::loadWorkOrders);
        }

        FloatingActionButton fab = view.findViewById(R.id.fab_add_work_order);
        if (fab != null) {
            AuthRepository authRepository = new AuthRepository(requireContext());
            String role = authRepository.getSavedUserRole();
            boolean canAdd = "ADMIN".equals(role) || "MANAGER".equals(role) || "OPERATOR".equals(role);

            if (canAdd) {
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(v -> {
                    NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.action_workOrderListFragment_to_addWorkOrderFragment);
                });
            } else {
                fab.setVisibility(View.GONE);
            }
        }
    }
}
