package com.example.cmms;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.cmms.data.repository.AuthRepository;
import com.example.cmms.ui.login.LoginViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private Button btnLogin;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(new AuthRepository(this).isLoggedIn()){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> handleLogin());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        loginViewModel.getErrorMessage().observe(this, errorMessage -> {
            if(errorMessage != null){
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        loginViewModel.getLoginSuccess().observe(this, loginResult -> {
            if(loginResult != null && loginResult){
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });
        loginViewModel.getIsLoading().observe(this, loading -> {
            btnLogin.setEnabled(!loading);
        });
    }

    private void handleLogin() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        loginViewModel.login(email, password);
    }
}