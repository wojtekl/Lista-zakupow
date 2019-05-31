package com.gmail.lesniakwojciech.listazakupowa;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

public class Permissions {
    public static String getPermissionDescription(final PackageManager packageManager, final String permissionGroup) {
        String uprawnienie = permissionGroup;
        try {
            uprawnienie = packageManager
                    .getPermissionGroupInfo(permissionGroup, 0)
                    .loadDescription(packageManager)
                    .toString();
        }
        catch(final Exception exception) {}
        return uprawnienie;
    }

    public static void requestPermission(final Activity activity, final String permission, final String message) {
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            new AlertDialog
                    .Builder(activity)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.uprawnienia)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.powroc, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            ActivityCompat.requestPermissions(activity, new String[]{permission}, 0);
                        }
                    })
                    .show();
        }
        else {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, 0);
        }
    }
}