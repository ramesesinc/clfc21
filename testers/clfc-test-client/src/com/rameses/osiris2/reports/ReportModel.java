package com.rameses.osiris2.reports;

import com.rameses.osiris2.client.InvokerFilter;
import com.rameses.osiris2.client.InvokerUtil;
import com.rameses.rcp.common.Action;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public abstract class ReportModel {
    
    @com.rameses.rcp.annotations.Invoker
    protected com.rameses.osiris2.Invoker invoker;

    private boolean allowSave;
    private boolean allowPrint;
    private JasperPrint reportOutput;
    private JasperReport mainReport;

    public ReportModel() { 
        this.allowSave = false;
        this.allowPrint = true; 
    }
  
    public abstract Object getReportData();

    public abstract String getReportName();

    public boolean isAllowSave() { return this.allowSave; } 
    public void setAllowSave(boolean allowSave) {
      this.allowSave = allowSave;
    }
  
    public boolean isAllowPrint() { return this.allowPrint; }
    public void setAllowPrint(boolean allowPrint) { 
        this.allowPrint = allowPrint; 
    }

    public SubReport[] getSubReports() { return null; }
  
    public Map getParameters() { return null; }

    private JasperPrint createReport() { 
        try {
            if (this.mainReport == null) {
                this.mainReport = ReportUtil.getJasperReport(getReportName());
            }

            Map conf = new HashMap();
            SubReport[] subReports = getSubReports();

            if (subReports != null) {
                for (SubReport sr : subReports) {
                    conf.put(sr.getName(), sr.getReport());
                }
            }
            Map params = getParameters();
            if (params != null) conf.putAll(params);

            JRDataSource ds = null;
            Object data = getReportData();
            if (data != null) { 
                ds = new ReportDataSource(data); 
            } else { 
                ds = new JREmptyDataSource();
            } 

            conf.put("REPORT_UTIL", new ReportDataUtil());
            return JasperFillManager.fillReport(this.mainReport, conf, ds); 

        } catch (RuntimeException re) {
            throw re;

        } catch (JRException ex) {
            ex.printStackTrace();
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }

    public String viewReport() {
        this.reportOutput = createReport();
        return "report";
    }

    public void exportToPDF() throws Exception { 
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode( JFileChooser.FILES_ONLY ); 
        jfc.setMultiSelectionEnabled( false ); 
            
        int opt = JFileChooser.CANCEL_OPTION;
        Window win = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow(); 
        if (win instanceof Frame) {
            opt = jfc.showSaveDialog((Frame)win); 
        } else if (win instanceof Dialog ) {
            opt = jfc.showSaveDialog((Dialog)win); 
        } else {
            opt = jfc.showSaveDialog((Frame) null); 
        }

        if ( opt == JFileChooser.APPROVE_OPTION ) {
            exportToPDF( jfc.getSelectedFile() ); 
        }
    }  
    
    public void exportToPDF( File file ) throws Exception {     
        FileOutputStream fos = null; 
        try { 
            JasperPrint jprint = createReport(); 
            fos = new FileOutputStream( file ); 
            JasperExportManager.exportReportToPdfStream(jprint, fos); 
        } finally { 
            try { fos.close(); }catch(Throwable t){;} 
        } 
    } 
    
    public JasperPrint getReport() {
        return this.reportOutput;
    }

    public List getReportActions() {
      List list = new ArrayList();
      list.add(new Action("_close", "Close", null));

      List xactions = lookupActions("reportActions");
      while (!xactions.isEmpty()) {
        Action a = (Action)xactions.remove(0);
        if (!containsAction(list, a)) {
          list.add(a);
        }
      }
      return list;
    }

    private boolean containsAction(List<Action> list, Action a) {
      for (Action aa : list) {
        if (aa.getName().equals(a.getName()))
          return true;
      }
      return false;
    }

    protected final List<Action> lookupActions(String type) {
      List actions = new ArrayList();
      try {
        actions = InvokerUtil.lookupActions(type, new InvokerFilter() {
          public boolean accept(com.rameses.osiris2.Invoker o) {
            return o.getWorkunitid().equals(ReportModel.this.invoker.getWorkunitid());
          } } );
      }
      catch (Throwable t) {
        System.out.println("[WARN] error lookup actions caused by " + t.getMessage());
      }

      for (int i = 0; i < actions.size(); ++i) {
        Action newAction = ((Action)actions.get(i)).clone();
        actions.set(i, newAction);
      }
      return actions;
    }

    public Object back() {
      return "_close";
    }
}
