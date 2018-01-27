package queue.gov.rameses.com.queuecodegenerator.service;

import com.rameses.service.ScriptServiceContext;

import java.util.LinkedHashMap;

/**
 * Created by Dino Quimson on 2/27/15.
 */
public class CodeGeneratorService {

    private ScriptServiceContext ctx;
    private QueueCodeGeneratorService service;
    public String errorString;

    public CodeGeneratorService(){
        errorString = null;
        try {
            ctx = ServiceProxy.getContext();
            if(ctx != null) service = (QueueCodeGeneratorService) ctx.create("QueueCodeGeneratorService", QueueCodeGeneratorService.class);
            if(ServiceProxy.errorString != null) errorString = ServiceProxy.errorString;
        }catch(Exception e){
            errorString=e.toString();
        }
    }

    public LinkedHashMap create(Object params){
        return service.create(params);
    }

    public String getCurrentSeries(Object params){
        return service.getCurrentSeries(params);
    }

    static interface QueueCodeGeneratorService{
        public String getCurrentSeries(Object params);
        public LinkedHashMap create(Object params);
    }

}
