package com.example.cmms;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cmms.data.remote.models.UserResponse;
import com.example.cmms.ui.profile.ProfileViewModel;
import com.example.cmms.utils.NetworkUtils;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            Toast.makeText(requireContext(), "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();
        }

        TextView tvAvatar = view.findViewById(R.id.tv_avatar);
        TextView tvName = view.findViewById(R.id.tv_name);
        TextView tvEmail = view.findViewById(R.id.tv_email);
        TextView tvRole = view.findViewById(R.id.tv_role);
        LinearLayout llCertificates = view.findViewById(R.id.ll_certificates);

        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) return;

            String fullName = user.getName() != null ? user.getName() : "";
            tvName.setText(fullName);
            tvEmail.setText(user.getEmail());
            tvRole.setText(translateRole(user.getRole()));

            String initials = "";
            String[] parts = fullName.trim().split("\\s+");
            for (String part : parts) {
                if (!part.isEmpty()) initials += part.charAt(0);
                if (initials.length() == 2) break;
            }
            tvAvatar.setText(initials.toUpperCase());

            if (llCertificates != null) {
                llCertificates.removeAllViews();
                List<UserResponse.Certification> certs = user.getCertifications();
                if (certs == null || certs.isEmpty()) {
                    TextView tvEmpty = new TextView(requireContext());
                    tvEmpty.setText(R.string.label_no_certificates);
                    tvEmpty.setTextSize(14f);
                    tvEmpty.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_text));
                    llCertificates.addView(tvEmpty);
                } else {
                    for (UserResponse.Certification cert : certs) {
                        View certView = buildCertificateView(cert);
                        llCertificates.addView(certView);
                    }
                }
            }
        });

        viewModel.getLogoutSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                startActivity(new Intent(requireContext(), LoginActivity.class));
                requireActivity().finish();
            }
        });

        viewModel.loadProfile();

        MaterialButton btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> viewModel.logout());
    }

    private View buildCertificateView(UserResponse.Certification cert) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 8, 0, 8);

        TextView tvType = new TextView(requireContext());
        tvType.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        tvType.setText(translateCertType(cert.getType()));
        tvType.setTextSize(14f);
        tvType.setTextColor(ContextCompat.getColor(requireContext(), R.color.noir));

        TextView tvStatus = new TextView(requireContext());
        tvStatus.setTextSize(12f);
        if (cert.isValid()) {
            tvStatus.setText(R.string.cert_valid);
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_completed_text));
        } else {
            tvStatus.setText(R.string.cert_expired);
            tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.critical_text));
        }

        row.addView(tvType);
        row.addView(tvStatus);
        return row;
    }

    private String translateRole(String role) {
        if (role == null) return "";
        switch (role) {
            case "ADMIN": return "Administrator";
            case "MANAGER": return "Kierownik";
            case "TECHNICIAN": return "Technik";
            case "WAREHOUSE": return "Magazynier";
            case "OPERATOR": return "Operator";
            default: return role;
        }
    }

    private String translateCertType(String type) {
        if (type == null) return "";
        switch (type) {
            case "SEP": return "Uprawnienia SEP";
            case "FORKLIFT": return "Wózek widłowy";
            case "GAS": return "Uprawnienia gazowe";
            case "HEIGHT_WORK": return "Praca na wysokości";
            case "WELDING": return "Uprawnienia spawalnicze";
            case "OTHER": return "Inne";
            default: return type;
        }
    }
}
