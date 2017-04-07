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

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;

/**
 *
 * @author Andriy Shapochka
 * @version 1.0
 */

public class BasicDocumentListener implements DocumentListener {

  public BasicDocumentListener() {
  }
  public void insertUpdate(DocumentEvent e) {
    documentUpdated(getText(e.getDocument()));
  }
  public void removeUpdate(DocumentEvent e) {
    documentUpdated(getText(e.getDocument()));
  }
  public void changedUpdate(DocumentEvent e) {
    documentUpdated(getText(e.getDocument()));
  }

  private String getText(Document document) {
    try {
      return document.getText(0, document.getLength());
    }
    catch (BadLocationException ex) {
      return null;
    }
  }

  public void documentUpdated(String text) {
  }
}