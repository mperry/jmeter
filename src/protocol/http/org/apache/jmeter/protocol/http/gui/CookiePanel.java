/*
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 * if any, must include the following acknowledgment:
 * "This product includes software developed by the
 * Apache Software Foundation (http://www.apache.org/)."
 * Alternately, this acknowledgment may appear in the software itself,
 * if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 * "Apache JMeter" must not be used to endorse or promote products
 * derived from this software without prior written permission. For
 * written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 * "Apache JMeter", nor may "Apache" appear in their name, without
 * prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jmeter.protocol.http.gui;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import org.apache.log.Hierarchy;
import org.apache.log.Logger;

import org.apache.jmeter.config.gui.AbstractConfigGui;
import org.apache.jmeter.gui.GUIFactory;
import org.apache.jmeter.gui.util.FileDialoger;
import org.apache.jmeter.gui.util.JMeterGridBagConstraints;
import org.apache.jmeter.protocol.http.control.*;
import org.apache.jmeter.testelement.NamedTestElement;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.util.LocaleChangeEvent;


/****************************************
 * Allows the user to specify if she needs cookie services, and give parameters
 * for this service.
 *
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @created   $Date$
 * @version   $Revision$
 ***************************************/
public class CookiePanel extends AbstractConfigGui implements ActionListener, ListSelectionListener, FocusListener
{

    transient private static Logger log = Hierarchy.getDefaultHierarchy().getLoggerFor("jmeter.protocol.http");


    private static String ADD = "add";
    private static String REMOVE = "remove";
    private static String LOAD = "load";
    private static String SAVE = "save";


    private JButton addButton;
    private JButton removeButton;
    private JButton loadButton;
    private JButton saveButton;
    private JLabel tableLabel;
    private JTable cookieTable;
    private CookieTableModel tableModel;


    public CookiePanel()
    {
    }


    public String getStaticLabel()
    {
        return "cookie_manager_title";
    }


    public void actionPerformed(ActionEvent e)
    {
        String action = e.getActionCommand();

        if (action.equals(REMOVE))
        {
            if (tableModel.getRowCount() > 0)
            {
                // If a table cell is being edited, we must cancel the editing before
                // deleting the row
                if (cookieTable.isEditing())
                {
                    TableCellEditor cellEditor = cookieTable.getCellEditor(cookieTable.getEditingRow(), cookieTable.getEditingColumn());
                    cellEditor.cancelCellEditing();
                }

                int rowSelected = cookieTable.getSelectedRow();

                if (rowSelected != -1)
                {
                    tableModel.removeCookie(rowSelected);

                    if (tableModel.getRowCount() > 0)
                    {
                        int rowToSelect = rowSelected;

                        if (rowSelected >= tableModel.getRowCount())
                        {
                            rowToSelect = rowSelected - 1;
                        }
                        cookieTable.clearSelection();
                        cookieTable.setRowSelectionInterval(rowToSelect, rowToSelect);
                    }
                }
            }
        } else if (action.equals(ADD))
        {
            // If a table cell is being edited, we should accept the current value
            // and stop the editing before adding a new row.
            if (cookieTable.isEditing())
            {
                TableCellEditor cellEditor = cookieTable.getCellEditor(cookieTable.getEditingRow(), cookieTable.getEditingColumn());
                cellEditor.stopCellEditing();
            }

            tableModel.addCookie();

            // Highlight (select) the appropriate row.
            int rowToSelect = tableModel.getRowCount() - 1;
            cookieTable.setRowSelectionInterval(rowToSelect, rowToSelect);
        } else if (action.equals(LOAD))
        {
            try
            {
                JFileChooser chooser = FileDialoger.promptToOpenFile();

                if (chooser != null)
                {
                    Collection authorizations = AuthManager.loadAuthorizations(chooser.getSelectedFile().getAbsolutePath());
                    List cookies = CookieManager.loadCookies(chooser.getSelectedFile().getAbsolutePath());
                    tableModel.addCookies(cookies);
                }
            } catch (IOException ex)
            {
                log.error("", ex);
            } catch (NullPointerException err)
            {
            }
        } else if (action.equals(SAVE))
        {
            try
            {
                JFileChooser chooser = FileDialoger.promptToSaveFile(null);

                if (chooser != null)
                {
                    CookieManager.saveCookies(tableModel.getCookies(), chooser.getSelectedFile().getAbsolutePath());
                }
            } catch (IOException ex)
            {
                log.error("", ex);
            } catch (NullPointerException err)
            {
            }
        }
    }


