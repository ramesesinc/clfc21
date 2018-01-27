package queue.gov.rameses.com.queuecodegenerator;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import queue.gov.rameses.com.queuecodegenerator.adapter.GroupAdapter;
import queue.gov.rameses.com.queuecodegenerator.bean.Color;
import queue.gov.rameses.com.queuecodegenerator.util.ColorList;
import queue.gov.rameses.com.queuecodegenerator.util.Screen;

/**
 * Created by Dino Quimson on 2/13/15.
 */
public class SectionActivity extends Activity {

    public static ArrayList<Color> colorList;
    public static int WIDTH,HEIGHT;
    public static int currentIndex = 0;
    private ArrayList<String> data;
    private Map currentGroup;
    private List<Map> sections;
    private Activity self;
    private ListView listView;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_list);
        init();
        GroupAdapter groupAdapter = new GroupAdapter(this,data);
        listView.setAdapter(groupAdapter);
        listView.setBackgroundColor(android.graphics.Color.WHITE);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map selectedSection = sections.get(position);
                Intent intent = new Intent(self, CategoryActivity.class);
                intent.putExtra("section",(Serializable) selectedSection);
                self.startActivity(intent);
            }
        });
        TextView title = (TextView) findViewById(R.id.title);
        TextView footer = (TextView) findViewById(R.id.footer);
        title.setText(currentGroup.get("name").toString());
        title.setTextSize((int) (SectionActivity.HEIGHT * 0.06));
        footer.setTextSize((int) (SectionActivity.HEIGHT * 0.035));
    }

    void init(){
        self = this;
        listView = (ListView) findViewById(R.id.mainList);
        colorList = ColorList.get();
        this.WIDTH = new Screen(this).getWidth();
        this.HEIGHT = new Screen(this).getHeight();

        data = new ArrayList<String>();
        if(LoadActivity.groupList != null && LoadActivity.groupList.size() > 0){
            currentGroup = LoadActivity.groupList.get(currentIndex);
            try {
                sections = (List<Map>) currentGroup.get("sections");
                for (Map m : sections) {
                    data.add(m.get("name").toString());
                }
            }catch(Exception e){
                System.out.println("Principle: "+e.toString());
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(0,0,0,"Change Group");
        menu.add(1,1,1,"Exit");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        //setting
        if(id == 0){
            final Dialog dialog = new Dialog(this);
            dialog.setTitle("Group");
            dialog.setContentView(R.layout.activity_changegroup);

            final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner);

            //get grouplist
            ArrayList<String> data = new ArrayList<String>();
            Iterator i = LoadActivity.groupList.iterator();
            while(i.hasNext()){
                Map m = (Map) i.next();
                data.add(m.get("name").toString());
            }

            //setup spinner adapter
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setSelection(currentIndex);

            Button okBtn = (Button) dialog.findViewById(R.id.groupbutton);
            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    currentIndex = spinner.getSelectedItemPosition();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });

            dialog.show();
        }

        //exit
        if(id == 1){
            System.exit(0);
        }
        return true;
    }

}
