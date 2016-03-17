package com.example.bartendingrobot;
 
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
 
public class AABDatabaseManager
{
	// the Activity or Application that is creating an object from this class.
	Context context;
 
	// a reference to the database used by this application/object
	private SQLiteDatabase db;
 
	// These constants are specific to the database.  They should be 
	// changed to suit your needs.
	private final String DB_ACCOUNT = "User_Account";
	private final int DB_VERSION = 1;
	
	// These constants are specific to the database table.  They should be
	// changed to suit your needs.
	private final String TABLE_NAME = "database_table";
	private final String TABLE_ROW_INDEX = "id";
	private final String TABLE_ROW_NAME = "Name";
	private final String TABLE_ROW_ADDRESS = "Address";
	private final String TABLE_ROW_EMAIL = "Email";
	private final String TABLE_ROW_CITY = "City";
	private final String TABLE_ROW_STATE = "State";
	private final String TABLE_ROW_CCNAM = "CC_Nam";
	private final String TABLE_ROW_CCNUM = "CC_Num";
	private final String TABLE_ROW_CCEXPDATE = "CC_Exp_Date";
	private final String TABLE_ROW_CCV = "CCV";
	private final String TABLE_ROW_WEIGHT = "Weight";
	private final String TABLE_ROW_GENDER = "Gender";
	private final String TABLE_ROW_DRINKSTRT = "Drink_Start";
	private final String TABLE_ROW_DRINKNUM = "Drink_Num";
	private final String TABLE_ROW_TABSTATUS = "Tab_Status";
	
	private final String DRINK_TABLE_NAME = "drink_database_table";
	//index is the same
	private final String TABLE_ROW_DRINK_NAME = "Drink_Name";
	private final String TABLE_ROW_ACVOL = "Alcohol_Volume";
	private final String TABLE_ROW_COST = "Cost";
	private final String TABLE_ROW_ORDTIME = "Order_Time";
	
	public AABDatabaseManager(Context context)
	{
		this.context = context;

		// create or open the database
		CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
		this.db = helper.getWritableDatabase();
	}




	/**********************************************************************
	 * ADDING A ROW TO THE DATABASE TABLE
	 * 
	 * the key is automatically assigned by the database
	 * @param rowStringOne the id column
	 * @param rowStringTwo the device name column 
	 * @param rowStringThree the serial number column 
	 * @param rowStringFour the warranty expiration column 
	 * @param rowStringFive the software column 
	 * @param rowStringSix the device GPS coordinate location column 
	 */

	public void addRow( 
			int rowID,
			String rowStringTwo,
			String rowStringThree,
			String rowStringFour, 
			String rowStringFive,
			String rowStringSix,
			String rowStringSeven,
			String rowStringEight,
			String rowStringNine,
			String rowStringTen,
			String rowStringEleven,
			String rowStringTwelve,
			String rowStringThirteen,
			String rowStringFourteen,
			String rowStringFifteen
			)
	{
		// this is a key value pair holder used by android's SQLite functions
		ContentValues values = new ContentValues();
		values.put(TABLE_ROW_INDEX, rowID);
		values.put(TABLE_ROW_NAME, rowStringTwo);
		values.put(TABLE_ROW_EMAIL, rowStringThree);
		values.put(TABLE_ROW_ADDRESS, rowStringFour);
		values.put(TABLE_ROW_CITY, rowStringFive);
		values.put(TABLE_ROW_STATE, rowStringSix);
		values.put(TABLE_ROW_CCNAM, rowStringSeven);
		values.put(TABLE_ROW_CCNUM, rowStringEight);
		values.put(TABLE_ROW_CCEXPDATE, rowStringNine);
		values.put(TABLE_ROW_CCV, rowStringTen);
		values.put(TABLE_ROW_WEIGHT, rowStringEleven);
		values.put(TABLE_ROW_GENDER, rowStringTwelve);
		values.put(TABLE_ROW_DRINKSTRT, rowStringThirteen);
		values.put(TABLE_ROW_DRINKNUM, rowStringFourteen);
		values.put(TABLE_ROW_TABSTATUS, rowStringFifteen);

		// ask the database object to insert the new data 
		try{db.insert(TABLE_NAME, null, values);}
		catch(Exception e)
		{
			Log.e("DB ERROR 1", e.toString());
			e.printStackTrace();
		}
	}
	
