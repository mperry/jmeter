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
package org.apache.jmeter.modifiers.gui;


import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

import org.apache.jmeter.config.gui.AbstractConfigGui;
import org.apache.jmeter.gui.GUIFactory;
import org.apache.jmeter.gui.util.JMeterGridBagConstraints;
import org.apache.jmeter.modifiers.UserParameters;
import org.apache.jmeter.testelement.*;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.util.LocaleChangeEvent;


/**
 * @author Administrator
 * @author  <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 *
 */
public class UserParametersGui extends AbstractConfigGui implements TableColumnModelListener, ListSelectionListener, FocusListener
{

    private ParameterTableModel tableModel;
    private ParameterJTable parameterTable;
    private JButton removeParameterButton;
    private JButton addParameterButton;
    private JButton addValueButton;
    private JButton removeValueButton;
    private JLabel tableLabel;


    public UserParametersGui()
    {
    }


    /**
     * @see org.apache.jmeter.gui.JMeterGUIComponent#getStaticLabel()
     */
    public String getStaticLabel()
    {
        return "user_parameters_title";
    }


    public void configure(TestElementConfiguration config)
    {
        super.configure(config);

// todo:        tableModel = new ParameterTableModel((List)config.getPropertyValue(UserParameters.PARAMETERS));
        tableModel = new ParameterTableModel(new ArrayList());
        parameterTable.setModel(tableModel);
        updateColumns();
        setButtonState();
//		tableModel.clearData();
//		UserParameters params = (UserParameters)el;
//		List names = params.getNames();
//		List threadValues = params.getThreadLists();
//		tableModel.setColumnData(0,names);
//		Iterator iter = threadValues.iterator();
//		if(iter.hasNext())
//		{
//			tableModel.setColumnData(1,(List)iter.next());
//		}
//		int count = 2;
//		while(iter.hasNext())
//		{
//			String colName = THREAD_COLUMNS+"_"+count;
//			tableModel.addNewColumn(colName,String.class);
//			tableModel.setColumnData(count,(List)iter.next());
//			count++;
//		}
    }


    /**
     * @see org.apache.jmeter.gui.JMeterGUIComponent#createTestElement()
     */
    public NamedTestElement createTestElement()
    {
        UserParameters params = new UserParameters();
//        params.setNames(tableModel.getColumnData(JMeterUtils.getResString("name")));
//        List threadLists = new LinkedList();
//        for (int x = 1; x < tableModel.getColumnCount(); x++) {
//            threadLists.add(tableModel.getColumnData(THREAD_COLUMNS + "_" + x));
//        }
//        params.setThreadLists(threadLists);
//        super.configureTestElement(params);
        return params;
    }


    protected void initComponents()
    {
        super.initComponents();

        add(initParameterPanel());
    }


