package queue.gov.rameses.com.queuecodegenerator.dialog;

import android.app.Activity;
import android.app.AlertDialog;

/**
 * Created by Dino Quimson on 3/6/15.
 */
public class InfoDialog {

    private AlertDialog.Builder dialog;

    public InfoDialog(Activity activity, String message){
        dialog = new AlertDialog.Builder(activity);
        dialog.setTitle("Message");
        dialog.setMessage(message);
    }

    public void show(){
        dialog.show();
    }

    public AlertDialog.Builder getDialog(){
        return dialog;
    }

}
