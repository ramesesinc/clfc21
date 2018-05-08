/*
 * OnlineCollectionPage.java
 *
 * Created on February 3, 2014, 11:40 AM
 */

package com.rameses.clfc.loan.payment.bak;

import com.rameses.osiris2.themes.FormPage;
import com.rameses.rcp.ui.annotations.StyleSheet;
import com.rameses.rcp.ui.annotations.Template;

/**
 *
 * @author  louie
 */

@StyleSheet
@Template(FormPage.class)
public class PaymentPage extends javax.swing.JPanel {
    
    /** Creates new form OnlineCollectionPage */
    public PaymentPage() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        xFormPanel2 = new com.rameses.rcp.control.XFormPanel();
        xDecimalField8 = new com.rameses.rcp.control.XDecimalField();
        xDateField3 = new com.rameses.rcp.control.XDateField();
        xDateField1 = new com.rameses.rcp.control.XDateField();
        xLabel5 = new com.rameses.rcp.control.XLabel();
        xDecimalField7 = new com.rameses.rcp.control.XDecimalField();
        xFormPanel3 = new com.rameses.rcp.control.XFormPanel();
        xDecimalField1 = new com.rameses.rcp.control.XDecimalField();
        xDecimalField2 = new com.rameses.rcp.control.XDecimalField();
        xDecimalField3 = new com.rameses.rcp.control.XDecimalField();
        xDecimalField5 = new com.rameses.rcp.control.XDecimalField();
        xDecimalField4 = new com.rameses.rcp.control.XDecimalField();
        xPanel1 = new com.rameses.rcp.control.XPanel();
        xFormPanel4 = new com.rameses.rcp.control.XFormPanel();
        xLabel6 = new com.rameses.rcp.control.XLabel();
        xDateField2 = new com.rameses.rcp.control.XDateField();
        xDecimalField6 = new com.rameses.rcp.control.XDecimalField();
        xComboBox1 = new com.rameses.rcp.control.XComboBox();
        xFormPanel5 = new com.rameses.rcp.control.XFormPanel();
        xComboBox4 = new com.rameses.rcp.control.XComboBox();
        xDateField6 = new com.rameses.rcp.control.XDateField();
        xTextField3 = new com.rameses.rcp.control.XTextField();
        xTextField4 = new com.rameses.rcp.control.XTextField();
        xPanel2 = new com.rameses.rcp.control.XPanel();
        xFormPanel1 = new com.rameses.rcp.control.XFormPanel();
        xLabel1 = new com.rameses.rcp.control.XLabel();
        xLabel2 = new com.rameses.rcp.control.XLabel();
        xLabel3 = new com.rameses.rcp.control.XLabel();
        xLabel4 = new com.rameses.rcp.control.XLabel();

        com.rameses.rcp.control.border.XTitledBorder xTitledBorder1 = new com.rameses.rcp.control.border.XTitledBorder();
        xTitledBorder1.setTitle("Ledger Information");
        jPanel1.setBorder(xTitledBorder1);

        xFormPanel2.setCaptionWidth(95);
        xFormPanel2.setPadding(new java.awt.Insets(10, 5, 5, 5));

        xDecimalField8.setCaption("Loan Amount");
        xDecimalField8.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDecimalField8.setEnabled(false);
        xDecimalField8.setName("bill.loanamount"); // NOI18N
        xDecimalField8.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel2.add(xDecimalField8);

        xDateField3.setCaption("Date Released");
        xDateField3.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDateField3.setEnabled(false);
        xDateField3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xDateField3.setName("bill.dtreleased"); // NOI18N
        xDateField3.setOutputFormat("MMM-dd-yyyy");
        xDateField3.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel2.add(xDateField3);

        xDateField1.setCaption("Maturity Date");
        xDateField1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDateField1.setEnabled(false);
        xDateField1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xDateField1.setName("bill.dtmatured"); // NOI18N
        xDateField1.setOutputFormat("MMM-dd-yyyy");
        xDateField1.setPreferredSize(new java.awt.Dimension(0, 20));
        xDateField1.setReadonly(true);
        xFormPanel2.add(xDateField1);

        com.rameses.rcp.control.border.XLineBorder xLineBorder1 = new com.rameses.rcp.control.border.XLineBorder();
        xLineBorder1.setLineColor(new java.awt.Color(180, 180, 180));
        xLabel5.setBorder(xLineBorder1);
        xLabel5.setCaption("Term");
        xLabel5.setExpression("#{bill.term}");
        xLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel5.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel2.add(xLabel5);

        xDecimalField7.setCaption("Payment Sched.");
        xDecimalField7.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDecimalField7.setEnabled(false);
        xDecimalField7.setName("bill.dailydue"); // NOI18N
        xDecimalField7.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel2.add(xDecimalField7);

        xFormPanel3.setCaptionWidth(100);
        xFormPanel3.setPadding(new java.awt.Insets(10, 5, 5, 5));