    private JPanel initParameterPanel()
    {
        JPanel parameterPanel = GUIFactory.createPanel();
        parameterPanel.setLayout(new GridBagLayout());
        JMeterGridBagConstraints constraints = new JMeterGridBagConstraints();

        JPanel valueButtonPanel = new JPanel();
        valueButtonPanel.setLayout(new GridBagLayout());
        constraints = new JMeterGridBagConstraints();
        constraints.fillHorizontal(1.0);
        addParameterButton = new JButton(JMeterUtils.getResString("add_parameter"));
        addParameterButton.addActionListener(new AddParameterAction());
        addParameterButton.setName("add_parameter");
        valueButtonPanel.add(addParameterButton, constraints);
        removeParameterButton = new JButton(JMeterUtils.getResString("delete_parameter"));
        removeParameterButton.setName("delete_parameter");
        removeParameterButton.addActionListener(new RemoveParameterAction());
        constraints = constraints.nextRow();
        valueButtonPanel.add(removeParameterButton, constraints);
        addValueButton = new JButton(JMeterUtils.getResString("add_value"));
        addValueButton.setName("add_value");
        addValueButton.addActionListener(new AddValueAction());
        constraints = constraints.nextRow();
        valueButtonPanel.add(addValueButton, constraints);
        removeValueButton = new JButton(JMeterUtils.getResString("delete_value"));
        removeValueButton.setName("delete_value");
        removeValueButton.addActionListener(new RemoveValueAction());
        constraints = constraints.nextRow();
        valueButtonPanel.add(removeValueButton, constraints);

        constraints = new JMeterGridBagConstraints();
        tableLabel = new JLabel(JMeterUtils.getResString("user_parameters_table"));
        tableLabel.setName("user_parameters_table");
        parameterPanel.add(tableLabel, constraints);
        parameterTable = new ParameterJTable(tableModel);
        parameterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        parameterTable.setDefaultRenderer(String.class, new ParameterTableCellRenderer());
        parameterTable.getTableHeader().setReorderingAllowed(false);
        parameterTable.getColumnModel().addColumnModelListener(this);
        parameterTable.getSelectionModel().addListSelectionListener(this);
        parameterTable.addFocusListener(this);
        JScrollPane scroller = new JScrollPane(parameterTable);
        Dimension tableDim = scroller.getPreferredSize();
        tableDim.height = 140;
        tableDim.width = (int)(tableDim.width * 1.5);
        parameterTable.setPreferredScrollableViewportSize(tableDim);
        scroller.setColumnHeaderView(parameterTable.getTableHeader());
        constraints = constraints.nextRow();
        constraints.fillHorizontal(1.0);
        constraints.fillVertical(1.0);
        parameterPanel.add(scroller, constraints);
        constraints = constraints.incrementX();
        constraints.fillNone();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        parameterPanel.add(valueButtonPanel, constraints);

        return parameterPanel;
    }


    public void focusGained(FocusEvent e)
    {
    }


    public void focusLost(FocusEvent e)
    {
        try
        {
            parameterTable.getCellEditor().stopCellEditing();
        } catch (RuntimeException err)
        {
        }
    }


    public void localeChanged(LocaleChangeEvent event)
    {
        super.localeChanged(event);
        updateLocalizedStrings(new JComponent[]{addParameterButton, removeParameterButton, addValueButton, removeValueButton, tableLabel});
        if (tableModel != null)
        {
            updateLocalizedTableHeaders(parameterTable, tableModel);
        }
    }


    // TableModelListener methods
    public void tableChanged(TableModelEvent e)
    {
        if (e.getFirstRow() == TableModelEvent.HEADER_ROW)
        {
            // structure has changed - recalculate column sizes
            updateColumns();

        }
    }


    // TableColumnModelListener methods
    public void columnAdded(TableColumnModelEvent e)
    {
        updateColumns();
    }


    public void columnRemoved(TableColumnModelEvent e)
    {
        updateColumns();
    }


    public void columnMoved(TableColumnModelEvent e)
    {
    }


    public void columnMarginChanged(ChangeEvent e)
    {
        updateColumns();
    }


    public void columnSelectionChanged(ListSelectionEvent e)
    {
        setButtonState();
    }


    // ListSelectionListener method
    public void valueChanged(ListSelectionEvent e)
    {
        setButtonState();
    }


    private void setButtonState()
    {
        int col = parameterTable.getSelectedColumn();
        int row = parameterTable.getSelectedRow();

        if (row == -1)
        {
            removeParameterButton.setEnabled(false);
            addValueButton.setEnabled(false);
            removeValueButton.setEnabled(false);
        } else
        {
            removeParameterButton.setEnabled(true);
            addValueButton.setEnabled(col <= tableModel.getValueCount(row) + 1);
            removeValueButton.setEnabled(col != 0 && tableModel.getValueCount(row) > 1 && col <= tableModel.getValueCount(row));
        }
    }


    private void updateColumns()
    {
        setCellEditors();
        calculateColumnWidths();
    }


    private void calculateColumnWidths()
    {
        TableColumnModel columns = parameterTable.getColumnModel();

        for (int i = 0; i < columns.getColumnCount(); i++)
        {
            TableColumn col = columns.getColumn(i);
            col.setMinWidth(Math.max(col.getMinWidth(), 100));
            col.setPreferredWidth(Math.max(col.getPreferredWidth(), 100));
        }
    }


