/*
 * The MIT License
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
 */

package com.yosanai.java.swing.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Security;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.DefaultTableModel;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 * 
 * @author Saravana Perumal Shanmugam
 */
public class ConfigDialog extends javax.swing.JDialog {
    /**
     * 
     */
    private static final String DEFAULT_ALGORITHM = "PBEWITHSHA256AND128BITAES-CBC-BC";

    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;

    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;

    protected String file;

    protected XMLConfiguration configuration;

    /** Creates new form ConfigDialog */
    public ConfigDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /** @return the return status of this dialog - one of RET_OK or RET_CANCEL */
    public int getReturnStatus() {
        return returnStatus;
    }

    /**
     * @param file
     *            the file to set
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * @return the configuration
     */
    public XMLConfiguration getConfiguration() {
        return configuration;
    }

    public void init(String... keys) {
        if (null == configuration) {
            ConfigPasswordDialog dialog = new ConfigPasswordDialog(null, true);
            StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
            if (null == Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)) {
                Security.addProvider(new BouncyCastleProvider());
            }
            encryptor.setAlgorithm(DEFAULT_ALGORITHM);
            dialog.setEncryptor(encryptor);
            dialog.setVisible(true);
            if (ConfigPasswordDialog.RET_OK == dialog.getReturnStatus()) {
                try {
                    configuration = new EncryptedXMLConfiguration(encryptor);
                    configuration.setFileName(file);
                    configuration.load(file);
                } catch (ConfigurationException e) {
                    try {
                        String defaultPath = System.getProperty("user.home") + "/" + file;
                        new File(defaultPath).createNewFile();
                        FileInputStream ins = new FileInputStream(defaultPath);
                        String entries = IOUtils.toString(ins);
                        IOUtils.closeQuietly(ins);
                        if (StringUtils.isBlank(entries)) {
                            configuration = new EncryptedXMLConfiguration(encryptor);
                            configuration.setFileName(defaultPath);
                            try {
                                configuration.save();
                            } catch (ConfigurationException cfEx) {
                                Logger.getLogger(ConfigDialog.class.getName()).log(Level.SEVERE, null, cfEx);
                            }
                        }
                    } catch (IOException ioEx) {
                        Logger.getLogger(ConfigDialog.class.getName()).log(Level.SEVERE, null, ioEx);
                    }
                }
            }
        }
        if (null != configuration) {
            configuration.setAutoSave(true);
            load(keys);
        }
    }

    protected void load(String... keys) {
        DefaultTableModel model = (DefaultTableModel) tblConfig.getModel();
        while (0 < model.getRowCount()) {
            model.removeRow(0);
        }
        for (String key : keys) {
            model.addRow(new Object[] { key, configuration.getString(key, "") });
        }
    }

    protected void updateConfig() {
        for (int index = 0; index < tblConfig.getRowCount(); index++) {
            configuration.setProperty(tblConfig.getValueAt(index, 0).toString(), tblConfig.getValueAt(index, 1)
                    .toString());
        }
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

        pnlBottom = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        pnlTop = new javax.swing.JPanel();
        scrConfig = new javax.swing.JScrollPane();
        tblConfig = new javax.swing.JTable();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        pnlBottom.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        pnlBottom.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        pnlBottom.add(cancelButton);

        getContentPane().add(pnlBottom, java.awt.BorderLayout.PAGE_END);

        pnlTop.setLayout(new java.awt.BorderLayout());

        tblConfig.setModel(new javax.swing.table.DefaultTableModel(new Object[][] {

        }, new String[] { "Key", "Value" }) {
            Class[] types = new Class[] { java.lang.String.class, java.lang.String.class };

            boolean[] canEdit = new boolean[] { false, true };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        scrConfig.setViewportView(tblConfig);

        pnlTop.add(scrConfig, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlTop, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }// GEN-LAST:event_closeDialog

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_okButtonActionPerformed
        updateConfig();
        doClose(RET_OK);
    }// GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }// GEN-LAST:event_cancelButtonActionPerformed

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ConfigDialog dialog = new ConfigDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;

    private javax.swing.JButton okButton;

    private javax.swing.JPanel pnlBottom;

    private javax.swing.JPanel pnlTop;

    private javax.swing.JScrollPane scrConfig;

    private javax.swing.JTable tblConfig;

    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