        xDecimalField1.setCaption("Interest");
        xDecimalField1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDecimalField1.setEnabled(false);
        xDecimalField1.setName("bill.interest"); // NOI18N
        xDecimalField1.setPreferredSize(new java.awt.Dimension(0, 20));
        xDecimalField1.setReadonly(true);
        xFormPanel3.add(xDecimalField1);

        xDecimalField2.setCaption("Overpayment");
        xDecimalField2.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDecimalField2.setEnabled(false);
        xDecimalField2.setName("bill.overpayment"); // NOI18N
        xDecimalField2.setPreferredSize(new java.awt.Dimension(0, 20));
        xDecimalField2.setReadonly(true);
        xFormPanel3.add(xDecimalField2);

        xDecimalField3.setCaption("Overdue Penalty");
        xDecimalField3.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDecimalField3.setEnabled(false);
        xDecimalField3.setName("bill.overduepenalty"); // NOI18N
        xDecimalField3.setPreferredSize(new java.awt.Dimension(0, 20));
        xDecimalField3.setReadonly(true);
        xFormPanel3.add(xDecimalField3);

        xDecimalField5.setCaption("Updated Balance");
        xDecimalField5.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDecimalField5.setEnabled(false);
        xDecimalField5.setName("bill.balance"); // NOI18N
        xDecimalField5.setPreferredSize(new java.awt.Dimension(0, 20));
        xDecimalField5.setReadonly(true);
        xFormPanel3.add(xDecimalField5);

