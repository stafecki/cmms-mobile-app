package com.example.cmms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmms.data.local.entities.MachineEntity;

import java.util.ArrayList;
import java.util.List;

public class MachineAdapter extends RecyclerView.Adapter<MachineAdapter.MachineViewHolder> {

    private List<MachineEntity> machineList = new ArrayList<>();

    public void setMachines(List<MachineEntity> machines) {
        this.machineList = new ArrayList<>(machines);
        notifyDataSetChanged();
    }

    public interface onMachineClickListener {
        void onMachineClick(MachineEntity machine);
    }

    private final onMachineClickListener listener;

    public MachineAdapter(onMachineClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MachineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_machine, parent, false);
        return new MachineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MachineViewHolder holder, int position) {
        MachineEntity machine = machineList.get(position);
        holder.tvMachineName.setText(machine.getName());
        holder.itemView.setOnClickListener(v -> listener.onMachineClick(machine));
    }

    @Override
    public int getItemCount() {
        return machineList.size();
    }

    static class MachineViewHolder extends RecyclerView.ViewHolder {
        TextView tvMachineName;

        public MachineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMachineName = itemView.findViewById(R.id.tv_machine_name);
        }
    }
}