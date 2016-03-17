package com.example.bartendingrobot;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class AccountActivity extends Activity {

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
	public int weight = 90;					//spinner
	String gend = "";					//spinner

	EditText 	
	mnameField, 
	memailField, 
	maddressField, 
	mcityField, 
	mzipField;
	
	boolean firstRun = true;
	int index = 0;
	int dBChooser = 1;
	
	public int drinkNum = 0;
	public int drinkStrt = 0;
	public String tabStat = "i";		//ip = in progress
	
	AABDatabaseManager accountDB;
	
	//=========================================================================== Spinner inits
	/**
     * Fields to contain the current position and display contents of the spinner
     */
    protected int sPos;
    protected String sSelection;
    
    protected int wPos;
    protected String wSelection;
    
    protected int gPos;
    protected String gSelection;

    /**
     * ArrayAdapter connects the spinner widget to array-based data.
     */
    protected ArrayAdapter<CharSequence> sAdapter;
    protected ArrayAdapter<CharSequence> wAdapter;
    protected ArrayAdapter<CharSequence> gAdapter;

    /**
     *  The initial position of the spinner when it is first installed.
     */
    public static final int DEFAULT_POSITION = 0;

    /**
     * The name of a properties file that stores the position and
     * selection when the activity is not loaded.
     */
    public static final String PREFERENCES_FILE = "SpinnerPrefs";

    /**
     * These values are used to read and write the properties file.
     * PROPERTY_DELIMITER delimits the key and value in a Java properties file.
     * The "marker" strings are used to write the properties into the file
     */
    public static final String PROPERTY_DELIMITER = "=";

    /**
     * The key or label for "position" in the preferences file
     */
    public static final String POSITION_KEY = "Position";

    /**
     * The key or label for "selection" in the preferences file
     */
    public static final String SELECTION_KEY = "Selection";

    public static final String POSITION_MARKER =
            POSITION_KEY + PROPERTY_DELIMITER;

    public static final String SELECTION_MARKER =
            SELECTION_KEY + PROPERTY_DELIMITER;

    /**
     * Initializes the application and the activity.
     * 1) Sets the view
     * 2) Reads the spinner's backing data from the string resources file
     * 3) Instantiates a callback listener for handling selection from the
     *    spinner
     * Notice that this method includes code that can be uncommented to force
     * tests to fail.
     *
     * This method overrides the default onCreate() method for an Activity.
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
	//===========================================================================
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		
		accountDB = new AABDatabaseManager(this);
		
		mnameField= 	(EditText)findViewById(R.id.nameField);
        memailField= 	(EditText)findViewById(R.id.emailField);
        maddressField= 	(EditText)findViewById(R.id.addressField);
        mcityField=		(EditText)findViewById(R.id.cityField);
        mzipField=		(EditText)findViewById(R.id.zipField);
        
        Log.i("on create", "position 1");
        retrieveRow();
        Log.i("on create", "position 2");
        if(!firstRun){
        	Log.i("on create", "not first run");
         mnameField.setText(name); 
         memailField.setText(email);
         maddressField.setText(address);
         mcityField.setText(city);
         mzipField.setText(zip);  
         setSSpinnerSelection(state);
         setWSpinnerSelection(weight);
         setGSpinnerSelection(gend);
         Log.i("on create", "set fields");
         }  

		//=========================================================================== spinner
		 Spinner sSpinner = (Spinner) findViewById(R.id.stateSpinner);
		 Spinner wSpinner = (Spinner) findViewById(R.id.weightSpinner);
		 Spinner gSpinner = (Spinner) findViewById(R.id.genderSpinner);

	     /*
	      * Create a backing mLocalAdapter for the Spinner from a list of the1
	      * planets. The list is defined by XML in the strings.xml file.
	      */

	     this.sAdapter = ArrayAdapter.createFromResource(this, R.array.StateArray,
	             android.R.layout.simple_spinner_dropdown_item);
	     this.wAdapter = ArrayAdapter.createFromResource(this, R.array.WeightArray,
	             android.R.layout.simple_spinner_dropdown_item);
	     this.gAdapter = ArrayAdapter.createFromResource(this, R.array.GenderArray,
	             android.R.layout.simple_spinner_dropdown_item);

	     /*
	      * Attach the mLocalAdapter to the spinner.
	      */

	     sSpinner.setAdapter(this.sAdapter);
	     wSpinner.setAdapter(this.wAdapter);
	     gSpinner.setAdapter(this.gAdapter);

	     /*
	      * Create a listener that is triggered when Android detects the
	      * user has selected an item in the Spinner.
	      */

	     OnItemSelectedListener sSpinnerListener = new myOnSItemSelectedListener(this,this.sAdapter);
	     OnItemSelectedListener wSpinnerListener = new myOnWItemSelectedListener(this,this.wAdapter);
	     OnItemSelectedListener gSpinnerListener = new myOnGItemSelectedListener(this,this.gAdapter);

	     /*
	      * Attach the listener to the Spinner.
	      */

	     sSpinner.setOnItemSelectedListener(sSpinnerListener);
	     wSpinner.setOnItemSelectedListener(wSpinnerListener);
	     gSpinner.setOnItemSelectedListener(gSpinnerListener);

	   //=========================================================================== end spinner
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.account, menu);
		return true;
	}

	//===================================================================================== Spinner





 /**
  *  A callback listener that implements the
  *  {@link android.widget.AdapterView.OnItemSelectedListener} interface
  *  For views based on adapters, this interface defines the methods available
  *  when the user selects an item from the View.
  *
  */
 public class myOnSItemSelectedListener implements OnItemSelectedListener {

     /*
      * provide local instances of the mLocalAdapter and the mLocalContext
      */

     ArrayAdapter<CharSequence> sLocalAdapter;
     Activity sLocalContext;

     /**
      *  Constructor
      *  @param c - The activity that displays the Spinner.
      *  @param ad - The Adapter view that
      *    controls the Spinner.
      *  Instantiate a new listener object.
      */
     public myOnSItemSelectedListener(Activity c, ArrayAdapter<CharSequence> ad) {

       this.sLocalContext = c;
       this.sLocalAdapter = ad;

     }

     /**
      * When the user selects an item in the spinner, this method is invoked by the callback
      * chain. Android calls the item selected listener for the spinner, which invokes the
      * onItemSelected method.
      *
      * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(
      *  android.widget.AdapterView, android.view.View, int, long)
      * @param parent - the AdapterView for this listener
      * @param v - the View for this listener
      * @param pos - the 0-based position of the selection in the mLocalAdapter
      * @param row - the 0-based row number of the selection in the View
      */
     public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {

         AccountActivity.this.sPos = pos;
         AccountActivity.this.sSelection = parent.getItemAtPosition(pos).toString();
         /*
          * Set the value of the text field in the UI
          */
         //TextView resultText = (TextView)findViewById(R.id.SpinnerResult);
         //resultText.setText(SpinnerActivity.this.mSelection);
     }

     /**
      * The definition of OnItemSelectedListener requires an override
      * of onNothingSelected(), even though this implementation does not use it.
      * @param parent - The View for this Listener
      */
     public void onNothingSelected(AdapterView<?> parent) {

         // do nothing

     }
 }
 
 /**
  *  A callback listener that implements the
  *  {@link android.widget.AdapterView.OnItemSelectedListener} interface
  *  For views based on adapters, this interface defines the methods available
  *  when the user selects an item from the View.
  *
  */
 public class myOnWItemSelectedListener implements OnItemSelectedListener {

     /*
      * provide local instances of the mLocalAdapter and the mLocalContext
      */

     ArrayAdapter<CharSequence> wLocalAdapter;
     Activity wLocalContext;

     /**
      *  Constructor
      *  @param c - The activity that displays the Spinner.
      *  @param ad - The Adapter view that
      *    controls the Spinner.
      *  Instantiate a new listener object.
      */
     public myOnWItemSelectedListener(Activity c, ArrayAdapter<CharSequence> ad) {

       this.wLocalContext = c;
       this.wLocalAdapter = ad;

     }

     /**
      * When the user selects an item in the spinner, this method is invoked by the callback
      * chain. Android calls the item selected listener for the spinner, which invokes the
      * onItemSelected method.
      *
      * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(
      *  android.widget.AdapterView, android.view.View, int, long)
      * @param parent - the AdapterView for this listener
      * @param v - the View for this listener
      * @param pos - the 0-based position of the selection in the mLocalAdapter
      * @param row - the 0-based row number of the selection in the View
      */
     public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {

         AccountActivity.this.wPos = pos;
         AccountActivity.this.wSelection = parent.getItemAtPosition(pos).toString();
         /*
          * Set the value of the text field in the UI
          */
         //TextView resultText = (TextView)findViewById(R.id.SpinnerResult);
         //resultText.setText(SpinnerActivity.this.mSelection);
     }

     /**
      * The definition of OnItemSelectedListener requires an override
      * of onNothingSelected(), even though this implementation does not use it.
      * @param parent - The View for this Listener
      */
     public void onNothingSelected(AdapterView<?> parent) {

         // do nothing

     }
 }
 
 /**
  *  A callback listener that implements the
  *  {@link android.widget.AdapterView.OnItemSelectedListener} interface
  *  For views based on adapters, this interface defines the methods available
  *  when the user selects an item from the View.
  *
  */
 public class myOnGItemSelectedListener implements OnItemSelectedListener {

     /*
      * provide local instances of the mLocalAdapter and the mLocalContext
      */

     ArrayAdapter<CharSequence> gLocalAdapter;
     Activity gLocalContext;

     /**
      *  Constructor
      *  @param c - The activity that displays the Spinner.
      *  @param ad - The Adapter view that
      *    controls the Spinner.
      *  Instantiate a new listener object.
      */
     public myOnGItemSelectedListener(Activity c, ArrayAdapter<CharSequence> ad) {

       this.gLocalContext = c;
       this.gLocalAdapter = ad;

     }

     /**
      * When the user selects an item in the spinner, this method is invoked by the callback
      * chain. Android calls the item selected listener for the spinner, which invokes the
      * onItemSelected method.
      *
      * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(
      *  android.widget.AdapterView, android.view.View, int, long)
      * @param parent - the AdapterView for this listener
      * @param v - the View for this listener
      * @param pos - the 0-based position of the selection in the mLocalAdapter
      * @param row - the 0-based row number of the selection in the View
      */
     public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {

         AccountActivity.this.gPos = pos;
         AccountActivity.this.gSelection = parent.getItemAtPosition(pos).toString();
         /*
          * Set the value of the text field in the UI
          */
         //TextView resultText = (TextView)findViewById(R.id.SpinnerResult);
         //resultText.setText(SpinnerActivity.this.mSelection);
     }

     /**
      * The definition of OnItemSelectedListener requires an override
      * of onNothingSelected(), even though this implementation does not use it.
      * @param parent - The View for this Listener
      */
     public void onNothingSelected(AdapterView<?> parent) {

         // do nothing

     }
 }

 /**
  * Restores the current state of the spinner (which item is selected, and the value
  * of that item).
  * Since onResume() is always called when an Activity is starting, even if it is re-displaying
  * after being hidden, it is the best place to restore state.
  *
  * Attempts to read the state from a preferences file. If this read fails,
  * assume it was just installed, so do an initialization. Regardless, change the
  * state of the spinner to be the previous position.
  *
  * @see android.app.Activity#onResume()
  */
 @Override
 public void onResume() {

     /*
      * an override to onResume() must call the super constructor first.
      */

     super.onResume();

     /*
      * Try to read the preferences file. If not found, set the state to the desired initial
      * values.
      */

     if (!readInstanceState(this)) setInitialState();

     /*
      * Set the spinner to the current state.
      */

     Spinner restoreSpinner = (Spinner)findViewById(R.id.stateSpinner);
     restoreSpinner.setSelection(getSSpinnerPosition());
     
     restoreSpinner = (Spinner)findViewById(R.id.weightSpinner);
     restoreSpinner.setSelection(getWSpinnerPosition());
     
     restoreSpinner = (Spinner)findViewById(R.id.genderSpinner);
     restoreSpinner.setSelection(getGSpinnerPosition());

 }

 /**
  * Store the current state of the spinner (which item is selected, and the value of that item).
  * Since onPause() is always called when an Activity is about to be hidden, even if it is about
  * to be destroyed, it is the best place to save state.
  *
  * Attempt to write the state to the preferences file. If this fails, notify the user.
  *
  * @see android.app.Activity#onPause()
  */
 @Override
 public void onPause() {

     /*
      * an override to onPause() must call the super constructor first.
      */

     super.onPause();

     /*
      * Save the state to the preferences file. If it fails, display a Toast, noting the failure.
      */

     if (!writeInstanceState(this)) {
          Toast.makeText(this,
                  "Failed to write state!", Toast.LENGTH_LONG).show();
       }
 }

 /**
  * Sets the initial state of the spinner when the application is first run.
  */
 public void setInitialState() {

     this.sPos = DEFAULT_POSITION;
     this.wPos = DEFAULT_POSITION;
     this.gPos = DEFAULT_POSITION;

 }

 /**
  * Read the previous state of the spinner from the preferences file
  * @param c - The Activity's Context
  */
 public boolean readInstanceState(Context c) {

     /*
      * The preferences are stored in a SharedPreferences file. The abstract implementation of
      * SharedPreferences is a "file" containing a hashmap. All instances of an application
      * share the same instance of this file, which means that all instances of an application
      * share the same preference settings.
      */

     /*
      * Get the SharedPreferences object for this application
      */

     SharedPreferences p = c.getSharedPreferences(PREFERENCES_FILE, MODE_WORLD_READABLE);
     /*
      * Get the position and value of the spinner from the file, or a default value if the
      * key-value pair does not exist.
      */
     this.sPos = p.getInt(POSITION_KEY, AccountActivity.DEFAULT_POSITION);
     this.sSelection = p.getString(SELECTION_KEY, "");
     
     this.wPos = p.getInt(POSITION_KEY, AccountActivity.DEFAULT_POSITION);
     this.wSelection = p.getString(SELECTION_KEY, "");
     
     this.gPos = p.getInt(POSITION_KEY, AccountActivity.DEFAULT_POSITION);
     this.gSelection = p.getString(SELECTION_KEY, "");

     /*
      * SharedPreferences doesn't fail if the code tries to get a non-existent key. The
      * most straightforward way to indicate success is to return the results of a test that
      * SharedPreferences contained the position key.
      */

       return (p.contains(POSITION_KEY));

     }

 /**
  * Write the application's current state to a properties repository.
  * @param c - The Activity's Context
  *
  */
 public boolean writeInstanceState(Context c) {

     /*
      * Get the SharedPreferences object for this application
      */

     SharedPreferences p =
             c.getSharedPreferences(AccountActivity.PREFERENCES_FILE, MODE_WORLD_READABLE);

     /*
      * Get the editor for this object. The editor interface abstracts the implementation of
      * updating the SharedPreferences object.
      */

     SharedPreferences.Editor e = p.edit();

     /*
      * Write the keys and values to the Editor
      */

     e.putInt(POSITION_KEY, this.sPos);
     e.putString(SELECTION_KEY, this.sSelection);
     
     e.putInt(POSITION_KEY, this.wPos);
     e.putString(SELECTION_KEY, this.wSelection);
     
     e.putInt(POSITION_KEY, this.gPos);
     e.putString(SELECTION_KEY, this.gSelection);

     /*
      * Commit the changes. Return the result of the commit. The commit fails if Android
      * failed to commit the changes to persistent storage.
      */

     return (e.commit());

 }

