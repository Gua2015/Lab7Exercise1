package com.missouristate.guadagnano.puzzleapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity
        implements View.OnTouchListener {
    public static int STATUS_BAR_HEIGHT = 24; // in dp
    public static int ACTION_BAR_HEIGHT = 56; // in dp
    private PuzzleView puzzleView;
    private Puzzle puzzle;
    private int statusBarHeight;
    private int actionBarHeight;
    private GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        puzzle = new Puzzle( );

        Point size = new Point( );
        getWindowManager( ).getDefaultDisplay( ).getSize( size );
        int screenHeight = size.y;
        int puzzleWidth = size.x;

        Resources res = getResources();
        DisplayMetrics metrics = res.getDisplayMetrics( );
        float pixelDensity = metrics.density;

        TypedValue tv = new TypedValue( );
        actionBarHeight = ( int ) ( pixelDensity * ACTION_BAR_HEIGHT );
        if( getTheme( ).resolveAttribute( android.R.attr.actionBarSize,
                tv, true ) )
            actionBarHeight = TypedValue.complexToDimensionPixelSize( tv.data,
                    metrics );

        statusBarHeight = ( int ) ( pixelDensity * STATUS_BAR_HEIGHT );
        int resourceId =
                res.getIdentifier( "status_bar_height", "dimen", "android" );
        if( resourceId != 0 ) // found resource for status bar height
            statusBarHeight = res.getDimensionPixelSize( resourceId );

        int puzzleHeight = screenHeight - statusBarHeight - actionBarHeight;
        puzzleView = new PuzzleView( this, puzzleWidth, puzzleHeight,
                puzzle.getNumberOfParts( ) );
        String [] scrambled = puzzle.scramble( );
        puzzleView.fillGui( scrambled );
        puzzleView.enableListener( this );
        setContentView( puzzleView );

        DoubleTapHandler dth = new DoubleTapHandler( );
        detector = new GestureDetector( this, dth );
        detector.setOnDoubleTapListener( dth );
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int index = puzzleView.indexOfTextView( v );
        int action = event.getAction( );
        switch( action ) {
            case MotionEvent.ACTION_DOWN:
                // initialize data before moving
                puzzleView.updateStartPositions( index, ( int ) event.getY( ) );
                // brings v to front
                puzzleView.bringChildToFront( v );
                break;
            case MotionEvent.ACTION_MOVE:
                // updates the y position TextView being moved
                puzzleView.moveTextViewVertically( index, ( int ) event.getY( ) );
                break;
            case MotionEvent.ACTION_UP:
                // swaps the 2 TextViews when move is complete
                int newPosition = puzzleView.tvPosition( index );
                puzzleView.placeTextViewAtPosition( index, newPosition );
                // if puzzle is solved disable listener to stop the game
                if( puzzle.solved( puzzleView.currentSolution( ) ) )
                    puzzleView.disableListener( );
                break;
        }
        return true;
    }

    public boolean onTouchEvent( MotionEvent event ) {
        detector.onTouchEvent( event );
        return true;
    }

    private class DoubleTapHandler
            extends GestureDetector.SimpleOnGestureListener {
        public boolean onDoubleTapEvent( MotionEvent event ) {
            int touchY = ( int ) event.getRawY( );

            // y coordinate of the touch within puzzleView is shown below
            int index = puzzleView.indexOfTextView( touchY
                    - actionBarHeight - statusBarHeight );
            if( puzzleView.getTextViewText( index )
                    .equals( puzzle.wordToChange( ) ) )
                puzzleView.setTextViewText( index, puzzle.replacementWord( ) );
            return  true;
        }
    }
}
