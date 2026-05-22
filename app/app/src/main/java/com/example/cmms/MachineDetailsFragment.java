package com.example.cmms;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cmms.ui.machines.MachineDetailViewModel;

public class MachineDetailsFragment extends Fragment {

    private MachineDetailViewModel viewModel;
    private RecentJobsAdapter adapter;

    public MachineDetailsFragment() {
        // Required empty public constructor
    }

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

        rvJobs.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RecentJobsAdapter();
        rvJobs.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(MachineDetailViewModel.class);

        viewModel.getMachine().observe(getViewLifecycleOwner(), machine -> {
            if (machine == null) return;
            tvName.setText(machine.getName());
            tvSerial.setText(machine.getSerialNumber());
            tvLocation.setText("-");
            tvHours.setText((int) machine.getOperatingHours() + " h");
        });

        if (machineId != null) {
            viewModel.loadMachine(machineId);
        }
    }
}
