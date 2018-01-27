package com.rameses.clfc.android;

import java.io.IOException;
import java.io.OutputStream;

public class ReceiptWriter {

	public final static int ALIGN_LEFT   = 0;
	public final static int ALIGN_CENTER = 1;
	public final static int ALIGN_RIGHT  = 2;
	
	public final static int UNDERLINE_MODE_OFF = 0;
	public final static int UNDERLINE_MODE_1   = 1;
	public final static int UNDERLINE_MODE_2   = 2;
	
	public final static int MODE_OFF = 0;
	public final static int MODE_ON  = 1;
	
	public final static int DIRECTION_LEFT_TO_RIGHT = 0;
	public final static int DIRECTION_BOTTOM_TO_TOP = 1;
	public final static int DIRECTION_RIGHT_TO_LEFT = 2;
	public final static int DIRECTION_TOP_TO_BOTTOM = 3;
	
	public final static int BARCODE_TYPE_UPC_A   = 65;
	public final static int BARCODE_TYPE_UPC_E   = 66;
	public final static int BARCODE_TYPE_EAN_13  = 67;
	public final static int BARCODE_TYPE_EAN_8   = 68;
	public final static int BARCODE_TYPE_CODE_39 = 69;
	public final static int BARCODE_TYPE_ITF     = 70;
	public final static int BARCODE_TYPE_CODABAR = 71;
	public final static int BARCODE_TYPE_QRCODE  = 76; 
	
	public final static int FONT_A = 0;
	public final static int FONT_B = 1; 	
	
	public final static byte[] LF = new byte[]{ 0x0a };
	
	private OutputStream out;
	
	public ReceiptWriter( OutputStream out ) {
		this.out = out; 
	}
	
	public void init() {
		write(new byte[]{27, 64});
	}
	
    public void formFeed( int size ) {
        write(0x1b);
        write(0x4a);
        write(size);
    } 	
    
    public void lineFeed() {
    	write( LF ); 
    }
    public void lineFeed( int count ) {
    	write(0x1b);
        write((int)'d');
        write( count );
    } 
    
        
    public void setAlignment( int alignment ) {
        write(0x1b); 
        write(0x61); 
        write( alignment ); 
    } 
    
    public void setLineSpacing( int size ) {
    	write(0x1b);
    	
    	if ( size <= 0 ) {
    		write(0x32); 
    	} else {
    		write(0x33);
    		write(size);
    	}
    }
    
    public void setFont( int font ) {
    	write(0x1b);
    	write(0x4d);
    	if ( font <= 0 ) {
    		font = 0; 
    	}
    	write( font ); 
    }
    
    public void setCharacterSpacing( int size ) {
    	write(0x1b);
    	write(0x20);
    	write(size); 
    }
    
    public void setUnderlineMode( int mode ) {
    	write(0x1b);
    	write(0x2d);
    	write(mode);
    }

    public void setEmphasized( boolean emphasized ) {
    	write(0x1b);
    	write(0x45);
    	write(emphasized? 1 : 0);
    }    

    public void setUpsideDownPrintingMode( int mode ) {
    	write(0x1b);
    	write(0x7b);
    	write(mode);
    }  
    
    public void setCharacterHeight( int mode ) {
    	if ( mode >= 0 && mode <= 7 ) {
    		write(0x1d);
    		write(0x21);
    		write(mode); 
    	}
    }

    public void setCharacterWidth( int mode ) {
    	if ( mode==0 || mode==8 || mode==16 || mode==32 || mode==48 || mode==64 || mode==80 || mode==96 || mode==112 ) {
    		write(0x1d);
    		write(0x21);
    		write(mode); 
    	}
    }
    
    public void setInversed( boolean inversed ) {
    	write(0x1d); 
    	write(0x42);
    	write(inversed? 1 : 0);
    }
    
    public void setAbsolutePosition( int nL, int nH ) {
    	write(0x1b);
    	write(0x24);
    	write(nL);
    	write(nH);
    }
    
    public void setRelativePosition( int nL, int nH ) {
    	write(0x1b);
    	write(0x5c);
    	write(nL);
    	write(nH);
    }
    
