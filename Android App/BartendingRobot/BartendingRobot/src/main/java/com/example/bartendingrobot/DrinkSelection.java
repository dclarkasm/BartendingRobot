package com.example.bartendingrobot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.example.bartendingrobot.AABDatabaseManager;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.os.Bundle;

public class DrinkSelection extends IOIOActivity {										//was extends Activity
	//test comment
	String name = "name";					//text field
	String email = "email";					//text field
	String address = "address";				//text field
	String city = "city";					//text field
	String state = "state";					//spinner
	String zip = "00000";					//text field
	
	String cCName = "name";					//text field
	String cCNum = "CCnum";					//text field
	String cCExpDate = "CCexpdate";			//text field
	String cCV = "CCV";						//text field
	int weight = 130;						//spinner
	String gend = "Male";					//spinner
	
	//=======================================================================
	double acRum = .35;			//%alcohol content Rum
	double acVod = .4;			//%alcohol content Vodka
	double acGin = .4;			//%alcohol content Gin
	double acDry = .15;			//%alcohol content Dry Vermouth
	double acVolSD = .8;		//spirit volume Rum
	double acVolCL = .7;		//spirit volume Vodka
	double acVolST = .4;		//spirit volume Gin
	double acVolMT = 1.075;		//spirit volume Dry Vermouth
	double acVol = 0;			//the alcohol content volume used in calculation
	//mGenCon = 0.68 fGenCon = 0.55;
	double GenCon = 0.68;
	double fGenCon = 0.55;
	double timestmp;
	double drnkHrs = 0;
	double BAC = 0;
	double drinkBAC = 0;
	double targetBAC = 0.08;		//maximum BAC level to legally drive
	public int drinkIndex = 0;
	public int drinkNum = 0;
	public int drinkStrt = 0;
	public int oldDrkNum = 0;
	public String tabStat = "ia";
	
	String sDTitle = "Screw Driver";		//screw driver title
	String cLTitle = "Cuba Libre";			//cuba libre title
	String sTTitle = "Shirley Temple";		//shirley temple title
	String mTTitle = "Martini";				//martini title
	String drinkName;
	
	double sDCost = 5.00;
	double cLCost = 6.00;
	double sTCost = 7.00;
	double mTCost = 3.00;
	double drinkCost;
	
	double TTS = 0;		//time til sober
	int hrs = 0;
	int mins = 0;
	Time today;
	String amPm;
	
	String sDMsg = "Ingredients:\n\t-2 oz Vodka\n\t-5 oz Orange Juice\n$" + sDCost;
	
	String cLMsg = "Cuba Libre translated means �Free Cuba� and was \n"
			+"popularized around the end of the Spanish American War.\n"
			+"Ingredients:\n\t-2 oz light Rum\n\t-4 oz Cola\n$" + cLCost;
	
	String sTMsg = "Ingredients:\n\t-1 oz Vodka\n\t-5 oz Sprite\n\t-1/2 oz grenadine\n$" + sTCost;
	
	String mTMsg = "This James Bond favorite only comes stirred, but\n" +
			"is sure to not disapoint\n" +
			"Ingredients:\n\t-2 1/2 oz Gin\n\t-1/2 oz Dry Vermouth\n$" + mTCost;
	
	TextView myBAC, myTTS;
	//=======================================================================
	
	boolean firstRun = true;
	int index = 0;
	
	AABDatabaseManager accountDB;
	
	int dSelec = 0;			//drink selection
	
	//=======================================================================
	private Timer BACTimer;
	//=======================================================================
	
	//Android IOIO
	Uart uart;
	public boolean test = false, orderSD=false, orderCL=false, orderST=false, orderMT=false;
	InputStream in = null;
	OutputStream out = null;
	byte outNum=0;
	byte inNum;
	boolean input;
	byte prevoutNum = 0;
	Timer timer = new Timer();
	/**
	 * This is the thread on which all the IOIO activity happens. It will be run
	 * every time the application is resumed and aborted when it is paused. The
	 * method setup() will be called right after a connection with the IOIO has
	 * been established (which might happen several times!). Then, loop() will
	 * be called repetitively until the IOIO gets disconnected.
	 */
	class Looper extends BaseIOIOLooper {
		/** The on-board LED. */
		private DigitalOutput led_, outSD, outCL, outST, outMT, ackTra, man_pin, home;
		private DigitalInput ackRec;

