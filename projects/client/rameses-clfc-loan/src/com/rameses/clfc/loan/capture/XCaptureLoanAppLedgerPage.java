/*
 * LoanAppCaptureLedger.java
 *
 * Created on September 23, 2013, 12:43 PM
 */

package com.rameses.clfc.loan.capture;

import com.rameses.osiris2.themes.FormPage;
import com.rameses.rcp.ui.annotations.StyleSheet;
import com.rameses.rcp.ui.annotations.Template;

/**
 *
 * @author  louie
 */
@StyleSheet
@Template(FormPage.class)
public class XCaptureLoanAppLedgerPage extends javax.swing.JPanel {
    
    /** Creates new form LoanAppCaptureLedger */
    public XCaptureLoanAppLedgerPage() {
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
        xDataTable1 = new com.rameses.rcp.control.XDataTable();
        jPanel2 = new javax.swing.JPanel();
        xFormPanel1 = new com.rameses.rcp.control.XFormPanel();
        xLabel3 = new com.rameses.rcp.control.XLabel();
        xComboBox1 = new com.rameses.rcp.control.XComboBox();
        xDecimalField6 = new com.rameses.rcp.control.XDecimalField();
        xFormPanel2 = new com.rameses.rcp.control.XFormPanel();
        xLabel1 = new com.rameses.rcp.control.XLabel();
        xLabel2 = new com.rameses.rcp.control.XLabel();
        xDecimalField2 = new com.rameses.rcp.control.XDecimalField();
        xDateField3 = new com.rameses.rcp.control.XDateField();
        xDateField4 = new com.rameses.rcp.control.XDateField();

        com.rameses.rcp.control.border.XTitledBorder xTitledBorder1 = new com.rameses.rcp.control.border.XTitledBorder();
        xTitledBorder1.setTitle("Payments");
        jPanel1.setBorder(xTitledBorder1);

        xDataTable1.setColumns(new com.rameses.rcp.common.Column[]{
            new com.rameses.rcp.common.Column(new Object[]{
                new Object[]{"name", "refno"}
                , new Object[]{"caption", "Ref. No."}
                , new Object[]{"width", 200}
                , new Object[]{"minWidth", 0}
                , new Object[]{"maxWidth", 0}
                , new Object[]{"required", true}
                , new Object[]{"resizable", true}
                , new Object[]{"nullWhenEmpty", true}
                , new Object[]{"editable", true}
                , new Object[]{"editableWhen", null}
                , new Object[]{"textCase", com.rameses.rcp.constant.TextCase.UPPER}
                , new Object[]{"typeHandler", new com.rameses.rcp.common.TextColumnHandler()}
            }),
            new com.rameses.rcp.common.Column(new Object[]{
                new Object[]{"name", "txndate"}
                , new Object[]{"caption", "Date"}
                , new Object[]{"width", 200}
                , new Object[]{"minWidth", 0}
                , new Object[]{"maxWidth", 0}
                , new Object[]{"required", true}
                , new Object[]{"resizable", true}
                , new Object[]{"nullWhenEmpty", true}
                , new Object[]{"editable", true}
                , new Object[]{"editableWhen", null}
                , new Object[]{"textCase", com.rameses.rcp.constant.TextCase.UPPER}
                , new Object[]{"typeHandler", new com.rameses.rcp.common.DateColumnHandler("yyyy-MM-dd", "MMM-dd-yyyy", "yyyy-MM-dd")}
            }),
            new com.rameses.rcp.common.Column(new Object[]{
                new Object[]{"name", "amount"}
                , new Object[]{"caption", "Payment Amount"}
                , new Object[]{"width", 200}
                , new Object[]{"minWidth", 0}
                , new Object[]{"maxWidth", 0}
                , new Object[]{"required", true}
                , new Object[]{"resizable", true}
                , new Object[]{"nullWhenEmpty", true}
                , new Object[]{"editable", true}
                , new Object[]{"editableWhen", null}
                , new Object[]{"textCase", com.rameses.rcp.constant.TextCase.UPPER}
                , new Object[]{"typeHandler", new com.rameses.rcp.common.DecimalColumnHandler("#,##0.00", -1.0, -1.0, false, 2)}
            })
        });
        xDataTable1.setHandler("paymentsHandler");
        xDataTable1.setName("selectedPayment"); // NOI18N
        xDataTable1.setRequired(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xDataTable1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xDataTable1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
        );

        com.rameses.rcp.control.border.XTitledBorder xTitledBorder2 = new com.rameses.rcp.control.border.XTitledBorder();
        xTitledBorder2.setPadding(new java.awt.Insets(15, 10, 10, 10));
        xTitledBorder2.setTitle(" General Information ");
        jPanel2.setBorder(xTitledBorder2);

        xFormPanel1.setCaptionWidth(110);
        xFormPanel1.setPadding(new java.awt.Insets(5, 0, 0, 0));

        xLabel3.setCaption("Product Type");
        xLabel3.setExpression("${entity.producttypeid}");
        xLabel3.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel1.add(xLabel3);

        xComboBox1.setCaption("Payment Method");
        xComboBox1.setExpression("#{item.name}");
        xComboBox1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xComboBox1.setItemKey("value");
        xComboBox1.setItems("paymentTypes");
        xComboBox1.setName("entity.paymentmethod"); // NOI18N
        xComboBox1.setPreferredSize(new java.awt.Dimension(0, 20));
        xComboBox1.setRequired(true);
        xFormPanel1.add(xComboBox1);

        xDecimalField6.setCaption("Overpayment");
        xDecimalField6.setDepends(new String[] {"entity.paymentmethod"});
        xDecimalField6.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDecimalField6.setName("entity.overpayment"); // NOI18N
        xDecimalField6.setRequired(true);
        xFormPanel1.add(xDecimalField6);

        xFormPanel2.setPadding(new java.awt.Insets(5, 5, 0, 20));

        xLabel1.setBackground(new java.awt.Color(250, 250, 250));
        com.rameses.rcp.control.border.XLineBorder xLineBorder1 = new com.rameses.rcp.control.border.XLineBorder();
        xLineBorder1.setLineColor(new java.awt.Color(204, 204, 204));
        xLabel1.setBorder(xLineBorder1);
        xLabel1.setCaption("App. No.");
        xLabel1.setCaptionWidth(90);
        xLabel1.setExpression("#{entity.appno}");
        xLabel1.setFontStyle("font-size:12");
        xLabel1.setOpaque(true);
        xLabel1.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel2.add(xLabel1);

        xLabel2.setBackground(new java.awt.Color(250, 250, 250));
        com.rameses.rcp.control.border.XLineBorder xLineBorder2 = new com.rameses.rcp.control.border.XLineBorder();
        xLineBorder2.setLineColor(new java.awt.Color(204, 204, 204));
        xLabel2.setBorder(xLineBorder2);
        xLabel2.setCaption("Borrower");
        xLabel2.setCaptionWidth(90);
        xLabel2.setDepends(new String[] {"application"});
        xLabel2.setExpression("#{entity.borrower.name}");
        xLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel2.setOpaque(true);
        xLabel2.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel2.add(xLabel2);

        xDecimalField2.setCaption("Loan Amount");
        xDecimalField2.setCaptionWidth(90);
        xDecimalField2.setEnabled(false);
        xDecimalField2.setFontStyle("font-size:14;");
        xDecimalField2.setName("entity.loanamount"); // NOI18N
        xDecimalField2.setPreferredSize(new java.awt.Dimension(150, 20));
        xFormPanel2.add(xDecimalField2);

        xDateField3.setCaption("Date Released");
        xDateField3.setCaptionWidth(90);
        xDateField3.setEnabled(false);
        xDateField3.setFontStyle("font-size:12");
        xDateField3.setName("entity.dtreleased"); // NOI18N
        xDateField3.setOutputFormat("MMM-dd-yyyy");
        xDateField3.setPreferredSize(new java.awt.Dimension(150, 20));
        xFormPanel2.add(xDateField3);

        xDateField4.setCaption("Maturity Date");
        xDateField4.setCaptionWidth(90);
        xDateField4.setEnabled(false);
        xDateField4.setFontStyle("font-size:12");
        xDateField4.setName("entity.dtmatured"); // NOI18N
        xDateField4.setOutputFormat("MMM-dd-yyyy");
        xDateField4.setPreferredSize(new java.awt.Dimension(150, 20));
        xFormPanel2.add(xDateField4);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(xFormPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xFormPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(xFormPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                    .addComponent(xFormPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private com.rameses.rcp.control.XComboBox xComboBox1;
    private com.rameses.rcp.control.XDataTable xDataTable1;
    private com.rameses.rcp.control.XDateField xDateField3;
    private com.rameses.rcp.control.XDateField xDateField4;
    private com.rameses.rcp.control.XDecimalField xDecimalField2;
    private com.rameses.rcp.control.XDecimalField xDecimalField6;
    private com.rameses.rcp.control.XFormPanel xFormPanel1;
    private com.rameses.rcp.control.XFormPanel xFormPanel2;
    private com.rameses.rcp.control.XLabel xLabel1;
    private com.rameses.rcp.control.XLabel xLabel2;
    private com.rameses.rcp.control.XLabel xLabel3;
    // End of variables declaration//GEN-END:variables
    
}