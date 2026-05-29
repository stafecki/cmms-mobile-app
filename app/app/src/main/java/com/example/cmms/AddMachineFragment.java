package com.example.cmms;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.cmms.data.remote.models.LocationResponse;
import com.example.cmms.ui.machines.AddMachineViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddMachineFragment extends Fragment {

    private List<LocationResponse> locationList = new ArrayList<>();
    private String editMachineId = null;
    private boolean dataPreFilled = false;

    public AddMachineFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_machine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editMachineId = getArguments() != null ? getArguments().getString("editMachineId") : null;
        boolean isEditMode = editMachineId != null;

        TextInputEditText etName = view.findViewById(R.id.et_machine_name);
        TextInputEditText etSerial = view.findViewById(R.id.et_serial_number);
        TextInputEditText etHours = view.findViewById(R.id.et_operating_hours);
        TextInputEditText etPurchaseDate = view.findViewById(R.id.et_purchase_date);
        TextInputEditText etPurchasePrice = view.findViewById(R.id.et_purchase_price);
        Spinner spinnerLocation = view.findViewById(R.id.spinner_location);
        MaterialButton btnSave = view.findViewById(R.id.btn_save_machine);

        AddMachineViewModel viewModel = new ViewModelProvider(this).get(AddMachineViewModel.class);

        etPurchaseDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (dp, year, month, day) -> {
                String date = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
                etPurchaseDate.setText(date);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        viewModel.getLocations().observe(getViewLifecycleOwner(), locations -> {
            if (locations == null || locations.isEmpty()) return;
            locationList = locations;
            List<String> names = new ArrayList<>();
            for (LocationResponse loc : locations) {
                names.add(loc.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, names);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLocation.setAdapter(adapter);

            if (isEditMode && !dataPreFilled) {
                viewModel.loadMachineForEdit(editMachineId);
            }
        });

        if (isEditMode) {
            viewModel.getMachineToEdit().observe(getViewLifecycleOwner(), machine -> {
                if (machine == null || dataPreFilled) return;
                dataPreFilled = true;

                etName.setText(machine.getName());
                etSerial.setText(machine.getSerialNumber());
                etHours.setText(String.valueOf((int) machine.getOperatingHours()));

                String locId = machine.getLocationId();
                if (locId == null && machine.getLocation() != null) {
                    locId = machine.getLocation().getId();
                }
                if (locId != null) {
                    for (int i = 0; i < locationList.size(); i++) {
                        if (locationList.get(i).getId().equals(locId)) {
                            spinnerLocation.setSelection(i);
                            break;
                        }
                    }
                }
            });
        }

        viewModel.loadLocations();

        btnSave.setOnClickListener(v -> {
            String name = etName.getText() != null ? etName.getText().toString().trim() : "";
            String serial = etSerial.getText() != null ? etSerial.getText().toString().trim() : "";
            String hoursStr = etHours.getText() != null ? etHours.getText().toString().trim() : "";
            String dateStr = etPurchaseDate.getText() != null ? etPurchaseDate.getText().toString().trim() : "";
            String priceStr = etPurchasePrice.getText() != null ? etPurchasePrice.getText().toString().trim() : "";

            if (name.isEmpty()) {
                etName.setError(getString(R.string.error_empty_field));
                return;
            }
            if (serial.isEmpty()) {
                etSerial.setError(getString(R.string.error_empty_field));
                return;
            }
            if (locationList.isEmpty() || spinnerLocation.getSelectedItemPosition() < 0) {
                Toast.makeText(requireContext(), getString(R.string.error_select_location), Toast.LENGTH_SHORT).show();
                return;
            }

            String locationId = locationList.get(spinnerLocation.getSelectedItemPosition()).getId();
            double hours = hoursStr.isEmpty() ? 0 : Double.parseDouble(hoursStr);

            if (isEditMode) {
                String purchaseDate = dateStr.isEmpty() ? null : dateStr;
                double purchasePrice = priceStr.isEmpty() ? 0 : Double.parseDouble(priceStr);
                viewModel.updateMachine(editMachineId, name, serial, locationId, hours, purchaseDate, purchasePrice);
            } else {
                if (dateStr.isEmpty()) {
                    etPurchaseDate.setError(getString(R.string.error_empty_field));
                    return;
                }
                if (priceStr.isEmpty()) {
                    etPurchasePrice.setError(getString(R.string.error_empty_field));
                    return;
                }
                double price = Double.parseDouble(priceStr);
                viewModel.saveMachine(name, serial, locationId, hours, dateStr, price);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            btnSave.setEnabled(loading == null || !loading);
        });

        viewModel.getSaveSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                String msg = isEditMode ? getString(R.string.msg_machine_updated) : getString(R.string.msg_machine_added);
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).popBackStack();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
