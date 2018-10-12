package com.rameses.clfc.reporttemplate.qrcode

import com.rameses.rcp.common.*;
import com.rameses.rcp.annotations.*;
import com.rameses.osiris2.client.*;
import com.rameses.osiris2.common.*;
import com.rameses.osiris2.reports.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

class GenerateQRController extends ReportModel {

    def mode = "init";
    def rptdata, rptname;
    def is;
    
    def close() {
        return "_close";
    }
    
    void generateQRandSave() {
        println 'generate qr and save';
        def text = "helloworld";
        String filePath = "JD.png";
        int size = 125;
        String fileType = "png";
        File qrFile = new File(filePath);
        println 'absolute path->' + qrFile.getAbsolutePath();
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix byteMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size);        
        
        int matrixWidth = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matrixWidth, matrixWidth);
        // Paint and save the image using the ByteMatrix
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        ImageIO.write(image, fileType, qrFile);
        
    }
    
    void generateQR() {        
        println 'generate qr';
        def text = "helloworld";
        String filePath = "JD.png";
        int size = 125;
        String fileType = "png";
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix byteMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size);        
        
        int matrixWidth = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matrixWidth, matrixWidth);
        // Paint and save the image using the ByteMatrix
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, fileType, os);
        is = new ByteArrayInputStream(os.toByteArray());
    }
    
    def previewQRSaved() {
        File file = new File("JD.png");
        rptdata = [imagepath: file.getAbsolutePath()];
        rptname = "com/rameses/clfc/reporttemplate/qrcode/qrreportUsingSavedQR.jasper";
        viewReport();
        mode = "preview";
        return "preview";
    }
    
    def previewQRGenerated() {
        if (!is) return;
        rptdata = [imagestream: is];
        rptname = "com/rameses/clfc/reporttemplate/qrcode/qrreport.jasper";
        viewReport();
        mode = "preview";
        return "preview";
    }
    
    def back() {
        mode = 'init';
        return "default";
    }
    
    public Map getParameters() {
//        return [:];
        //def data = [:];
        //data.putAll(rptdata);
        //if (data.items) data.remove("items");
        //data.criteria = criteria;
        return [:];

    }

    public Object getReportData() {
        return rptdata;
    }
    
    public String getReportName() {
        if (!rptname) rptname = "com/rameses/clfc/reporttemplate/qrcode/qrreportUsingSavedQR.jasper";
        return rptname;
        //return 
    }
}

