package com.basis.base.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Tsang on 2020/5/12
 */
public class ScreenUtils {

    /**
     *  隐藏虚拟按键，并且全屏
     */
    public static void hideBottomUIMenu(Activity activity,boolean isAll) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (isAll){
            params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        }else {
            params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY ;
        }
        window.setAttributes(params);
    }

    /**
     * 设置dialog全屏
     * @param dialog
     */
    public static void dialogFullScreen(Dialog dialog) {
        if (dialog != null) {
            final Window window = dialog.getWindow();
            if (window != null) {

                window.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                        fullScreen(window);
                    }
                });
            }
        }
    }

    public static void fullScreen(Window window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        window.getDecorView().setSystemUiVisibility(uiOptions);
    }
}
