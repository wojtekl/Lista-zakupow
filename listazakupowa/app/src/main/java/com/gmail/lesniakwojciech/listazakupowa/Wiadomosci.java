package com.gmail.lesniakwojciech.listazakupowa;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

public class Wiadomosci {
    public static Intent tekst(final PackageManager packageManager, final String tresc) {
        final Intent intent = new Intent(Intent.ACTION_SENDTO)
                .setData(Uri.parse("smsto:"))
                .putExtra("sms_body", tresc);
        if(Permissions.canStart(intent, packageManager)) {
            return intent;
        }
        return null;
    }

    public static void obraz(final Context context, final Uri uri) {
        final Intent intent = new Intent(Intent.ACTION_SEND)
                .setData(Uri.parse("smsto:"))
                .putExtra(Intent.EXTRA_STREAM, uri)
                .setType("image/jpeg");
        if(Permissions.canStart(intent, context.getPackageManager())) {
            context.startActivity(intent);
        }
    }
}
