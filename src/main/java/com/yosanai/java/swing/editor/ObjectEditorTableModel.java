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
package com.yosanai.java.swing.editor;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditorSupport;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

/**
 * @author Saravana Perumal Shanmugam
 * 
 */
@SuppressWarnings("serial")
public class ObjectEditorTableModel extends DefaultTableModel {

    @SuppressWarnings("rawtypes")
    protected Class[] types = new Class[] { java.lang.String.class, java.lang.String.class };

    protected Object wrappedObj;

    protected BeanWrapper beanWrapper;

    protected boolean editable;

    protected boolean expandAllProperties;

    protected PropertyEditorSupport dateAndTimePropertyEditor = null;

    /**
     * 
     */
    public ObjectEditorTableModel() {
        super(new Object[][] {

        }, new String[] { "Field Name", "Field Value" });
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Class getColumnClass(int columnIndex) {
        return types[columnIndex];
    }

    /**
     * @return the editable
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * @param editable
     *            the editable to set
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    /**
     * @return the expandAllProperties
     */
    public boolean isExpandAllProperties() {
        return expandAllProperties;
    }

    /**
     * @param expandAllProperties
     *            the expandAllProperties to set
     */
    public void setExpandAllProperties(boolean expandAllProperties) {
        this.expandAllProperties = expandAllProperties;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        boolean ret = false;
        if (editable && 1 == columnIndex) {
            String propertyName = getValueAt(rowIndex, 0).toString();
            ret = beanWrapper.isWritableProperty(propertyName);
        }
        return ret;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see javax.swing.table.DefaultTableModel#setValueAt(java.lang.Object,
     * int, int)
     */
    @Override
    public void setValueAt(Object aValue, int row, int column) {
        String propertyName = getValueAt(row, 0).toString();
        Object oldValue = beanWrapper.getPropertyValue(propertyName);
        try {
            beanWrapper.setPropertyValue(propertyName, aValue);
            super.setValueAt(aValue, row, column);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Failed to set property value for " + propertyName,
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            beanWrapper.setPropertyValue(propertyName, oldValue);
        }
    }

    public void clearTable() {
        while (0 < getRowCount()) {
            removeRow(0);
        }
    }

    public void setObject(Object wrappedObj) {
        clearTable();
        if (wrappedObj instanceof Collection<?>) {
            // NOOP
        } else if (wrappedObj instanceof Map<?, ?>) {
            Map<?, ?> mapObj = (Map<?, ?>) wrappedObj;
            for (Object key : mapObj.keySet()) {
                addRow(new Object[] { key.toString(), mapObj.get(key) });
            }
        } else {
            this.wrappedObj = wrappedObj;
            this.beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(wrappedObj);
            beanWrapper.setExtractOldValueForEditor(true);
            if (null != dateAndTimePropertyEditor) {
                beanWrapper.registerCustomEditor(Timestamp.class, dateAndTimePropertyEditor);
                beanWrapper.registerCustomEditor(Date.class, dateAndTimePropertyEditor);
            }
            addRows(wrappedObj, null, null);
        }

    }

    protected void addRows(Object wrappedObject, String prefix, Set<Integer> visited) {
        if (null == visited) {
            visited = new HashSet<Integer>();
        }
        if (null != wrappedObject && !visited.contains(wrappedObject.hashCode())) {
            visited.add(wrappedObject.hashCode());
            BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(wrappedObj);
            beanWrapper.setExtractOldValueForEditor(true);
            if (null != dateAndTimePropertyEditor) {
                beanWrapper.registerCustomEditor(Timestamp.class, dateAndTimePropertyEditor);
                beanWrapper.registerCustomEditor(Date.class, dateAndTimePropertyEditor);
            }
            PropertyDescriptor[] propDescs = beanWrapper.getPropertyDescriptors();
            for (PropertyDescriptor propertyDescriptor : propDescs) {
                addRows(beanWrapper, propertyDescriptor, prefix, visited);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    protected void addRows(BeanWrapper beanWrapper, PropertyDescriptor propertyDescriptor, String prefix,
            Set<Integer> visited) {
        if (StringUtils.isBlank(prefix)) {
            prefix = "";
        } else if (!prefix.endsWith(".")) {
            prefix += ".";
        }
        Object propertyValue = beanWrapper.getPropertyValue(propertyDescriptor.getName());
        if (isPrimitive(propertyDescriptor.getPropertyType())) {
            String value = "";
            if (null != propertyValue) {
                if (propertyDescriptor.getPropertyType().isEnum()) {
                    value = ((Enum) propertyValue).name();
                } else {
                    value = propertyValue.toString();
                }
            }
            addRow(new Object[] { prefix + propertyDescriptor.getName(), value });
        } else if (expandAllProperties) {
            addRows(propertyValue, prefix + propertyDescriptor.getName(), visited);
        }
    }

    public void saveValues() {
        for (int index = 1; index < getRowCount(); index++) {
            String propertyName = getValueAt(index, 0).toString();
            String propertyValue = getValueAt(index, 1).toString();
            if (beanWrapper.isWritableProperty(propertyName)) {
                try {
                    beanWrapper.setPropertyValue(propertyName, propertyValue);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Failed to set property value for "
                            + propertyName, JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public boolean isPrimitive(Class<?> classObj) {
        boolean ret = false;
        ret = null != classObj
                && (classObj.isPrimitive() || classObj.isEnum() || classObj.equals(Integer.class)
                        || classObj.equals(Float.class) || classObj.equals(Double.class) || classObj.equals(Date.class)
                        || classObj.equals(String.class) || classObj.equals(Boolean.class) || Number.class
                        .isAssignableFrom(classObj));
        return ret;
    }
}
