package com.android.Launcher1;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;



/* This Activity does nothing but receive USB_DEVICE_ATTACHED events from the
* USB service
*/
public final class UsbActivity extends Activity {

    private String USB_FOLDER = "/mnt/usbhost0/E1Player/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(USB_FOLDER));

        try {
            //startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Log.d("LOG EXC.", e.getMessage());
        }
        finish();
    }
}