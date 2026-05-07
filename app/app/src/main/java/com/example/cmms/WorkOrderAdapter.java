package com.example.cmms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class WorkOrderAdapter extends RecyclerView.Adapter<WorkOrderAdapter.WorkOrderViewHolder> {


    private List<WorkOrder> workOrdersFull;
    private List<WorkOrder> workOrdersDisplayed;

    public void setWorkOrders(List<WorkOrder> workOrders) {
        this.workOrdersFull = new ArrayList<>(workOrders);
        this.workOrdersDisplayed = new ArrayList<>(workOrders);
        notifyDataSetChanged();
    }

    public void filterByStatus(String status){
        workOrdersDisplayed.clear();
        if (status.isEmpty()) {
            workOrdersDisplayed.addAll(workOrdersFull);
        } else {
            for (WorkOrder workOrder : workOrdersFull) {
                if (workOrder.getStatus().equals(status)) {
                    workOrdersDisplayed.add(workOrder);
                }
            }
        }
        notifyDataSetChanged();
    }

    public interface onWorkOrderClickListener{
        void onWorkOrderClick(WorkOrder workOrder);
    }

    private onWorkOrderClickListener listener;
    public WorkOrderAdapter(onWorkOrderClickListener listener){
        this.listener = listener;
    }


    @NonNull
    @Override
    public WorkOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_work_order, parent, false);
        return new WorkOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkOrderViewHolder holder, int position) {
        holder.tvTitle.setText(workOrdersDisplayed.get(position).getTitle());
        holder.tvPriority.setText(workOrdersDisplayed.get(position).getPriority());
        holder.tvStatus.setText(workOrdersDisplayed.get(position).getStatus());
        holder.itemView.setOnClickListener(view -> {
            listener.onWorkOrderClick(workOrdersDisplayed.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return workOrdersDisplayed.size();
    }
    static class WorkOrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvPriority;
        TextView tvStatus;

        public WorkOrderViewHolder(@NonNull View itemView) {
            super (itemView);
            tvTitle = itemView.findViewById(R.id.tv_work_order_title);
            tvPriority = itemView.findViewById(R.id.tv_work_order_priority);
            tvStatus = itemView.findViewById(R.id.tv_work_order_status);
        }

    }
}
