package imjames.msu.edu;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class WinnerActivity extends AppCompatActivity {
    String Winner = "";
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner);

        Winner = getIntent().getStringExtra(SteamActivity.WINNER);
        TextView tv1 = (TextView) findViewById(R.id.winner);
        tv1.setText(Winner+ " "+getString(R.string.winner));

        //check if both players have made it to the end page
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Cloud cloud = new Cloud();
                boolean end = cloud.getEnd();
                if(end==true)
                {
                    endGame();
                }
            }
        }).start();
    }


    public void endGame() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                final Cloud cloud = new Cloud();
                cloud.endGame();
            }
        }).start();
    }

    public void startNewGame(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
