/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.clfc.posting.header2;

import com.rameses.osiris2.themes.OKCancelPage;
import com.rameses.rcp.ui.annotations.StyleSheet;
import com.rameses.rcp.ui.annotations.Template;

/**
 *
 * @author louie
 */

@StyleSheet
@Template(OKCancelPage.class)
public class FieldUpdatePage extends javax.swing.JPanel {

    /**
     * Creates new form FieldUpdatePage
     */
    public FieldUpdatePage() {
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

        xFormPanel3 = new com.rameses.rcp.control.XFormPanel();
        xTextField3 = new com.rameses.rcp.control.XTextField();
        xTextField4 = new com.rameses.rcp.control.XTextField();
        xTextField7 = new com.rameses.rcp.control.XTextField();
        xTextField8 = new com.rameses.rcp.control.XTextField();
        xTextField5 = new com.rameses.rcp.control.XTextField();

        xFormPanel3.setCaptionWidth(100);

        xTextField3.setCaption("Schema");
        xTextField3.setName("entity.schemaname"); // NOI18N
        xTextField3.setPreferredSize(new java.awt.Dimension(150, 20));
        xTextField3.setRequired(true);
        xTextField3.setTextCase(com.rameses.rcp.constant.TextCase.LOWER);
        xFormPanel3.add(xTextField3);

        xTextField4.setCaption("Sub Schema");
        xTextField4.setName("entity.subschemaname"); // NOI18N
        xTextField4.setPreferredSize(new java.awt.Dimension(150, 20));
        xTextField4.setTextCase(com.rameses.rcp.constant.TextCase.LOWER);
        xFormPanel3.add(xTextField4);

        xTextField7.setCaption("Primary Key");
        xTextField7.setName("entity.primarykey"); // NOI18N
        xTextField7.setPreferredSize(new java.awt.Dimension(150, 20));
        xTextField7.setRequired(true);
        xTextField7.setTextCase(com.rameses.rcp.constant.TextCase.LOWER);
        xFormPanel3.add(xTextField7);

        xTextField8.setCaption("Parameter Key");
        xTextField8.setName("entity.paramkey"); // NOI18N
        xTextField8.setPreferredSize(new java.awt.Dimension(150, 20));
        xTextField8.setRequired(true);
        xTextField8.setTextCase(com.rameses.rcp.constant.TextCase.LOWER);
        xFormPanel3.add(xTextField8);

        xTextField5.setCaption("Field Name");
        xTextField5.setName("entity.fieldname"); // NOI18N
        xTextField5.setPreferredSize(new java.awt.Dimension(150, 20));
        xTextField5.setRequired(true);
        xTextField5.setTextCase(com.rameses.rcp.constant.TextCase.LOWER);
        xFormPanel3.add(xTextField5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.rcp.control.XFormPanel xFormPanel3;
    private com.rameses.rcp.control.XTextField xTextField3;
    private com.rameses.rcp.control.XTextField xTextField4;
    private com.rameses.rcp.control.XTextField xTextField5;
    private com.rameses.rcp.control.XTextField xTextField7;
    private com.rameses.rcp.control.XTextField xTextField8;
    // End of variables declaration//GEN-END:variables
}
