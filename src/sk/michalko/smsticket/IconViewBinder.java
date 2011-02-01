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
					iconState.setImageResource(R.drawable.ticket_valid);
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

			String stateString = cursor.getString(columnIndex);

			try {
				TicketState state = TicketState.valueOf(stateString);

				switch (state) {
				case TICKET_ORDER_CREATED:
					textState.setText("Ticket was created on:");
					break;
				case TICKET_ORDER_IN_PROGRESS:
					textState.setText("Ticket ordered.");
					break;
				case TICKET_ORDER_CONFIRMED:
					textState.setText("Ticket order confirmed.");
					break;
				case TICKET_VALID:
					textState.setText("This ticket is valid until:");
					break;
				case TICKET_EXPIRED:
					textState.setText("Ticket expired on:");
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
