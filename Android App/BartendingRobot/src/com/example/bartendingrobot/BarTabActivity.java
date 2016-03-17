package com.example.bartendingrobot;

import java.math.BigDecimal;
import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class BarTabActivity extends Activity {
	
	String name = "";					//text field
	String email = "";					//text field
	String address = "";				//text field
	String city = "";					//text field
	String state = "";					//spinner
	String zip = "";					//text field
	
	String cCName = "";					//text field
	String cCNum = "";					//text field
	String cCExpDate = "";				//text field
	String cCV = "";					//text field
	int weight = 130;					//spinner
	String gend = "";					//spinner
	
	public int drinkNum = 0;
	public int drinkStrt = 0;
	double drinkCost;
	public int drinkIndex = 0;
	String drinkName;
	public String tabStat = "ia";
	double acVol = 0;			//the alcohol content volume used in calculation
	double timestmp;
	
	double crntTime;
	int hrs = 0;
	int mins = 0;
	Time today;
	String amPm;
	BigDecimal hrsRound;
	BigDecimal minsRound;
	
	double tTotal;		//tab total
	
	TextView tabTot;
	
	// the table that displays the data
	TableLayout dataTable;

	// the class that opens or creates the database and makes sql calls to it
	AABDatabaseManager db;

	Intent returnIntent = new Intent();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bar_tab);
		setResult(RESULT_CANCELED, returnIntent);
		try
    	{
	        // create the database manager object
	        db = new AABDatabaseManager(this);
 
	        // create references and listeners for the GUI interface
	        dataTable = (TableLayout)findViewById(R.id.data_table);
	        tabTot = (TextView)findViewById(R.id.tabTotal);
 
	        // load the data table
	    	retrieveRow();		//was after updateTable
	    	updateTable();
	    	calcTotal();
    	}
    	catch (Exception e)
    	{
    		Log.e("ERROR", e.toString());
    		e.printStackTrace();
    	}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bar_tab, menu);
		return true;
	}
	
	public void calcTotal(){
		tTotal = 0;		//tab total
		
		for(drinkIndex=drinkNum; drinkIndex>drinkStrt; drinkIndex--){
			retrieveDrinkRow();
			tTotal += drinkCost;
		}
		
		tabTot.setText("$" + Double.toString(tTotal));
	}
	
	public void payTab(View view){
		
		//retrieveRow();
		//updateTable();
		/*
		for(int i; i<=drinkNum; i++){
			deleteDrinkRow();
		}
		*/
		showDialog(1);
		
	}
	
	protected Dialog onCreateDialog(int id)
    {
    
	final AlertDialog.Builder alert = new AlertDialog.Builder(this);    
    LinearLayout lila1= new LinearLayout(this);
    
    if(id == 1){
    	lila1.setOrientation(1); //1 is for vertical orientation
	    
	    alert.setView(lila1);

	        alert.setIcon(R.drawable.ic_launcher);
	        alert.setTitle("Pay Tab");
	        alert.setMessage("Are you sure you wish to close and pay your current tab. " +
	        		"Press 'Yes' to pay tab with credit card.");

	        //does this when the Yes button is pressed
	        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {             
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	//db.deleteRow(drinkNum);
	            	Toast.makeText(getApplicationContext(), "Drink count: " + drinkNum,
	                Toast.LENGTH_LONG).show();
	            	tabStat = "ia";			//set tabStat to inactive
	            	//drinkNum = 0;
	            	drinkStrt = drinkNum;
	            	updateAccountRow();
	            	updateTable();
	            	calcTotal();
	            	setResult(RESULT_OK, returnIntent);
	            }                     
	        });                 
	        alert.setNegativeButton("Cancel",          //close the dialog if the cancel button is pressed        
	                new DialogInterface.OnClickListener() {                           
	            public void onClick(DialogInterface dialog, int whichButton) {          
	                //add code to handle cancel of update or cancel of initial set up
	            	dialog.cancel();  
	            }     });  
    }
      
        return alert.create();      
        }
	
	public void exit (View view){
		finish();
	}
	
	/**
     * updates the table from the database.
     */
    private void updateTable()
    {
    	// delete all but the first row.  remember that the count 
    	// starts at one and the index starts at zero
    	while (dataTable.getChildCount() > 1)
    	{
    		// while there are at least two rows in the table widget, delete
    		// the second row.
    		dataTable.removeViewAt(1);
    	}
 
    	// collect the current row information from the database and
    	// store it in a two dimensional ArrayList
    	ArrayList<ArrayList<Object>> data = db.getAllDrinkRowsAsArrays(drinkStrt);
 
    	// iterate the ArrayList, create new rows each time and add them
    	// to the table widget.
    	
    	for (int position=0; position < data.size(); position++)
    	{
    		
    		TableRow tableRow= new TableRow(this);

    		ArrayList<Object> row = data.get(position);

    		TextView idText = new TextView(this);				//Drink #
    		idText.setText(Integer.toString(position+1));
    		tableRow.addView(idText);

    		TextView textOne = new TextView(this);				//Drink Name
    		textOne.setText(row.get(1).toString());
    		tableRow.addView(textOne);

    		TextView textTwo = new TextView(this);				//ACD
    		textTwo.setText(row.get(2).toString());
    		tableRow.addView(textTwo);

    		TextView textThree = new TextView(this);			//Cost
    		textThree.setText("$" + row.get(3).toString() + "0");
    		tableRow.addView(textThree);

    		TextView textFour = new TextView(this);				//Order Time
    		timestmp = Double.parseDouble(row.get(4).toString());
    		timeCalc();
    		if(mins<10) textFour.setText(hrs + ":0" + mins + " " + amPm);
    		else textFour.setText(hrs + ":" + mins + " " + amPm);
    		tableRow.addView(textFour);

    		dataTable.addView(tableRow);
    	}
    }
    
    public void timeCalc(){
    	hrs = (int)((System.currentTimeMillis() - timestmp)/3600000);
        //hrsRound = new BigDecimal(hrs);
        //hrsRound = hrsRound.setScale(0, BigDecimal.ROUND_FLOOR);
        	
        mins = (int)(60 * (((System.currentTimeMillis() - timestmp)/3600000) - hrs));
        //minsRound = new BigDecimal(mins);
        //minsRound = minsRound.setScale(0, BigDecimal.ROUND_CEILING);
        
        today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		
		hrs = today.hour - hrs;
		if(hrs<0){
			hrs = 12 + hrs;
		}
		
		mins = today.minute - mins;
		if(mins<0){
			mins = 59 + mins;
			hrs--;
		}
		
		amPm = "AM";
				
		if(hrs == 0) hrs = 12;
		else if(hrs > 12) {
			hrs -= 12;
			amPm = "PM";
		}
    }

    protected void retrieveDrinkRow()
    {
    	ArrayList<Object> row;
    		//when dBChooser is 1, retrieve the user account
    		
    	try
    	{
    		// ask the database manager to retrieve the row with the given index
    		Log.i("Retrieve Drink Row", "index 1: " + drinkIndex);
    		row = db.getDrinkRowAsArray(Integer.toString(drinkIndex));	
    		
    		// update the form fields to hold the retrieved data1
    		drinkName = (String)row.get(1);
    		acVol = Double.parseDouble((String)row.get(2));  //acVol wants double 
    		drinkCost = Double.parseDouble((String)row.get(3));
    		timestmp = Double.parseDouble((String)row.get(4));
    	}	
    	catch (Exception e)
    	{
    		Log.e("Retrieve Error drink", e.toString());
    		e.printStackTrace();
    	}	
    	
    }
    
    /**
     * retrieves a row from the database with the index which should always be 0
     */
    protected void retrieveRow()
    {
    	ArrayList<Object> row;
    		//when dBChooser is 1, retrieve the user account
    		
    	try
    	{
    		// ask the database manager to retrieve the row with the given index
    		row = db.getRowAsArray("0");	
    		
    		// update the form fields to hold the retrieved data1
    		name = (String)row.get(1);
    		email = (String)row.get(2);
    		address = (String)row.get(3);
    		city = (String)row.get(4);
    		state = (String)row.get(5);
    		cCName = (String)row.get(6);
    		cCNum = (String)row.get(7);
    		cCExpDate = (String)row.get(8);
    		cCV = (String)row.get(9);
    		weight = Integer.parseInt((String)row.get(10));
    		gend = (String)row.get(11);
    		drinkStrt = Integer.parseInt((String)row.get(12));
    		drinkNum = Integer.parseInt((String)row.get(13));
    		tabStat = (String)row.get(14);
    	}
    	catch (Exception e)
    	{
    		Log.e("Retrieve Account Error", e.toString());
    		e.printStackTrace();
    	}	
    }
    
    /**
     * updates a row with the most current information
     */
    public void updateAccountRow()
    {
    	try
    	{
    		Toast.makeText(getApplicationContext(), "Drink count: " + drinkNum,
            Toast.LENGTH_LONG).show();
    		// ask the database manager to update the row based on the information
    		// found in the corresponding user entry fields
    		//if(dBChooser == 1){
    		db.updateRow
    		(
    				//Long.parseLong(index.toString()),
    				0,
    				name.toString(),
    				email.toString(),
    				address.toString(),
    				city.toString(),
    				state.toString(),
    				cCName.toString(),
    				cCNum.toString(),
    				cCExpDate.toString(),
    				cCV.toString(),
    				Integer.toString(weight),
    				gend.toString(),
    				Integer.toString(drinkStrt),
    				Integer.toString(drinkNum),
    				tabStat.toString()
    		);
    		
    	}
    	catch (Exception e)
    	{
    		Log.e("Update Error", e.toString());
    		e.printStackTrace();
    		}
    }
}
