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

import javax.swing.tree.*;
import javax.swing.event.*;
import java.util.*;

/**
 *
 * @author Nataliya Kholodna
 * @version 1.0
 */

public class DataMapTreeModel implements TreeModel{
  private List dataMaps;
  private String root = "root";
    /** Listeners. */
  private Vector treeModelListeners;

  public DataMapTreeModel() {
  }

  public void setDataMaps(List dataMaps){
    this.dataMaps = dataMaps;
    fireTreeStructureChanged(new TreeModelEvent(this, new TreePath(root)));
  }


  /**
   * Returns the child of parent at index index in the parent's child array.
   */
  public Object getChild(Object parent, int index) {
    if (root.equals(parent)) {
      return dataMaps.get(index);
    } else if (parent instanceof DVDataMap){
      DVDataMap p = (DVDataMap)parent;
      return p.getObjEntity(index);
    } else if (parent instanceof DVObjEntity){
      DVObjEntity p = (DVObjEntity)parent;
      return p.getObjEntityView(index);
    } else if (parent instanceof ObjEntityView){
      ObjEntityView p = (ObjEntityView)parent;
      return p.getObjEntityViewField(index);
    } else
      return null;
  }
   /**
   * Returns the number of children of parent.
   */
  public int getChildCount(Object parent) {
    if (root.equals(parent)) {
      return dataMaps.size();
    } else if (parent instanceof DVDataMap){
      DVDataMap p = (DVDataMap)parent;
      return p.getObjEntityCount();
    } else if (parent instanceof DVObjEntity){
      DVObjEntity p = (DVObjEntity)parent;
      return p.getObjEntityViewCount();
    } else if (parent instanceof ObjEntityView){
      ObjEntityView p = (ObjEntityView)parent;
      return p.getObjEntityViewFieldCount();
    } else
      return 0;
  }

  /**
   * Returns the index of child in parent.
   */
  public int getIndexOfChild(Object parent, Object child) {
    if (root.equals(parent)) {
      return dataMaps.indexOf(child);
    } else if (parent instanceof DVDataMap){
      DVDataMap p = (DVDataMap)parent;
      return p.getIndexOfObjEntity((DVObjEntity)child);
    } else if (parent instanceof DVObjEntity){
      DVObjEntity p = (DVObjEntity)parent;
      return p.getIndexOfObjEntityView((ObjEntityView)child);
    } else if (parent instanceof ObjEntityView){
      ObjEntityView p = (ObjEntityView)parent;
      return p.getIndexOfObjEntityViewField((ObjEntityViewField)child);
    } else
      return 0;
  }

  /**
   * Returns the root of the tree.
   */
  public Object getRoot() {
    return root;
  }

  /**
   * Returns true if node is a leaf.
   */
  public boolean isLeaf(Object node) {
    if (root.equals(node)) {
      return dataMaps == null || dataMaps.isEmpty();
    } else if (node instanceof DVDataMap){
      DVDataMap p = (DVDataMap)node;
      return p.getObjEntityCount() == 0;
    } else if (node instanceof DVObjEntity){
      DVObjEntity p = (DVObjEntity)node;
      return p.getObjEntityViewCount() == 0;
    } else if (node instanceof ObjEntityView){
      ObjEntityView p = (ObjEntityView)node;
      return p.getObjEntityViewFieldCount() == 0;
    } else if (node instanceof ObjEntityViewField){
      return true;
    } else
      return true;
  }



  /**
   * Messaged when the user has altered the value for the item
   * identified by path to newValue.  Not used by this model.
   */
  public void valueForPathChanged(TreePath path, Object newValue) {
    System.out.println("*** valueForPathChanged : "
                       + path + " --> " + newValue);
  }

