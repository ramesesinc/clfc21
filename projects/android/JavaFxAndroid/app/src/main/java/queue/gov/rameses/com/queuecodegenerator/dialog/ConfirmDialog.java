package queue.gov.rameses.com.queuecodegenerator.dialog;

import android.app.Activity;
import android.app.AlertDialog;

/**
 * Created by Dino Quimson on 2/27/15.
 */
public class ConfirmDialog {

    private AlertDialog.Builder dialog;

    public ConfirmDialog(Activity activity,String message){
        dialog = new AlertDialog.Builder(activity);
        dialog.setTitle("Confirm");
        dialog.setMessage(message);
    }

    public void show(){
        dialog.show();
    }

    public AlertDialog.Builder getDialog(){
        return dialog;
    }


}
