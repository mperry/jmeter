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
package org.apache.jmeter.assertions.gui;


import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import org.apache.jmeter.assertions.ResponseAssertion;
import org.apache.jmeter.gui.GUIFactory;
import org.apache.jmeter.gui.util.JMeterGridBagConstraints;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.util.LocaleChangeEvent;


/****************************************
 * Title: Jakarta-JMeter Description: Copyright: Copyright (c) 2001 Company:
 * Apache
 *
 * @author    Michael Stover
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @created   $Date$
 * @version   $Revision$
 ***************************************/
// todo: rename to ResponseAssertionGui

public class AssertionGui extends AbstractAssertionGui implements ActionListener, ListSelectionListener
{

    private static final String ADD = "add";
    private static final String REMOVE = "remove";
    private static final String FIELD_TEXT = "text";
    private static final String FIELD_URL = "url";
    private static final String MODE_CONTAINS = "contains";
    private static final String MODE_CONTAINS_NOT = "contains_not";
    private static final String MODE_MATCHES = "matches";
    private static final String MODE_MATCHES_NOT = "matches_not";

    private PatternTableModel tableModel;
    private JLabel testFieldLabel;
    private JRadioButton rbText;
    private JRadioButton rbURL;
    private JLabel modeLabel;
    private JRadioButton rbContains;
    private JRadioButton rbContainsNot;
    private JRadioButton rbMatch;
    private JRadioButton rbMatchNot;
    private JButton addButton;
    private JButton removeButton;
    private JTable patternTable;
    private JLabel tableLabel;


    public AssertionGui()
    {
    }


    public String getStaticLabel()
    {
        return "assertion_title";
    }


    public TestElement createTestElement()
    {
        ResponseAssertion el = new ResponseAssertion();
//		String[] testStrings = tableModel.getData().getColumn(COL_NAME);
//		for(int i = 0;i < testStrings.length;i++)
//		{
//			el.addTestString(testStrings[i]);
//		}
//		configureTestElement(el);
//		if(labelButton.isSelected())
//		{
//			el.setTestField(ResponseAssertion.SAMPLE_LABEL);
//		}
//		else
//		{
//			el.setTestField(ResponseAssertion.RESPONSE_DATA);
//		}
//		if(containsBox.isSelected())
//		{
//			el.setToContainsType();
//		}
//		else
//		{
//			el.setToMatchType();
//		}
//		if(notBox.isSelected())
//		{
//			el.setToNotType();
//		}
//		else
//		{
//			el.unsetNotType();
//		}
        return el;
    }


    public void configure(TestElement element)
    {
        super.configure(element);
        tableModel = new PatternTableModel((List)element.getProperty(ResponseAssertion.TEST_PATTERNS));
        patternTable.setModel(tableModel);

        DefaultCellEditor editor = new DefaultCellEditor(new JTextField());
        editor.setClickCountToStart(1);

        patternTable.getColumnModel().getColumn(0).setCellEditor(editor);
        setButtonState();

        int field = ((Integer)element.getProperty(ResponseAssertion.TEST_FIELD)).intValue();
        int mode = ((Integer)element.getProperty(ResponseAssertion.TEST_MODE)).intValue();

        if (field == ResponseAssertion.TEXT_RESPONSE) {
            rbText.setSelected(true);
        } else {
            rbURL.setSelected(true);
        }

        switch (mode) {
            case ResponseAssertion.MATCH:
                rbMatch.setSelected(true);
                break;
            case ResponseAssertion.MATCH_NOT:
                rbMatchNot.setSelected(true);
                break;
            case ResponseAssertion.CONTAINS:
                rbContains.setSelected(true);
                break;
            case ResponseAssertion.CONTAINS_NOT:
                rbContainsNot.setSelected(true);
                break;
            default:
        }
    }


    private void setButtonState()
    {
        addButton.setEnabled(true);
        removeButton.setEnabled(patternTable.getSelectedRow() != -1);
    }


    protected void initComponents()
    {
        super.initComponents();

        add(createModePanel());
        add(createPatternPanel());
    }


