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
package org.apache.jmeter.config.gui;


import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import junit.framework.TestCase;

import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.gui.util.JMeterGridBagConstraints;
import org.apache.jmeter.testelement.*;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.util.LocaleChangeEvent;


/****************************************
 * Title: JMeter Description: Copyright: Copyright (c) 2000 Company: Apache
 *
 * @author    Michael Stover
 * @author    <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @created   March 13, 2001
 * @version   1.0
 ***************************************/

public class ArgumentsPanel extends AbstractConfigGui implements ActionListener, ListSelectionListener, FocusListener
{

    private static String ADD = "add";
    private static String REMOVE = "remove";

    private JPanel argumentsPanel;
    private JButton addButton;
    private JButton removeButton;
    private JTable argumentsTable;
    private ArgumentsTableModel tableModel;


    public ArgumentsPanel()
    {
        super(false);
    }


    // todo: remove when not used anymore
    public ArgumentsPanel(String label)
    {
        super(false);
    }


    public String getStaticLabel()
    {
        // this is never used as a standalone gui
        return "";
    }


    public NamedTestElement createTestElement()
    {
//        Data model = tableModel.getData();
        Arguments args = new Arguments();
//        model.reset();
//        while (model.next()) {
//            args.addArgument((String)model.getColumnValue(Arguments.COLUMN_NAMES[0]),
//                             model.getColumnValue(Arguments.COLUMN_NAMES[1]));
//        }
//        this.configureTestElement(args);
        return (NamedTestElement)args.clone();
    }


    public void configure(TestElementConfiguration config)
    {
        super.configure(config);

        tableModel = createTableModel(config);
        argumentsTable.setModel(tableModel);

        DefaultCellEditor editor = new DefaultCellEditor(new JTextField());
        editor.setClickCountToStart(1);

        for (int i = 0; i < tableModel.getColumnCount(); i++)
        {
            if (tableModel.getColumnClass(i) == String.class)
            {
                argumentsTable.getColumnModel().getColumn(i).setCellEditor(editor);
            }
        }
        setButtonState();
    }


    protected ArgumentsTableModel createTableModel(TestElementConfiguration element)
    {
        // todo:
        return new ArgumentsTableModel(new ArrayList());
//        return new ArgumentsTableModel((List)element.getProperty(Arguments.ARGUMENTS));
    }


    public void actionPerformed(ActionEvent e)
    {
        String action = e.getActionCommand();
        if (action.equals(REMOVE))
        {
            deleteArgument();
        } else if (action.equals(ADD))
        {
            addArgument();
        }
    }


    protected void deleteArgument()
    {
        // If a table cell is being edited, we must cancel the editing before
        // deleting the row
        if (argumentsTable.isEditing())
        {
            TableCellEditor cellEditor = argumentsTable.getCellEditor(argumentsTable.getEditingRow(), argumentsTable.getEditingColumn());
            cellEditor.cancelCellEditing();
        }

        int rowSelected = argumentsTable.getSelectedRow();
        if (rowSelected >= 0)
        {
            tableModel.removeArgument(rowSelected);

            if (tableModel.getRowCount() > 0)
            {
                int rowToSelect = rowSelected;

                if (rowSelected >= tableModel.getRowCount())
                {
                    rowToSelect = rowSelected - 1;
                }
                argumentsTable.clearSelection();
                argumentsTable.setRowSelectionInterval(rowToSelect, rowToSelect);
                argumentsTable.setColumnSelectionInterval(0, 0);
            }
            setButtonState();
        }
    }


    protected void addArgument()
    {
        // If a table cell is being edited, we should accept the current value
        // and stop the editing before adding a new row.
        if (argumentsTable.isEditing())
        {
            TableCellEditor cellEditor = argumentsTable.getCellEditor(argumentsTable.getEditingRow(), argumentsTable.getEditingColumn());
            cellEditor.stopCellEditing();
        }

        tableModel.addArgument();
        // Highlight (select) the appropriate row.
        int rowToSelect = tableModel.getRowCount() - 1;
        argumentsTable.clearSelection();
        argumentsTable.setRowSelectionInterval(rowToSelect, rowToSelect);
        argumentsTable.setColumnSelectionInterval(0, 0);
        setButtonState();
    }


