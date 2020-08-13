package com.example.tokmanniexpirysystem2.fragments;



import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tokmanniexpirysystem2.MainActivity;
import com.example.tokmanniexpirysystem2.R;
import com.example.tokmanniexpirysystem2.alarm.AlarmHelper;
import com.example.tokmanniexpirysystem2.database.DatabaseClient;
import com.example.tokmanniexpirysystem2.entities.Product;
import com.example.tokmanniexpirysystem2.entities.User;
import com.example.tokmanniexpirysystem2.utilities.ExtraAlarmStringHelper;
import com.example.tokmanniexpirysystem2.utilities.HistoryLogger;
import com.example.tokmanniexpirysystem2.utilities.UserAction;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import androidx.fragment.app.Fragment;

import com.example.tokmanniexpirysystem2.utilities.DateConverter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class AddProductFragment extends Fragment implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener, View.OnFocusChangeListener, AdapterView.OnItemSelectedListener {

    private User user;

    private Button newButton;
    private Button saveButton;

    private ImageButton setDateImgButton;

    private EditText productNameTxt;
    private EditText productCodeTxt;
    private EditText productExpireDateTxt;
    private EditText productDescriptionTxt;

    private Spinner productExtraAlarmSpinner;



    // Form parameters
    String productName;
    String productCode;
    Date productExpireDate;
    Date productCreationDate;
    String productDescription;
    int productExtraAlarmInDays;

    public AddProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        user = (User) ((MainActivity)getActivity()).getUser();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);
        initializeViews(view);
        return view;
    }
 /*
        <item>1 One Day</item>
        <item>3 Three Days</item>
        <item>10 Ten Days</item>
        <item>15 Fifteen Days</item>
        <item>20 Twenty Days</item>
        <item>30 One Month</item>
        <item>45 One Month and half</item>
        <item>60 Two Months</item>
        <item>90 Three Months</item>
        <item>None</item>
        */

    public void initializeExtraAlarmSpinner() {
        String dateString = productExpireDateTxt.getText().toString();
        if(dateString != null && !(dateString.isEmpty())) {
            Date expireDate =  DateConverter.fromTimestamp(productExpireDateTxt.getText().toString().trim());
            Date now = new Date();

            productExtraAlarmSpinner.setAdapter(null);

            List<String> list = ExtraAlarmStringHelper.getInstance(getContext()).ListOfLiteralExtraAlarm(now, expireDate);
            // Create an ArrayAdapter using the string array and a default spinner layout
            //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.extra_alarm, android.R.layout.simple_spinner_item);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, list);

            // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            productExtraAlarmSpinner.setAdapter(adapter);
            if(list.size() > 1) {
                productExtraAlarmSpinner.setSelection(1);
            }

            productExtraAlarmSpinner.setOnItemSelectedListener(this);
        }
    }

    private void initializeViews(View view) {

        newButton = view.findViewById(R.id.product_add_clear_btn);
        newButton.setOnClickListener(this);

        saveButton = view.findViewById(R.id.product_add_save_btn);
        saveButton.setOnClickListener(this);

        setDateImgButton = view.findViewById(R.id.product_add_set_date_img_btn);
        setDateImgButton.setOnClickListener(this);

        productNameTxt = view.findViewById(R.id.product_add_product_name_txt);
        productCodeTxt = view.findViewById(R.id.product_add_product_code__txt);
        productExpireDateTxt = view.findViewById(R.id.product_add_expire_date_txt);
        productExpireDateTxt.setOnFocusChangeListener(this);
        productDescriptionTxt = view.findViewById(R.id.product_add_description_txt);

        productExtraAlarmSpinner = (Spinner) view.findViewById(R.id.product_add_extra_alarm_spinner);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.product_add_clear_btn:
                clearForm();
                break;
            case R.id.product_add_save_btn:
                saveProduct();
                break;
            case R.id.product_add_set_date_img_btn:
                setDate();
                break;
        }

    }

    private void clearForm() {
        productNameTxt.setText("");
        productCodeTxt.setText("");
        productExpireDateTxt.setText("");
        productDescriptionTxt.setText("");
        productExtraAlarmSpinner.setAdapter(null);
    }

    private void setFormValues() {
        productName = productNameTxt.getText().toString().trim();
        productCode = productCodeTxt.getText().toString().trim();
        productExpireDate = DateConverter.fromTimestamp(productExpireDateTxt.getText().toString().trim());
        productCreationDate = Calendar.getInstance().getTime();
        productDescription = productDescriptionTxt.getText().toString().trim();
    }

    private void saveProduct() {
        setFormValues();
        if (productName.isEmpty()) {
            productNameTxt.setError(getResources().getString(R.string.productedit_name_empty_error));
            productNameTxt.requestFocus();
            return;
        }

        if (productExpireDate == null) {
            productExpireDateTxt.setError(getResources().getString(R.string.productedit_date_null_error));
            productExpireDateTxt.requestFocus();
            return;
        }

        if (productExpireDate.before(new Date())) {
            productExpireDateTxt.setError(getResources().getString(R.string.productedit_date_old_error));
            productExpireDateTxt.requestFocus();
            return;
        }


        class SaveProduct extends AsyncTask<Void, Void, Product> {

            @Override
            protected Product doInBackground(Void... voids) {
                //creating a product
                Product product = new Product();
                product.setProductName(productName);
                product.setProductCode(productCode);
                product.setProductExpire(productExpireDate);
                product.setProductCreationDate(productCreationDate);
                product.setProductDescription(productDescription);
                product.setProductExtraAlarm(productExtraAlarmInDays);

                //adding to database
                DatabaseClient.getInstance(getActivity().getApplicationContext()).getAppDatabase()
                        .productDao()
                        .insert(product);
                return product;
            }

            @Override
            protected void onPostExecute(Product product) {
                super.onPostExecute(product);
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.productedit_saved_toast), Toast.LENGTH_SHORT).show();
                AlarmHelper.getInstance(getContext()).cancelAlarm();
                AlarmHelper.getInstance(getContext()).setAlarm();
                HistoryLogger.logEvent(getActivity(),product, UserAction.CREAT);
            }
        }

        SaveProduct sp = new SaveProduct();
        sp.execute();
    }




    private void setDate() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );
