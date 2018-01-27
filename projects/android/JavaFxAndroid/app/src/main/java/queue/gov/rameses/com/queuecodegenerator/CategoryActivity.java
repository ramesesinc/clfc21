package queue.gov.rameses.com.queuecodegenerator;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import queue.gov.rameses.com.queuecodegenerator.adapter.GroupAdapter;
import queue.gov.rameses.com.queuecodegenerator.dialog.ErrorDialog;
import queue.gov.rameses.com.queuecodegenerator.service.CodeGeneratorService;

/**
 * Created by Dino Quimson on 2/20/15.
 */
public class CategoryActivity extends Activity{

    private ArrayList<String> data;
    private Map section;
    private Map group;
    private List<Map> category;
    private Activity self;
    private ListView listView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        self = this;
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
                Map selectedCategory = category.get(position);
                String prefix = group.get("name").toString() + section.get("name").toString() + selectedCategory.get("name").toString();
                String sectionCode = section.get("code").toString();
                String categoryCode = selectedCategory.get("code").toString();

                Map params = new HashMap();
                params.put("prefix", prefix);
                params.put("sectioncode", sectionCode + categoryCode);

                String code = "";
                boolean error = false;
                try {
                    CodeGeneratorService service = new CodeGeneratorService();
                    code = service.getCurrentSeries(params);
                } catch (Exception e) {
                    ErrorDialog confirmDialog = new ErrorDialog(self, e.toString());
                    confirmDialog.getDialog().setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    confirmDialog.show();
                    error = true;
                }

                //dismiss progress

                Intent intent = new Intent(self, CodeActivity.class);
                intent.putExtra("code", code);
                intent.putExtra("group", (Serializable) group);
                intent.putExtra("section", (Serializable) section);
                intent.putExtra("category", (Serializable) selectedCategory);
                if (!error) {
                    self.startActivity(intent);
                    self.finish();
                }
            }
        });
        TextView title = (TextView) findViewById(R.id.title);
        TextView footer = (TextView) findViewById(R.id.footer);
        title.setText("Category");
        title.setTextSize((int) (SectionActivity.HEIGHT * 0.06));
        footer.setTextSize((int) (SectionActivity.HEIGHT * 0.035));
    }

    void init(){
        listView = (ListView) findViewById(R.id.mainList);
        data = new ArrayList<String>();
        group = (Map) LoadActivity.groupList.get(SectionActivity.currentIndex);
        section = (Map) this.getIntent().getSerializableExtra("section");
        category = (List<Map>) group.get("categories");
        for(Map m : category){
            data.add(m.get("name").toString());
        }
    }

}
