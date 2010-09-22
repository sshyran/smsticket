package sk.michalko.smsticket.handlers;

import sk.michalko.smsticket.R;
import sk.michalko.smsticket.TicketDao;
import sk.michalko.smsticket.TicketState;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;



/**
 * @author mmm
 * 
 * @description This class is base class to all notification callbacks
 * 				used in the application. It purpose is to define
 * 				common functionality for process of dealing with state 
 * 				change notifications.
 *
 */
public class SMSReceiver extends BroadcastReceiver {

	static final String TAG = SMSReceiver.class.getSimpleName();
	
	Context ctx = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		

		ctx = context;
		
		String INTENT_SENT = ctx.getResources().getString(R.string.intent_sms_sent);
		String INTENT_DELIVERED = ctx.getResources().getString(R.string.intent_sms_delivered);
		
		// what are we notified about ?
		String action = intent.getAction();
		String ticketId = intent.getDataString();
		
		Log.d(TAG,"Received notification " + action + ", " +ticketId );
		
		Toast.makeText(context, action , Toast.LENGTH_LONG).show();
		
		TicketDao ticket = TicketDao.getById(ticketId, ctx);
		
		if (INTENT_SENT.equalsIgnoreCase(action)) {
			
			changeState(TicketState.TICKET_ORDER_CREATED, TicketState.TICKET_ORDER_IN_PROGRESS, ticket);
			
		} else if (INTENT_DELIVERED.equalsIgnoreCase(action)) {
			
			changeState(TicketState.TICKET_ORDER_IN_PROGRESS,TicketState.TICKET_ORDER_CONFIRMED, ticket);
			
		} else if ("android.provider.Telephony.SMS_RECEIVED".equalsIgnoreCase(action)){
			
			//changeState(TicketState.TICKET_ORDER_CONFIRMED,TicketState.TICKET_VALID, ticket);
			
			// read received messages from intent object
			Bundle bundle = intent.getExtras();
			SmsMessage [] messages = null;
			String text = "";
			if (bundle!=null) {		

				Object[] pdus = (Object[]) bundle.get("pdus");
				messages = new SmsMessage[pdus.length];            
				for (int i=0; i<messages.length; i++){
					messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);                
					text += "SMS from " + messages[i].getOriginatingAddress();                     
					text += " :";
					text += messages[i].getMessageBody().toString();
					text += "\n";        
				}
				Toast.makeText(context, text, Toast.LENGTH_LONG).show();
			}
		}

	}
	
	

	public void changeState(TicketState currentState, TicketState nextState, TicketDao ticket){
				
		//if (TicketState.valueOf(ticket.getState()) == currentState) {
			ticket.setState(nextState.toString());
		//} else Log.e(TAG,"Ticket in unexpected state " + ticket.getState() + ", expected " + currentState);
	}

}
