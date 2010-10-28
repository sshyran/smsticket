package sk.michalko.smsticket;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TicketDao {
	
	static final String TAG = TicketDao.class.getSimpleName();

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
			
		Cursor result = db.query("tickets", null, "uuid=?", new String [] {byId} , null, null, null);
		
		if (result.getCount() == 0) return null;
		if (result.getCount() >1 ) throw new RuntimeException("The database has been corrupted, please reinstall application.");
		
		TicketDao ticket = new TicketDao(); 
		
		Log.d(TAG,"Records read : " + result.getCount());
		
		ticket = rs2dao(ticket,result);
		
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
		
		return ticket;
		
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
		int index = result.getColumnIndex("uuid");
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
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
		return dateFormat.format(date);
		
	}
	
	private static Date string2date(String date){
		
		if (date == null) return null;
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
		
		Date temp ;
		try {
			temp = dateFormat.parse(date, new ParsePosition(0));
			return temp;
		} catch (NullPointerException npe) {
			// unreachable (date!=null and ParsePosition!=null)
		}
		
		return null;
		
	}
	
	
}
