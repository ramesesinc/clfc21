/*
 * OtherLendingForm.java
 *
 * Created on September 2, 2013, 11:25 AM
 */

package com.rameses.clfc.loan.otherlending;

import com.rameses.osiris2.themes.OKCancelPage;
import com.rameses.rcp.ui.annotations.StyleSheet;
import com.rameses.rcp.ui.annotations.Template;
import java.math.BigDecimal;

/**
 *
 * @author  Rameses
 */
@StyleSheet
@Template(OKCancelPage.class)
public class OtherLendingFormPage extends javax.swing.JPanel {
    
    /** Creates new form OtherLendingForm */
    public OtherLendingFormPage() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        xFormPanel1 = new com.rameses.rcp.control.XFormPanel();
        xTextField1 = new com.rameses.rcp.control.XTextField();
        xTextField2 = new com.rameses.rcp.control.XTextField();
        xTextField3 = new com.rameses.rcp.control.XTextField();
        xFormPanel2 = new com.rameses.rcp.control.XFormPanel();
        xDecimalField1 = new com.rameses.rcp.control.XDecimalField();
        xDateField1 = new com.rameses.rcp.control.XDateField();
        xDateField2 = new com.rameses.rcp.control.XDateField();
        xIntegerField1 = new com.rameses.rcp.control.XIntegerField();
        xFormPanel3 = new com.rameses.rcp.control.XFormPanel();
        xDecimalField4 = new com.rameses.rcp.control.XDecimalField();
        xComboBox1 = new com.rameses.rcp.control.XComboBox();
        xDecimalField3 = new com.rameses.rcp.control.XDecimalField();
        xLabel1 = new com.rameses.rcp.control.XLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        xTextArea2 = new com.rameses.rcp.control.XTextArea();
        xLabel2 = new com.rameses.rcp.control.XLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        xTextArea1 = new com.rameses.rcp.control.XTextArea();

        jPanel1.setLayout(null);

        com.rameses.rcp.control.border.XTitledBorder xTitledBorder1 = new com.rameses.rcp.control.border.XTitledBorder();
        xTitledBorder1.setTitle("  General Information  ");
        jPanel1.setBorder(xTitledBorder1);

        xFormPanel1.setPadding(new java.awt.Insets(3, 0, 0, 0));
        xTextField1.setCaption("Kind of Loan");
        xTextField1.setCaptionWidth(140);
        xTextField1.setIndex(-10);
        xTextField1.setName("entity.kind");
        xTextField1.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel1.add(xTextField1);

        xTextField2.setCaption("Name of Lending Inst.");
        xTextField2.setCaptionWidth(140);
        xTextField2.setName("entity.name");
        xTextField2.setPreferredSize(new java.awt.Dimension(0, 20));
        xTextField2.setRequired(true);
        xFormPanel1.add(xTextField2);

        xTextField3.setCaption("Address");
        xTextField3.setCaptionWidth(140);
        xTextField3.setName("entity.address");
        xTextField3.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel1.add(xTextField3);

        jPanel1.add(xFormPanel1);
        xFormPanel1.setBounds(15, 25, 515, 70);

        xFormPanel2.setPadding(new java.awt.Insets(0, 0, 0, 0));
        xDecimalField1.setCaption("Loan Amount");
        xDecimalField1.setCaptionWidth(140);
        xDecimalField1.setName("entity.amount");
        xDecimalField1.setPreferredSize(new java.awt.Dimension(0, 20));
        xDecimalField1.setRequired(true);
        xFormPanel2.add(xDecimalField1);

        xDateField1.setCaption("Date Granted");
        xDateField1.setCaptionWidth(140);
        xDateField1.setName("entity.dtgranted");
        xDateField1.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel2.add(xDateField1);

        xDateField2.setCaption("Maturity Date");
        xDateField2.setCaptionWidth(140);
        xDateField2.setName("entity.dtmatured");
        xDateField2.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel2.add(xDateField2);

