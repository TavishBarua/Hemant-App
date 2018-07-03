package com.demo.tavish.hemantapp.CustomTexts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by tavish on 6/3/2017.
 */

public class TextLatoReg extends TextView {

  public TextLatoReg(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  public TextLatoReg(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public TextLatoReg(Context context) {
    super(context);
    init();
  }

  private void init() {
    Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
        "fonts/Lato-Regular.ttf");
    setTypeface(tf);
  }



}
