/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.clfc.followup.result;

import com.rameses.rcp.ui.annotations.StyleSheet;

/**
 *
 * @author louie
 */

@StyleSheet
public class AmnestyAvailPage extends javax.swing.JPanel {

    /**
     * Creates new form AmnestyAvailPage
     */
    public AmnestyAvailPage() {
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
        xFormPanel2 = new com.rameses.rcp.control.XFormPanel();
        xLookupField1 = new com.rameses.rcp.control.XLookupField();
        xFormPanel3 = new com.rameses.rcp.control.XFormPanel();
        xDateField3 = new com.rameses.rcp.control.XDateField();
        xDateField4 = new com.rameses.rcp.control.XDateField();
        xLabel2 = new com.rameses.rcp.control.XLabel();
        xButton1 = new com.rameses.rcp.control.XButton();

        setLayout(new java.awt.BorderLayout());

        com.rameses.rcp.control.border.XTitledBorder xTitledBorder1 = new com.rameses.rcp.control.border.XTitledBorder();
        xTitledBorder1.setTitle("Avail Amnesty");
        xPanel2.setBorder(xTitledBorder1);

        xFormPanel2.setPadding(new java.awt.Insets(0, 5, 0, 5));

        xLookupField1.setCaption("Ref. No.");
        xLookupField1.setCaptionWidth(90);
        xLookupField1.setDisableWhen("#{mode=='read'}");
        xLookupField1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xLookupField1.setExpression("${item.refno}");
        xLookupField1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLookupField1.setHandler("lookupAmnesty");
        xLookupField1.setName("entity.availedamnesty"); // NOI18N
        xLookupField1.setPreferredSize(new java.awt.Dimension(200, 20));
        xLookupField1.setRequired(true);
        xFormPanel2.add(xLookupField1);

        xFormPanel3.setCellspacing(5);

        xDateField3.setCaption("Effectivity Date");
        xDateField3.setCaptionWidth(90);
        xDateField3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xDateField3.setName("entity.availedamnesty.dtstarted"); // NOI18N
        xDateField3.setOutputFormat("MMM-dd-yyyy");
        xFormPanel3.add(xDateField3);

        xDateField4.setCaption("Maturity Date");
        xDateField4.setCaptionWidth(90);
        xDateField4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xDateField4.setName("entity.availedamnesty.dtended"); // NOI18N
        xDateField4.setOutputFormat("MMM-dd-yyyy");
        xDateField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xDateField4ActionPerformed(evt);
            }
        });
        xFormPanel3.add(xDateField4);

        xLabel2.setCaption("Description");
        xLabel2.setCaptionWidth(90);
        xLabel2.setExpression("${entity.availedamnesty.description}");
        xLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xLabel2.setPreferredSize(new java.awt.Dimension(0, 20));
        xFormPanel3.add(xLabel2);

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
                        .addComponent(xFormPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(xButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 34, Short.MAX_VALUE))
                    .addComponent(xFormPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        xPanel2Layout.setVerticalGroup(
            xPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(xPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(xFormPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(xButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xFormPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                .addGap(14, 14, 14))
        );

        add(xPanel2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void xDateField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xDateField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_xDateField4ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.rcp.control.XButton xButton1;
    private com.rameses.rcp.control.XDateField xDateField3;
    private com.rameses.rcp.control.XDateField xDateField4;
    private com.rameses.rcp.control.XFormPanel xFormPanel2;
    private com.rameses.rcp.control.XFormPanel xFormPanel3;
    private com.rameses.rcp.control.XLabel xLabel2;
    private com.rameses.rcp.control.XLookupField xLookupField1;
    private com.rameses.rcp.control.XPanel xPanel2;
    // End of variables declaration//GEN-END:variables
}
