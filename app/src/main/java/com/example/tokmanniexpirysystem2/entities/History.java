package com.example.tokmanniexpirysystem2.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;
import androidx.room.TypeConverters;

import com.example.tokmanniexpirysystem2.utilities.TimeStampConverter;
import com.example.tokmanniexpirysystem2.utilities.UserAction;
import com.example.tokmanniexpirysystem2.utilities.UserActionConverter;

import java.util.Date;
import java.util.List;

@Entity
public class History {

    @PrimaryKey(autoGenerate = true)
    private int id;


    @ColumnInfo(name = "product_id")
    private int  productId;

   @ColumnInfo(name = "modifier_id")
    private int modifierId;

    @ColumnInfo(name = "modificationTime")
    @TypeConverters({TimeStampConverter.class})
    private Date modificationDate;

    private  String summary;

    @ColumnInfo(name = "user_action")
    @TypeConverters({UserActionConverter.class})
    private UserAction userAction;


    public History() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getModifierId() {
        return modifierId;
    }

    public void setModifierId(int modifierId) {
        this.modifierId = modifierId;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public UserAction getUserAction() {
        return userAction;
    }

    public void setUserAction(UserAction userAction) {
        this.userAction = userAction;
    }
}
