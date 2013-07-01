package se.huvuddator.greed;

import android.widget.TextView;

public class ScoreResult {
	private int[] mDiceValues;
	private boolean[] mIsDiceScoring;
	private int mScore = 0;
	private TextView mTextLog;

	public ScoreResult(int[] diceValues, TextView textLog) {
		mDiceValues = diceValues;
		mIsDiceScoring = new boolean[diceValues.length];
		mTextLog = textLog;
		
		calculateScore();
	}
	
	public int getScore() {
		return mScore;
	}

	public boolean[] getIsDiceScoring() {
		return mIsDiceScoring;
	}
	
	public boolean hasScoredWithAll() {
		boolean result = true;
		for (boolean isScoring : mIsDiceScoring) {
			if(!isScoring) {
				result = false;
			}
		}
		return result;
	}
	
	private void calculateScore() {
		int[] occurences = new int[mDiceValues.length];
		for (int i = 0; i < mDiceValues.length; i++) {
			if(-1 != mDiceValues[i]) {
				occurences[mDiceValues[i]]++;
			}
		}
		
		// Init highest value
		int result = 0;
		
		mTextLog.setText("Occurencies: ");
		for (int i : occurences) {
			mTextLog.append(i +" ");
		}

		// Look for ladder
		boolean foundLadder = true;
		for (int i = 0; i < occurences.length; i++) {
			if(1 != occurences[i]) {
				foundLadder = false;
				break;
			}
		}

		// If we have a ladder no higher scoring is possible
		if(foundLadder) {
			mTextLog.append("\nLadder");
			for (int i = 0; i < mIsDiceScoring.length; i++) {
				mIsDiceScoring[i] = true;
			}
			mScore = 1000;
		}

		// Look for triplets
		mTextLog.append("\nTri: ");
		for (int i = 0; i < occurences.length; i++) {
			int numOfTriplets = occurences[i]/3;
			mTextLog.append(" " +numOfTriplets);
			occurences[i] -= 3 * numOfTriplets;
			setIsScoring(i, numOfTriplets*3);
			if(0 == i) {
				result += 1000 * numOfTriplets;
			} else {
				result += (i+1)*100 * numOfTriplets;
			}
		}

		// Look for singles
		mTextLog.append("\nSingel: 1: " +occurences[0] +" 5: " +occurences[4]);

		result += 100 * occurences[0];
		setIsScoring(0, occurences[0]);
		occurences[0] = 0;

		result += 50 * occurences[4];
		setIsScoring(4, occurences[4]);
		occurences[4] = 0;
		
		mTextLog.append("\nResult: " +result);
		
		mTextLog.append("\nisScoring: ");
		for(boolean isScoring : mIsDiceScoring) {
			mTextLog.append(" " +(isScoring ? "1" : "0"));
		}
		
		mScore = result;
	}

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
