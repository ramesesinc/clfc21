/*
 * BorrowerSpouseInfoPage.java
 *
 * Created on September 9, 2013, 9:25 PM
 */

package com.rameses.clfc.loan.collateral;

import com.rameses.rcp.ui.annotations.StyleSheet;

/**
 *
 * @author  compaq
 */
@StyleSheet
public class CollateralOtherPage extends javax.swing.JPanel {
    
    public CollateralOtherPage() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        xTextArea1 = new com.rameses.rcp.control.XTextArea();
        jPanel2 = new javax.swing.JPanel();
        fileViewPanel1 = new com.rameses.filemgmt.components.FileViewPanel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 10));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setPreferredSize(new java.awt.Dimension(47, 200));

        xTextArea1.setCaption("Remarks");
        xTextArea1.setName("entity.remarks"); // NOI18N
        xTextArea1.setTextCase(com.rameses.rcp.constant.TextCase.UPPER);
        jScrollPane1.setViewportView(xTextArea1);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("  General Information   ", jPanel1);

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel2.setLayout(new java.awt.BorderLayout());

        fileViewPanel1.setDividerLocation(120);
        fileViewPanel1.setEditableWhen("#{mode != 'read'}");
        fileViewPanel1.setItems("entity.attachments");
        jPanel2.add(fileViewPanel1, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("  Attachments   ", jPanel2);

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.filemgmt.components.FileViewPanel fileViewPanel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private com.rameses.rcp.control.XTextArea xTextArea1;
    // End of variables declaration//GEN-END:variables
    
}
