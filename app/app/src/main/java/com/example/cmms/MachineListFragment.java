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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cmms.ui.machines.MachinesViewModel;
import com.example.cmms.utils.NetworkUtils;

public class MachineListFragment extends Fragment {

    private MachinesViewModel viewModel;
    private MachineAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyView;

    public MachineListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_machine_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            Toast.makeText(requireContext(), "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();
        }

        recyclerView = view.findViewById(R.id.rv_machines);
        emptyView = view.findViewById(R.id.tv_empty_view);
        SwipeRefreshLayout swipe = view.findViewById(R.id.main);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new MachineAdapter(machine -> {
            Bundle bundle = new Bundle();
            bundle.putString("machineId", machine.getId());
            bundle.putString("machineName", machine.getName());
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_machineListFragment_to_machineDetailsFragment, bundle);
        });
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(MachinesViewModel.class);

        EditText etSearch = view.findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    viewModel.loadMachines();
                } else {
                    viewModel.search(query);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        viewModel.getMachines().observe(getViewLifecycleOwner(), machines -> {
            if (machines == null || machines.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                adapter.setMachines(machines);
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

        viewModel.loadMachines();

        if (swipe != null) {
            swipe.setOnRefreshListener(viewModel::loadMachines);
        }
    }
}