    private void setCellEditors()
    {
        TableColumnModel columnModel = parameterTable.getColumnModel();
        DefaultCellEditor editor = new DefaultCellEditor(new JTextField());
        editor.setClickCountToStart(1);

        for (int i = 0; i < tableModel.getColumnCount(); i++)
        {
            if (i < columnModel.getColumnCount() && tableModel.getColumnClass(i) == String.class)
            {
                columnModel.getColumn(i).setCellEditor(editor);
            }
        }
    }

    private static class ParameterTableCellRenderer extends JLabel implements TableCellRenderer
    {

        protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);


        public ParameterTableCellRenderer()
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


        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            if (isSelected)
            {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else if (!table.isCellEditable(row, column))
            {
                setBackground(Color.lightGray);
            } else
            {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }

            setFont(table.getFont());

            if (hasFocus)
            {
                setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
                if (table.isCellEditable(row, column))
                {
                    setForeground(UIManager.getColor("Table.focusCellForeground"));
                    setBackground(UIManager.getColor("Table.focusCellBackground"));
                }
            } else
            {
                setBorder(noFocusBorder);
            }

            if (value == null)
            {
                setText("");
            } else
            {
                setText(value.toString());
            }

            return this;
        }


        public boolean isOpaque()
        {
            Color back = getBackground();
            Component parent = getParent();
            if (parent != null)
            {
                parent = parent.getParent();
            }
            // parent should now be the JTable.
            boolean colorMatch = (back != null) && (parent != null) && back.equals(parent.getBackground()) && parent.isOpaque();
            return !colorMatch && super.isOpaque();
        }


        public void validate()
        {
        }


        public void revalidate()
        {
        }


        public void repaint(long tm, int x, int y, int width, int height)
        {
        }


        public void repaint(Rectangle r)
        {
        }


        protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
        {
            // Strings get interned...
            if (propertyName == "text")
            {
                super.firePropertyChange(propertyName, oldValue, newValue);
            }
        }


