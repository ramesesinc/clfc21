package queue.gov.rameses.com.queuecodegenerator;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import queue.gov.rameses.com.queuecodegenerator.dialog.ErrorDialog;
import queue.gov.rameses.com.queuecodegenerator.service.GroupService;
import queue.gov.rameses.com.queuecodegenerator.util.Screen;
import queue.gov.rameses.com.queuecodegenerator.util.Setting;

/**
 * Created by Dino Quimson on 2/20/15.
 */
public class LoadActivity extends Activity{

    public static List<Map> groupList;
    private Activity self;
    private TextView error;
    private TextView text;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_load);
        self = this;
        //get the device width and height
        int scrwidth = new Screen(this).getWidth();
        int scrheight = new Screen(this).getHeight();

        //referencing the ui from xml layout
        ImageView image = (ImageView) findViewById(R.id.loadImageView);
        text = (TextView) findViewById(R.id.loadTextView);
        error = (TextView) findViewById(R.id.loadErrorMessage);

        //adjusting the image's width and height for consistency in different devices
        image.getLayoutParams().width = scrwidth;
        image.getLayoutParams().height = scrwidth;

        //adjusting the textsize for consistency in different devices
        text.setTextSize((float) (scrwidth*0.08));

        //get data from server
        loadData();
    }

    public void loadData(){
        Thread thread = new Thread(new Runnable(){
            public void run(){
                boolean success = true;
                Map params = new HashMap();
                params.put("objid","%");
                try {
                    final GroupService service = new GroupService();
                    if (service != null) groupList = service.getGroups(params);
                }catch(final Exception e){
                    success = false;
                    self.runOnUiThread(new Runnable(){
                        public void run(){
                            String err = e.toString();
                            text.setText("ERROR");
                            ErrorDialog confirmDialog = new ErrorDialog(self,err);
                            confirmDialog.getDialog().setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            confirmDialog.show();
                            self.openOptionsMenu();
                        }
                    });
                }
                if(success) showMainActivity();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void showMainActivity(){
        Intent intent = new Intent(this, SectionActivity.class);
        self.startActivity(intent);
        self.finish();
    }

    @Override
    public void onBackPressed(){
        //disable the back button
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(0,0,0,"Settings");
        menu.add(1,1,1,"Reload");
        menu.add(2,2,2,"Exit");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        //setting
        if(id == 0){
            final Dialog dialog = new Dialog(this);
            dialog.setTitle("Connection Setting");
            dialog.setContentView(R.layout.activity_setting);
            dialog.show();

            final TextView ipTextView = (TextView) dialog.findViewById(R.id.ipAddress);
            ipTextView.setText(Setting.IP);

            final TextView portTextView = (TextView) dialog.findViewById(R.id.portNumber);
            portTextView.setText(Setting.PORT);

            Button button = (Button) dialog.findViewById(R.id.applySetting);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Setting.IP = ipTextView.getText().toString();
                    Setting.PORT = portTextView.getText().toString();
                    dialog.dismiss();
                    self.openOptionsMenu();
                }
            });
        }
        //reload
        if(id == 1){
            text.setTextColor(Color.parseColor("#000000"));
            text.setText("Please Wait");
            error.setText("");
            loadData();
        }
        //exit
        if(id == 2){
            System.exit(0);
        }
        return true;
    }

}