	public void addDrinkRow( 
			int rowID,
			String rowStringTwo,
			String rowStringThree,
			String rowStringFour, 
			String rowStringFive
			)
	{
		// this is a key value pair holder used by android's SQLite functions
		ContentValues values = new ContentValues();
		values.put(TABLE_ROW_INDEX, rowID);
		values.put(TABLE_ROW_DRINK_NAME, rowStringTwo);
		values.put(TABLE_ROW_ACVOL, rowStringThree);
		values.put(TABLE_ROW_COST, rowStringFour);
		values.put(TABLE_ROW_ORDTIME, rowStringFive);

		// ask the database object to insert the new data 
		try{db.insert(DRINK_TABLE_NAME, null, values);}
		catch(Exception e)
		{
			Log.e("DB ERROR 1", e.toString());
			e.printStackTrace();
		}
	}
 
 
	/**********************************************************************
	 * DELETING A ROW FROM THE DATABASE TABLE
	 * 
	 * This is used to delete the 
	 * 
	 * @param rowID the SQLite database identifier for the row to delete.
	 */
	public void deleteRow(long drinkCount)
	{
			try
			{
					for(;drinkCount!=0; drinkCount--)
					{
						db.delete(DRINK_TABLE_NAME, TABLE_ROW_INDEX + "=" + drinkCount, null);
						Log.d("Deleting", "row: " + drinkCount);
					}
					// move the cursor's pointer up one position.
			}
			catch (SQLException e)
			{
				Log.e("DB FAILED TO DELETE", e.toString());
				e.printStackTrace();
			}
		}
	
 
	/**********************************************************************
	 * UPDATING A ROW IN THE DATABASE TABLE
	 * 
	 * @param rowStringOne the id column
	 * @param rowStringTwo the device name column 
	 * @param rowStringThree the serial number column 
	 * @param rowStringFour the warranty expiration column 
	 * @param rowStringFive the software column 
	 * @param rowStringSix the device GPS coordinate location column  
	 */ 
	public void updateRow(
			long rowID,
			String rowStringTwo, 
			String rowStringThree,
			String rowStringFour, 
			String rowStringFive,
			String rowStringSix,
			String rowStringSeven,
			String rowStringEight,
			String rowStringNine,
			String rowStringTen,
			String rowStringEleven,
			String rowStringTwelve,
			String rowStringThirteen,
			String rowStringFourteen,
			String rowStringFifteen
			)
	{
		
		// this is a key value pair holder used by android's SQLite functions
		ContentValues values = new ContentValues();
		
		values.put(TABLE_ROW_INDEX, rowID);
		values.put(TABLE_ROW_NAME, rowStringTwo);
		values.put(TABLE_ROW_EMAIL, rowStringThree);
		values.put(TABLE_ROW_ADDRESS, rowStringFour);
		values.put(TABLE_ROW_CITY, rowStringFive);
		values.put(TABLE_ROW_STATE, rowStringSix);
		values.put(TABLE_ROW_CCNAM, rowStringSeven);
		values.put(TABLE_ROW_CCNUM, rowStringEight);
		values.put(TABLE_ROW_CCEXPDATE, rowStringNine);
		values.put(TABLE_ROW_CCV, rowStringTen);
		values.put(TABLE_ROW_WEIGHT, rowStringEleven);
		values.put(TABLE_ROW_GENDER, rowStringTwelve);
		values.put(TABLE_ROW_DRINKSTRT, rowStringThirteen);
		values.put(TABLE_ROW_DRINKNUM, rowStringFourteen);
		values.put(TABLE_ROW_TABSTATUS, rowStringFifteen);

		// ask the database object to update the database row of given rowID
		try {db.update(TABLE_NAME, values, TABLE_ROW_INDEX + " = '" + rowID + "'", null);
		Log.d("AABDDataB", "update successful");}

		catch (Exception e)
		{
			Log.e("DB ERROR 7", e.toString());
			e.printStackTrace();
			Log.d("AABDDataB", "update unsuccessful");
					}
	}
	
	
	/**********************************************************************
	 * RETRIEVING A ROW FROM THE DATABASE TABLE
	 * 
	 * This is an example of how to retrieve a row from a database table
	 * using this class.  You should edit this method to suit your needs.
	 * 
	 * @param rowID the id of the row to retrieve
	 * @return an array containing the data from the row
	 */
	public ArrayList<Object> getRowAsArray(String rowID)
	{
		// create an array list to store data from the database row.
		// I would recommend creating a JavaBean compliant object 
		// to store this data instead.  That way you can ensure
		// data types are correct.
		ArrayList<Object> rowArray = new ArrayList<Object>();
		Cursor cursor;
		
		try
		{
			// this is a database call that creates a "cursor" object.
			// the cursor object store the information collected from the
			// database and is used to iterate through the data.
			cursor = db.query
			(
					TABLE_NAME,
					new String[] { 
							TABLE_ROW_INDEX,
							TABLE_ROW_NAME,
							TABLE_ROW_EMAIL,
							TABLE_ROW_ADDRESS,
							TABLE_ROW_CITY,
							TABLE_ROW_STATE,
							TABLE_ROW_CCNAM,
							TABLE_ROW_CCNUM,
							TABLE_ROW_CCEXPDATE,
							TABLE_ROW_CCV,
							TABLE_ROW_WEIGHT,
							TABLE_ROW_GENDER,
							TABLE_ROW_DRINKSTRT,
							TABLE_ROW_DRINKNUM,
							TABLE_ROW_TABSTATUS
							},
					TABLE_ROW_INDEX + " = '" + rowID + "'",
					null, null, null, null, null
			);
			// move the pointer to position zero in the cursor.
			cursor.moveToFirst();
			// if there is data available after the cursor's pointer, add
			// it to the ArrayList that will be returned by the method.
			if (!cursor.isAfterLast())
			{
				do{
					rowArray.add(cursor.getString(0));
					rowArray.add(cursor.getString(1));
					rowArray.add(cursor.getString(2));
					rowArray.add(cursor.getString(3));
					rowArray.add(cursor.getString(4));
					rowArray.add(cursor.getString(5));
					rowArray.add(cursor.getString(6));
					rowArray.add(cursor.getString(7));
					rowArray.add(cursor.getString(8));
					rowArray.add(cursor.getString(9));
					rowArray.add(cursor.getString(10));
					rowArray.add(cursor.getString(11));
					rowArray.add(cursor.getString(12));
					rowArray.add(cursor.getString(13));
					rowArray.add(cursor.getString(14));
				}
				while (cursor.moveToNext());
			}
 
			// let java know that you are through with the cursor.
			cursor.close();
		}
		catch (SQLException e) 
		{
			Log.e("DB ERROR 10", e.toString());
			e.printStackTrace();
		}
 
		// return the ArrayList containing the given row from the database.
		return rowArray;
	}

