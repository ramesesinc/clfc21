/*
 * DepositSlipPage.java
 *
 * Created on June 14, 2014, 11:00 AM
 */

package com.rameses.clfc.treasury.depositslip;

import com.rameses.osiris2.themes.FormPage;
import com.rameses.rcp.ui.annotations.StyleSheet;
import com.rameses.rcp.ui.annotations.Template;

/**
 *
 * @author  louie
 */

@StyleSheet
@Template(FormPage.class)
public class XXDepositSlipPage extends javax.swing.JPanel {
    
    /** Creates new form DepositSlipPage */
    public XXDepositSlipPage() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        xSubFormPanel1 = new com.rameses.rcp.control.XSubFormPanel();
        xTabbedPane1 = new com.rameses.rcp.control.XTabbedPane();
        xPanel1 = new com.rameses.rcp.control.XPanel();
        xFormPanel1 = new com.rameses.rcp.control.XFormPanel();
        xDateField1 = new com.rameses.rcp.control.XDateField();
        xTextField1 = new com.rameses.rcp.control.XTextField();
        xLookupField1 = new com.rameses.rcp.control.XLookupField();
        xLabel1 = new com.rameses.rcp.control.XLabel();
        xLabel2 = new com.rameses.rcp.control.XLabel();
        xLookupField2 = new com.rameses.rcp.control.XLookupField();
        xLookupField3 = new com.rameses.rcp.control.XLookupField();
        xLookupField4 = new com.rameses.rcp.control.XLookupField();
        xComboBox1 = new com.rameses.rcp.control.XComboBox();
        xDecimalField1 = new com.rameses.rcp.control.XDecimalField();
        xLabel3 = new com.rameses.rcp.control.XLabel();

        xSubFormPanel1.setDynamic(true);
        xSubFormPanel1.setHandler("cashbreakdown");

