/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.clfc.treasury.ledger.amnesty.fix;

import com.rameses.osiris2.themes.FormPage;
import com.rameses.rcp.ui.annotations.StyleSheet;
import com.rameses.rcp.ui.annotations.Template;

/**
 *
 * @author louie
 */

@StyleSheet
@Template(FormPage.class)
public class AmnestyFixPage extends javax.swing.JPanel {

    /**
     * Creates new form AmnestyFixPage
     */
    public AmnestyFixPage() {
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
        xFormPanel2 = new com.rameses.rcp.control.XFormPanel();
        xLabel1 = new com.rameses.rcp.control.XLabel();
        xLabel2 = new com.rameses.rcp.control.XLabel();
        xLabel3 = new com.rameses.rcp.control.XLabel();
        xDecimalField1 = new com.rameses.rcp.control.XDecimalField();
        xDateField1 = new com.rameses.rcp.control.XDateField();
        xDateField2 = new com.rameses.rcp.control.XDateField();
        xPanel2 = new com.rameses.rcp.control.XPanel();
        xFormPanel3 = new com.rameses.rcp.control.XFormPanel();
        xLookupField1 = new com.rameses.rcp.control.XLookupField();
        xFormPanel4 = new com.rameses.rcp.control.XFormPanel();
        xDateField3 = new com.rameses.rcp.control.XDateField();
        xDateField4 = new com.rameses.rcp.control.XDateField();
        xLabel4 = new com.rameses.rcp.control.XLabel();
        xButton1 = new com.rameses.rcp.control.XButton();
        xLabel5 = new com.rameses.rcp.control.XLabel();

        com.rameses.rcp.control.border.XTitledBorder xTitledBorder1 = new com.rameses.rcp.control.border.XTitledBorder();
        xTitledBorder1.setTitle("Application Information");
        xPanel1.setBorder(xTitledBorder1);

        xFormPanel2.setCellspacing(5);

        xLabel1.setCaption("App. No.");
        xLabel1.setCaptionWidth(90);
        xLabel1.setExpression("#{entity.loanapp.appno}");
        xLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel1.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel2.add(xLabel1);

        xLabel2.setCaption("Borrower");
        xLabel2.setCaptionWidth(90);
        xLabel2.setExpression("#{entity.borrower.name}");
        xLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel2.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel2.add(xLabel2);

        xLabel3.setCaption("Address");
        xLabel3.setCaptionWidth(90);
        xLabel3.setExpression("#{entity.borrower.address}");
        xLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel3.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel2.add(xLabel3);

        xDecimalField1.setCaption("Amount");
        xDecimalField1.setCaptionWidth(90);
        xDecimalField1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDecimalField1.setName("entity.loanapp.loanamount"); // NOI18N
        xFormPanel2.add(xDecimalField1);

        xDateField1.setCaption("Release Date");
        xDateField1.setCaptionWidth(90);
        xDateField1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDateField1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xDateField1.setName("entity.loanapp.dtreleased"); // NOI18N
        xDateField1.setOutputFormat("MMM-dd-yyyy");
        xFormPanel2.add(xDateField1);

        xDateField2.setCaption("Maturity Date");
        xDateField2.setCaptionWidth(90);
        xDateField2.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDateField2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xDateField2.setName("entity.loanapp.dtmatured"); // NOI18N
        xDateField2.setOutputFormat("MMM-dd-yyyy");
        xFormPanel2.add(xDateField2);

        javax.swing.GroupLayout xPanel1Layout = new javax.swing.GroupLayout(xPanel1);
        xPanel1.setLayout(xPanel1Layout);
        xPanel1Layout.setHorizontalGroup(
            xPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
                .addContainerGap())
        );
        xPanel1Layout.setVerticalGroup(
            xPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        com.rameses.rcp.control.border.XTitledBorder xTitledBorder2 = new com.rameses.rcp.control.border.XTitledBorder();
        xTitledBorder2.setTitle("Availed Amnesty");
        xPanel2.setBorder(xTitledBorder2);

        xFormPanel3.setPadding(new java.awt.Insets(0, 5, 0, 5));

        xLookupField1.setCaption("Ref. No.");
        xLookupField1.setCaptionWidth(90);
        xLookupField1.setDisableWhen("#{mode=='read'}");
        xLookupField1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xLookupField1.setExpression("#{entity.refno}");
        xLookupField1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLookupField1.setHandler("lookupAmnesty");
        xLookupField1.setName("amnesty"); // NOI18N
        xLookupField1.setPreferredSize(new java.awt.Dimension(200, 20));
        xLookupField1.setRequired(true);
        xFormPanel3.add(xLookupField1);

        xFormPanel4.setCellspacing(5);

        xDateField3.setCaption("Start Date");
        xDateField3.setCaptionWidth(90);
        xDateField3.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDateField3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xDateField3.setName("entity.dtstarted"); // NOI18N
        xDateField3.setOutputFormat("MMM-dd-yyyy");
        xFormPanel4.add(xDateField3);

        xDateField4.setCaption("Maturity Date");
        xDateField4.setCaptionWidth(90);
        xDateField4.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xDateField4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xDateField4.setName("entity.dtended"); // NOI18N
        xDateField4.setOutputFormat("MMM-dd-yyyy");
        xFormPanel4.add(xDateField4);

        xLabel4.setCaption("Description");
        xLabel4.setCaptionWidth(90);
        xLabel4.setExpression("#{entity.description}");
        xLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel4.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel4.add(xLabel4);

        xButton1.setDisableWhen("#{mode=='read'}");
        xButton1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xButton1.setName("removeAmnesty"); // NOI18N
        xButton1.setPreferredSize(new java.awt.Dimension(80, 23));
        xButton1.setText("Remove");

        javax.swing.GroupLayout xPanel2Layout = new javax.swing.GroupLayout(xPanel2);
        xPanel2.setLayout(xPanel2Layout);
        xPanel2Layout.setHorizontalGroup(
            xPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(xPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(xPanel2Layout.createSequentialGroup()
                        .addComponent(xFormPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(xButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 57, Short.MAX_VALUE))
                    .addComponent(xFormPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        xPanel2Layout.setVerticalGroup(
            xPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(xPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(xFormPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(xButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xFormPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );

        xLabel5.setExpression("${status}");
        xLabel5.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        xLabel5.setForeground(new java.awt.Color(255, 0, 0));
        xLabel5.setPreferredSize(new java.awt.Dimension(400, 30));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(xPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(xPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(xLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(xLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(xPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.rcp.control.XButton xButton1;
    private com.rameses.rcp.control.XDateField xDateField1;
    private com.rameses.rcp.control.XDateField xDateField2;
    private com.rameses.rcp.control.XDateField xDateField3;
    private com.rameses.rcp.control.XDateField xDateField4;
    private com.rameses.rcp.control.XDecimalField xDecimalField1;
    private com.rameses.rcp.control.XFormPanel xFormPanel2;
    private com.rameses.rcp.control.XFormPanel xFormPanel3;
    private com.rameses.rcp.control.XFormPanel xFormPanel4;
    private com.rameses.rcp.control.XLabel xLabel1;
    private com.rameses.rcp.control.XLabel xLabel2;
    private com.rameses.rcp.control.XLabel xLabel3;
    private com.rameses.rcp.control.XLabel xLabel4;
    private com.rameses.rcp.control.XLabel xLabel5;
    private com.rameses.rcp.control.XLookupField xLookupField1;
    private com.rameses.rcp.control.XPanel xPanel1;
    private com.rameses.rcp.control.XPanel xPanel2;
    // End of variables declaration//GEN-END:variables
}
