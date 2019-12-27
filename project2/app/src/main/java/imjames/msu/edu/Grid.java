package imjames.msu.edu;

import java.io.IOException;
import java.io.StringReader;
import java.nio.channels.Pipe;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.util.ArrayList;
import java.util.TimerTask;

import static android.graphics.Paint.Style.FILL;
import static java.util.Collections.shuffle;

@SuppressWarnings("ALL")
public class Grid {

    /**
     * The name of the bundle keys to save the puzzle
     */
    private final static String LOCATIONS = "Grid.locations";
    private final static String IDS = "Grid.ids";
    private final static String CONNECTIONS = "Grid.connections";
    private final static String PLAYERS = "Grid.players";
    private final static String ANGLES = "Grid.angles";
    private final static String PREVIOUS_ANGLES = "Grid.previousAngles";
    private final static String EXTRAX = "Grid.extraX";
    private final static String EXTRAY = "Grid.extraY";
    private final static String CURRENT_PLAYER = "Grid.currentP";
    private final static String GUAGE_ANGLE= "Grid.ang";
    private final static String TIME= "Grid.T";
    private final static String VISIT=  "Grid.visit";



    /**
     * Percentage of the display width or height that
     * is occupied by the grid.
     */
    final static float SCALE_IN_VIEW = 1f;

    SteamView view;

    SteamActivity activity;



    public void setActivity(SteamActivity s) {
        activity = s;
    }

    public void setView(SteamView v) {
        view = v;
    }

    /**
     * First touch status
     */
    private Touch touch1 = new Touch();

    /**
     * Second touch status
     */
    private Touch touch2 = new Touch();

    /**
     * Local class to handle the touch status for one touch.
     * We will have one object of this type for each of the
     * two possible touches.
     */
    private class Touch {
        /**
         * Touch id
         */
        public int id = -1;

        /**
         * Current x location
         */
        public float x = 0;

        /**
         * Current y location
         */
        public float y = 0;

        /**
         * Previous x location
         */
        public float lastX = 0;

        /**
         * Previous y location
         */
        public float lastY = 0;

        /**
         * Copy the current values to the previous values
         */
        public void copyToLast() {
            lastX = x;
            lastY = y;
        }

        /**
         * Change in x value from previous
         */
        public float dX = 0;

        /**
         * Change in y value from previous
         */
        public float dY = 0;

        /**
         * Compute the values of dX and dY
         */
        public void computeDeltas() {
            dX = x - lastX;
            dY = y - lastY;
        }

        public float startx = 0;

        public float starty = 0;
    }



    public ArrayList<GridPiece> randomPieces = new ArrayList<GridPiece>();


    /**
     * Collection of Grid pieces for dragging
     */
    public ArrayList<GridPiece> pieces_drag = new ArrayList<GridPiece>();


    /**
     * size of the grid the user chooses
     */
    public int size;


    /**
     * Width of the playing area (integer number of cells)
     */
    private int width;

    /**
     * Height of the playing area (integer number of cells)
     */
    private int height;

    public int[][] leakLoc;

    /**
     * Storage for the pipes
     * First level: X, second level Y
     */
    private GridPiece[][] pipes;

    public GridPiece[][] getPipes() {
        return pipes;
    }

    /**
     * Paint for filling the area the game is in
     */
    private Paint fillPaint;

    /**
     * Paint for filling the area the game is in
     */
    private Paint fillPaint2;

    /**
     * Paint for drawing the player names
     */
    private Paint playerPaint;

    /**
     * names of player one
     */
    private String nameOne;

    public String getNameOne(){
        return nameOne;
    }

    public String getNameTwo(){
        return nameTwo;
    }

    /**
     * names of player two
     */
    private String nameTwo;

    /**
     * Player who's turn it is
     */
    private int current_player;

    public int getCurrent_player() {
        return current_player;
    }

    public void setCurrentPlayer(int p)
    {
        current_player = p;
    }
    private GridPiece startPieceBitmap = null;
    private GridPiece startPieceBitmap2 = null;
    private GridPiece valve = null;
    private GridPiece valve2 = null;
    private GridPiece timebitmap = null;

    /**
     * This variable is set to a piece we are dragging. If
     * we are not dragging, the variable is null.
     */
    private GridPiece dragging = null;


    private GridPiece endPieceBitmap = null;
    private GridPiece endPieceBitmap2 = null;

    private Bitmap teePiece = null;
    private Bitmap straightPiece = null;
    private Bitmap p90Piece = null;
    private Bitmap capPiece = null;


    /**
     * The context of the grid
     */
    private Context mContext;

    public Context getContext() {
        return mContext;
    }

    /**
     * The size of the grid in pixels
     */
    private int gridSize;


