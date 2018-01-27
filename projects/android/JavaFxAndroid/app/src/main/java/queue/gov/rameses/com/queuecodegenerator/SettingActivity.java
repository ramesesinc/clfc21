package queue.gov.rameses.com.queuecodegenerator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import queue.gov.rameses.com.queuecodegenerator.util.Setting;

/**
 * Created by Dino Quimson on 3/6/15.
 */
public class SettingActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setting);

        final TextView ipTextView = (TextView) findViewById(R.id.ipAddress);
        ipTextView.setText(Setting.IP);

        final TextView portTextView = (TextView) findViewById(R.id.portNumber);
        portTextView.setText(Setting.PORT);

        Button applyBtn = (Button) findViewById(R.id.applySetting);
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Setting.IP = ipTextView.getText().toString();
                Setting.PORT = portTextView.getText().toString();
                finish();
            }
        });
    }

}
