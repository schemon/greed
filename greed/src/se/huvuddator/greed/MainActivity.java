package se.huvuddator.greed;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	private Random mRandom = new Random(System.currentTimeMillis());

	private static final int[] DiceDrawables = {
		R.drawable.dice_1, 
		R.drawable.dice_2,
		R.drawable.dice_3,
		R.drawable.dice_4,
		R.drawable.dice_5,
		R.drawable.dice_6,
		};
	
	private ToggleButton[] mDices = new ToggleButton[6];
	
	private int[] mDiceValues = new int[6];
	
	private int mScore = 0;
	private int mTurnScore = 0;
	private static final int MAX_TURN_SCORE = 1000;
	private int mRounds = 0;

	private View mButtonThrow;
	private TextView mTextScore;
	private TextView mTextTurnScore;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mDices[0] = (ToggleButton) findViewById(R.id.dice1);
		mDices[1] = (ToggleButton) findViewById(R.id.dice2);
		mDices[2] = (ToggleButton) findViewById(R.id.dice3);
		mDices[3] = (ToggleButton) findViewById(R.id.dice4);
		mDices[4] = (ToggleButton) findViewById(R.id.dice5);
		mDices[5] = (ToggleButton) findViewById(R.id.dice6);
		mButtonThrow = findViewById(R.id.buttonThrow);
		mTextScore = (TextView) findViewById(R.id.textScore);
		mTextTurnScore = (TextView) findViewById(R.id.textTurnScore);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onThrow(View view) {
		
		
		int[] occurences = new int[mDiceValues.length];
		
		for (int i = 0; i < 6; i++) {
			if(!mDices[i].isChecked()) {
				mDiceValues[i] = mRandom.nextInt(6);
				occurences[mDiceValues[i]] += 1;
				mDices[i].setBackgroundResource(DiceDrawables[mDiceValues[i]]);
			}
		}

		// Init highest value
		int highestValueSoFar = 0;
		
		// Look for singles
		if(1 == occurences[0]) {
			highestValueSoFar = Math.max(highestValueSoFar, 100);
		} else if(1 == occurences[5]) {
			highestValueSoFar = Math.max(highestValueSoFar, 50);
		}
		
		// look for doubles
		if(2 == occurences[0]) {
			highestValueSoFar = Math.max(highestValueSoFar, 200);
		} else if(2 == occurences[5]) {
			highestValueSoFar = Math.max(highestValueSoFar, 100);
		}
		
		// Look for triplets
		for (int i = 0; i < occurences.length; i++) {
			if(3 <= occurences[i]) {
				if(0 == i) {
					highestValueSoFar = 1000;
					break;
				} else {
					highestValueSoFar = Math.max(highestValueSoFar, (i+1)*1000);
				}
			} 
		}
		
		// Look for ladder
		boolean foundLadder = true;
		for (int i = 0; i < occurences.length; i++) {
			if(1 != occurences[i]) {
				foundLadder = false;
				break;
			}
		}
		
		if(foundLadder) {
			highestValueSoFar = Math.max(highestValueSoFar, 1000);
		}
		
		mTurnScore = highestValueSoFar;
		
		mTextTurnScore.setText("Score: " +mTurnScore);
		
		if(MAX_TURN_SCORE <= highestValueSoFar) {
			mButtonThrow.setEnabled(false);
		}
	}
}
