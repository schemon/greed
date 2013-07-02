package se.huvuddator.greed;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Dialog that ask user to exit the parent activity.<br>
 * The parent activity will be exited using {@code finish()}.
 * @author simarv
 */
public class ExitDialog extends DialogFragment {
	
	 public static ExitDialog newInstance() {
	        ExitDialog frag = new ExitDialog();
	        Bundle args = new Bundle();
	        frag.setArguments(args);
	        return frag;
	    }

	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_exit_title)
                .setMessage(R.string.dialog_exit_body)
                .setPositiveButton(R.string.dialog_exit_positive,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            getActivity().finish();
                        }
                    }
                )
                .setNegativeButton(R.string.dialog_exit_negative, null)
                .create();
	    }

}
