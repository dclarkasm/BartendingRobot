package com.example.bartendingrobot;

import java.math.BigDecimal;
import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.WindowManager;

import com.androidplot.util.PlotStatistics;
import com.androidplot.xy.*;

import java.util.Arrays;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;


public class StatsActivity extends Activity {
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
	BigDecimal hrsRound, minsRound, shotRound, flozRound;
	
	String mODrink;		//most ordered drink
	int tDrinks;		//total drinks
	double aDPH;		//average drinks per hour
	double tTotal;		//tab total
	double tAC;			//total alcohol consumed
	String sTime;		//start time
	Time today;
	String amPm;
	
	int PDC[] = {0,0,0,0};		//per drink count: 0 = scrw drv, 1 = cub lib, 2 = mart, 3 = shirl tmp
	
	TextView mostOrdDnk, totdnks, avgDnkPHr, tabTot,
			totAC, strtTm;
	
	// the table that displays the data
	TableLayout dataTable;

	// the class that opens or creates the database and makes sql calls to it
	AABDatabaseManager db;

	Intent returnIntent = new Intent();
	
	private XYPlot plot;
    private XYPlot aprLevelsPlot = null;
    private SimpleXYSeries aprLevelsSeries = null;
    int yMax;
	
    /**
     * A simple formatter to convert bar indexes into sensor names.
     */
    private class APRIndexFormat extends Format {
        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            Number num = (Number) obj;
 
            // using num.intValue() will floor the value, so we add 0.5 to round instead:
            int roundNum = (int) (num.floatValue() + 0.5f);		//
            switch(roundNum) {
                case 0:
                    toAppendTo.append("Screw Driver");
                    break;
                case 1:
                    toAppendTo.append("Cuba Libre");
                    break;
                case 2:
                    toAppendTo.append("Martini");
                    break;
                case 3:
                    toAppendTo.append("Shirley Temple");
                    break;
                default:
                    toAppendTo.append("Unknown");
            }
            return toAppendTo;
        }
 
