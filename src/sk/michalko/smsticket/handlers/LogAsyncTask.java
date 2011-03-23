package sk.michalko.smsticket.handlers;

import android.os.AsyncTask;
import android.util.Log;

public class LogAsyncTask extends AsyncTask<String , Void, Void> {

	static final String TAG = LogAsyncTask.class.getSimpleName();

	protected Void doInBackground(String... message) {
        Log.d(TAG,message[0]);
		return null;
    }
}
