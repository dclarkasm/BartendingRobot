package com.example.bartendingrobot;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ToggleButton;

/**
 * This is the main activity of the HelloIOIO example application.
 * 
 * It displays a toggle button on the screen, which enables control of the
 * on-board LED. This example shows a very simple usage of the IOIO, by using
 * the {@link IOIOActivity} class. For a more advanced use case, see the
 * HelloIOIOPower example.
 */
public class ManualActivity extends IOIOActivity {
	private ImageButton forward, right, reverse, left, home, up, down;
	private Button sol1, sol2, sol3, sol4, sol5, sol6, sol7, sol8;
	private DigitalInput ackRec;
	boolean input;

	/**
	 * Called when the activity is first created. Here we normally initialize
	 * our GUI.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manual);
		forward = (ImageButton) findViewById(R.id.fwdButton);
		right = (ImageButton) findViewById(R.id.rightButton);
		reverse = (ImageButton) findViewById(R.id.revButton);
		left = (ImageButton) findViewById(R.id.leftButton);
		home = (ImageButton) findViewById(R.id.homebutton);
		
		up = (ImageButton) findViewById(R.id.upButton);
		down = (ImageButton) findViewById(R.id.downButton);
		sol1 = (Button) findViewById(R.id.sol1Button);
		sol2 = (Button) findViewById(R.id.sol2Button);
		sol3 = (Button) findViewById(R.id.sol3Button);
		sol4 = (Button) findViewById(R.id.sol4Button);
		sol5 = (Button) findViewById(R.id.sol5Button);
		sol6 = (Button) findViewById(R.id.sol6Button);
		sol7 = (Button) findViewById(R.id.sol7Button);
		sol8 = (Button) findViewById(R.id.sol8Button);
		//buttonLoop();
	}
/*
public void buttonLoop(){
		
		for(;;){
		if(forward.isPressed()){
			fwd=true;
		}
		else if(!.isPressed()){
			fwd=false;
		}
		if(rbutton.isPressed()){
			rit=true;
		}
		else if(!rbutton.isPressed()){
			rit=false;
		}
		if(dbutton.isPressed()){
			dwn=true;
		}
		else if(!dbutton.isPressed()){
			dwn=false;
		}
		if(lbutton.isPressed()){
			lft=true;
		}
		else if(!lbutton.isPressed()){
			lft=false;
		}
		}
		
	}
*/
	/**
	 * This is the thread on which all the IOIO activity happens. It will be run
	 * every time the application is resumed and aborted when it is paused. The
	 * method setup() will be called right after a connection with the IOIO has
	 * been established (which might happen several times!). Then, loop() will
	 * be called repetitively until the IOIO gets disconnected.
	 */
	class Looper extends BaseIOIOLooper {
		/** The on-board LED. */
		private DigitalOutput fwd_pin, rit_pin, rev_pin, lft_pin, 
		hom_pin, man_pin, up_pin, dwn_pin, sol1_pin, sol2_pin, sol3_pin, sol4_pin;

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
			man_pin = ioio_.openDigitalOutput(24, true);
			hom_pin = ioio_.openDigitalOutput(22, true);
			fwd_pin = ioio_.openDigitalOutput(18, true);
			rit_pin = ioio_.openDigitalOutput(19, true);
			rev_pin = ioio_.openDigitalOutput(20, true);
			lft_pin = ioio_.openDigitalOutput(21, true);
			
			up_pin = ioio_.openDigitalOutput(31, true);
			dwn_pin = ioio_.openDigitalOutput(32, true);
			sol1_pin = ioio_.openDigitalOutput(33, true);
			sol2_pin = ioio_.openDigitalOutput(41, true);
			sol3_pin = ioio_.openDigitalOutput(42, true);
			sol4_pin = ioio_.openDigitalOutput(43, true);
			//ackRec = ioio_.openDigitalInput(44);
		}

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
			fwd_pin.write(false);
			rit_pin.write(false);
			rev_pin.write(false);
			lft_pin.write(false);
			hom_pin.write(false);
			
			up_pin.write(false);
			dwn_pin.write(false);
			sol1_pin.write(false);
			sol2_pin.write(false);
			sol3_pin.write(false);
			sol4_pin.write(false);
			
			man_pin.write(true);
			
			fwd_pin.write(forward.isPressed());
			rit_pin.write(right.isPressed());
			rev_pin.write(reverse.isPressed());
			lft_pin.write(left.isPressed());
			hom_pin.write(home.isPressed());
			up_pin.write(up.isPressed());
			dwn_pin.write(down.isPressed());
			
			
			while(sol1.isPressed()){
				sol1_pin.write(true);
				sol2_pin.write(false);
				sol3_pin.write(false);
				sol4_pin.write(false);
			}
			while(sol2.isPressed()){
				sol1_pin.write(false);
				sol2_pin.write(true);
				sol3_pin.write(false);
				sol4_pin.write(false);
			}
			while(sol3.isPressed()){
				sol1_pin.write(true);
				sol2_pin.write(true);
				sol3_pin.write(false);
				sol4_pin.write(false);
			}
			while(sol4.isPressed()){
				sol1_pin.write(false);
				sol2_pin.write(false);
				sol3_pin.write(true);
				sol4_pin.write(false);
			}
			while(sol5.isPressed()){
				sol1_pin.write(true);
				sol2_pin.write(false);
				sol3_pin.write(true);
				sol4_pin.write(false);
			}
			while(sol6.isPressed()){
				sol1_pin.write(false);
				sol2_pin.write(true);
				sol3_pin.write(true);
				sol4_pin.write(false);
			}
			while(sol7.isPressed()){
				sol1_pin.write(true);
				sol2_pin.write(true);
				sol3_pin.write(true);
				sol4_pin.write(false);
			}
			while(sol8.isPressed()){
				sol1_pin.write(false);
				sol2_pin.write(false);
				sol3_pin.write(false);
				sol4_pin.write(true);
			}
			
				
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
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
}
