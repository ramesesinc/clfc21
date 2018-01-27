package queue.gov.rameses.com.queuecodegenerator.util;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Created by Dino Quimson on 2/18/15.
 */
public class Screen{

    static Activity act;

    public Screen(Activity act){
        this.act = act;
    }

    public int getWidth(){
        int width = 250;
        try {
            DisplayMetrics displayMeterics = new DisplayMetrics();
            act.getWindowManager().getDefaultDisplay().getMetrics(displayMeterics);
            width = displayMeterics.widthPixels;
        }catch(Exception e){
            Log.v("Dino Quimson",e.toString());
        }
        return width;
    }

    public int getHeight(){
        int height = 100;
        try {
            DisplayMetrics displayMeterics = new DisplayMetrics();
            act.getWindowManager().getDefaultDisplay().getMetrics(displayMeterics);
            height = displayMeterics.heightPixels;
        }catch(Exception e){
            Log.v("Error",e.toString());
        }
        return height;
    }
}