        @Override
        public Object parseObject(String source, ParsePosition pos) {
            return null;  // We don't use this so just return null for now.
        }
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);
		
	    	//================================================================================================================ bar graph
	    	// setup the APR Levels plot:
	        aprLevelsPlot = (XYPlot) findViewById(R.id.aprLevelsPlot);
	 
	        aprLevelsSeries = new SimpleXYSeries("Number of Drinks");
	        aprLevelsSeries.useImplicitXVals();
	        aprLevelsPlot.addSeries(aprLevelsSeries,
	                new BarFormatter(Color.argb(100, 0, 200, 0), Color.rgb(0, 80, 0)));
	        aprLevelsPlot.setDomainStepValue(4);
	        aprLevelsPlot.setTicksPerRangeLabel(1);
	 
	        // per the android documentation, the minimum and maximum readings we can get from
	        // any of the orientation sensors is -180 and 359 respectively so we will fix our plot's
	        // boundaries to those values.  If we did not do this, the plot would auto-range which
	        // can be visually confusing in the case of dynamic plots.
	        aprLevelsPlot.setRangeBoundaries(0, 0, BoundaryMode.FIXED);								//Y axis range
	 
	        // use our custom domain value formatter:
	        aprLevelsPlot.setDomainValueFormat(new APRIndexFormat());
	 
	        // update our domain and range axis labels:
	        aprLevelsPlot.setDomainLabel("Drink");
	        aprLevelsPlot.getDomainLabelWidget().pack();
	        aprLevelsPlot.setRangeLabel("Number of Drinks");
	        aprLevelsPlot.getRangeLabelWidget().pack();
	        aprLevelsPlot.setGridPadding(15, 15, 40, 0);
	 
	        final PlotStatistics levelStats = new PlotStatistics(1000, false);
	 
	        aprLevelsPlot.addListener(levelStats);
	        // get a ref to the BarRenderer so we can make some changes to it:
	        BarRenderer barRenderer = (BarRenderer) aprLevelsPlot.getRenderer(BarRenderer.class);
	        if(barRenderer != null) {
	            // make our bars a little thicker than the default so they can be seen better:
	            barRenderer.setBarWidth(25);
	        }
	        //================================================================================================================
	        
		try
    	{
	        // create the database manager object
	        db = new AABDatabaseManager(this);
 
	        // create references and listeners for the GUI interface
	        dataTable = (TableLayout)findViewById(R.id.data_table);
	        mostOrdDnk = (TextView)findViewById(R.id.mostOrdered);
	        totdnks = (TextView)findViewById(R.id.totalDrinks);
	        avgDnkPHr = (TextView)findViewById(R.id.avgDPH);
	        tabTot = (TextView)findViewById(R.id.tabTotal);
	        totAC = (TextView)findViewById(R.id.totalAC);
	        strtTm = (TextView)findViewById(R.id.strtTime);
 
	        // load the data table
	        retrieveRow();
	    	clacStatistics();
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
		getMenuInflater().inflate(R.menu.stats, menu);
		return true;
	}
	
	public void clacStatistics(){
		mODrink = null;		//most ordered drink
		tDrinks = 0;		//total drinks
		aDPH = 0;		//average drinks per hour
		tTotal = 0;		//tab total
		tAC = 0;			//total alcohol consumed
		sTime = null;		//start time
		
		/*		
		 * mostOrdDnk, totdnks, avgDnkPHr, tabTot,
			totAC, strtTm
		returned from retrieve drink row
		drinkName = (String)row.get(1);
    	acVol = Double.parseDouble((String)row.get(2));  //acVol wants double 
    	drinkCost = Double.parseDouble((String)row.get(3));
    	timestmp = Double.parseDouble((String)row.get(4));
		*/
		tDrinks = drinkNum;
		
		for(drinkIndex=drinkNum; drinkIndex>0; drinkIndex--){
			retrieveDrinkRow();
			if(drinkIndex > drinkStrt){
				tTotal += drinkCost;
				if(drinkIndex==(drinkStrt+1)){
					timeCalc();
					if(mins<10) sTime = hrs + ":0" + mins + " " + amPm;
					else sTime = hrs + ":" + mins + " " + amPm;
				}
			}
			tAC += acVol;
			
			//0 = scrw drv, 1 = cub lib, 2 = mart, 3 = shirl tmp
			if(drinkName.equalsIgnoreCase("Screw Driver")) PDC[0]++;
			else if(drinkName.equalsIgnoreCase("Cuba Libre")) PDC[1]++;
			else if(drinkName.equalsIgnoreCase("Martini")) PDC[2]++;
			else if(drinkName.equalsIgnoreCase("Shirley Temple")) PDC[3]++;
		}
		
		if((PDC[0]>=PDC[1])&&(PDC[0]>=PDC[2])&&(PDC[0]>=PDC[3])){
			mODrink = "Screw Driver";
			yMax = PDC[0];
		}
		else if((PDC[1]>=PDC[0])&&(PDC[1]>=PDC[2])&&(PDC[1]>=PDC[3])){
			mODrink = "Cuba Libre";
			yMax = PDC[1];
		}
		else if((PDC[2]>=PDC[0])&&(PDC[2]>=PDC[1])&&(PDC[2]>=PDC[3])){
			mODrink = "Martini";
			yMax = PDC[2];
		}
		else if((PDC[3]>=PDC[0])&&(PDC[3]>=PDC[2])&&(PDC[3]>=PDC[1])){
			mODrink = "Shirley Temple";
			yMax = PDC[3];
		}
		
		flozRound = new BigDecimal(tAC);
		flozRound = flozRound.setScale(2, BigDecimal.ROUND_CEILING);
		
		shotRound = new BigDecimal(tAC/1.5);
		shotRound = shotRound.setScale(2, BigDecimal.ROUND_CEILING);
		
		mostOrdDnk.setText(mODrink);
		totdnks.setText(Integer.toString(tDrinks));
		tabTot.setText("$" + Double.toString(tTotal));
		totAC.setText(shotRound + " shots (" +
				      flozRound + " fl. oz.)");
		strtTm.setText(sTime);
		
		//set bar graph values
		aprLevelsPlot.setRangeBoundaries(0, yMax+3, BoundaryMode.FIXED);								//Y axis range yMax
		Number[] series1Numbers = {PDC[0], PDC[1], PDC[2], PDC[3]};
        aprLevelsSeries.setModel(Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);
        aprLevelsPlot.redraw();
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
	
	public void exit (View view){
		finish();
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
