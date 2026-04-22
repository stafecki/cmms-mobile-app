package com.example.cmms;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MachineDetailsActivity extends AppCompatActivity {

    private MachineDetailsViewModel viewModel;
    private RecentJobsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_details);

        TextView tvName = findViewById(R.id.tv_detail_name);
        TextView tvSerial = findViewById(R.id.tv_detail_serial);
        TextView tvLocation = findViewById(R.id.tv_detail_location);
        TextView tvHours = findViewById(R.id.tv_detail_hours);
        RecyclerView rvJobs = findViewById(R.id.rv_recent_jobs);

        rvJobs.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecentJobsAdapter();
        rvJobs.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(MachineDetailsViewModel.class);

        viewModel.getMachineName().observe(this, tvName::setText);
        viewModel.getMachineSerial().observe(this, tvSerial::setText);
        viewModel.getMachineLocation().observe(this, tvLocation::setText);
        viewModel.getMachineHours().observe(this, tvHours::setText);

        viewModel.getRecentJobs().observe(this, jobs -> {
            if (jobs != null) {
                adapter.setJobs(jobs);
            }
        });

        viewModel.loadMachineDetails();
    }
}