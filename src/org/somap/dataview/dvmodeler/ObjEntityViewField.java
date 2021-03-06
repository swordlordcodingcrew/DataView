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

import java.util.*;
import org.jdom.*;
import org.somap.dataview.CalcTypeEnum;

/**
 * Provides the DV Modeller ObjEntityViewField.
 * 
 * @author Nataliya Kholodna
 * @version 1.0
 */
public class ObjEntityViewField extends DVObject 
{	
  private ObjEntityView objEntityView;
  private DVObjRelationship objRelationship;//implied
  private DVObjAttribute objAttribute;//implied
  private int prefIndex;
  private boolean editable;//required
  private String calcType = "";//required
  private String dataType = ""; //required
  private boolean visible; //required
  private String width = "";
  private boolean sortable;
  private boolean required;

  private String caption = "";
  private String tooltip = "";
  private String defaultValue = "";
  private Lookup lookup;
  private EditFormat editFormat;
  private DisplayFormat displayFormat;

  private List loadErrors = new ArrayList();
  private List saveErrors = new ArrayList();

  public ObjEntityViewField(ObjEntityView objEntityView, Element element) {
    this.objEntityView = objEntityView;
    DataView dataView = objEntityView.getDataView();
    String fieldPath = "<b>" + dataView + "." + objEntityView + ".";
    String attributeValue = element.getAttributeValue("name");
    if ((attributeValue == null) || (attributeValue.length() == 0)){
      setName("");
      fieldPath += "</b><br>";
      loadErrors.add(fieldPath + "field has no name<br><br>");
    } else {
      fieldPath += attributeValue + "</b><br>";
      setName(attributeValue);
    }
    attributeValue = element.getAttributeValue("editable");
    if ((attributeValue == null) || (attributeValue.length() == 0)){
      editable = false;
      loadErrors.add(fieldPath + "field has no attribute \"editable\"<br><br>");

    } else {
      if ((attributeValue.equalsIgnoreCase("true"))
         || (attributeValue.equalsIgnoreCase("false"))){
        editable = Boolean.valueOf(attributeValue).booleanValue();
      } else {
        editable = false;
        loadErrors.add(fieldPath + "\"editable\" attribute value is not valid<br><br>");

      }
    }
    attributeValue = element.getAttributeValue("calc-type");
    if ((attributeValue == null) || (attributeValue.length() == 0))
    {
      Attribute relationship = element.getAttribute("obj-relationship-name");
      Element lookupChild = element.getChild("lookup");
      
      if ((relationship != null) || (lookupChild != null))
      {
        calcType = CalcTypeEnum.LOOKUP_TYPE_NAME;
      }
      else 
      {
        calcType = CalcTypeEnum.NO_CALC_TYPE_NAME;
      }
      loadErrors.add(fieldPath + "field has no attribute \"calc-type\"<br><br>");

    } 
    else 
    {
      if (attributeValue.equals(CalcTypeEnum.NO_CALC_TYPE_NAME) 
    		  || attributeValue.equals(CalcTypeEnum.CODE_TYPE_NAME)
    		  || attributeValue.equals(CalcTypeEnum.LOOKUP_TYPE_NAME))
      {
        calcType = attributeValue;
      } 
      else 
      {
        Attribute relationship = element.getAttribute("obj-relationship-name");
        Element lookupChild = element.getChild("lookup");
        Attribute attribute = element.getAttribute("obj-attribute-name");
        if (attribute != null)
        {
          calcType = CalcTypeEnum.NO_CALC_TYPE_NAME;
        } 
        else if ((relationship != null) || (lookupChild != null))
        {
          calcType = CalcTypeEnum.LOOKUP_TYPE_NAME;
        }
        else 
        {
          calcType = CalcTypeEnum.NO_CALC_TYPE_NAME;
        }
        loadErrors.add(fieldPath + "\"calc-type\" attribute value is not valid<br><br>");
      }
    }

    attributeValue = element.getAttributeValue("data-type");
    if ((attributeValue == null) || (attributeValue.length() == 0)){
      dataType = "String";
      loadErrors.add(fieldPath + "field has no attribute \"data-type\"<br><br>");

    } else {
      if (attributeValue.equals("String") || attributeValue.equals("Money")
        || attributeValue.equals("Integer") || attributeValue.equals("Double")
        || attributeValue.equals("Percent") || attributeValue.equals("Date")
        || attributeValue.equals("Datetime") || attributeValue.equals("Boolean")
        || attributeValue.equals("Object")){

        dataType = attributeValue;
      } else {
        dataType = "String";
        loadErrors.add(fieldPath + "Attribute \"data-type\" is not valid<br><br>");
      }
    }
    attributeValue = element.getAttributeValue("visible");
    if (attributeValue == null){
      visible = true;
      loadErrors.add(fieldPath + "field has no attribute \"visible\"<br><br>");

    } else {
      if ((attributeValue.equalsIgnoreCase("false"))
         || (attributeValue.equalsIgnoreCase("true"))){
        visible = new Boolean(attributeValue).booleanValue();
      } else {
        visible = false;
        loadErrors.add(fieldPath + "Attribute \"visible\" is not valid. <br> It must be true or false<br><br>");

      }
    }
 // -- Implied attributes---
    if (calcType.equals(CalcTypeEnum.NO_CALC_TYPE_NAME) || calcType.equals(CalcTypeEnum.CODE_TYPE_NAME))
    {
      attributeValue = element.getAttributeValue("obj-attribute-name");
      lookup = new Lookup(this);
      if ((attributeValue == null) || (attributeValue.length() == 0)){
        objAttribute = null;
        loadErrors.add(fieldPath + "field attribute \"calc-type\" has value \"nocalc\", " + "but field has no attribute \"obj-attribute-name\"<br><br>");
      } 
      else
      {
        DVObjEntity entity = objEntityView.getObjEntity();
        if (entity != null)
        {
          objAttribute = entity.getObjAttribute(attributeValue);
          if (objAttribute == null)
          {
          loadErrors.add(fieldPath + "ObjEntity \"" + entity + "\" has no attribute \"" + attributeValue + "\"<br><br>");
          }
        }
        else
        {
          objAttribute = null;
          loadErrors.add(fieldPath + "ObjEntity for ObjEntity View \"" + objEntityView + "\" is null<br><br>");
        }

      }
    }
    else if (calcType.equals(CalcTypeEnum.LOOKUP_TYPE_NAME))
    {
        attributeValue = element.getAttributeValue("obj-relationship-name");
        if ((attributeValue == null) || (attributeValue.length() == 0))
        {
          objRelationship = null;
          lookup = new Lookup(this);
          loadErrors.add(fieldPath + "field has no attribute \"obj-relationship-name\"<br><br>");
        } 
        else
        {
          DVObjEntity objEntity = objEntityView.getObjEntity();
          List relationships = objEntity.getDataMap().getObjRelationshipsBySourceToOne(objEntity);
          for (Iterator j = relationships.iterator(); j.hasNext();){
            DVObjRelationship relationship = (DVObjRelationship)j.next();
            if (relationship.getName().equals(attributeValue)){
              objRelationship = relationship;
            }
          }
          if (objRelationship == null)
          {
            lookup = new Lookup(this);
            loadErrors.add(fieldPath + "ObjRelationship "
            + attributeValue + " with source " + objEntity + " and toMany = false does not exist in the Data Map "
            + objEntity.getDataMap() + "<br><br>");
          }
          else 
          {
            Element lookupChild = element.getChild("lookup");
            if (lookupChild != null)
            {
              lookup = new Lookup(this);
              String lookupViewName = lookupChild.getAttributeValue("obj-entity-view-name");
              String lookupFieldName = lookupChild.getAttributeValue("field-name");
              if ((lookupViewName == null) || (lookupViewName.length() == 0))
              {
                loadErrors.add(fieldPath + "Calc Type of this field is \"lookup\", but its lookup " +
                               "has no attribute \"obj-entity-view\" <br><br>");
              }
              else 
              {
                if (lookupFieldName == null)
                {
                  loadErrors.add(fieldPath + "Field lookup has no attribute \"field\"<br><br>");
                }
                dataView.getCayenneProject().putFieldLookup(this, lookupViewName, lookupFieldName);
              }

            } 
            else 
            {
              lookup = new Lookup(this);
            }
          }
        }
      }
    

    attributeValue = element.getAttributeValue("pref-index");
    if (attributeValue == null) {
      prefIndex = -1;
    } else if (attributeValue.length() == 0){
      prefIndex = -1;
      loadErrors.add(fieldPath + "Attribute \"pref-index\" has no value<br><br>");
    } else {
      try{
        prefIndex = Integer.parseInt(attributeValue);
      } catch (Exception e){
        prefIndex = -1;
        loadErrors.add(fieldPath + "Attribute \"pref-index\" is not valid<br><br>");
      }
      if (prefIndex < -1) {
        prefIndex = -1;
        loadErrors.add(fieldPath + "Attribute \"pref-index\" has value &lt -1<br><br>");
      }
    }

 // -- child elements (don't required)

    if (element.getChild("caption") != null){
      caption = element.getChild("caption").getText().trim();
    }

    if (element.getChild("tooltip") != null){
        tooltip = element.getChild("tooltip").getText().trim();
    }

    if (element.getChild("edit-format") != null){
      editFormat = new EditFormat(element.getChild("edit-format"));
    } else {
      editFormat = new EditFormat();
    }
    if (element.getChild("display-format") != null){
      displayFormat = new DisplayFormat(element.getChild("display-format"));
    } else {
      displayFormat = new DisplayFormat();
    }
    if (element.getChild("default-value") != null){
      defaultValue = element.getChildText("default-value").trim();
    }
    this.objEntityView.addObjEntityViewField(this);
  }

