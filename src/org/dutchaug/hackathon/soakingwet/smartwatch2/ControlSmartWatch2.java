/*
Copyright (c) 2011, Sony Ericsson Mobile Communications AB
Copyright (c) 2011-2013, Sony Mobile Communications AB

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of the Sony Ericsson Mobile Communications AB / Sony Mobile
 Communications AB nor the names of its contributors may be used to endorse or promote
 products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.dutchaug.hackathon.soakingwet.smartwatch2;

import java.io.ByteArrayOutputStream;

import org.dutchaug.hackathon.soakingwet.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ImageView;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlObjectClickEvent;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;
import com.sonyericsson.extras.liveware.extension.util.control.ControlViewGroup;

/**
 * The sample control for SmartWatch handles the control on the accessory. This class exists in one instance for every supported host application that we have registered to
 */
class ControlSmartWatch2 extends ControlExtension {

    private static final int MENU_ITEM_0 = 0;

    private Handler mHandler;

    private ControlViewGroup mLayout = null;

    private boolean mTextMenu = false;
    Bundle[] mMenuItemsText = new Bundle[3];
    Bundle[] mMenuItemsIcons = new Bundle[3];
    private Location location;

    /**
     * Create sample control.
     * 
     * @param hostAppPackageName Package name of host application.
     * @param context The context.
     * @param handler The handler to use
     */
    ControlSmartWatch2(final String hostAppPackageName, final Context context, Handler handler) {
        super(context, hostAppPackageName);
        if (handler == null) {
            throw new IllegalArgumentException("handler == null");
        }
        this.location = location;
        mHandler = handler;
        setupClickables(context);
        initializeMenus();

    }

    private void setupClickables(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.sample_control_2, null);
        mLayout = (ControlViewGroup) parseLayout(layout);
        if (mLayout != null) {
        }
    }

    private void initializeMenus() {
        mMenuItemsText[0] = new Bundle();
        mMenuItemsText[0].putInt(Control.Intents.EXTRA_MENU_ITEM_ID, MENU_ITEM_0);
        mMenuItemsText[0].putString(Control.Intents.EXTRA_MENU_ITEM_TEXT, "Refresh");
    }

    /**
     * Get supported control width.
     * 
     * @param context The context.
     * @return the width.
     */
    public static int getSupportedControlWidth(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_width);
    }

    /**
     * Get supported control height.
     * 
     * @param context The context.
     * @return the height.
     */
    public static int getSupportedControlHeight(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_height);
    }

    @Override
    public void onDestroy() {
        mHandler = null;
    };

    @Override
    public void onStart() {
        // Nothing to do. Animation is handled in onResume.
    }

    @Override
    public void onStop() {
        // Nothing to do. Animation is handled in onPause.
    }

    @Override
    public void onResume() {

        Bundle b1 = new Bundle();
        b1.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.sample_control_text_1);
        b1.putString(Control.Intents.EXTRA_TEXT, "Hoi");

        Bundle b4 = new Bundle();
        b4.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.sample_control_text_4);
        b4.putString(Control.Intents.EXTRA_TEXT, "Lars");

        Bundle jouwPlaatje = new Bundle();
        jouwPlaatje.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.animatedImage);

        byte[] bitmapdata = getWeatherClock();
        jouwPlaatje.putByteArray(Control.Intents.EXTRA_DATA, bitmapdata);

        Bundle[] data = new Bundle[4];
        data[0] = b1;
        data[1] = b4;
        data[2] = jouwPlaatje;

        showLayout(R.layout.sample_control_2, data);

    }

    private byte[] getWeatherClock() {
        LayoutInflater systemService = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView wc = (ImageView) systemService.inflate(R.layout.weather_clock, null);

        Bitmap bitmap = Bitmap.createBitmap(170, 170, Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);

        wc.setDrawingCacheEnabled(true);

        // Supply measurements
        wc.measure(MeasureSpec.makeMeasureSpec(canvas.getWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(canvas.getHeight(), MeasureSpec.EXACTLY));

        // Apply the measures so the layout would resize before drawing.
        wc.layout(0, 0, 170, 170);

        // and now the bmp object will actually contain the requested layout
        canvas.drawBitmap(wc.getDrawingCache(), 0, 0, new Paint());

        wc.draw(canvas);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();
        return bitmapdata;
    }

    @Override
    public void onTouch(final ControlTouchEvent event) {
        if (event.getAction() == Control.Intents.TOUCH_ACTION_RELEASE) {
        }
    }

    @Override
    public void onObjectClick(final ControlObjectClickEvent event) {
        if (event.getLayoutReference() != -1) {
            mLayout.onClick(event.getLayoutReference());
        }
    }

    @Override
    public void onKey(final int action, final int keyCode, final long timeStamp) {
        if (action == Control.Intents.KEY_ACTION_RELEASE && keyCode == Control.KeyCodes.KEYCODE_OPTIONS) {
            toggleMenu();
        }
        else if (action == Control.Intents.KEY_ACTION_RELEASE && keyCode == Control.KeyCodes.KEYCODE_BACK) {
        }
    }

    @Override
    public void onMenuItemSelected(final int menuItem) {
        if (menuItem == MENU_ITEM_0) {
            clearDisplay();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onResume();
                }
            }, 1000);
        }
    }

    private void toggleMenu() {
        if (mTextMenu) {
            showMenu(mMenuItemsIcons);
        }
        else {
            showMenu(mMenuItemsText);
        }
        mTextMenu = !mTextMenu;
    }

}
