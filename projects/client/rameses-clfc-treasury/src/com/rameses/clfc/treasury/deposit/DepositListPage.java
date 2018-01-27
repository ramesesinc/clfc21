/*
 * DepositListPage.java
 *
 * Created on June 24, 2014, 11:03 AM
 */

package com.rameses.clfc.treasury.deposit;

import com.rameses.osiris2.themes.ListPage;
import com.rameses.rcp.ui.annotations.StyleSheet;
import com.rameses.rcp.ui.annotations.Template;

/**
 *
 * @author  louie
 */
@StyleSheet
@Template(value=ListPage.class, target="sidebar")
public class DepositListPage extends javax.swing.JPanel 
{
    
    public DepositListPage() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        xList1 = new com.rameses.rcp.control.XList();

        jScrollPane1.setPreferredSize(new java.awt.Dimension(170, 130));

        xList1.setExpression("#{item.caption}");
        xList1.setHandler("optionsModel");
        xList1.setName("selectedOption"); // NOI18N
        xList1.setPadding(new java.awt.Insets(3, 10, 3, 5));
        jScrollPane1.setViewportView(xList1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private com.rameses.rcp.control.XList xList1;
    // End of variables declaration//GEN-END:variables
    
}