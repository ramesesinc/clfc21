package com.rameses.clfc.android;


import java.util.ArrayList;

/**
 *
 * @author wflores 
 */
public class TextFormatter {

    public final static int ALIGN_LEFT = 1;
    public final static int ALIGN_CENTER = 2;
    public final static int ALIGN_RIGHT = 3;
    
    private ArrayList<Segment> segments = new ArrayList();

    private boolean overflow; 
    private int charlength; 
    
    public TextFormatter() {
        this( 32 ); 
    }
    
    public TextFormatter( int charlength ) {
        this.charlength = charlength; 
    }
    
    public void clear() {
        segments = new ArrayList();
    }
        
    public boolean isOverflow() { return overflow; } 
    public void setOverflow( boolean overflow ) {
        this.overflow = overflow; 
    }
      
    public void addText( String text ) {
        addText( text, ALIGN_LEFT ); 
    }
    public void addText( String text, int alignment ) { 
        addText( text, alignment, 0 ); 
    }
    public void addText( String text, int alignment, int fixedlength ) { 
        if (alignment >= 1 && alignment <= 3) {
            //alignment setting is correct 
        } else {
            alignment = ALIGN_LEFT; 
        }
        
        Segment seg = new Segment();
        seg.alignment = alignment;
        seg.fixedlength = fixedlength;
        seg.charlength = this.charlength;
        seg.text = text; 
        segments.add( seg );
    }
    
    public String buildLine() {
        return buildLine('-'); 
    }
    
    public String buildLine( char ch ) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<charlength; i++) {
            sb.append( ch );  
        }
        return sb.toString(); 
    }
    
    public String build() { 
        
        StringBuilder out = new StringBuilder();
        for ( Segment seg : segments ) {
            seg.build( out ); 
        }
        if ( out.length() > charlength ) {
            int count = out.length() / charlength; 
            for (int i=0; i<count-1; i++) {
                int pos = (i+1)*charlength; 
                out.insert(pos, '\n');
            }
        }
        return out.toString(); 
    }
    
    
    private class Segment {
        
        int charlength;
        int alignment;
        int fixedlength; 
        String text;
                
        void build( StringBuilder out ) { 
            if (text == null || text.length()==0 ) {
                return; 
            }
            
            if ( alignment == ALIGN_CENTER ) {
                buildCenter( out ); 
            } else if ( alignment == ALIGN_RIGHT ) {
                buildRight( out ); 
            } else {
                buildLeft( out ); 
            }
        }
        
        void tidyUp( StringBuilder target, int sourcelen  ) { 
            int maxlen = Math.max(target.length(), sourcelen); 
            int count = maxlen / charlength; 
            int mod = (maxlen % charlength); 
            if ( mod > 0 ) count += 1; 

            int preferredlength = count * charlength; 
            int padlength = preferredlength - target.length(); 
            if ( padlength < 0 ) padlength=0; 

            padRight( target, padlength ); 
        }
        
        void buildLeft( StringBuilder out ) {
            tidyUp(out, text.length()); 
            out.replace(0, text.length(), text); 
        }
        void buildRight( StringBuilder out ) { 
            int textlen = text.length();
            tidyUp(out, textlen); 
            
            int count = textlen / charlength; 
            int mod = (textlen % charlength); 
            for (int i=0; i<count; i++) { 
                int startidx = i * charlength; 
                int endidx = (i+1) * charlength; 
                out.replace(startidx, endidx, text.substring( startidx, endidx ));  
            }
            if ( mod > 0 ) {
                int startidx = count * charlength; 
                int endidx = startidx + mod; 
                String str = text.substring( startidx, endidx );
                int padlength = (charlength - str.length()); 
                if ( padlength < 0 ) padlength = 0;
                
                startidx += padlength;
                out.replace(startidx, startidx+str.length(), str); 
            } 
        }         
        void buildCenter( StringBuilder out ) {
            int textlen = text.length();
            tidyUp(out, textlen); 
            
            int count = textlen / charlength; 
            int mod = (textlen % charlength); 
            for (int i=0; i<count; i++) { 
                int startidx = i * charlength; 
                int endidx = (i+1) * charlength; 
                out.replace(startidx, endidx, text.substring( startidx, endidx ));  
            }
            if ( mod > 0 ) {
                int startidx = count * charlength; 
                int endidx = startidx + mod; 
                String str = text.substring( startidx, endidx );
                int padlength = (charlength - str.length())/2; 
                if ( padlength < 0 ) padlength = 0;
                
                startidx += padlength;
                out.replace(startidx, startidx+str.length(), str); 
            } 
        }
                
        void padRight( StringBuilder sb, int padlength ) {
            if ( padlength < 0 ) padlength = 0;
            for (int i=0; i<padlength; i++) {
                sb.append(" "); 
            }
        }
        void padLeft( StringBuilder sb, int padlength ) {
            if ( padlength < 0 ) padlength = 0;
            for (int i=0; i<padlength; i++) {
                if (sb.length() > 0) {
                    sb.insert(0, " "); 
                } else {
                    sb.append(" "); 
                }
            }
        }        
    }
}

