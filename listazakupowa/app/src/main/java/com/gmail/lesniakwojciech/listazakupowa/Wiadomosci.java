package com.gmail.lesniakwojciech.listazakupowa;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

public class Wiadomosci {
    public static void tekst(final Context context, final String tresc) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(Intent.ACTION_SENDTO)
                .setData(Uri.parse("smsto:"))
                .putExtra("sms_body", tresc);
        final ActivityInfo activityInfo = intent.resolveActivityInfo(packageManager,
                intent.getFlags());
        if(null != activityInfo && activityInfo.exported) {
            context.startActivity(intent);
        }
    }

    public static boolean obraz(final Context context, final Uri uri) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(Intent.ACTION_SEND)
                .setData(Uri.parse("smsto:"))
                .putExtra(Intent.EXTRA_STREAM, uri)
                .setType("image/jpeg");
        final ActivityInfo activityInfo = intent.resolveActivityInfo(packageManager,
                intent.getFlags());
        if(null != activityInfo && activityInfo.exported) {
            context.startActivity(intent);
            return true;
        }
        return false;
    }
}
