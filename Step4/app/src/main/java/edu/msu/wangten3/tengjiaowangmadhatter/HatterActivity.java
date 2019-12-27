package edu.msu.wangten3.tengjiaowangmadhatter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

public class HatterActivity extends AppCompatActivity {


    /**
     * Request code when selecting a picture
     */
    private static final int SELECT_PICTURE = 1;
    private static final String PARAMETERS = "parameters";

    private static final int SELECT_COLOR = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }

        /*
         * Set up the spinner
         */

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.hats_spinner, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        getSpinner().setAdapter(adapter);

        getSpinner().setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View view,
                                       int pos, long id) {
                getHatterView().setHat(pos);
                updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }

        });
        /*
         * Restore any state
         */
        if(savedInstanceState != null) {
            getHatterView().getFromBundle(PARAMETERS, savedInstanceState);

            getSpinner().setSelection(getHatterView().getHat());

        }

    }

    /**
     * The hatter view object
     */
    private HatterView getHatterView() {
        return (HatterView) findViewById(R.id.hatterView);
    }

    /**
     * The color select button
     */
    private Button getColorButton() {
        return (Button)findViewById(R.id.buttonColor);
    }

    /**
     * The feather checkbox
     */
    private CheckBox getFeatherCheck() {
        return (CheckBox)findViewById(R.id.checkFeather);
    }

    //check if the checkbox is checked.
    public void onFeather(View view){
        CheckBox cb = getFeatherCheck();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (cb.isChecked()){
            getHatterView().drawfeather();
        }
        else{
            getHatterView().notdraw();
        }
        updateUI();

    }
    public void oncolor(View view){
        Intent intent = new Intent(this, ColorSelectActivity.class);
        this.startActivityForResult(intent, SELECT_COLOR);
    }
    /**
     * The hat choice spinner
     */
    private Spinner getSpinner() {
        return (Spinner) findViewById(R.id.spinnerHat);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            // Response from the picture selection activity
            Uri imageUri = data.getData();

            // We have to query the database to determine the document ID for the image
            Cursor cursor = getContentResolver().query(imageUri, null, null, null, null);
            cursor.moveToFirst();
            String document_id = cursor.getString(0);
            document_id = document_id.substring(document_id.lastIndexOf(":")+1);
            cursor.close();

            // Next, we query the content provider to find the path for this
            // document id.
            cursor = getContentResolver().query(
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
            cursor.moveToFirst();
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();

            if(path != null) {
                Log.i("Path", path);
                //getHatterView().setImagePath(path);
                getHatterView().setImagePath(path);
            }
        }
        else if(requestCode == SELECT_COLOR && resultCode == Activity.RESULT_OK) {
            int color = data.getIntExtra(ColorSelectActivity.COLOR, Color.BLACK);
            Log.i(Integer.toString(color), "color:");
            getHatterView().setColor(color);
        }
    }

    /**
     * Handle a Picture button press
     * @param view
     */
    public void onPicture(View view) {
    // Get a picture from the gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getHatterView().putToBundle(PARAMETERS, outState);
    }
    /**
     * Ensure the user interface components match the current state
     */
    public void updateUI() {
        CheckBox cb = getFeatherCheck();
        int hat = getHatterView().getHat();
        getSpinner().setSelection(hat);
        cb.setChecked(getHatterView().getdrafeather());
        getColorButton().setEnabled(hat == HatterView.HAT_CUSTOM);
    }
}