	public ArrayList<Object> getDrinkRowAsArray(String rowID)
	{
		// create an array list to store data from the database row.
		// I would recommend creating a JavaBean compliant object 
		// to store this data instead.  That way you can ensure
		// data types are correct.
		ArrayList<Object> rowArray = new ArrayList<Object>();
		Cursor cursor;
		
		try
		{
			// this is a database call that creates a "cursor" object.
			// the cursor object store the information collected from the
			// database and is used to iterate through the data.
			/*
			 * TABLE_ROW_INDEX
			 * TABLE_ROW_DRINK_NAME
			 * TABLE_ROW_ACVOL
			 * TABLE_ROW_COST
			 * TABLE_ROW_ORDTIME
			 */
			cursor = db.query
			(
					DRINK_TABLE_NAME,
					new String[] { 
							TABLE_ROW_INDEX,
							TABLE_ROW_DRINK_NAME,
							TABLE_ROW_ACVOL,
							TABLE_ROW_COST,
							TABLE_ROW_ORDTIME
							},
					TABLE_ROW_INDEX + " = '" + rowID + "'",
					null, null, null, null, null
			);
			// move the pointer to position zero in the cursor.
			cursor.moveToFirst();
			// if there is data available after the cursor's pointer, add
			// it to the ArrayList that will be returned by the method.
			if (!cursor.isAfterLast())
			{
				do{
					rowArray.add(cursor.getLong(0));
					rowArray.add(cursor.getString(1));
					rowArray.add(cursor.getString(2));
					rowArray.add(cursor.getString(3));
					rowArray.add(cursor.getString(4));
				}
				while (cursor.moveToNext());
			}
 
			// let java know that you are through with the cursor.
			cursor.close();
		}
		catch (SQLException e) 
		{
			Log.e("DB ERROR 10", e.toString());
			e.printStackTrace();
		}
 
		// return the ArrayList containing the given row from the database.
		return rowArray;
	}	
	
	

	/**********************************************************************
	 * RETRIEVING ALL ROWS FROM THE DATABASE TABLE
	 * 
	 * the key is automatically assigned by the database
	 */
 