    public void setPrintDirection( int mode ) {
    	write(0x1b);
    	write(0x54);
    	write(mode);
    }
    
    public void transmitPrinterStatus() {
    	write(0x1b);
    	write(0x76); //paper-in
    }

    public void setQRCode( int width ) {
    	write(0x1d);
    	write(0x6f);
    	if (width < 1) {
    		width = 1;
    	} else if (width > 255) {
    		width = 255;
    	}
    	write("0," + width + ",0,1");
    	
    }
    
    public void setQRModel() {
    	write(0x1d);
    	write(0x28);
    	write(0x6b);
    	write(4);
    	write(0);
    	write(49);
    	write(65);
    	write(49);
    	write(0);
    	/*
    	if (model==0 || model==1) {
        	write(0x1d);
        	write(0x29);
        	write(model);
    	}
    	*/
    }
    
    public void setBarcodeHeight( int size ) {
		//GS h = sets barcode height    	
    	write(0x1d);
    	write(0x68);
    	if ( size <= 0 ) {
    		//set to default value 
    		size = 60; 
    	}
    	write( size ); 
    }

    public void setBarcodeWidth( int size ) {
		//GS w = sets barcode width    	
    	write(0x1d);
    	write(0x77);
    	if ( size <= 0 ) {
    		//set to default value 
    		size = 2; 
    	}
    	write( size ); 
    }
    public void setBarcodeFont( int font ) {
		//GS f = set barcode characters
		write(0x1d);
		write("f");
		write(font);
    }
    public void setBarcodePosition( int pos ) {
    	//GS H = HRI position
		write(0x1d);
		write("H");
		write(pos); //0=no print, 1=above, 2=below, 3=above & below    	
    }
    
    public void writeBarcode( String value ) {
    	writeBarcode( value, 69 ); 
    }
    public void writeBarcode( String value, int codetype ) {
    	write(0x1d);
    	write(0x6b);
    	write(codetype);
    	
    	char[] chars = value.toCharArray();
    	write(chars.length);
    	
    	for (int i=0; i<chars.length; i++) {
    		write((int) chars[i]); 
    	}
    	write(0); 
    }
        
    public void write( String value ) {
    	write( value, -1 ); 
    }
    public void write( String value, int alignment ) {
    	if ( alignment >= 0 ) { 
    		setAlignment( alignment ); 
    	} 
    	write( value.getBytes() ); 
    }
    
    public void writeln( String value ) {
    	writeln( value, -1 ); 
    }    
    public void writeln( String value, int alignment ) { 
    	if ( alignment >= 0 ) { 
    		setAlignment( alignment ); 
    	} 
    	write( value.getBytes() ); 
    	lineFeed(); 
    }      
	
	public void write( byte[] values ) {
		try {
			out.write( values );
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e); 
		}
	}
	public void write( int value ) {
		try {
			out.write( value );
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e); 
		}
	}
	
	
	public String formatText( String value, int length ) { 
		return formatText( value, length, ALIGN_LEFT ); 
	} 
	public String formatText( String value, int length, int alignment ) { 
		return formatText( value, length, alignment, ' ' );
	}
	public String formatText( String value, int length, int alignment, char padchar ) {
		StringBuilder sb = new StringBuilder();
		if (alignment == ALIGN_RIGHT) { 
			int diff = length - value.length(); 
			for (int i=0; i<diff; i++) sb.append( padchar ); 
			
			sb.append( value );			
		} else {
			sb.append( value ); 
			int diff = length - value.length(); 
			for (int i=0; i<diff; i++) sb.append( padchar ); 
		} 
		
		try { 
			String str = sb.substring(0, length);
			sb = new StringBuilder( str ); 
		} catch(Throwable t) {;} 
		
		return sb.toString(); 
	}
	
	public String updateText( String target, String value ) {
		char[] mchars = target.toCharArray();
		char[] nchars = value.toCharArray(); 
		for (int i=0; i<nchars.length; i++) {
			if ( i >= mchars.length ) break; 
			
			mchars[i] = nchars[i]; 
		}
		return new String(mchars); 
	}
	
}
