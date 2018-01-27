package queue.gov.rameses.com.queuecodegenerator.util;

import java.util.ArrayList;
import queue.gov.rameses.com.queuecodegenerator.bean.Color;

/**
 * Created by Dino Quimson on 2/16/15.
 */
public class ColorList {

    public static ArrayList<Color> get(){
        ArrayList<Color> list = new ArrayList<Color>();
        list.add(new Color("#68ba4b","#78d656","#87d56c"));
        list.add(new Color("#f7a708","#febb37","#fbbe47"));
        list.add(new Color("#fe53af","#fe66b8","#fe78c0"));
        list.add(new Color("#53b3e5","#5ac3fa","#73ccfa"));
        list.add(new Color("#f52525","#fd4141","#ff6161"));
        list.add(new Color("#6ca602","#84ca03","#8eca20"));
        return list;
    }

}
