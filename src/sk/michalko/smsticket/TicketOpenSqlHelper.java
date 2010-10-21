package sk.michalko.smsticket;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TicketOpenSqlHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "sk.michalko.smsticket.db";
	private static final int DATABASE_VERSION = 3;
	private static final String SMSTICKET_TABLE_NAME = "tickets";
	private static final String SMSTICKET_TABLE_CREATE = "CREATE TABLE "
			+ SMSTICKET_TABLE_NAME
			+ " ('_id' integer primary key, 'uuid' unique ,'created', 'changed', 'state','validFrom','validThrough','ticketId','smsBody');";

	static TicketOpenSqlHelper instance = null;

	public static TicketOpenSqlHelper getInstance(Context context) {
		if (instance == null) {
			instance = new TicketOpenSqlHelper(context);
		}
		return instance;
	}

	TicketOpenSqlHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SMSTICKET_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < newVersion) {
			db.execSQL("DROP TABLE " + SMSTICKET_TABLE_NAME + ";");
			db.execSQL(SMSTICKET_TABLE_CREATE);
		}
	}

}
