package edu.msu.wangten3.cloudhatter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;

public class AboutDlg  extends DialogFragment {

	public AboutDlg() {
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    
	    builder.setTitle(R.string.about_dlg_title);
	    
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(inflater.inflate(R.layout.about_dlg, null))
	    // Add action buttons
	           .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	               }
	           });
	    
	    final Dialog dlg = builder.create();
        
        dlg.setOnShowListener(new DialogInterface.OnShowListener() {
        	
            @Override
            public void onShow(DialogInterface dialog) {   
            }
        });
	    
	    return dlg;
	}
}
