package queue.gov.rameses.com.queuecodegenerator;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import queue.gov.rameses.com.queuecodegenerator.dialog.ConfirmDialog;
import queue.gov.rameses.com.queuecodegenerator.dialog.ErrorDialog;
import queue.gov.rameses.com.queuecodegenerator.dialog.InfoDialog;
import queue.gov.rameses.com.queuecodegenerator.service.CodeGeneratorService;

/**
 * Created by Dino Quimson on 2/27/15.
 */
public class CodeActivity extends Activity {

    private Button button;
    private Activity self;
    private Map group;
    private Map section;
    private Map category;
    private boolean generated = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_code);
        self = this;
        TextView textView = (TextView) findViewById(R.id.codeTextView);
        String code = this.getIntent().getExtras().getString("code");
        textView.setText(code);

        //Get the intent's data
        Intent intent = getIntent();
        group = (Map) intent.getSerializableExtra("group");
        section = (Map) intent.getSerializableExtra("section");
        category = (Map) intent.getSerializableExtra("category");

        button = (Button) findViewById(R.id.codeButton);
        button.setTextSize((int)(SectionActivity.HEIGHT * 0.08));
        button.getLayoutParams().width = SectionActivity.WIDTH;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(generated){
                    return;
                }
                ConfirmDialog confirmDialog = new ConfirmDialog(self,"You are about to generate the code. Continue?");
                confirmDialog.getDialog().setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean error = false;
                        Map params = new HashMap();
                        params.put("state", "PENDING");
                        params.put("sectioncode", section.get("code").toString() + category.get("code").toString());
                        params.put("prefix", group.get("name").toString() + section.get("name").toString() + category.get("name").toString());
                        params.put("group", group);
                        params.put("section", section);
                        params.put("category", category);
                        try {
                            CodeGeneratorService svc = new CodeGeneratorService();
                            LinkedHashMap result = svc.create(params);
                            generated = true;
                        } catch (Exception e) {
                            error = true;
                            generated = false;
                            ErrorDialog confirmDialog = new ErrorDialog(self,e.toString());
                            confirmDialog.getDialog().setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                            confirmDialog.show();
                        }
                        if(!error){
                            InfoDialog infoDialog = new InfoDialog(self,"The code was successfully generated.");
                            infoDialog.getDialog().setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    button.setText("Print Again");
                                }
                            });
                            infoDialog.show();
                        }
                    }
                });
                confirmDialog.getDialog().setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                confirmDialog.show();
            }
        });
        //setting the textsize
        textView.setTextSize((int) (SectionActivity.HEIGHT * 0.20));
        TextView title = (TextView) findViewById(R.id.title);
        TextView footer = (TextView) findViewById(R.id.footer);
        title.setText("Number");
        title.setTextSize((int) (SectionActivity.HEIGHT * 0.06));
        footer.setTextSize((int) (SectionActivity.HEIGHT * 0.035));
    }

}
