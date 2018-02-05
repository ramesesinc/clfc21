/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.clfc.functions;

import com.rameses.util.DateUtil;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author louie
 */
public class ModifiedDateFunc {
    
    public static Date dayAdd(Date date, int num) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, num);
        String dt = DateUtil.formatDate(null, cal.getTime());
        return java.sql.Date.valueOf(dt);
    }
    
    public static Date monthAdd(Date date, int num) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, num);
        String dt = DateUtil.formatDate(null, cal.getTime());
        return java.sql.Date.valueOf(dt);
    }
    
    public static Date yearAdd(Date date, int num) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, num);
        String dt = DateUtil.formatDate(null, cal.getTime());
        return java.sql.Date.valueOf(dt);
    }
}
