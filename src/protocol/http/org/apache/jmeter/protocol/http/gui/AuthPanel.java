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
import java.util.*;
import java.util.List;
import java.io.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

import org.apache.jmeter.config.gui.AbstractConfigGui;
import org.apache.jmeter.gui.util.JMeterGridBagConstraints;
import org.apache.jmeter.gui.util.FileDialoger;
import org.apache.jmeter.gui.GUIFactory;
import org.apache.jmeter.protocol.http.control.AuthManager;
import org.apache.jmeter.protocol.http.control.Authorization;
import org.apache.jmeter.testelement.NamedTestElement;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.util.LocaleChangeEvent;

import org.apache.log.Hierarchy;
import org.apache.log.Logger;


/****************************************
 * Handles input for determining if authentication services are required for a
 * Sampler. It also understands how to get AuthManagers for the files that the
 * user selects.
 *
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @created   $Date$
 * @version   $Revision$
 ***************************************/
// todo: rename this to AuthManagerGui

public class AuthPanel extends AbstractConfigGui implements ActionListener, ListSelectionListener, FocusListener
{

    transient private static Logger log = Hierarchy.getDefaultHierarchy().getLoggerFor("jmeter.protocol.http");


    private static String ADD = "add";
    private static String REMOVE = "remove";
    private static String LOAD = "load";
    private static String SAVE = "save";

    private JButton removeButton;
    private JButton addButton;
    private JButton loadButton;
    private JButton saveButton;
    private JTable authTable;
    private AuthorizationTableModel tableModel;
    private JLabel tableLabel;


    public AuthPanel()
    {
    }


    public NamedTestElement createTestElement()
    {
//        AuthManager authMan = tableModel.manager;
//        configureTestElement(authMan);
//        return (NamedTestElement)authMan.clone();
        return null;
    }


    public void configure(TestElement element)
    {
        super.configure(element);
        tableModel = new AuthorizationTableModel((List)element.getPropertyValue(AuthManager.AUTHORIZATIONS));
        authTable.setModel(tableModel);
        TableColumn passwordColumn = authTable.getColumnModel().getColumn(2);
        DefaultCellEditor editor = new DefaultCellEditor(new JPasswordField());
        editor.setClickCountToStart(1);

        passwordColumn.setCellEditor(editor);
        passwordColumn.setCellRenderer(new PasswordCellRenderer());

        editor = new DefaultCellEditor(new JTextField());
        editor.setClickCountToStart(1);

        for (int i = 0; i < 2; i++)
        {
            authTable.getColumnModel().getColumn(i).setCellEditor(editor);
        }
        setButtonState();
    }


    private void setButtonState()
    {
        addButton.setEnabled(true);
        removeButton.setEnabled(authTable.getSelectedRow() != -1);
        loadButton.setEnabled(true);
        saveButton.setEnabled(false);
        if (tableModel != null)
        {
            saveButton.setEnabled(tableModel.getRowCount() > 0);
        }
    }


