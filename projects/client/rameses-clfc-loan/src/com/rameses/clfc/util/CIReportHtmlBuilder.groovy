package com.rameses.clfc.util;

class CIReportHtmlBuilder extends HtmlBuilder {
    
    public def build( def data ) {
        if ( !data?.items ) return '';
        
        StringBuilder sb = new StringBuilder();
        sb.append(template.getResult(url+'cireport.gtpl', [data: data, ifNull: ifnull]));
        return getHtml(sb.toString());
    }
}