  public ObjEntityViewField(ObjEntityView objEntityView){
    setName("Field");
    this.objEntityView = objEntityView;
    calcType = CalcTypeEnum.NO_CALC_TYPE_NAME;
    dataType = "String";
    visible = true;
    editable = true;
    prefIndex = objEntityView.getObjEntityViewFieldCount();
    lookup = new Lookup(this);
    editFormat = new EditFormat();
    displayFormat = new DisplayFormat();
    objEntityView.addObjEntityViewField(this);
  }

  public List getLoadErrors(){
    return Collections.unmodifiableList(loadErrors);
  }
  public List getSaveErrors(){
    return Collections.unmodifiableList(saveErrors);
  }

  public void setObjEntityView(ObjEntityView objEntityView){
    this.objEntityView = objEntityView;
  }

  public ObjEntityView getObjEntityView(){
    return objEntityView;
  }

  //get-set methods for attributes

  public void setDataType(String dataType){
    String oldDataType = this.dataType;
    this.dataType = dataType;
    propertyChangeListeners.firePropertyChange("dataType", oldDataType, this.dataType);
  }
  public String getDataType(){
    return dataType;
  }

  public void setCalcType(String calcType){
    String oldCalcType = this.calcType;
    this.calcType = calcType;
    propertyChangeListeners.firePropertyChange("calcType", oldCalcType, calcType);
  }