    protected void initComponents()
    {
        super.initComponents();

        argumentsPanel = new JPanel(); // GUIFactory.createPanel();
        argumentsPanel.setLayout(new GridBagLayout());
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

        argumentsTable = new JTable(tableModel);
        argumentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        argumentsTable.getTableHeader().setReorderingAllowed(false);
        argumentsTable.getSelectionModel().addListSelectionListener(this);
        argumentsTable.addFocusListener(this);
        JScrollPane scroller = new JScrollPane(argumentsTable);
        Dimension tableDim = scroller.getPreferredSize();
        tableDim.height = 140;
        tableDim.width = (int)(tableDim.width * 1.5);
        argumentsTable.setPreferredScrollableViewportSize(tableDim);
        scroller.setColumnHeaderView(argumentsTable.getTableHeader());
        constraints = constraints.nextRow();
        constraints.fillHorizontal(1.0);
        constraints.fillVertical(1.0);
        argumentsPanel.add(scroller, constraints);
        constraints = constraints.incrementX();
        constraints.fillNone();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        argumentsPanel.add(buttonPanel, constraints);

        add(argumentsPanel);
    }


    public void focusGained(FocusEvent e)
    {
    }


    public void focusLost(FocusEvent e)
    {
//        if (argumentsTable.isEditing()) {
//            TableCellEditor cellEditor = argumentsTable.getCellEditor(argumentsTable.getEditingRow(), argumentsTable.getEditingColumn());
//            cellEditor.stopCellEditing();
//        }
    }


    public void localeChanged(LocaleChangeEvent event)
    {
        super.localeChanged(event);
        updateLocalizedStrings(new JComponent[]{addButton, removeButton});
        if (tableModel != null)
        {
            updateLocalizedTableHeaders(argumentsTable, tableModel);
        }
    }


    // ListSelectionListener methods
    public void valueChanged(ListSelectionEvent e)
    {
        setButtonState();
    }


    protected void setButtonState()
    {
        addButton.setEnabled(true);
        removeButton.setEnabled(argumentsTable.getSelectedRow() != -1);
    }


    protected static class ArgumentsTableModel extends DefaultTableModel
    {

        private List arguments;


        public ArgumentsTableModel(List arguments)
        {
            this.arguments = arguments;
        }


        protected List getArguments()
        {
            return arguments;
        }


        public int getRowCount()
        {
            if (arguments == null)
            {
                return 0;
            }
            return arguments.size();
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
            Argument argument = (Argument)arguments.get(row);

            switch (column)
            {
                case 0:
                    return argument.getName();
                case 1:
                    return argument.getValue();
                default:
                    return null;
            }
        }


        public void setValueAt(Object value, int row, int column)
        {
            Argument argument = (Argument)arguments.get(row);

            switch (column)
            {
                case 0:
                    argument.setName((String)value);
                    break;
                case 1:
                    argument.setValue((String)value);
                    break;
                default:

            }
        }


        public void addArgument()
        {
            arguments.add(new Argument());
            fireTableRowsInserted(arguments.size() - 1, arguments.size() - 1);
        }


        public void removeArgument(int row)
        {
            arguments.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }


    // todo: obsolete?
    public static class Test extends TestCase
    {

        public Test(String name)
        {
            super(name);
        }


        public void testArgumentCreation() throws Exception
        {
            ArgumentsPanel gui = new ArgumentsPanel();
            gui.tableModel.addArgument();
            gui.tableModel.setValueAt("howdy", 0, 0);
            gui.tableModel.setValueAt("doody", 0, 1);
            assertEquals("=", ((Argument)((Arguments)gui.createTestElement()).getArguments().get(0)).getMetaData());
        }
    }
}
