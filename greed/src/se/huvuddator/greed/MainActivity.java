package se.huvuddator.greed;

import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Main activity of the game Greed.
 * This activity has three responsibilities:<br>
 * - Holds the game state.<br>
 * - Handles input from user.<br>
 * - Game logic (updating game state from input).<br>
 * <br>
 * The user interactions are: <br>
 * - Throwing dice {@code onThrow(View view)}.<br>
 * - End turn and score {@code onScore(View view)}.<br>
 * - Clicking a die to take it aside for scoring {@code onClickDie(View view)}.<br>
 * - Click back button to quit playing {@code onBackPressed(View view)}.
 * @author simarv
 *
 */
/*
 * The dice representation is built on arrays. 
 * Each die is represented by one value in each one of these arrays:
 * - DiceDrawables; contains graphical representation displayed by the die view.
 * - mDiceValues; current die eye value [0, 5] where real eye value is mDiceValues[i] + 1.
 * - mDiceChecked; holds boolean value whether die is checked/marked for scoring.
 * - mDiceEnabled; holds boolean value whether die hasn't been used for scoring in current turn.
 * - mDices; holds the ToggleButtons that represents the dice.
 */
public class MainActivity extends FragmentActivity  {
	private Random mRandom = new Random(System.currentTimeMillis());

	// game constants
	private static final int WIN_THRESHOLD = 10000;
	private static final int MINIMUN_VALUE_TO_CONTINUE = 300;
	public static final int NUMBER_OF_DICE = 6;
	public static final int DIE_VALUE_NOT_SET = -1;
	private static final int[] DiceDrawables = {
		R.drawable.dice_1, 
		R.drawable.dice_2,
		R.drawable.dice_3,
		R.drawable.dice_4,
		R.drawable.dice_5,
		R.drawable.dice_6,
	};
	
	// Game state
	private int[] mDiceValues = new int[NUMBER_OF_DICE];
	private boolean[] mDiceChecked = new boolean[NUMBER_OF_DICE];
	private boolean[] mDiceEnabled = new boolean[NUMBER_OF_DICE];
	private int mScore = 0;
	private int mTurnScore = 0;
	private int mNumberOfTurns = 0;
	private boolean mIsNewTurn = true;
	private boolean mIsThrowEnabled = true;
	private boolean mIsSaveScoreEnabled = false;
	private String mLastThrowInfo = "";