  public String getCalcType(){
    return calcType;
  }

  public void setObjRelationship(DVObjRelationship relationship){
    DVObjRelationship oldRelationship = objRelationship;
    objRelationship = relationship;

    propertyChangeListeners.firePropertyChange("objRelationship", oldRelationship, objRelationship);

  }
  public DVObjRelationship getObjRelationship(){
    return objRelationship;
  }


  public void setObjAttribute(DVObjAttribute attribute){
    DVObjAttribute oldObjAttribute = objAttribute;
    objAttribute = attribute;
    propertyChangeListeners.firePropertyChange("objAttribute", oldObjAttribute, objAttribute);

  }
  public DVObjAttribute getObjAttribute(){
    return objAttribute;
  }

  public void setPrefIndex(int index){
    prefIndex = index;
  }

  public int getPrefIndex(){
    return prefIndex;
  }

  public void setEditable(boolean editable){
    this.editable = editable;
  }
  public boolean getEditable(){
    return editable;
  }

  public void setVisible(boolean visible){
    this.visible = visible;
  }
  public boolean getVisible(){
    return visible;
  }

  public void setCaption(String caption){
    this.caption = caption;
  }
  public String getCaption(){
    return caption;
  }
  
  public void setTooltip(String tooltip){
      this.tooltip = tooltip;
  }
  public String getTooltip(){
      return tooltip;
  }

