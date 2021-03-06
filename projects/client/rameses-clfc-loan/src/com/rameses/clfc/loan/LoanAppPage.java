/*
 * LoanAppPage.java
 *
 * Created on September 9, 2013, 3:33 PM
 */

package com.rameses.clfc.loan;

import com.rameses.rcp.ui.annotations.StyleSheet;

/**
 *
 * @author  wflores
 */
@StyleSheet
public class LoanAppPage extends javax.swing.JPanel {
    
    public LoanAppPage() {
        initComponents();
        xlist.setFixedCellHeight(30); 
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        xLabel1 = new com.rameses.rcp.control.XLabel();
        jPanel2 = new javax.swing.JPanel();
        xActionBar1 = new com.rameses.rcp.control.XActionBar();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        xlist = new com.rameses.rcp.control.XList();
        jPanel5 = new javax.swing.JPanel();
        xSubFormPanel1 = new com.rameses.rcp.control.XSubFormPanel();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        com.rameses.rcp.control.border.XEtchedBorder xEtchedBorder1 = new com.rameses.rcp.control.border.XEtchedBorder();
        xEtchedBorder1.setHideLeft(true);
        xEtchedBorder1.setHideRight(true);
        xEtchedBorder1.setHideTop(true);
        jPanel1.setBorder(xEtchedBorder1);
        jPanel1.setMinimumSize(new java.awt.Dimension(162, 50));
        jPanel1.setLayout(new java.awt.BorderLayout());

        xLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        xLabel1.setAntiAliasOn(true);
        xLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        xLabel1.setDepends(new String[] {"selectedMenu"});
        xLabel1.setExpression("#{title}");
        xLabel1.setName("title"); // NOI18N
        jPanel1.add(xLabel1, java.awt.BorderLayout.WEST);

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 20, 5, 5));
        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.BorderLayout());

        xActionBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        xActionBar1.setDepends(new String[] {"selectedMenu"});
        xActionBar1.setFormName("loanapp");
        xActionBar1.setName("formActions"); // NOI18N
        xActionBar1.setOpaque(false);
        xActionBar1.setUseToolBar(false);
        jPanel2.add(xActionBar1, java.awt.BorderLayout.NORTH);

        jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 2));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setPreferredSize(new java.awt.Dimension(150, 130));

        xlist.setName("selectedMenu"); // NOI18N
        xlist.setDynamic(true);
        xlist.setExpression("#{item.caption}");
        xlist.setHandler("listHandler");
        xlist.setPadding(new java.awt.Insets(3, 10, 3, 5));
        jScrollPane1.setViewportView(xlist);

        jPanel4.add(jScrollPane1, java.awt.BorderLayout.WEST);

        jPanel3.add(jPanel4, java.awt.BorderLayout.WEST);

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        jPanel5.setLayout(new java.awt.BorderLayout());

        xSubFormPanel1.setDynamic(true);
        xSubFormPanel1.setHandler("opener");
        jPanel5.add(xSubFormPanel1, java.awt.BorderLayout.CENTER);

        jPanel3.add(jPanel5, java.awt.BorderLayout.CENTER);

        add(jPanel3, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private com.rameses.rcp.control.XActionBar xActionBar1;
    private com.rameses.rcp.control.XLabel xLabel1;
    private com.rameses.rcp.control.XSubFormPanel xSubFormPanel1;
    private com.rameses.rcp.control.XList xlist;
    // End of variables declaration//GEN-END:variables
    
}
