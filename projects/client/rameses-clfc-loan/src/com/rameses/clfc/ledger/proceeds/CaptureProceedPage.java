/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.clfc.ledger.proceeds;

import com.rameses.osiris2.themes.FormPage;
import com.rameses.rcp.ui.annotations.StyleSheet;
import com.rameses.rcp.ui.annotations.Template;

/**
 *
 * @author louie
 */

@StyleSheet
@Template(FormPage.class)
public class CaptureProceedPage extends javax.swing.JPanel {

    /**
     * Creates new form ProceedPage
     */
    public CaptureProceedPage() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        xFormPanel1 = new com.rameses.rcp.control.XFormPanel();
        xLabel1 = new com.rameses.rcp.control.XLabel();
        xLabel2 = new com.rameses.rcp.control.XLabel();
        xDateField1 = new com.rameses.rcp.control.XDateField();
        xTextField1 = new com.rameses.rcp.control.XTextField();
        xDecimalField1 = new com.rameses.rcp.control.XDecimalField();
        jScrollPane1 = new javax.swing.JScrollPane();
        xTextArea1 = new com.rameses.rcp.control.XTextArea();

        xLabel1.setCaption("Borrower");
        xLabel1.setExpression("${entity.borrower.name}");
        xLabel1.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel1.add(xLabel1);

        xLabel2.setCaption("Address");
        xLabel2.setExpression("${entity.borrower.address}");
        xLabel2.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel1.add(xLabel2);

        xDateField1.setCaption("Date");
        xDateField1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDateField1.setName("entity.txndate"); // NOI18N
        xDateField1.setOutputFormat("MMM-dd-yyyy");
        xDateField1.setRequired(true);
        xFormPanel1.add(xDateField1);

        xTextField1.setCaption("Ref. No.");
        xTextField1.setName("entity.refno"); // NOI18N
        xTextField1.setPreferredSize(new java.awt.Dimension(150, 20));
        xTextField1.setRequired(true);
        xFormPanel1.add(xTextField1);

        xDecimalField1.setCaption("Amount");
        xDecimalField1.setName("entity.amount"); // NOI18N
        xDecimalField1.setRequired(true);
        xFormPanel1.add(xDecimalField1);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(0, 80));

        xTextArea1.setCaption("Description");
        xTextArea1.setName("entity.description"); // NOI18N
        xTextArea1.setRequired(true);
        jScrollPane1.setViewportView(xTextArea1);

        xFormPanel1.add(jScrollPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 192, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private com.rameses.rcp.control.XDateField xDateField1;
    private com.rameses.rcp.control.XDecimalField xDecimalField1;
    private com.rameses.rcp.control.XFormPanel xFormPanel1;
    private com.rameses.rcp.control.XLabel xLabel1;
    private com.rameses.rcp.control.XLabel xLabel2;
    private com.rameses.rcp.control.XTextArea xTextArea1;
    private com.rameses.rcp.control.XTextField xTextField1;
    // End of variables declaration//GEN-END:variables
}
