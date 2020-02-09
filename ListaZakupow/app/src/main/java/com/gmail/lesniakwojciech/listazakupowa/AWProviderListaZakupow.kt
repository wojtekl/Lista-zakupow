package com.gmail.lesniakwojciech.listazakupowa

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.widget.RemoteViews

class AWProviderListaZakupow : AppWidgetProvider() {
    companion object {
        fun update(context: Context) {
            context.sendBroadcast(
                Intent(context, AWProviderListaZakupow::class.java)
                    .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                    .putExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_IDS, AppWidgetManager
                            .getInstance(context)
                            .getAppWidgetIds(
                                ComponentName(
                                    context,
                                    AWProviderListaZakupow::class.java
                                )
                            )
                    )
            )
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val repository = RepositoryListaZakupow(context)
        repository.get()
        var string = repository.doWyslania("")
        if (TextUtils.isEmpty(string)) string = "- - -"
        for (id in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.awproviderlistazakupow)
            views.setTextViewText(R.id.awplzTextView, string)
            views.setOnClickPendingIntent(
                R.id.awplzTextView, PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, ActivityMain::class.java),
                    0
                )
            )
            appWidgetManager.updateAppWidget(id, views)
        }
    }
}
