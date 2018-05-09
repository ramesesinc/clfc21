/*
 * SubmitForApprovalPage.java
 *
 * Created on September 2, 2013, 2:24 PM
 */

package com.rameses.clfc.loan;

import com.rameses.osiris2.themes.OKCancelPage;
import com.rameses.rcp.ui.annotations.Template;
import java.math.BigDecimal;

/**
 *
 * @author  Rameses
 */
@Template(OKCancelPage.class)
public class SubmitForApprovalPage extends javax.swing.JPanel {
    
    /** Creates new form SubmitForApprovalPage */
    public SubmitForApprovalPage() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        xFormPanel1 = new com.rameses.rcp.control.XFormPanel();
        xLabel1 = new com.rameses.rcp.control.XLabel();
        xSeparator1 = new com.rameses.rcp.control.XSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        xTextArea1 = new com.rameses.rcp.control.XTextArea();
        jPanel3 = new javax.swing.JPanel();
        xFormPanel4 = new com.rameses.rcp.control.XFormPanel();
        xDecimalField1 = new com.rameses.rcp.control.XDecimalField();
        xDecimalField2 = new com.rameses.rcp.control.XDecimalField();
        xDecimalField3 = new com.rameses.rcp.control.XDecimalField();
        xFormPanel2 = new com.rameses.rcp.control.XFormPanel();
        xDecimalField4 = new com.rameses.rcp.control.XDecimalField();
        xDecimalField5 = new com.rameses.rcp.control.XDecimalField();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(new java.awt.Dimension(554, 415));
        setLayout(new java.awt.BorderLayout());

        xFormPanel1.setOrientation(com.rameses.rcp.constant.UIConstants.HORIZONTAL);
        xFormPanel1.setPadding(new java.awt.Insets(0, 0, 5, 0));

        xLabel1.setPadding(new java.awt.Insets(1, 0, 1, 0));
        xLabel1.setPreferredSize(new java.awt.Dimension(95, 20));
        xLabel1.setShowCaption(false);
        xLabel1.setText("Recommendations");
        xFormPanel1.add(xLabel1);

        xSeparator1.setPreferredSize(new java.awt.Dimension(0, 19));

        javax.swing.GroupLayout xSeparator1Layout = new javax.swing.GroupLayout(xSeparator1);
        xSeparator1.setLayout(xSeparator1Layout);
        xSeparator1Layout.setHorizontalGroup(
            xSeparator1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 437, Short.MAX_VALUE)
        );
        xSeparator1Layout.setVerticalGroup(
            xSeparator1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 19, Short.MAX_VALUE)
        );

        xFormPanel1.add(xSeparator1);

        add(xFormPanel1, java.awt.BorderLayout.NORTH);

        xTextArea1.setCaption("Recommendations");
        xTextArea1.setName("data.crecomremarks"); // NOI18N
        xTextArea1.setRequired(true);
        xTextArea1.setTextCase(com.rameses.rcp.constant.TextCase.UPPER);
        jScrollPane1.setViewportView(xTextArea1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        xFormPanel4.setCellpadding(new java.awt.Insets(0, 0, 0, 5));
        xFormPanel4.setPadding(new java.awt.Insets(0, 0, 0, 0));

        xDecimalField1.setCaption("Marketer");
        xDecimalField1.setCaptionWidth(80);
        xDecimalField1.setFontStyle("font-size:14;");
        xDecimalField1.setName("data.marketeramount"); // NOI18N
        xDecimalField1.setPreferredSize(new java.awt.Dimension(0, 20));
        xDecimalField1.setRequired(true);
        xFormPanel4.add(xDecimalField1);

        xDecimalField2.setCaption("CI");
        xDecimalField2.setCaptionWidth(80);
        xDecimalField2.setFontStyle("font-size:14;");
        xDecimalField2.setName("data.ciamount"); // NOI18N
        xDecimalField2.setPreferredSize(new java.awt.Dimension(0, 20));
        xDecimalField2.setRequired(true);
        xFormPanel4.add(xDecimalField2);

        xDecimalField3.setCaption("FCA");
        xDecimalField3.setCaptionWidth(80);
        xDecimalField3.setFontStyle("font-size:14;");
        xDecimalField3.setName("data.fcaamount"); // NOI18N
        xDecimalField3.setPreferredSize(new java.awt.Dimension(0, 20));
        xDecimalField3.setRequired(true);
        xFormPanel4.add(xDecimalField3);

        xFormPanel2.setCellpadding(new java.awt.Insets(0, 0, 0, 5));
        xFormPanel2.setPadding(new java.awt.Insets(0, 0, 0, 0));

        xDecimalField4.setCaption("CAO");
        xDecimalField4.setCaptionWidth(80);
        xDecimalField4.setFontStyle("font-size:14;");
        xDecimalField4.setName("data.caoamount"); // NOI18N
        xDecimalField4.setPreferredSize(new java.awt.Dimension(0, 20));
        xDecimalField4.setRequired(true);
        xFormPanel2.add(xDecimalField4);

        xDecimalField5.setCaption("BCOH");
        xDecimalField5.setCaptionWidth(80);
        xDecimalField5.setFontStyle("font-size:14;");
        xDecimalField5.setName("data.bcohamount"); // NOI18N
        xDecimalField5.setPreferredSize(new java.awt.Dimension(0, 20));
        xDecimalField5.setRequired(true);
        xFormPanel2.add(xDecimalField5);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(xFormPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(xFormPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(xFormPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(xFormPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        add(jPanel3, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private com.rameses.rcp.control.XDecimalField xDecimalField1;
    private com.rameses.rcp.control.XDecimalField xDecimalField2;
    private com.rameses.rcp.control.XDecimalField xDecimalField3;
    private com.rameses.rcp.control.XDecimalField xDecimalField4;
    private com.rameses.rcp.control.XDecimalField xDecimalField5;
    private com.rameses.rcp.control.XFormPanel xFormPanel1;
    private com.rameses.rcp.control.XFormPanel xFormPanel2;
    private com.rameses.rcp.control.XFormPanel xFormPanel4;
    private com.rameses.rcp.control.XLabel xLabel1;
    private com.rameses.rcp.control.XSeparator xSeparator1;
    private com.rameses.rcp.control.XTextArea xTextArea1;
    // End of variables declaration//GEN-END:variables
    
}
