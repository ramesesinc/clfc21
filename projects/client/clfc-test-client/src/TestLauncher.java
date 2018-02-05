
import com.rameses.osiris2.test.OsirisTestPlatform;
import java.util.HashMap;
import java.util.Map;
import javax.swing.UIManager;

/**
 *
 * @author wflores
 */
public class TestLauncher {

    public static void main(String[] args) throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch(Exception ex) {;} 
        
        Map env = new HashMap(); 
        env.put("app.host","localhost:8070"); 
        env.put("app.context","clfc"); 
        env.put("app.cluster","osiris3"); 
        env.put("app.debug",true); 
        
        env.put("app.custom", "clfc"); 
        //env.put("ws.host", "localhost:8060");         
        
        env.put("fileserver.host", "localhost:8070"); 
        env.put("fileserver.cluster", "osiris3"); 
        env.put("fileserver.context", "fileserver"); 
        OsirisTestPlatform.runTest(env, new HashMap(), new HashMap()); 
        
        
    }
}
