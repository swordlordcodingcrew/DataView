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

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.somap.dataview.CalcTypeEnum;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

/**
 * This class defines contents for part of DVModelerFrame frame.
 *
 * @author Nataliya Kholodna
 * @version 1.0
 */

public class CardPanel extends JPanel{
  private FieldEditor fieldPanel;
  private JTable attributesTable;
  private JPanel objEntityViewPanel;

  private JPanel dataMapPanel;
  private JTextField dataMapNameField;
  private JTextField dataMapFileField;

  private JPanel dataViewPanel;
  private DataView dataView;
  private JTextField dataViewNameField;
  private JTextField dataViewFileField;
  private DataMapTreeModel dataMapTreeModel;

  private JTextField dataViewField;
  private JTextField viewNameField;
  private ObjEntityView objEntityView;
  private JComboBox objEntityCombo;

  private JPanel emptyPanel;
  private CardLayout cardLayout;


  private DVObject selectedObject;

  public CardPanel() {
    super();
    cardLayout = new CardLayout();
    this.setLayout( cardLayout );

    /*empty panel*/
    emptyPanel = new JPanel();
    add(emptyPanel, "emptyPanel");

    /*panel for field editing */
    fieldPanel = new FieldEditor();
    add(fieldPanel, "objEntityViewFieldPanel");

    /*table for ObjEntity's attributes*/
    attributesTable = new JTable();
    attributesTable.setModel(new AttributesTableModel());
    attributesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane scrollPane = new JScrollPane(attributesTable);
    scrollPane.setBorder(Borders.EMPTY_BORDER);
    add(scrollPane, "attributesTable");

    /*building panel for objEntityView properties*/
    FormLayout layout = new FormLayout(
        "right:55dlu, 3dlu, 200dlu",
        "");
    DefaultFormBuilder builderView = new DefaultFormBuilder(layout);
    builderView.setDefaultDialogBorder();

    viewNameField = new JTextField();
    viewNameField.getDocument().addDocumentListener(
        new NameChangeListener());
    builderView.append("Name:", viewNameField);

    objEntityCombo = new JComboBox();
    objEntityCombo.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        objEntityComboAction(e);
      }
    });
    builderView.append("ObjEntity:",   objEntityCombo);

    dataViewField = new JTextField();

    dataViewField.setEditable(false);
    dataViewField.setBorder(Borders.EMPTY_BORDER);
    builderView.append("Data View:", dataViewField);

    objEntityViewPanel = builderView.getPanel();
    scrollPane = new JScrollPane(objEntityViewPanel);
    scrollPane.setBorder(Borders.EMPTY_BORDER);
    add(scrollPane, "objEntityViewPanel");

    /*Building panel for dataMap properties*/
    FormLayout layout1 = new FormLayout(
        "right:55dlu, 3dlu, 200dlu",
        "");

    DefaultFormBuilder builderMap = new DefaultFormBuilder(layout1);
    builderMap.setDefaultDialogBorder();

    dataMapNameField = new JTextField();
    dataMapNameField.setEditable(false);
    dataMapNameField.setBorder(Borders.EMPTY_BORDER);
    builderMap.append("Name:", dataMapNameField);

    dataMapFileField = new JTextField();
    dataMapFileField.setEditable(false);
    dataMapFileField.setBorder(Borders.EMPTY_BORDER);
    builderMap.append("Location:", dataMapFileField);

    dataMapPanel = builderMap.getPanel();
    scrollPane = new JScrollPane(dataMapPanel);
    scrollPane.setBorder(Borders.EMPTY_BORDER);
    add(scrollPane, "dataMapPanel");

    /*Building panel for dataView properties*/
    FormLayout layout2 = new FormLayout(
        "right:55dlu, 3dlu, 200dlu",
        "");

    DefaultFormBuilder builderDataView = new DefaultFormBuilder(layout2);
    builderDataView.setDefaultDialogBorder();

    dataViewNameField = new JTextField();
    dataViewNameField.getDocument().addDocumentListener(
        new NameChangeListener());

    builderDataView.append("Name:", dataViewNameField);

    dataViewFileField = new JTextField();
    dataViewFileField.setEditable(false);
    dataViewFileField.setBorder(Borders.EMPTY_BORDER);
    builderDataView.append("Location:", dataViewFileField);

    dataViewPanel = builderDataView.getPanel();
    scrollPane = new JScrollPane(dataViewPanel);
    scrollPane.setBorder(Borders.EMPTY_BORDER);
    add(scrollPane, "dataViewPanel");
    /*setting default panel*/
    cardLayout.show(this, "emptyPanel");
  }

  /*shows  definning by selectedObject panel */
  public void showPanel(DVObject selectedObject){
    this.selectedObject = null;
    if (selectedObject instanceof DVDataMap){
      DVDataMap dataMap = (DVDataMap)selectedObject;
      dataMapNameField.setText(dataMap.getName());
      dataMapFileField.setText(dataMap.getFile().getName());
      cardLayout.show(this, "dataMapPanel");
    } else if (selectedObject instanceof DataView){
      dataView = (DataView)selectedObject;
      dataViewNameField.setText(dataView.getName());
      dataViewFileField.setText(
          (dataView.getFile() != null ?
          dataView.getFile().getName() :
          "New Data View"));

      cardLayout.show(this, "dataViewPanel");
    } else if (selectedObject instanceof DVObjEntity){
      DVObjEntity objEntity = (DVObjEntity)selectedObject;
      AttributesTableModel attributesTableModel =
          (AttributesTableModel)attributesTable.getModel();
      attributesTableModel.setObjEntity(objEntity);
      cardLayout.show(this, "attributesTable");
    } else if (selectedObject instanceof ObjEntityView){
      objEntityView = (ObjEntityView)selectedObject;
      dataViewField.setText(objEntityView.getDataView().getName());
      viewNameField.setText(objEntityView.getName());

      DVObjEntity objEntity = objEntityView.getObjEntity();
      if (objEntity == null){
        objEntityCombo.setSelectedIndex(0);
      } else {
        objEntityCombo.setSelectedItem(objEntity);
      }

      cardLayout.show(this, "objEntityViewPanel");
    } else if (selectedObject instanceof ObjEntityViewField){
      ObjEntityViewField objEntityViewField = (ObjEntityViewField)selectedObject;
      fieldPanel.setFieldProperties(objEntityViewField);
      cardLayout.show(this, "objEntityViewFieldPanel");
    } else if (selectedObject == null ){
      cardLayout.show(this, "emptyPanel");
    }
    this.selectedObject = selectedObject;
  }

  /*sets models which can be changed*/
  public void setModels(DataMapTreeModel mapModel,
                        DataViewTreeModel viewModel,
                        FieldsTableModel tableModel){
    dataMapTreeModel = mapModel;
    fieldPanel.setModels(mapModel, viewModel, tableModel);
  }

  /*sets another CayenneProject*/
  public void setProject(CayenneProject project) {
    if (project == null) {
      objEntityCombo.setModel(new DefaultComboBoxModel());
      return;
    }

    DVObjEntity[] projectEntities = project.getObjEntities();
    DVObjEntity[] entities = new DVObjEntity[projectEntities.length + 1];
    DVObjEntity nullEntity = null;
    entities[0] = nullEntity;

    for (int j = 0; j < projectEntities.length; j++){
      entities[j+1] = projectEntities[j];
    }
    DefaultComboBoxModel objEntitiesDefaultModel =
        new DefaultComboBoxModel(entities);
    objEntityCombo.setModel(objEntitiesDefaultModel);
  }

  private class NameChangeListener extends BasicDocumentListener {
    public void documentUpdated(String text) {
      if (selectedObject != null){
        selectedObject.setName(text.trim());
        if (selectedObject instanceof DataView){
          File file = dataView.getFile();
          File newFile = new File(file.getParentFile(), dataView.getName() + ".view.xml");
          dataView.setFile(newFile);
          dataViewFileField.setText(dataView.getFile().getName());
        }
      }
    }
  }

  private void objEntityComboAction(ActionEvent e){
    DVObjEntity selectedObjEntity = (DVObjEntity)objEntityCombo.getSelectedItem();
    DVObjEntity objEntity = objEntityView.getObjEntity();
    if ((objEntity != selectedObjEntity)){
      if ((objEntity != null) && (selectedObjEntity != null)){
        java.util.List relationships = selectedObjEntity.getDataMap().getObjRelationshipsBySourceToOne(selectedObjEntity);
        int oldIndex = objEntity.getIndexOfObjEntityView(objEntityView);
        objEntityView.setObjEntity(selectedObjEntity);

        /*refrashing dataMapTreeModel: replace ObjEntityView
          to new ObjEntity*/
        dataMapTreeModel.replaceObjEntityView(objEntity, oldIndex, selectedObjEntity, objEntityView);

        /*changing fields properties, which depend on ObjEntity */
        java.util.List fields = objEntityView.getObjEntityViewFields();
        for (Iterator itr = fields.iterator();itr.hasNext();){
          ObjEntityViewField field = (ObjEntityViewField)itr.next();
          
          if(field.getCalcType().equals(CalcTypeEnum.NO_CALC_TYPE_NAME) || field.getCalcType().equals(CalcTypeEnum.CODE_TYPE_NAME))
          {
            if (field.getObjAttribute() != null)
            {
              String fieldAttributeName = field.getObjAttribute().getName();
              if (selectedObjEntity.getObjAttribute(fieldAttributeName) != null){
                field.setObjAttribute(selectedObjEntity.getObjAttribute(fieldAttributeName));
              }else{
                field.setObjAttribute(null);
              }
            }
          }
          else if(field.getCalcType().equals(CalcTypeEnum.LOOKUP_TYPE_NAME)){
            DVObjRelationship fieldRelationship = field.getObjRelationship();
            if (fieldRelationship != null){
              String relationshipName = fieldRelationship.getName();
              DVObjEntity targetObjEntity = fieldRelationship.getTargetObjEntity();
              Lookup lookup = field.getLookup();
              Lookup nullLookup = new Lookup(field);
              nullLookup.setLookupField(null);
              nullLookup.setLookupObjEntityView(null);

              field.setObjRelationship(null);
              field.setLookup(nullLookup);
              for (Iterator j = relationships.iterator(); j.hasNext();){
                DVObjRelationship relationship = (DVObjRelationship)j.next();
                if ((relationship.getName().equals(relationshipName))&&
                    (relationship.getTargetObjEntity() == targetObjEntity)){
                  field.setObjRelationship(relationship);
                  field.setLookup(lookup);
                }
              }
            }
          }
        }
      } else if ((objEntity == null) && (selectedObjEntity != null)){
        objEntityView.setObjEntity(selectedObjEntity);

        /*refreshing dataMapTreeModel: adding objEntityView to objEntity*/
        dataMapTreeModel.objEntityViewAdded(objEntityView);
      } else if ((objEntity != null) && (selectedObjEntity == null)){
        int index = objEntity.getIndexOfObjEntityView(objEntityView);
        objEntityView.setObjEntity(null);

        /*refreshing dataMapTreeModel: removing objEntityView from objEntity*/
        dataMapTreeModel.objEntityViewRemoved(objEntity, objEntityView, index);

        /*changing fields properties, which depend on ObjEntity */
        java.util.List fields = objEntityView.getObjEntityViewFields();

        for (Iterator itr = fields.iterator();itr.hasNext();)
        {
          ObjEntityViewField field = (ObjEntityViewField)itr.next();
          
          if(field.getCalcType().equals(CalcTypeEnum.NO_CALC_TYPE_NAME) || field.getCalcType().equals(CalcTypeEnum.CODE_TYPE_NAME))
          {
            field.setObjAttribute(null);
          }
          else if(field.getCalcType().equals(CalcTypeEnum.LOOKUP_TYPE_NAME))
          {
            Lookup nullLookup = new Lookup(field);
            nullLookup.setLookupField(null);
            nullLookup.setLookupObjEntityView(null);

            field.setObjRelationship(null);
            field.setLookup(nullLookup);
          }
        }
      }
    }
  }
}
