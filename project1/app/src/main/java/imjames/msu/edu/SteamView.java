package imjames.msu.edu;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

@SuppressWarnings("ALL")
public class SteamView extends View {
    /**
     * The actual grid
     */
    private Grid grid;
    private int size;
    private String name1;
    private String name2;

    public SteamView(Context context) {
        super(context);
        //init(null, 0);
    }

    public SteamView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //init(attrs, 0);
    }

    public SteamView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //init(attrs, defStyle);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return grid.onTouchEvent(this, event);
    }

    //private void init(AttributeSet attrs, int defStyle) {
        //grid = new Grid(size, getContext());
    //}

    public void setGridSize(int pos)
    {
        switch(pos) {
            case 0:
                size = 5;
                grid = new Grid(size, getContext());
                grid.setNameOne(name1);
                grid.setNameTwo(name2);
                grid.setSize(5);
                grid.setView(this);
                break;

            case 1:
                size = 10;
                grid = new Grid(size, getContext());
                grid.setNameOne(name1);
                grid.setNameTwo(name2);
                grid.setSize(10);
                grid.setView(this);
                break;

            case 2:
                size = 20;
                grid = new Grid(size, getContext());
                grid.setNameOne(name1);
                grid.setNameTwo(name2);
                grid.setSize(20);
                grid.setView(this);
                break;
        }
    }

    public void setNameOne(String name){
        name1 = name;
        //grid.setNameOne(name);
    }

    public void setNameTwo(String name){
        name2 = name;
        //grid.setNameTwo(name);
    }

    public Grid getGrid(){return grid;}

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        grid.draw(canvas);
    }

    // In SteamView
    /**
     * Save the grid to a bundle
     * @param bundle The bundle we save to
     */
    public void saveInstanceState(Bundle bundle) {
        grid.saveInstanceState(bundle);
    }

    /**
     * Load the grid from a bundle
     * @param bundle The bundle we save to
     */
    public void loadInstanceState(Bundle bundle) {
        grid.loadInstanceState(bundle, this.getContext());
    }
    
}
