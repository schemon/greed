package se.huvuddator.greed;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Shows score and number of turns passed in the intent extra.<br>
 * To pass score and turn use these strings:<br>
 * {@code ScoreActivity.INTENT_EXTRAS_SCORE} and <br>
 * {@code ScoreActivity.INTENT_EXTRAS_TURNS} <br>
 * 
 * @author simarv
 *
 */
public class ScoreActivity extends Activity {

	public static final String INTENT_EXTRAS_SCORE = "intent_extras_score";
	public static final String INTENT_EXTRAS_TURNS = "intent_extras_turns";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_highscore);
		TextView textScore = (TextView) findViewById(R.id.text_highschore);
		if(getIntent() != null && getIntent().getExtras() != null) {
			Bundle extras = getIntent().getExtras();
			int score = extras.getInt(INTENT_EXTRAS_SCORE, 0);
			int turns = extras.getInt(INTENT_EXTRAS_TURNS, 0);
			textScore.setText("You got " +score +" points after " +turns +" rounds");
		}
	}

	public void onClickTryAgain(View view) {
		finish();
	}

}