// If you're calling this from a support Fragment
        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
// If you're calling this from an AppCompatActivity
// dpd.show(getSupportFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String day = dayOfMonth < 10? "0" + dayOfMonth: "" + dayOfMonth;
        String month = monthOfYear < 10? "0" + (monthOfYear +1): "" + (monthOfYear+1) ;
        productExpireDateTxt.setError(null);
        productExpireDateTxt.setText(String.format("%s.%s.%d", day, month, year));
        initializeExtraAlarmSpinner();

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(v == productExpireDateTxt)
        initializeExtraAlarmSpinner();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        productExtraAlarmInDays = ExtraAlarmStringHelper.getInstance(getContext()).daysOfIndex(position);
        /*
        String extraAlarmString = parent.getAdapter().getItem(position).toString();
        String[] stringArray = getResources().getStringArray(R.array.extra_alarm);

        <item>1 One Day</item>
        <item>3 Three Days</item>
        <item>10 Ten Days</item>
        <item>15 Fifteen Days</item>
        <item>20 Twenty Days</item>
        <item>30 One Month</item>
        <item>45 One Month and half</item>
        <item>60 Two Months</item>
        <item>90 Three Months</item>
        <item>None</item>

        if(extraAlarmString.equals(stringArray[0])){
            productExtraAlarmInDays = 1;
        } else if(extraAlarmString.equals(stringArray[1])){
            productExtraAlarmInDays = 3;
        } else if (extraAlarmString.equals(stringArray[2])){
            productExtraAlarmInDays = 10;
        } else if(extraAlarmString.equals(stringArray[3])){
            productExtraAlarmInDays = 15;
        } else if(extraAlarmString.equals(stringArray[4])){
            productExtraAlarmInDays = 20;
        } else if(extraAlarmString.equals(stringArray[5])){
            productExtraAlarmInDays = 30;
        } else if(extraAlarmString.equals(stringArray[6])){
            productExtraAlarmInDays = 45;
        } else if(extraAlarmString.equals(stringArray[7])){
            productExtraAlarmInDays = 60;
        } else if(extraAlarmString.equals(stringArray[8])){
            productExtraAlarmInDays = 90;
        } else if(extraAlarmString.equals(stringArray[9])){
            productExtraAlarmInDays = 0;
        } else {
            productExtraAlarmInDays = 1;
        }*/
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        productExtraAlarmInDays = 0;
    }
}