		/**
		 * Called every time a connection with IOIO has been established.
		 * Typically used to open pins.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * 
		 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#setup()
		 */
		@Override
		protected void setup() throws ConnectionLostException {
			led_ = ioio_.openDigitalOutput(0, true);
			man_pin = ioio_.openDigitalOutput(24, true);
			outSD = ioio_.openDigitalOutput(18, true);
			outCL = ioio_.openDigitalOutput(19, true);
			outST = ioio_.openDigitalOutput(20, true);
			outMT = ioio_.openDigitalOutput(21, true);
			home = ioio_.openDigitalOutput(22, true);
			ackTra = ioio_.openDigitalOutput(23, true);
			ackRec = ioio_.openDigitalInput(44);		//, DigitalInput.Spec.Mode.PULL_UP
			uart = ioio_.openUart(13, 14, 9600, Uart.Parity.NONE, Uart.StopBits.ONE);		//rx, tx, baud=9600, parity, # of stop bits 
		}																					//use IOIO.INVALID_PIN for JUST rx or JUST tx

		/**
		 * Called repetitively while the IOIO is connected.
		 * 
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * 
		 * @see ioio.lib.util.AbstractIOIOActivity.IOIOThread#loop()
		 */
		@Override
		public void loop() throws ConnectionLostException {
			//led_.write(test);
			man_pin.write(false);
			if (prevoutNum==0) prevoutNum=outNum;
			else if (prevoutNum > 0){
				if(prevoutNum != outNum){
					outSD.write(false);
					outCL.write(false);
					outST.write(false);
					outMT.write(false);
					home.write(false);
					ackTra.write(false);
					prevoutNum = outNum;
				}
			}
			
			if(outNum == 1) {
				outSD.write(true); 
				led_.write(true);
				Log.d("devons IOIO", "1");
			}
			else if(outNum == 2){
				outCL.write(true); 
				led_.write(true);
				Log.d("devons IOIO", "2");
			}
			else if(outNum == 3){
				outST.write(true); 
				led_.write(true);
				Log.d("devons IOIO", "3");
			}
			else if(outNum == 4){
				outMT.write(true); 
				led_.write(true);
				Log.d("devons IOIO", "4");
			}
			else{
				outSD.write(false);
				outCL.write(false);
				outST.write(false);
				outMT.write(false);
				led_.write(false);
				Log.d("devons IOIO", "5");
			}
			
			
			try {
				input = ackRec.read();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if(input) 
				{
					//led_.write(true);
					Log.d("devons IOIO", "dig in on");
					outNum = 0;
					/*
					ackTra.write(true);
					
					timer.scheduleAtFixedRate(new TimerTask()
			        {
			            public void run() 
			            {       
			            	try {
								ackTra.write(false);	//set false after 200ms
								onDestroy();		//destroy the timer after 1 use
							} catch (ConnectionLostException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			            }
			        }, 0, 200);
					
					/*
					while(input)
					{
						try {
							input = ackRec.read();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					*/
					
				}
			else 
				{
					
					//led_.write(false);
					Log.d("devons IOIO", "ldig in off");
				}
			
			try {
				final boolean value = ackRec.read();
				input = value;
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			/*
			//*********************************************************************************** UART stuff
			try{
				//uart = ioio_.openUart(13, 14, 9600, Uart.Parity.NONE, Uart.StopBits.ONE);		//tx=45, rx=46, baud=9600, parity, # of stop bits 
																								//use IOIO.INVALID_PIN for JUST rx or JUST tx

				in = uart.getInputStream();
				out = uart.getOutputStream();
				out.write(outNum);

				//outNum = 0;
				Log.d("IOIO UART", "out = " + outNum);				
				
				try {
					inNum = (byte) in.read();
					Log.d("IOIO UART", "in = " + inNum);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("IOIO UART", "Could not read");
				}
				
					
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("IOIO UART", "Could not transmit");
			}
			
			
			if(in != null) {
				try {
					in.close();
				} 
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				
			
			if(out != null) {
				try {
					out.close();
				} 
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				
			//Log.d("IOIO UART", "Didnt work");
			//uart.close();
			//***********************************************************************************
			*/
			try {
				Thread.sleep(100);
			} 
			catch (InterruptedException e) {
				
			}
		}
	}
	
	
	private void shutdownService()
    {
        if (timer != null) timer.cancel();
    }

    public void onDestroy() 
    {     
        super.onDestroy();
        shutdownService();
    }
	
	
	/**
	 * A method to create our IOIO thread.
	 * 
	 * @see ioio.lib.util.AbstractIOIOActivity#createIOIOThread()
	 */
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drink_selection);
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.homeScreen);
		rl.setBackgroundResource(R.drawable.backgroundbeerbottlevertical);
		accountDB = new AABDatabaseManager(this);
		
