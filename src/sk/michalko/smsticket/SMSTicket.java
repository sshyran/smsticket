package sk.michalko.smsticket;

import sk.michalko.smsticket.handlers.SMSReceiver;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SMSTicket extends Activity {

	static final String TAG = SMSTicket.class.getSimpleName();
	static boolean isWaitingResponse = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button btnBuyTicket = (Button) findViewById(R.id.ButtonBuyTicket);
        
        btnBuyTicket.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) 
            {   
            	// See if we have received all ordered tickets
            	TicketDao ticket = TicketDao.getCurrent(v.getContext());
            	
            	//if (ticket == null || ticket.getState() == TicketState.TICKET_EXPIRED.toString()){
            		sendSMS();                
            		Toast.makeText(getBaseContext(), "Empty SMS Sent to number 1100", Toast.LENGTH_SHORT).show();
            	//} 
            }
        });
    }
    
 	public void sendSMS(){
    	
    	Context context = this.getBaseContext();
    	
	    TicketDao ticket = TicketDao.create(context);
	    
	    ticket.setState(TicketState.TICKET_ORDER_CREATED.toString());
	    ticket.save(context);
	    
	    Uri uriTicketId = Uri.parse(ticket.getUuid());
	    
    	Intent intentSent = new Intent(this,SMSReceiver.class);
    	intentSent.setAction(getResources().getString(R.string.intent_sms_sent));
     	intentSent.setData(uriTicketId);
    	PendingIntent intentSMSSent = PendingIntent.getBroadcast(context, 0, intentSent, 0);
     	
    	Intent intentDelivered = new Intent(this,SMSReceiver.class);
    	intentDelivered.setAction(getResources().getString(R.string.intent_sms_delivered));
     	intentDelivered.setData(uriTicketId);
       	PendingIntent intentSMSDelivered = PendingIntent.getBroadcast(context, 0, intentDelivered, 0);
            	
    	SmsManager smsManager = SmsManager.getDefault();
    	smsManager.sendTextMessage("00421905547580", null, "SMSTicket", intentSMSSent, intentSMSDelivered);
    	
    	Log.d(TAG, "SMS Ticket message sent. " + ticket.getUuid());
  	
    }
}