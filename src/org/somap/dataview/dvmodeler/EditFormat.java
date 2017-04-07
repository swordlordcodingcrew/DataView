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

import org.jdom.*;

/**
 *
 * @author Nataliya Kholodna
 * @version 1.0
 */

public class EditFormat{
  private String className = "";
  private String pattern = "";

  public EditFormat(Element element){
    className = element.getAttributeValue("class");
    pattern = element.getChild("pattern").getText();
  }

  public EditFormat(){
  }

  public boolean isEmpty(){
    if (className.length() == 0){
      return true;
    } else{
      return false;
    }
  }

  public void setClassName(String className){
    this.className = className;
  }

  public String getClassName(){
    return className;
  }

  public void setPattern(String Pattern){
    this.pattern = Pattern;
  }

  public String getPattern(){
    return pattern;
  }

  public Element getEditFormatElement(){
    Element e = new Element("edit-format");
    e.setAttribute(new Attribute("class", className));
    Element ePattern = new Element("pattern");
    ePattern.addContent(pattern);
    e.addContent(ePattern);
    return e;
  }
}