		retrieveRow();
		if(firstRun){
			Log.d("First run:", "true");
			Intent account = new Intent(this, AccountActivity.class);
			startActivity(account);
			firstRun = false;
		}
		if (gend.equals("Male")) GenCon = .68;
		else GenCon = .55;
		myBAC = (TextView)findViewById(R.id.myBACField);
		myTTS = (TextView)findViewById(R.id.myTTSField);
		
		BACTimer = new Timer();
		BACTimer.scheduleAtFixedRate(new TimerTask() {
			
		    @Override
		    public void run() {
		        //Called each time when 1000 milliseconds (1 second) (the period parameter)
		    	runOnUiThread(new Runnable() {
		    		@Override
		    		public void run() {
		    			BAC();
		    			Log.d("BAC Updated", "true");
		    		}
		    	});
		    }
		         
		},
		//Set how long before to start calling the TimerTask (in milliseconds)
		0,
		//Set the amount of time between each execution (in milliseconds)
		5000);
		
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.drink_selection, menu);
		return true;
	}
	
	public void screwDriver(View view){
		showDialog(1);
	}
	
	public void cubaLibre(View view){
		showDialog(2);
	}
	
	public void shirleyTemple(View view){
		showDialog(3);
	}
	
	public void martini(View view){
		showDialog(4);
	}
	public void pInfo(View view){
		//goto account activity
		Intent account = new Intent(this, AccountActivity.class);
		startActivity(account);
	}
	
	public void goToBarTab (View view){
		//Intent tab = new Intent(this, .class);
		//startActivity(tab);
		
		try{				//opens the MapActivity
			Intent tab = new Intent(this, BarTabActivity.class);
			final int result = 1;
			startActivityForResult(tab, result);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	//waits for result from MapActivity
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		  if (requestCode == 1) {

		     if(resultCode == RESULT_OK){      	//result ok if a location was set
		    	 retrieveRow();	
		    	 //retrieveDrinkRow();	
		     }
		     if (resultCode == RESULT_CANCELED) {    	//result cancelled if no location was set
		         //Write your code if there's no result
		    	 Toast toast = Toast.makeText(DrinkSelection.this,
							"pay tab cancelled",			//pop up message
							Toast.LENGTH_LONG);
					toast.show();
		     }
		  }
		}
	
	public void statistics(View view){
		Intent stats = new Intent(this, StatsActivity.class);
		startActivity(stats);
	}
	
	public void manualControls(View view){
		Intent manual = new Intent(this, ManualActivity.class);
		startActivity(manual);
	}
	
	public void BAC(){	
		
		//if(drinkNum != oldDrkNum){
		retrieveRow();
		double hours = 0;
		
		BAC = 0;
		for(drinkIndex = 1; drinkIndex<=drinkNum; drinkIndex++){
			retrieveDrinkRow();
			hours = (System.currentTimeMillis() - timestmp)/3600000;
			drinkBAC = ((acVol * 5.14)/(weight * GenCon)) - (.015 * hours);
			if(drinkBAC<0) drinkBAC = 0;
			BAC += drinkBAC;
			//oldDrkNum = drinkNum;
		}
		
		if (BAC < 0){
			BAC = 0;
		}
		
		TTS = (BAC-targetBAC)/.015;
		timeCalc();
		
		BigDecimal roundBAC = new BigDecimal(BAC);
		BigDecimal roundTTS = new BigDecimal(TTS);
		roundBAC = roundBAC.setScale(3, BigDecimal.ROUND_CEILING);
		roundTTS = roundTTS.setScale(3, BigDecimal.ROUND_CEILING);
		//myBAC.setText("out = " + outNum + "in = " + input);			//Double.toString(roundBAC.doubleValue())
		//myBAC.setText("0.00");
		myBAC.setText("" + roundBAC);
		
		if(mins<10){
			myTTS.setText(hrs + ":0" + mins + " " + amPm + " (" + (int)(TTS) + " hours, " + (int)(60 * (((int)TTS) - TTS)) + " min)");
		}
		else{
			myTTS.setText(hrs + ":" + mins + " " + amPm + " (" + (int)(TTS) + " hours, " + (int)(60 * (((int)TTS) - TTS)) + " min)");
		}
		
		Log.i("BAC Calc", "# = " + drinkIndex + ", BAC= " + BAC + 
				",\nhrs= " + hours + ", cnt= " + drinkNum);
		//}
		/*
		Toast.makeText(getApplicationContext(), "\nmyBAC: " + BAC + "\nhours: " + hours
				+ "\nDrink count: " + drinkNum,
        		Toast.LENGTH_LONG).show();
        */
	}
	
    public void timeCalc(){
    	hrs = (int)(TTS);
        //hrsRound = new BigDecimal(hrs);
        //hrsRound = hrsRound.setScale(0, BigDecimal.ROUND_FLOOR);
        	
        mins = (int)(60 * (TTS - hrs));
        //minsRound = new BigDecimal(mins);
        //minsRound = minsRound.setScale(0, BigDecimal.ROUND_CEILING);
        
        today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		
		hrs += today.hour;
		if(hrs>23){
			hrs -= 23;
		}
		
		mins += today.minute;
		if(mins>59){
			mins -= 59;
			hrs++;
		}
		
		amPm = "AM";
				
		if(hrs == 0) hrs = 12;
		else if(hrs > 12) {
			hrs -= 12;
			amPm = "PM";
		}
    }	
	
	protected Dialog onCreateDialog(int id)
    {
    
	final AlertDialog.Builder alert = new AlertDialog.Builder(this);    
    LinearLayout lila1= new LinearLayout(this);
    /*
	if(test){
		test=false;
	}
	else{
		test=true;
	}
    */
    if(id == 1){
    	lila1.setOrientation(LinearLayout.VERTICAL); //1 is for vertical orientation
	    
	    alert.setView(lila1);

	        alert.setIcon(R.drawable.ic_launcher);
	        alert.setTitle(sDTitle);
	        alert.setMessage(sDMsg);

	        //does this when the Yes button is pressed
	        alert.setPositiveButton("Select", new DialogInterface.OnClickListener() {             
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	dSelec = 1;
	            	Log.i("Drink Selection Status", "Drink selection:" + dSelec);
	            	acVol = acVolSD;
	            	timestmp = System.currentTimeMillis();
	            	drinkCost = sDCost;
	            	drinkName = sDTitle;
	            	drinkNum++;
	            	addDrinkRow();
	            	if(tabStat.equals("ia")){
	            		tabStat = "ip";
	            	}
	            	updateRow();
	            	if(orderSD){
	            		orderSD=false;
	            	}
	            	else{
	            		orderSD=true;
	            	}
	        		BAC();
	        		outNum = 1;
	            	//Toast.makeText(getApplicationContext(), "test = " + test,
	                //		Toast.LENGTH_LONG).show();
	            	/*
	            	try {
						LED.loop();
					} catch (ConnectionLostException e) {
						e.printStackTrace();
					}
	            	*/
	            }                     });                 
	        alert.setNegativeButton("Cancel",          //close the dialog if the cancel button is pressed        
	                new DialogInterface.OnClickListener() {                           
	            public void onClick(DialogInterface dialog, int whichButton) {          
	                //add code to handle cancel of update or cancel of initial set up
	            	dialog.cancel();  
	            }     });  
    }
    else if(id == 2){
    	lila1.setOrientation(LinearLayout.VERTICAL); //1 is for vertical orientation
	    
	    alert.setView(lila1);

	        alert.setIcon(R.drawable.ic_launcher);
	        alert.setTitle(cLTitle);
	        alert.setMessage(cLMsg);

	        //does this when the Yes button is pressed
	        alert.setPositiveButton("Select", new DialogInterface.OnClickListener() {             
	            public void onClick(DialogInterface dialog, int whichButton) { 
	            	dSelec = 2;
	            	Log.i("Drink Selection Status", "Drink selection:" + dSelec);
	            	acVol = acVolCL;
	            	timestmp = System.currentTimeMillis();
	            	drinkCost = cLCost;
	            	drinkName = cLTitle;
	            	drinkNum++;
	            	addDrinkRow();
	            	if(tabStat.equals("ia")){
	            		tabStat = "ip";
	            	}
	            	updateRow();
	            	if(orderCL){
	            		orderCL=false;
	            	}
	            	else{
	            		orderCL=true;
	            	}
	        		BAC();
	        		outNum = 2;
	            }                     });                 
	        alert.setNegativeButton("Cancel",          //close the dialog if the cancel button is pressed        
	                new DialogInterface.OnClickListener() {                           
	            public void onClick(DialogInterface dialog, int whichButton) {          
	                //add code to handle cancel of update or cancel of initial set up
	            	dialog.cancel();  
	            }     });  
    }
    else if(id == 3){
    	lila1.setOrientation(LinearLayout.VERTICAL); //1 is for vertical orientation
	    
	    alert.setView(lila1);

	        alert.setIcon(R.drawable.ic_launcher);
	        alert.setTitle(sTTitle);
	        alert.setMessage(sTMsg);

	        //does this when the Yes button is pressed
	        alert.setPositiveButton("Select", new DialogInterface.OnClickListener() {             
	            public void onClick(DialogInterface dialog, int whichButton) {   
	            	dSelec = 3;
	            	Log.i("Drink Selection Status", "Drink selection:" + dSelec);
	            	acVol = acVolST;
	            	timestmp = System.currentTimeMillis();
	            	drinkCost = sTCost;
	            	drinkName = sTTitle;
	            	drinkNum++;
	            	addDrinkRow();
	            	if(tabStat.equals("ia")){
	            		tabStat = "ip";
	            	}
	            	updateRow();
	            	if(orderST){
	            		orderST=false;
	            	}
	            	else{
	            		orderST=true;
	            	}
	        		BAC();
	        		outNum = 3;
	            }                     });                 
	        alert.setNegativeButton("Cancel",          //close the dialog if the cancel button is pressed        
	                new DialogInterface.OnClickListener() {                           
	            public void onClick(DialogInterface dialog, int whichButton) {          
	                //add code to handle cancel of update or cancel of initial set up
	            	dialog.cancel();  
	            }     });  
    }
    else if(id == 4){
    	lila1.setOrientation(LinearLayout.VERTICAL); //1 is for vertical orientation
	    
	    alert.setView(lila1);

	        alert.setIcon(R.drawable.ic_launcher);
	        alert.setTitle(mTTitle);
	        alert.setMessage(mTMsg);

	        //does this when the Yes button is pressed
	        alert.setPositiveButton("Select", new DialogInterface.OnClickListener() {             
	            public void onClick(DialogInterface dialog, int whichButton) {   
	            	dSelec = 4;
	            	Log.i("Drink Selection Status", "Drink selection:" + dSelec);
	            	acVol = acVolMT;
	            	timestmp = System.currentTimeMillis();
	            	drinkCost = mTCost;
	            	drinkName = mTTitle;
	            	drinkNum++;
	            	addDrinkRow();
	            	if(tabStat.equals("ia")){
	            		tabStat = "ip";
	            	}
	            	updateRow();
	            	if(orderMT){
	            		orderMT=false;
	            	}
	            	else{
	            		orderMT=true;
	            	}
	        		BAC();
	        		outNum = 4;
	            }                     });                 
	        alert.setNegativeButton("Cancel",          //close the dialog if the cancel button is pressed        
	                new DialogInterface.OnClickListener() {                           
	            public void onClick(DialogInterface dialog, int whichButton) {          
	                //add code to handle cancel of update or cancel of initial set up
	            	dialog.cancel();  
	            }     });  
    }

        return alert.create();      
        }
