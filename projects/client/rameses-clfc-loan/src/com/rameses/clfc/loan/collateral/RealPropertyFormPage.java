/*
 * RealPropertyFormPage.java
 *
 * Created on September 2, 2013, 11:57 AM
 */

package com.rameses.clfc.loan.collateral;

import com.rameses.osiris2.themes.OKCancelPage;
import com.rameses.rcp.ui.annotations.StyleSheet;
import com.rameses.rcp.ui.annotations.Template;
import java.math.BigDecimal;

/**
 *
 * @author  Rameses
 */
@StyleSheet
@Template(OKCancelPage.class)
public class RealPropertyFormPage extends javax.swing.JPanel {
    
    /** Creates new form RealPropertyFormPage */
    public RealPropertyFormPage() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        xFormPanel1 = new com.rameses.rcp.control.XFormPanel();
        xComboBox1 = new com.rameses.rcp.control.XComboBox();
        xTextField1 = new com.rameses.rcp.control.XTextField();
        xFormPanel2 = new com.rameses.rcp.control.XFormPanel();
        xDecimalField3 = new com.rameses.rcp.control.XDecimalField();
        xComboBox2 = new com.rameses.rcp.control.XComboBox();
        xFormPanel3 = new com.rameses.rcp.control.XFormPanel();
        xDecimalField1 = new com.rameses.rcp.control.XDecimalField();
        xDateField1 = new com.rameses.rcp.control.XDateField();
        xComboBox3 = new com.rameses.rcp.control.XComboBox();
        xTextField3 = new com.rameses.rcp.control.XTextField();
        xTextField4 = new com.rameses.rcp.control.XTextField();
        xDecimalField2 = new com.rameses.rcp.control.XDecimalField();
        jScrollPane1 = new javax.swing.JScrollPane();
        xTextArea1 = new com.rameses.rcp.control.XTextArea();
        jPanel2 = new javax.swing.JPanel();
        fileViewPanel1 = new com.rameses.filemgmt.components.FileViewPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        xTextArea2 = new com.rameses.rcp.control.XTextArea();

        jPanel1.setLayout(null);

        xFormPanel1.setPadding(new java.awt.Insets(0, 0, 0, 0));

        xComboBox1.setCaption("Classification");
        xComboBox1.setCaptionWidth(140);
        xComboBox1.setExpression("#{item.value}");
        xComboBox1.setIndex(-10);
        xComboBox1.setItemKey("value");
        xComboBox1.setItems("classifications");
        xComboBox1.setName("entity.classification"); // NOI18N
        xComboBox1.setPreferredSize(new java.awt.Dimension(0, 22));
        xComboBox1.setRequired(true);
        xFormPanel1.add(xComboBox1);

        xTextField1.setCaption("Location");
        xTextField1.setCaptionWidth(140);
        xTextField1.setName("entity.location"); // NOI18N
        xTextField1.setPreferredSize(new java.awt.Dimension(0, 22));
        xTextField1.setRequired(true);
        xFormPanel1.add(xTextField1);

        jPanel1.add(xFormPanel1);
        xFormPanel1.setBounds(20, 20, 485, 50);

        xFormPanel2.setOrientation("HORIZONTAL");
        xFormPanel2.setPadding(new java.awt.Insets(2, 0, 0, 0));

        xDecimalField3.setName("entity.areavalue"); // NOI18N
        xDecimalField3.setCaption("Area");
        xDecimalField3.setCaptionWidth(140);
        xDecimalField3.setPreferredSize(new java.awt.Dimension(120, 20));
        xDecimalField3.setRequired(true);
        xFormPanel2.add(xDecimalField3);

        xComboBox2.setName("entity.areauom"); // NOI18N
        xComboBox2.setCaption("Unit of Measure");
        xComboBox2.setExpression("#{item.value}");
        xComboBox2.setItemKey("value");
        xComboBox2.setItems("uomList");
        xComboBox2.setRequired(true);
        xComboBox2.setShowCaption(false);
        xFormPanel2.add(xComboBox2);

        jPanel1.add(xFormPanel2);
        xFormPanel2.setBounds(20, 71, 485, 22);

        xFormPanel3.setPadding(new java.awt.Insets(0, 0, 0, 0));

