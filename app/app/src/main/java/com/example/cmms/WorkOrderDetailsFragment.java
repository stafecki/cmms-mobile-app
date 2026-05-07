package com.example.cmms;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkOrderDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkOrderDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WorkOrderDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorkOrderDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkOrderDetailsFragment newInstance(String param1, String param2) {
        WorkOrderDetailsFragment fragment = new WorkOrderDetailsFragment();
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
        return inflater.inflate(R.layout.fragment_work_order_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        TextView tvTitle = view.findViewById(R.id.tv_work_order_details_title);
        TextView tvDescription = view.findViewById(R.id.tv_work_order_details_description);
        TextView tvPriority = view.findViewById(R.id.tv_work_order_details_priority);
        TextView tvStatus = view.findViewById(R.id.tv_work_order_details_status);
        TextView tvWorker = view.findViewById(R.id.tv_work_order_details_worker);
        TextView tvParts = view.findViewById(R.id.tv_work_order_details_parts);

        Bundle bundle = getArguments();
        if(bundle != null){
            tvTitle.setText(bundle.getString("title"));
            tvDescription.setText(bundle.getString("description"));
            tvPriority.setText(bundle.getString("priority"));
            tvStatus.setText(bundle.getString("status"));
        }
    }
}