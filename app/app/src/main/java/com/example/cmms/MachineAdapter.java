package com.example.cmms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MachineAdapter extends RecyclerView.Adapter<MachineAdapter.MachineViewHolder> {

    private List<String> machineListFull = new ArrayList<>();
    private List<String> machineListDisplayed = new ArrayList<>();

    public void setMachines(List<String> machines) {
        this.machineListFull = new ArrayList<>(machines);
        this.machineListDisplayed = new ArrayList<>(machines);
        notifyDataSetChanged();
    }

    public void filter(String text) {
        machineListDisplayed.clear();
        if (text.isEmpty()) {
            machineListDisplayed.addAll(machineListFull);
        } else {
            String filterPattern = text.toLowerCase().trim();
            for (String item : machineListFull) {
                if (item.toLowerCase().contains(filterPattern)) {
                    machineListDisplayed.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MachineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_machine, parent, false);
        return new MachineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MachineViewHolder holder, int position) {
        holder.tvMachineName.setText(machineListDisplayed.get(position));
    }

    @Override
    public int getItemCount() {
        return machineListDisplayed.size();
    }

    static class MachineViewHolder extends RecyclerView.ViewHolder {
        TextView tvMachineName;
        public MachineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMachineName = itemView.findViewById(R.id.tv_machine_name);
        }
    }
}