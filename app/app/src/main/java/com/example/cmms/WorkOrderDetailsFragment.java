package com.example.cmms;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cmms.ui.workorders.WorkOrderDetailViewModel;

public class WorkOrderDetailsFragment extends Fragment {

    private WorkOrderDetailViewModel viewModel;

    public WorkOrderDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_work_order_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String workOrderId = getArguments() != null ? getArguments().getString("workOrderId") : null;

        TextView tvTitle = view.findViewById(R.id.tv_work_order_details_title);
        TextView tvDescription = view.findViewById(R.id.tv_work_order_details_description);
        TextView tvPriority = view.findViewById(R.id.tv_work_order_details_priority);
        TextView tvStatus = view.findViewById(R.id.tv_work_order_details_status);
        TextView tvWorker = view.findViewById(R.id.tv_work_order_details_technicianName);
        TextView tvMachine = view.findViewById(R.id.tv_work_order_details_machine);
        TextView tvParts = view.findViewById(R.id.tv_work_order_details_parts);

        viewModel = new ViewModelProvider(this).get(WorkOrderDetailViewModel.class);

        viewModel.getWorkOrder().observe(getViewLifecycleOwner(), workOrder -> {
            if (workOrder == null) return;
            tvTitle.setText(workOrder.getTitle());
            tvDescription.setText(workOrder.getDescription());
            tvPriority.setText(workOrder.getPriority());
            tvStatus.setText(workOrder.getStatus());
            tvWorker.setText("-");
            tvMachine.setText("-");
            tvParts.setText("-");
        });

        if (workOrderId != null) {
            viewModel.loadWorkOrder(workOrderId);
        }
    }
}