    private JPanel createModePanel()
    {
        JPanel modePanel = GUIFactory.createPanel();
        modePanel.setLayout(new GridBagLayout());
        JMeterGridBagConstraints constraints = new JMeterGridBagConstraints();
        constraints.insets = new Insets(1, 3, 1, 3);
        testFieldLabel = new JLabel(JMeterUtils.getResString("assertion_resp_field"));
        testFieldLabel.setName("assertion_resp_field");
        modePanel.add(testFieldLabel, constraints);
        rbText = new JRadioButton(JMeterUtils.getResString("assertion_text_resp"));
        rbText.setName("assertion_text_resp");
        rbText.addActionListener(this);
        rbText.setActionCommand(FIELD_TEXT);
        constraints = constraints.incrementX();
        modePanel.add(rbText, constraints);
        rbURL = new JRadioButton(JMeterUtils.getResString("assertion_url_samp"));
        rbURL.setName("assertion_url_samp");
        rbURL.addActionListener(this);
        rbURL.setActionCommand(FIELD_URL);
        constraints = constraints.incrementY();
        modePanel.add(rbURL, constraints);
        ButtonGroup protocolButtonGroup = new ButtonGroup();
        protocolButtonGroup.add(rbText);
        protocolButtonGroup.add(rbURL);

        modeLabel = new JLabel(JMeterUtils.getResString("assertion_pattern_match_rules"));
        modeLabel.setName("assertion_pattern_match_rules");
        constraints = constraints.nextRow();
        constraints.insets = new Insets(8, 3, 1, 3);
        modePanel.add(modeLabel, constraints);
        rbContains = new JRadioButton(JMeterUtils.getResString("assertion_contains"));
        rbContains.setName("assertion_contains");
        rbContains.addActionListener(this);
        rbContains.setActionCommand(MODE_CONTAINS);
        constraints = constraints.incrementX();
        modePanel.add(rbContains, constraints);
        rbContainsNot = new JRadioButton(JMeterUtils.getResString("assertion_contains_not"));
        rbContainsNot.setName("assertion_contains_not");
        rbContainsNot.addActionListener(this);
        rbContainsNot.setActionCommand(MODE_CONTAINS_NOT);
        constraints = constraints.incrementY();
        constraints.insets = new Insets(1, 3, 1, 3);
        modePanel.add(rbContainsNot, constraints);
        rbMatch = new JRadioButton(JMeterUtils.getResString("assertion_matches"));
        rbMatch.setName("assertion_matches");
        rbMatch.addActionListener(this);
        rbMatch.setActionCommand(MODE_MATCHES);
        constraints = constraints.incrementY();
        modePanel.add(rbMatch, constraints);
        rbMatchNot = new JRadioButton(JMeterUtils.getResString("assertion_matches_not"));
        rbMatchNot.setName("assertion_matches_not");
        rbMatchNot.addActionListener(this);
        rbMatchNot.setActionCommand(MODE_MATCHES_NOT);
        constraints = constraints.incrementY();
        modePanel.add(rbMatchNot, constraints);
        Component filler = Box.createHorizontalGlue();
        constraints = constraints.incrementX();
        constraints.fillHorizontal(1.0);
        modePanel.add(filler, constraints);

        ButtonGroup modeButtonGroup = new ButtonGroup();
        modeButtonGroup.add(rbContains);
        modeButtonGroup.add(rbContainsNot);
        modeButtonGroup.add(rbMatch);
        modeButtonGroup.add(rbMatchNot);

        return modePanel;
    }


    private JPanel createPatternPanel()
    {
        JPanel patternPanel = GUIFactory.createPanel();
        patternPanel.setLayout(new GridBagLayout());
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

        tableLabel = new JLabel(JMeterUtils.getResString("assertion_patterns_to_test"));
        tableLabel.setName("assertion_patterns_to_test");
        constraints = new JMeterGridBagConstraints();
        patternPanel.add(tableLabel, constraints);
        patternTable = new JTable(tableModel);
        patternTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patternTable.getTableHeader().setReorderingAllowed(false);
        patternTable.getSelectionModel().addListSelectionListener(this);

//        patternTable.setDefaultEditor(String.class, new TextAreaTableCellEditor());
//        patternTable.setDefaultRenderer(String.class, new TextAreaCellRenderer());

        JScrollPane scroller = new JScrollPane(patternTable);
        Dimension tableDim = scroller.getPreferredSize();
        tableDim.height = 140;
        tableDim.width = (int)(tableDim.width * 1.5);
        patternTable.setPreferredScrollableViewportSize(tableDim);
        scroller.setColumnHeaderView(patternTable.getTableHeader());
        constraints = constraints.nextRow();
        constraints.fillHorizontal(1.0);
        constraints.fillVertical(1.0);
        patternPanel.add(scroller, constraints);
        constraints = constraints.incrementX();
        constraints.fillNone();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        patternPanel.add(buttonPanel, constraints);

        return patternPanel;
    }


