package com.demo.tavish.hemantapp.CustomTexts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by tavish on 6/6/2017.
 */

public class TextAlegreyaSansLight extends TextView {

  public TextAlegreyaSansLight(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  public TextAlegreyaSansLight(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public TextAlegreyaSansLight(Context context) {
    super(context);
    init();
  }

  private void init() {
    Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
        "fonts/AlegreyaSans-Light.ttf");
    setTypeface(tf);
  }




}