    /**
     * How much we scale the puzzle pieces
     */
    private float scaleFactor;
    private float PressureFactor;

    /**
     * Left margin in pixels
     */
    private int marginX;

    /**
     * Top margin in pixels
     */
    private int marginY;

    /**
     * Most recent relative X touch when dragging
     */
    private float lastRelX;

    /**
     * Most recent relative Y touch when dragging
     */
    private float lastRelY;

    private float scaling = 1;
    private float scrollingX = 0;
    private float scrollingY = 0;
    private float offsetX = 0;
    private float offsetY = 0;

    private boolean scalingStatus = false;


    public float angle = -45;

    private int guageAngle1 = -45;
    private int guageAngle2 = -45;

    public float tempAngle = 0;

    private Context c;


    /**
     * Construct a playing area
     */
    public Grid(int gsize, Context context) {
        c = context;
        this.width = gsize;
        this.height = gsize;
        this.mContext = context;

        // Allocate the playing area
        // Java automatically initializes all of the locations to null
        pipes = new GridPiece[width][height];
        leakLoc = new int[width][height];

        teePiece = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.tee);
        straightPiece = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.straight);
        p90Piece = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.p90);
        capPiece = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.cap);

        startPieceBitmap = new GridPiece(context, R.drawable.straight, false, true,false,false, this, 1);
        add(startPieceBitmap, 0, gsize / 3);
        startPieceBitmap2 = new GridPiece(context, R.drawable.straight, false, true,false,false, this, 2);
        add(startPieceBitmap2, 0, (gsize / 3) * 2 + 1);

        valve = new GridPiece(context, R.drawable.handle, false, false, false, false, this, 1);
        valve2 = new GridPiece(context, R.drawable.handle, false, false, false, false, this, 2);
        valve.setX(0);
        valve.setY(gsize / 3);
        valve2.setX(0);
        valve2.setY((gsize / 3) * 2 + 1);

        endPieceBitmap = new GridPiece(context, R.drawable.gauge, false,false,false,true, this, 1);
        add(endPieceBitmap, gsize - 1, (gsize / 3) +1);
        endPieceBitmap2 = new GridPiece(context, R.drawable.gauge, false,false,false,true, this, 2);
        add(endPieceBitmap2, gsize - 1, (gsize / 3) * 2 + 2);


        //draw the pressure machine
        timebitmap = new GridPiece(context, R.drawable.streamgauge, false, false, false, false, this, 1);

        startPieceBitmap.setPieceAngle(90);
        endPieceBitmap.setPieceAngle(270);
        startPieceBitmap2.setPieceAngle(90);
        endPieceBitmap2.setPieceAngle(270);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(0xff90EE90);

        fillPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint2.setColor(0xffcccccc);

        playerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        playerPaint.setTypeface(Typeface.create("Arial", Typeface.BOLD_ITALIC));
        playerPaint.setColor(Color.BLACK);
        playerPaint.setStyle(FILL);
        playerPaint.setTextSize(50);

        // Load the grid pieces
        pieces_drag.add(new GridPiece(context, R.drawable.straight, true,false,true,false, this, 1));
        pieces_drag.add(new GridPiece(context, R.drawable.cap, false,false,true,false, this, 1));
        pieces_drag.add(new GridPiece(context, R.drawable.tee, true,true,true,false, this, 1));
        pieces_drag.add(new GridPiece(context, R.drawable.p90, false,true,true,false, this, 1));
        pieces_drag.add(new GridPiece(context, R.drawable.straight, true,false,true,false, this, 1));

        // Load the grid pieces to the random piece array
        randomPieces.add(new GridPiece(context, R.drawable.straight, true,false,true,false, this, 1));
        randomPieces.add(new GridPiece(context, R.drawable.cap, false,false,true,false, this, 1));
        randomPieces.add(new GridPiece(context, R.drawable.tee, true,true,true,false, this, 1));
        randomPieces.add(new GridPiece(context, R.drawable.p90, false,true,true,false, this, 1));
        randomPieces.add(new GridPiece(context, R.drawable.straight, true,false,true,false, this, 1));
    }

    /**
     * Get the playing area height
     *
     * @return Height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the playing area width
     *
     * @return Width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the pipe at a given location.
     * This will return null if outside the playing area.
     *
     * @param x X location
     * @param y Y location
     * @return Reference to Pipe object or null if none exists
     */
    public GridPiece getPipe(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return null;
        }

        return pipes[x][y];
    }

    /**
     * Add a pipe to the playing area
     *
     * @param pipe Pipe to add
     * @param x    X location
     * @param y    Y location
     */
    public void add(GridPiece pipe, int x, int y) {
        pipes[x][y] = pipe;
        pipe.set(this, x, y);
    }


    public void setSize(int grid) {
        size = grid;
    }

    public void setNameOne(String name) {
        nameOne = name;
    }

    public void setNameTwo(String name) {
        nameTwo = name;
    }

    // sets header depending on status of game
    public String setHeader(int curPlayer) {
        if (current_player == 1) {
            return nameOne + "'s turn!";
        } else {
            return nameTwo + "'s turn!";
        }
    }

    public void draw(Canvas canvas) {
        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(0xff008000);
        linePaint.setStrokeWidth(3);

        int wid = canvas.getWidth();
        int hit = canvas.getHeight();

        canvas.save();
        canvas.translate(scrollingX, scrollingY);
        canvas.scale(scaling, scaling, offsetX, offsetY);

        // Determine the minimum of the two dimensions
        int minDim = wid < hit ? wid : hit;

        gridSize = (int) (minDim * SCALE_IN_VIEW);

        // Compute the margins so we center the puzzle
        marginX = (wid - gridSize) / 2;
        marginY = (hit - gridSize) / 2;

        //spacing for the options at the bottom
        int choices = gridSize / 5;
        scaleFactor = (float) gridSize / size;
        PressureFactor = (float) gridSize / 5;

        canvas.drawRect(marginX, 0,
                marginX + gridSize, gridSize, fillPaint2);

        //draw grid lines
        for (int i = 1; i < size + 1; i++) {
            canvas.drawLine(0 + marginX, scaleFactor * i, gridSize + marginX, scaleFactor * i, linePaint);
        }

        for (int i = 1; i < size + 1; i++) {
            canvas.drawLine(scaleFactor * i + marginX, 0, scaleFactor * i + marginX, gridSize, linePaint);
        }

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        String text = setHeader(current_player);
        paint.setTextSize(100);
        canvas.drawText(text, 20, 100, paint);

        if (size == 5) {
            //if screen not landscape draw green bar below grid
            if (minDim == wid) {
                canvas.drawRect(marginX, gridSize, wid, hit, fillPaint);

            } else//if screen is landscape put green bar to side
            {
                canvas.drawRect(marginX + gridSize, 0, marginX * 2 + gridSize, gridSize, fillPaint);

            }
        } else if (size == 10) {
            //if screen not landscape draw green bar below grid
            if (minDim == wid) {
                canvas.drawRect(marginX, gridSize, wid, hit, fillPaint);
            } else//if screen is landscape put green bar to side
            {
                canvas.drawRect(marginX + gridSize, 0, marginX * 2 + gridSize, gridSize, fillPaint);
            }
        } else {
            //if screen not landscape draw green bar below grid
            if (minDim == wid) {
                canvas.drawRect(marginX, gridSize, wid, hit, fillPaint);
            } else//if screen is landscape put green bar to side
            {
                canvas.drawRect(marginX + gridSize, 0, marginX * 2 + gridSize, gridSize, fillPaint);
            }
        }

        if (minDim == wid) {
            int i = 0;
            for (GridPiece piece : pieces_drag) {
                if (piece.getX() == 0 && piece.getY() == 0) {
                    piece.setX(.15f * i);
                    piece.setY(.9f);
                    piece.setPipeOriginalX(.15f * i);
                    piece.setPipeOriginalY(.9f);
                }
                piece.drawOption(canvas, marginX, marginY, size, gridSize, scaleFactor);
                i = i + 1;
            }

            timebitmap.setX(.8f);
            timebitmap.setY(.915f);
            timebitmap.drawTime(canvas, marginY, size, gridSize, gridSize / 5);

            int startx = (int)(.8*wid) + ((gridSize/5)/2); //wid/10*9;
            int starty = gridSize + (marginY) + 15; //gridSize+marginY/100*105;
            int endx;
            int endy;
            int length = choices / 2;
            Map<String, Integer> map = rotateline(startx, starty, length, angle);
            endx = map.get("x");
            endy = map.get("y");

            canvas.drawLine(startx, starty, endx, endy, linePaint);

        } else {
            int i = 0;
            for (GridPiece piece : pieces_drag) {
                if (piece.getX() == 0 && piece.getY() == 0) {
                    piece.setX(1.0f);
                    piece.setY(0.2f * i);
                    piece.setPipeOriginalX(1.0f);
                    piece.setPipeOriginalY(0.2f * i);
                }
                piece.drawOption(canvas, marginX, marginY, size, gridSize, scaleFactor);
                i = i + 1;
            }

            timebitmap.setX(0f);
            timebitmap.setY(0.5f);
            timebitmap.drawTime(canvas, marginY, size, gridSize, gridSize / 5);

            int startx =  choices / 2;
            int starty = hit/2+choices/2;

            int endx;
            int endy;
            int length = choices / 2;

            Map<String, Integer> map = rotateline(startx, starty, length, angle);
            endx = map.get("x");
            endy = map.get("y");

            canvas.drawLine(startx, starty, endx, endy, linePaint);
        }
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (pipes[x][y] != null) {
                    pipes[x][y].draw(canvas, marginX, marginY, size, gridSize, scaleFactor);
                }
            }
        }

        Map<String, Integer> map = rotateline(gridSize +marginX - (choices/2) , (gridSize/size) *(size / 3+1) + (choices/3) , choices/4, guageAngle1);
        int endx = map.get("x");
        int endy = map.get("y");
        canvas.drawLine(gridSize +marginX - (choices/2), (gridSize/size) *(size / 3 +1) +(choices/3), endx, endy, linePaint);
        Map<String, Integer> map2 = rotateline(gridSize +marginX - (choices/2) , (gridSize/size) *((size / 3) * 2 + 2) + (choices/3) , choices/4, guageAngle2);
        int endx2 = map2.get("x");
        int endy2 = map2.get("y");
        canvas.drawLine(gridSize +marginX - (choices/2), (gridSize/size) *((size / 3) * 2 + 2) +(choices/3), endx2, endy2, linePaint);


        canvas.drawText(nameOne, marginX, (size / 3) * (gridSize / size), playerPaint);
        canvas.drawText(nameTwo, marginX, (((size / 3) * 2) + 1) * (gridSize / size), playerPaint);
        valve.draw(canvas, marginX, marginY, size, gridSize, scaleFactor);
        valve2.draw(canvas, marginX, marginY, size, gridSize, scaleFactor);
        canvas.restore();
    }

    //for rotate the line for pressure timer
    public Map<String, Integer> rotateline(int startx, int starty, int length, float angle) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        float endx;
        float endy;
        float x;
        float y;
        // Compute the radians angle
        double rAngle = Math.toRadians(angle);
        if (angle <= 0) {
            x = (float) Math.cos(rAngle) * length;
            y = (float) Math.sin(rAngle) * length;
            endx = startx - x;
            endy = starty - y;
        } else if (0 < angle && angle <= 90) {
            x = (float) Math.cos(rAngle) * length;
            y = (float) Math.sin(rAngle) * length;
            endx = startx - x;
            endy = starty - y;
        } else if (angle > 90 && angle <= 180) {
            x = (float) Math.cos(rAngle) * length;
            y = (float) Math.sin(rAngle) * length;
            endx = startx - x;
            endy = starty - y;
        } else {
            x = (float) Math.cos(rAngle) * length;
            y = (float) Math.sin(rAngle) * length;
            endx = startx - x;
            endy = starty - y;
        }
        map.put("x", (int) endx);
        map.put("y", (int) endy);
        return map;
    }

    /**
     * Handle a touch event from the view.
     *
     * @param view  The view that is the source of the touch
     * @param event The motion event describing the touch
     * @return true if the touch is handled.
     */
    public boolean onTouchEvent(View view, MotionEvent event) {
        int id = event.getPointerId(event.getActionIndex());
        //
        // Convert an x,y location to a relative location in the
        // puzzle.
        //
        String name;
        if(current_player==1){name = nameOne;}
        else{name = nameTwo;}
        if(name.equals(activity.username)) {
            float relX = (event.getX() - marginX) / gridSize;
            float relY = (event.getY() - marginY) / gridSize;


            switch (event.getActionMasked()) {


                case MotionEvent.ACTION_DOWN:
                    Log.i("on touched", "the x" + relX + " the y " + relY);
                    touch1.id = id;
                    touch2.id = -1;
                    getPositions(view, event);
                    touch1.copyToLast();
                    if (dragging == null)
                        onTouched(relX, relY);
                    return true;

                case MotionEvent.ACTION_POINTER_DOWN:
                    scalingStatus = true;
                    if (touch1.id >= 0 && touch2.id < 0) {
                        touch2.id = id;
                        getPositions(view, event);
                        touch2.copyToLast();
                        offsetX = (touch1.x + touch2.x) / 2;
                        offsetY = (touch1.y + touch2.y) / 2;
                        touch1.startx = touch1.x;
                        touch1.starty = touch1.y;
                        return true;
                    }
                    break;

                case MotionEvent.ACTION_UP:
//                if(dragging != null) {
//                    dragging = null;
//                    return true;
//                }
                    touch1.id = -1;
                    touch2.id = -1;
                    if (scalingStatus) {
                        scalingStatus = false;
                        return true;
                    } else
                        return true;

                case MotionEvent.ACTION_POINTER_UP:
                    if (id == touch2.id) {
                        touch2.id = -1;
                    } else if (id == touch1.id) {
                        // Make what was touch2 now be touch1 by
                        // swapping the objects.
                        Touch t = touch1;
                        touch1 = touch2;
                        touch2 = t;
                        touch2.id = -1;
                    }
                    if (dragging != null) {
                        if ((tempAngle % 360) > 315 && (tempAngle % 360) <= 45) {
                            dragging.setPieceAngle(0 + dragging.getPieceAngle());
                        } else if ((tempAngle % 360) > 45 && (tempAngle % 360) <= 135) {
                            dragging.setPieceAngle(dragging.getPieceAngle() + 90);
                        } else if ((tempAngle % 360) > 135 && (tempAngle % 360) <= 225) {
                            dragging.setPieceAngle(dragging.getPieceAngle() + 180);
                        } else if ((tempAngle % 360) + dragging.getPieceAngle() > 225 && (tempAngle % 360) + dragging.getPieceAngle() <= 315) {
                            dragging.setPieceAngle(dragging.getPieceAngle() + 270);
                        }
                    }
                    tempAngle = 0;
                    view.invalidate();
                    return true;
                case MotionEvent.ACTION_CANCEL:
                    touch1.id = -1;
                    touch2.id = -1;
                    view.invalidate();
                    return true;
                //break;

                case MotionEvent.ACTION_MOVE:
                    // If we are dragging, move the piece and force a redraw
                    if (dragging != null) {
                        getPositions(view, event);
                        move(dragging, relX - lastRelX, relY - lastRelY);
                        lastRelX = relX;
                        lastRelY = relY;
                        //view.invalidate();
                        return true;
                    } else {
                        getPositions(view, event);
                        scalingStatus = true;
                        move2();
                        view.invalidate();
                        return true;
                    }

            }
            return false;
        }
        else
        {
            return false;
        }
    }


    /**
     * Determine the distance for two touches
     *
     * @param x1 Touch 1 x
     * @param y1 Touch 1 y
     * @param x2 Touch 2 x
     * @param y2 Touch 2 y
     * @return computed distance
     */
    private float distance(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Scale the grid around the point x1, y1
     *
     * @param factor scale factor
     */
    public void scale(float factor) {
        scaling *= factor;
        if (scaling < 1) {
            scaling = 1;
        }
    }

    private void move(GridPiece dragging, float d1, float d2) {
        // If no touch1, we have nothing to do
        // This should not happen, but it never hurts
        // to check.
        if (touch1.id < 0) {
            return;
        }

        if (touch1.id >= 0) {
            // At least one touch
            // We are moving
            touch1.computeDeltas();
            dragging.move((touch1.dX) / gridSize, (touch1.dY) / gridSize);
            view.invalidate();
        }
        if (touch2.id >= 0) {
            // Two touches
            /*
             * Rotation
             */
            float angle1 = dragging.angle(touch1.x, touch1.y, touch2.lastX, touch2.lastY);
            float angle2 = dragging.angle(touch1.x, touch1.y, touch2.x, touch2.y);
            float da = angle2 - angle1;
            float midX = (touch2.x + touch1.x) / 2;
            float midY = (touch2.y + touch1.y) / 2;
            dragging.rotate(da, midX / gridSize, midY / gridSize, gridSize);

        }

    }

    private void move2() {

        if (touch1.id < 0) {
            return;
        }

        if (touch1.id >= 0) {
            // At least one touch
            // We are moving
            touch1.computeDeltas();
            if (touch2.id >= 0) {
                if (scalingStatus) {
                    /*
                     *Scaling
                     */
                    float distance1 = distance(touch1.lastX, touch1.lastY, touch2.lastX, touch2.lastY);
                    float distance2 = distance(touch1.x, touch1.y, touch2.x, touch2.y);
                    float dist = distance2 / distance1;
                    scale(dist);
                    scalingStatus = false;
                }
            }
            /*
             *Scrolling
             */
            scrollingX += touch1.dX * scaling;
            scrollingY += touch1.dY * scaling;
        }
    }


    /**
     * Get the positions for the two touches and put them
     * into the appropriate touch objects.
     *
     * @param event the motion event
     */
    private void getPositions(View view, MotionEvent event) {
        for (int i = 0; i < event.getPointerCount(); i++) {

            // Get the pointer id
            int id = event.getPointerId(i);

            // Get coordinates
            float x = (event.getX(i) - marginX) / (scaling);
            float y = (event.getY(i) - marginY) / (scaling);

            if (id == touch1.id) {
                touch1.copyToLast();
                touch1.x = x;
                touch1.y = y;
            } else if (id == touch2.id) {
                touch1.copyToLast();
                touch2.x = x;
                touch2.y = y;
            }
        }

        view.invalidate();
    }

    /**
     * Handle a touch message. This is when we get an initial touch
     *
     * @param x x location for the touch, relative to the puzzle - 0 to 1 over the puzzle
     * @param y y location for the touch, relative to the puzzle - 0 to 1 over the puzzle
     * @return true if the touch is handled
     */
    private boolean onTouched(float x, float y) {

        // Check each piece to see if it has been hit
        // We do this in reverse order so we find the pieces in front
        for (int p = pieces_drag.size() - 1; p >= 0; p--) {
            if (pieces_drag.get(p).hit(x, y, gridSize, scaleFactor)) {
                // We hit a piece!
                dragging = pieces_drag.get(p);
                dragging.setPlayer(current_player);
                lastRelX = x;
                lastRelY = y;

                return true;
            }
        }

        return false;
    }

    /**
     * Handle a release of a touch message.
     *
     * @return true if the touch is handled
     */
    public boolean onReleased(View view) {

        if (dragging != null) {
            for (int X = 0; X < size; X++) {
                for (int Y = 0; Y < size; Y++) {
                    if (pipes[X][Y] != null) {
                        if (dragging.maybeSnap(pipes[X][Y], gridSize, size, marginY, marginX)) {
                            dragging.maybeSnap(endPieceBitmap, gridSize, size, marginY, marginX);
                            dragging.maybeSnap(endPieceBitmap2, gridSize, size, marginY, marginX);
                            shuffle(randomPieces);
                            GridPiece temp = new GridPiece(getContext(), randomPieces.get(0).getId(), randomPieces.get(0).getNorth(), randomPieces.get(0).getEast(), randomPieces.get(0).getSouth(), randomPieces.get(0).getWest(), this,1);
                            //float dx = dragging.getPipeOriginalX();
                            //float ddy = dragging.getPipeOriginalY();
                            temp.setX(dragging.getPipeOriginalX());
                            temp.setY(dragging.getPipeOriginalY());
                            temp.setPipeOriginalX(dragging.getPipeOriginalX());
                            temp.setPipeOriginalY(dragging.getPipeOriginalY());

                            // We have snapped into place
                            //add piece to grid
                            pipes[(int) dragging.getX()][(int) dragging.getY()] = dragging;
                            //remove from options
                            pieces_drag.remove(dragging);
                            pieces_drag.add(temp);
                            //shuffle(pieces_drag);
                            view.invalidate();
                            dragging = null;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean discardPipe(View view) {
        if (dragging != null) {

            shuffle(randomPieces);
            GridPiece temp = new GridPiece(getContext(), randomPieces.get(0).getId(), randomPieces.get(0).getNorth(), randomPieces.get(0).getEast(), randomPieces.get(0).getSouth(), randomPieces.get(0).getWest(), this, 1);
            temp.setX(dragging.getPipeOriginalX());
            temp.setY(dragging.getPipeOriginalY());
            temp.setPipeOriginalX(dragging.getPipeOriginalX());
            temp.setPipeOriginalY(dragging.getPipeOriginalY());

            pieces_drag.remove(dragging);
            pieces_drag.add(temp);
            dragging = null;
            view.invalidate();
            return true;
        }
        return false;
    }

    public void changeTurn() {
        if (current_player == 1) {
            current_player = 2;
        } else {
            current_player = 1;
        }
        view.invalidate();
    }

    public String getWinner() {
        if (current_player == 1)
            return nameOne;
        else
            return nameTwo;
    }

    /**
     * Load the girdpiece from a bundle
     *
     * @param bundle The bundle we save to
     */
    public void loadInstanceState(Bundle bundle, Context context) {
        int[][] locations = (int[][]) bundle.getSerializable(LOCATIONS);
        int[][] ids = (int[][]) bundle.getSerializable(IDS);
        boolean[][] connections = (boolean[][]) bundle.getSerializable(CONNECTIONS);
        int[][] players = (int[][]) bundle.getSerializable(PLAYERS);
        int[][] angles = (int[][]) bundle.getSerializable(ANGLES);
        int[][] prevAngles = (int[][]) bundle.getSerializable(PREVIOUS_ANGLES);
        int[][] extraX = (int[][]) bundle.getSerializable(EXTRAX);
        int[][] extraY = (int[][]) bundle.getSerializable(EXTRAY);
        boolean[][] visit = (boolean[][]) bundle.getSerializable(VISIT);
        int time = (int) bundle.getSerializable(TIME);
        activity.setTime(time);
        float ang = (float) bundle.getSerializable(GUAGE_ANGLE);
        angle = ang;
        int currentP = (int) bundle.getSerializable(CURRENT_PLAYER);
        current_player = currentP;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (ids[i][j] != -1) {
                    boolean north = connections[i * 4][j * 4];
                    boolean east = connections[i * 4 + 1][j * 4 + 1];
                    boolean south = connections[i * 4 + 2][j * 4 + 2];
                    boolean west = connections[i * 4 + 3][j * 4 + 3];
                    int id = ids[i][j];
                    int player = players[i][j];
                    int angle = angles[i][j];
                    int prevAngle = prevAngles[i][j];
                    int extrax = extraX[i][j];
                    int extray = extraY[i][j];
                    boolean v = visit[i][j];

                    GridPiece piece = new GridPiece(context, id, north, east, south, west, this, player);
                    piece.setExtraX(extrax);
                    piece.setExtraY(extray);
                    piece.setPieceAngle((float)(angle));
                    piece.setPiecePrevAngle((float)(prevAngle));
                    piece.setVisited(v);
                    add(piece, locations[i * 2][j * 2], locations[i * 2 + 1][j * 2 + 1]);
                }
            }
        }
    }

    /**
     * Save the puzzle to a bundle
     *
     * @param bundle The bundle we save to
     */
    public void saveInstanceState(Bundle bundle) {
        int[][] locations = new int[height * 2][width * 2];
        int[][] angles = new int[height][width];
        int[][] preAngles = new int[height][width];
        int[][] ids = new int[height][width];
        int[][] players = new int[height][width];
        boolean[][] connections = new boolean[height * 4][width * 4];

        boolean[][] visit = new boolean[height][width];

        int t = activity.getTime();
        int[][] extrasX = new int[height][width];
        int[][] extrasY = new int[height][width];

        int currentP = current_player;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                GridPiece piece = pipes[i][j];
                if (piece != null) {
                    locations[i * 2][j * 2] = (int) piece.getX();
                    locations[i * 2 + 1][j * 2 + 1] = (int) piece.getY();
                    angles[i][j] = (int)piece.getPieceAngle();
                    preAngles[i][j] = (int)piece.getPiecePrevAngle();
                    connections[i * 4][j * 4] = piece.getConnect(0);
                    connections[i * 4 + 1][j * 4 + 1] = piece.getConnect(1);
                    connections[i * 4 + 2][j * 4 + 2] = piece.getConnect(2);
                    connections[i * 4 + 3][j * 4 + 3] = piece.getConnect(3);
                    ids[i][j] = piece.getId();
                    players[i][j] = piece.getPlayer();
                    extrasX[i][j] = piece.getExtraX();
                    extrasY[i][j] = piece.getExtraY();
                    visit[i][j] = piece.beenVisited();

                } else {
                    locations[i * 2][j * 2] = -1;
                    locations[i * 2 + 1][j * 2 + 1] = -1;
                    angles[i][j] = - 1;
                    ids[i][j] = -1;
                    players[i][j] = -1;
                    preAngles[i][j] = -1;
                }
            }
        }
        float ang = angle;
        bundle.putSerializable(LOCATIONS, locations);
        bundle.putSerializable(IDS, ids);
        bundle.putSerializable(CONNECTIONS, connections);
        bundle.putSerializable(PLAYERS, players);
        bundle.putSerializable(ANGLES, angles);
        bundle.putSerializable(PREVIOUS_ANGLES, preAngles);
        bundle.putSerializable(EXTRAX, extrasX);
        bundle.putSerializable(EXTRAY, extrasY);
        bundle.putSerializable(CURRENT_PLAYER, currentP);
        bundle.putSerializable(GUAGE_ANGLE, ang);
        bundle.putSerializable(TIME, t);
        bundle.putSerializable(VISIT, visit);
    }

    public void saveXML(XmlSerializer xml)throws IOException {
        xml.startTag(null, "game");
        xml.attribute(null, "width", Integer.toString(width));
        xml.attribute(null, "height", Integer.toString(height));
        xml.attribute(null, "playerOneName", nameOne);
        xml.attribute(null, "playerTwoName", nameTwo);
        xml.attribute(null, "currentPlayer", Integer.toString(current_player));
        xml.startTag(null, "pieces");
        for(int i=0;  i<height; i++) {
            for(int j=0;  j<width; j++) {
                GridPiece piece = pipes[i][j];
                if (piece != null) {
                    piece.saveXML(xml);
                }
            }
        }
        xml.endTag(null, "pieces");
        xml.endTag(null, "game");
    }


    //get state from cloud and pass it to this function to load everything
    public void loadXML(String state) throws IOException, XmlPullParserException {
        XmlPullParser xml = XmlPullParserFactory.newInstance().newPullParser();
        xml.setInput(new StringReader(state));


        xml.nextTag();
        xml.require(XmlPullParser.START_TAG, null, "game");
        width = Integer.parseInt(xml.getAttributeValue(null, "width"));
        height = Integer.parseInt(xml.getAttributeValue(null, "height"));
        nameOne = xml.getAttributeValue(null, "playerOneName");
        nameTwo = xml.getAttributeValue(null, "playerTwoName");
        current_player = Integer.parseInt(xml.getAttributeValue(null,"currentPlayer"));
        xml.nextTag();
        xml.require(XmlPullParser.START_TAG, null, "pieces");
        xml.nextTag();

        while (xml.getName() != "piece") {
            if(xml.getEventType() == XmlPullParser.START_TAG) {
                boolean conn0 = Boolean.parseBoolean(xml.getAttributeValue(null, "connect0"));
                boolean conn1 = Boolean.parseBoolean(xml.getAttributeValue(null, "connect1"));
                boolean conn2 = Boolean.parseBoolean(xml.getAttributeValue(null, "connect2"));
                boolean conn3 = Boolean.parseBoolean(xml.getAttributeValue(null, "connect3"));
                Float angle = Float.parseFloat(xml.getAttributeValue(null, "angle"));
                Float prevAngle = Float.parseFloat(xml.getAttributeValue(null, "previousAngle"));
                int x = Integer.parseInt(xml.getAttributeValue(null, "x"));
                int y = Integer.parseInt(xml.getAttributeValue(null, "y"));
                int id = Integer.parseInt(xml.getAttributeValue(null, "id"));
                int player = Integer.parseInt(xml.getAttributeValue(null,"player"));
                int extraX = Integer.parseInt(xml.getAttributeValue(null,"extraX"));
                int extraY = Integer.parseInt(xml.getAttributeValue(null,"extraY"));
                boolean visited = Boolean.parseBoolean(xml.getAttributeValue(null,"visited"));


                GridPiece piece = new GridPiece(getContext(), id, conn0, conn1, conn2, conn3, this, player);
                piece.setPieceAngle(angle);
                add(piece, x, y);
                piece.setExtraY(extraY);
                piece.setExtraX(extraX);
                piece.setVisited(visited);
                piece.setPiecePrevAngle(prevAngle);
            }

            xml.nextTag();
        }
        view.invalidate();
    }



    public boolean openValve(){
        GridPiece s;
        //open the valve bitmap
        if(current_player == 1)
        {
            valve.setPieceAngle(90);
            s = startPieceBitmap;
        }
        else
        {
            valve2.setPieceAngle(90);
            s = startPieceBitmap2;
        }
        view.invalidate();

        /*
         * Set the visited flags to false
         */
        for(GridPiece[] row: pipes) {
            for(GridPiece pipe : row) {
                if (pipe != null&& pipe.getPlayer() == current_player) {
                    pipe.setVisited(false);
                }
            }
        }
        /*
         * The pipe itself does the actual search
         */

        boolean win = s.search();

        for(GridPiece[] row: pipes) {
            for(GridPiece pipe : row) {
                if (pipe != null&& pipe.getPlayer() == current_player) {
                    pipe.setVisited(false);
                }
            }
        }
        //need to have reached the end
        if(current_player == 1)
        {
            if(endPieceBitmap.search()==false){
                win = false;
            }
        }
        else{
            if(endPieceBitmap2.search()==false){
                win = false;
            }
        }
        for(GridPiece[] row: pipes) {
            for(GridPiece pipe : row) {
                if (pipe != null&& pipe.getPlayer() == current_player) {
                    pipe.setVisited(false);
                }
            }
        }
        s.search2();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if(leakLoc[i][j] == 1 || leakLoc[i][j] == 90 || leakLoc[i][j] == 180 || leakLoc[i][j] == 270)
                {
                    GridPiece leak = new GridPiece(c, R.drawable.leak, false, true,false,false, this, 1);
                    if(leakLoc[i][j] != 1)
                    {
                        leak.setPieceAngle(leakLoc[i][j]);
                    }
                    if(leakLoc[i][j]==180 || leakLoc[i][j] == 270)
                    {
                        leak.extraY = 1;
                        leak.extraX = 1;
                    }
                    add(leak, i, j);
                }
            }
        }
        if(current_player == 1)
        {
            if(win == true) {
                guageAngle1 = 225;
            }
            else{
                guageAngle1 = 90;
            }
        }
        else{
            if(win == true) {
                guageAngle2 = 225;
            }
            else{
                guageAngle2 = 90;
            }
        }
        view.invalidate();
        return win;
    }


}
