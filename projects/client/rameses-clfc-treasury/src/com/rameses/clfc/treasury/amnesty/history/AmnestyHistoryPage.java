/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.clfc.treasury.amnesty.history;

import com.rameses.osiris2.themes.FormPage;
import com.rameses.rcp.ui.annotations.StyleSheet;
import com.rameses.rcp.ui.annotations.Template;

/**
 *
 * @author louie
 */
@StyleSheet
@Template(FormPage.class)
public class AmnestyHistoryPage extends javax.swing.JPanel {

    /**
     * Creates new form AmnestyPage
     */
    public AmnestyHistoryPage() {
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

        xPanel2 = new com.rameses.rcp.control.XPanel();
        xFormPanel3 = new com.rameses.rcp.control.XFormPanel();
        xDateField1 = new com.rameses.rcp.control.XDateField();
        xLabel3 = new com.rameses.rcp.control.XLabel();
        xLabel5 = new com.rameses.rcp.control.XLabel();
        xLabel1 = new com.rameses.rcp.control.XLabel();
        xDateField2 = new com.rameses.rcp.control.XDateField();
        xDateField3 = new com.rameses.rcp.control.XDateField();
        xLabel2 = new com.rameses.rcp.control.XLabel();
        xLabel4 = new com.rameses.rcp.control.XLabel();
        xLabel6 = new com.rameses.rcp.control.XLabel();
        xDateField4 = new com.rameses.rcp.control.XDateField();
        xLabel7 = new com.rameses.rcp.control.XLabel();
        xSubFormPanel1 = new com.rameses.rcp.control.XSubFormPanel();

        com.rameses.rcp.control.border.XTitledBorder xTitledBorder1 = new com.rameses.rcp.control.border.XTitledBorder();
        xTitledBorder1.setTitle("General Information");
        xPanel2.setBorder(xTitledBorder1);

        xFormPanel3.setCaptionWidth(90);
        xFormPanel3.setPadding(new java.awt.Insets(5, 0, 5, 5));

        xDateField1.setCaption("Date");
        xDateField1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDateField1.setEnabled(false);
        xDateField1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xDateField1.setName("entity.txndate"); // NOI18N
        xDateField1.setOutputFormat("MMM-dd-yyyy");
        xFormPanel3.add(xDateField1);

        xLabel3.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        xLabel3.setCaption("Request No.");
        xLabel3.setExpression("#{entity.refno}");
        xLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel3.setPreferredSize(new java.awt.Dimension(200, 20));
        xFormPanel3.add(xLabel3);

        xLabel5.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        xLabel5.setCaption("Borrower");
        xLabel5.setExpression("#{entity.borrower.name}");
        xLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel5.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel3.add(xLabel5);

        xLabel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        xLabel1.setCaption("Address");
        xLabel1.setDepends(new String[] {"borrower"});
        xLabel1.setExpression("#{entity.borrower.address}");
        xLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel1.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel3.add(xLabel1);

        xDateField2.setCaption("Start Date");
        xDateField2.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDateField2.setEnabled(false);
        xDateField2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xDateField2.setName("entity.dtstarted"); // NOI18N
        xDateField2.setOutputFormat("MMM-dd-yyyy");
        xDateField2.setPreferredSize(new java.awt.Dimension(130, 20));
        xFormPanel3.add(xDateField2);

        xDateField3.setCaption("End Date");
        xDateField3.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDateField3.setEnabled(false);
        xDateField3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xDateField3.setName("entity.dtended"); // NOI18N
        xDateField3.setOutputFormat("MMM-dd-yyyy");
        xDateField3.setPreferredSize(new java.awt.Dimension(130, 20));
        xFormPanel3.add(xDateField3);

        xLabel2.setCaption("Status");
        xLabel2.setExpression("#{entity.txnstate}");
        xLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        xLabel2.setForeground(new java.awt.Color(0, 0, 203));
        xLabel2.setPreferredSize(new java.awt.Dimension(121, 20));
        xFormPanel3.add(xLabel2);

        xLabel4.setCaption("Txntype");
        xLabel4.setExpression("#{entity.txntype}");
        xLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        xLabel4.setForeground(new java.awt.Color(0, 0, 203));
        xLabel4.setPreferredSize(new java.awt.Dimension(104, 20));
        xFormPanel3.add(xLabel4);

        xLabel6.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        xLabel6.setCaption("Option");
        xLabel6.setExpression("#{entity.amnestyoption}");
        xLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel6.setPreferredSize(new java.awt.Dimension(200, 20));
        xFormPanel3.add(xLabel6);

        xDateField4.setCaption("Date Amended");
        xDateField4.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDateField4.setEnabled(false);
        xDateField4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xDateField4.setName("entity.dtamended"); // NOI18N
        xDateField4.setOutputFormat("MMM-dd-yyyy");
        xFormPanel3.add(xDateField4);

        xLabel7.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        xLabel7.setCaption("Amended By");
        xLabel7.setExpression("#{entity.amendedby.name}");
        xLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel7.setPreferredSize(new java.awt.Dimension(300, 20));
        xFormPanel3.add(xLabel7);

        org.jdesktop.layout.GroupLayout xPanel2Layout = new org.jdesktop.layout.GroupLayout(xPanel2);
        xPanel2.setLayout(xPanel2Layout);
        xPanel2Layout.setHorizontalGroup(
            xPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(xPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(xFormPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                .addContainerGap())
        );
        xPanel2Layout.setVerticalGroup(
            xPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(xPanel2Layout.createSequentialGroup()
                .add(xFormPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                .addContainerGap())
        );

        xSubFormPanel1.setDepends(new String[] {"option"});
        xSubFormPanel1.setDynamic(true);
        xSubFormPanel1.setHandler("opener");

        org.jdesktop.layout.GroupLayout xSubFormPanel1Layout = new org.jdesktop.layout.GroupLayout(xSubFormPanel1);
        xSubFormPanel1.setLayout(xSubFormPanel1Layout);
        xSubFormPanel1Layout.setHorizontalGroup(
            xSubFormPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 477, Short.MAX_VALUE)
        );
        xSubFormPanel1Layout.setVerticalGroup(
            xSubFormPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 108, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(xPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(xSubFormPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(xPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(xSubFormPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.rcp.control.XDateField xDateField1;
    private com.rameses.rcp.control.XDateField xDateField2;
    private com.rameses.rcp.control.XDateField xDateField3;
    private com.rameses.rcp.control.XDateField xDateField4;
    private com.rameses.rcp.control.XFormPanel xFormPanel3;
    private com.rameses.rcp.control.XLabel xLabel1;
    private com.rameses.rcp.control.XLabel xLabel2;
    private com.rameses.rcp.control.XLabel xLabel3;
    private com.rameses.rcp.control.XLabel xLabel4;
    private com.rameses.rcp.control.XLabel xLabel5;
    private com.rameses.rcp.control.XLabel xLabel6;
    private com.rameses.rcp.control.XLabel xLabel7;
    private com.rameses.rcp.control.XPanel xPanel2;
    private com.rameses.rcp.control.XSubFormPanel xSubFormPanel1;
    // End of variables declaration//GEN-END:variables
}