/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.clfc.audit.amnesty.capture.baddebt;

import com.rameses.osiris2.themes.FormPage;
import com.rameses.rcp.ui.annotations.StyleSheet;
import com.rameses.rcp.ui.annotations.Template;

/**
 *
 * @author louie
 */

@StyleSheet
@Template(FormPage.class)
public class BadDebtAmnestyPage extends javax.swing.JPanel {

    /**
     * Creates new form BadDebtAmnestyPage
     */
    public BadDebtAmnestyPage() {
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

        xPanel3 = new com.rameses.rcp.control.XPanel();
        xLabel1 = new com.rameses.rcp.control.XLabel();
        xPanel2 = new com.rameses.rcp.control.XPanel();
        xPanel1 = new com.rameses.rcp.control.XPanel();
        xFormPanel1 = new com.rameses.rcp.control.XFormPanel();
        xLabel4 = new com.rameses.rcp.control.XLabel();
        xDateField3 = new com.rameses.rcp.control.XDateField();
        xLookupField1 = new com.rameses.rcp.control.XLookupField();
        xLabel2 = new com.rameses.rcp.control.XLabel();
        xDecimalField1 = new com.rameses.rcp.control.XDecimalField();
        xDateField1 = new com.rameses.rcp.control.XDateField();
        xDateField2 = new com.rameses.rcp.control.XDateField();
        xDecimalField2 = new com.rameses.rcp.control.XDecimalField();
        xPanel5 = new com.rameses.rcp.control.XPanel();
        xFormPanel2 = new com.rameses.rcp.control.XFormPanel();
        xDateField4 = new com.rameses.rcp.control.XDateField();
        xTextField1 = new com.rameses.rcp.control.XTextField();
        xDecimalField3 = new com.rameses.rcp.control.XDecimalField();
        xPanel4 = new com.rameses.rcp.control.XPanel();
        xLabel3 = new com.rameses.rcp.control.XLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        xTextArea1 = new com.rameses.rcp.control.XTextArea();

        setLayout(new java.awt.BorderLayout());

        xPanel3.setPreferredSize(new java.awt.Dimension(100, 40));

        xLabel1.setExpression("${status}");
        xLabel1.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        xLabel1.setForeground(new java.awt.Color(255, 0, 0));
        xLabel1.setPadding(new java.awt.Insets(3, 5, 0, 0));
        xLabel1.setPreferredSize(new java.awt.Dimension(400, 30));

        javax.swing.GroupLayout xPanel3Layout = new javax.swing.GroupLayout(xPanel3);
        xPanel3.setLayout(xPanel3Layout);
        xPanel3Layout.setHorizontalGroup(
            xPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(84, Short.MAX_VALUE))
        );
        xPanel3Layout.setVerticalGroup(
            xPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel3Layout.createSequentialGroup()
                .addComponent(xLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 13, Short.MAX_VALUE))
        );

        add(xPanel3, java.awt.BorderLayout.NORTH);

        com.rameses.rcp.control.border.XTitledBorder xTitledBorder1 = new com.rameses.rcp.control.border.XTitledBorder();
        xTitledBorder1.setTitle("General Information");
        xPanel1.setBorder(xTitledBorder1);

        xFormPanel1.setCaptionWidth(90);

        xLabel4.setCaption("Ref. No.");
        xLabel4.setExpression("#{entity.refno}");
        xLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel4.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel1.add(xLabel4);

        xDateField3.setCaption("Date");
        xDateField3.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDateField3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xDateField3.setName("entity.dtstarted"); // NOI18N
        xDateField3.setOutputFormat("MMM-dd-yyyy");
        xDateField3.setRequired(true);
        xFormPanel1.add(xDateField3);

        xLookupField1.setCaption("App. No.");
        xLookupField1.setCaptionWidth(90);
        xLookupField1.setDisableWhen("#{mode=='read'}");
        xLookupField1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xLookupField1.setExpression("${item.appno}");
        xLookupField1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLookupField1.setHandler("ledgerLookup");
        xLookupField1.setName("entity.loanapp"); // NOI18N
        xLookupField1.setPreferredSize(new java.awt.Dimension(150, 20));
        xLookupField1.setRequired(true);
        xFormPanel1.add(xLookupField1);

        xLabel2.setCaption("Borrower");
        xLabel2.setCaptionWidth(90);
        xLabel2.setExpression("#{entity.borrower.name}");
        xLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel2.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel1.add(xLabel2);

        xDecimalField1.setCaption("Loan Amount");
        xDecimalField1.setCaptionWidth(90);
        xDecimalField1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDecimalField1.setName("entity.loanapp.loanamount"); // NOI18N
        xFormPanel1.add(xDecimalField1);

        xDateField1.setCaption("Release Date");
        xDateField1.setCaptionWidth(90);
        xDateField1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDateField1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xDateField1.setName("entity.loanapp.dtreleased"); // NOI18N
        xDateField1.setOutputFormat("MMM-dd-yyyy");
        xFormPanel1.add(xDateField1);

        xDateField2.setCaption("Maturity Date");
        xDateField2.setCaptionWidth(90);
        xDateField2.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDateField2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xDateField2.setName("entity.loanapp.dtmatured"); // NOI18N
        xDateField2.setOutputFormat("MMM-dd-yyyy");
        xFormPanel1.add(xDateField2);

        xDecimalField2.setCaption("Balance");
        xDecimalField2.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDecimalField2.setName("entity.ledger.balance"); // NOI18N
        xFormPanel1.add(xDecimalField2);

        javax.swing.GroupLayout xPanel1Layout = new javax.swing.GroupLayout(xPanel1);
        xPanel1.setLayout(xPanel1Layout);
        xPanel1Layout.setHorizontalGroup(
            xPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                .addContainerGap())
        );
        xPanel1Layout.setVerticalGroup(
            xPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        com.rameses.rcp.control.border.XTitledBorder xTitledBorder2 = new com.rameses.rcp.control.border.XTitledBorder();
        xTitledBorder2.setTitle("Payment Information");
        xPanel5.setBorder(xTitledBorder2);

        xFormPanel2.setCaptionWidth(90);

        xDateField4.setCaption("Date");
        xDateField4.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDateField4.setName("entity.payment.txndate"); // NOI18N
        xDateField4.setOutputFormat("MMM-dd-yyyy");
        xFormPanel2.add(xDateField4);

        xTextField1.setCaption("Ref. No.");
        xTextField1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xTextField1.setName("entity.payment.refno"); // NOI18N
        xTextField1.setRequired(true);
        xFormPanel2.add(xTextField1);

        xDecimalField3.setCaption("Amount");
        xDecimalField3.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDecimalField3.setName("entity.payment.amount"); // NOI18N
        xFormPanel2.add(xDecimalField3);

        javax.swing.GroupLayout xPanel5Layout = new javax.swing.GroupLayout(xPanel5);
        xPanel5.setLayout(xPanel5Layout);
        xPanel5Layout.setHorizontalGroup(
            xPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        xPanel5Layout.setVerticalGroup(
            xPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        xLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        xLabel3.setPreferredSize(new java.awt.Dimension(150, 20));
        xLabel3.setText("Remarks");

        xTextArea1.setLineWrap(true);
        xTextArea1.setWrapStyleWord(true);
        xTextArea1.setCaption("Remarks");
        xTextArea1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xTextArea1.setName("entity.remarks"); // NOI18N
        xTextArea1.setRequired(true);
        jScrollPane1.setViewportView(xTextArea1);

        javax.swing.GroupLayout xPanel4Layout = new javax.swing.GroupLayout(xPanel4);
        xPanel4.setLayout(xPanel4Layout);
        xPanel4Layout.setHorizontalGroup(
            xPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel4Layout.createSequentialGroup()
                .addComponent(xLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 324, Short.MAX_VALUE))
            .addComponent(jScrollPane1)
        );
        xPanel4Layout.setVerticalGroup(
            xPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel4Layout.createSequentialGroup()
                .addComponent(xLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout xPanel2Layout = new javax.swing.GroupLayout(xPanel2);
        xPanel2.setLayout(xPanel2Layout);
        xPanel2Layout.setHorizontalGroup(
            xPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, xPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(xPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(xPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(xPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(xPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        xPanel2Layout.setVerticalGroup(
            xPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel2Layout.createSequentialGroup()
                .addComponent(xPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(xPanel2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private com.rameses.rcp.control.XDateField xDateField1;
    private com.rameses.rcp.control.XDateField xDateField2;
    private com.rameses.rcp.control.XDateField xDateField3;
    private com.rameses.rcp.control.XDateField xDateField4;
    private com.rameses.rcp.control.XDecimalField xDecimalField1;
    private com.rameses.rcp.control.XDecimalField xDecimalField2;
    private com.rameses.rcp.control.XDecimalField xDecimalField3;
    private com.rameses.rcp.control.XFormPanel xFormPanel1;
    private com.rameses.rcp.control.XFormPanel xFormPanel2;
    private com.rameses.rcp.control.XLabel xLabel1;
    private com.rameses.rcp.control.XLabel xLabel2;
    private com.rameses.rcp.control.XLabel xLabel3;
    private com.rameses.rcp.control.XLabel xLabel4;
    private com.rameses.rcp.control.XLookupField xLookupField1;
    private com.rameses.rcp.control.XPanel xPanel1;
    private com.rameses.rcp.control.XPanel xPanel2;
    private com.rameses.rcp.control.XPanel xPanel3;
    private com.rameses.rcp.control.XPanel xPanel4;
    private com.rameses.rcp.control.XPanel xPanel5;
    private com.rameses.rcp.control.XTextArea xTextArea1;
    private com.rameses.rcp.control.XTextField xTextField1;
    // End of variables declaration//GEN-END:variables
}
