/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.clfc.report.loan.expiries;

import com.rameses.osiris2.themes.FormPage;
import com.rameses.rcp.ui.annotations.StyleSheet;
import com.rameses.rcp.ui.annotations.Template;

/**
 *
 * @author louie
 */

@StyleSheet
@Template(FormPage.class)
public class LoanExpiriesReportInitPage extends javax.swing.JPanel {

    /**
     * Creates new form BranchBranchLoanReleaseReportInitPage
     */
    public LoanExpiriesReportInitPage() {
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

        xPanel1 = new com.rameses.rcp.control.XPanel();
        xFormPanel1 = new com.rameses.rcp.control.XFormPanel();
        xDateField1 = new com.rameses.rcp.control.XDateField();
        xDateField2 = new com.rameses.rcp.control.XDateField();

        com.rameses.rcp.control.border.XTitledBorder xTitledBorder1 = new com.rameses.rcp.control.border.XTitledBorder();
        xTitledBorder1.setTitle("Initial Information");
        xPanel1.setBorder(xTitledBorder1);

        xDateField1.setCaption("Start Date");
        xDateField1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xDateField1.setName("startdate"); // NOI18N
        xDateField1.setOutputFormat("MMM-dd-yyyy");
        xDateField1.setRequired(true);
        xFormPanel1.add(xDateField1);

        xDateField2.setCaption("End Date");
        xDateField2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xDateField2.setName("enddate"); // NOI18N
        xDateField2.setOutputFormat("MMM-dd-yyyy");
        xDateField2.setRequired(true);
        xFormPanel1.add(xDateField2);

        javax.swing.GroupLayout xPanel1Layout = new javax.swing.GroupLayout(xPanel1);
        xPanel1.setLayout(xPanel1Layout);
        xPanel1Layout.setHorizontalGroup(
            xPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        xPanel1Layout.setVerticalGroup(
            xPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.rcp.control.XDateField xDateField1;
    private com.rameses.rcp.control.XDateField xDateField2;
    private com.rameses.rcp.control.XFormPanel xFormPanel1;
    private com.rameses.rcp.control.XPanel xPanel1;
    // End of variables declaration//GEN-END:variables
}