//get spin position =============================
 public int getSSpinnerPosition() {
     return this.sPos;
 }
 
 public int getWSpinnerPosition() {
     return this.wPos;
 }
 
 public int getGSpinnerPosition() {
     return this.gPos;
 }

 //Set spin position ========================
 public void setSSpinnerPosition(int pos) {
     this.sPos = pos;
 }

 public void setWSpinnerPosition(int pos) {
     this.wPos = pos;
 }
 
 public void setGSpinnerPosition(int pos) {
     this.gPos = pos;
 }
 
 //get spin selection =======================
 public String getSSpinnerSelection() {
     return this.sSelection;
 }
 
 public String getWSpinnerSelection() {
     return this.wSelection;
 }
 
 public String getGSpinnerSelection() {
     return this.gSelection;
 }

 //set spin selection =======================
 public void setSSpinnerSelection(String selection) {
     this.sSelection = selection;
 }
 
 public void setWSpinnerSelection(int selection) {
     this.wSelection = Integer.toString(selection);
 }
 
 public void setGSpinnerSelection(String selection) {
     this.gSelection = selection;
 }

 //===================================================================================== End spinner

 public void saveAccount(View view){
		
	name = mnameField.getText().toString().trim(); 
   	email = memailField.getText().toString().trim(); 
   	address = maddressField.getText().toString().trim();  
    city = mcityField.getText().toString().trim();
    zip = mzipField.getText().toString().trim();
    state = getSSpinnerSelection();
    weight = Integer.parseInt(getWSpinnerSelection());
    gend = getGSpinnerSelection();
	 
            //if true, add a new account
        if(firstRun){
        	addRow();
        }
            //if false, update the account with new information
        else{
            updateRow();
        }
 }
 
 public void exitAccount(View view){			//temporary activity used to check connection; remove after testing
	    //if true, add a new account
	 if(firstRun){
		 addRow();
	 }
	 	//if false, update the account with new information
	 else{
        updateRow();
	 }
	 finish();  			//close this activity if cancel is clicked
	}
 
 public void runPaymentDialog(View view){			//temporary activity used to check connection; remove after testing
		showDialog(0);
	}
	
	
	protected Dialog onCreateDialog(int i)
    {
    
	final AlertDialog.Builder alert = new AlertDialog.Builder(this);    
    LinearLayout lila1= new LinearLayout(this);

        lila1.setOrientation(LinearLayout.VERTICAL); //1 is for vertical orientation
        final EditText inputCCName = new EditText(this);
        final EditText inputCCNum = new EditText(this);
        final EditText inputCCExpDate = new EditText(this);		//transfer to spinner
        final EditText inputCCV = new EditText(this);			
        
        inputCCName.setHint("Name on Card");
        inputCCNum.setHint("Card Number");
        inputCCExpDate.setHint("Expiration Date");
        inputCCV.setHint("CCV");
        
        lila1.addView(inputCCName);
        lila1.addView(inputCCNum);
        lila1.addView(inputCCExpDate);
        lila1.addView(inputCCV);
        
        alert.setView(lila1);
        
           alert.setIcon(R.drawable.ic_launcher);
           alert.setTitle("Payment Information");
           
           retrieveRow();
           if(!firstRun){
           inputCCName.setText(cCName);
           inputCCNum.setText(cCNum);				
           inputCCExpDate.setText(cCExpDate);		
           inputCCV.setText(cCV);					
           
           }
            alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {             
                public void onClick(DialogInterface dialog, int whichButton) {              
                	
                	//if addNewEmployee is true, add a new employee record
                	
                	name = mnameField.getText().toString().trim(); 
                   	email = memailField.getText().toString().trim(); 
                   	address = maddressField.getText().toString().trim();  
                    city = mcityField.getText().toString().trim();
                    zip = mzipField.getText().toString().trim();
                    state = getSSpinnerSelection();
                    weight = Integer.parseInt(getWSpinnerSelection());
                    gend = getGSpinnerSelection();
                    cCNum = inputCCNum.getText().toString().trim();				//
                    cCExpDate = inputCCExpDate.getText().toString().trim();		//
                    cCV = inputCCV.getText().toString().trim();					//
                	
                if(firstRun){
            		addRow();}
              //if addNewEmployee is false, update the existing employee record with new information
                else{
            		updateRow();
            		}
            		
                }                     });                 
            alert.setNegativeButton("Cancel",                  
                    new DialogInterface.OnClickListener() {                           
                public void onClick(DialogInterface dialog, int whichButton) {        
                	dialog.cancel();  		//if an employee record already exists, just return to the app
                }     });    
      
        return alert.create();      
       
    }
	
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
    				gend.toString(),
    				Integer.toString(drinkStrt),
    				Integer.toString(drinkNum),
    				tabStat.toString()
    		);
    		}
    		catch (Exception e)
        	{
        		Log.e("Add Employee Error", e.toString());
        		e.printStackTrace();
        	}
    		//}
    		
   /* 		
    		else{		//if the dBChooser equals 2, add a new audit record
    			//first find the time and date that the audit started
    			try{
    			today = new Time(Time.getCurrentTimezone());
    			today.setToNow();
    			
    			if(today.weekDay == 0) dayOfWeek = "Sunday";
    			else if(today.weekDay == 1) dayOfWeek = "Monday";
    			else if(today.weekDay == 2) dayOfWeek = "Tuesday";
    			else if(today.weekDay == 3) dayOfWeek = "Wednesday";
    			else if(today.weekDay == 4) dayOfWeek = "Thurday";
    			else if(today.weekDay == 5) dayOfWeek = "Friday";
    			else if(today.weekDay == 6) dayOfWeek = "Saturday";
    			else dayOfWeek = "didn't work!";			
    			
    			if(today.month == 0) monthOfYear = "January";
    			else if(today.month == 1) monthOfYear = "February";
    			else if(today.month == 2) monthOfYear = "March";
    			else if(today.month == 3) monthOfYear = "April";
    			else if(today.month == 4) monthOfYear = "May";
    			else if(today.month == 5) monthOfYear = "June";
    			else if(today.month == 6) monthOfYear = "July";
    			else if(today.month == 7) monthOfYear = "August";
    			else if(today.month == 8) monthOfYear = "September";
    			else if(today.month == 9) monthOfYear = "October";
    			else if(today.month == 10) monthOfYear = "November";
    			else if(today.month == 11) monthOfYear = "December";
    			else monthOfYear = "didn't work!";
    				
    				if(today.hour == 0)timeStarted = today.format("12:%M " + "AM");
    				else if(today.hour == 1)timeStarted = today.format("1:%M " + "AM");
    				else if(today.hour == 2)timeStarted = today.format("2:%M " + "AM");
    				else if(today.hour == 3)timeStarted = today.format("3:%M " + "AM");
    				else if(today.hour == 4)timeStarted = today.format("4:%M " + "AM");
    				else if(today.hour == 5)timeStarted = today.format("5:%M " + "AM");
    				else if(today.hour == 6)timeStarted = today.format("6:%M " + "AM");
    				else if(today.hour == 7)timeStarted = today.format("7:%M " + "AM");
    				else if(today.hour == 8)timeStarted = today.format("8:%M " + "AM");
    				else if(today.hour == 9)timeStarted = today.format("9:%M " + "AM");
    				else if(today.hour == 10)timeStarted = today.format("10:%M " + "AM");
    				else if(today.hour == 11)timeStarted = today.format("11:%M " + "AM");
    				else if(today.hour == 12)timeStarted = today.format("12:%M " + "PM");
    				else if(today.hour == 13)timeStarted = today.format("1:%M " + "PM");
    				else if(today.hour == 14) timeStarted = today.format("2:%M " + "PM");
    				else if(today.hour == 15) timeStarted = today.format("3:%M " + "PM");
    				else if(today.hour == 16) timeStarted = today.format("4:%M " + "PM");
    				else if(today.hour == 17) timeStarted = today.format("5:%M " + "PM");
    				else if(today.hour == 18) timeStarted = today.format("6:%M " + "PM");
    				else if(today.hour == 19) timeStarted = today.format("7:%M " + "PM");
    				else if(today.hour == 20) timeStarted = today.format("8:%M " + "PM");
    				else if(today.hour == 21) timeStarted = today.format("9:%M " + "PM");
    				else if(today.hour == 22) timeStarted = today.format("10:%M " + "PM");
    				else if(today.hour == 23) timeStarted = today.format("11:%M " + "PM");
    			
    			dateStarted = today.format(dayOfWeek + ", " + monthOfYear + " %d, %Y");
    			auditName = "TMP" + today.format("%m%d%Y") + today.format("%H%M%S");			//set a default audit name
    			auditStatus = "in progress";					//set the status to "in progress" because we started a new audit
    			
    			// ask the database manager to add a row given the 8 audit strings
        		auditDB.addRowPreAud
        		(
        				index,
        				auditName.toString(),
        				setAuditLocation.toString(),
        				hospitalEmail.toString(),
        				dateStarted.toString(),
        				timeStarted.toString(),
        				auditStatus.toString(),
        				auditComments.toString(),
        				deviceCount
        		);
    		}
    			catch (Exception e)
    	{
    		Log.e("Add audit record Error", e.toString());
    		e.printStackTrace();
    	}
    		}
 */
    	}
    
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
			row = accountDB.getRowAsArray("0");	
			
			// update the form fields to hold the retrieved data
			//index = (Long)row.get(0);
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
			
			if(index != 0){		//do nothing if the employee record already exists
			}
			firstRun = false;
			}
		catch (Exception e)
		{
			Log.e("Retrieve Error employee", e.toString());
			e.printStackTrace();
			
			firstRun = true;
		}	
		
	}
}

	