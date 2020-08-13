package com.example.tokmanniexpirysystem2.utilities;

import android.content.Context;

import com.example.tokmanniexpirysystem2.R;

public class LocalizedRoleConverter {
    private static LocalizedRoleConverter mInstance;
    private Context mContext;
   public static LocalizedRoleConverter getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new LocalizedRoleConverter(context);
        }
        return mInstance;
    }

  private LocalizedRoleConverter(Context context) {
        mContext = context;
    }

   public String localizedStringFromRole(Role value) {
        String string = "";
        String[] stringArray = mContext.getResources().getStringArray(R.array.roles);
        switch (value) {
            case ADMIN:
                string = stringArray[0];
                break;
            case USER:
                string = stringArray[1];
                break;
        }
        return string;
    }

    public String[] localizedStringArrayOfRoles() {

        String[] stringArray = mContext.getResources().getStringArray(R.array.roles);

        return stringArray;
    }
}
