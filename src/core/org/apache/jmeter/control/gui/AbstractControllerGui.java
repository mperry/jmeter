package org.apache.jmeter.control.gui;


import java.util.Arrays;
import java.util.Collection;
import java.awt.event.*;

import javax.swing.JPopupMenu;

import org.apache.jmeter.gui.AbstractJMeterGuiComponent;
import org.apache.jmeter.gui.action.AddElement;
import org.apache.jmeter.gui.action.Actions;
import org.apache.jmeter.gui.util.MenuFactory;
import org.apache.jmeter.testelement.NamedTestElement;
import org.apache.jmeter.util.JMeterUtils;


/****************************************
 * Title: JMeter Description: Copyright: Copyright (c) 2000 Company: Apache
 *
 * @author    Michael Stover
 * @author <a href="mailto:oliver@tuxerra.com">Oliver Rossmueller</a>
 * @created   $Date$
 * @version   1.0
 ***************************************/

public abstract class AbstractControllerGui extends AbstractJMeterGuiComponent
{

    /****************************************
     * !ToDoo (Method description)
     *
     *@return   !ToDo (Return description)
     ***************************************/
    public Collection getMenuCategories()
    {
        return Arrays.asList(new String[]{MenuFactory.CONTROLLERS});
    }

    public JPopupMenu createPopupMenu()
    {
        JPopupMenu pop = new JPopupMenu();

        pop.add(MenuFactory.makeMenus(new String[]{MenuFactory.CONTROLLERS,
                                                   MenuFactory.SAMPLERS, MenuFactory.CONFIG_ELEMENTS,
                                                   MenuFactory.MODIFIERS, MenuFactory.RESPONSE_BASED_MODIFIERS,
                                                   MenuFactory.TIMERS,
                                                   MenuFactory.LISTENERS},
                                      JMeterUtils.getResString("Add"),
                                      Actions.addElement));
        pop.add(MenuFactory.makeMenus(new String[]{MenuFactory.CONTROLLERS},
                                      JMeterUtils.getResString("insert_parent"), "Add Parent"));
//		MenuFactory.addEditMenu(pop, true);
//		MenuFactory.addFileMenu(pop);
        return pop;
    }

}
