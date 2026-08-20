package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnalogClockWidgetReceiver : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAnalogClockWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAnalogClockWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val views = RemoteViews(context.packageName, R.layout.widget_analog_clock)
            views.setOnClickPendingIntent(R.id.widget_analog_root, pendingIntent)

            val dateFormatted = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date())
            views.setTextViewText(R.id.widget_analog_date, dateFormatted)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
