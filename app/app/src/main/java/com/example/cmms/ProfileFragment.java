package com.example.cmms;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cmms.ui.profile.ProfileViewModel;
import com.example.cmms.utils.NetworkUtils;
import com.google.android.material.button.MaterialButton;

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
            tvRole.setText(user.getRole());

            String initials = "";
            String[] parts = fullName.trim().split("\\s+");
            for (String part : parts) {
                if (!part.isEmpty()) initials += part.charAt(0);
                if (initials.length() == 2) break;
            }
            tvAvatar.setText(initials.toUpperCase());

            if (llCertificates != null) {
                llCertificates.removeAllViews();
                TextView tvEmpty = new TextView(requireContext());
                tvEmpty.setText(R.string.label_no_certificates);
                tvEmpty.setTextSize(14f);
                tvEmpty.setTextColor(getResources().getColor(R.color.grey_text, null));
                llCertificates.addView(tvEmpty);
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
}
