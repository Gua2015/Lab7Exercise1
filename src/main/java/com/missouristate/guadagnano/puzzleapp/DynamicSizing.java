package com.missouristate.guadagnano.puzzleapp;

import android.util.TypedValue;
import android.view.View.MeasureSpec;
import android.widget.TextView;

class DynamicSizing {
    public static final int MAX_FONT_SIZE = 200;
    public static final int MIN_FONT_SIZE = 1;

     // the following sets the maximum font size of tv so that the text inside tvfits on one line
    public static int setFontSizeToFitInView( TextView tv ) {
        // the TextView whose font size is changed
        int fontSize = MAX_FONT_SIZE;
        tv.setTextSize( TypedValue.COMPLEX_UNIT_SP, fontSize );
        tv.measure( MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED );
        int lines = tv.getLineCount( );
        if( lines > 0 ) {
            while( lines != 1 && fontSize >= MIN_FONT_SIZE + 2 ) {
                fontSize--;
                tv.setTextSize( TypedValue.COMPLEX_UNIT_SP, fontSize );
                tv.measure( MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED );
                lines = tv.getLineCount( );
            }
            tv.setTextSize( TypedValue.COMPLEX_UNIT_SP, --fontSize );
        }
        //returns the resulting font size
        return fontSize;
    }
}
