/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.clfc.patch.ledger.edit.posting.branch;

import com.rameses.osiris2.themes.OKCancelPage;
import com.rameses.rcp.ui.annotations.StyleSheet;
import com.rameses.rcp.ui.annotations.Template;

/**
 *
 * @author louie
 */

@StyleSheet
@Template(OKCancelPage.class)
public class PostingDetailPage extends javax.swing.JPanel {

    /**
     * Creates new form PostingDetailPage
     */
    public PostingDetailPage() {
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
        xDateField1 = new com.rameses.rcp.control.XDateField();
        xDecimalField1 = new com.rameses.rcp.control.XDecimalField();
        xDecimalField2 = new com.rameses.rcp.control.XDecimalField();
        xDecimalField3 = new com.rameses.rcp.control.XDecimalField();
        xDecimalField4 = new com.rameses.rcp.control.XDecimalField();
        xDecimalField5 = new com.rameses.rcp.control.XDecimalField();
        xTextField1 = new com.rameses.rcp.control.XTextField();
        xDateField2 = new com.rameses.rcp.control.XDateField();
        xDecimalField6 = new com.rameses.rcp.control.XDecimalField();
        xDecimalField7 = new com.rameses.rcp.control.XDecimalField();

        xFormPanel1.setCaptionWidth(130);

        xDateField1.setCaption("Schedule of Payment");
        xDateField1.setName("entity.dtschedule"); // NOI18N
        xFormPanel1.add(xDateField1);

        xDecimalField1.setCaption("Partial Payment");
        xDecimalField1.setName("entity.partialpayment"); // NOI18N
        xFormPanel1.add(xDecimalField1);

        xDecimalField2.setCaption("Balance");
        xDecimalField2.setName("entity.balance"); // NOI18N
        xFormPanel1.add(xDecimalField2);

        xDecimalField3.setCaption("Interest Paid");
        xDecimalField3.setName("entity.interest"); // NOI18N
        xFormPanel1.add(xDecimalField3);

        xDecimalField4.setCaption("Penalty Charge");
        xDecimalField4.setName("entity.penalty"); // NOI18N
        xDecimalField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xDecimalField4ActionPerformed(evt);
            }
        });
        xFormPanel1.add(xDecimalField4);

        xDecimalField5.setCaption("Total Payment");
        xDecimalField5.setName("entity.totalpayment"); // NOI18N
        xFormPanel1.add(xDecimalField5);

        xTextField1.setCaption("OR No.");
        xTextField1.setName("entity.refno"); // NOI18N
        xTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xTextField1ActionPerformed(evt);
            }
        });
        xFormPanel1.add(xTextField1);

        xDateField2.setCaption("Date Paid");
        xDateField2.setName("entity.dtpaid"); // NOI18N
        xFormPanel1.add(xDateField2);

        xDecimalField6.setCaption("Lacking Interest");
        xDecimalField6.setName("entity.lackinginterest"); // NOI18N
        xFormPanel1.add(xDecimalField6);

        xDecimalField7.setCaption("Lacking Penalty");
        xDecimalField7.setName("entity.lackingpenalty"); // NOI18N
        xFormPanel1.add(xDecimalField7);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void xDecimalField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xDecimalField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_xDecimalField4ActionPerformed

    private void xTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_xTextField1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.rcp.control.XDateField xDateField1;
    private com.rameses.rcp.control.XDateField xDateField2;
    private com.rameses.rcp.control.XDecimalField xDecimalField1;
    private com.rameses.rcp.control.XDecimalField xDecimalField2;
    private com.rameses.rcp.control.XDecimalField xDecimalField3;
    private com.rameses.rcp.control.XDecimalField xDecimalField4;
    private com.rameses.rcp.control.XDecimalField xDecimalField5;
    private com.rameses.rcp.control.XDecimalField xDecimalField6;
    private com.rameses.rcp.control.XDecimalField xDecimalField7;
    private com.rameses.rcp.control.XFormPanel xFormPanel1;
    private com.rameses.rcp.control.XTextField xTextField1;
    // End of variables declaration//GEN-END:variables
}
