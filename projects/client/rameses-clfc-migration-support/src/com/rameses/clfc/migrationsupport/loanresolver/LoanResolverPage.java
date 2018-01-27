/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.clfc.migrationsupport.loanresolver;

import com.rameses.osiris2.themes.FormPage;
import com.rameses.rcp.ui.annotations.StyleSheet;
import com.rameses.rcp.ui.annotations.Template;

/**
 *
 * @author louie
 */

@StyleSheet
@Template(FormPage.class)
public class LoanResolverPage extends javax.swing.JPanel {

    /**
     * Creates new form LoanResolverPage
     */
    public LoanResolverPage() {
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

        xLabel1 = new com.rameses.rcp.control.XLabel();
        xDataTable1 = new com.rameses.rcp.control.XDataTable();
        xFormPanel1 = new com.rameses.rcp.control.XFormPanel();
        xLabel2 = new com.rameses.rcp.control.XLabel();
        xLabel3 = new com.rameses.rcp.control.XLabel();
        xLookupField3 = new com.rameses.rcp.control.XLookupField();
        xLabel5 = new com.rameses.rcp.control.XLabel();
        xDecimalField1 = new com.rameses.rcp.control.XDecimalField();
        xDateField1 = new com.rameses.rcp.control.XDateField();
        xNumberField1 = new com.rameses.rcp.control.XNumberField();
        xDecimalField2 = new com.rameses.rcp.control.XDecimalField();
        xLookupField1 = new com.rameses.rcp.control.XLookupField();
        xDecimalField4 = new com.rameses.rcp.control.XDecimalField();
        xIntegerField1 = new com.rameses.rcp.control.XIntegerField();
        xComboBox1 = new com.rameses.rcp.control.XComboBox();
        xDecimalField3 = new com.rameses.rcp.control.XDecimalField();
        xLookupField2 = new com.rameses.rcp.control.XLookupField();
        xLabel4 = new com.rameses.rcp.control.XLabel();
        xComboBox2 = new com.rameses.rcp.control.XComboBox();

        xLabel1.setText("xLabel1");

        xLabel2.setCaption("Loan No.");
        xLabel2.setCaptionWidth(130);
        xLabel2.setExpression("#{entity.objid}");
        xLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel2.setPreferredSize(new java.awt.Dimension(300, 20));
        xFormPanel1.add(xLabel2);

        xLabel3.setCaption("Acct. Name");
        xLabel3.setCaptionWidth(130);
        xLabel3.setExpression("#{entity.borrowername}");
        xLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel3.setPreferredSize(new java.awt.Dimension(300, 20));
        xFormPanel1.add(xLabel3);

        xLookupField3.setCaption("Borrower ID");
        xLookupField3.setCaptionWidth(130);
        xLookupField3.setDisableWhen("#{mode=='read'}");
        xLookupField3.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xLookupField3.setExpression("#{entity.borrower.objid}");
        xLookupField3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLookupField3.setHandler("borrowerLookup");
        xLookupField3.setName("borrower"); // NOI18N
        xLookupField3.setPreferredSize(new java.awt.Dimension(150, 20));
        xLookupField3.setRequired(true);
        xFormPanel1.add(xLookupField3);

        xLabel5.setCaption("Borrower Name");
        xLabel5.setCaptionWidth(130);
        xLabel5.setExpression("#{entity.borrower.name}");
        xLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel5.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel1.add(xLabel5);

        xDecimalField1.setCaption("Loan Amount");
        xDecimalField1.setCaptionWidth(130);
        xDecimalField1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDecimalField1.setName("entity.loanamount"); // NOI18N
        xFormPanel1.add(xDecimalField1);

        xDateField1.setCaption("Date Released");
        xDateField1.setCaptionWidth(130);
        xDateField1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDateField1.setName("entity.loandate"); // NOI18N
        xDateField1.setOutputFormat("MMM-dd-yyyy");
        xFormPanel1.add(xDateField1);

        xNumberField1.setCaption("Term");
        xNumberField1.setCaptionWidth(130);
        xNumberField1.setName("entity.term"); // NOI18N
        xFormPanel1.add(xNumberField1);

        xDecimalField2.setCaption("Interest Rate");
        xDecimalField2.setCaptionWidth(130);
        xDecimalField2.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDecimalField2.setName("entity.intrate"); // NOI18N
        xFormPanel1.add(xDecimalField2);

        xLookupField1.setCaption("Product Type");
        xLookupField1.setCaptionWidth(130);
        xLookupField1.setDisableWhen("#{mode=='read'}");
        xLookupField1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xLookupField1.setExpression("#{entity.producttype.name}");
        xLookupField1.setHandler("productTypeLookup");
        xLookupField1.setName("producttype"); // NOI18N
        xLookupField1.setPreferredSize(new java.awt.Dimension(150, 20));
        xLookupField1.setRequired(true);
        xFormPanel1.add(xLookupField1);

        xDecimalField4.setCaption("Interest Rate");
        xDecimalField4.setCaptionWidth(130);
        xDecimalField4.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDecimalField4.setName("entity.producttype.interestrate"); // NOI18N
        xFormPanel1.add(xDecimalField4);

        xIntegerField1.setCaption("Term");
        xIntegerField1.setCaptionWidth(130);
        xIntegerField1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xIntegerField1.setName("entity.producttype.term"); // NOI18N
        xFormPanel1.add(xIntegerField1);

        xComboBox1.setCaption("Payment Method");
        xComboBox1.setCaptionWidth(130);
        xComboBox1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xComboBox1.setItems("paymentMethodList");
        xComboBox1.setName("entity.paymentmethod"); // NOI18N
        xComboBox1.setPreferredSize(new java.awt.Dimension(170, 21));
        xComboBox1.setRequired(true);
        xFormPanel1.add(xComboBox1);

        xDecimalField3.setCaption("Overpayment Amount");
        xDecimalField3.setCaptionWidth(130);
        xDecimalField3.setDepends(new String[] {"entity.paymentmethod"});
        xDecimalField3.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDecimalField3.setName("entity.overpaymentamount"); // NOI18N
        xDecimalField3.setRequired(true);
        xDecimalField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xDecimalField3ActionPerformed(evt);
            }
        });
        xFormPanel1.add(xDecimalField3);

        xLookupField2.setCaption("Route");
        xLookupField2.setCaptionWidth(130);
        xLookupField2.setDisableWhen("#{mode=='read'}");
        xLookupField2.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xLookupField2.setExpression("#{entity.route.description}");
        xLookupField2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLookupField2.setHandler("routeLookup");
        xLookupField2.setName("route"); // NOI18N
        xLookupField2.setPreferredSize(new java.awt.Dimension(200, 20));
        xLookupField2.setRequired(true);
        xFormPanel1.add(xLookupField2);

        xLabel4.setCaption("Area");
        xLabel4.setCaptionWidth(130);
        xLabel4.setExpression("#{entity.route.area}");
        xLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel4.setPreferredSize(new java.awt.Dimension(300, 20));
        xFormPanel1.add(xLabel4);

        xComboBox2.setAllowNull(false);
        xComboBox2.setCaption("Account Status");
        xComboBox2.setCaptionWidth(130);
        xComboBox2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xComboBox2.setItems("accountStatusList");
        xComboBox2.setName("entity.acctstate"); // NOI18N
        xComboBox2.setPreferredSize(new java.awt.Dimension(150, 21));
        xComboBox2.setRequired(true);
        xFormPanel1.add(xComboBox2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void xDecimalField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xDecimalField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_xDecimalField3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.rcp.control.XComboBox xComboBox1;
    private com.rameses.rcp.control.XComboBox xComboBox2;
    private com.rameses.rcp.control.XDataTable xDataTable1;
    private com.rameses.rcp.control.XDateField xDateField1;
    private com.rameses.rcp.control.XDecimalField xDecimalField1;
    private com.rameses.rcp.control.XDecimalField xDecimalField2;
    private com.rameses.rcp.control.XDecimalField xDecimalField3;
    private com.rameses.rcp.control.XDecimalField xDecimalField4;
    private com.rameses.rcp.control.XFormPanel xFormPanel1;
    private com.rameses.rcp.control.XIntegerField xIntegerField1;
    private com.rameses.rcp.control.XLabel xLabel1;
    private com.rameses.rcp.control.XLabel xLabel2;
    private com.rameses.rcp.control.XLabel xLabel3;
    private com.rameses.rcp.control.XLabel xLabel4;
    private com.rameses.rcp.control.XLabel xLabel5;
    private com.rameses.rcp.control.XLookupField xLookupField1;
    private com.rameses.rcp.control.XLookupField xLookupField2;
    private com.rameses.rcp.control.XLookupField xLookupField3;
    private com.rameses.rcp.control.XNumberField xNumberField1;
    // End of variables declaration//GEN-END:variables
}
