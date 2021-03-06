/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.views;

/**
 *
 * @author rameses
 */
public class TestAttachmentPage extends javax.swing.JPanel {

    /**
     * Creates new form TestAttachmentPage
     */
    public TestAttachmentPage() {
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

        fileViewPanel1 = new com.rameses.filemgmt.components.FileViewPanel();
        xFormPanel1 = new com.rameses.rcp.control.XFormPanel();
        xCheckBox1 = new com.rameses.rcp.control.XCheckBox();
        xCheckBox2 = new com.rameses.rcp.control.XCheckBox();
        xCheckBox3 = new com.rameses.rcp.control.XCheckBox();

        fileViewPanel1.setAllowAddWhen("#{allowadd == true}");
        fileViewPanel1.setAllowRemoveWhen("#{allowremove == true}");
        fileViewPanel1.setDepends(new String[] {"editable", "allowadd", "allowremove"});
        fileViewPanel1.setDividerLocation(200);
        fileViewPanel1.setEditableWhen("#{editable == true}");
        fileViewPanel1.setItems("attachments");

        xFormPanel1.setCaptionBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        xFormPanel1.setCaptionVAlignment(com.rameses.rcp.constant.UIConstants.CENTER);
        xFormPanel1.setOrientation(com.rameses.rcp.constant.UIConstants.HORIZONTAL);

        xCheckBox1.setCaption("Editable");
        xCheckBox1.setCaptionWidth(55);
        xCheckBox1.setName("editable"); // NOI18N
        xFormPanel1.add(xCheckBox1);

        xCheckBox2.setCaption("Allow Add");
        xCheckBox2.setCaptionWidth(60);
        xCheckBox2.setCellPadding(new java.awt.Insets(0, 10, 0, 0));
        xCheckBox2.setName("allowadd"); // NOI18N
        xFormPanel1.add(xCheckBox2);

        xCheckBox3.setCaption("Allow Remove");
        xCheckBox3.setCaptionWidth(80);
        xCheckBox3.setCellPadding(new java.awt.Insets(0, 10, 0, 0));
        xCheckBox3.setName("allowremove"); // NOI18N
        xFormPanel1.add(xCheckBox3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(xFormPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(fileViewPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fileViewPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.filemgmt.components.FileViewPanel fileViewPanel1;
    private com.rameses.rcp.control.XCheckBox xCheckBox1;
    private com.rameses.rcp.control.XCheckBox xCheckBox2;
    private com.rameses.rcp.control.XCheckBox xCheckBox3;
    private com.rameses.rcp.control.XFormPanel xFormPanel1;
    // End of variables declaration//GEN-END:variables
}
