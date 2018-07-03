package com.demo.tavish.hemantapp.CustomTexts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by tavish on 6/3/2017.
 */

public class TextLatoThin extends TextView {

  public TextLatoThin(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context);
  }

  public TextLatoThin(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public TextLatoThin(Context context) {
    super(context);
    init(context);
  }

  public void init(Context c) {
    Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
        "fonts/Lato-Thin.ttf");
    setTypeface(tf);
  }

  public void setTextType(Context c,TextView tv) {
    Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
            "fonts/Lato-Thin.ttf");
    tv.setTypeface(tf);
  }

}
