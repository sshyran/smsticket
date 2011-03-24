/**
 * 
 */
package sk.michalko.smsticket;

import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;

/**
 * @author mmm
 * 
 *         This class binds ticket state to appropriate icon resource
 */
public class IconViewBinder implements ViewBinder {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.SimpleCursorAdapter.ViewBinder#setViewValue(android.view
	 * .View, android.database.Cursor, int)
	 */
	
	static final String TAG = ViewBinder.class.getSimpleName();

	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

		int viewId = view.getId();
		if (viewId == R.id.item_image) {

			ImageView iconState = (ImageView) view;

			String stateString = cursor.getString(columnIndex);

			try {
				TicketState state = TicketState.valueOf(stateString);

				switch (state) {
				case TICKET_ORDER_CREATED:
					iconState.setImageResource(R.drawable.ticket_created);
					break;
				case TICKET_ORDER_IN_PROGRESS:
					iconState.setImageResource(R.drawable.ticket_in_progress);
					break;
				case TICKET_ORDER_CONFIRMED:
					iconState.setImageResource(R.drawable.ticket_ordered);
					break;
				case TICKET_VALID:
					iconState.setImageResource(R.drawable.ticket_valid);
					break;
				case TICKET_EXPIRED:
					iconState.setImageResource(R.drawable.ticket_expired);
					break;
				default:
					iconState.setImageResource(R.drawable.stub);
					break;
				}
			} catch (Exception ex) {
				Log.e(TAG, "Ticket state " + stateString + " exception: " + ex);
				//iconState.setImageResource(R.drawable.stub);
			}
			return true;
		}
		if (viewId == R.id.item_text) {

			TextView textState = (TextView) view;

			String stateString = cursor.getString(2);
			
			try {
				TicketState state = TicketState.valueOf(stateString);
				String text;
				
				switch (state) {
				case TICKET_ORDER_CREATED:
					text = "Created on: ";
					text = text + cursor.getString(1);
					textState.setText(text);
					break;
				case TICKET_ORDER_IN_PROGRESS:
					text = "Sms sent on: ";
					text = text + cursor.getString(1);
					textState.setText(text);
					break;
				case TICKET_ORDER_CONFIRMED:
					text = "Order confirmed on: ";
					text = text + cursor.getString(1);
					textState.setText(text);
					break;
				case TICKET_VALID:
					text = "Valid until: ";
					text = text + cursor.getString(3);
					textState.setText(text);
					break;
				case TICKET_EXPIRED:
					text = "Expired on: ";
					text = text + cursor.getString(3);
					textState.setText(text);
					break;
				default:
					textState.setText("Ticket in unknown state.");
					break;
				}
			} catch (Exception ex) {
				Log.e(TAG, "Ticket naming exception: " + ex );
				//textState.setText("Ticket does not want to show text.");
			}
			return true;
		}
		return false;
	}

}
