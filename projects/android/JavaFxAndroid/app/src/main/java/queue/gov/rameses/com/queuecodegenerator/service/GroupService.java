package queue.gov.rameses.com.queuecodegenerator.service;

import com.rameses.service.ScriptServiceContext;

import java.util.List;
import java.util.Map;

/**
 * Created by Dino Quimson on 2/19/15.
 */
public class GroupService {

    private ScriptServiceContext ctx;
    private QueueGroupService service;
    public String errorString;

    public GroupService(){
        errorString = null;
        try {
            ctx = ServiceProxy.getContext();
            if(ctx != null) service = (QueueGroupService) ctx.create("QueueGroupService", QueueGroupService.class);
            if(ServiceProxy.errorString != null) errorString = ServiceProxy.errorString;
        }catch(Exception e){
            errorString=e.toString();
        }
    }

    public List<Map> getGroups(Map params){
        return service.getGroups(params);
    }

    static interface QueueGroupService{
        public List getGroups(Object params);
    }

}