        javax.swing.GroupLayout xSubFormPanel1Layout = new javax.swing.GroupLayout(xSubFormPanel1);
        xSubFormPanel1.setLayout(xSubFormPanel1Layout);
        xSubFormPanel1Layout.setHorizontalGroup(
            xSubFormPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 375, Short.MAX_VALUE)
        );
        xSubFormPanel1Layout.setVerticalGroup(
            xSubFormPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 483, Short.MAX_VALUE)
        );

        xTabbedPane1.setDynamic(true);
        xTabbedPane1.setHandler("pluginList");

        com.rameses.rcp.control.border.XTitledBorder xTitledBorder1 = new com.rameses.rcp.control.border.XTitledBorder();
        xTitledBorder1.setTitle("General Information");
        xPanel1.setBorder(xTitledBorder1);

        xFormPanel1.setCaptionWidth(100);
        xFormPanel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        xDateField1.setCaption("Date");
        xDateField1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDateField1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xDateField1.setName("entity.txndate"); // NOI18N
        xDateField1.setOutputFormat("MMM-dd-yyyy");
        xDateField1.setRequired(true);
        xFormPanel1.add(xDateField1);

        xTextField1.setCaption("Control No.");
        xTextField1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xTextField1.setName("entity.controlno"); // NOI18N
        xTextField1.setPreferredSize(new java.awt.Dimension(300, 20));
        xTextField1.setRequired(true);
        xFormPanel1.add(xTextField1);

        xLookupField1.setCaption("Passbook No.");
        xLookupField1.setDisableWhen("#{mode=='read'}");
        xLookupField1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xLookupField1.setExpression("#{entity.passbook.passbookno}");
        xLookupField1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLookupField1.setHandler("passbookLookup");
        xLookupField1.setName("passbook"); // NOI18N
        xLookupField1.setPreferredSize(new java.awt.Dimension(200, 20));
        xLookupField1.setRequired(true);
        xFormPanel1.add(xLookupField1);

        xLabel1.setCaption("Account No.");
        xLabel1.setDepends(new String[] {"passbook"});
        xLabel1.setExpression("#{entity.passbook.acctno}");
        xLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel1.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel1.add(xLabel1);

        xLabel2.setCaption("Account Name");
        xLabel2.setDepends(new String[] {"passbook"});
        xLabel2.setExpression("#{entity.passbook.acctname}");
        xLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel2.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel1.add(xLabel2);

        xLookupField2.setCaption("Currency Type");
        xLookupField2.setDisableWhen("#{mode=='read'}");
        xLookupField2.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xLookupField2.setExpression("#{entity.currencytype.name}");
        xLookupField2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLookupField2.setHandler("currencyTypeLookup");
        xLookupField2.setName("currencytype"); // NOI18N
        xLookupField2.setPreferredSize(new java.awt.Dimension(250, 20));
        xFormPanel1.add(xLookupField2);

        xLookupField3.setCaption("Account Type");
        xLookupField3.setDisableWhen("#{mode=='read'}");
        xLookupField3.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xLookupField3.setExpression("#{entity.accounttype.name}");
        xLookupField3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLookupField3.setHandler("accountTypeLookup");
        xLookupField3.setName("accounttype"); // NOI18N
        xLookupField3.setPreferredSize(new java.awt.Dimension(250, 20));
        xFormPanel1.add(xLookupField3);

        xLookupField4.setCaption("Deposit Type");
        xLookupField4.setDisableWhen("#{mode=='read'}");
        xLookupField4.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xLookupField4.setExpression("#{entity.deposittype.name}");
        xLookupField4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLookupField4.setHandler("depositTypeLookup");
        xLookupField4.setName("deposittype"); // NOI18N
        xLookupField4.setPreferredSize(new java.awt.Dimension(250, 20));
        xFormPanel1.add(xLookupField4);

        xComboBox1.setAllowNull(false);
        xComboBox1.setCaption("Type");
        xComboBox1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xComboBox1.setItems("typeList");
        xComboBox1.setName("entity.type"); // NOI18N
        xComboBox1.setPreferredSize(new java.awt.Dimension(150, 20));
        xFormPanel1.add(xComboBox1);

        xDecimalField1.setCaption("Amount");
        xDecimalField1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDecimalField1.setEnabled(false);
        xDecimalField1.setName("entity.amount"); // NOI18N
        xDecimalField1.setPreferredSize(new java.awt.Dimension(150, 20));
        xDecimalField1.setReadonly(true);
        xDecimalField1.setRequired(true);
        xFormPanel1.add(xDecimalField1);

        javax.swing.GroupLayout xPanel1Layout = new javax.swing.GroupLayout(xPanel1);
        xPanel1.setLayout(xPanel1Layout);
        xPanel1Layout.setHorizontalGroup(
            xPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(100, Short.MAX_VALUE))
        );
        xPanel1Layout.setVerticalGroup(
            xPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                .addContainerGap())
        );

        xLabel3.setCaption("Status");
        xLabel3.setExpression("#{entity.state}");
        xLabel3.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        xLabel3.setForeground(new java.awt.Color(255, 0, 0));
        xLabel3.setPreferredSize(new java.awt.Dimension(300, 30));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(xTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(xLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(224, 224, 224))
                    .addComponent(xPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xSubFormPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(xLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(xPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(xTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(xSubFormPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 483, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.rcp.control.XComboBox xComboBox1;
    private com.rameses.rcp.control.XDateField xDateField1;
    private com.rameses.rcp.control.XDecimalField xDecimalField1;
    private com.rameses.rcp.control.XFormPanel xFormPanel1;
    private com.rameses.rcp.control.XLabel xLabel1;
    private com.rameses.rcp.control.XLabel xLabel2;
    private com.rameses.rcp.control.XLabel xLabel3;
    private com.rameses.rcp.control.XLookupField xLookupField1;
    private com.rameses.rcp.control.XLookupField xLookupField2;
    private com.rameses.rcp.control.XLookupField xLookupField3;
    private com.rameses.rcp.control.XLookupField xLookupField4;
    private com.rameses.rcp.control.XPanel xPanel1;
    private com.rameses.rcp.control.XSubFormPanel xSubFormPanel1;
    private com.rameses.rcp.control.XTabbedPane xTabbedPane1;
    private com.rameses.rcp.control.XTextField xTextField1;
    // End of variables declaration//GEN-END:variables
    
}
