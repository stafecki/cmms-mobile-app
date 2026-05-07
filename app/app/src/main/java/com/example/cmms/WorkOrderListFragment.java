package com.example.cmms;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkOrderListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkOrderListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private WorkOrderViewModel viewModel;
    private WorkOrderAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyView;

    public WorkOrderListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorkOrderListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkOrderListFragment newInstance(String param1, String param2) {
        WorkOrderListFragment fragment = new WorkOrderListFragment();
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
        return inflater.inflate(R.layout.fragment_work_order_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_work_orders);
        emptyView = view.findViewById(R.id.tv_empty_view);
        Chip chipAll = view.findViewById(R.id.chip_all);
        Chip chipNew = view.findViewById(R.id.chip_new);
        Chip chipInProgress = view.findViewById(R.id.chip_in_progress);
        Chip chipCompleted = view.findViewById(R.id.chip_completed);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new WorkOrderAdapter(workOrder -> {
//            NavController navController = Navigation.findNavController(view);
//            navController.navigate(R.id.action_workOrderListFragment_to_workOrderDetailsFragment);
            //tymczasowo:
            Toast.makeText(requireContext(), "Work order clicked: " + workOrder.getTitle(), Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(WorkOrderViewModel.class);

        viewModel.getWorkOrders().observe(getViewLifecycleOwner(), workOrders -> {
            if(workOrders == null || workOrders.isEmpty()){
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }else{
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                adapter.setWorkOrders(workOrders);
            }
        });

        chipAll.setOnClickListener(v -> adapter.filterByStatus(""));
        chipNew.setOnClickListener(v -> adapter.filterByStatus("NEW"));
        chipInProgress.setOnClickListener(v -> adapter.filterByStatus("IN_PROGRESS"));
        chipCompleted.setOnClickListener(v -> adapter.filterByStatus("COMPLETED"));


        SwipeRefreshLayout swipe = view.findViewById(R.id.main);
        if (swipe != null){
            swipe.setOnRefreshListener(() -> {
                viewModel.refreshWorkOrders();
                swipe.setRefreshing(false);
            });
        }
    }
}