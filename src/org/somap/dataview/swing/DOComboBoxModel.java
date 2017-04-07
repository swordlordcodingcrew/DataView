/* ====================================================================
 * 
 * Copyright 2017 by SwordLord - the coding crew
 *
 * Parts of this software are based on sourcecode from SOMAP.org which is
 * Copyright (c) 2004-2008, SOMAP.org and individual authors.
 * 
 * Parts of this software are based on sourcecode from Cayenne which is
 * Copyright (c) 2002-2005, Andrei (Andrus) Adamchik and individual authors.
 * 
 * ====================================================================
 * 
 * SwordLord licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 * 
 * ====================================================================
 */

package org.somap.dataview.swing;

import javax.swing.ComboBoxModel;

import org.apache.cayenne.DataObject;
import org.somap.dataview.LookupCache;

/**
 * A ComboBoxModel to display DataObjects.
 * 
 * @since 1.1
 * @author Andriy Shapochka
 */
public class DOComboBoxModel extends DOListModel implements ComboBoxModel {

    protected DataObject selectedObject;

    public DOComboBoxModel() {
    }

    public void setSelectedDataObject(DataObject dataObject) {
        if ((selectedObject != null && !selectedObject.equals(dataObject))
                || selectedObject == null
                && dataObject != null) {
            selectedObject = dataObject;
            fireContentsChanged(this, -1, -1);
        }
    }

    public DataObject getSelectedDataObject() {
        return selectedObject;
    }

    public void setSelectedItem(Object selectedValue) {
        if (viewField == null) {
            if (selectedValue instanceof DataObject)
                setSelectedDataObject((DataObject) selectedValue);
            else
                setSelectedDataObject(null);
        }
        else {
            LookupCache cache = viewField.getOwner().getOwner().getLookupCache();
            setSelectedDataObject(cache.getDataObject(viewField, selectedValue));
        }
    }

    public Object getSelectedItem() {
        if (viewField == null)
            return getSelectedDataObject();
        return viewField.getValue(getSelectedDataObject());
    }
}