    public void actionPerformed(ActionEvent e)
    {
        String action = e.getActionCommand();

        if (action.equals(REMOVE)) {
            if (tableModel.getRowCount() > 0) {
                // If a table cell is being edited, we must cancel the editing before
                // deleting the row
                if (patternTable.isEditing()) {
                    TableCellEditor cellEditor = patternTable.getCellEditor(patternTable.getEditingRow(), patternTable.getEditingColumn());
                    cellEditor.cancelCellEditing();
                }

                int rowSelected = patternTable.getSelectedRow();

                if (rowSelected != -1) {
                    tableModel.removePattern(rowSelected);

                    if (tableModel.getRowCount() > 0) {
                        int rowToSelect = rowSelected;

                        if (rowSelected >= tableModel.getRowCount()) {
                            rowToSelect = rowSelected - 1;
                        }
                        patternTable.clearSelection();
                        patternTable.setRowSelectionInterval(rowToSelect, rowToSelect);
                    }
                }
            }
        } else if (action.equals(ADD)) {
            // If a table cell is being edited, we should accept the current value
            // and stop the editing before adding a new row.
            if (patternTable.isEditing()) {
                TableCellEditor cellEditor = patternTable.getCellEditor(patternTable.getEditingRow(), patternTable.getEditingColumn());
                cellEditor.stopCellEditing();
            }

            tableModel.addPattern();

            // Highlight (select) the appropriate row.
            int rowToSelect = tableModel.getRowCount() - 1;
            patternTable.clearSelection();
            patternTable.setRowSelectionInterval(rowToSelect, rowToSelect);
        } else {
            // radio button actions
            if (action.equals(FIELD_TEXT)) {
                getElement().setProperty(ResponseAssertion.TEST_FIELD, new Integer(ResponseAssertion.TEXT_RESPONSE));
            } else if (action.equals(FIELD_URL)) {
                getElement().setProperty(ResponseAssertion.TEST_FIELD, new Integer(ResponseAssertion.URL));
            } else if (action.equals(MODE_CONTAINS)) {
                getElement().setProperty(ResponseAssertion.TEST_MODE, new Integer(ResponseAssertion.CONTAINS));
            } else if (action.equals(MODE_CONTAINS_NOT)) {
                getElement().setProperty(ResponseAssertion.TEST_MODE, new Integer(ResponseAssertion.CONTAINS_NOT));
            } else if (action.equals(MODE_MATCHES)) {
                getElement().setProperty(ResponseAssertion.TEST_MODE, new Integer(ResponseAssertion.MATCH));
            } else if (action.equals(MODE_MATCHES_NOT)) {
                getElement().setProperty(ResponseAssertion.TEST_MODE, new Integer(ResponseAssertion.MATCH_NOT));
            }
        }
        setButtonState();
    }


    public void valueChanged(ListSelectionEvent e)
    {
        setButtonState();
    }


    public void localeChanged(LocaleChangeEvent event)
    {
        super.localeChanged(event);

        updateLocalizedStrings(new JComponent[]{addButton, removeButton, rbContains, rbContainsNot, rbMatch, rbMatchNot, tableLabel, testFieldLabel, modeLabel});
        updateLocalizedTableHeaders(patternTable, tableModel);
    }


    private static class PatternTableModel extends AbstractTableModel
    {

        private List patterns;


        public PatternTableModel(List patterns)
        {
            this.patterns = patterns;
        }


        public int getRowCount()
        {
            return patterns.size();
        }


        public int getColumnCount()
        {
            return 1;
        }


        public Object getValueAt(int rowIndex, int columnIndex)
        {
            return patterns.get(rowIndex);
        }


        public void setValueAt(Object value, int rowIndex, int columnIndex)
        {
            patterns.remove(rowIndex);
            patterns.add(rowIndex, value);
        }


        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            return true;
        }


        public String getColumnName(int column)
        {
            return JMeterUtils.getResString("pattern_column");
        }


        public Class getColumnClass(int columnIndex)
        {
            return String.class;
        }


        public void addPattern()
        {
            patterns.add("");
            fireTableRowsInserted(patterns.size() - 1, patterns.size() - 1);
        }


        public void removePattern(int index)
        {
            patterns.remove(index);
            fireTableRowsDeleted(index, index);
        }


        public Collection getPatterns()
        {
            return patterns;
        }
    }


}
