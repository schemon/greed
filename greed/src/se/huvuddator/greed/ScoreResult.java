package se.huvuddator.greed;

import android.widget.TextView;

/**
 * Calculates score for a die combination.<br>
 * 
 * @author simarv
 *
 */
public class ScoreResult {
	private int[] mDiceValues;
	private boolean[] mIsDiceScoring;
	private int mScore = 0;
	private TextView mTextLog;

	/**
	 * Constructs a ScoreResult object. This object has three types of result: <br>
	 * - The {@code int} score value using {@code getScore()}.<br>
	 * - An array {@code boolean[]} with dice that contributes to score using {@code getIsDiceScoring()}.<br>
	 * - The {@code boolean} whether all dice contributes to score using {@code hasScoredWithAll()}.<br>
	 * @param diceValues
	 * 	 An array {@code int[]} with the dice values
	 * @param textLog
	 */
	public ScoreResult(int[] diceValues, TextView textLog) {
		mDiceValues = diceValues;
		mIsDiceScoring = new boolean[diceValues.length];
		mTextLog = textLog;

		calculateScore();
	}

	/**
	 * @return The {@code int} score value.
	 */
	public int getScore() {
		return mScore;
	}

	/**
	 * @return An array {@code boolean[]} with dice that contributes to score.
	 */
	public boolean[] getIsDiceScoring() {
		return mIsDiceScoring;
	}

	/**
	 * @return The {@code boolean} whether all dice contributes to score.
	 */
	public boolean hasScoredWithAll() {
		boolean result = true;
		for (boolean isScoring : mIsDiceScoring) {
			if(!isScoring) {
				result = false;
			}
		}
		return result;
	}

	/**
	 * Calculates score result and logs detailed scoring data to mTextLog.<br>
	 * <br>
	 * This order of score evaluation is crucial. Singles has to come last:<br>
	 * 1. Ladder<br>
	 * 2. Triplets<br>
	 * 3. Singles<br>
	 * <br>
	 * If the order is wrong Triplets of ones might 
	 * instead be scored as three singles.<br>
	 * When a score is evaluated, the involved dice 
	 * is removed from the array of {@code occurrences}.<br>
	 * <br>
	 * TODO: The order of dice shouldn't be important.<br>
	 */
	private void calculateScore() {
		int[] occurrences = getOccurences();
		int result = 0;

		mTextLog.setText("Occurencies: ");
		for (int i : occurrences) {
			mTextLog.append(i +" ");
		}

		// 1. Look for ladder
		boolean foundLadder = true;
		for (int i = 0; i < occurrences.length; i++) {
			if(1 != occurrences[i]) {
				foundLadder = false;
				break;
			}
		}

		/* If we have a ladder no higher scoring is possible 
		 * so just set mScore to 1000.
		 */
		if(foundLadder) {
			mTextLog.append("\nLadder");
			for (int i = 0; i < mIsDiceScoring.length; i++) {
				mIsDiceScoring[i] = true;
			}
			mScore = 1000;
		}

		/* 2. Look for triplets.
		 * Triplets of ones is worth 1000.
		 * Other triplets is worth die value times 100.
		 */
		mTextLog.append("\nTri: ");
		for (int i = 0; i < occurrences.length; i++) {
			int numOfTriplets = occurrences[i]/3;
			mTextLog.append(" " +numOfTriplets);
			occurrences[i] -= 3 * numOfTriplets;
			setIsScoring(i, numOfTriplets*3);
			if(0 == i) {
				result += 1000 * numOfTriplets;
			} else {
				// All dice values is off by one so i must be incremented by 1.
				result += (i+1)*100 * numOfTriplets;
			}
		}

		/* 3. Look for singles
		 * Ones give 100 points each.
		 * Fives give 50 points each.
		 */
		mTextLog.append("\nSingel: 1: " +occurrences[0] +" 5: " +occurrences[4]);
		result += 100 * occurrences[0];
		setIsScoring(0, occurrences[0]);
		occurrences[0] = 0;
		result += 50 * occurrences[4];
		setIsScoring(4, occurrences[4]);
		occurrences[4] = 0;

		mTextLog.append("\nResult: " +result);

		mTextLog.append("\nisScoring: ");
		for(boolean isScoring : mIsDiceScoring) {
			mTextLog.append(" " +(isScoring ? "1" : "0"));
		}

		mScore = result;
	}

	/**
	 * Calculates occurrences of dice values.
	 * @return An array {@code int[]} with occurrences. <br>
	 * E.g. for three 2:s, one 5 and two 6:s the result is:<br>
	 * {0, 3, 0, 0, 1, 2} 
	 */
	private int[] getOccurences() {
		int[] occurrences = new int[mDiceValues.length];
		for (int i = 0; i < mDiceValues.length; i++) {
			if(MainActivity.DIE_VALUE_NOT_SET != mDiceValues[i]) {
				occurrences[mDiceValues[i]]++;
			}
		}
		return occurrences;
	}

	/**
	 * Mark dice as scoring.
	 * @param value {@code int} of the dice to mark as scoring.
	 * @param occurances {@code int} number of of die to mark.
	 */
	private void setIsScoring(int value, int occurances) {
		if(occurances > 0) {
			for (int i = 0; i < mDiceValues.length; i++) {
				if(!mIsDiceScoring[i] && value == mDiceValues[i] && occurances > 0) {
					mIsDiceScoring[i] = true;
					occurances -= 1;
				}
			}
		}
	}
}
