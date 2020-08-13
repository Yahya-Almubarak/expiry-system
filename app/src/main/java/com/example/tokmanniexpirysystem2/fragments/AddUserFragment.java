package com.example.tokmanniexpirysystem2.fragments;


import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tokmanniexpirysystem2.R;
import com.example.tokmanniexpirysystem2.database.DatabaseClient;
import com.example.tokmanniexpirysystem2.entities.User;
import com.example.tokmanniexpirysystem2.utilities.Authintication;
import com.example.tokmanniexpirysystem2.utilities.LocalizedRoleConverter;
import com.example.tokmanniexpirysystem2.utilities.Role;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddUserFragment extends Fragment implements View.OnClickListener {
    
    User currentUser;
    private boolean passwordhasChanged;

    private Button clearButton;
    private Button saveButton;

    private CheckBox editMyProfileCheckbox;

    private EditText firstNameTxt;
    private EditText lastNameTxt;

    private Spinner roleSpinner;

    private TextView notesLabel;
    private EditText notesTxt;

    private TextView userNameLabel;
    private  EditText userNameTxt;

    private CheckBox changePasswordCheckbox;

    private TextView passwordLabel;
    private EditText passwordTxt;

    private TextView confirmPasswordLabel;
    private EditText confirmPasswordTxt;




    // Form parameters
    String firstName;
    String lastName;
    String userName;
    String password;
    String role;
    Date creationDate;
    String notes;

    public AddUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        currentUser = Authintication.user;
        View view = inflater.inflate(R.layout.fragment_add_user, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeRoleSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        Role[] roles;
        if(isAdmin()) {
            roles = Role.values();
        }
        else {
            roles = new Role[] {Role.USER};
        }

        //String[] rolesAsString = Arrays.stream(roles).map(Enum::name).toArray(String[]::new);
        String[] rolesAsString = LocalizedRoleConverter.getInstance(getContext()).localizedStringArrayOfRoles();
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getContext(),
                android.R.layout.simple_spinner_item, rolesAsString);
      //  ArrayAdapter..createFromResource(getContext(), roles, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        roleSpinner.setAdapter(adapter);
        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               // role = parent.getItemAtPosition(position).toString();
                role = Role.values()[position].toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initializeViews(View view) {

        clearButton = view.findViewById(R.id.add_user_clear_btn);
        clearButton.setOnClickListener(this);

        saveButton = view.findViewById(R.id.add_user_save_btn);
        saveButton.setOnClickListener(this);

        editMyProfileCheckbox = view.findViewById(R.id.add_user_edit_my_profile_checkbox);
        editMyProfileCheckbox.setOnCheckedChangeListener(new EditMyProfileCheckboxListener());

        firstNameTxt = view.findViewById(R.id.add_user_first_name_txt);
        lastNameTxt = view.findViewById(R.id.add_user_last_name_txt);



        roleSpinner = view.findViewById(R.id.add_user_role_spinner);
        notesTxt = view.findViewById(R.id.add_user_note_txt);
        notesLabel = view.findViewById(R.id.add_user_note_label);


        userNameLabel = view.findViewById(R.id.add_user_username_label);
        userNameTxt = view.findViewById(R.id.add_user_username_txt);



        firstNameTxt.addTextChangedListener(new UserNameTextChangeWatcher());
        lastNameTxt.addTextChangedListener(new UserNameTextChangeWatcher());


        changePasswordCheckbox = view.findViewById(R.id.add_user_change_password_checkbox);
        changePasswordCheckbox.setOnCheckedChangeListener(new ChangePasswordCheckboxListener());

        passwordLabel = view.findViewById(R.id.add_user_password_label);
        passwordTxt = view.findViewById(R.id.add_user_password_txt);
        passwordTxt.addTextChangedListener(new PasswordTextChangeWatcher());

        confirmPasswordLabel = view.findViewById(R.id.add_user_confirm_password_label);
        confirmPasswordTxt = view.findViewById(R.id.add_user_confirm_password_txt);
        confirmPasswordTxt.addTextChangedListener(new PasswordTextChangeWatcher());

        if(isUser()) {
            firstNameTxt.setEnabled(false);
            lastNameTxt.setEnabled(false);
            notesLabel.setVisibility(View.GONE);
            notesTxt.setVisibility(View.GONE);
            userNameTxt.setEnabled(true);
            passwordLabel.setVisibility(View.GONE);
            passwordTxt.setVisibility(View.GONE);
            confirmPasswordLabel.setVisibility(View.GONE);
            confirmPasswordTxt.setVisibility(View.GONE);
            editMyProfileCheckbox.setVisibility(View.GONE);
            clearButton.setText(getResources().getString(R.string.add_user_cancel_button_title));
            saveButton.setEnabled(false);
            clearButton.setEnabled(false);
            renderViewsWithUserInfo();

        }
        else if(isAdmin()) {
            firstNameTxt.setEnabled(true);
            lastNameTxt.setEnabled(true);
            editMyProfileCheckbox.setVisibility(View.VISIBLE);
            userNameTxt.setEnabled(false);
            changePasswordCheckbox.setVisibility(View.GONE);
            passwordLabel.setVisibility(View.GONE);
            passwordTxt.setVisibility(View.GONE);
            confirmPasswordLabel.setVisibility(View.GONE);
            confirmPasswordTxt.setVisibility(View.GONE);
            clearButton.setText(getResources().getString(R.string.add_user_clear_button_title));
        }

        initializeRoleSpinner();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_user_clear_btn:
                clearForm();
                break;
            case R.id.add_user_save_btn:
                saveUser();
                break;
        }

    }

    private void renderViewsWithUserInfo() {
        firstNameTxt.setText(Authintication.user.getFirstName());
        lastNameTxt.setText(Authintication.user.getLastName());
        userNameTxt.setText(Authintication.user.getUserName());
        if(isAdmin()) {
            notesTxt.setText(Authintication.user.getNotes());
            roleSpinner.setSelection(1);
        } else {
            roleSpinner.setSelection(0);
        }
    }

    private void clearForm() {
        if(isAdminAddingUser()) {
            firstNameTxt.setText("");
            lastNameTxt.setText("");
            notesTxt.setText("");
            roleSpinner.setSelection(0);
            userNameTxt.setText("");
        }
        if(isAdminEdittingHisProfile()) {
            editMyProfileCheckbox.setChecked(false);
        }
        if(isUser()) {
            renderViewsWithUserInfo();
        }
        changePasswordCheckbox.setChecked(false);
        passwordLabel.setVisibility(View.GONE);
        passwordTxt.setVisibility(View.GONE);
        confirmPasswordLabel.setVisibility(View.GONE);
        confirmPasswordTxt.setVisibility(View.GONE);
    }

    private void setFormValues() {

        creationDate = Calendar.getInstance().getTime();
        firstName = firstNameTxt.getText().toString().trim();
        lastName = lastNameTxt.getText().toString().trim();

        if (isAdmin()) {
            notes = notesTxt.getText().toString().trim();
            }
        if(isUser() || isAdminEdittingHisProfile()) {
            if(passwordhasChanged){
                password = passwordTxt.getText().toString().trim();
            }
            userName = userNameTxt.getText().toString().trim();
        }
        if(isAdminAddingUser()){
            userName = firstName + "." + lastName;
            password = "user";
        }
    }

    private void saveUser() {
        setFormValues();
        if (firstName.isEmpty()) {
            firstNameTxt.setError(getResources().getString(R.string.add_user_first_name_empty_error));
            firstNameTxt.requestFocus();
            return;
        }

        if (lastName.isEmpty()) {
            lastNameTxt.setError(getResources().getString(R.string.add_user_last_name_empty_error));
            lastNameTxt.requestFocus();
            return;
        }


        class SaveUser extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                //creating a user and adding to database
                if (isAdminAddingUser()) {
                    User user = new User();
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setUserName(userName);
                    user.setPassword(password);
                    user.setCreationDate(creationDate);
                    user.setRole(Role.valueOf(role));
                    user.setNotes(notes);
                    DatabaseClient.getInstance(getActivity().getApplicationContext()).getAppDatabase()
                            .userDao()
                            .insert(user);
                }
                else if (isAdminEdittingHisProfile() || isUser()) {

                    currentUser.setUserName(userName);
                    currentUser.setCreationDate(creationDate);
                    currentUser.setRole(Role.valueOf(role));
                    if (isAdminEdittingHisProfile()) {
                        currentUser.setFirstName(firstName);
                        currentUser.setLastName(lastName);
                        currentUser.setNotes(notes);
                    }
                    if(passwordhasChanged) {
                        currentUser.setPassword(password);
                    }
                    DatabaseClient.getInstance(getActivity().getApplicationContext()).getAppDatabase()
                            .userDao()
                            .update(currentUser);
                }


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getActivity().getApplicationContext(),  userName+ " " + password + " " +getResources().getString(R.string.add_user_saved_toast), Toast.LENGTH_SHORT).show();
            }
        }

        SaveUser su = new SaveUser();
        su.execute();
    }




    private boolean isUser() {
        return Authintication.user.getRole().equals(Role.USER);
    }
    private boolean isAdminEdittingHisProfile() {
        return Authintication.user.getRole().equals(Role.ADMIN) && editMyProfileCheckbox.isChecked();
    }
    private boolean isAdmin() {
        return Authintication.user.getRole().equals(Role.ADMIN);
    }
    private boolean isAdminAddingUser() {
        return Authintication.user.getRole().equals(Role.ADMIN) && !editMyProfileCheckbox.isChecked();
    }

    private class UserNameTextChangeWatcher implements TextWatcher {
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {

         }

         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {
            // responseToChange();
         }

         @Override
         public void afterTextChanged(Editable s) {

             responseToChange();
         }

         private void responseToChange() {
             if(isAdminAddingUser()) {
                 userNameTxt.setText(firstNameTxt.getText().toString().trim() + "." + lastNameTxt.getText().toString().trim());
             }

             clearButton.setEnabled(true);
             saveButton.setEnabled(true);
         }
    }


    private  class PasswordTextChangeWatcher implements  TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            responseToChanges();
        }

        @Override
        public void afterTextChanged(Editable s) {
            responseToChanges();
        }

        private void responseToChanges() {

            String passwordContents = passwordTxt.getText().toString().trim();
            String confirmPasswordContents = confirmPasswordTxt.getText().toString().trim();
            if(!passwordContents.equals(confirmPasswordContents)) {
                confirmPasswordTxt.setError("should be the same as password field!");
                passwordhasChanged = false;
                saveButton.setEnabled(false);
            } else {
                passwordhasChanged = true;
                saveButton.setEnabled(true);
            }
        }
    }


    private class EditMyProfileCheckboxListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                userNameTxt.setEnabled(true);
                changePasswordCheckbox.setVisibility(View.VISIBLE);
                renderViewsWithUserInfo();
                clearButton.setText(getResources().getString(R.string.add_user_cancel_button_title));

            }
            else {
                userNameTxt.setEnabled(false);
                changePasswordCheckbox.setVisibility(View.GONE);
                passwordLabel.setVisibility(View.GONE);
                passwordTxt.setVisibility(View.GONE);
                confirmPasswordLabel.setVisibility(View.GONE);
                confirmPasswordTxt.setVisibility(View.GONE);
                clearButton.setText(getResources().getString(R.string.add_user_clear_button_title));
                clearForm();
            }
        }
    }

    private class ChangePasswordCheckboxListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                passwordLabel.setVisibility(View.VISIBLE);
                passwordTxt.setVisibility(View.VISIBLE);
                confirmPasswordLabel.setVisibility(View.VISIBLE);
                confirmPasswordTxt.setVisibility(View.VISIBLE);
            } else {
                passwordLabel.setVisibility(View.GONE);
                passwordTxt.setVisibility(View.GONE);
                passwordTxt.setText("");
                confirmPasswordLabel.setVisibility(View.GONE);
                confirmPasswordTxt.setVisibility(View.GONE);
                confirmPasswordTxt.setText("");
                passwordhasChanged = false;

            }
        }
    }




}
