package queue.gov.rameses.com.queuecodegenerator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import queue.gov.rameses.com.queuecodegenerator.R;
import queue.gov.rameses.com.queuecodegenerator.SectionActivity;

/**
 * Created by Dino Quimson on 2/16/15.
 */
public class GroupAdapter extends ArrayAdapter<String>{

    private Context context;
    private ArrayList<String> list;

    public GroupAdapter(Context ctx, ArrayList<String> list){
        super(ctx, R.layout.activity_list_item, list);
        this.context = ctx;
        this.list = list;
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.activity_list_item, parent, false);
        RelativeLayout layout = (RelativeLayout) root.findViewById(R.id.sectionContainer);
        TextView textView = (TextView) root.findViewById(R.id.sectionTextView);
        textView.setText(list.get(position));

        //setting the textsize
        try {
            int textSize = (int) (SectionActivity.HEIGHT * 0.08);
            textView.setTextSize(textSize);
        }catch(Exception e){
            e.printStackTrace();
        }

        return root;
    }

}
