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
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class WaitingActivity extends AppCompatActivity {
    public final static String PLAYER = "player";
    public final static String PLAYER2 = "player2";
    public final static String START_PLAYER = "start";
    public final static String USERNAME= "username";

    String username = "";
    String player2 = "";
    String start = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        // gridSize = getIntent().getIntExtra(MainActivity.SIZE,0);
        username = getIntent().getStringExtra(MainActivity.USER);
        addUser();
    }

    public void addUser()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Cloud cloud = new Cloud();
                String success = cloud.joinUser(username);
                if(success!="0")
                {
                    setStart(success);
                    //start = success;
                }
                else
                {

                }
            }
        }).start();

        checkUser();

    }

    public void checkUser() {
        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                final Cloud cloud = new Cloud();
                String success = cloud.isGameReady(username);
                if (success != "")
                {
                    //stop timer
                    timer.cancel();
                    timer.purge();
                    //set opponent - remove quotes around the strings
                    success = success.substring(1, success.length()-1);
                    player2 = success;
                    startGame(start);
                }
                else
                {

                }
            }
        };
        timer.schedule(timerTask, 0,1000);
    }

    public void setStart(String s)
    {
        start = s;
    }


    public void startGame(String s){
        Intent intent = new Intent(this, SteamActivity.class);
        if(s.equals("1"))
        {
            intent.putExtra(PLAYER,username);
            intent.putExtra(PLAYER2,player2);
        }
        else{
            intent.putExtra(PLAYER,player2);
            intent.putExtra(PLAYER2,username);
        }
        intent.putExtra(START_PLAYER, s);
        intent.putExtra(USERNAME, username);
        startActivity(intent);
    }
}




