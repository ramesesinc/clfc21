package com.rameses.clfc.treasury.ledger.amnesty

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import java.text.*;

class AvailedAmnestyController 
{
    @Binding
    def binding;
    
    def entity, availed = [:];
    def daysList, maxDay = 31;
    def mode = 'read';
    
    private def decFormat = new DecimalFormat('#,##0.00');
    private def dateFormat = new SimpleDateFormat('MMM dd, yyyy');
    
    @PropertyChangeListener
    def listener = [
        'availed.withmd': { o->
            switch (o) {
                case 0: availed.usedate = 0;
                        availed.date = null;
                        availed.day = 0;
                        availed.month = 0;
                        break;
            }
            availed.description = buildDescription(availed);
            availed.dtended = buildMaturityDate(availed);
            binding?.refresh();
        },
        'availed.usedate': { o->
            switch (o) {
                case 1: availed.day = 0;
                        availed.month = 0;
                        break;
                case 0: availed.date = null;
                        break;
            }
            availed.description = buildDescription(availed);
            availed.dtended = buildMaturityDate(availed);
            binding?.refresh();
        },
        'availed.(date|amount|day|month|dtstarted)': { o->
            availed.description = buildDescription(availed);
            availed.dtended = buildMaturityDate(availed);
            binding?.refresh();
        }
    ];
    
    /*
    @PropertyChangeListener
    def listener = [
        'availed.amount': { o->
            availed.description = buildDescription(availed);
            binding?.refresh();
        },
        "availed.usedate": { o->
            switch (o) {
                case 1: availed.day = 0;
                        availed.month = 0;
                        break;
                case 0: availed.date = null;
                        break;
            }
            availed.description = buildDescription(availed);
            availed.dtended = buildMaturityDate(availed);
            binding?.refresh();
        },
        'availed.day': { o->
            availed.description = buildDescription(availed);
            availed.dtended = buildMaturityDate(availed);
            binding?.refresh();
        },
        'availed.month': { o->
            availed.description = buildDescription(availed);
            availed.dtended = buildMaturityDate(availed);
            binding?.refresh();
        },
        'availed.date': { o->
            availed.description = buildDescription(availed);
            availed.dtended = buildMaturityDate(availed);
            binding?.refresh();
        },
        'availed.dtstarted': { o->
            availed.description = buildDescription(availed);
            availed.dtended = buildMaturityDate(availed);
            binding?.refresh();
        }
    ]
    */
    
    void init() {
        if (entity) {
            if (!entity.availed) entity.availed = [month: 0, day: 0, usedate: 0];
            availed = entity.availed;
        }
        resetDaysList();
    }    
    void resetDaysList() {
        daysList = [];
        for (int i=0; i <= maxDay; i++) { daysList << i; }
        binding?.refresh();
    }

    private def buildDescription( data ) {
        if (!data.amount) return null;
        def str = decFormat.format(data.amount) + ' ';
        
        if (!data.withmd || data.withmd == 0) {
            str += 'No Maturity Date ';
        } else {
            if (data.usedate == 0 || !data.usedate) {
                if (data.month > 0) str += data.month + ' Month(s) ';
                if (data.day > 0) str += data.day + ' Day(s) ';
            } else if (data.usedate == 1 && data.date) {
                str += 'until ' + dateFormat.format(parseDate(data.date)) + ' ';
            }
        }
        
        return str;
    }
    
    private def buildMaturityDate( data ) {
        if (!data.dtstarted || (!data.withmd || data.withmd == 0)) return null;
        
        def cal = Calendar.getInstance();
        cal.setTime(parseDate(data.dtstarted));
        
        if (data.usedate == 0 || !data.usedate) {
            cal.add(Calendar.MONTH, data.month);
            cal.add(Calendar.DATE, data.day);
        } else if (data.usedate == 1 && data.date) {
            cal.setTime(parseDate(data.date));
        }
        
        def df = new SimpleDateFormat('yyyy-MM-dd');
        return parseDate(df.format(cal.getTime()));
    }
    
    private def parseDate( date ) {
        if (!date) return null;
        
        if (date instanceof Date) {
            return date;
        } else {
            return java.sql.Date.valueOf(date);
        }
    }
    
}