  public void setDefaultValue(String defaultValue){
    this.defaultValue = defaultValue;
  }
  public String getDefaultValue(){
    return defaultValue;
  }

  public void setLookup(Lookup lookup){
    Lookup oldLookup = this.lookup;
    if (lookup == null){
      this.lookup.setLookupObjEntityView(null);
      this.lookup.setLookupField(null);
    }
    this.lookup = lookup;
    propertyChangeListeners.firePropertyChange("lookup", oldLookup, lookup);
  }

  public Lookup getLookup(){
    return lookup;
  }

  public void setEditFormat(EditFormat editFormat){
    this.editFormat = editFormat;
  }

  public EditFormat getEditFormat(){
    return editFormat;
  }

  public void setDisplayFormat(DisplayFormat displayFormat){
    this.displayFormat = displayFormat;
  }
  public DisplayFormat getDisplayFormat(){
    return displayFormat;
  }

//-- toString
  public String toString(){
    return getName();
  }
// -- get element
  public Element getObjEntityViewFieldElement()
  {
    Element fieldElement = new Element("field");
    if (saveErrors.size() != 0)
    {
      saveErrors.clear();
    }
    DataView dataView = objEntityView.getDataView();
    String fieldPath = "<b>" + dataView.getName() + "." + objEntityView.getName() + "." + getName() + "</b><br>";
    
    if (getName().length() == 0)
    {
      saveErrors.add(fieldPath + "Field has no name;<br><br>");
    }
    
    fieldElement.setAttribute("name", getName());

    fieldElement.setAttribute("calc-type", calcType);
    
    if (calcType.equals(CalcTypeEnum.NO_CALC_TYPE_NAME) || calcType.equals(CalcTypeEnum.CODE_TYPE_NAME))
    {
      if (objAttribute == null)
      {
        saveErrors.add(fieldPath + "field attribute \"calc-type\" value is \"nocalc\", but field has no \"obj-attribute-name\" attribute value<br><br>");
        fieldElement.setAttribute("obj-attribute-name", "");
      } 
      else 
      {
        fieldElement.setAttribute("obj-attribute-name", objAttribute.getName());
      }
    }
    else if (calcType.equals(CalcTypeEnum.LOOKUP_TYPE_NAME))
	{
	    if (objRelationship == null)
	    {
	      fieldElement.setAttribute("obj-relationship-name", "");
	      saveErrors.add(fieldPath + "field attribute \"calc-type\" value is \"lookup\", but field has no \"obj-relationship-name\" attribute value<br><br>");
	    } 
	    else 
	    {
	      fieldElement.setAttribute("obj-relationship-name", objRelationship.getName());
	    }
	    
	    fieldElement.addContent(lookup.getLookupElement());
	    saveErrors.addAll(lookup.getSaveErrors());
	}
    
    fieldElement.setAttribute("editable", String.valueOf(editable));
    fieldElement.setAttribute("pref-index", String.valueOf(prefIndex));
    fieldElement.setAttribute("data-type", dataType);
    fieldElement.setAttribute("visible", String.valueOf(visible));
    Element captionElement = new Element("caption");
    
    if (caption.length() != 0){
      fieldElement.addContent(captionElement.addContent(caption));
    }

    Element tooltipElement = new Element("tooltip");
    if (tooltip.length() != 0){
      fieldElement.addContent(tooltipElement.addContent(tooltip));
    }
    
    if (!editFormat.isEmpty()){
      fieldElement.addContent(editFormat.getEditFormatElement());
    }
    if (!displayFormat.isEmpty()){
      fieldElement.addContent(displayFormat.getDisplayFormatElement());
    }
    Element defaultValueElement = new Element("default-value");
    if (defaultValue.length() != 0){
      fieldElement.addContent(defaultValueElement.addContent(defaultValue));
    }
    return fieldElement;
  }
}
