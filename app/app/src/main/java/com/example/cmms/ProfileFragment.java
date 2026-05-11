package com.example.cmms;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cmms.utils.NetworkUtils;
import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private ProfileViewModel viewModel;

    public ProfileFragment() {}

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(!NetworkUtils.isNetworkAvailable(requireContext())){
            Toast.makeText(requireContext(), "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();
        }

        TextView tvAvatar = view.findViewById(R.id.tv_avatar);
        TextView tvName = view.findViewById(R.id.tv_name);
        TextView tvEmail = view.findViewById(R.id.tv_email);
        TextView tvRole = view.findViewById(R.id.tv_role);
        LinearLayout llCertificates = view.findViewById(R.id.ll_certificates);

        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            String firstName = user.getFirstName();
            String lastName = user.getLastName();

            tvName.setText(firstName + " " + lastName);
            tvEmail.setText(user.getEmail());
            tvRole.setText(user.getRole());

            String initials = "";
            if (firstName != null && !firstName.isEmpty()) initials += firstName.charAt(0);
            if (lastName != null && !lastName.isEmpty()) initials += lastName.charAt(0);
            tvAvatar.setText(initials.toUpperCase());

            llCertificates.removeAllViews();
            if (user.getCertificates() == null || user.getCertificates().isEmpty()) {
                TextView tvEmpty = new TextView(requireContext());
                tvEmpty.setText(R.string.label_no_certificates);
                tvEmpty.setTextSize(14f);
                tvEmpty.setTextColor(getResources().getColor(R.color.grey_text, null));
                llCertificates.addView(tvEmpty);
            } else {
                int certCount = user.getCertificates().size();
                for (int i = 0; i < certCount; i++) {
                    User.Certificate cert = user.getCertificates().get(i);

                    LinearLayout row = new LinearLayout(requireContext());
                    row.setOrientation(LinearLayout.HORIZONTAL);

                    LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    row.setLayoutParams(rowParams);

                    TextView tvCertName = new TextView(requireContext());
                    LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                    tvCertName.setLayoutParams(nameParams);
                    tvCertName.setText(cert.name());
                    tvCertName.setTextSize(14f);
                    tvCertName.setTextColor(getResources().getColor(R.color.noir, null));
                    tvCertName.setTypeface(null, android.graphics.Typeface.BOLD);

                    TextView tvCertDate = new TextView(requireContext());
                    tvCertDate.setText(cert.date());
                    tvCertDate.setTextSize(12f);
                    tvCertDate.setTextColor(getResources().getColor(R.color.grey_text, null));

                    row.addView(tvCertName);
                    row.addView(tvCertDate);
                    llCertificates.addView(row);

                    if (i < certCount - 1) {
                        View divider = new View(requireContext());
                        LinearLayout.LayoutParams divParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, 1);
                        int margin = (int) (12 * getResources().getDisplayMetrics().density);
                        divParams.topMargin = margin;
                        divParams.bottomMargin = margin;
                        divider.setLayoutParams(divParams);
                        divider.setBackgroundColor(getResources().getColor(R.color.darker_oat, null));
                        llCertificates.addView(divider);
                    }
                }
            }
        });

        MaterialButton btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        });
    }
}