  public synchronized void removeTreeModelListener(TreeModelListener l) {
    if (treeModelListeners != null && treeModelListeners.contains(l)) {
      Vector v = (Vector) treeModelListeners.clone();
      v.removeElement(l);
      treeModelListeners = v;
    }
  }
  public synchronized void addTreeModelListener(TreeModelListener l) {
    Vector v = treeModelListeners == null ? new Vector(2) : (Vector) treeModelListeners.clone();
    if (!v.contains(l)) {
      v.addElement(l);
      treeModelListeners = v;
    }
  }
  protected void fireTreeNodesChanged(TreeModelEvent e) {
    if (treeModelListeners != null) {
      Vector listeners = treeModelListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((TreeModelListener) listeners.elementAt(i)).treeNodesChanged(e);
      }
    }
  }
  protected void fireTreeNodesInserted(TreeModelEvent e) {
    if (treeModelListeners != null) {
      Vector listeners = treeModelListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((TreeModelListener) listeners.elementAt(i)).treeNodesInserted(e);
      }
    }
  }
  protected void fireTreeNodesRemoved(TreeModelEvent e) {
    if (treeModelListeners != null) {
      Vector listeners = treeModelListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((TreeModelListener) listeners.elementAt(i)).treeNodesRemoved(e);
      }
    }
  }
  protected void fireTreeStructureChanged(TreeModelEvent e) {
    if (treeModelListeners != null) {
      Vector listeners = treeModelListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((TreeModelListener) listeners.elementAt(i)).treeStructureChanged(e);
      }
    }
  }

  public void replaceObjEntityView(DVObjEntity oldObjEntity,
                                   int oldIndex,
                                   DVObjEntity newObjEntity,
                                   ObjEntityView objEntityView) {

    DVDataMap oldDataMap = oldObjEntity.getDataMap();
    fireTreeNodesRemoved(new TreeModelEvent(
        this,
        new Object[] {root, oldDataMap, oldObjEntity},
        new int[] {oldIndex},
        new Object[] {objEntityView}));

    DVDataMap newDataMap = newObjEntity.getDataMap();

    int newIndex = newObjEntity.getIndexOfObjEntityView(objEntityView);
    fireTreeNodesInserted(new TreeModelEvent(
        this,
        new Object[] {root, newDataMap, newObjEntity},
        new int[] {newIndex},
        new Object[] {objEntityView}));

  }

  public TreePath objEntityViewAdded(ObjEntityView view) {
    DVObjEntity entity = view.getObjEntity();
    if (entity == null)
      return null;
    DVDataMap dataMap = entity.getDataMap();
    int index = entity.getIndexOfObjEntityView(view);
    fireTreeNodesInserted(new TreeModelEvent(
        this,
        new Object[] {root, dataMap, entity},
        new int[] {index},
        new Object[] {view}));
    return new TreePath(new Object[] {root, dataMap, entity, view});
  }

  public void objEntityViewRemoved(DVObjEntity entity, ObjEntityView view, int index) {
    DVDataMap dataMap = entity.getDataMap();
    fireTreeNodesRemoved(new TreeModelEvent(
        this,
        new Object[] {root, dataMap, entity},
        new int[] {index},
        new Object[] {view}));
  }

  public void objEntityViewsRemoved(Map removingViews) {
    HashMap views = (HashMap)removingViews;
    for(Iterator j = views.keySet().iterator(); j.hasNext();){
      ObjEntityView view = (ObjEntityView)j.next();
      DVObjEntity entity = view.getObjEntity();
      Integer index = (Integer)views.get(view);
      DVDataMap dataMap = entity.getDataMap();

      fireTreeNodesRemoved(new TreeModelEvent(
          this,
          new Object[] {root, dataMap, entity},
          new int[] {index.intValue()},
          new Object[] {view}));
    }
  }


  public TreePath objEntityViewChanged(ObjEntityView view) {
    DVObjEntity entity = view.getObjEntity();
    if (entity == null)
      return null;
    DVDataMap dataMap = entity.getDataMap();
    int index = entity.getIndexOfObjEntityView(view);
    fireTreeNodesChanged(new TreeModelEvent(
        this,
        new Object[] {root, dataMap, entity},
        new int[] {index},
        new Object[] {view}));
    return new TreePath(new Object[] {root, dataMap, entity, view});
  }

  public TreePath fieldAdded(ObjEntityViewField field) {
    ObjEntityView owner = field.getObjEntityView();
    DVObjEntity entity = owner.getObjEntity();
    if (entity == null)
      return null;
    DVDataMap dataMap = entity.getDataMap();
    int index = owner.getIndexOfObjEntityViewField(field);
    fireTreeNodesInserted(new TreeModelEvent(
        this,
        new Object[] {root, dataMap, entity, owner},
        new int[] {index},
        new Object[] {field}));
    return new TreePath(new Object[] {root, dataMap, entity, owner, field});
  }

  public void fieldRemoved(ObjEntityViewField field, int index) {
    ObjEntityView owner = field.getObjEntityView();
    DVObjEntity entity = owner.getObjEntity();

    DVDataMap dataMap = entity.getDataMap();
    fireTreeNodesRemoved(new TreeModelEvent(
        this,
        new Object[] {root, dataMap, entity, owner},
        new int[] {index},
        new Object[] {field}));

  }

  public TreePath fieldChanged(ObjEntityViewField field) {
    ObjEntityView owner = field.getObjEntityView();
    DVObjEntity entity = owner.getObjEntity();
    if (entity == null)
      return null;
    DVDataMap dataMap = entity.getDataMap();
    int index = owner.getIndexOfObjEntityViewField(field);
    fireTreeNodesChanged(new TreeModelEvent(
        this,
        new Object[] {root, dataMap, entity, owner},
        new int[] {index},
        new Object[] {field}));
    return new TreePath(new Object[] {root, dataMap, entity, owner, field});
  }
}
