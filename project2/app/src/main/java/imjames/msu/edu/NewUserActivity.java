package imjames.msu.edu;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewUserActivity extends AppCompatActivity {

    private String username = "";
    private String password1 = "";
    private String password2 = "";
    private EditText getUserName() {
        return (EditText)findViewById(R.id.createUsernameEditText);
    }
    private EditText getPsw1() {
        return (EditText)findViewById(R.id.createPasswordEditText);
    }
    private EditText getPsw2() {
        return (EditText)findViewById(R.id.createPasswordConfirmEditText);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        //Log.d("p1",password1);
        final Button button = (Button) findViewById(R.id.createButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onCreateUser(view);
            }
        });
    }

    public boolean createUserCheck() {
        username = getUserName().getText().toString();
        password1 = getPsw1().getText().toString();
        password2 = getPsw2().getText().toString();
        if (password1.equals(password2)) {
            Log.d("password: ",password1.toString());
            return true;
        }
        return false;
    }

    public void back(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onCreateUser(View view) {
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
        if (createUserCheck()) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    final Cloud cloud = new Cloud();
                    cloud.createUser(username,password2);
                    back();
                }
            }).start();
        } else {
            Toast.makeText(view.getContext(), "Please make sure your passwords match", Toast.LENGTH_SHORT).show();
        }
    }
}