/*	
	public void addRow()
    {	
    		//if(dBChooser == 1){
    		try{
    		// ask the database manager to add a row given the 4 employee strings
    		accountDB.addRow
    		(
    				index = 0,
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
    				gend.toString()
    		);
    		}
    		catch (Exception e)
        	{
        		Log.e("Add Account Error", e.toString());
        		e.printStackTrace();
        	}
    		//}
    		
    	}
    */
/**
 * updates a row with the most current information
 */
public void updateRow()
{
	try
	{
		// ask the database manager to update the row based on the information
		// found in the corresponding user entry fields
		//if(dBChooser == 1){
		accountDB.updateRow
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
		Log.e("Update Account Error", e.toString());
		//e.printStackTrace();
		}
}

/**
 * retrieves a row from the database with the index which should always be 0
 */
public void retrieveRow()
{
	ArrayList<Object> row;
		//when dBChooser is 1, retrieve the user account
		
			try
			{
		// ask the database manager to retrieve the row with the given index
		row = accountDB.getRowAsArray("0");	
		
		// update the form fields to hold the retrieved data1
		//String index = (String)row.get(0);
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
		
		firstRun = false;
		}
		
	catch (Exception e)
	{
		Log.e("Retrieve Account Error1", e.toString());
		//e.printStackTrace();
		firstRun = true;
	}
}