        public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue)
        {
        }
    }


    private class AddParameterAction implements ActionListener
    {

        public void actionPerformed(ActionEvent e)
        {
            if (parameterTable.isEditing())
            {
                TableCellEditor cellEditor =
                    parameterTable.getCellEditor(
                        parameterTable.getEditingRow(),
                        parameterTable.getEditingColumn());
                cellEditor.stopCellEditing();
            }

            tableModel.addParameter();

            int rowToSelect = tableModel.getRowCount() - 1;

            parameterTable.clearSelection();
            parameterTable.setRowSelectionInterval(rowToSelect, rowToSelect);
            parameterTable.setColumnSelectionInterval(0, 0);
            parameterTable.scrollToVisible(rowToSelect, 0);
            setButtonState();
        }
    }

    private class RemoveParameterAction implements ActionListener
    {

        public void actionPerformed(ActionEvent e)
        {

            if (parameterTable.isEditing())
            {

                TableCellEditor cellEditor =
                    parameterTable.getCellEditor(
                        parameterTable.getEditingRow(),
                        parameterTable.getEditingColumn());
                cellEditor.stopCellEditing();
            }

            int row = parameterTable.getSelectedRow();

            if (row != -1)
            {
                // to make the following work without IndexOutOfBounds
                parameterTable.clearSelection();
                tableModel.removeParameter(row);

                int rowToSelect = Math.min(row, tableModel.getRowCount() - 1);

                if (rowToSelect != -1)
                {
                    parameterTable.clearSelection();
                    parameterTable.setRowSelectionInterval(rowToSelect, rowToSelect);
                    parameterTable.setColumnSelectionInterval(0, 0);
                }
            }
            setButtonState();
        }
    }


    private class AddValueAction implements ActionListener
    {

        public void actionPerformed(ActionEvent e)
        {

            if (parameterTable.isEditing())
            {
                TableCellEditor cellEditor =
                    parameterTable.getCellEditor(
                        parameterTable.getEditingRow(),
                        parameterTable.getEditingColumn());
                cellEditor.stopCellEditing();
            }

            int row = parameterTable.getSelectedRow();
            int col = parameterTable.getSelectedColumn();

            if (row != -1)
            {
                tableModel.addValue(row, col);
                parameterTable.clearSelection();
                parameterTable.setRowSelectionInterval(row, row);
                parameterTable.setColumnSelectionInterval(col, col);
                setButtonState();
            }
        }
    }


    private class RemoveValueAction implements ActionListener
    {

        public void actionPerformed(ActionEvent e)
        {

            if (parameterTable.isEditing())
            {
                TableCellEditor cellEditor =
                    parameterTable.getCellEditor(
                        parameterTable.getEditingRow(),
                        parameterTable.getEditingColumn());
                cellEditor.stopCellEditing();
            }

            int row = parameterTable.getSelectedRow();
            int col = parameterTable.getSelectedColumn();

            if (row != -1 && col > 0)
            {
                tableModel.removeValue(row, col);
                parameterTable.clearSelection();
                parameterTable.setRowSelectionInterval(row, row);
                col = Math.min(col, tableModel.getColumnCount() - 1);
                parameterTable.setColumnSelectionInterval(col, col);
                setButtonState();
            }
        }
    }

    private class DeleteRowAction implements ActionListener
    {

        public void actionPerformed(ActionEvent e)
        {
            if (parameterTable.isEditing())
            {
                TableCellEditor cellEditor =
                    parameterTable.getCellEditor(
                        parameterTable.getEditingRow(),
                        parameterTable.getEditingColumn());
                cellEditor.cancelCellEditing();
            }

            int rowSelected = parameterTable.getSelectedRow();
            if (rowSelected >= 0)
            {
//                tableModel.removeRow(rowSelected);
//                tableModel.fireTableDataChanged();

                // Disable DELETE if there are no rows in the table to delete.
                if (tableModel.getRowCount() == 0)
                {
//                    deleteRowButton.setEnabled(false);
                }

                // Table still contains one or more rows, so highlight (select)
                // the appropriate one.
                else
                {
                    int rowToSelect = rowSelected;

                    if (rowSelected >= tableModel.getRowCount())
                    {
                        rowToSelect = rowSelected - 1;
                    }

                    parameterTable.setRowSelectionInterval(
                        rowToSelect,
                        rowToSelect);
                }
            }
        }
    }

    private class DeleteColumnAction implements ActionListener
    {

        public void actionPerformed(ActionEvent e)
        {
            if (parameterTable.isEditing())
            {
                TableCellEditor cellEditor =
                    parameterTable.getCellEditor(
                        parameterTable.getEditingRow(),
                        parameterTable.getEditingColumn());
                cellEditor.cancelCellEditing();
            }

            int colSelected = parameterTable.getSelectedColumn();
            if (colSelected == 0 || colSelected == 1)
            {
                JOptionPane.showMessageDialog(null,
                                              JMeterUtils.getResString("column_delete_disallowed"), "Error",
                                              JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (colSelected >= 0)
            {
//                tableModel.removeColumn(colSelected);
//                tableModel.fireTableDataChanged();

                // Disable DELETE if there are no rows in the table to delete.
                if (tableModel.getColumnCount() == 0)
                {
//                    deleteColumnButton.setEnabled(false);
                }

                // Table still contains one or more rows, so highlight (select)
                // the appropriate one.
                else
                {

                    if (colSelected >= tableModel.getColumnCount())
                    {
                        colSelected = colSelected - 1;
                    }

                    parameterTable.setColumnSelectionInterval(
                        colSelected,
                        colSelected);
                }
            }
        }
    }


    private static class ParameterTableModel extends AbstractTableModel
    {

        private List parameters;


        public ParameterTableModel(List parameters)
        {
            this.parameters = parameters;
        }


        public int getRowCount()
        {
            return parameters.size();
        }


        public int getColumnCount()
        {
            int count = 1;
            Iterator iterator = parameters.iterator();

            while (iterator.hasNext())
            {
                UserParameters.Parameter multivaluedParameter = (UserParameters.Parameter)iterator.next();

                count = Math.max(count, multivaluedParameter.getValueCount());
            }

            return count + 1;
        }


        public Object getValueAt(int rowIndex, int columnIndex)
        {
            UserParameters.Parameter parameter = (UserParameters.Parameter)parameters.get(rowIndex);

            switch (columnIndex)
            {
                case 0:
                    return parameter.getName();
                default:
                    if (columnIndex > parameter.getValueCount())
                    {
                        return "";
                    } else
                    {
                        return parameter.getValue(columnIndex - 1);
                    }
            }
        }


        public void setValueAt(Object value, int rowIndex, int columnIndex)
        {
            UserParameters.Parameter parameter = (UserParameters.Parameter)parameters.get(rowIndex);

            switch (columnIndex)
            {
                case 0:
                    parameter.setName((String)value);
                    break;
                default:
                    parameter.setValue(columnIndex - 1, (String)value);
            }
        }


        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            UserParameters.Parameter parameter = (UserParameters.Parameter)parameters.get(rowIndex);

            return parameter.getValueCount() >= columnIndex;
        }


        public String getColumnName(int column)
        {
            if (column == 0)
            {
                return JMeterUtils.getResString("name_column");
            } else
            {
                return JMeterUtils.getResString("value_column") + (column);
            }
        }


        public Class getColumnClass(int columnIndex)
        {
            return String.class;
        }


        public void addParameter()
        {
            parameters.add(new UserParameters.Parameter());
            fireTableRowsInserted(parameters.size() - 1, parameters.size() - 1);
        }


        public int getValueCount(int rowIndex)
        {
            UserParameters.Parameter parameter = (UserParameters.Parameter)parameters.get(rowIndex);
            return parameter.getValueCount();
        }


        public void removeParameter(int index)
        {
            UserParameters.Parameter parameter = (UserParameters.Parameter)parameters.get(index);
            int size = parameter.getValueCount();

            parameters.remove(index);

            if (size > getColumnCount() - 1)
            {
                fireTableStructureChanged();
            }

            fireTableRowsDeleted(index, index);
        }


        public void addValue(int rowIndex, int valueIndex)
        {
            UserParameters.Parameter parameter = (UserParameters.Parameter)parameters.get(rowIndex);
            int index = valueIndex - 1;
            int max = getColumnCount() - 1;

            if (valueIndex == 0)
            {
                index = parameter.getValueCount();
            }
            parameter.addValue(index);
            if (parameter.getValueCount() > max)
            {
                fireTableStructureChanged();
                fireTableRowsUpdated(rowIndex, rowIndex);
            } else
            {
                fireTableCellUpdated(rowIndex, valueIndex + 1);
            }


        }


        public void removeValue(int rowIndex, int valueIndex)
        {
            UserParameters.Parameter parameter = (UserParameters.Parameter)parameters.get(rowIndex);

            parameter.removeValue(valueIndex - 1);
            if (parameter.getValueCount() == getColumnCount() - 1)
            {
                fireTableStructureChanged();
                fireTableRowsUpdated(rowIndex, rowIndex);
            } else
            {
                for (int i = 0; i <= parameter.getValueCount(); i++)
                {
                    fireTableCellUpdated(rowIndex, i + 1);
                }
            }
        }
    }


    private static class ParameterJTable extends JTable
    {

        public ParameterJTable(TableModel dm)
        {
            super(dm);
        }


        public boolean getScrollableTracksViewportWidth()
        {
            TableColumnModel columns = getColumnModel();
            int preferedWidth = 0;

            for (int i = 0; i < columns.getColumnCount(); i++)
            {
                TableColumn col = columns.getColumn(i);

                preferedWidth += col.getPreferredWidth();
            }

            return preferedWidth <= getParent().getWidth();
        }

        public void scrollToVisible(int rowIndex, int vColIndex)
        {
            if (!(getParent() instanceof JViewport))
            {
                return;
            }
            JViewport viewport = (JViewport)getParent();
            Rectangle cellRect = getCellRect(rowIndex, vColIndex, true);
            Point viewportPosition = viewport.getViewPosition();

            // Translate the cell location so that it is relative
            // to the view, assuming the northwest corner of the
            // view is (0,0)
            cellRect.setLocation(cellRect.x - viewportPosition.x, cellRect.y - viewportPosition.y);

            // Scroll the area into view
            viewport.scrollRectToVisible(cellRect);
        }
    }
}
