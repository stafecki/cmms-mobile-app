package com.example.cmms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class WorkOrderViewModel extends ViewModel {
    private MutableLiveData<List<WorkOrder>> workOrders;

    public LiveData<List<WorkOrder>> getWorkOrders() {
        if(workOrders == null) {
            workOrders = new MutableLiveData<>();
            loadWorkOrders();
        }
        return workOrders;
    }

    public void refreshWorkOrders() {
        loadWorkOrders();
    }

    private void loadWorkOrders() {
        List<WorkOrder> workOrderList = new ArrayList<>();
        workOrderList.add(new WorkOrder("Zamówienie 1", "Opis zamówienia 1", "HIGH", "IN_PROGRESS"));
        workOrderList.add(new WorkOrder("Zamówienie 2", "Opis zamówienia 2", "MEDIUM", "IN_PROGRESS"));
        workOrderList.add(new WorkOrder("Zamówienie 3", "Opis zamówienia 3", "LOW", "COMPLETED"));
        workOrderList.add(new WorkOrder("Zamówienie 4", "Opis zamówienia 4", "HIGH", "IN_PROGRESS"));
        workOrderList.add(new WorkOrder("Zamówienie 5", "Opis zamówienia 5", "MEDIUM", "NEW"));
        workOrders.setValue(workOrderList);
    }
}

