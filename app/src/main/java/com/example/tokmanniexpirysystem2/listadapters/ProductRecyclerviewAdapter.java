package com.example.tokmanniexpirysystem2.listadapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tokmanniexpirysystem2.R;
import com.example.tokmanniexpirysystem2.alarm.AlarmHelper;
import com.example.tokmanniexpirysystem2.database.DatabaseClient;
import com.example.tokmanniexpirysystem2.entities.Product;
import com.example.tokmanniexpirysystem2.utilities.DateConverter;
import com.example.tokmanniexpirysystem2.utilities.HistoryLogger;
import com.example.tokmanniexpirysystem2.utilities.UserAction;


import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProductRecyclerviewAdapter extends RecyclerView.Adapter<ProductRecyclerviewAdapter.ViewHolder> {
    private List<Product> productsList;
    private Context context;
    public ProductRecyclerviewAdapter(Context context, List<Product> dataArgs){
        productsList = dataArgs;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.products_recyclerview_element, parent, false);
        ViewHolder viewHolder= new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.productNameTextView.setText(productsList.get(position).getProductName());
        holder.productExpireTextView.setText(DateConverter.dateToTimestamp(productsList.get(position).getProductExpire()));
        holder.productCodeTextView.setText(productsList.get(position).getProductCode());
        holder.productExtraAlarmEditText.setText("" + productsList.get(position).getProductExtraAlarm());
        holder.productDeleteImageButton.setOnClickListener(holder);
        Calendar expireCalendar = Calendar.getInstance();
        expireCalendar.setTime(productsList.get(position).getProductExpire());
        Calendar nowCalendar = Calendar.getInstance();

        if(expireCalendar.compareTo(nowCalendar) < 0) {
            holder.productSaveImageButton.setEnabled(false);
            holder.productSaveImageButton.setVisibility(View.INVISIBLE);
        }
        holder.productSaveImageButton.setOnClickListener(holder);
       // initializeExtraAlarmSpinner(holder.productExtraAlarmEditText, productsList.get(position));
        if(!productsList.get(position).getProductAlarmDate().after(new Date())){
            holder.view.setBackgroundColor(context.getColor(R.color.recyclerView_product_alarm_color));
        } else {
            if(position%2 == 0) {
                holder.view.setBackgroundColor(context.getColor(R.color.recyclerView_even_color));
            } else {
                holder.view.setBackgroundColor(context.getColor(R.color.recyclerView_odd_color));
            }
        }

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    /**
     * A Simple ViewHolder for the RecyclerView
     */
    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView productNameTextView;
        public TextView productExpireTextView;
        public TextView productCodeTextView;
        public EditText productExtraAlarmEditText;
        public ImageButton productDeleteImageButton;
        public ImageButton productSaveImageButton;
        public View view;


        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            productNameTextView = (TextView) itemView.findViewById(R.id.products_list_element_product_name);
            productExpireTextView = (TextView) itemView.findViewById(R.id.products_list_element_product_expire);
            productCodeTextView = (TextView) itemView.findViewById(R.id.products_list_element_product_code);
            productExtraAlarmEditText = (EditText) itemView.findViewById(R.id.products_list_element_product_extra_alarm);
            productDeleteImageButton = (ImageButton) itemView.findViewById(R.id.products_list_element_delete_btn);
            productSaveImageButton = (ImageButton) itemView.findViewById(R.id.products_list_element_save_btn);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.products_list_element_delete_btn){
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getResources().getString(R.string.products_delete_alert_title));
                builder.setPositiveButton(context.getResources().getString(R.string.products_delete_alert_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int position = getAdapterPosition();
                        deleteProduct(productsList.get(position));
                        productsList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, productsList.size());
                    }
                });
                builder.setNegativeButton(context.getResources().getString(R.string.products_delete_alert_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog ad = builder.create();
                ad.show();
            }

            if(v.getId() == R.id.products_list_element_save_btn) {
                int position = getAdapterPosition();
                Calendar expireCalendar = Calendar.getInstance();
                expireCalendar.setTime(productsList.get(position).getProductExpire());
                expireCalendar.set(Calendar.HOUR_OF_DAY, 8);
                expireCalendar.set(Calendar.MINUTE, 0);
                expireCalendar.set(Calendar.SECOND, 0);
                Calendar nowCalendar = Calendar.getInstance();
                nowCalendar.set(Calendar.HOUR_OF_DAY, 8);
                nowCalendar.set(Calendar.MINUTE, 0);
                nowCalendar.set(Calendar.SECOND, 0);

                String extraAlarmString = productExtraAlarmEditText.getText().toString().trim();
                int extraAlarm;
                try {
                    extraAlarm = Integer.parseInt(extraAlarmString);
                } catch (Exception e) {
                    productExtraAlarmEditText.setError(context.getResources().getString(R.string.provalueduct_recycleview_extral_alarm_large_value_error));
                    return;
                }

                long allowdExtraAlarm = (expireCalendar.getTimeInMillis() - nowCalendar.getTimeInMillis())/(1000*60*60*24);
                expireCalendar.add(Calendar.DAY_OF_YEAR, -1 * extraAlarm);
                if(expireCalendar.compareTo(nowCalendar) < 0) {
                    productExtraAlarmEditText.setError(context.getResources().getString(R.string.product_recyclerview_extra_alarm_in_the_past_error) + allowdExtraAlarm + context.getResources().getString(R.string.product_recyclerview_extra_alarm_in_the_past_error_days));
                    return;
                }
                productsList.get(position).setProductExtraAlarm(extraAlarm);
                updateProduct(productsList.get(position));


            }

        }

        private void deleteProduct(final Product product) {
            class DeleteProduct extends AsyncTask<Void, Void, Void> {

                @Override
                protected Void doInBackground(Void... voids) {
                    DatabaseClient.getInstance(context.getApplicationContext()).getAppDatabase()
                            .productDao()
                            .delete(product);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    HistoryLogger.logEvent(context, product, UserAction.DELETE);
                    AlarmHelper.getInstance(context).cancelAlarm();
                    AlarmHelper.getInstance(context).setAlarm();
                    Toast.makeText(context, context.getResources().getString(R.string.products_deleted_toast), Toast.LENGTH_LONG).show();

                }
            }

            DeleteProduct dp = new DeleteProduct();
            dp.execute();

        }
    }
    /*

    private void initializeExtraAlarmSpinner(Spinner spinner, Product product) {
        Date alarmDate = product.getProductAlarmDate();
        Date expireDate = product.getProductExpire();
        Calendar nowCalandar = Calendar.getInstance();
        Calendar alarmCalandar = Calendar.getInstance();
        alarmCalandar.setTime(alarmDate);
        Calendar expireCalandar = Calendar.getInstance();
        expireCalandar.setTime(expireDate);


        List<String> restrictedList = new ArrayList<>();
        spinner.setAdapter(null);

        // Create an ArrayAdapter using the string array and a default spinner layout
        restrictedList = ExtraAlarmStringHelper.getInstance(context).ListOfLiteralExtraAlarm(nowCalandar.getTime(), expireDate);
        int position = 0;
        String extraAlarmString = ExtraAlarmStringHelper.getInstance(context).stringOfDaysExtraAlarms(product.getProductExtraAlarm());
        int extraAlarmIndex = ExtraAlarmStringHelper.getInstance(context).indexOfDaysExtraAlarms(product.getProductExtraAlarm());
        if(extraAlarmIndex >= restrictedList.size()) {
            restrictedList.add(extraAlarmString);
            position = restrictedList.size() - 1;
        }
        else {
            position = extraAlarmIndex;
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, restrictedList );

        spinner.setAdapter(adapter);
        spinner.setSelection(position);



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int extraAlarmDays = ExtraAlarmStringHelper.getInstance(context).daysOfIndex(position);
                    product.setProductExtraAlarm(extraAlarmDays);
                    updateProduct(product);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    */

    private void updateProduct(final Product product) {


        class UpdateProduct extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(context.getApplicationContext()).getAppDatabase()
                        .productDao()
                        .update(product);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                HistoryLogger.logEvent(context, product, UserAction.UPDATE);
                AlarmHelper.getInstance(context).cancelAlarm();
                AlarmHelper.getInstance(context).setAlarm();

            }
        }

        UpdateProduct up = new UpdateProduct();
        up.execute();

    }

}

