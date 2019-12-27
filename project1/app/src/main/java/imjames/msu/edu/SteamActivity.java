package imjames.msu.edu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class SteamActivity extends AppCompatActivity {

    public static final String WINNER = "WINNER";

    Timer timer;
    private boolean win;

    public int s = 20;

    public int getTime(){return s;}

    public void setTime(int time){s = time;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steam);

        int gridSize = getIntent().getIntExtra(MainActivity.SIZE,0);
        String name1 = getIntent().getStringExtra(MainActivity.NAME1);
        String name2 = getIntent().getStringExtra(MainActivity.NAME2);
        getSteamView().setNameOne(name1);
        getSteamView().setNameTwo(name2);
        getSteamView().setGridSize(gridSize);
        getSteamView().getGrid().setActivity(this);
        timer = new Timer();
        startTimer(s);

        SteamView sView = getSteamView();
        if(savedInstanceState != null)
        {
            sView.loadInstanceState(savedInstanceState);
        }
    }

    /**
     * The hatter view object
     */
    private SteamView getSteamView() {
        return (SteamView)this.findViewById(R.id.steamView);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_grid, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_install:
                if (getSteamView().getGrid().onReleased(getSteamView()))
                {
                    getSteamView().getGrid().changeTurn();
                    timer.cancel();
                    timer.purge();
                    startTimer(20);
                    return true;
                }
                else
                {
                    //make a toast saying "couldn't install"
                    getSteamView().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getSteamView().getContext(), R.string.no_install, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return true;

            case R.id.menu_discard:
                boolean val = getSteamView().getGrid().discardPipe(getSteamView());
                if(val == true)
                {
                    getSteamView().getGrid().changeTurn();
                    timer.cancel();
                    timer.purge();
                    startTimer(20);
                }
                return true;

            case R.id.menu_open:
                win = getSteamView().getGrid().openValve();
                if (win == true)
                {
                    endTimer();
                }
                else
                {
                    endTimer();
                }
                return true;
            case R.id.menu_surrender:
                getSteamView().getGrid().changeTurn();
                onSurrender(getSteamView());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void onSurrender(View view)
    {
        //need to pass the winner to this intent.
        Intent intent = new Intent(this, WinnerActivity.class);
        intent.putExtra(WINNER, getSteamView().getGrid().getWinner());
        startActivity(intent);
    }

    public void endTimer(){
        timer.cancel();
        timer.purge();
        timer = new Timer();
        TimerTask t = new EndTask();
        timer.schedule(t,3*1000,1*1000);  //subsequent rate
    }

    public void startTimer(int sec){
        TimerTask t = new RemindTask();
        timer = new Timer();
        s = sec;
        timer.schedule(t,0,1*1000);  //subsequent rate
        getSteamView().getGrid().angle=-45;
        getSteamView().getGrid().discardPipe(getSteamView());
    }

    class RemindTask extends TimerTask {
        int numSec = 20;
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (s > 0) {
                        s--;
                        getSteamView().getGrid().angle += 13.5;
                        getSteamView().invalidate();
                    } else {
                        getSteamView().getGrid().changeTurn();
                        timer.cancel();
                        timer.purge();
                        startTimer(20);
                    }
                }
            });
        }
    }


    class EndTask extends TimerTask {
        int numSec = 0;

        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (numSec > 0) {
                        numSec--;
                    } else {
                        if(win != true){
                            getSteamView().getGrid().changeTurn();
                        }
                        onSurrender(getSteamView());
                        timer.cancel();
                        timer.purge();
                    }
                }
            });
        }
    }

    /**
     * Save the instance state into a bundle
     *
     * @param bundle the bundle to save into
     */
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        timer.cancel();
        timer.purge();
        super.onSaveInstanceState(bundle);

        getSteamView().saveInstanceState(bundle);

    }

}
