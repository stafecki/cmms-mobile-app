package com.example.cmms.data.remote.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class DashboardResponse {

    private String period;
    private String from;
    private String to;

    @SerializedName("workOrders")
    private WorkOrderStats workOrders;

    private CostStats costs;
    private MachineStats machines;
    private InventoryStats inventory;
    private PreventiveStats preventive;

    public String getPeriod() { return period; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public WorkOrderStats getWorkOrders() { return workOrders; }
    public CostStats getCosts() { return costs; }
    public MachineStats getMachines() { return machines; }
    public InventoryStats getInventory() { return inventory; }
    public PreventiveStats getPreventive() { return preventive; }

    public static DashboardResponse fromBasicStats(int openCount, int criticalCount) {
        DashboardResponse response = new DashboardResponse();
        WorkOrderStats stats = new WorkOrderStats();
        stats.open = openCount;
        stats.critical = criticalCount;
        response.workOrders = stats;
        return response;
    }

    public static class WorkOrderStats {
        private int total;
        private Map<String, Integer> byStatus;
        private Map<String, Integer> byPriority;
        private int open;
        private int critical;

        public int getTotal() { return total; }
        public Map<String, Integer> getByStatus() { return byStatus; }
        public Map<String, Integer> getByPriority() { return byPriority; }
        public int getOpen() { return open; }
        public int getCritical() { return critical; }
    }

    public static class CostStats {
        private double totalLaborCost;
        private double totalPartsCost;
        private double totalCost;
        private List<MachineCost> topMachinesByCost;

        public double getTotalLaborCost() { return totalLaborCost; }
        public double getTotalPartsCost() { return totalPartsCost; }
        public double getTotalCost() { return totalCost; }
        public List<MachineCost> getTopMachinesByCost() { return topMachinesByCost; }
    }

    public static class MachineCost {
        private String machineId;
        private String machineName;
        private double totalCost;

        public String getMachineId() { return machineId; }
        public String getMachineName() { return machineName; }
        public double getTotalCost() { return totalCost; }
    }

    public static class MachineStats {
        private int total;
        private int active;
        private double mttr;
        private double mtbf;

        public int getTotal() { return total; }
        public int getActive() { return active; }
        public double getMttr() { return mttr; }
        public double getMtbf() { return mtbf; }
    }

    public static class InventoryStats {
        private int totalParts;
        private int lowStockCount;
        private int activeLoans;

        public int getTotalParts() { return totalParts; }
        public int getLowStockCount() { return lowStockCount; }
        public int getActiveLoans() { return activeLoans; }
    }

    public static class PreventiveStats {
        private int totalPlans;
        private int upcomingIn7Days;
        private int overdue;

        public int getTotalPlans() { return totalPlans; }
        public int getUpcomingIn7Days() { return upcomingIn7Days; }
        public int getOverdue() { return overdue; }
    }
}