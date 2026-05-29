package com.example.cmms;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cmms.data.repository.AuthRepository;
import com.example.cmms.ui.machines.MachineDetailViewModel;
import com.example.cmms.utils.NetworkUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class MachineDetailsFragment extends Fragment {

    private MachineDetailViewModel viewModel;
    private RecentJobsAdapter adapter;

    public MachineDetailsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_machine_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String machineId = getArguments() != null ? getArguments().getString("machineId") : null;

        TextView tvName = view.findViewById(R.id.tv_detail_name);
        TextView tvSerial = view.findViewById(R.id.tv_detail_serial);
        TextView tvLocation = view.findViewById(R.id.tv_detail_location);
        TextView tvHours = view.findViewById(R.id.tv_detail_hours);
        RecyclerView rvJobs = view.findViewById(R.id.rv_recent_jobs);

        MaterialCardView cardActions = view.findViewById(R.id.card_machine_actions);
        MaterialButton btnEdit = view.findViewById(R.id.btn_edit_machine);
        MaterialButton btnDelete = view.findViewById(R.id.btn_delete_machine);

        MaterialCardView cardUpdateHours = view.findViewById(R.id.card_update_hours);
        TextInputEditText etUpdateHours = view.findViewById(R.id.et_update_hours);
        MaterialButton btnUpdateHours = view.findViewById(R.id.btn_update_hours);

        rvJobs.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RecentJobsAdapter();
        rvJobs.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(MachineDetailViewModel.class);

        AuthRepository authRepository = new AuthRepository(requireContext());
        String role = authRepository.getSavedUserRole();
        boolean isOnline = NetworkUtils.isNetworkAvailable(requireContext());

        boolean isAdmin = "ADMIN".equals(role);
        boolean isManager = "MANAGER".equals(role);
        boolean isTechnician = "TECHNICIAN".equals(role);

        if (isOnline && (isAdmin || isManager)) {
            cardActions.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        }

        if (isOnline && (isAdmin || isManager || isTechnician)) {
            cardUpdateHours.setVisibility(View.VISIBLE);
        }

        viewModel.getMachine().observe(getViewLifecycleOwner(), machine -> {
            if (machine == null) return;
            tvName.setText(machine.getName());
            tvSerial.setText(machine.getSerialNumber());
            tvLocation.setText(machine.getLocationName() != null ? machine.getLocationName() : "-");
            tvHours.setText((int) machine.getOperatingHours() + " h");

            etUpdateHours.setText(String.valueOf((int) machine.getOperatingHours()));
        });

        if (machineId != null) {
            btnEdit.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("editMachineId", machineId);
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_machineDetailsFragment_to_addMachineFragment, bundle);
            });

            btnDelete.setOnClickListener(v -> {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.dialog_delete_title)
                        .setMessage(R.string.dialog_delete_machine_message)
                        .setNegativeButton(R.string.btn_cancel, null)
                        .setPositiveButton(R.string.btn_delete, (dialog, which) -> viewModel.deleteMachine(machineId))
                        .show();
            });

            btnUpdateHours.setOnClickListener(v -> {
                String hoursStr = etUpdateHours.getText() != null ? etUpdateHours.getText().toString().trim() : "";
                if (hoursStr.isEmpty()) {
                    etUpdateHours.setError(getString(R.string.error_empty_field));
                    return;
                }
                double hours = Double.parseDouble(hoursStr);
                viewModel.updateOperatingHours(machineId, hours);
            });
        }

        viewModel.getDeleteSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), getString(R.string.msg_machine_deleted), Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).popBackStack();
            }
        });

        viewModel.getHoursUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(requireContext(), getString(R.string.msg_hours_updated), Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        if (machineId != null) {
            viewModel.loadMachine(machineId);
        }
    }
}
