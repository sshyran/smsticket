package sk.michalko.smsticket.handlers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import sk.michalko.smsticket.R;
import sk.michalko.smsticket.TicketDao;
import sk.michalko.smsticket.TicketState;
import sk.michalko.smsticket.widget.SMSTicketWidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * @author mmm
 * 
 * @description This class is base class to all notification callbacks used in
 *              the application. It purpose is to define common functionality
 *              for process of dealing with state change notifications.
 * 
 */
public class SMSReceiver extends BroadcastReceiver {

	static final String TAG = SMSReceiver.class.getSimpleName();

    static final String INTENT_SMS_SENT = "sk.michalko.smsticket.SMS_SENT";
    static final String INTENT_SMS_DELIVERED = "sk.michalko.smsticket.SMS_DELIVERED";
    static final String INTENT_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    static final String INTENT_TICKET_EXPIRED = "sk.michalko.smsticket.TICKET_EXPIRED";

	Context ctx = null;

	@Override
	public void onReceive(Context context, Intent intent) {

		ctx = context;



		// what are we notified about ?
		String action = intent.getAction();
		String ticketUuid = intent.getDataString();

		Log.d(TAG, "Received notification " + action + ", " + ticketUuid);

//		Toast.makeText(context, action, Toast.LENGTH_LONG).show();

		TicketDao ticket = null;

		if (INTENT_SMS_SENT.equalsIgnoreCase(action)) {

			ticket = TicketDao.getByUUID(ticketUuid, ctx);
			changeState(TicketState.TICKET_ORDER_CREATED, TicketState.TICKET_ORDER_IN_PROGRESS, ticket);
			ticket.update(ctx);
			Toast.makeText(context, ctx.getResources().getString(R.string.intent_sms_sent_toast), Toast.LENGTH_LONG).show();

		} else if (INTENT_SMS_DELIVERED.equalsIgnoreCase(action)) {

			ticket = TicketDao.getByUUID(ticketUuid, ctx);
			changeState(TicketState.TICKET_ORDER_IN_PROGRESS, TicketState.TICKET_ORDER_CONFIRMED, ticket);
			ticket.update(ctx);
			Toast.makeText(context, ctx.getResources().getString(R.string.intent_sms_delivered_toast), Toast.LENGTH_LONG).show();

		} else if (INTENT_SMS_RECEIVED.equalsIgnoreCase(action)) {

			// read received messages from intent object
			Bundle bundle = intent.getExtras();
			SmsMessage[] messages = null;
			String text = "";
			if (bundle != null) {
				Object[] pdus = (Object[]) bundle.get("pdus");
				messages = new SmsMessage[pdus.length];
				int j = 0;

				for (int i = 0; i < messages.length; i++) {
					messages[j] = SmsMessage.createFromPdu((byte[]) pdus[i]);
					text = messages[j].getMessageBody();
					// Detect SMS Ticket message
					if (text != null && TicketDao.isTicketSms(text)) {
						j++;
						Log.d(TAG, "Found Ticket SMS: \n" + text);
					}
				}

				// Lets assume we have only one ticket here
				if (j == 1) {
					// Get last created ticket, but not validated
					ticket = TicketDao.getCurrent(ctx);
					//ticket.setState(TicketState.TICKET_VALID.toString());
					//ticket.setChanged(new Date());
					ticket.setSmsBody(messages[0].getMessageBody());
					ticket.expandBody();

                    Toast.makeText(context, ctx.getResources().getString(R.string.intent_sms_received_toast), Toast.LENGTH_LONG).show();
					changeState(TicketState.TICKET_ORDER_CONFIRMED, TicketState.TICKET_VALID, ticket);
                    ticket.update(ctx);

                    // Setup expire notification
                    //Intent updateIntent = new Intent(INTENT_TICKET_EXPIRED);
                    Uri uriTicketId = Uri.parse(ticket.getUuid());
                    Intent updateIntent = new Intent(INTENT_TICKET_EXPIRED, uriTicketId, ctx, SMSReceiver.class);
                    //updateIntent.setData(uriTicketId);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, updateIntent, 0);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(ticket.getValidThrough());
                    Log.d(TAG,TicketDao.dateFormatSms.format(calendar.getTime()));

                    AlarmManager alarmManager = (AlarmManager)ctx.getSystemService(ctx.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC,calendar.getTimeInMillis(),pendingIntent);

                    // Log message
					//StringBuffer message = new StringBuffer();
					//message.append("Found Ticket SMS: \n");
					//message.append(messages[0].getMessageBody());

					//new LogAsyncTask().execute(message.toString());

				}
			}
		}else if (INTENT_TICKET_EXPIRED.equalsIgnoreCase(action)){
            ticket = TicketDao.getByUUID(ticketUuid, ctx);
            changeState(TicketState.TICKET_VALID, TicketState.TICKET_EXPIRED, ticket);
            ticket.update(ctx);
            Toast.makeText(context, ctx.getResources().getString(R.string.intent_ticket_expired_toast), Toast.LENGTH_LONG).show();

            // Log message
            StringBuffer message = new StringBuffer();
            message.append("sk.michalko.smsticket: SMS Ticket expired: ");
            message.append(ticketUuid);
            

            new LogAsyncTask().execute(message.toString());

        }
	}

	public void changeState(TicketState currentState, TicketState targetState, TicketDao ticket) {

		if (TicketState.valueOf(ticket.getState()).compareTo(targetState) >= 0) {
			Log.e(TAG,"Ticket already " + ticket.getState() + " notification " + targetState + " is too late.");			
			return;
		} 
		
		ticket.setState(targetState.toString());

        Uri uriTicketUuid = Uri.parse(ticket.getUuid());

		Intent intentUpdate = new Intent(ctx.getResources().getString(R.string.intent_ticket_update),uriTicketUuid);

		ctx.sendBroadcast(intentUpdate);

	}

}
