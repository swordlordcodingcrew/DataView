/* ====================================================================
 *
 * The ObjectStyle Group Software License, version 1.1
 * ObjectStyle Group - http://objectstyle.org/
 * 
 * Copyright (c) 2002-2005, Andrei (Andrus) Adamchik and individual authors
 * of the software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any,
 *    must include the following acknowlegement:
 *    "This product includes software developed by independent contributors
 *    and hosted on ObjectStyle Group web site (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse
 *    or promote products derived from this software without prior written
 *    permission. For written permission, email
 *    "andrus at objectstyle dot org".
 * 
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    or "Cayenne", nor may "ObjectStyle" or "Cayenne" appear in their
 *    names without prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
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
 * individuals and hosted on ObjectStyle Group web site.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 */

package org.somap.dataview.dvmodeler;

import java.util.*;
import org.jdom.*;

/**
 *
 * @author Nataliya Kholodna
 * @version 1.0
 */

public class ObjEntityView extends DVObject {
  private DataView dataView;
  private DVObjEntity objEntity;

  private List objEntityViewFields = new ArrayList();

  private List loadErrors = new ArrayList();
  private List saveErrors = new ArrayList();

  private Set objEntityViewFieldsNames = new HashSet();

  public ObjEntityView(CayenneProject project, DataView dataView, Element element) {
    this.dataView = dataView;
    String viewPath = "<b>" + dataView + ".";
    String attributeValue = element.getAttributeValue("name");
    if ((attributeValue == null) || (attributeValue.length() == 0)){
      setName("");
      viewPath += "</b><br>";
      loadErrors.add(viewPath + "ObjEntity View has no attribute \"name\"<br><br>");
    } else {
      setName(attributeValue);
      viewPath += attributeValue + "</b><br>";
    }

    attributeValue = element.getAttributeValue("obj-entity-name");
    if ((attributeValue == null) || (attributeValue.length() == 0)){
      objEntity = null;
      loadErrors.add(viewPath + "ObjEntity View has no attribute \"obj-entity-name\"<br><br>");
    } else {
      DVObjEntity entity = project.getObjEntity(attributeValue);
      if (entity != null){
        objEntity = entity;
        objEntity.addObjEntityView(this);
      } else {
        objEntity = null;
        loadErrors.add(viewPath + "ObjEntity \""
        + attributeValue + "\" does not exist in the project<br><br>");
      }

    }
    List objEntityViewFieldElements = element.getChildren("field");
    Iterator itr = objEntityViewFieldElements.iterator();
    while(itr.hasNext()){
      Object o = itr.next();
      Element fieldElement = (Element)o;
      ObjEntityViewField field = new ObjEntityViewField(this, fieldElement);
      if (objEntityViewFieldsNames.add(field.getName()) == false){
        String path = "<b>" + dataView.getName() + "." + getName()
            + "." + field.getName() + "</b><br>";
        loadErrors.add(path + "ObjEntityView Field \"" + field.getName()
            + "\" already exists in the ObjEntity View\"" + getName() + "\"<br><br>");
      }
      loadErrors.addAll(field.getLoadErrors());
    }
    objEntityViewFieldsNames.clear();
    dataView.addObjEntityView(this);
  }

  public ObjEntityView(DataView dataView){
    this.dataView = dataView;
    setName("ObjEntityView");
    dataView.addObjEntityView(this);
  }

  public List getLoadErrors(){
    return Collections.unmodifiableList(loadErrors);
  }

  public List getSaveErrors(){
    return Collections.unmodifiableList(saveErrors);
  }

  public void setDataView(DataView dataView){
    this.dataView = dataView;
  }

  public DataView getDataView(){
    return dataView;
  }
  public void clearObjEntity(){
    objEntity = null;
  }

  public void setObjEntity(DVObjEntity objEntity){
    if(this.objEntity != null){
      this.objEntity.removeObjEntityView(this);
    }
    if (objEntity != null){
      this.objEntity = objEntity;
      objEntity.getDataMap().addObjEntityView(this);
    }else{
      this.objEntity = null;
    }
  }

