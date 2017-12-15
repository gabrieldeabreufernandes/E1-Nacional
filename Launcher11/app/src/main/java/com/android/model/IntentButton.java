package com.android.model;

import android.content.Context;
import android.widget.ImageButton;

public class IntentButton extends ImageButton {

    private String intent;
    private boolean haspassword;

    public IntentButton(Context context) {
        super(context);
    }

    public IntentButton(Context context, String intent)
    {
        super(context);
        this.intent = intent;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public boolean getHaspassword() {
        return haspassword;
    }

    public void setHaspassword(boolean haspassword) {
        this.haspassword = haspassword;
    }
}