	// Views
	private View mButtonThrow;
	private TextView mTextScore;
	private TextView mTextTurnScore;
	private TextView mTextTurnCounter;
	private TextView mTextThrowInfo;
	private TextView mTextLog;
	private ToggleButton[] mDices = new ToggleButton[NUMBER_OF_DICE];
	private View mButtonScore;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
	}
	
	/* onResume is overridden in order to render all views
	 * after activity has been created or restored.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		renderViews();
	}
	
	/* All dice is initiated individually. 
	 * If the number of dice should be changed, 
	 * the initiation has to be adjusted.
	 */
	private void initViews() {
		mDices[0] = (ToggleButton) findViewById(R.id.dice1);
		mDices[1] = (ToggleButton) findViewById(R.id.dice2);
		mDices[2] = (ToggleButton) findViewById(R.id.dice3);
		mDices[3] = (ToggleButton) findViewById(R.id.dice4);
		mDices[4] = (ToggleButton) findViewById(R.id.dice5);
		mDices[5] = (ToggleButton) findViewById(R.id.dice6);
		mButtonThrow = findViewById(R.id.buttonThrow);
		mButtonScore = findViewById(R.id.buttonScore);
		mTextScore = (TextView) findViewById(R.id.textScore);
		mTextTurnScore = (TextView) findViewById(R.id.textTurnScore);
		mTextTurnCounter = (TextView) findViewById(R.id.textRoundCounter);
		mTextThrowInfo = (TextView) findViewById(R.id.textThrowInfo);
		mTextLog = (TextView) findViewById(R.id.logThrow);
	}
	
	private void renderViews() {
		mButtonThrow.setEnabled(mIsThrowEnabled);
		mButtonScore.setEnabled(mIsSaveScoreEnabled);

		mTextScore.setText(getString(R.string.score, mScore));
		mTextTurnScore.setText(getString(R.string.turn_score, mTurnScore));
		mTextTurnCounter.setText(getString(R.string.number_of_turns, mNumberOfTurns));
		mTextThrowInfo.setText(mLastThrowInfo);

		// Each die view has three attributes and all must be set.
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			mDices[i].setBackgroundResource(DiceDrawables[mDiceValues[i]]);
			mDices[i].setChecked(mDiceChecked[i]);
			mDices[i].setEnabled(mDiceEnabled[i]);
		}
	}

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
		outState.putBoolean("isSaveScoreEnabled", mIsSaveScoreEnabled);
		outState.putString("lastThrowInfo", mLastThrowInfo);
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
		mIsSaveScoreEnabled = savedInstanceState.getBoolean("isSaveScoreEnabled");
		mLastThrowInfo = savedInstanceState.getString("lastThrowInfo");
	}

	/* When pressing the back button the user has to confirm exit.
	 */
	@Override
	public void onBackPressed() {
		DialogFragment newFragment = ExitDialog.newInstance();
	    newFragment.show(getSupportFragmentManager(), "dialog");
	}

	/**
	 * Called when user wants to throw enabled dice not set aside. 
	 * 
	 * @param view
	 */
	public void onThrow(View view) {
		mLastThrowInfo = "";
		// If this is a new turn, reset dice, otherwise end last throw.
		if(mIsNewTurn) {
			onStartTurn();
		} else {
			onEndLastThrow();
		}

		// Throw the dice that are enabled
		throwAllEnabledDice();

		int throwScore = new ScoreResult(getEnabledDiceValues(), mTextLog).getScore();

		// If score is not enough the turn should be ended and turn score should be lost.
		if(isThrowScoreInsufficient(throwScore)) {
			mLastThrowInfo = getString(R.string.insufficient_points, throwScore); 
			mTurnScore = 0;
			onEndTurn();
		} else {
			mIsThrowEnabled = false;
			mIsSaveScoreEnabled = true;
		}

		renderViews();
	}
	
	/**
	 * Called when user wants to end turn and collect points.
	 * @param view
	 */
	public void onScore(View view) {
		
		int[] scoringDiceValues = createDiceValues();
		
		// Add enabled dice values for scoring
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			if(mDiceEnabled[i]) {
				scoringDiceValues[i] = mDiceValues[i];
			}
		}
		
		// Accumulate current score to turn
		ScoreResult scoreResult = new ScoreResult(scoringDiceValues, mTextLog);
		mTurnScore += scoreResult.getScore();

		// Mark scoring dice as disabled
		boolean[] isDiceScoring = scoreResult.getIsDiceScoring();
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			if(isDiceScoring[i]) {
				mDiceEnabled[i] = false;
			}
		}

		// Finalize action
		onEndTurn();
		renderViews();
	}

	/**
	 * Called when user wants to set aside one die for scoring. 
	 * If a die is not part of a scoring combination the die will be unmarked.
	 * 
	 * The throw button will be enabled if at least one die is correctly marked.
	 * @param view
	 */
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
		updateIsCheckedValues();
		
		// Only allow checking dice that is part of possibly scoring combination.
		uncheckNonScoringDice();		

		// Only allow throw if user has checked a scoring combination
		ScoreResult checkedDiceScore = new ScoreResult(
				getValuesOfCheckedDice(), 
				mTextLog);
		mIsThrowEnabled = (0 < checkedDiceScore.getScore());

		renderViews();
	}

	/**
	 * Called in start of turn to setup game state.
	 */
	protected void onStartTurn() {
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			mDiceChecked[i] = false;
			mDiceEnabled[i] = true;
		}
		mIsNewTurn = false;
	}

	/**
	 * Called at the beginning of a new throw to end last.
	 * This method is triggered when user clicks throw. <br>
	 * <br>
	 * Dice marked to be set aside will be set as disabled
	 * and their score value will be accumulated to turn score.
	 */
	protected void onEndLastThrow() {
		int[] scoringDiceValues = createDiceValues();
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			if(mDiceChecked[i]) {
				scoringDiceValues[i] = mDiceValues[i];
				mDiceChecked[i] = false;
				mDiceEnabled[i] = false;
			}
		}
		int throwScore = new ScoreResult(scoringDiceValues, mTextLog).getScore();
		mTurnScore += throwScore;

		boolean hasScoredWithAll = new ScoreResult(mDiceValues, mTextLog).hasScoredWithAll();
		if(hasScoredWithAll) {
			mLastThrowInfo = getString(R.string.score_with_all);
			for (int i = 0; i < NUMBER_OF_DICE; i++) {
				mDiceEnabled[i] = true;
			}
		}

	}

	/**
	 * Called at the end of a turn to accumulate score with turn score.
	 * The game state is prepared for next turn.
	 */
	protected void onEndTurn() {
		mNumberOfTurns += 1;
		mScore += mTurnScore;

		if(mScore >= WIN_THRESHOLD) {
			onWin();
		}

		mTurnScore = 0;
		mIsSaveScoreEnabled = false;
		mIsThrowEnabled = true;
		mIsNewTurn  = true;
	}

	/**
	 * Called when user has reached {@code WIN_THRESHOLD}.
	 */
	protected void onWin() {
		Intent intent = new Intent(this, ScoreActivity.class);
		intent.putExtra(ScoreActivity.INTENT_EXTRAS_SCORE, mScore);
		intent.putExtra(ScoreActivity.INTENT_EXTRAS_TURNS, mNumberOfTurns);
		startActivity(intent);
		resetGameState();
		renderViews();
	}
	
	/*	-------- Private modifiers of game state ----------*/
	
	private void resetGameState() {
		mDiceValues = new int[NUMBER_OF_DICE];
		mDiceChecked = new boolean[NUMBER_OF_DICE];
		mDiceEnabled = new boolean[NUMBER_OF_DICE];
		mScore = 0;
		mTurnScore = 0;
		mNumberOfTurns = 0;
		mIsNewTurn = true;
		mIsThrowEnabled = true;
		mIsSaveScoreEnabled = false;
	}	
	
	private void throwAllEnabledDice() {
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			if(mDiceEnabled[i]) {
				int value = mRandom.nextInt(6);
				mDiceValues[i] = value;
			}
		}		
	}
	
	/**
	 * Sets non scoring die as not checked. 
	 * renderViews() has to be called before change is visible to user.
	 */
	private void uncheckNonScoringDice() {
		ScoreResult scoreResult = new ScoreResult(
				getEnabledDiceValues(),
				mTextLog);
		
		boolean[] isDiceScoring = scoreResult.getIsDiceScoring();
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			if(mDiceChecked[i] && !isDiceScoring[i]) {
				mDiceChecked[i] = false;
			}
		}		
	}

	/**
	 * Updates values in mDiceChecked to actual value of dice views.
	 */
	private void updateIsCheckedValues() {
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			mDiceChecked[i] = mDices[i].isChecked();
		}
	}
	
	/*	--------------- Private getters ------------------ */
	
	/**
	 * Creates a int array of length {@code NUMBER_OF_DICE}
	 *  with values {@code DICE_VALUE_NOT_SET}
	 */
	private static int[] createDiceValues() {
		int[] diceValues = new int[NUMBER_OF_DICE];
		for (int i = 0; i < diceValues.length; i++) {
			diceValues[i] = DIE_VALUE_NOT_SET;
		}
		return diceValues;
	}

	
	/**
	 * Returns an int array with eye value of dice that are checked.
	 * For dice that are not checked the value will be {@code DICE_VALUE_NOT_SET}
	 * @return
	 */
	private int[] getValuesOfCheckedDice() {
		int[] checkedDice = createDiceValues();
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			if(mDiceChecked[i]) {
				checkedDice[i] = mDiceValues[i];
			}
		}
		return checkedDice;
	}

	/**
	 * Returns an int array with eye value of dice that are enabled.
	 * For dice that are not checked the value will be {@code DICE_VALUE_NOT_SET}
	 * @return
	 */	
	private int[] getEnabledDiceValues() {
		int[] enabledDice = createDiceValues();
		for (int i = 0; i < NUMBER_OF_DICE; i++) {
			if(mDiceEnabled[i]) {
				enabledDice[i] = mDiceValues[i];
			}
		}
		return enabledDice;
	}
	
	private boolean isThrowScoreInsufficient(int throwScore) {
		return 0 == throwScore 
				|| (MINIMUN_VALUE_TO_CONTINUE > throwScore 
						&& 0 == mTurnScore);
	}
	
	
}
