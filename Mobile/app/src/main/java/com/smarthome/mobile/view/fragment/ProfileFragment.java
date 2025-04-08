package com.smarthome.mobile.view.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.smarthome.mobile.R;
import com.smarthome.mobile.app.MyApp;
import com.smarthome.mobile.databinding.FragmentProfileBinding;
import com.smarthome.mobile.view.activity.LoginActivity;
import com.smarthome.mobile.view.widget.CustomLoadingDialog;
import com.smarthome.mobile.view.widget.CustomToast;
import com.smarthome.mobile.viewmodel.AuthViewModel;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private AuthViewModel authViewModel;
    private CustomLoadingDialog loading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        loading = new CustomLoadingDialog(requireContext());

        binding.idTv.setText(String.valueOf(MyApp.getInstance().getSessionManager().fetchUserID()));
        binding.emailTv.setText(String.valueOf(MyApp.getInstance().getSessionManager().fetchUserEmail()));
        binding.addressTv.setText(String.valueOf(MyApp.getInstance().getSessionManager().fetchUserAddress()));

        binding.changePasswordBtn.setOnClickListener(v -> {

        });

        authViewModel.getLogoutStatus().observe(getViewLifecycleOwner(), result -> {
            switch (result.status) {
                case ERROR:
                    CustomToast.showError(requireContext(), result.message);
                    break;
                case SUCCESS:
                    loading.dismiss();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                    break;
                case LOADING:
                    loading.show();
                    break;
            }
        });

        authViewModel.getChangePasswordStatus().observe(getViewLifecycleOwner(), result -> {
            switch (result.status) {
                case ERROR:
                    CustomToast.showError(requireContext(), result.message);
                    break;
                case SUCCESS:
                    loading.dismiss();
                    break;
                case LOADING:
                    loading.show();
                    break;
            }
        });

        binding.logoutBtn.setOnClickListener(v -> {
            authViewModel.logout();
        });

        binding.changePasswordBtn.setOnClickListener(v -> showChangePasswordDialog());
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_password, null);
        builder.setView(view);
        builder.setTitle("Đổi mật khẩu");

        EditText edtOldPassword = view.findViewById(R.id.edtOldPassword);
        EditText edtNewPassword = view.findViewById(R.id.edtNewPassword);
        EditText edtConfirmPassword = view.findViewById(R.id.edtConfirmPassword);

        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            String oldPass = edtOldPassword.getText().toString();
            String newPass = edtNewPassword.getText().toString();
            String confirmPass = edtConfirmPassword.getText().toString();

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                CustomToast.showError(requireContext(), "Vui lòng điền đầy đủ thông tin");
                return;
            }

            if (!newPass.equals(confirmPass)) {
                CustomToast.showError(requireContext(), "Mật khẩu xác nhận không chính xác");
                return;
            }

            authViewModel.changePassword(oldPass, newPass);
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}