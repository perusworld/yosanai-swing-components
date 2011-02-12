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

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
import org.jasypt.encryption.pbe.PBEStringEncryptor;

/**
 * @author Saravana Perumal Shanmugam
 * 
 */
@SuppressWarnings("serial")
public class EncryptedXMLConfiguration extends XMLConfiguration {

    protected PBEStringEncryptor encryptor;

    /**
     * @param encryptor
     */
    public EncryptedXMLConfiguration(PBEStringEncryptor encryptor) {
        super();
        this.encryptor = encryptor;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see
     * org.apache.commons.configuration.AbstractConfiguration#getString(java
     * .lang.String, java.lang.String)
     */
    @Override
    public String getString(String key, String defaultValue) {
        String ret = super.getString(key, defaultValue);
        if (StringUtils.isNotBlank(ret)) {
            ret = encryptor.decrypt(ret);
        }
        return ret;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see
     * org.apache.commons.configuration.AbstractHierarchicalFileConfiguration
     * #setProperty(java.lang.String, java.lang.Object)
     */
    @Override
    public void setProperty(String key, Object value) {
        if (value instanceof String) {
            String strValue = (String) value;
            if (StringUtils.isNotBlank(strValue)) {
                value = encryptor.encrypt(strValue);
            }
        }
        super.setProperty(key, value);
    }

}
