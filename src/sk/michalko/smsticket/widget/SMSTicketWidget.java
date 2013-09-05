package sk.michalko.smsticket.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import sk.michalko.smsticket.R;
import sk.michalko.smsticket.TicketDao;
import sk.michalko.smsticket.TicketState;


/**
 * Created by mmichalko on 16.8.2013.
 */
public class SMSTicketWidget extends AppWidgetProvider{

    public static String TICKET_UPDATE = "sk.michalko.smsticket.TICKET_UPDATE";

    @Override
    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);

        if(TICKET_UPDATE.equals(intent.getAction())){
            TicketDao ticket = TicketDao.getByUUID(intent.getDataString(),context);
            ticket.getState();
            Toast.makeText(context, "SMS Ticket widget update", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        TicketDao ticket = TicketDao.getCurrent(context);

        RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);


        if (ticket == null) {
            // Default visual
            updateViews.setTextViewText(R.id.widget_text, "You have no tickets");
            updateViews.setImageViewResource(R.id.widget_image,R.drawable.icon);
            return;
        }

        String stateString = ticket.getState();
        TicketState state = TicketState.valueOf(stateString);


        updateViews.setTextViewText(R.id.widget_text, stateString);

        switch (state) {
            case TICKET_ORDER_CREATED:
                updateViews.setImageViewResource(R.id.widget_image,R.drawable.ticket_created);
                break;
            case TICKET_ORDER_IN_PROGRESS:
                updateViews.setImageViewResource(R.id.widget_image,R.drawable.ticket_in_progress);
                break;
            case TICKET_ORDER_CONFIRMED:
                updateViews.setImageViewResource(R.id.widget_image,R.drawable.ticket_ordered);
                break;
            case TICKET_VALID:
                updateViews.setImageViewResource(R.id.widget_image,R.drawable.ticket_valid);
                break;
            case TICKET_EXPIRED:
                updateViews.setImageViewResource(R.id.widget_image,R.drawable.ticket_expired);
                break;
            default:
                updateViews.setImageViewResource(R.id.widget_image,R.drawable.stub);
                break;
        }

        appWidgetManager.updateAppWidget(appWidgetIds, updateViews);

    }
}
