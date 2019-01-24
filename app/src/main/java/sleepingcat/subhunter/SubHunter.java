package sleepingcat.subhunter;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.util.Log;
import android.widget.ImageView;
import java.util.Random;

public class SubHunter extends Activity {

    int numberHorizontalPixels;
    int numberVerticalPixels;
    int blockSize;
    int gridWidth = 40;
    int gridHeight;
    float horizontalTouched = -100;
    float verticalTouched = -100;
    int subHorizontalPosition;
    int subVerticalPosition;
    boolean hit = false;
    int shotsTaken;
    int distanceFromSub;
    boolean debugging = true;
    // Drawing objects
    ImageView gameView;
    Bitmap blankBitmap;
    Canvas canvas;
    Paint paint;

    /*
        Android runs this code just before
        the player sees the app.
        This makes it a good place to add
        the code for the one-time setup phase.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the screen resolution
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // Initialize size variables
        numberHorizontalPixels = size.x;
        numberVerticalPixels = size.y;
        blockSize = numberHorizontalPixels / gridWidth;
        gridHeight = numberVerticalPixels / blockSize;

        // Initialize drawing stuff
        blankBitmap = Bitmap.createBitmap(numberHorizontalPixels,
                numberVerticalPixels, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(blankBitmap);
        gameView = new ImageView(this);
        paint = new Paint();

        setContentView(gameView);

        Log.d("Debugging", "In onCreate");
        newGame();
        draw();
    }

    /*
        This code will execute when a new
        game needs to be started. It will
        happen when the app is first started
        and after the player wins a game
     */
    void newGame() {
        Random randGenerator = new Random();
        subHorizontalPosition = randGenerator.nextInt(gridWidth);
        subVerticalPosition = randGenerator.nextInt(gridHeight);
        shotsTaken = 0;

        Log.d("Debugging", "In newGame");
    }

    /*
        Here we will do all the drawing.
        The grid lines, the HUD and
        the touch indicator
     */
    void draw() {
        gameView.setImageBitmap(blankBitmap);

        // Draw the white background
        canvas.drawColor(Color.argb(255, 255, 255, 255));

        // Draw some lines
        paint.setColor(Color.argb(255, 0, 0, 0));

        // Vertical
        for (int i = 1; i < gridWidth; i++) {
            canvas.drawLine(blockSize * i, 0,
                    blockSize * i, numberVerticalPixels - 1,
                    paint);
        }
        // Horizontal
        for (int i = 1; i < gridHeight; i++) {
            canvas.drawLine(0, blockSize * i,
                    numberHorizontalPixels - 1, blockSize * i,
                    paint);
        }

        // Draw player shot
        paint.setColor(Color.argb(255, 255, 0, 0));
        canvas.drawRect(horizontalTouched * blockSize,
                verticalTouched * blockSize,
                (horizontalTouched * blockSize) + blockSize,
                (verticalTouched * blockSize) + blockSize,
                paint);

        // Draw the HUD
        paint.setTextSize(blockSize);
        paint.setColor(Color.argb(255, 0, 0, 255));
        canvas.drawText("Shots Taken: " + shotsTaken + "  Distance: " + distanceFromSub,
                blockSize, blockSize, paint);

        Log.d("Debugging", "In draw");
        if (debugging) {
            printDebuggingText();
        }
    }

    /*
        This part of the code will
        handle detecting that the player
        has tapped the screen
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        Log.d("Debugging", "In onTouchEvent");

        // Take a shot when player releases press
        if((motionEvent.getAction() &
                MotionEvent.ACTION_MASK) ==
                MotionEvent.ACTION_UP) {
            takeShot(motionEvent.getX(), motionEvent.getY());
        }

        return true;
    }

    /*
        The code here will execute when
        the player taps the screen, It will
        calculate the distance from the sub
        and decide a hit or a miss
     */
    void takeShot(float touchX, float touchY) {
        Log.d("Debugging", "In takeShot");

        shotsTaken++;

        horizontalTouched = (int) touchX / blockSize;
        verticalTouched = (int) touchY / blockSize;

        hit = horizontalTouched == subHorizontalPosition &&
                verticalTouched == subVerticalPosition;

        int horizontalGap = (int) horizontalTouched - subHorizontalPosition;
        int verticalGap = (int) verticalTouched - subVerticalPosition;

        distanceFromSub = (int) Math.sqrt(
                (horizontalGap * horizontalGap) + (verticalGap * verticalGap)
        );

        if (hit) {
            boom();
        } else {
            draw();
        }
    }

    // This code says "BOOM!"
    void boom() {
        gameView.setImageBitmap(blankBitmap);

        canvas.drawColor(Color.argb(200, 200, 200, 200));

        paint.setColor(Color.argb(255, 255, 255, 255));
        paint.setTextSize(blockSize * 10);

        canvas.drawText("BOOM!", blockSize * 4,
                blockSize * 14, paint);

        paint.setTextSize(blockSize * 2);
        canvas.drawText("Take a shot to start again",
                blockSize * 8,
                blockSize * 18,
                paint);

        newGame();
    }

    // This code prints the debugging text
    void printDebuggingText() {
        Log.d("numberHorizontalPixels",
                "" + numberHorizontalPixels);
        Log.d("numberVerticalPixels",
                "" + numberVerticalPixels);

        Log.d("blockSize", "" + blockSize);
        Log.d("gridWidth", "" + gridWidth);
        Log.d("gridHeight", "" + gridHeight);
        Log.d("horizontalTouched",
                "" + horizontalTouched);
        Log.d("verticalTouched",
                "" + verticalTouched);
        Log.d("subHorizontalPosition",
                "" + subHorizontalPosition);
        Log.d("subVerticalPosition",
                "" + subVerticalPosition);

        Log.d("hit", "" + hit);
        Log.d("shotsTaken", "" + shotsTaken);
        Log.d("debugging", "" + debugging);

        Log.d("distanceFromSub",
                "" + distanceFromSub);
    }
}
