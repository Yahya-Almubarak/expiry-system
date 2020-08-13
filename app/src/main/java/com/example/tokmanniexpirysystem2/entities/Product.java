package com.example.tokmanniexpirysystem2.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.tokmanniexpirysystem2.utilities.DateConverter;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

@Entity
public class Product {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "product_name")
    private String productName;

    @ColumnInfo(name = "product_code")
    private String productCode;

    @ColumnInfo(name = "product_expire")
    @TypeConverters({DateConverter.class})
    private Date productExpire;

    @ColumnInfo(name = "product_extra_alarm")
    private int productExtraAlarm;

    @ColumnInfo(name = "product_is_expired")
    private boolean productExpired;

    @ColumnInfo(name = "product_description")
    private String productDescription;

    @ColumnInfo(name = "product_creation_date")
    @TypeConverters({DateConverter.class})
    private Date productCreationDate;

    @Ignore
    private Date productAlarmDate;

    public static Comparator<Product> compareExpireDate = new Comparator<Product>() {

        public int compare(Product p1, Product p2) {
            if(p1 == null && p2 == null) return 0;
            if(p1 == null ) return -1;
            if(p2 == null) return +1;
            Date expire1 = p1.getProductExpire();
            Date expire2 = p2.getProductExpire();
            if(expire1 == null && expire2 == null) return 0;
            if (expire1 == null) return -1;
            if (expire2 == null) return +1;

            /*For ascending order*/
            return expire1.compareTo(expire2);

        }};

    public static Comparator<Product> compareAlarmDate = new Comparator<Product>() {

        public int compare(Product p1, Product p2) {
            if(p1 == null && p2 == null) return 0;
            if(p1 == null ) return -1;
            if(p2 == null) return +1;
            Date alarm1 = p1.getProductAlarmDate();
            Date alarm2 = p2.getProductAlarmDate();
            if(alarm1 == null && alarm2 == null) return 0;
            if (alarm1 == null) return -1;
            if (alarm2 == null) return +1;

            /*For ascending order*/
            return alarm1.compareTo(alarm2);

        }};

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Date getProductExpire() {
        return productExpire;
    }

    public void setProductExpire(Date productExpire) {
        this.productExpire = productExpire;
    }


    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public Date getProductCreationDate() {
        return productCreationDate;
    }

    public void setProductCreationDate(Date productCreationDate) {
        this.productCreationDate = productCreationDate;
    }

    public boolean isProductExpired() {
        return productExpired;
    }

    public void setProductExpired(boolean productExpired) {
        this.productExpired = productExpired;
    }

    public int getProductExtraAlarm() {
        return productExtraAlarm;
    }

    public void setProductExtraAlarm(int productExtraAlarm) {
        this.productExtraAlarm = productExtraAlarm;
    }

    public Date getProductAlarmDate() {
        Calendar alarmCalendar = Calendar.getInstance();
        alarmCalendar.setTime(getProductExpire());
        alarmCalendar.add(Calendar.DAY_OF_YEAR, - getProductExtraAlarm());
        return alarmCalendar.getTime();
        /*long expireDateMilliSeconds = productExpire.getTime();
        long alarmDateMilliSecond = expireDateMilliSeconds - (long)productExtraAlarm*24*60*60*1000;
        if(alarmDateMilliSecond < new Date().getTime()) {
            alarmDateMilliSecond = expireDateMilliSeconds;
        }
        productAlarmDate = new Date(alarmDateMilliSecond);
        return productAlarmDate;*/

    }
}
