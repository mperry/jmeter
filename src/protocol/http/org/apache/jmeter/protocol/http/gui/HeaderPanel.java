/*
 * ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 - 2003 The Apache Software Foundation.  All rights
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
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.testelement.*;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.util.LocaleChangeEvent;


/****************************************
 * Allows the user to specify if she needs HTTP header services, and give
 * parameters for this service.
 *
 * @author    mstover
 * @author    <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @created   $Date$
 * @version   $Revision$
 ***************************************/
public class HeaderPanel extends AbstractConfigGui implements ActionListener, FocusListener, ListSelectionListener
{

    transient private static Logger log = Hierarchy.getDefaultHierarchy().getLoggerFor("jmeter.protocol.http");

    private static String ADD = "add";
    private static String REMOVE = "remove";
    private static String LOAD = "load";
    private static String SAVE = "save";


    HeaderTableModel tableModel;
    private JButton addButton;
    private JButton removeButton;
    private JButton loadButton;
    private JButton saveButton;
    private JLabel tableLabel;
    private JTable headerTable;


    public HeaderPanel()
    {
    }


    /****************************************
     * !ToDo (Method description)
     *
     *@return   !ToDo (Return description)
     ***************************************/
    public NamedTestElement createTestElement()
    {
//        HeaderManager headerManager = tableModel.manager;
//        configureTestElement(headerManager);
//        return (NamedTestElement)headerManager.clone();
        return null;
    }


    public void configure(TestElementConfiguration config)
    {
        super.configure(config);
// todo:       tableModel = new HeaderTableModel((List)element.getPropertyValue(HeaderManager.HEADERS));
        tableModel = new HeaderTableModel(new ArrayList());
        headerTable.setModel(tableModel);
        DefaultCellEditor editor = new DefaultCellEditor(new JTextField());
        editor.setClickCountToStart(1);

        for (int i = 0; i < 2; i++)
        {
            headerTable.getColumnModel().getColumn(i).setCellEditor(editor);
        }
        setButtonState();
    }


    public String getStaticLabel()
    {
        return "header_manager_title";
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

        tableLabel = new JLabel(JMeterUtils.getResString("headers_stored"));
        tableLabel.setName("headers_stored");
        constraints = new JMeterGridBagConstraints();
        panel.add(tableLabel, constraints);
        headerTable = new JTable(tableModel);
        headerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        headerTable.getTableHeader().setReorderingAllowed(false);
        headerTable.getSelectionModel().addListSelectionListener(this);
        headerTable.addFocusListener(this);
        JScrollPane scroller = new JScrollPane(headerTable);
        Dimension tableDim = scroller.getPreferredSize();
        tableDim.height = 140;
        tableDim.width = (int)(tableDim.width * 1.5);
        headerTable.setPreferredScrollableViewportSize(tableDim);
        scroller.setColumnHeaderView(headerTable.getTableHeader());
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
            updateLocalizedTableHeaders(headerTable, tableModel);
        }
    }


    public void valueChanged(ListSelectionEvent e)
    {
        setButtonState();
    }


    private void setButtonState()
    {
        addButton.setEnabled(true);
        removeButton.setEnabled(headerTable.getSelectedRow() != -1);
        loadButton.setEnabled(true);
        saveButton.setEnabled(false);
        if (tableModel != null)
        {
            saveButton.setEnabled(tableModel.getRowCount() > 0);
        }
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
                if (headerTable.isEditing())
                {
                    TableCellEditor cellEditor = headerTable.getCellEditor(headerTable.getEditingRow(), headerTable.getEditingColumn());
                    cellEditor.cancelCellEditing();
                }

                int rowSelected = headerTable.getSelectedRow();

                if (rowSelected != -1)
                {
                    tableModel.removeArgument(rowSelected);

                    if (tableModel.getRowCount() > 0)
                    {
                        int rowToSelect = rowSelected;

                        if (rowSelected >= tableModel.getRowCount())
                        {
                            rowToSelect = rowSelected - 1;
                        }
                        headerTable.clearSelection();
                        headerTable.setRowSelectionInterval(rowToSelect, rowToSelect);
                    }
                }
            }
        } else if (action.equals(ADD))
        {
            // If a table cell is being edited, we should accept the current value
            // and stop the editing before adding a new row.
            if (headerTable.isEditing())
            {
                TableCellEditor cellEditor = headerTable.getCellEditor(headerTable.getEditingRow(), headerTable.getEditingColumn());
                cellEditor.stopCellEditing();
            }

            tableModel.addArgument();

            // Highlight (select) the appropriate row.
            int rowToSelect = tableModel.getRowCount() - 1;
            headerTable.setRowSelectionInterval(rowToSelect, rowToSelect);
        } else if (action.equals(LOAD))
        {
            try
            {
                JFileChooser chooser = FileDialoger.promptToOpenFile();

                if (chooser != null)
                {
                    List headers = HeaderManager.loadHeaders(chooser.getSelectedFile().getAbsolutePath());
                    tableModel.addHeaders(headers);
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
                    HeaderManager.saveHeaders(tableModel.getHeaders(), chooser.getSelectedFile().getAbsolutePath());
                }
            } catch (IOException ex)
            {
                log.error("", ex);
            } catch (NullPointerException err)
            {
            }
        }
    }


    public void focusGained(FocusEvent e)
    {
    }


    public void focusLost(FocusEvent e)
    {
//        System.out.println("Focus lost");
//        if (headerTable.isEditing()) {
//            System.out.println("isEditing");
//            TableCellEditor cellEditor = headerTable.getCellEditor(headerTable.getEditingRow(), headerTable.getEditingColumn());
//            Component editor = headerTable.getEditorComponent();
//            if (! editor.isFocusOwner()) {
//                System.out.println("Stop editing");
//                cellEditor.stopCellEditing();
//            }
//        }
    }


    protected static class HeaderTableModel extends DefaultTableModel
    {

        private List headers;


        public HeaderTableModel(List arguments)
        {
            this.headers = arguments;
        }


        protected List getHeaders()
        {
            return headers;
        }


        public int getRowCount()
        {
            if (headers == null)
            {
                return 0;
            }
            return headers.size();
        }


        public int getColumnCount()
        {
            return 2;
        }


        public String getColumnName(int column)
        {
            switch (column)
            {
                case 0:
                    return JMeterUtils.getResString("name_column");
                case 1:
                    return JMeterUtils.getResString("value_column");
                default:
                    return "";
            }
        }


        public Class getColumnClass(int columnIndex)
        {
            return String.class;
        }


        public boolean isCellEditable(int row, int column)
        {
            return true;
        }


        public Object getValueAt(int row, int column)
        {
            Header header = (Header)headers.get(row);

            switch (column)
            {
                case 0:
                    return header.getName();
                case 1:
                    return header.getValue();
                default:
                    return null;
            }
        }


        public void setValueAt(Object value, int row, int column)
        {
            Header header = (Header)headers.get(row);

            switch (column)
            {
                case 0:
                    header.setName((String)value);
                    break;
                case 1:
                    header.setValue((String)value);
                    break;
                default:

            }
        }


        public void addArgument()
        {
            headers.add(new Header());
            fireTableRowsInserted(headers.size() - 1, headers.size() - 1);
        }

        public void addHeaders(Collection headers)
        {
            int startIndex = headers.size();

            this.headers.addAll(headers);
            fireTableRowsInserted(startIndex, headers.size() - 1);
        }

        public void removeArgument(int row)
        {
            headers.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }

}
