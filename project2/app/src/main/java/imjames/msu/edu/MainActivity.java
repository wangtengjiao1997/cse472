package imjames.msu.edu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final static String USERNAME = "user";
    private final static String PASSWORD = "pass";
    private SharedPreferences settings = null;

    private String userName = "";
    private String passWord = "";

    private int gridSize = 0;
    private boolean success = false;

    private CheckBox rememberMe() {
        return (CheckBox)findViewById(R.id.rememberMeCheckBox);
    }

    public static final String SIZE = "SIZE";
    public static final String NAME1 = "NAME1";
    public static final String NAME2 = "NAME2";
    public static final String USER = "USERNAME";

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

        //get saved user name and password from preferences
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        userName = settings.getString(USERNAME, "");
        passWord = settings.getString(PASSWORD, "");
        setUI();
    }

    /**
     * Set all user interface components to the current state
     */
    private void setUI() {
        CheckBox c = (CheckBox) findViewById(R.id.rememberMeCheckBox);
        c.setChecked(true);
        EditText viewUser = (EditText) findViewById(R.id.userName);
        EditText viewPass = (EditText) findViewById(R.id.password);
        viewUser.setText(userName);
        viewPass.setText(passWord);
    }

    public void onCreateUser(View view) {
        Intent intent = new Intent(this, NewUserActivity.class);
        startActivity(intent);
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
//        Intent intent = new Intent(this, SteamActivity.class);
//        intent.putExtra(SIZE, gridSize);
//        intent.putExtra(NAME1,getUsername().getText().toString());
//        intent.putExtra(NAME2,getPassword().getText().toString());
        onLoginUser(view);
        //startActivity(intent);
    }
    /**
     * The size choice spinner
     */
    private Spinner getSpinner() {
        return (Spinner) findViewById(R.id.SizeSpinner);
    }

    private EditText getUsername() {
        return (EditText) findViewById(R.id.userName);
    }

    private EditText getPassword() {
        return (EditText) findViewById(R.id.password);
    }

    public void startWait(){
        Intent intent = new Intent(this, WaitingActivity.class);
        intent.putExtra(SIZE, gridSize);
        intent.putExtra(USER,getUsername().getText().toString());
        startActivity(intent);
    }

    public void onLoginUser(View view) {
        final View v = view;

        new Thread(new Runnable() {
            @Override
            public void run() {
                final Cloud cloud = new Cloud();
                success = cloud.loginUser(getUsername().getText().toString(), getPassword().getText().toString());
                if(success)
                {
                    CheckBox b = rememberMe();
                    boolean val = b.isChecked();
                    if(val == true)
                    {
                        //save to preferences
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(USERNAME, getUsername().getText().toString());
                        editor.putString(PASSWORD, getPassword().getText().toString());
                        editor.commit();
                    }
                    else{
                        //save nothing to preferences if not checked
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(USERNAME, "");
                        editor.putString(PASSWORD, "");
                        editor.commit();
                    }
                    startWait();
                }
                else
                {
                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(v.getContext(), "Wrong username or password", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}