    public String getStaticLabel()
    {
        return "auth_manager_title";
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
                if (authTable.isEditing())
                {
                    TableCellEditor cellEditor = authTable.getCellEditor(authTable.getEditingRow(), authTable.getEditingColumn());
                    cellEditor.cancelCellEditing();
                }

                int rowSelected = authTable.getSelectedRow();

                if (rowSelected != -1)
                {
                    tableModel.removeAuthorization(rowSelected);

                    if (tableModel.getRowCount() > 0)
                    {
                        int rowToSelect = rowSelected;

                        if (rowSelected >= tableModel.getRowCount())
                        {
                            rowToSelect = rowSelected - 1;
                        }
                        authTable.clearSelection();
                        authTable.setRowSelectionInterval(rowToSelect, rowToSelect);
                    }
                }
            }
        } else if (action.equals(ADD))
        {
            // If a table cell is being edited, we should accept the current value
            // and stop the editing before adding a new row.
            if (authTable.isEditing())
            {
                TableCellEditor cellEditor = authTable.getCellEditor(authTable.getEditingRow(), authTable.getEditingColumn());
                cellEditor.stopCellEditing();
            }

            tableModel.addAuthorization();

            // Highlight (select) the appropriate row.
            int rowToSelect = tableModel.getRowCount() - 1;
            authTable.clearSelection();
            authTable.setRowSelectionInterval(rowToSelect, rowToSelect);
        } else if (action.equals(LOAD))
        {
            try
            {
                JFileChooser chooser = FileDialoger.promptToOpenFile();

                if (chooser != null)
                {
                    Collection authorizations = AuthManager.loadAuthorizations(chooser.getSelectedFile().getAbsolutePath());
                    tableModel.addAuthorizations(authorizations);
                }
            } catch (IOException ex)
            {
                log.error("", ex);
            }
        } else if (action.equals(SAVE))
        {
            if (authTable.isEditing())
            {
                TableCellEditor cellEditor = authTable.getCellEditor(authTable.getEditingRow(), authTable.getEditingColumn());
                cellEditor.stopCellEditing();
            }
            try
            {
                JFileChooser chooser = FileDialoger.promptToSaveFile(null);

                if (chooser != null)
                {
                    AuthManager.saveAuthorizations(tableModel.getAuthorizations(), chooser.getSelectedFile().getAbsolutePath());
                }
            } catch (IOException ex)
            {
                log.error("", ex);
            }
        }
        setButtonState();
    }


    protected void initComponents()
    {
        super.initComponents();

        JPanel panel = GUIFactory.createPanel();
        panel.setLayout(new GridBagLayout());
        JMeterGridBagConstraints constraints;

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

        tableLabel = new JLabel(JMeterUtils.getResString("auths_stored"));
        tableLabel.setName("auths_stored");
        constraints = new JMeterGridBagConstraints();
        panel.add(tableLabel, constraints);
        authTable = new JTable(tableModel);
        authTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        authTable.getTableHeader().setReorderingAllowed(false);
        authTable.getSelectionModel().addListSelectionListener(this);
        authTable.addFocusListener(this);
        JScrollPane scroller = new JScrollPane(authTable);
        Dimension tableDim = scroller.getPreferredSize();
        tableDim.height = 140;
        tableDim.width = (int)(tableDim.width * 1.5);
        authTable.setPreferredScrollableViewportSize(tableDim);
        scroller.setColumnHeaderView(authTable.getTableHeader());
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


    public void focusGained(FocusEvent e)
    {
    }


    public void focusLost(FocusEvent e)
    {
//        if (authTable.isEditing()) {
//            TableCellEditor cellEditor = authTable.getCellEditor(authTable.getEditingRow(), authTable.getEditingColumn());
//            cellEditor.stopCellEditing();
//        }
    }

    public void localeChanged(LocaleChangeEvent event)
    {
        super.localeChanged(event);
        updateLocalizedStrings(new JComponent[]{tableLabel, addButton, removeButton, loadButton, saveButton});
        if (tableModel != null)
        {
            updateLocalizedTableHeaders(authTable, tableModel);
        }
    }


    public void valueChanged(ListSelectionEvent e)
    {
        setButtonState();
    }


    DefaultTableCellRenderer getRend()
    {
        return null;
    }

    private static class PasswordCellRenderer extends JPasswordField implements TableCellRenderer
    {

        protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);


        private Color foreground;
        private Color background;


        public PasswordCellRenderer()
        {
            super();
            setOpaque(true);
            setBorder(noFocusBorder);
        }


        public void updateUI()
        {
            super.updateUI();
            setForeground(null);
            setBackground(null);
        }


        public void setForeground(Color fg)
        {
            super.setForeground(fg);
            foreground = fg;
        }


        public void setBackground(Color bg)
        {
            super.setBackground(bg);
            background = bg;
        }


        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            if (isSelected)
            {
                System.out.println(row + " selected");
                super.setForeground(table.getSelectionForeground());
                super.setBackground(table.getSelectionBackground());
            } else
            {
                System.out.println(row + " not selected");
                if (foreground != null)
                {
                    super.setForeground(foreground);
                } else
                {
                    super.setForeground(table.getForeground());
                }
                if (background != null)
                {
                    super.setBackground(background);
                } else
                {
                    super.setBackground(table.getBackground());
                }
            }

            setFont(table.getFont());

            if (hasFocus)
            {
                setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
                if (table.isCellEditable(row, column))
                {
                    super.setForeground(UIManager.getColor("Table.focusCellForeground"));
                    super.setBackground(UIManager.getColor("Table.focusCellBackground"));
                }
            } else
            {
                setBorder(noFocusBorder);
            }

            System.out.println("Value = " + value);
            if (value == null)
            {
                setText("");
            } else
            {
                setText(value.toString());
            }

            return this;
        }
    }


    private static class AuthorizationTableModel extends AbstractTableModel
    {

        private List authorizations;


        public AuthorizationTableModel(List parameters)
        {
            this.authorizations = parameters;
        }


        public int getRowCount()
        {
            return authorizations.size();
        }


        public int getColumnCount()
        {
            return 3;
        }


        public Object getValueAt(int rowIndex, int columnIndex)
        {
            Authorization authorization = (Authorization)authorizations.get(rowIndex);

            switch (columnIndex)
            {
                case 0:
                    return authorization.getUrl();
                case 1:
                    return authorization.getUsername();
                case 2:
                    return authorization.getPassword();
                default:
                    return "";
            }
        }


        public void setValueAt(Object value, int rowIndex, int columnIndex)
        {
            Authorization authorization = (Authorization)authorizations.get(rowIndex);

            switch (columnIndex)
            {
                case 0:
                    authorization.setUrl((String)value);
                    break;
                case 1:
                    authorization.setUsername((String)value);
                    break;
                case 2:
                    authorization.setPassword((String)value);
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
                    return JMeterUtils.getResString("auth_base_url");
                case 1:
                    return JMeterUtils.getResString("username_column");
                case 2:
                    return JMeterUtils.getResString("password_column");
                default:
                    return "";
            }
        }


        public Class getColumnClass(int columnIndex)
        {
            return String.class;
        }


        public void addAuthorization()
        {
            authorizations.add(new Authorization());
            fireTableRowsInserted(authorizations.size() - 1, authorizations.size() - 1);
        }


        public void removeAuthorization(int index)
        {
            authorizations.remove(index);
            fireTableRowsDeleted(index, index);
        }


        public void addAuthorizations(Collection authorizations)
        {
            int startIndex = authorizations.size();

            this.authorizations.addAll(authorizations);
            fireTableRowsInserted(startIndex, authorizations.size() - 1);
        }


        public Collection getAuthorizations()
        {
            return authorizations;
        }
    }

}
