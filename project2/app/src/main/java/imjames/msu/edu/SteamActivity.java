package imjames.msu.edu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.util.Timer;
import java.util.TimerTask;


public class SteamActivity extends AppCompatActivity {

    public static final String WINNER = "WINNER";

    Timer timer;
    private boolean win;

    public int s = 20;

    public String username;

    public String name1;
    public String name2;

    public int winnerVal = 0;

    public int getTime(){return s;}

    public void setTime(int time){s = time;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steam);

        int gridSize = 0;
        name1 = getIntent().getStringExtra(WaitingActivity.PLAYER); //yours
        name2 = getIntent().getStringExtra(WaitingActivity.PLAYER2); //opponents
        username = getIntent().getStringExtra(WaitingActivity.USERNAME);
        username = username.trim();
        String start = getIntent().getStringExtra(WaitingActivity.START_PLAYER);
        getSteamView().setNameOne(name1);
        getSteamView().setNameTwo(name2);
        getSteamView().setGridSize(gridSize);
        getSteamView().getGrid().setActivity(this);
        getSteamView().getGrid().setCurrentPlayer(1);
        if(!name1.equals(username))
        {
            checkTurn();
        }
        else{
            timer = new Timer();
            startTimer(s);
        }

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
        String name;
        if(getSteamView().getGrid().getCurrent_player()==1){name = getSteamView().getGrid().getNameOne();}
        else{name = getSteamView().getGrid().getNameTwo();}
        switch (item.getItemId()) {
            case R.id.menu_install:
                if(name.equals(username)) {
                    if (getSteamView().getGrid().onReleased(getSteamView())) {
                        getSteamView().getGrid().changeTurn();
                        change();
                        getSteamView().getGrid().angle=-45;
                        return true;
                    } else {
                        //make a toast saying "couldn't install"
                        getSteamView().post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getSteamView().getContext(), R.string.no_install, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    return true;
                }
                return false;

            case R.id.menu_discard:
                if(name.equals(username)) {
                    boolean val = getSteamView().getGrid().discardPipe(getSteamView());
                    if (val == true) {
                        getSteamView().getGrid().changeTurn();
                        change();
                        getSteamView().getGrid().angle=-45;
                    }
                    return true;
                }
                return false;

            case R.id.menu_open:
                if(name.equals(username)) {
                    win = getSteamView().getGrid().openValve();
                    if (win == true) {
                        endTimer();
                    } else {
                        endTimer();
                    }
                    return true;
                }
                return false;
            case R.id.menu_surrender:
                if(name.equals(username)) {
                    getSteamView().getGrid().changeTurn();
                    onSurrender(getSteamView());
                    return true;
                }
                return false;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void change()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Cloud cloud = new Cloud();
                boolean success = cloud.saveGameToCloud(getSteamView().getGrid(), winnerVal);
                if(success)
                {
                    timer.cancel();
                    timer.purge();
                    if(winnerVal!=1) {
                        checkTurn(); //done with turn, need to start waiting for next turn
                    }
                }
            }
        }).start();
    }

    public void onSurrender(View view)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Cloud cloud = new Cloud();
                boolean success = cloud.updateEnd();
            }
        }).start();
        winnerVal = 1;
        change();
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
                    if (numSec > 0) {
                        numSec--;
                        getSteamView().getGrid().angle += 13.5;
                        getSteamView().invalidate();
                    } else {
                        getSteamView().getGrid().changeTurn();
                        boolean val = getSteamView().getGrid().discardPipe(getSteamView());
                        timer.cancel();
                        timer.purge();
                        getSteamView().getGrid().angle = -45;
                        change();
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

    /**
     * Called when this application is no longer the foreground application.
     * Passively exiting the game is the same as a surrender, i.e. they automatically lose the game.
     */
    @Override
    protected void onPause() {
        if(winnerVal != 1)
        {
            getSteamView().getGrid().changeTurn();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Cloud cloud = new Cloud();
                boolean end = cloud.getEnd();
                if(end==true || winnerVal==1)
                {
                }
                else{
                    boolean success = cloud.updateEnd();
                    winnerVal=1;
                    change();
                }
            }
        }).start();
        super.onPause();
    }


    public void endActivity()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Cloud cloud = new Cloud();
                boolean success = cloud.updateEnd();
            }
        }).start();

        Intent intent = new Intent(this, WinnerActivity.class);
        intent.putExtra(WINNER, getSteamView().getGrid().getWinner());
        startActivity(intent);
    }


    public void checkTurn() {
        final Timer timer2 = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                final Cloud cloud = new Cloud();
                String turn = cloud.checkTurn();
                turn = turn.trim();
                Log.i("turn", turn);
                if (turn.equals(username)) // it's now the players turn, they can load the game
                {
                    //load the game
                    cloud.loadGameFromCloud(getSteamView().getGrid());
                    String winner = cloud.checkWin();
                    if(winner.equals("WIN"))
                    {
                        endActivity();
                    }

                    timer2.cancel();
                    timer2.purge();

                    //start turn timer
                    timer = new Timer();
                    startTimer(s);
                }
                else // not their turn yet, need to keep waiting
                {

                }
            }
        };
        timer2.schedule(timerTask, 0,1000);
    }



}
