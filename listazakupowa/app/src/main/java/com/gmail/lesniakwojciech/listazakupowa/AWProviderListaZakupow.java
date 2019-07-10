package com.gmail.lesniakwojciech.listazakupowa;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AWProviderListaZakupow extends AppWidgetProvider {
    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager,
                         final int[] appWidgetIds) {
        final List<ModelProdukt> doKupienia = new ArrayList<>();
        try {
            final JSONArray jsonArray = new JSONArray(new Ustawienia(context)
                    .getListy("[[],[],[]]")).getJSONArray(1);
            for (int i = 0, d = jsonArray.length(); d > i; ++i) {
                doKupienia.add(ModelProdukt.fromJSON(jsonArray.getString(i)));
            }
        }
        catch (final JSONException exception) {
            Toast.makeText(context, exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
        String string = "- - -";
        int l = doKupienia.size();
        if (0 < l) {
            final StringBuilder stringBuilder = new StringBuilder();
            --l;
            for (int i = 0; l > i; ++i) {
                stringBuilder.append(" * ").append(doKupienia.get(i).getNazwa()).append(",\n");
            }
            stringBuilder.append(" * ").append(doKupienia.get(l).getNazwa()).append(".");
            string = stringBuilder.toString();
        }

        for (int i = 0, d = appWidgetIds.length; d > i; ++i) {
            final RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.awproviderlistazakupow);
            views.setTextViewText(R.id.awplzTextView, string);
            views.setOnClickPendingIntent(R.id.awplzTextView, PendingIntent.getActivity(
                    context,
                    0,
                    new Intent(context, ActivityMain.class),
                    0
                    )
            );
            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
        }
    }

    public static void update(final Context context) {
        context.sendBroadcast(new Intent(context, AWProviderListaZakupow.class)
                .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, AppWidgetManager
                        .getInstance(context)
                        .getAppWidgetIds(new ComponentName(context, AWProviderListaZakupow.class))
                )
        );
    }

    /* static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.awproviderlistazakupow);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    } */
}
