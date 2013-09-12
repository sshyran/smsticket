package sk.michalko.smsticket;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TicketDao {
	
	static final String TAG = TicketDao.class.getSimpleName();

	static final SimpleDateFormat dateFormatDb = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	static final SimpleDateFormat dateFormatSms = new SimpleDateFormat("dd-MM-yyyy HH:mm");
	static final Pattern messagePattern = Pattern.compile(".*Platnost od ([0-9]{2}-[0-9]{2}-[0-9]{4}) ([0-9]{2}:[0-9]{2}) do ([0-9]{2}:[0-9]{2}) hod.*([[0-9][a-z]]{11})");

	String _id;
	String uuid;
	Date created;
	Date changed;
	String state;
	Date validFrom;
	Date validThrough;
	String ticketId;
	String smsBody;
	
	private TicketDao(){
		uuid = UUID.randomUUID().toString();
		created = changed = validFrom = validThrough = null;
		state = ticketId = smsBody = "";
	}
	
	public static TicketDao create(Context context){
		TicketDao ticket = new TicketDao();
		
		ticket.setCreated(new Date());
		
		return ticket;
	}
	
	public void save(Context context){
		
		if (getState()==null) return;
		
		setChanged(new Date());
		
		TicketOpenSqlHelper sqlHelper = TicketOpenSqlHelper.getInstance(context);
		
		SQLiteDatabase db = sqlHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		
		values.put("uuid", uuid);
		values.put("created", date2string(created) );
		values.put("changed", date2string(changed));
		values.put("state", state);
		values.put("validFrom", date2string(validFrom));
		values.put("validThrough", date2string(validThrough));
		values.put("ticketId", ticketId);
		values.put("smsBody", smsBody);
		
		db.insert("tickets", "", values);
	}
	
	public void update(Context context){
		
		if (getState()==null || _id == "") return;
		
		setChanged(new Date());
		
		TicketOpenSqlHelper sqlHelper = TicketOpenSqlHelper.getInstance(context);
		
		SQLiteDatabase db = sqlHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		
		values.put("uuid", uuid);
		values.put("created", date2string(created) );
		values.put("changed", date2string(changed));
		values.put("state", state);
		values.put("validFrom", date2string(validFrom));
		values.put("validThrough", date2string(validThrough));
		values.put("ticketId", ticketId);
		values.put("smsBody", smsBody);
		
		db.update("tickets", values, "_id=?" , new String[] {_id});
	}
	
	public static TicketDao getById(String byId, Context context){
		
		TicketOpenSqlHelper sqlHelper = TicketOpenSqlHelper.getInstance(context);
		
		SQLiteDatabase db = sqlHelper.getWritableDatabase();
			
		Cursor result = db.query("tickets", null, "_id=?", new String [] {byId} , null, null, null);
		
		if (result.getCount() == 0) return null;
		if (result.getCount() >1 ) throw new RuntimeException("The database has been corrupted, please reinstall application.");
		
		TicketDao ticket = new TicketDao(); 
		
		Log.d(TAG,"Records read : " + result.getCount());
		
		ticket = rs2dao(ticket,result);
		
		result.close();
		
		return ticket;
		
	}
	
	public static TicketDao getByUUID(String byId, Context context){
		
		TicketOpenSqlHelper sqlHelper = TicketOpenSqlHelper.getInstance(context);
		
		SQLiteDatabase db = sqlHelper.getWritableDatabase();
			
		Cursor result = db.query("tickets", null, "uuid=?", new String [] {byId} , null, null, null);
		
		if (result.getCount() == 0) return null;
		if (result.getCount() >1 ) throw new RuntimeException("The database has been corrupted, please reinstall application.");
		
		TicketDao ticket = new TicketDao(); 
		
		Log.d(TAG,"Records read : " + result.getCount());
		
		ticket = rs2dao(ticket,result);
		
		result.close();
		
		return ticket;
		
	}
	public static TicketDao getCurrent(Context context){
		
		TicketOpenSqlHelper sqlHelper = TicketOpenSqlHelper.getInstance(context);
		
		SQLiteDatabase db = sqlHelper.getWritableDatabase();
			
		Cursor result = db.query("tickets", null, null, null , null, null, "created DESC", "1");
		
		if (result.getCount() == 0) return null;
		if (result.getCount() >1 ) throw new RuntimeException("Limit 1 query returns more than 1 result ? Head explodes.");
		
		TicketDao ticket = new TicketDao(); 
		
		Log.d(TAG,"Records read : " + result.getCount());
		
		ticket = rs2dao(ticket,result);

		result.close();
		
		return ticket;
		
	}

	public static TicketDao getValid(Context context){
		
		TicketOpenSqlHelper sqlHelper = TicketOpenSqlHelper.getInstance(context);
		
		SQLiteDatabase db = sqlHelper.getWritableDatabase();
			
		Cursor result = db.query("tickets", null, null, null , null, null, "created DESC", "1");
		
		if (result.getCount() == 0) return null;
		if (result.getCount() >1 ) throw new RuntimeException("Limit 1 query returns more than 1 result ? Head explodes.");
		
		TicketDao ticket = new TicketDao(); 
		
		Log.d(TAG,"Records read : " + result.getCount());
		
		ticket = rs2dao(ticket,result);

		result.close();
		
		return ticket;
		
	}

	public String get_id() {
		return _id;
	}

	private void set_id(String id) {
		_id = id;
	}

	public String getUuid() {
		return uuid;
	}
	public void setUuid(String id) {
		this.uuid = id;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getChanged() {
		return changed;
	}
	public void setChanged(Date changed) {
		this.changed = changed;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Date getValidFrom() {
		return validFrom;
	}
	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}
	public Date getValidThrough() {
		return validThrough;
	}
	public void setValidThrough(Date validThrough) {
		this.validThrough = validThrough;
	}
	public String getTicketId() {
		return ticketId;
	}
	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}
	public String getSmsBody() {
		return smsBody;
	}
	public void setSmsBody(String smsBody) {
		this.smsBody = smsBody;
	}
	
	private static TicketDao rs2dao(TicketDao dao, Cursor result){
		
		result.moveToFirst();
		int index = result.getColumnIndex("_id");
		dao.set_id(result.getString(index));
		index = result.getColumnIndex("uuid");
		String temp = result.getString(index);
		dao.setUuid(temp);
		index = result.getColumnIndex("created");
		dao.setCreated(string2date(result.getString(index)));
		index = result.getColumnIndex("changed");
		dao.setChanged(string2date(result.getString(index)));
		index = result.getColumnIndex("state");
		dao.setState(result.getString(index));
		index = result.getColumnIndex("validFrom");
		dao.setValidFrom(string2date(result.getString(index)));
		dao.setValidThrough(string2date(result.getString(result.getColumnIndex("validThrough"))));
		dao.setTicketId(result.getString(result.getColumnIndex("ticketId")));
		dao.setSmsBody(result.getString(result.getColumnIndex("smsBody")));
		
		return dao;
		
	}
	
	private static String date2string(Date date){
		
		if (date == null) return null;
		
		return dateFormatDb.format(date);
		
	}
	
	private static Date string2date(String date){
		
		if (date == null) return null;
				
		Date temp ;
		try {
			temp = dateFormatDb.parse(date, new ParsePosition(0));
			return temp;
		} catch (NullPointerException npe) {
			// unreachable (date!=null and ParsePosition!=null)
		}
		
		return null;
		
	}

	/**
	 *  Parse ticket SMS message text and store extracted info in DAO fileds. 
	 */
	public void expandBody() {
		try{
			//Pattern messagePattern = Pattern.compile(".*Platnost od ([0-9]{2}-[0-9]{2}-[0-9]{4}) ([0-9]{2}:[0-9]{2}) do ([0-9]{2}:[0-9]{2}) hod. ([[0-9][a-z]]{11})");
			Matcher matcher = messagePattern.matcher(getSmsBody());
			String dateFrom = "";
			String dateThrough = "";
			
			
			if (matcher.find()) {
				dateFrom = matcher.group(1) + " " + matcher.group(2);
				dateThrough = matcher.group(1) + " " + matcher.group(3);
				setValidFrom(dateFormatSms.parse(dateFrom));
				setValidThrough(dateFormatSms.parse(dateThrough));
				setTicketId(matcher.group(4));				
			}
			//String dateFrom = getSmsBody().substring(46,62);    //substring(70, 86);
			//String dateThrough = dateFrom.substring(0,11) + getSmsBody().substring(66,71); //substring(90, 95);
			Log.d(TAG, "Message parsed, valid from " + getValidFrom() + " to " + getValidThrough() );
			
		} catch (Exception e) 
		{
			//throw ()
			Log.e(TAG, "Message cannot be parsed for ticket." + e);
		}
	}
	
	
}
