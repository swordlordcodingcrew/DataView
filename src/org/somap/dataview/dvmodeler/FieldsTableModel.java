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

package org.somap.dataview.dvmodeler;

import javax.swing.table.*;

import org.somap.dataview.CalcTypeEnum;

/**
 *
 * @author Nataliya Kholodna
 * @version 1.0
 */
@SuppressWarnings("serial")
public class FieldsTableModel extends AbstractTableModel 
{
  private ObjEntityView objEntityView;
  private String[] columnNames= {"Name",
                   "Type",
                   "CalcType",
                   "Attribute/Relationship",
                   "Lookup"};

  public FieldsTableModel(ObjEntityView objEntityView) {
    this.objEntityView = objEntityView;
  }

  public FieldsTableModel() {
  }

  public void setObjEntityView(ObjEntityView objEntityView){
    this.objEntityView = objEntityView;
    fireTableStructureChanged();
  }


  public int getRowCount(){
    if (objEntityView != null){
      return objEntityView.getObjEntityViewFieldCount();
    }else{
      return 0;
    }
  }

  public int getColumnCount(){
      return columnNames.length;
  }

  public String getColumnName(int col) {
    return columnNames[col];
  }

  public Class getColumnClass(int c) {
    return getValueAt(0, c).getClass();
  }

  public Object getValueAt(int row, int column){
    ObjEntityViewField field = objEntityView.getObjEntityViewField(row);
    switch (column){
      case 0:
        return field.getName();
      case 1:
        return field.getDataType();
      case 2:
        return field.getCalcType();
      case 3: 
      {
        String calcType = field.getCalcType();
        if ((calcType.equals(CalcTypeEnum.NO_CALC_TYPE_NAME) || calcType.equals(CalcTypeEnum.CALC_TYPE_NAME)) 
        		&& field.getObjAttribute() != null)
        {
          return field.getObjAttribute().getName();
        } 
        else if (field.getCalcType().equals(CalcTypeEnum.LOOKUP_TYPE_NAME) && field.getObjRelationship() != null) 
        {
          return field.getObjRelationship().getName();
        } 
        else
        {
          return "";
        }
      }
      case 4:{
        String calcType = field.getCalcType();
        if (calcType.equals(CalcTypeEnum.LOOKUP_TYPE_NAME))
        {
          return field.getLookup().toString();
        }
        else
        {
          return "";
        }
      }

      default: return null;
    }
  }
}