        xDecimalField1.setName("entity.zonalvalue"); // NOI18N
        xDecimalField1.setCaption("Zonal Value");
        xDecimalField1.setCaptionWidth(140);
        xDecimalField1.setPreferredSize(new java.awt.Dimension(120, 20));
        xFormPanel3.add(xDecimalField1);

        xDateField1.setCaption("Date Acquired");
        xDateField1.setCaptionWidth(140);
        xDateField1.setName("entity.dtacquired"); // NOI18N
        xDateField1.setPreferredSize(new java.awt.Dimension(120, 20));
        xFormPanel3.add(xDateField1);

        xComboBox3.setCaption("Mode of Acquisition");
        xComboBox3.setExpression("#{item.value}");
        xComboBox3.setItemKey("value");
        xComboBox3.setItems("modesOfAcquisition");
        xComboBox3.setName("entity.modeofacquisition"); // NOI18N
        xComboBox3.setCaptionWidth(140);
        xComboBox3.setPreferredSize(new java.awt.Dimension(0, 22));
        xComboBox3.setRequired(true);
        xFormPanel3.add(xComboBox3);

        xTextField3.setCaption("Acquired From");
        xTextField3.setCaptionWidth(140);
        xTextField3.setName("entity.acquiredfrom"); // NOI18N
        xTextField3.setPreferredSize(new java.awt.Dimension(0, 20));
        xTextField3.setRequired(true);
        xFormPanel3.add(xTextField3);

        xTextField4.setCaption("Reigstered Name");
        xTextField4.setCaptionWidth(140);
        xTextField4.setName("entity.registeredname"); // NOI18N
        xTextField4.setPreferredSize(new java.awt.Dimension(0, 20));
        xTextField4.setRequired(true);
        xFormPanel3.add(xTextField4);

        xDecimalField2.setName("entity.marketvalue"); // NOI18N
        xDecimalField2.setCaption("Market/Appraisal Value");
        xDecimalField2.setCaptionWidth(140);
        xDecimalField2.setPreferredSize(new java.awt.Dimension(120, 20));
        xFormPanel3.add(xDecimalField2);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(0, 80));

        xTextArea1.setName("entity.remarks"); // NOI18N
        xTextArea1.setCaption("Remarks");
        xTextArea1.setCaptionWidth(140);
        xTextArea1.setHint("Specify remarks here");
        xTextArea1.setTextCase(com.rameses.rcp.constant.TextCase.UPPER);
        jScrollPane1.setViewportView(xTextArea1);

        xFormPanel3.add(jScrollPane1);

        jPanel1.add(xFormPanel3);
        xFormPanel3.setBounds(20, 100, 485, 220);

        jTabbedPane1.addTab("  General Information   ", jPanel1);

        fileViewPanel1.setDividerLocation(120);
        fileViewPanel1.setEditableWhen("#{mode != 'read'}");
        fileViewPanel1.setItems("entity.attachments");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fileViewPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fileViewPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("  Attachments   ", jPanel2);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel3.setLayout(new java.awt.BorderLayout());

        xTextArea2.setName("entity.ci.evaluation"); // NOI18N
        jScrollPane2.setViewportView(xTextArea2);

        jPanel3.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("   CI Report   ", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.filemgmt.components.FileViewPanel fileViewPanel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private com.rameses.rcp.control.XComboBox xComboBox1;
    private com.rameses.rcp.control.XComboBox xComboBox2;
    private com.rameses.rcp.control.XComboBox xComboBox3;
    private com.rameses.rcp.control.XDateField xDateField1;
    private com.rameses.rcp.control.XDecimalField xDecimalField1;
    private com.rameses.rcp.control.XDecimalField xDecimalField2;
    private com.rameses.rcp.control.XDecimalField xDecimalField3;
    private com.rameses.rcp.control.XFormPanel xFormPanel1;
    private com.rameses.rcp.control.XFormPanel xFormPanel2;
    private com.rameses.rcp.control.XFormPanel xFormPanel3;
    private com.rameses.rcp.control.XTextArea xTextArea1;
    private com.rameses.rcp.control.XTextArea xTextArea2;
    private com.rameses.rcp.control.XTextField xTextField1;
    private com.rameses.rcp.control.XTextField xTextField3;
    private com.rameses.rcp.control.XTextField xTextField4;
    // End of variables declaration//GEN-END:variables
    
}