  public DVObjEntity getObjEntity(){
    try{
      return objEntity;
    } catch(Exception e){
      return null;
    }
  }

  public List getObjEntityViewFields() {
    return Collections.unmodifiableList(objEntityViewFields);
  }

  public ObjEntityViewField createObjEntityViewField(){
    String nameRoot = "field";
    String name = nameRoot;
    for (int i = 0; i < Integer.MAX_VALUE; i++) {
      name = nameRoot + i;
      boolean nameExists = false;
      for (int j = 0; j < objEntityViewFields.size(); j++) {
        ObjEntityViewField objEntityViewField = (ObjEntityViewField)objEntityViewFields.get(j);
        nameExists = name.equalsIgnoreCase(objEntityViewField.getName());
        if (nameExists)
          break;
      }
      if (!nameExists)
        break;
    }
    ObjEntityViewField objEntityViewField = new ObjEntityViewField(this);
    objEntityViewField.setName(name);
    return objEntityViewField;
  }

  public void addObjEntityViewField(ObjEntityViewField objEntityViewField){
    objEntityViewFields.add(objEntityViewField);
  }

  public void removeObjEntityViewField(ObjEntityViewField objEntityViewField){
    objEntityViewFields.remove(objEntityViewField);
  }

  public ObjEntityViewField getObjEntityViewField(String name){
    Iterator itr = objEntityViewFields.iterator();
    while (itr.hasNext()){
      Object o = itr.next();
      ObjEntityViewField objEntField = (ObjEntityViewField)o;
      if (objEntField.getName().equals(name)){
        return objEntField;
      }
    }
    return null;
  }

  public ObjEntityViewField getObjEntityViewField(int index){
    return (ObjEntityViewField)(objEntityViewFields.get(index));
  }

  public int getIndexOfObjEntityViewField(ObjEntityViewField objEntityViewField){
    return objEntityViewFields.indexOf(objEntityViewField);
  }


  public int getObjEntityViewFieldCount(){
    return objEntityViewFields.size();
  }

  public void clear(){
    objEntityViewFields.clear();
  }

  public String toString(){
    return getName();
  }

  public Element getObjEntityViewElement(){
    Element objEntityViewElement = new Element("obj-entity-view");

    if (saveErrors.size() != 0){
      saveErrors.clear();
    }

    String viewPath = "<b>" + dataView.getName() + "."
                      + getName() + "</b><br>";

    objEntityViewElement.setAttribute("name", getName());
    if (getName().length() == 0){
      saveErrors.add(viewPath + "ObjEntity View has no name<br><br>");
    }

    if (objEntity != null){
      objEntityViewElement.setAttribute("obj-entity-name", objEntity.getName());
    }else{
      objEntityViewElement.setAttribute("obj-entity-name", "");
      saveErrors.add(viewPath + "ObjEntity View has no attribute obj-entity-name<br><br>");
    }

    List objEntityViewFieldElements = new ArrayList();
    Set objEntityViewFieldsSort = new TreeSet(new ObjEntityViewFieldComparator());
    objEntityViewFieldsSort.addAll(objEntityViewFields);

    Iterator itr = objEntityViewFieldsSort.iterator();
    while (itr.hasNext()){
      Object o = itr.next();
      ObjEntityViewField field = (ObjEntityViewField)o;
      Element objEntityViewFieldElement =
                     field.getObjEntityViewFieldElement();
      if (objEntityViewFieldsNames.add(field.getName()) == false){
        String path = "<b>" + dataView.getName() + "." + getName()
            + "." + field.getName() + "</b><br>";
        saveErrors.add(path + "ObjEntityView Field \"" + field.getName()
            + "\" already exists in the ObjEntity View \"" + getName() + "\"<br><br>");
      }

      saveErrors.addAll(field.getSaveErrors());
      objEntityViewFieldElements.add(objEntityViewFieldElement);
    }
    objEntityViewFieldsNames.clear();
    objEntityViewElement.setContent(objEntityViewFieldElements);
    return objEntityViewElement;
  }
}
