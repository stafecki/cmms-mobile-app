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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MachineDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MachineDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MachineDetailsViewModel viewModel;
    private RecentJobsAdapter adapter;

    public MachineDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MachineDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MachineDetailsFragment newInstance(String param1, String param2) {
        MachineDetailsFragment fragment = new MachineDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_machine_details, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle bundle){
        super.onViewCreated(view, bundle);

        TextView tvName = view.findViewById(R.id.tv_detail_name);
        TextView tvSerial = view.findViewById(R.id.tv_detail_serial);
        TextView tvLocation = view.findViewById(R.id.tv_detail_location);
        TextView tvHours = view.findViewById(R.id.tv_detail_hours);
        RecyclerView rvJobs = view.findViewById(R.id.rv_recent_jobs);

        rvJobs.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RecentJobsAdapter();
        rvJobs.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(MachineDetailsViewModel.class);

        viewModel.getMachineName().observe(getViewLifecycleOwner(), tvName::setText);
        viewModel.getMachineSerial().observe(getViewLifecycleOwner(), tvSerial::setText);
        viewModel.getMachineLocation().observe(getViewLifecycleOwner(), tvLocation::setText);
        viewModel.getMachineHours().observe(getViewLifecycleOwner(), tvHours::setText);

        viewModel.getRecentJobs().observe(getViewLifecycleOwner(), jobs -> {
            if (jobs != null) {
                adapter.setJobs(jobs);
            }
        });

        viewModel.loadMachineDetails();



    }
}