        xIntegerField1.setCaption("Term");
        xIntegerField1.setCaptionWidth(140);
        xIntegerField1.setName("entity.term");
        xIntegerField1.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel2.add(xIntegerField1);

        jPanel1.add(xFormPanel2);
        xFormPanel2.setBounds(15, 95, 270, 90);

        xFormPanel3.setPadding(new java.awt.Insets(0, 0, 0, 0));
        xDecimalField4.setCaption("Interest Rate");
        xDecimalField4.setCaptionWidth(100);
        xDecimalField4.setName("entity.interest");
        xDecimalField4.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel3.add(xDecimalField4);

        xComboBox1.setCaption("Mode of Payment");
        xComboBox1.setCaptionWidth(100);
        xComboBox1.setExpression("#{item.value}");
        xComboBox1.setItemKey("value");
        xComboBox1.setItems("modesOfPayment");
        xComboBox1.setName("entity.paymentmode");
        xComboBox1.setPreferredSize(new java.awt.Dimension(0, 22));
        xFormPanel3.add(xComboBox1);

        xDecimalField3.setCaption("Payment");
        xDecimalField3.setCaptionWidth(100);
        xDecimalField3.setName("entity.paymentamount");
        xDecimalField3.setPreferredSize(new java.awt.Dimension(0, 20));
        xDecimalField3.setRequired(true);
        xFormPanel3.add(xDecimalField3);

        jPanel1.add(xFormPanel3);
        xFormPanel3.setBounds(305, 95, 225, 90);

        xLabel1.setFontStyle("font-weight:bold;");
        xLabel1.setForeground(new java.awt.Color(80, 80, 80));
        xLabel1.setPadding(new java.awt.Insets(5, 0, 1, 1));
        xLabel1.setText("Collateral(s) Offered:");
        jPanel1.add(xLabel1);
        xLabel1.setBounds(15, 190, 140, 20);

        xTextArea2.setName("entity.collateral");
        xTextArea2.setTextCase(com.rameses.rcp.constant.TextCase.UPPER);
        jScrollPane2.setViewportView(xTextArea2);

        jPanel1.add(jScrollPane2);
        jScrollPane2.setBounds(15, 210, 515, 70);

        xLabel2.setFontStyle("font-weight:bold;");
        xLabel2.setForeground(new java.awt.Color(80, 80, 80));
        xLabel2.setPadding(new java.awt.Insets(5, 0, 1, 1));
        xLabel2.setText("Remarks:");
        jPanel1.add(xLabel2);
        xLabel2.setBounds(15, 280, 110, 20);

        xTextArea1.setName("entity.remarks");
        xTextArea1.setTextCase(com.rameses.rcp.constant.TextCase.UPPER);
        jScrollPane1.setViewportView(xTextArea1);

        jPanel1.add(jScrollPane1);
        jScrollPane1.setBounds(15, 300, 515, 70);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private com.rameses.rcp.control.XComboBox xComboBox1;
    private com.rameses.rcp.control.XDateField xDateField1;
    private com.rameses.rcp.control.XDateField xDateField2;
    private com.rameses.rcp.control.XDecimalField xDecimalField1;
    private com.rameses.rcp.control.XDecimalField xDecimalField3;
    private com.rameses.rcp.control.XDecimalField xDecimalField4;
    private com.rameses.rcp.control.XFormPanel xFormPanel1;
    private com.rameses.rcp.control.XFormPanel xFormPanel2;
    private com.rameses.rcp.control.XFormPanel xFormPanel3;
    private com.rameses.rcp.control.XIntegerField xIntegerField1;
    private com.rameses.rcp.control.XLabel xLabel1;
    private com.rameses.rcp.control.XLabel xLabel2;
    private com.rameses.rcp.control.XTextArea xTextArea1;
    private com.rameses.rcp.control.XTextArea xTextArea2;
    private com.rameses.rcp.control.XTextField xTextField1;
    private com.rameses.rcp.control.XTextField xTextField2;
    private com.rameses.rcp.control.XTextField xTextField3;
    // End of variables declaration//GEN-END:variables
    
}