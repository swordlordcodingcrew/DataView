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

package org.somap.dataview;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.conf.Configuration;
import org.apache.cayenne.conf.DefaultConfiguration;
import org.apache.cayenne.util.ResourceLocator;
import org.apache.cayenne.util.Util;
import org.apache.commons.collections.Predicate;

public class ConfigurationHelper 
{
	public static boolean loadDataView(DataView dataView) throws IOException 
	{
        return loadDataView(dataView, Configuration.ACCEPT_ALL_DATAVIEWS);
    }

    public static boolean loadDataView(DataView dataView, Predicate dataViewNameFilter) throws IOException 
    {
        if (dataView == null) 
        {
            throw new IllegalArgumentException("DataView cannot be null.");
        }

        Configuration.initializeSharedConfiguration();
		Configuration configuration = Configuration.getSharedConfiguration();
		
		ResourceLocator locator = new ResourceLocator();
        locator.setSkipAbsolutePath(true);
        locator.setSkipClasspath(false);
        locator.setSkipCurrentDirectory(true);
        locator.setSkipHomeDirectory(true);

        // add the DefaultCnfigurator's subclass' package as additional path.
        locator.addClassPath(Util.getPackagePath(DefaultConfiguration.class.getName()));
        
		Map dataViewLocations = configuration.getDataViewLocations();
        
        if (dataViewLocations.size() == 0 || dataViewLocations.size() > 512) 
        {
            return false;
        }

        if (dataViewNameFilter == null)
        {
            dataViewNameFilter = Configuration.ACCEPT_ALL_DATAVIEWS;
        }
        
        List viewXMLSources = new ArrayList(dataViewLocations.size());
        int index = 0;
        for (Iterator i = dataViewLocations.entrySet().iterator(); i.hasNext(); index++) 
        {
            Map.Entry entry = (Map.Entry) i.next();
            String name = (String) entry.getKey();
            if (!dataViewNameFilter.evaluate(name))
                continue;
            String location = (String) entry.getValue();
            InputStream in = locator.findResourceStream(location);
            if (in != null)
                viewXMLSources.add(in);
        }

        if (viewXMLSources.isEmpty())
            return false;

        dataView.load((InputStream[]) viewXMLSources.toArray(new InputStream[viewXMLSources.size()]));
        return true;
    }
}
