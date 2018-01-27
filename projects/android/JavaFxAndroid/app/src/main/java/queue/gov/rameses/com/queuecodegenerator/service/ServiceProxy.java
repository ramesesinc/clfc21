package queue.gov.rameses.com.queuecodegenerator.service;

import com.rameses.service.ScriptServiceContext;

import java.util.HashMap;
import java.util.Map;

import queue.gov.rameses.com.queuecodegenerator.util.Setting;

public class ServiceProxy
{
    static ScriptServiceContext ctx;
    public static String errorString;
  
    public static ScriptServiceContext getContext(){
        errorString = null;
        ctx = null;
        try {
            Map env = new HashMap();
            env.put("app.context", "etracs25");
            env.put("app.cluster", "osiris3");
            env.put("app.host", Setting.IP+":"+Setting.PORT);
            if (ctx == null) ctx = new ScriptServiceContext(env);
        }catch(Exception e){
            errorString = e.toString();
        }
        return ctx;
    }

}