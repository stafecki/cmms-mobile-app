package com.example.cmms;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cmms.data.remote.models.PartResponse;
import com.example.cmms.data.remote.models.UserResponse;
import com.example.cmms.data.repository.AuthRepository;
import com.example.cmms.ui.workorders.WorkOrderDetailViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class WorkOrderDetailsFragment extends Fragment {

    private WorkOrderDetailViewModel viewModel;
    private List<UserResponse> technicianList = new ArrayList<>();
    private List<PartResponse> partList = new ArrayList<>();

    public WorkOrderDetailsFragment() {}

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

        MaterialButton btnInProgress = view.findViewById(R.id.btn_status_in_progress);
        MaterialButton btnCompleted = view.findViewById(R.id.btn_status_completed);
        MaterialCardView cardActions = view.findViewById(R.id.card_status_actions);

        MaterialCardView cardEditActions = view.findViewById(R.id.card_edit_actions);
        MaterialButton btnEdit = view.findViewById(R.id.btn_edit_work_order);

        MaterialCardView cardAssign = view.findViewById(R.id.card_assign_technician);
        Spinner spinnerTechnician = view.findViewById(R.id.spinner_technician);
        MaterialButton btnAssign = view.findViewById(R.id.btn_assign_technician);

        MaterialCardView cardAddParts = view.findViewById(R.id.card_add_parts);
        Spinner spinnerPart = view.findViewById(R.id.spinner_part);
        TextInputEditText etQuantity = view.findViewById(R.id.et_part_quantity);
        MaterialButton btnAddPart = view.findViewById(R.id.btn_add_part);

        viewModel = new ViewModelProvider(this).get(WorkOrderDetailViewModel.class);

        AuthRepository authRepository = new AuthRepository(requireContext());
        String role = authRepository.getSavedUserRole();
        String currentUserId = authRepository.getSavedUserId();

        boolean isAdmin = "ADMIN".equals(role);
        boolean isManager = "MANAGER".equals(role);
        boolean isManagerOrAdmin = isAdmin || isManager;
        boolean isTechnician = "TECHNICIAN".equals(role);
        boolean canEdit = isManagerOrAdmin;

        if (canEdit) {
            cardEditActions.setVisibility(View.VISIBLE);
        }

        if (isManagerOrAdmin) {
            cardAssign.setVisibility(View.VISIBLE);
            viewModel.loadTechnicians();
        }

        viewModel.getWorkOrder().observe(getViewLifecycleOwner(), wo -> {
            if (wo == null) return;
            tvTitle.setText(wo.getTitle());
            tvDescription.setText(wo.getDescription());
            tvPriority.setText(wo.getPriority());
            tvStatus.setText(wo.getStatus());

            tvMachine.setText(wo.getMachineName() != null ? wo.getMachineName() : "-");
            tvWorker.setText(wo.getAssignedToName() != null ? wo.getAssignedToName() : "-");
            tvParts.setText(wo.getPartsText() != null ? wo.getPartsText() : "-");

            boolean isAssigned = currentUserId != null && currentUserId.equals(wo.getAssignedToId());
            boolean canChangeStatus = isManagerOrAdmin || (isTechnician && isAssigned);
            boolean canAddParts = isManagerOrAdmin || (isTechnician && isAssigned);

            if (canAddParts) {
                cardAddParts.setVisibility(View.VISIBLE);
                viewModel.loadAvailableParts();
            } else {
                cardAddParts.setVisibility(View.GONE);
            }

            String status = wo.getStatus() != null ? wo.getStatus() : "";

            if (!canChangeStatus) {
                cardActions.setVisibility(View.GONE);
            } else {
                switch (status) {
                    case "NEW":
                        btnInProgress.setVisibility(View.VISIBLE);
                        btnCompleted.setVisibility(View.GONE);
                        cardActions.setVisibility(View.VISIBLE);
                        break;
                    case "IN_PROGRESS":
                        btnInProgress.setVisibility(View.GONE);
                        btnCompleted.setVisibility(View.VISIBLE);
                        cardActions.setVisibility(View.VISIBLE);
                        break;
                    case "COMPLETED":
                    case "CANCELLED":
                        cardActions.setVisibility(View.GONE);
                        if (canEdit) {
                            cardEditActions.setVisibility(View.GONE);
                        }
                        break;
                    default:
                        btnInProgress.setVisibility(View.VISIBLE);
                        btnCompleted.setVisibility(View.VISIBLE);
                        cardActions.setVisibility(View.VISIBLE);
                        break;
                }
            }

            if (isManagerOrAdmin && wo.getAssignedToId() != null && !technicianList.isEmpty()) {
                for (int i = 0; i < technicianList.size(); i++) {
                    if (wo.getAssignedToId().equals(technicianList.get(i).getId())) {
                        spinnerTechnician.setSelection(i);
                        break;
                    }
                }
            }
        });

        viewModel.getTechnicians().observe(getViewLifecycleOwner(), techs -> {
            if (techs == null || techs.isEmpty()) return;
            technicianList = techs;
            List<String> names = new ArrayList<>();
            for (UserResponse t : techs) {
                names.add(t.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, names);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTechnician.setAdapter(adapter);
        });

        viewModel.getAvailableParts().observe(getViewLifecycleOwner(), parts -> {
            if (parts == null || parts.isEmpty()) return;
            partList = parts;
            List<String> names = new ArrayList<>();
            for (PartResponse p : parts) {
                names.add(p.getName() + " (w mag: " + p.getStockQuantity() + ")");
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, names);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPart.setAdapter(adapter);
        });

        if (workOrderId != null) {
            btnInProgress.setOnClickListener(v -> viewModel.changeStatus(workOrderId, "IN_PROGRESS"));
            btnCompleted.setOnClickListener(v -> viewModel.changeStatus(workOrderId, "COMPLETED"));

            btnEdit.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("editWorkOrderId", workOrderId);
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_workOrderDetailsFragment_to_addWorkOrderFragment, bundle);
            });

            btnAssign.setOnClickListener(v -> {
                int pos = spinnerTechnician.getSelectedItemPosition();
                if (pos < 0 || pos >= technicianList.size()) return;
                String techId = technicianList.get(pos).getId();
                viewModel.assignTechnician(workOrderId, techId);
            });

            btnAddPart.setOnClickListener(v -> {
                int pos = spinnerPart.getSelectedItemPosition();
                if (pos < 0 || pos >= partList.size()) return;

                String quantityStr = etQuantity.getText() != null ? etQuantity.getText().toString().trim() : "";
                if (quantityStr.isEmpty()) {
                    etQuantity.setError(getString(R.string.error_empty_field));
                    return;
                }
                int quantity = Integer.parseInt(quantityStr);
                if (quantity < 1) {
                    etQuantity.setError(getString(R.string.error_empty_field));
                    return;
                }

                PartResponse selected = partList.get(pos);
                if (quantity > selected.getStockQuantity()) {
                    etQuantity.setError(getString(R.string.error_no_stock));
                    return;
                }

                viewModel.addPart(workOrderId, selected.getId(), quantity);
            });
        }

        viewModel.getStatusUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), getString(R.string.msg_status_updated), Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getAssignSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), getString(R.string.msg_technician_assigned), Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getAddPartSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), getString(R.string.msg_part_added), Toast.LENGTH_SHORT).show();
                etQuantity.setText("1");
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        if (workOrderId != null) {
            viewModel.loadWorkOrder(workOrderId);
        }
    }
}
