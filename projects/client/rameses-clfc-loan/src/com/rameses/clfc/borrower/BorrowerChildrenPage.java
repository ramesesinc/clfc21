/*
 * LoanAppBusinessPage.java
 *
 * Created on September 10, 2013, 6:11 PM
 */

package com.rameses.clfc.borrower;

import com.rameses.rcp.ui.annotations.StyleSheet;

/**
 *
 * @author  wflores
 */
@StyleSheet
public class BorrowerChildrenPage extends javax.swing.JPanel {
    
    public BorrowerChildrenPage() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        xEtchedBorder1 = new com.rameses.rcp.control.border.XEtchedBorder();
        xSplitView1 = new com.rameses.rcp.control.XSplitView();
        jPanel1 = new javax.swing.JPanel();
        xDataTable2 = new com.rameses.rcp.control.XDataTable();
        jPanel2 = new javax.swing.JPanel();
        xButton2 = new com.rameses.rcp.control.XButton();
        xButton1 = new com.rameses.rcp.control.XButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        xHtmlView1 = new com.rameses.rcp.control.XHtmlView();
        xLabel1 = new com.rameses.rcp.control.XLabel();

        xSplitView1.setDividerLocation(200);
        xSplitView1.setOrientation(com.rameses.rcp.constant.UIConstants.VERTICAL);

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 10));
        jPanel1.setLayout(new java.awt.BorderLayout());

        xDataTable2.setHandler("childrenHandler");
        xDataTable2.setName("selectedChild"); // NOI18N
        xDataTable2.setAutoResize(false);
        xDataTable2.setColumns(new com.rameses.rcp.common.Column[]{
            new com.rameses.rcp.common.Column(new Object[]{
                new Object[]{"name", "name"}
                , new Object[]{"caption", "Name"}
                , new Object[]{"width", 200}
                , new Object[]{"minWidth", 0}
                , new Object[]{"maxWidth", 0}
                , new Object[]{"required", false}
                , new Object[]{"resizable", true}
                , new Object[]{"nullWhenEmpty", true}
                , new Object[]{"editable", false}
                , new Object[]{"visible", true}
                , new Object[]{"visibleWhen", null}
                , new Object[]{"textCase", com.rameses.rcp.constant.TextCase.UPPER}
                , new Object[]{"typeHandler", new com.rameses.rcp.common.TextColumnHandler()}
            }),
            new com.rameses.rcp.common.Column(new Object[]{
                new Object[]{"name", "age"}
                , new Object[]{"caption", "Age"}
                , new Object[]{"width", 50}
                , new Object[]{"minWidth", 0}
                , new Object[]{"maxWidth", 0}
                , new Object[]{"required", false}
                , new Object[]{"resizable", true}
                , new Object[]{"nullWhenEmpty", true}
                , new Object[]{"editable", false}
                , new Object[]{"visible", true}
                , new Object[]{"visibleWhen", null}
                , new Object[]{"alignment", "RIGHT"}
                , new Object[]{"textCase", com.rameses.rcp.constant.TextCase.UPPER}
                , new Object[]{"typeHandler", new com.rameses.rcp.common.TextColumnHandler()}
            }),
            new com.rameses.rcp.common.Column(new Object[]{
                new Object[]{"name", "education"}
                , new Object[]{"caption", "Educational Attainment"}
                , new Object[]{"width", 200}
                , new Object[]{"minWidth", 0}
                , new Object[]{"maxWidth", 0}
                , new Object[]{"required", false}
                , new Object[]{"resizable", true}
                , new Object[]{"nullWhenEmpty", true}
                , new Object[]{"editable", false}
                , new Object[]{"visible", true}
                , new Object[]{"visibleWhen", null}
                , new Object[]{"textCase", com.rameses.rcp.constant.TextCase.UPPER}
                , new Object[]{"typeHandler", new com.rameses.rcp.common.TextColumnHandler()}
            }),
            new com.rameses.rcp.common.Column(new Object[]{
                new Object[]{"name", "remarks"}
                , new Object[]{"caption", "Remarks"}
                , new Object[]{"width", 250}
                , new Object[]{"minWidth", 0}
                , new Object[]{"maxWidth", 0}
                , new Object[]{"required", false}
                , new Object[]{"resizable", true}
                , new Object[]{"nullWhenEmpty", true}
                , new Object[]{"editable", false}
                , new Object[]{"visible", true}
                , new Object[]{"visibleWhen", null}
                , new Object[]{"textCase", com.rameses.rcp.constant.TextCase.UPPER}
                , new Object[]{"typeHandler", new com.rameses.rcp.common.TextColumnHandler()}
            })
        });
        jPanel1.add(xDataTable2, java.awt.BorderLayout.CENTER);

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 0, 5, 0));

        xButton2.setDepends(new String[] {"selectedChild"});
        xButton2.setDisableWhen("#{selectedChild == null || borrowerContext.mode=='read'}");
        xButton2.setName("removeChild"); // NOI18N
        xButton2.setText("Remove");

        xButton1.setName("addChild"); // NOI18N
        xButton1.setText("Add");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(xButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(517, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(xButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(xButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.add(jPanel2, java.awt.BorderLayout.NORTH);

        xSplitView1.add(jPanel1);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 10, 10));
        jPanel3.setLayout(new java.awt.BorderLayout());

        xHtmlView1.setDepends(new String[] {"selectedChild"});
        xHtmlView1.setName("htmlview"); // NOI18N
        jScrollPane1.setViewportView(xHtmlView1);

        jPanel3.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        xLabel1.setFontStyle("font-weight:bold;");
        xLabel1.setForeground(new java.awt.Color(80, 80, 80));
        xLabel1.setText("Quick Info");
        jPanel3.add(xLabel1, java.awt.BorderLayout.NORTH);

        xSplitView1.add(jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xSplitView1, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xSplitView1, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private com.rameses.rcp.control.XButton xButton1;
    private com.rameses.rcp.control.XButton xButton2;
    private com.rameses.rcp.control.XDataTable xDataTable2;
    private com.rameses.rcp.control.border.XEtchedBorder xEtchedBorder1;
    private com.rameses.rcp.control.XHtmlView xHtmlView1;
    private com.rameses.rcp.control.XLabel xLabel1;
    private com.rameses.rcp.control.XSplitView xSplitView1;
    // End of variables declaration//GEN-END:variables
    
}
