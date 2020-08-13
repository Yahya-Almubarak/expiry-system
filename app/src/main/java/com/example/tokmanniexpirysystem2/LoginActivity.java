package com.example.tokmanniexpirysystem2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tokmanniexpirysystem2.databinding.Listener;
import com.example.tokmanniexpirysystem2.databinding.ActivityLoginBinding;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import com.example.tokmanniexpirysystem2.entities.User;
import com.example.tokmanniexpirysystem2.repositories.UserRepository;
import com.example.tokmanniexpirysystem2.utilities.Authintication;
import com.example.tokmanniexpirysystem2.utilities.HistoryLogger;
import com.example.tokmanniexpirysystem2.utilities.Role;
import com.example.tokmanniexpirysystem2.viewmodels.LoginViewModel;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LoginActivity extends AppCompatActivity implements Listener {
private LoginViewModel loginViewModel;
private List<User> mUsers;

private ActivityLoginBinding binding;

@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean prefUserAuthinticationOn = sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_USER_AUTHENTICATION, true );
        if(!prefUserAuthinticationOn) {
                User user = new User();
                user.setUserName(getResources().getString(R.string.settings_default_user_name));
                user.setFirstName(getResources().getString(R.string.settings_default_user_first_name));
                user.setLastName(getResources().getString(R.string.settings_default_user_last_name));
                user.setRole(Role.DEFAULT);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("username",getResources().getString(R.string.settings_default_user_name));
                intent.putExtra("user", bundle);
                getApplicationContext().startActivity(intent);
                finish();
        }
        binding = DataBindingUtil.setContentView(LoginActivity.this, R.layout.activity_login);

        binding.setClickListener((LoginActivity) this);


        loginViewModel = ViewModelProviders.of(LoginActivity.this).get(LoginViewModel.class);

        loginViewModel.getGetAllData().observe(this, new Observer<List<User>>() {
@Override
public void onChanged(@Nullable List<User> users) {

        if(!users.stream().anyMatch(u -> u.getRole().equals(Role.ADMIN))) {
                User admin = new User();
                admin.setUserName("admin");
                admin.setFirstName("admin");
                admin.setLastName("admin");
                admin.setPassword("admin");
                admin.setRole(Role.ADMIN);
                admin.setCreationDate(new Date());
                loginViewModel.insert(admin);
        }

        mUsers = users;
        try {
        } catch (Exception e) {
        e.printStackTrace();
        }
        }
        });


        }

@Override
public void onClick(View view) {

        String strUserName = binding.loginUsernameTxt.getText().toString().trim();
        String strPassword = binding.loginPasswordTxt.getText().toString().trim();
        UserRepository userRepository = new UserRepository(getApplication());
        User user = new User();

        if (TextUtils.isEmpty(strUserName)) {
        binding.loginUsernameTxt.setError(getApplicationContext().getResources().getString(R.string.login_user_name_error_txt));
        binding.loginUsernameTxt.requestFocus();
        }
        else if (TextUtils.isEmpty(strPassword)) {
        binding.loginPasswordTxt.setError(getApplicationContext().getResources().getString(R.string.login_password_error_txt));
        binding.loginPasswordTxt.requestFocus();
        }
        else {

        Optional<User> result = mUsers.stream().filter(u -> u.getUserName().equals(strUserName)).findAny();

        if(!result.isPresent()) {
                binding.loginUsernameTxt.setError(getApplicationContext().getResources().getString(R.string.login_user_not_exist_error));
                binding.loginUsernameTxt.requestFocus();
        } else if(result.get().getPassword() != null && result.get().getPassword().equals(strPassword)) {
                user = result.get();
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("username", strUserName);
                intent.putExtra("role", user.getRole().toString());
                intent.putExtra("user", bundle);
                getApplicationContext().startActivity(intent);
                finish();

        } else {
                binding.loginPasswordTxt.setError(getApplicationContext().getResources().getString(R.string.login_password_error_txt));
                binding.loginPasswordTxt.requestFocus();
        }


        }

        }
        }
/*
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText usernameEditText;
    EditText passwordEditText;
     Button loginButton;
     ProgressBar loadingProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);

        loginButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
         String userName = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if(userName.isEmpty()) {
            usernameEditText.setError(getResources().getString(R.string.login_user_name_error));
            return;
        }
        if(!Authintication.isUserName(userName)){
            usernameEditText.setError(getResources().getString(R.string.login_user_name_error));
        }
        if(Authintication.isCreditionalOK(userName, password )){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("username", userName);
            getApplicationContext().startActivity(intent);
            finish();
        }
    }
} */
