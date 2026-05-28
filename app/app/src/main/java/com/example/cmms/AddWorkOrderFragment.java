package com.example.cmms;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.cmms.data.remote.models.MachineResponse;
import com.example.cmms.ui.workorders.AddWorkOrderViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AddWorkOrderFragment extends Fragment {

    private List<MachineResponse> machineList = new ArrayList<>();
    private String editWorkOrderId = null;
    private boolean dataPreFilled = false;

    public AddWorkOrderFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_work_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editWorkOrderId = getArguments() != null ? getArguments().getString("editWorkOrderId") : null;
        boolean isEditMode = editWorkOrderId != null;

        Spinner spinnerMachine = view.findViewById(R.id.spinner_machine);
        TextInputEditText etTitle = view.findViewById(R.id.et_wo_title);
        TextInputEditText etDescription = view.findViewById(R.id.et_wo_description);
        ChipGroup chipGroupPriority = view.findViewById(R.id.chip_group_priority);
        MaterialButton btnSave = view.findViewById(R.id.btn_save_work_order);

        AddWorkOrderViewModel viewModel = new ViewModelProvider(this).get(AddWorkOrderViewModel.class);

        viewModel.getMachines().observe(getViewLifecycleOwner(), machines -> {
            if (machines == null || machines.isEmpty()) return;
            machineList = machines;
            List<String> names = new ArrayList<>();
            for (MachineResponse m : machines) {
                names.add(m.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, names);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerMachine.setAdapter(adapter);

            if (isEditMode && !dataPreFilled) {
                viewModel.loadWorkOrderForEdit(editWorkOrderId);
            }
        });

        if (isEditMode) {
            spinnerMachine.setEnabled(false);

            viewModel.getWorkOrderToEdit().observe(getViewLifecycleOwner(), wo -> {
                if (wo == null || dataPreFilled) return;
                dataPreFilled = true;

                etTitle.setText(wo.getTitle());
                etDescription.setText(wo.getDescription());

                if (wo.getMachine() != null) {
                    for (int i = 0; i < machineList.size(); i++) {
                        if (machineList.get(i).getId().equals(wo.getMachine().getId())) {
                            spinnerMachine.setSelection(i);
                            break;
                        }
                    }
                }

                String priority = wo.getPriority();
                if (priority != null) {
                    switch (priority) {
                        case "LOW":
                            chipGroupPriority.check(R.id.chip_priority_low);
                            break;
                        case "HIGH":
                            chipGroupPriority.check(R.id.chip_priority_high);
                            break;
                        case "CRITICAL":
                            chipGroupPriority.check(R.id.chip_priority_critical);
                            break;
                        default:
                            chipGroupPriority.check(R.id.chip_priority_medium);
                            break;
                    }
                }
            });
        }

        viewModel.loadMachines();

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
            String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";

            if (title.length() < 3) {
                etTitle.setError(getString(R.string.error_title_min));
                return;
            }
            if (description.length() < 10) {
                etDescription.setError(getString(R.string.error_description_min));
                return;
            }

            String priority = getSelectedPriority(chipGroupPriority);

            if (isEditMode) {
                viewModel.updateWorkOrder(editWorkOrderId, title, description, priority);
            } else {
                if (machineList.isEmpty() || spinnerMachine.getSelectedItemPosition() < 0) {
                    Toast.makeText(requireContext(), getString(R.string.error_select_machine), Toast.LENGTH_SHORT).show();
                    return;
                }
                String machineId = machineList.get(spinnerMachine.getSelectedItemPosition()).getId();
                viewModel.saveWorkOrder(machineId, title, description, priority);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            btnSave.setEnabled(loading == null || !loading);
        });

        viewModel.getSaveSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                String msg = isEditMode ? getString(R.string.msg_work_order_updated) : getString(R.string.msg_work_order_added);
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).popBackStack();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getSelectedPriority(ChipGroup chipGroup) {
        int checkedId = chipGroup.getCheckedChipId();
        if (checkedId == R.id.chip_priority_low) return "LOW";
        if (checkedId == R.id.chip_priority_high) return "HIGH";
        if (checkedId == R.id.chip_priority_critical) return "CRITICAL";
        return "MEDIUM";
    }
}
