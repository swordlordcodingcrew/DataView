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

import java.util.EventListener;

public class DataObjectChangeEvent extends DispatchableEvent {
  public static final int DATAOBJECT_ADDED         = 1;
  public static final int DATAOBJECT_REMOVED       = 2;
  public static final int DATAOBJECT_CHANGED       = 3;
  public static final int DATAOBJECTS_CHANGED      = 4;

  private int id;
  private int affectedDataObjectIndex;

  public DataObjectChangeEvent(Object source, int id) {
    this(source, id, -1);
  }

  public DataObjectChangeEvent(Object source, int id, int affectedDataObjectIndex) {
    super(source);
    this.id = id;
    this.affectedDataObjectIndex = affectedDataObjectIndex;
  }

  public void dispatch(EventListener listener) {
    ((DataObjectChangeListener)listener).dataChanged(this);
  }


  public boolean isMultiObjectChange() {
    return affectedDataObjectIndex == -1;
  }

  public int getAffectedDataObjectIndex() {
    return affectedDataObjectIndex;
  }

  public final int    getId() {
    return id;
  }

  public String toString() {
    return super.toString()+" "+id;
  }
}
