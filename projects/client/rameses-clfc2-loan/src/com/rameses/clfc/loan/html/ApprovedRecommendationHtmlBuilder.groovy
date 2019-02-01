package com.rameses.clfc.loan.html;

import com.rameses.clfc.util.HtmlBuilder;

class ApprovedRecommendationHtmlBuilder extends HtmlBuilder
{
    public def getApprovedRecommendation( recom ) {
        if (!recom) return '';
        StringBuffer sb = new StringBuffer();
        def path = 'com/rameses/clfc/loan/html/approved_recommendation.gtpl';
        sb.append(template.getResult(path, [recom: recom, ifNull: ifnull]));
        return getHtml(sb.toString());
    }
    /*
    private def template = TemplateProvider.instance;
    private StringBuffer sb;
    private String url = "com/rameses/clfc/loan/html/";
    protected def ifnull = {v, dv->
        ifNull(v, dv);
    }
    
    protected def ifNull( value, defaultvalue ) {
        if( value == null ) return defaultvalue;
        return value;
    }
    
    public def getHtml( recom ) {
        sb=new StringBuffer();
        def path = url + 'approved_recommendation.gtpl';
        println 'result-> ' + template.getResult(path, [ifNull: ifnull]);
        //println 'file-> ' + url + 'approved_recommendation.gtpl';
        //println 'result-> ' + template.getResult() 
        //sb.append(template.getResult(url+'approved_recommendation.gtpl', [recom: recom, ifNull: ifnull]));
        return sb.toString();
    }
    */
}