        xDecimalField4.setCaption("Amount Due");
        xDecimalField4.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDecimalField4.setEnabled(false);
        xDecimalField4.setName("bill.amountdue"); // NOI18N
        xDecimalField4.setPreferredSize(new java.awt.Dimension(0, 20));
        xDecimalField4.setReadonly(true);
        xFormPanel3.add(xDecimalField4);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(xFormPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xFormPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(68, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(xFormPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(xFormPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        com.rameses.rcp.control.border.XTitledBorder xTitledBorder2 = new com.rameses.rcp.control.border.XTitledBorder();
        xTitledBorder2.setTitle("Payment Information");
        xPanel1.setBorder(xTitledBorder2);

        com.rameses.rcp.control.border.XLineBorder xLineBorder2 = new com.rameses.rcp.control.border.XLineBorder();
        xLineBorder2.setLineColor(new java.awt.Color(180, 180, 180));
        xLabel6.setBorder(xLineBorder2);
        xLabel6.setCaption("Ref. No.");
        xLabel6.setExpression("#{entity.refno}");
        xLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel6.setPreferredSize(new java.awt.Dimension(150, 20));
        xFormPanel4.add(xLabel6);

        xDateField2.setCaption("Date");
        xDateField2.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDateField2.setEnabled(false);
        xDateField2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xDateField2.setName("entity.txndate"); // NOI18N
        xDateField2.setOutputFormat("MMM-dd-yyyy");
        xDateField2.setPreferredSize(new java.awt.Dimension(150, 20));
        xDateField2.setReadonly(true);
        xFormPanel4.add(xDateField2);

        xDecimalField6.setCaption("Amount");
        xDecimalField6.setName("entity.amount"); // NOI18N
        xDecimalField6.setPreferredSize(new java.awt.Dimension(150, 20));
        xDecimalField6.setRequired(true);
        xFormPanel4.add(xDecimalField6);

        xComboBox1.setAllowNull(false);
        xComboBox1.setCaption("Option");
        xComboBox1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xComboBox1.setImmediate(true);
        xComboBox1.setItems("optionList");
        xComboBox1.setName("entity.payoption"); // NOI18N
        xComboBox1.setPreferredSize(new java.awt.Dimension(150, 20));
        xComboBox1.setRequired(true);
        xFormPanel4.add(xComboBox1);

        xComboBox4.setCaption("Bank");
        xComboBox4.setDepends(new String[] {"entity.payoption"});
        xComboBox4.setDynamic(true);
        xComboBox4.setExpression("#{item.name}");
        xComboBox4.setItems("bankList");
        xComboBox4.setName("entity.bank"); // NOI18N
        xComboBox4.setPreferredSize(new java.awt.Dimension(200, 20));
        xComboBox4.setRequired(true);
        xFormPanel5.add(xComboBox4);

        xDateField6.setCaption("Check Date");
        xDateField6.setDepends(new String[] {"entity.payoption"});
        xDateField6.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDateField6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xDateField6.setName("entity.check.date"); // NOI18N
        xDateField6.setOutputFormat("MMM-dd-yyyy");
        xDateField6.setRequired(true);
        xFormPanel5.add(xDateField6);

        xTextField3.setCaption("Check No.");
        xTextField3.setDepends(new String[] {"entity.payoption"});
        xTextField3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xTextField3.setName("entity.check.no"); // NOI18N
        xTextField3.setPreferredSize(new java.awt.Dimension(150, 20));
        xTextField3.setRequired(true);
        xFormPanel5.add(xTextField3);

        xTextField4.setCaption("Paid By");
        xTextField4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xTextField4.setName("entity.paidby"); // NOI18N
        xTextField4.setPreferredSize(new java.awt.Dimension(270, 20));
        xTextField4.setRequired(true);
        xFormPanel5.add(xTextField4);

        javax.swing.GroupLayout xPanel1Layout = new javax.swing.GroupLayout(xPanel1);
        xPanel1.setLayout(xPanel1Layout);
        xPanel1Layout.setHorizontalGroup(
            xPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(xFormPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 327, Short.MAX_VALUE)
                .addContainerGap())
        );
        xPanel1Layout.setVerticalGroup(
            xPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(xPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(xFormPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
                    .addComponent(xFormPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        com.rameses.rcp.control.border.XTitledBorder xTitledBorder3 = new com.rameses.rcp.control.border.XTitledBorder();
        xTitledBorder3.setTitle("Borrower Information");
        xPanel2.setBorder(xTitledBorder3);

        com.rameses.rcp.control.border.XLineBorder xLineBorder3 = new com.rameses.rcp.control.border.XLineBorder();
        xLineBorder3.setLineColor(new java.awt.Color(180, 180, 180));
        xLabel1.setBorder(xLineBorder3);
        xLabel1.setCaption("App. No.");
        xLabel1.setExpression("#{entity.loanapp.appno}");
        xLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel1.setPreferredSize(new java.awt.Dimension(150, 20));
        xFormPanel1.add(xLabel1);

        com.rameses.rcp.control.border.XLineBorder xLineBorder4 = new com.rameses.rcp.control.border.XLineBorder();
        xLineBorder4.setLineColor(new java.awt.Color(180, 180, 180));
        xLabel2.setBorder(xLineBorder4);
        xLabel2.setCaption("Borrower");
        xLabel2.setExpression("#{entity.borrower.name}");
        xLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel2.setPreferredSize(new java.awt.Dimension(300, 20));
        xFormPanel1.add(xLabel2);

        com.rameses.rcp.control.border.XLineBorder xLineBorder5 = new com.rameses.rcp.control.border.XLineBorder();
        xLineBorder5.setLineColor(new java.awt.Color(180, 180, 180));
        xLabel3.setBorder(xLineBorder5);
        xLabel3.setCaption("Address");
        xLabel3.setExpression("#{entity.borrower.address}");
        xLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel3.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel1.add(xLabel3);

        com.rameses.rcp.control.border.XLineBorder xLineBorder6 = new com.rameses.rcp.control.border.XLineBorder();
        xLineBorder6.setLineColor(new java.awt.Color(180, 180, 180));
        xLabel4.setBorder(xLineBorder6);
        xLabel4.setCaption("Route");
        xLabel4.setExpression("#{entity.route.description} - #{entity.route.area}");
        xLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel4.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel1.add(xLabel4);

        javax.swing.GroupLayout xPanel2Layout = new javax.swing.GroupLayout(xPanel2);
        xPanel2.setLayout(xPanel2Layout);
        xPanel2Layout.setHorizontalGroup(
            xPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        xPanel2Layout.setVerticalGroup(
            xPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(xPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(xPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(xPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private com.rameses.rcp.control.XComboBox xComboBox1;
    private com.rameses.rcp.control.XComboBox xComboBox4;
    private com.rameses.rcp.control.XDateField xDateField1;
    private com.rameses.rcp.control.XDateField xDateField2;
    private com.rameses.rcp.control.XDateField xDateField3;
    private com.rameses.rcp.control.XDateField xDateField6;
    private com.rameses.rcp.control.XDecimalField xDecimalField1;
    private com.rameses.rcp.control.XDecimalField xDecimalField2;
    private com.rameses.rcp.control.XDecimalField xDecimalField3;
    private com.rameses.rcp.control.XDecimalField xDecimalField4;
    private com.rameses.rcp.control.XDecimalField xDecimalField5;
    private com.rameses.rcp.control.XDecimalField xDecimalField6;
    private com.rameses.rcp.control.XDecimalField xDecimalField7;
    private com.rameses.rcp.control.XDecimalField xDecimalField8;
    private com.rameses.rcp.control.XFormPanel xFormPanel1;
    private com.rameses.rcp.control.XFormPanel xFormPanel2;
    private com.rameses.rcp.control.XFormPanel xFormPanel3;
    private com.rameses.rcp.control.XFormPanel xFormPanel4;
    private com.rameses.rcp.control.XFormPanel xFormPanel5;
    private com.rameses.rcp.control.XLabel xLabel1;
    private com.rameses.rcp.control.XLabel xLabel2;
    private com.rameses.rcp.control.XLabel xLabel3;
    private com.rameses.rcp.control.XLabel xLabel4;
    private com.rameses.rcp.control.XLabel xLabel5;
    private com.rameses.rcp.control.XLabel xLabel6;
    private com.rameses.rcp.control.XPanel xPanel1;
    private com.rameses.rcp.control.XPanel xPanel2;
    private com.rameses.rcp.control.XTextField xTextField3;
    private com.rameses.rcp.control.XTextField xTextField4;
    // End of variables declaration//GEN-END:variables
    
}
