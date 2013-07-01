package se.huvuddator.greed;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

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
	                            ((MainActivity) getActivity()).onExit();
	                        }
	                    }
	                )
	                .setNegativeButton(R.string.dialog_exit_negative,
	                    new DialogInterface.OnClickListener() {
	                        public void onClick(DialogInterface dialog, int whichButton) {
	                        }
	                    }
	                )
	                .create();
	    }

//	public interface ExitDialogListener {
//		void onFinishExitDialog(boolean exit);
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.dialog_exit, container);
//		view.findViewById(R.id.buttonYes).setOnClickListener(this);
//		view.findViewById(R.id.buttonNo).setOnClickListener(this);
//		getDialog().setTitle("Do you want to exit?");
//		return view;
//	}
//
//	@Override
//	public void onClick(View v) {
//		this.dismiss();
//		ExitDialogListener activity = (ExitDialogListener) getActivity();
//		activity.onFinishExitDialog(v.getId() == R.id.buttonYes);
//	}

}
