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

	// Static game constants
	private static final int WIN_THRESHOLD = 10000;
	private static final int MINIMUN_VALUE_TO_CONTINUE = 1;
	public static final int NUMBER_OF_DICE = 6;

	// Game state
	private int[] mDiceValues = new int[NUMBER_OF_DICE];
	private boolean[] mDiceChecked = new boolean[NUMBER_OF_DICE];
	private boolean[] mDiceEnabled = new boolean[NUMBER_OF_DICE];
	private int mScore = 0;
	private int mTurnScore = 0;
	private int mNumberOfTurns = 0;
	private boolean mIsNewTurn = true;
	private boolean mIsThrowEnabled = false;
	private boolean mIsSavedEnabled = false;

	// Views
	private View mButtonThrow;
	private TextView mTextScore;
	private TextView mTextTurnScore;
	private TextView mTextTurnCounter;
	private TextView mTextLog;
	private ToggleButton[] mDices = new ToggleButton[NUMBER_OF_DICE];
	private View mButtonSave;
	private static final int[] DiceDrawables = {
		R.drawable.dice_1, 
		R.drawable.dice_2,
		R.drawable.dice_3,
		R.drawable.dice_4,
		R.drawable.dice_5,
		R.drawable.dice_6,
	};



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


	/*
	 * 	private int[] mDiceValues = new int[NUMBER_OF_DICE];
	private int mScore = 0;
	private int mTurnScore = 0;
	private int mNumberOfTurns = 0;
	private boolean isNewTurn = true;
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putIntArray("diceValues", mDiceValues);
		outState.putBooleanArray("diceChecked", mDiceChecked);
		outState.putBooleanArray("diceEnabled", mDiceEnabled);
		outState.putInt("score", mScore);
		outState.putInt("turnScore", mTurnScore);
		outState.putInt("numberOfTurns", mNumberOfTurns);
		outState.putBoolean("isNewTurn", mIsNewTurn);
		outState.putBoolean("isThrowEnabled", mIsThrowEnabled);
		outState.putBoolean("isSaveEnabled", mIsSavedEnabled);

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mDiceValues = savedInstanceState.getIntArray("diceValues");
		mDiceChecked = savedInstanceState.getBooleanArray("diceChecked");
		mDiceEnabled = savedInstanceState.getBooleanArray("diceEnabled");
		mScore = savedInstanceState.getInt("score");
		mTurnScore = savedInstanceState.getInt("turnScore");
		mNumberOfTurns = savedInstanceState.getInt("numberOfTurns");
		mIsNewTurn = savedInstanceState.getBoolean("isNewTurn");
		mIsThrowEnabled = savedInstanceState.getBoolean("isThrowEnabled");
		mIsSavedEnabled = savedInstanceState.getBoolean("isSaveEnabled");
		renderViews();
	}


	private void renderViews() {
		mButtonThrow.setEnabled(mIsThrowEnabled);
		mButtonSave.setEnabled(mIsSavedEnabled);

		mTextScore.setText("Score: " +mScore);
		mTextTurnScore.setText("Turn score: " +mTurnScore);
		mTextTurnCounter.setText("Turns: " +mNumberOfTurns);

		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			mDices[i].setBackgroundResource(DiceDrawables[mDiceValues[i]]);
			mDices[i].setChecked(mDiceChecked[i]);
			mDices[i].setEnabled(mDiceEnabled[i]);
		}
	}

	public void onSave(View view) {
		int[] scoringDiceValues = new int[mDiceValues.length];
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			if(mDiceEnabled[i]) {
				scoringDiceValues[i] = mDiceValues[i];
			} else {
				scoringDiceValues[i] = -1;
			}
		}
		ScoreResult scoreResult = new ScoreResult(scoringDiceValues, mTextLog);
		mTurnScore += scoreResult.getScore();

		boolean[] isDiceScoring = scoreResult.getIsDiceScoring();
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			if(isDiceScoring[i]) {
				mDiceEnabled[i] = false;
			}
		}

		onEndTurn();
		renderViews();
	}

	public void onClickDie(View view) {
		// Should not be here if state is "new turn". Clean up and return.
		if(mIsNewTurn) {
			if (view instanceof ToggleButton) {
				ToggleButton die = (ToggleButton) view;
				die.setChecked(false);
			}
			mIsThrowEnabled = true;
			return;
		}

		// Update game state
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			mDiceChecked[i] = mDices[i].isChecked();
		}

		int[] scoringDiceValues = new int[NUMBER_OF_DICE];
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			if(mDiceChecked[i]) {
				scoringDiceValues[i] = mDiceValues[i];
			} else {
				scoringDiceValues[i] = -1;
			}
		}

		int[] possiblyScoringDice = new int[NUMBER_OF_DICE];
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			if(mDiceEnabled[i]) {
				possiblyScoringDice[i] = mDiceValues[i];
			} else {
				possiblyScoringDice[i] = -1;
			}
		}

		ScoreResult scoreResult = new ScoreResult(possiblyScoringDice, mTextLog);

		boolean[] isDiceScoring = scoreResult.getIsDiceScoring();
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			if(mDiceChecked[i] && !isDiceScoring[i]) {
				mDiceChecked[i] = false;
			}
		}

		int checkedDiceScore = scoreResult.getScore();
		mIsThrowEnabled = (0 < checkedDiceScore);

		renderViews();
	}


	/**
	 * Throwing
	 * 
	 * @param view
	 */
	public void onThrow(View view) {
		// If this is a new turn, reset dice, otherwise end last throw.
		if(mIsNewTurn) {
			setupTurn();
		} else {
			onEndThrow();
		}

		int[] scoringDiceValues = new int[NUMBER_OF_DICE];

		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			if(mDiceEnabled[i]) {
				int value = mRandom.nextInt(6);
				mDiceValues[i] = value;
				scoringDiceValues[i] = mDiceValues[i];
			} else {
				scoringDiceValues[i] = -1;
			}
		}

		int throwScore = new ScoreResult(scoringDiceValues, mTextLog).getScore();

		if(0 == throwScore || (MINIMUN_VALUE_TO_CONTINUE > throwScore && 0 == mTurnScore)) {
			mTurnScore = 0;
			mTextLog.append("\nTurn ends with no points!");
			Toast.makeText(this, "Turn ends with no points!", Toast.LENGTH_SHORT).show();
			onEndTurn();
		} else {
			mIsThrowEnabled = false;
			mIsSavedEnabled = true;
		}

		renderViews();
	}

	private void setupTurn() {
		for (int i = 0; i < mDices.length; i++) {
			mDiceChecked[i] = false;
			mDiceEnabled[i] = true;
		}
		mIsNewTurn = false;
	}

	private void onEndThrow() {
		int[] scoringDiceValues = new int[NUMBER_OF_DICE];
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			if(mDiceChecked[i]) {
				scoringDiceValues[i] = mDiceValues[i];
				mDiceChecked[i] = false;
				mDiceEnabled[i] = false;
			} else {
				scoringDiceValues[i] = -1;
			}
		}
		mTurnScore += new ScoreResult(scoringDiceValues, mTextLog).getScore();
		mTextTurnScore.setText("Turn score: " +mTurnScore);

		boolean hasScoredWithAll = new ScoreResult(mDiceValues, mTextLog).hasScoredWithAll();
		if(hasScoredWithAll) {
			Toast.makeText(this, "Scored with all!", Toast.LENGTH_SHORT).show();
			for (int i = 0; i < NUMBER_OF_DICE; i++) {
				mDiceEnabled[i] = true;
			}
		}

	}

	private void onEndTurn() {
		mNumberOfTurns += 1;
		mScore += mTurnScore;
		mTurnScore = 0;
		mIsSavedEnabled = false;
		mIsThrowEnabled = true;
		mIsNewTurn  = true;
	}
}
