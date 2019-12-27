package edu.msu.wangten3.cloudhatter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import edu.msu.wangten3.cloudhatter.Cloud.Cloud;
import edu.msu.wangten3.cloudhatter.Cloud.Models.Hat;

public class ComfirmDlg extends DialogFragment {
    /**
     * Id for the image we are loading
     */
    private String catId;
    private String catName;

    private final static String ID = "id";
    private final static String NAME = "name";

    private volatile boolean cancel = false;
    private AlertDialog dlg;

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        bundle.putString(ID, catId);
        bundle.putString(NAME, catName);

    }

    /**
     * Create the dialog box
     */
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        cancel = false;
        if (bundle != null) {
            catId = bundle.getString(ID);
        }

        cancel = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle(R.string.Delete);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();


        // Pass null as the parent view because its going in the dialog layout
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.delete_dlg, null);

        TextView hat_name = (TextView)view.findViewById(R.id.hatname);
        hat_name.setText(catName);
        builder.setView(view);


        // Get a reference to the view we are going to load into
        final HatterView hatterView = (HatterView) getActivity().findViewById(R.id.hatterView);


        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                cancel = true;
            }
        });

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                /*
                 * Create a thread to delete the hatting from the cloud
                 */
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // Create a cloud object and get the XML
                        Cloud cloud = new Cloud();
                        InputStream stream = cloud.deleteFromCloud(catId);

                        if(cancel) {
                            return;
                        }

                        // Test for an error
                        boolean fail = stream == null;

                        if(!fail) {
                            try {
                                XmlPullParser xml = Xml.newPullParser();
                                xml.setInput(stream, "UTF-8");

                                xml.nextTag();      // Advance to first tag
                                xml.require(XmlPullParser.START_TAG, null, "hatter");
                                String status = xml.getAttributeValue(null, "status");
                                if(status.equals("yes")) {

                                    while(xml.nextTag() == XmlPullParser.START_TAG) {
                                        if(xml.getName().equals("hatting")) {
                                            if(cancel) {
                                                return;
                                            }

                                            hatterView.loadXml(xml);
                                            break;
                                        }

                                        Cloud.skipToEndTag(xml);
                                    }
                                } else {
                                    fail = true;
                                }

                            } catch(IOException ex) {
                                fail = true;
                            } catch(XmlPullParserException ex) {
                                fail = true;
                            } finally {
                                try {
                                    stream.close();
                                } catch(IOException ex) {
                                }
                            }
                        }

                        final boolean fail1 = fail;
                        hatterView.post(new Runnable() {

                            @Override
                            public void run() {
                                dlg.dismiss();
                                if(fail1) {
                                    Toast.makeText(hatterView.getContext(), R.string.deletef, Toast.LENGTH_LONG).show();
                                } else {
                                    // Success!
                                    if(getActivity() instanceof HatterActivity) {
                                        ((HatterActivity)getActivity()).updateUI();
                                    }
                                }

                            }

                        });
                    }
                }).start();

            }
        });

        dlg = builder.create();
        return dlg;
    }


}
