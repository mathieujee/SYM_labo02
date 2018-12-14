package com.example.olivier.sym_labo3.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;

public class PermissionUtils {

    public static void checkPermissions(Activity activity, String... permissions){
        ArrayList<String> permissionsNotGranted = new ArrayList<>();
        int missingPermissions = 0;
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsNotGranted.add(permission);
                    missingPermissions++;
                }
            }
        }
        if(missingPermissions == 0)
            return;
        String[] permissionsToAsk = new String[missingPermissions];
        for(int i = 0; i < missingPermissions; i++){
            permissionsToAsk[i] = permissionsNotGranted.get(i);
        }
        ActivityCompat.requestPermissions(activity, permissionsToAsk, 1);
    }

}