    /****************************************
     * !ToDo (Method description)
     *
     *@return   !ToDo (Return description)
     ***************************************/
    public NamedTestElement createTestElement()
    {
//        CookieManager cookieManager = createCookieManager();
//        configureTestElement(cookieManager);
//        return cookieManager;
        return null;
    }


    public void configure(TestElement element)
    {
        super.configure(element);
        tableModel = new CookieTableModel((List)element.getPropertyValue(CookieManager.COOKIES));
        cookieTable.setModel(tableModel);

        DefaultCellEditor editor = new DefaultCellEditor(new JTextField());
        editor.setClickCountToStart(1);

        for (int i = 0; i < 6; i++)
        {
            if (i != 4)
            {
                cookieTable.getColumnModel().getColumn(i).setCellEditor(editor);
            }
        }
        setButtonState();
    }


    protected void initComponents()
    {
        super.initComponents();

        JPanel panel = GUIFactory.createPanel();
        panel.setLayout(new GridBagLayout());
        JMeterGridBagConstraints constraints = new JMeterGridBagConstraints();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        constraints = new JMeterGridBagConstraints();
        constraints.fillHorizontal(1.0);
        addButton = new JButton(JMeterUtils.getResString("add"));
        addButton.setActionCommand(ADD);
        addButton.addActionListener(this);
        addButton.setName("add");
        buttonPanel.add(addButton, constraints);
        removeButton = new JButton(JMeterUtils.getResString("remove"));
        removeButton.setName("remove");
        removeButton.setActionCommand(REMOVE);
        removeButton.addActionListener(this);
        constraints = constraints.nextRow();
        buttonPanel.add(removeButton, constraints);
        loadButton = new JButton(JMeterUtils.getResString("load"));
        loadButton.setName("load");
        loadButton.setActionCommand(LOAD);
        loadButton.addActionListener(this);
        constraints = constraints.nextRow();
        buttonPanel.add(loadButton, constraints);
        saveButton = new JButton(JMeterUtils.getResString("save"));
        saveButton.setName("save");
        saveButton.setActionCommand(SAVE);
        saveButton.addActionListener(this);
        saveButton.setMnemonic('S');
        constraints = constraints.nextRow();
        buttonPanel.add(saveButton, constraints);

        tableLabel = new JLabel(JMeterUtils.getResString("cookies_stored"));
        tableLabel.setName("cookies_stored");
        constraints = new JMeterGridBagConstraints();
        panel.add(tableLabel, constraints);
        cookieTable = new JTable(tableModel);
        cookieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cookieTable.getTableHeader().setReorderingAllowed(false);
        cookieTable.getSelectionModel().addListSelectionListener(this);
        cookieTable.addFocusListener(this);
        JScrollPane scroller = new JScrollPane(cookieTable);
        Dimension tableDim = scroller.getPreferredSize();
        tableDim.height = 140;
        tableDim.width = (int)(tableDim.width * 1.5);
        cookieTable.setPreferredScrollableViewportSize(tableDim);
        scroller.setColumnHeaderView(cookieTable.getTableHeader());
        constraints = constraints.nextRow();
        constraints.fillHorizontal(1.0);
        constraints.fillVertical(1.0);
        panel.add(scroller, constraints);
        constraints = constraints.incrementX();
        constraints.fillNone();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        panel.add(buttonPanel, constraints);

        add(panel);
    }


