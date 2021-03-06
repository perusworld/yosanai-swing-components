/*
 *
 * This is the MIT License
 * http://www.opensource.org/licenses/mit-license.php
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package com.yosanai.java.swing.config;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang.mutable.MutableBoolean;

/**
 * @author Saravana Perumal Shanmugam
 * 
 */
@SuppressWarnings("serial")
public class ConfigPanel extends javax.swing.JPanel {

    protected CustomTableModel tableModel = new CustomTableModel();

    public class CustomTableModel extends DefaultTableModel {

        @SuppressWarnings("rawtypes")
        Class[] types = new Class[] { java.lang.String.class, java.lang.String.class };

        MutableBoolean[] canEdit = new MutableBoolean[] { new MutableBoolean(false), new MutableBoolean(true) };

        /**
         * 
         */
        private CustomTableModel() {
            super(new Object[][] {}, new String[] { "Key", "Value" });
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public Class getColumnClass(int columnIndex) {
            return types[columnIndex];
        }

        /*
         * (non-Jsdoc)
         * 
         * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
         */
        @Override
        public boolean isCellEditable(int row, int column) {
            return canEdit[column].booleanValue();
        }

    }

    /** Creates new form ConfigPanel */
    public ConfigPanel() {
        initComponents();
    }

    public void loadConfig(Map<String, String> config) {
        DefaultTableModel model = (DefaultTableModel) tblConfig.getModel();
        while (0 < model.getRowCount()) {
            model.removeRow(0);
        }
        for (String key : config.keySet()) {
            model.addRow(new Object[] { key, (null == config.get(key) ? "" : config.get(key)) });
        }
    }

    public Map<String, String> getConfig() {
        Map<String, String> ret = new LinkedHashMap<String, String>();
        for (int index = 0; index < tblConfig.getRowCount(); index++) {
            ret.put(tblConfig.getValueAt(index, 0).toString(), tblConfig.getValueAt(index, 1).toString());
        }
        return ret;
    }

    public void setEditable(int column, boolean edit) {
        tableModel.canEdit[column].setValue(edit);
        tableModel.fireTableStructureChanged();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed"
    // desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlTop = new javax.swing.JPanel();
        scrConfig = new javax.swing.JScrollPane();
        tblConfig = new javax.swing.JTable();
        pnlRight = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        pnlTop.setLayout(new java.awt.BorderLayout());

        tblConfig.setModel(tableModel);
        tblConfig.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrConfig.setViewportView(tblConfig);

        pnlTop.add(scrConfig, java.awt.BorderLayout.CENTER);

        add(pnlTop, java.awt.BorderLayout.CENTER);

        pnlRight.setLayout(new java.awt.GridBagLayout());

        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlRight.add(btnAdd, gridBagConstraints);

        btnRemove.setText("Remove");
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        pnlRight.add(btnRemove, gridBagConstraints);

        add(pnlRight, java.awt.BorderLayout.LINE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnAddActionPerformed
        DefaultTableModel model = (DefaultTableModel) tblConfig.getModel();
        model.addRow(new Object[] { "key", "value" });
    }// GEN-LAST:event_btnAddActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnRemoveActionPerformed
        int row = tblConfig.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) tblConfig.getModel();
        if (-1 < row) {
            model.removeRow(row);
        }
    }// GEN-LAST:event_btnRemoveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;

    private javax.swing.JButton btnRemove;

    private javax.swing.JPanel pnlRight;

    private javax.swing.JPanel pnlTop;

    private javax.swing.JScrollPane scrConfig;

    private javax.swing.JTable tblConfig;
    // End of variables declaration//GEN-END:variables

}
