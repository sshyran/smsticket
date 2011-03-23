package sk.michalko.smsticket;

import sk.michalko.smsticket.handlers.SMSReceiver;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class SMSTicket extends ListActivity {

	static final String TAG = SMSTicket.class.getSimpleName();
	
	static boolean isWaitingResponse = false;
	static String[] PROJECTION = new String[] { "state", "validThrough" };
	
	Cursor cursor = null;
	SimpleCursorAdapter adapter = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		TicketOpenSqlHelper sqlHelper = TicketOpenSqlHelper.getInstance(this);
		SQLiteDatabase db = sqlHelper.getWritableDatabase();

		cursor = db.query("tickets", new String[] { "_id", "state",	"validThrough" }, null, null, null, null, "created ASC", "6");

		adapter = new SimpleCursorAdapter(this,	R.layout.item, cursor, PROJECTION, new int[] { R.id.item_image,	R.id.item_text });
		adapter.setViewBinder(new IconViewBinder());
		setListAdapter(adapter);

		// Register refresh gui event receiver
		registerReceiver(refresh, new IntentFilter(getResources().getString(R.string.intent_update)));

		Button btnBuyTicket = (Button) findViewById(R.id.ButtonBuyTicket);
		btnBuyTicket.setOnClickListener(buttonListener);
		
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(refresh);
		setListAdapter(null);
		cursor.close();
		super.onDestroy();
	}


	public BroadcastReceiver refresh = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			cursor.requery();
			adapter.notifyDataSetChanged();
			Log.d(TAG, "Notification: Tickets changed.");
		}
	};

	public View.OnClickListener buttonListener = new View.OnClickListener() {

		public void onClick(View v) {
			// See if we have received all ordered tickets
			// TicketDao ticket = TicketDao.getCurrent(v.getContext());

			// if (ticket == null || ticket.getState() == TicketState.TICKET_EXPIRED.toString()){
			sendSMS();
			Toast.makeText(getBaseContext(), "Sending ticket request.",	Toast.LENGTH_SHORT).show();
			// }
		}
	};

	public void sendSMS() {

		Context context = this.getBaseContext();

		TicketDao ticket = TicketDao.create(context);

		ticket.setState(TicketState.TICKET_ORDER_CREATED.toString());
		ticket.save(context);

		Uri uriTicketId = Uri.parse(ticket.getUuid());

		Intent intentSent = new Intent(this, SMSReceiver.class);
		intentSent.setAction(getResources().getString(R.string.intent_sms_sent));
		intentSent.setData(uriTicketId);
		PendingIntent intentSMSSent = PendingIntent.getBroadcast(context, 0, intentSent, 0);

		Intent intentDelivered = new Intent(this, SMSReceiver.class);
		intentDelivered.setAction(getResources().getString(R.string.intent_sms_delivered));
		intentDelivered.setData(uriTicketId);
		PendingIntent intentSMSDelivered = PendingIntent.getBroadcast(context, 0, intentDelivered, 0);

		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage("5556", null, "DPB .a.s. Prestupny CL 0,80EUR (24.10Sk) 1EUR=30.1260Sk Platnost od 01-02-2011 12:40 do 01:50 hod. gwoea4qg3wt", intentSMSSent, intentSMSDelivered);
		//smsManager.sendTextMessage("1100", null, "", intentSMSSent, intentSMSDelivered);
		//smsManager.sendTextMessage("00421905547580", null, "DPB .a.s. Prestupny CL 0,80EUR (24.10Sk) 1EUR=30.1260Sk Platnost od 01-02-2011 12:40 do 01:50 hod. gwoea4qg3wt", intentSMSSent, intentSMSDelivered);

		Log.d(TAG, "SMS Ticket message sent. " + ticket.getUuid());

	}
}