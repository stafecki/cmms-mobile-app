package com.example.cmms;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmms.data.local.entities.WorkOrderEntity;

import java.util.ArrayList;
import java.util.List;

public class WorkOrderAdapter extends RecyclerView.Adapter<WorkOrderAdapter.WorkOrderViewHolder> {

    private List<WorkOrderEntity> workOrderList = new ArrayList<>();

    public void setWorkOrders(List<WorkOrderEntity> workOrders) {
        this.workOrderList = new ArrayList<>(workOrders);
        notifyDataSetChanged();
    }

    public interface onWorkOrderClickListener {
        void onWorkOrderClick(WorkOrderEntity workOrder);
    }

    private final onWorkOrderClickListener listener;

    public WorkOrderAdapter(onWorkOrderClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public WorkOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_work_order, parent, false);
        return new WorkOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkOrderViewHolder holder, int position) {
        WorkOrderEntity workOrder = workOrderList.get(position);
        holder.tvTitle.setText(workOrder.getTitle());
        holder.tvPriority.setText(workOrder.getPriority());
        holder.tvStatus.setText(workOrder.getStatus());

        if (workOrder.getMachineName() != null && !workOrder.getMachineName().isEmpty()) {
            holder.tvMachine.setText("Maszyna: " + workOrder.getMachineName());
            holder.tvMachine.setVisibility(View.VISIBLE);
        } else {
            holder.tvMachine.setVisibility(View.GONE);
        }

        if (workOrder.getAssignedToName() != null && !workOrder.getAssignedToName().isEmpty()) {
            holder.tvAssignee.setText("Technik: " + workOrder.getAssignedToName());
            holder.tvAssignee.setVisibility(View.VISIBLE);
        } else {
            holder.tvAssignee.setVisibility(View.GONE);
        }

        int bgColorRes;
        int textColorRes;
        switch (workOrder.getStatus() != null ? workOrder.getStatus() : "") {
            case "NEW":
                bgColorRes = R.color.status_new_bg;
                textColorRes = R.color.status_new_text;
                break;
            case "IN_PROGRESS":
                bgColorRes = R.color.status_in_progress_bg;
                textColorRes = R.color.status_in_progress_text;
                break;
            case "COMPLETED":
                bgColorRes = R.color.status_completed_bg;
                textColorRes = R.color.status_completed_text;
                break;
            default:
                bgColorRes = R.color.darker_oat;
                textColorRes = R.color.noir;
                break;
        }

        GradientDrawable badge = new GradientDrawable();
        badge.setShape(GradientDrawable.RECTANGLE);
        badge.setCornerRadius(100f);
        badge.setColor(ContextCompat.getColor(holder.itemView.getContext(), bgColorRes));
        holder.tvStatus.setBackground(badge);
        holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), textColorRes));

        holder.itemView.setOnClickListener(v -> listener.onWorkOrderClick(workOrder));
    }

    @Override
    public int getItemCount() {
        return workOrderList.size();
    }

    static class WorkOrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvPriority;
        TextView tvStatus;
        TextView tvMachine;
        TextView tvAssignee;

        public WorkOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_work_order_title);
            tvPriority = itemView.findViewById(R.id.tv_work_order_priority);
            tvStatus = itemView.findViewById(R.id.tv_work_order_status);
            tvMachine = itemView.findViewById(R.id.tv_work_order_machine);
            tvAssignee = itemView.findViewById(R.id.tv_work_order_assignee);
        }
    }
}
