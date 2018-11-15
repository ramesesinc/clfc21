/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rameses.clfc.posting.header2;

import com.rameses.osiris2.themes.FormPage;
import com.rameses.rcp.ui.annotations.StyleSheet;
import com.rameses.rcp.ui.annotations.Template;

/**
 *
 * @author louie
 */

@StyleSheet
@Template(FormPage.class)
public class PostingHeaderPage extends javax.swing.JPanel {

    /**
     * Creates new form PostingHeaderPage
     */
    public PostingHeaderPage() {
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

        xFormPanel1 = new com.rameses.rcp.control.XFormPanel();
        xTextField2 = new com.rameses.rcp.control.XTextField();
        xTextField3 = new com.rameses.rcp.control.XTextField();
        xComboBox1 = new com.rameses.rcp.control.XComboBox();
        xComboBox2 = new com.rameses.rcp.control.XComboBox();
        xComboBox3 = new com.rameses.rcp.control.XComboBox();
        xTextField4 = new com.rameses.rcp.control.XTextField();
        xIntegerField1 = new com.rameses.rcp.control.XIntegerField();
        xPanel1 = new com.rameses.rcp.control.XPanel();
        xDataTable1 = new com.rameses.rcp.control.XDataTable();
        xButton1 = new com.rameses.rcp.control.XButton();
        xButton2 = new com.rameses.rcp.control.XButton();
        xButton3 = new com.rameses.rcp.control.XButton();

        xFormPanel1.setPadding(new java.awt.Insets(0, 0, 0, 0));

        xTextField2.setCaption("Title");
        xTextField2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xTextField2.setName("entity.title"); // NOI18N
        xTextField2.setPreferredSize(new java.awt.Dimension(0, 20));
        xTextField2.setRequired(true);
        xTextField2.setTextCase(com.rameses.rcp.constant.TextCase.NONE);
        xFormPanel1.add(xTextField2);

        xTextField3.setCaption("Field Name");
        xTextField3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        xTextField3.setName("entity.name"); // NOI18N
        xTextField3.setPreferredSize(new java.awt.Dimension(0, 20));
        xTextField3.setRequired(true);
        xTextField3.setSpaceChar('_');
        xTextField3.setTextCase(com.rameses.rcp.constant.TextCase.LOWER);
        xFormPanel1.add(xTextField3);

        xComboBox1.setCaption("Type");
        xComboBox1.setItems("typeList");
        xComboBox1.setName("entity.type"); // NOI18N
        xComboBox1.setPreferredSize(new java.awt.Dimension(120, 20));
        xFormPanel1.add(xComboBox1);

        xComboBox2.setCaption("Data Type");
        xComboBox2.setItems("dataTypeList");
        xComboBox2.setName("entity.datatype"); // NOI18N
        xComboBox2.setPreferredSize(new java.awt.Dimension(120, 20));
        xFormPanel1.add(xComboBox2);

        xComboBox3.setCaption("Category");
        xComboBox3.setItems("categoryList");
        xComboBox3.setName("entity.category"); // NOI18N
        xComboBox3.setPreferredSize(new java.awt.Dimension(120, 20));
        xComboBox3.setRequired(true);
        xFormPanel1.add(xComboBox3);

        xTextField4.setCaption("Format");
        xTextField4.setName("entity.format"); // NOI18N
        xTextField4.setPreferredSize(new java.awt.Dimension(200, 20));
        xTextField4.setTextCase(com.rameses.rcp.constant.TextCase.NONE);
        xFormPanel1.add(xTextField4);

        xIntegerField1.setCaption("Seq. No.");
        xIntegerField1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        xIntegerField1.setName("entity.seqno"); // NOI18N
        xIntegerField1.setRequired(true);
        xFormPanel1.add(xIntegerField1);

        com.rameses.rcp.control.border.XTitledBorder xTitledBorder1 = new com.rameses.rcp.control.border.XTitledBorder();
        xTitledBorder1.setTitle("Field(s) to update after posting");
        xPanel1.setBorder(xTitledBorder1);

        xDataTable1.setHandler("listHandler");
        xDataTable1.setName("selectedItem"); // NOI18N
        xDataTable1.setColumns(new com.rameses.rcp.common.Column[]{
            new com.rameses.rcp.common.Column(new Object[]{
                new Object[]{"name", "schemaname"}
                , new Object[]{"caption", "Schema"}
                , new Object[]{"width", 100}
                , new Object[]{"minWidth", 0}
                , new Object[]{"maxWidth", 0}
                , new Object[]{"required", false}
                , new Object[]{"resizable", true}
                , new Object[]{"nullWhenEmpty", true}
                , new Object[]{"editable", false}
                , new Object[]{"visible", true}
                , new Object[]{"visibleWhen", null}
                , new Object[]{"textCase", com.rameses.rcp.constant.TextCase.NONE}
                , new Object[]{"typeHandler", new com.rameses.rcp.common.TextColumnHandler()}
            }),
            new com.rameses.rcp.common.Column(new Object[]{
                new Object[]{"name", "subschemaname"}
                , new Object[]{"caption", "Sub Schema"}
                , new Object[]{"width", 100}
                , new Object[]{"minWidth", 0}
                , new Object[]{"maxWidth", 0}
                , new Object[]{"required", false}
                , new Object[]{"resizable", true}
                , new Object[]{"nullWhenEmpty", true}
                , new Object[]{"editable", false}
                , new Object[]{"visible", true}
                , new Object[]{"visibleWhen", null}
                , new Object[]{"textCase", com.rameses.rcp.constant.TextCase.NONE}
                , new Object[]{"typeHandler", new com.rameses.rcp.common.TextColumnHandler()}
            }),
            new com.rameses.rcp.common.Column(new Object[]{
                new Object[]{"name", "fieldname"}
                , new Object[]{"caption", "Field Name"}
                , new Object[]{"width", 100}
                , new Object[]{"minWidth", 0}
                , new Object[]{"maxWidth", 0}
                , new Object[]{"required", false}
                , new Object[]{"resizable", true}
                , new Object[]{"nullWhenEmpty", true}
                , new Object[]{"editable", false}
                , new Object[]{"visible", true}
                , new Object[]{"visibleWhen", null}
                , new Object[]{"textCase", com.rameses.rcp.constant.TextCase.NONE}
                , new Object[]{"typeHandler", new com.rameses.rcp.common.TextColumnHandler()}
            })
        });

        xButton1.setDisableWhen("${mode=='read'}");
        xButton1.setName("addField"); // NOI18N
        xButton1.setPreferredSize(new java.awt.Dimension(60, 23));
        xButton1.setText("Add");

        xButton2.setDepends(new String[] {"selectedItem"});
        xButton2.setDisableWhen("${mode=='read'||selectedItem==null}");
        xButton2.setName("editField"); // NOI18N
        xButton2.setPreferredSize(new java.awt.Dimension(60, 23));
        xButton2.setText("Edit");

        xButton3.setDepends(new String[] {"selectedItem"});
        xButton3.setDisableWhen("${mode=='read'||selectedItem==null}");
        xButton3.setName("removeField"); // NOI18N
        xButton3.setPreferredSize(new java.awt.Dimension(80, 23));
        xButton3.setText("Remove");

        javax.swing.GroupLayout xPanel1Layout = new javax.swing.GroupLayout(xPanel1);
        xPanel1.setLayout(xPanel1Layout);
        xPanel1Layout.setHorizontalGroup(
            xPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(xPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(xDataTable1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(xPanel1Layout.createSequentialGroup()
                        .addComponent(xButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(xButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(xButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        xPanel1Layout.setVerticalGroup(
            xPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(xPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xDataTable1, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(xPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(xButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(xButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(xFormPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 43, Short.MAX_VALUE))
                    .addComponent(xPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xFormPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.rameses.rcp.control.XButton xButton1;
    private com.rameses.rcp.control.XButton xButton2;
    private com.rameses.rcp.control.XButton xButton3;
    private com.rameses.rcp.control.XComboBox xComboBox1;
    private com.rameses.rcp.control.XComboBox xComboBox2;
    private com.rameses.rcp.control.XComboBox xComboBox3;
    private com.rameses.rcp.control.XDataTable xDataTable1;
    private com.rameses.rcp.control.XFormPanel xFormPanel1;
    private com.rameses.rcp.control.XIntegerField xIntegerField1;
    private com.rameses.rcp.control.XPanel xPanel1;
    private com.rameses.rcp.control.XTextField xTextField2;
    private com.rameses.rcp.control.XTextField xTextField3;
    private com.rameses.rcp.control.XTextField xTextField4;
    // End of variables declaration//GEN-END:variables
}
