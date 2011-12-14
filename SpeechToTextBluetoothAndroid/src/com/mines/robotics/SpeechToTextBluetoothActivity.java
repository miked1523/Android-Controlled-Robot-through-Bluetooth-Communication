package com.mines.robotics;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import at.abraxas.amarino.Amarino;

public class SpeechToTextBluetoothActivity extends Activity {
	
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1523;
	private String m_DEVICE_ADDRESS = "";
	private boolean m_Connected = false;
	
	// signals to robots and their human readable equivalent
	private static final char DRIVE = 'D';
	private static final char LEFT = 'L';
	private static final char RIGHT = 'R';
	private static final char STOP = 'S';
	private static final char SPEAK = 'T';
	private static final char REVERSE = 'E';
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Get display items for later interaction
        Button speakButton = (Button) findViewById(R.id.speech_btn);
        
        // Check to see if a recognition activity is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() != 0) {

        } else {
            speakButton.setEnabled(false);
            speakButton.setText("Recognizer not present");
        }
        
        // load last device setting
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	EditText deviceId = (EditText)this.findViewById(R.id.device_id);
    	deviceId.setText(prefs.getString("DEVICE_ID", "Enter Device Id"));
    }
    
    /**
     * Handle the results from the recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
        	String strOutput = "";
            TextView output = (TextView)this.findViewById(R.id.txt_output);    
            // Fill the list view with the strings the recognizer thought it could have heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            for( int i = 0; i < matches.size(); i++ ) {
            	strOutput = matches.get(i).toString();  
            }
            String[] commands = {"drive","left","right","stop","speak","reverse"};
            char[] commandCodes = {'D','L','R','S','T','E'};
            String wordChoice = strOutput;
            int wordToSend = -1;
            
            // loop through google's word choices and see if any of them match a command
            for(int i = 0; i < matches.size(); i++) {
            	String match = matches.get(i);
            	for(int j = 0; j < commands[i].length(); j++) {
            		// if we find a match, select the command character and exit loop
            		if( match.equals(commands[j])) {
            			wordChoice = commands[j];
            			wordToSend = j;
            		}
            	}
            
            }
            
            // old algorithm
     //       float maxProb = 0f;
            
            /* loop through each command word and determine which word most resembles
            // the intended command
            for(int i = 0; i < commands.length; i++) {
            	int counter = 0;
            	// sum all letters in common
            	for(int j = 0; j < commands[i].length(); j++) {
            		if( commands[i].contains(strOutput.substring(j,j+1)) ) {
            			counter++;
            			// place more weight on the first letter of the word
            			if( j == 0 ) {
            				counter = counter + 3;
            			}
            		}
            	}
            	// find probability that the word matches
            	float prob = (float)counter/(float)commands[i].length();
            	wordChoice = wordChoice + " " + prob;
            	// update the most probable word
            	if( prob > maxProb ) {
            		wordChoice = wordChoice + " " + commands[i];
            		maxProb = prob;
            		wordToSend = i;
            	}
            }
            
            // hard coded exception
            if(strOutput.equals("write")) {
            	wordChoice = wordChoice + " " + "right";
            	wordToSend = 2;
            }*/

            if( wordToSend != -1 ) {
	            // output results
	        	output.setText(wordChoice);
	        	
	    		// I have chosen random small letters for the flag 'c' for sending a command to the robot
	    		// you could select any small letter you want
	    		// however be sure to match the character you register a function for your in Arduino sketch
	    		if(m_Connected) {
	    			Amarino.sendDataToArduino(this, m_DEVICE_ADDRESS, 'A', commandCodes[wordToSend]);
	    		}
            }
            else {
            	// output failed results
	        	output.setText("No matching command");          	
            }
        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    /**
     * Return to login screen
     */
    public void speechConvertButton(View v) {
        // call speech to text activity
    	startVoiceRecognitionActivity();
    }
    
    /**
     * Send signal to drive robot
     */
    public void driveButton(View v) {
    	if(m_Connected) {
    		Amarino.sendDataToArduino(this, m_DEVICE_ADDRESS, 'A', DRIVE);
    	}
    }
    
    /**
     * Send signal to have robot turn left
     */
    public void leftButton(View v) {
    	if(m_Connected) {
    		Amarino.sendDataToArduino(this, m_DEVICE_ADDRESS, 'A', LEFT);
    	}
    }
    
    /**
     * Send signal to have robot turn right
     */
    public void rightButton(View v) {
    	if(m_Connected) {
    		Amarino.sendDataToArduino(this, m_DEVICE_ADDRESS, 'A', RIGHT);
    	}
    }
    
    /**
     * Send signal to stop the robot
     */
    public void stopButton(View v) {
    	if(m_Connected) {
    		Amarino.sendDataToArduino(this, m_DEVICE_ADDRESS, 'A', STOP);
    	}
    }
    
    /**
     * Send signal to reverse the robot
     */
    public void reverseButton(View v) {
    	if(m_Connected) {
    		Amarino.sendDataToArduino(this, m_DEVICE_ADDRESS, 'A', REVERSE);
    	}
    }
    
    /**
     * Connect bluetooth device id in edit text field 
     */
    public void connectButton(View v) {
    	EditText deviceId = (EditText)this.findViewById(R.id.device_id);
    	
    	// save address for later use
    	m_DEVICE_ADDRESS = deviceId.getText().toString();
    	
    	// connect to bluetooth device
    	Amarino.connect(this, m_DEVICE_ADDRESS);

		// save state
		PreferenceManager.getDefaultSharedPreferences(this)
			.edit()
				.putString("DEVICE_ID", m_DEVICE_ADDRESS)
			.commit();	
		
		// let app know that it has been connected
		m_Connected = true;
    }
    

    /*
     * Stops bluetooth connection
     * (non-Javadoc)
     * @see android.app.Activity#onStop()
     */
	@Override
	protected void onStop() {
		super.onStop();

		// if it is connected
		if( m_Connected == true ) {
			// stop Amarino's background service, we don't need it any more     	
			Amarino.disconnect(this, m_DEVICE_ADDRESS);
		}				
	}
    
    /**
     * Fire an intent to start the speech recognition activity.
     */
    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());

        // Display an hint to the user about what he should say.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Robot Instruction");

        // Given an hint to the recognizer about what the user is going to say
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

        // Specify how many results you want to receive. The results will be sorted
        // where the first result is the one with higher confidence.
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

        // Specify the recognition language. This parameter has to be specified only if the
        // recognition has to be done in a specific language and not the default one (i.e., the
        /* system locale). Most of the applications do not have to set this parameter.
        if (!mSupportedLanguageView.getSelectedItem().toString().equals("Default")) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                    mSupportedLanguageView.getSelectedItem().toString());
        }*/

        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

}