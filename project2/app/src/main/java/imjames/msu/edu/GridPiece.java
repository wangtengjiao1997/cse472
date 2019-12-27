package imjames.msu.edu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public class GridPiece {
    /**
     * grid area this pipe is a member of
     */
    private Grid grid = null;
    /**
     * The puzzle piece ID
     */
    private int id;
    /**
     * The image for the actual piece.
     */
    private Bitmap piece;

    /**
     * x location.
     * We use relative x locations in the range 0-1 for the center
     * of the puzzle piece.
     */
    private float x = 0;

    /**
     * y location
     */
    private float y = 0;

    private float pieceAngle = 0;

    public float getPieceAngle(){return pieceAngle;}

    public float getPiecePrevAngle(){return prevAngle;}


    /**
     * Original X location in the playing area
     */
    private float pipeOriginalX = 0;

    public boolean getNorth(){return connect[0];}

    public boolean getEast(){return connect[1];}

    public boolean getSouth(){return connect[2];}

    public boolean getWest(){return connect[3];}
    /**
     * Set Original X location in the playing area
     */
    public void setPipeOriginalX(float x) {
        pipeOriginalX = x;
    }

    /**
     * Get Original X location in the playing area
     */
    public float getPipeOriginalX() {
        return pipeOriginalX;
    }

    /**
     * Y location in the playing area
     */
    private float pipeOriginalY = 0;

    /**
     * Set Original Y location in the playing area
     */
    public void setPipeOriginalY(float y) {
        pipeOriginalY = y;
    }

    /**
     * Get Original X location in the playing area
     */
    public float getPipeOriginalY() {
        return pipeOriginalY;
    }

    public float prevAngle = 0;



    public int extraX = 0;
    public int getExtraX(){return extraX;}
    public int getExtraY(){return extraY;}

    public void setExtraX(int x){extraX=x;}
    public void setExtraY(int y){extraY=y;}
    public int extraY = 0;

    /**
     * Depth-first visited visited
     */
    public boolean visited = false;

    private Context c;

    /**
     * Array that indicates which sides of this pipe
     * has flanges. The order is north, east, south, west.
     * <p>
     * As an example, a T that has a horizontal pipe
     * with the T open to the bottom would be:
     * <p>
     * false, true, true, true
     */
    private boolean[] connect = {false, false, false, false};

    /**
     * We consider a piece to be in the right location if within
     * this distance.
     */
    final static float SNAP_DISTANCE = 25f;

    public int getId() {
        return id;
    }

    public int player;

    public void setPlayer(int p) {
        player = p;
    }

    /**
     * Constructor
     *
     * @param north True if connected north
     * @param east  True if connected east
     * @param south True if connected south
     * @param west  True if connected west
     */
    public GridPiece(Context context, int id, boolean north, boolean east, boolean south, boolean west, Grid g, int p) {
        this.id = id;
        piece = BitmapFactory.decodeResource(context.getResources(), id);
        connect[0] = north;
        connect[1] = east;
        connect[2] = south;
        connect[3] = west;
        grid = g;
        player = p;
        c = context;
    }

    public void rotate(float dAngle, float x1, float y1, int gridS) {
        pieceAngle += dAngle;

        // Compute the radians angle
        double rAngle = Math.toRadians(dAngle);

        float ca = (float) Math.cos(rAngle);
        float sa = (float) Math.sin(rAngle);
        float xp = (x - x1) * ca - (y - y1) * sa + x1;
        float yp = (x - x1) * sa + (y - y1) * ca + y1;

        x = xp;
        y = yp;
    }

    /**
     * Determine the angle for two touches
     *
     * @param x1 Touch 1 x
     * @param y1 Touch 1 y
     * @param x2 Touch 2 x
     * @param y2 Touch 2 y
     * @return computed angle in degrees
     */
    public float angle(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.toDegrees(Math.atan2(dy, dx));
    }

    public void setPieceAngle(float ang) {
        pieceAngle = ang;
    }

    public void setPiecePrevAngle(float ang) {
        prevAngle = ang;
    }


    /**
     * Draw the grid piece
     *
     * @param canvas      Canvas we are drawing on
     * @param marginX     Margin x value in pixels
     * @param marginY     Margin y value in pixels
     * @param gridSize    Size we draw the grid in pixels
     * @param scaleFactor Amount we scale the grid pieces when we draw them
     */
    public void draw(Canvas canvas, int marginX, int marginY, int size,
                     int gridSize, float scaleFactor) {

        float scaley = scaleFactor / piece.getWidth();
        float scalex = scaleFactor / piece.getHeight();

        Log.i("width", "width: " + piece.getWidth());

        canvas.save();

        if (this.id == R.drawable.gauge) {
            canvas.translate(marginX + ((x + 1) * (gridSize / size)), ((y + 1) * (gridSize / size)));
        } else {
            canvas.translate(marginX + ((x+extraX) * (gridSize / size)), ((y+extraY) * (gridSize / size)));
        }

        // Scale it to the right size
        canvas.scale(scalex, scaley);

        canvas.rotate(pieceAngle);

        // This magic code makes the center of the piece at 0, 0
        canvas.translate(-piece.getWidth() / 2f, -piece.getHeight() / 2f);

        // Draw the bitmap
        int width = piece.getWidth() / 2;
        int height = piece.getHeight() / 2;

        if (pieceAngle == 90 || pieceAngle == 270) {
            canvas.drawBitmap(piece, width, -height, null);
        } else {
            canvas.drawBitmap(piece, width, height, null);
        }

        canvas.restore();
    }

    public void drawOption(Canvas canvas, int marginX, int marginY, int size,
                           int gridSize, float scaleFactor) {
        float scaley = scaleFactor / piece.getWidth();
        float scalex = scaleFactor / piece.getHeight();

        Log.i("width", "width: " + piece.getWidth());

        canvas.save();

        canvas.translate(marginX + x * gridSize, marginY + y * gridSize);

        // Scale it to the right size
        canvas.scale(scalex, scaley);

        canvas.rotate(pieceAngle);

        // This magic code makes the center of the piece at 0, 0
        canvas.translate(-piece.getWidth() / 2f, -piece.getHeight() / 2f);

        // Draw the bitmap
        int width = piece.getWidth() / 2;
        int height = piece.getHeight() / 2;

        canvas.drawBitmap(piece, width, height, null);

        canvas.restore();

    }

    public void drawTime(Canvas canvas,int marginY, int size,
                         int gridSize, float scaleFactor) {
        float scaley = scaleFactor / piece.getWidth();
        float scalex = scaleFactor / piece.getHeight();

        Log.i("width", "width: " + piece.getWidth());

        canvas.save();

        canvas.translate(x * gridSize, marginY + y * gridSize);

        // Scale it to the right size
        canvas.scale(scalex, scaley);

        canvas.rotate(pieceAngle);

        // This magic code makes the center of the piece at 0, 0
        canvas.translate(-piece.getWidth() / 2f, -piece.getHeight() / 2f);

        // Draw the bitmap
        int width = piece.getWidth() / 2;
        int height = piece.getHeight() / 2;

        canvas.drawBitmap(piece, width, height, null);

        canvas.restore();
    }
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }


    /**
     * Test to see if we have touched a puzzle piece
     * @param testX X location as a normalized coordinate (0 to 1)
     * @param testY Y location as a normalized coordinate (0 to 1)
     * @param gridSize the size of the puzzle in pixels
     * @param scaleFactor the amount to scale a piece by
     * @return true if we hit the piece
     */
    public boolean hit(float testX, float testY,
                       int gridSize, float scaleFactor) {

        Log.i("hit::","piecex: "+x + "  piecey: "+y);
        // Make relative to the location and size to the piece size
        int pX = (int)((testX - x) * gridSize );
        int pY = (int)((testY - y) * gridSize );

        Log.i("px::","piecepx: "+pX + "  piecepy: "+pY);

        Log.i("test::","width: "+testX + "  height: "+testY);
        if(pX < 0 || pX >= piece.getWidth() ||
                pY < 0 || pY >= piece.getHeight()) {
            return false;
        }

        // We are within the rectangle of the piece.
        // Are we touching actual picture?
        return true;
       // return (piece.getPixel(pX, pY) & 0xff000000) != 0;
    }
    /**
     * Move the puzzle piece by dx, dy
     * @param dx x amount to move
     * @param dy y amount to move
     */
    public void move(float dx, float dy) {
        x += dx;
        y += dy;
    }

    /**
     * If we are within SNAP_DISTANCE of the correct
     * answer, snap to the correct answer exactly.
     * @return
     */
    public boolean maybeSnap(GridPiece pipe, int gridSize, int size, int marginY,int marginX) {
        float oneSlot = (gridSize/size);
        float X =  (pipe.x * (oneSlot));
        float Y =  (pipe.y * (oneSlot));
        float newAngle = 0;

        float newX = x;
        float newY = y;
        float tempAngle = pieceAngle%360;

        //need to snap to nearest angle
        if (Math.abs(tempAngle) >= 315 && Math.abs(tempAngle) % 360 < 45) {
            newAngle = 0;
        } else if (Math.abs(tempAngle) >= 45 && Math.abs(tempAngle) < 135) {
            if(pieceAngle>0){
                newAngle = 90;
                newX = newX - (float) (1.0/size);
            }
            else {
                newAngle = -90;
                newY = newY - (float) (1.0/size);
                extraY = 1;
            }
        } else if (Math.abs(tempAngle) >= 135 && Math.abs(tempAngle) < 225) {
            if(pieceAngle>0) {
                newAngle = 180;
                newX = newX - (float) (1.0/size);
                newY = newY - (float) (1.0/size);
                extraX = 1;
                extraY = 1;
            }
            else{
                newAngle= -180;
                newX = newX - (float) (1.0/size);
                newY = newY - (float) (1.0/size);
                extraX = 1;
                extraY = 1;
            }
        } else if (Math.abs(tempAngle) >= 225 && Math.abs(tempAngle) < 315) {
            if(pieceAngle>0) {
                newAngle = 270;
                newY = newY - (float) (1.0/size);
                extraX = 1;
                extraY = 1;
            }
            else{
                newAngle= -270;
                newX = newX - (float) (1.0/size);
                extraX = 1;
            }
        }
        int pieceSet = 0;
        int times = (int) Math.abs(prevAngle - newAngle) / 90;

        //if to the right of a piece
        if(Math.abs(((newX*gridSize) + marginX) - ((X)+oneSlot+marginX)) < SNAP_DISTANCE &&
                Math.abs(newY*gridSize + marginY - (Y)) < SNAP_DISTANCE) {
            if(grid.getPipes()[(int)pipe.x+1][(int)pipe.y]!=null || player!=pipe.player){return false;}
            //need to make sure you can connect based on the North, East, South, West values
            if(pieceAngle>=0){updateDirectionsPos(times);}
            if(pieceAngle<0){updateDirectionsNeg(times);}
            boolean val = pipe.connect[3];
            boolean val2 = pipe.connect[1];
            if(connect[3] == true && pipe.connect[1] == true) {
                x = (pipe.x + 1);
                y = (pipe.y);
                prevAngle = newAngle;
                pieceAngle = newAngle;

                pieceSet = 1;
            }
            else{
                if(pieceAngle>=0){updateDirectionsNeg(times);}
                if(pieceAngle<0){updateDirectionsPos(times);}
            }
        }


        //if below a piece
        if(Math.abs(((newX*gridSize) + marginX) - ((X)+marginX)) < SNAP_DISTANCE &&
                Math.abs(newY*gridSize + marginY - ((Y)+oneSlot)) < SNAP_DISTANCE) {
            if(grid.getPipes()[(int)pipe.x][(int)pipe.y+1]!=null || player!=pipe.player){return false;}
            //need to make sure you can connect based on the North, East, South, West values
            //both need to have an open hinge
            if(pieceAngle>=0){updateDirectionsPos(times);}
            if(pieceAngle<0){updateDirectionsNeg(times);}
            if(connect[0] == true && pipe.connect[2] == true) {
                x = (pipe.x);
                y = (pipe.y+1);
                prevAngle = newAngle;
                pieceAngle = newAngle;

                pieceSet = 1;
            }
            else{
                if(pieceAngle>=0){updateDirectionsNeg(times);}
                if(pieceAngle<0){updateDirectionsPos(times);}
            }
        }


        //if above a piece
        if(Math.abs(((newX*gridSize) + marginX) - ((X)+marginX)) < SNAP_DISTANCE &&
                Math.abs(newY*gridSize + marginY - ((Y)-oneSlot)) < SNAP_DISTANCE) {
            if(grid.getPipes()[(int)pipe.x][(int)pipe.y-1]!=null || player!=pipe.player){return false;}
            //need to make sure you can connect based on the North, East, South, West values
            //both need to have an open hinge
            if(pieceAngle>=0){updateDirectionsPos(times);}
            if(pieceAngle<0){updateDirectionsNeg(times);}
            if(connect[2] == true && pipe.connect[0] == true) {
                x = (pipe.x);
                y = (pipe.y-1);
                prevAngle = newAngle;
                pieceAngle = newAngle;
                pieceSet = 1;
            }
            else{
                if(pieceAngle>=0){updateDirectionsNeg(times);}
                if(pieceAngle<0){updateDirectionsPos(times);}
            }
        }


        //to left of piece
        if(Math.abs(((newX*gridSize) + marginX) - ((X - oneSlot)+marginX)) < SNAP_DISTANCE &&
                Math.abs(newY*gridSize + marginY - ((Y))) < SNAP_DISTANCE) {
            if(grid.getPipes()[(int)pipe.x][(int)pipe.y+1]!=null || player!=pipe.player){return false;}
            //need to make sure you can connect based on the North, East, South, West values
            //both need to have an open hinge
            if(pieceAngle>=0){updateDirectionsPos(times);}
            if(pieceAngle<0){updateDirectionsNeg(times);}
            //if both sides are flanges
            if(connect[1] == true && pipe.connect[3] == true) {
                x = (pipe.x-1);
                y = (pipe.y);
                prevAngle = newAngle;
                pieceAngle = newAngle;

                pieceSet = 1;
            }
            else{
                if(pieceAngle>=0){updateDirectionsNeg(times);}
                if(pieceAngle<0){updateDirectionsPos(times);}
            }
        }
        if(pieceSet == 1){return true;}
        return false;
    }


    public void updateDirectionsPos(int num)
    {
        for(int i = 0; i<num; i++)
        {
            boolean zero = connect[0];
            connect[0] = connect[3];
            connect[3] = connect[2];
            connect[2] = connect[1];
            connect[1] = zero;
        }
    }

    public void updateDirectionsNeg(int num)
    {
        for(int i = 0; i<num; i++)
        {
            boolean zero = connect[0];
            connect[0] = connect[1];
            connect[1] = connect[2];
            connect[2] = connect[3];
            connect[3] = zero;
        }
    }

    /**
     * Determine if this piece is snapped in place
     * @return true if snapped into place
     */
    public boolean isSnapped() {
        return false;
    }

    /**
     * Search to see if there are any downstream of this pipe
     *
     * This does a simple depth-first search to find any connections
     * that are not, in turn, connected to another pipe. It also
     * set the visited flag in all pipes it does visit, so you can
     * tell if a pipe is reachable from this pipe by checking that flag.
     * @return True if no leaks in the pipe
     */
    public boolean search() {
        visited = true;

        for(int d=0; d<4; d++) {
            /*
             * If no connection this direction, ignore
             */
            if(!connect[d]) {
                continue;
            }

            GridPiece n = neighbor(d);
            if(n == null) {
                // We leak
                // We have a connection with nothing on the other side
                return false;
            }

            // What is the matching location on
            // the other pipe. For example, if
            // we are looking in direction 1 (east),
            // the other pipe must have a connection
            // in direction 3 (west)
            int dp = (d + 2) % 4;
            if(!n.connect[dp]) {
                // We have a bad connection, the other side is not
                // a flange to connect to
                return false;
            }

            if(n.visited) {
                // Already visited this one, so no leaks this way
                continue;
            } else {
                // Is there a lead in that direction
                if(!n.search()) {
                    // We found a leak downstream of this pipe
                    return false;
                }
            }
        }
        // Yah, no leaks
        return true;
    }

    public void search2() {
        visited = true;

        for(int d=0; d<4; d++) {
            /*
             * If no connection this direction, ignore
             */
            if(!connect[d]) {
                continue;
            }

            GridPiece n = neighbor(d);
            if(n == null || n.getId() == R.drawable.leak) {
                // We leak
                // We have a connection with nothing on the other side
                //draw leak
                switch(d) {
                    case 0:
                        grid.leakLoc[(int)x][(int)y-1] = 1;
                        break;
                    case 1:
                        grid.leakLoc[(int)x+1][(int)y] = 90;
                        break;
                    case 2:
                        grid.leakLoc[(int)x][(int)y+1] = 180;
                        break;
                    case 3:
                        grid.leakLoc[(int)x-1][(int)y] = 270;
                        break;
                }
                continue;
            }

            // What is the matching location on
            // the other pipe. For example, if
            // we are looking in direction 1 (east),
            // the other pipe must have a connection
            // in direction 3 (west)
            int dp = (d + 2) % 4;
            if(!n.connect[dp]) {
                // We have a bad connection, the other side is not
                //draw leak
                switch(dp) {
                    case 0:
                        grid.leakLoc[(int)x][(int)y-1] = 1;
                        break;
                    case 1:
                        grid.leakLoc[(int)x+1][(int)y] = 90;
                        break;
                    case 2:
                        grid.leakLoc[(int)x][(int)y+1] = 180;
                        break;

                    case 3:
                        grid.leakLoc[(int)x-1][(int)y] = 270;
                        break;
                }
                continue;
            }

            if(n.visited) {
                // Already visited this one, so no leaks this way
                continue;
            } else {
                // Is there a lead in that direction
                n.search2();
            }
        }
        // Yah, no leaks

    }


    /**
     * Find the neighbor of this pipe
     * @param d Index (north=0, east=1, south=2, west=3)
     * @return Pipe object or null if no neighbor
     */
    private GridPiece neighbor(int d) {
        switch(d) {
            case 0:
                return grid.getPipe((int)x, (int)y-1);

            case 1:
                return grid.getPipe((int)x+1, (int)y);

            case 2:
                return grid.getPipe((int)x, (int)y+1);

            case 3:
                return grid.getPipe((int)x-1, (int)y);
        }

        return null;
    }

    /**
     * Get the playing area
     * @return Playing area object
     */
    public Grid getPlayingArea() {
        return grid;
    }

    /**
     * Set the playing area and location for this pipe
     * @param playingArea Playing area we are a member of
     * @param x X index
     * @param y Y index
     */
    public void set(Grid playingArea, int x, int y) {
        this.grid = playingArea;
        this.x = x;
        this.y = y;
    }

    /**
     * Has this pipe been visited by a search?
     * @return True if yes
     */
    public boolean beenVisited() {
        return visited;
    }

    /**
     * Set the visited flag for this pipe
     * @param visited Value to set
     */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public int getPlayer() {
        return player;
    }

    public boolean getConnect(int i) {
        if (i >= 0 && i < 4) {
            return connect[i];
        } else
            return false;
    }

    public void saveXML(XmlSerializer xml)throws IOException {
        xml.startTag(null, "piece");
        xml.attribute(null, "x", Integer.toString((int)x));//
        xml.attribute(null, "y", Integer.toString((int)y));//
        xml.attribute(null, "angle", Float.toString(pieceAngle));//
        xml.attribute(null, "previousAngle", Float.toString(prevAngle));//
        xml.attribute(null, "id", Integer.toString(id));//
        xml.attribute(null, "connect0", Boolean.toString(connect[0]));//
        xml.attribute(null, "connect1", Boolean.toString(connect[1]));//
        xml.attribute(null, "connect2", Boolean.toString(connect[2]));//
        xml.attribute(null, "connect3", Boolean.toString(connect[3]));//
        xml.attribute(null, "player", Integer.toString(player));//
        xml.attribute(null, "extraX", Integer.toString(extraX));//
        xml.attribute(null, "extraY", Integer.toString(extraY));//
        xml.attribute(null, "visited", Boolean.toString(visited));//
        xml.endTag(null, "piece");
    }
}
