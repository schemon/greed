package se.huvuddator.greed;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
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

	private ToggleButton[] mDices = new ToggleButton[NUMBER_OF_DICE];

	private int[] mDiceValues = new int[NUMBER_OF_DICE];
	
	public static final int NUMBER_OF_DICE = 6;
	
	private int mScore = 0;
	private int mTurnScore = 0;
	private static final int WIN_THRESHOLD = 10000;

	private static final int MINIMUN_VALUE_TO_CONTINUE = 1;
	private int mNumberOfTurns = 0;

	private View mButtonThrow;
	private TextView mTextScore;
	private TextView mTextTurnScore;
	private TextView mTextTurnCounter;
	private TextView mTextLog;
	
	private View mButtonSave;

	private int mLastThrowScore;

	private boolean isNewTurn = true;


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
		mButtonSave = findViewById(R.id.buttonSave);
		mTextScore = (TextView) findViewById(R.id.textScore);
		mTextTurnScore = (TextView) findViewById(R.id.textTurnScore);
		mTextTurnCounter = (TextView) findViewById(R.id.textRoundCounter);
		mTextLog = (TextView) findViewById(R.id.logThrow);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	public void onSave(View view) {
		int[] scoringDiceValues = new int[mDiceValues.length];
		for (int i = 0; i < mDices.length; i++) {
			if(mDices[i].isEnabled()) {
				scoringDiceValues[i] = mDiceValues[i];
			} else {
				scoringDiceValues[i] = -1;
			}
		}
		ScoreResult scoreResult = new ScoreResult(scoringDiceValues, mTextLog);
		mTurnScore += scoreResult.getScore();

		boolean[] isDiceScoring = scoreResult.getIsDiceScoring();
		for (int i = 0; i < isDiceScoring.length; i++) {
			if(isDiceScoring[i]) {
				mDices[i].setEnabled(false);
			}
		}
		
		onEndTurn();
	}

	public void onClickDie(View view) {
		int[] scoringDiceValues = new int[mDiceValues.length];
		for (int i = 0; i < mDices.length; i++) {
			if(mDices[i].isChecked()) {
				scoringDiceValues[i] = mDiceValues[i];
			} else {
				scoringDiceValues[i] = -1;
			}
		}
		
		int checkedDiceScore = new ScoreResult(scoringDiceValues, mTextLog).getScore();
		if(0 < checkedDiceScore) {
			mButtonThrow.setEnabled(true);
		} else {
			mButtonThrow.setEnabled(false);
		}
	}


	/**
	 * Throwing
	 * 
	 * @param view
	 */
	public void onThrow(View view) {
		// If this is a new turn, reset dice, otherwise end last throw.
		if(isNewTurn) {
			setupTurn();
		} else {
			onEndThrow();
		}
		
		mLastThrowScore = 0;
		int[] scoringDiceValues = new int[mDiceValues.length];
		
		for (int i = 0; i < mDices.length; i++) {
			if(mDices[i].isEnabled()) {
				int value = mRandom.nextInt(6);
				mDices[i].setBackgroundResource(DiceDrawables[value]);
				mDiceValues[i] = value;
				scoringDiceValues[i] = mDiceValues[i];
			} else {
				scoringDiceValues[i] = -1;
			}
		}

		mLastThrowScore = new ScoreResult(scoringDiceValues, mTextLog).getScore();
		
		if(0 == mLastThrowScore || (MINIMUN_VALUE_TO_CONTINUE > mLastThrowScore && 0 == mTurnScore)) {
			mTurnScore = 0;
			mTextLog.append("\nTurn ends with no points!");
			onEndTurn();
		} else {
			mButtonThrow.setEnabled(false);
			mButtonSave.setEnabled(true);
		}
	}

	private void setupTurn() {
		for (int i = 0; i < mDices.length; i++) {
			mDices[i].setChecked(false);
			mDices[i].setEnabled(true);
		}
		isNewTurn = false;
	}

	private void onEndThrow() {
		int[] scoringDiceValues = new int[mDiceValues.length];
		for (int i = 0; i < mDices.length; i++) {
			if(mDices[i].isChecked()) {
				scoringDiceValues[i] = mDiceValues[i];
				mDices[i].setChecked(false);
				mDices[i].setEnabled(false);
			} else {
				scoringDiceValues[i] = -1;
			}
		}
		mTurnScore += new ScoreResult(scoringDiceValues, mTextLog).getScore();
		mTextTurnScore.setText("Turn score: " +mTurnScore);
		
		boolean hasScoredWithAll = new ScoreResult(mDiceValues, mTextLog).hasScoredWithAll();
		if(hasScoredWithAll) {
			for (int i = 0; i < mDices.length; i++) {
				mDices[i].setEnabled(true);
			}
		}

	}

	private void onEndTurn() {
		mNumberOfTurns += 1;

		mScore += mTurnScore;
		mTurnScore = 0;

		mTextTurnCounter.setText("Turns: " +mNumberOfTurns);
		mTextScore.setText("Score: " +mScore);
		mTextTurnScore.setText("Turn score: " +mTurnScore);

		mButtonSave.setEnabled(false);
		mButtonThrow.setEnabled(true);
		
		isNewTurn  = true;
	}
}