	public ArrayList<ArrayList<Object>> getAllDrinkRowsAsArrays(int initPosition)
	{
		// create an ArrayList that will hold all of the data collected from
		// the database.
		ArrayList<ArrayList<Object>> dataArrays = new ArrayList<ArrayList<Object>>();
		
		// this is a database call that creates a "cursor" object.
		// the cursor object store the information collected from the
		// database and is used to iterate through the data.
		Cursor cursor;
 
		try
		{
			// ask the database object to create the cursor.
			/*
			 * TABLE_ROW_INDEX
			 * TABLE_ROW_DRINK_NAME
			 * TABLE_ROW_ACVOL
			 * TABLE_ROW_COST
			 * TABLE_ROW_ORDTIME
			 */
			cursor = db.query(
					DRINK_TABLE_NAME,
					new String[]{
							TABLE_ROW_INDEX, 
							TABLE_ROW_DRINK_NAME,
							TABLE_ROW_ACVOL, 
							TABLE_ROW_COST,
							TABLE_ROW_ORDTIME
							},
					null, null, null, null, null
			);
 
			// move the cursor's pointer to position zero.
			//cursor.moveToFirst();
			cursor.moveToPosition(initPosition);	//move cursor to the start position
 
			// if there is data after the current cursor position, add it
			// to the ArrayList.
			if (!cursor.isAfterLast())
			{
				do
				{
					
					ArrayList<Object> dataList = new ArrayList<Object>();
 
					dataList.add(cursor.getLong(0));
					dataList.add(cursor.getString(1));
					dataList.add(cursor.getString(2));
					dataList.add(cursor.getString(3));
					dataList.add(cursor.getString(4));
 
					dataArrays.add(dataList);
					
				}
				// move the cursor's pointer up one position.
				while (cursor.moveToNext());
			}
		}
		catch (SQLException e)
		{
			Log.e("Error retrieving rows", e.toString());
			e.printStackTrace();
		}
 
		// return the ArrayList that holds the data collected from
		// the database.
		return dataArrays;
	}

	/**********************************************************************
	 * THIS IS THE BEGINNING OF THE INTERNAL SQLiteOpenHelper SUBCLASS.
	 * 
	 * I MADE THIS CLASS INTERNAL SO I CAN COPY A SINGLE FILE TO NEW APPS 
	 * AND MODIFYING IT - ACHIEVING DATABASE FUNCTIONALITY.  ALSO, THIS WAY 
	 * I DO NOT HAVE TO SHARE CONSTANTS BETWEEN TWO FILES AND CAN
	 * INSTEAD MAKE THEM PRIVATE AND/OR NON-STATIC.  HOWEVER, I THINK THE
	 * INDUSTRY STANDARD IS TO KEEP THIS CLASS IN A SEPARATE FILE.
	 *********************************************************************/
 
	/**
	 * This class is designed to check if there is a database that currently
	 * exists for the given program.  If the database does not exist, it creates
	 * one.  After the class ensures that the database exists, this class
	 * will open the database for use.  Most of this functionality will be
	 * handled by the SQLiteOpenHelper parent class.  The purpose of extending
	 * this class is to tell the class how to create (or update) the database.
	 * 
	 * @author Randall Mitchell
	 *
	 */
	private class CustomSQLiteOpenHelper extends SQLiteOpenHelper
	{
		public CustomSQLiteOpenHelper(Context context)
		{
			super(context, DB_ACCOUNT, null, DB_VERSION);
		}
 		
		
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			// This string is used to create the database.  It should
			// be changed to suit your needs.
			//primary key autoincrement not null
			String newTableQueryString = "create table " +
										TABLE_NAME +
										" (" +
										TABLE_ROW_INDEX + " integer," +
										TABLE_ROW_NAME + " text," +
										TABLE_ROW_EMAIL + " text," +
										TABLE_ROW_ADDRESS + " text," +
										TABLE_ROW_CITY + " text," +
										TABLE_ROW_STATE + " text," +
										TABLE_ROW_CCNAM + " text," +
										TABLE_ROW_CCNUM + " text," +
										TABLE_ROW_CCEXPDATE + " text," +
										TABLE_ROW_CCV + " text," +
										TABLE_ROW_WEIGHT + " text," +
										TABLE_ROW_GENDER + " text," +
										TABLE_ROW_DRINKSTRT + " text," +
										TABLE_ROW_DRINKNUM + " text," +
										TABLE_ROW_TABSTATUS + " text" +
										");";
			// execute the query string to the database.
			db.execSQL(newTableQueryString);
			
			
			/*
			 * TABLE_ROW_INDEX
			 * TABLE_ROW_DRINK_NAME
			 * TABLE_ROW_ACVOL
			 * TABLE_ROW_COST
			 * TABLE_ROW_ORDTIME
			 */
			newTableQueryString = "create table " +
					DRINK_TABLE_NAME +
					" (" +
					TABLE_ROW_INDEX + " integer," +
					TABLE_ROW_DRINK_NAME + " text," +
					TABLE_ROW_ACVOL + " text," +
					TABLE_ROW_COST + " text," +
					TABLE_ROW_ORDTIME + " text" +
					");";
			// execute the query string to the database.
			db.execSQL(newTableQueryString);
		}


		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			// NOTHING TO DO HERE. THIS IS THE ORIGINAL DATABASE VERSION.
			// OTHERWISE, YOU WOULD SPECIFIY HOW TO UPGRADE THE DATABASE.
		}
	}
}