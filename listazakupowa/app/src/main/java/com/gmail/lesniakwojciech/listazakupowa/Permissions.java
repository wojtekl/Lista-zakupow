package com.gmail.lesniakwojciech.listazakupowa;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

public class Permissions {
    public static String getPermissionDescription(final PackageManager packageManager,
                                                  final String permissionGroup) {
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

    public static void requestPermission(final Activity activity, final String permission,
                                         final String message) {
        if(PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(activity,
                permission)) {
            return;
        }
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

    public static boolean hasInternet(final Context context, final View view) {
        final NetworkInfo networkInfo = ((ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        final boolean isConnected = null != networkInfo && networkInfo.isConnected();
        if (!isConnected) {
            Snackbar.make(view, R.string.brakPolaczeniaZInternetem, Snackbar.LENGTH_LONG).show();
        }
        return isConnected;
    }

    public static boolean canStart(final Intent intent, final PackageManager packageManager) {
        final ActivityInfo activityInfo = intent.resolveActivityInfo(packageManager, intent.getFlags());
        return null != activityInfo && activityInfo.exported;
    }
}
