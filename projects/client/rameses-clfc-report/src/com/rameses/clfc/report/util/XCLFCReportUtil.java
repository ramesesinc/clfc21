package com.rameses.clfc.report.util;

import com.rameses.osiris2.reports.ReportModel;
import com.rameses.osiris2.reports.SubReport;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;

public class XCLFCReportUtil {
    
    public static void generatePDFFile( String path, String filename, String rptname, 
            Object rptdata, Map params, SubReport[] subreports) throws Exception {
           
        CustomReportModel model = new CustomReportModel(rptname, rptdata, params, subreports);
        
        Properties props = new Properties();
        try {
             File f1 = new File("client.conf");
             props.load(new FileInputStream(f1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
//        println 'props ' + props;
        
        File f = new File(path, filename);
        model.exportToPDF(f);
    }
    
    public static class CustomReportModel extends ReportModel {
        
        private String rptname;
        private Object rptdata;
        private Map rptparams;
        private SubReport[] subreports;
        
        public CustomReportModel(String rptname, Object rptdata, Map rptparams, SubReport[] subreports) {
            this.rptname = rptname;
            this.rptdata = rptdata;
            this.rptparams = rptparams;
            this.subreports = subreports;
        }
        
        public String getReportName() {
            return rptname;
        }
        
        public Object getReportData() {
            return rptdata;
        }
        
        public Map getParameters() {
            return rptparams;
        }
        
        public SubReport[] getSubReports() {
            return subreports;
        }
    }
}
