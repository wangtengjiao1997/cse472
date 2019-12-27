package imjames.msu.edu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    private int gridSize = 0;
    public static final String SIZE = "SIZE";
    public static final String NAME1 = "NAME1";
    public static final String NAME2 = "NAME2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.size_spinner, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        getSpinner().setAdapter(adapter);


        getSpinner().setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View view,
                                       int pos, long id) {
                gridSize = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public void onStartAbout(View view) {
        // Instantiate a dialog box builder
        AlertDialog.Builder builder =
                new AlertDialog.Builder(view.getContext());

        // Parameterize the builder
        builder.setTitle(R.string.about);
        builder.setMessage(R.string.instruction);
        builder.setPositiveButton(android.R.string.ok, null);

        // Create the dialog box and show it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void onStartSteam(View view){
        Intent intent = new Intent(this, SteamActivity.class);
        intent.putExtra(SIZE, gridSize);
        intent.putExtra(NAME1,getEditText1().getText().toString());
        intent.putExtra(NAME2,getEditText2().getText().toString());
        startActivity(intent);
    }
    /**
     * The size choice spinner
     */
    private Spinner getSpinner() {
        return (Spinner) findViewById(R.id.SizeSpinner);
    }

    private EditText getEditText1() {
        return (EditText) findViewById(R.id.PlayerOneName);
    }

    private EditText getEditText2() {
        return (EditText) findViewById(R.id.PlayerTwoName);
    }
}