public void addDrinkRow()
{
		//if(dBChooser == 1){
		try{
		// ask the database manager to add a row
		Log.i("Add Drink Row", "index 1: " + drinkNum);
		accountDB.addDrinkRow
		(
				drinkNum,
				drinkName.toString(),
				Double.toString(acVol),
				Double.toString(drinkCost),
				Double.toString(timestmp)
		);
		Log.i("Add Drink Row", "index 2: " + drinkNum);
		}
		catch (Exception e)
    	{
    		Log.e("Add Drink Error", e.toString());
    		//e.printStackTrace();
    	}
		//}
		
	}

public void retrieveDrinkRow()
{
	ArrayList<Object> row;
		//when dBChooser is 1, retrieve the user account
		
		try{
		// ask the database manager to retrieve the row with the given index
		//Log.i("Retrieve Drink Row", "index 1: " + drinkIndex);
		row = accountDB.getDrinkRowAsArray(Integer.toString(drinkIndex));	
		
		// update the form fields to hold the retrieved data1
		drinkName = (String)row.get(1);
		acVol = Double.parseDouble((String)row.get(2));  //acVol wants double 
		drinkCost = Double.parseDouble((String)row.get(3));
		timestmp = Double.parseDouble((String)row.get(4));
		
		firstRun = false;
		}
	catch (Exception e)
	{
		Log.e("Retrieve Error drink!!!", e.toString());
		//e.printStackTrace();
		firstRun = true;
	}	
}
}