    public void localeChanged(LocaleChangeEvent event)
    {
        super.localeChanged(event);
        updateLocalizedStrings(new JComponent[]{tableLabel, addButton, removeButton, loadButton, saveButton});
        if (tableModel != null)
        {
            updateLocalizedTableHeaders(cookieTable, tableModel);
        }
    }


    public void valueChanged(ListSelectionEvent e)
    {
        setButtonState();
    }


    public void focusGained(FocusEvent e)
    {
    }


    public void focusLost(FocusEvent e)
    {
//        if (cookieTable.isEditing()) {
//            TableCellEditor cellEditor = cookieTable.getCellEditor(cookieTable.getEditingRow(), cookieTable.getEditingColumn());
//            cellEditor.stopCellEditing();
//        }
    }


    private void setButtonState()
    {
        addButton.setEnabled(true);
        removeButton.setEnabled(cookieTable.getSelectedRow() != -1);
        loadButton.setEnabled(true);
        saveButton.setEnabled(false);
        if (tableModel != null)
        {
            saveButton.setEnabled(tableModel.getRowCount() > 0);
        }
    }


    private static class CookieTableModel extends AbstractTableModel
    {

        private List cookies;


        public CookieTableModel(List cookies)
        {
            this.cookies = cookies;
        }


        public int getRowCount()
        {
            return cookies.size();
        }


        public int getColumnCount()
        {
            return 6;
        }


        public Object getValueAt(int rowIndex, int columnIndex)
        {
            Cookie cookie = (Cookie)cookies.get(rowIndex);

            switch (columnIndex)
            {
                case 0:
                    return cookie.getName();
                case 1:
                    return cookie.getValue();
                case 2:
                    return cookie.getDomain();
                case 3:
                    return cookie.getPath();
                case 4:
                    return new Boolean(cookie.isSecure());
                case 5:
                    return new Long(cookie.getExpires());
                default:
                    return "";
            }
        }


        public void setValueAt(Object value, int rowIndex, int columnIndex)
        {
            Cookie cookie = (Cookie)cookies.get(rowIndex);

            switch (columnIndex)
            {
                case 0:
                    cookie.setName((String)value);
                    break;
                case 1:
                    cookie.setValue((String)value);
                    break;
                case 2:
                    cookie.setDomain((String)value);
                    break;
                case 3:
                    cookie.setPath((String)value);
                    break;
                case 4:
                    cookie.setSecure(((Boolean)value).booleanValue());
                    break;
                case 5:
                    cookie.setExpires((new Long((String)value)).longValue());
                    break;
                default:
                    ;
            }
        }


        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            return true;
        }


        public String getColumnName(int column)
        {
            switch (column)
            {
                case 0:
                    return JMeterUtils.getResString("name_column");
                case 1:
                    return JMeterUtils.getResString("value_column");
                case 2:
                    return JMeterUtils.getResString("domain_column");
                case 3:
                    return JMeterUtils.getResString("path_column");
                case 4:
                    return JMeterUtils.getResString("secure_column");
                case 5:
                    return JMeterUtils.getResString("expires_column");
                default:
                    return "";
            }
        }


        public Class getColumnClass(int columnIndex)
        {
            if (columnIndex == 4)
            {
                return Boolean.class;
            } else if (columnIndex == 5)
            {
                return Long.class;
            }
            return String.class;
        }


        public void addCookie()
        {
            cookies.add(new Cookie());
            fireTableRowsInserted(cookies.size() - 1, cookies.size() - 1);
        }


        public void removeCookie(int index)
        {
            cookies.remove(index);
            fireTableRowsDeleted(index, index);
        }


        public void addCookies(Collection cookies)
        {
            int startIndex = cookies.size();

            this.cookies.addAll(cookies);
            fireTableRowsInserted(startIndex, cookies.size() - 1);
        }


        public Collection getCookies()
        {
            return cookies;
        }
    }
}
