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

import java.awt.BorderLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.somap.dataview.CalcTypeEnum;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.Options;

/**
 *
 * @author Nataliya Kholodna
 * @author Andriy Shapochka
 * @version 1.0
 */

public class FieldEditor extends JPanel {
  ObjEntityViewField objEntityViewField;
  private BorderLayout borderLayout = new BorderLayout();

  private JTextField viewNameField;
  private JComboBox objEntityCombo;
  private JTextField fieldNameField;
  private JComboBox dataTypeCombo;
  private JComboBox calcTypeCombo;
  private JComboBox objAttributeCombo;
  private JTextField defaultValueField;
  private JTextField captionField;
  private JTextField tooltipField;
  private JCheckBox editableCheckBox;
  private JCheckBox visibleCheckBox;
  private JTextField displayPatternField;
  private JTextField displayClassField;
  private JTextField editPatternField;
  private JTextField editClassField;
  private JSpinner preferredIndexField;
  private JComboBox  lookupViewCombo;
  private JComboBox  lookupFieldCombo;
  private JComboBox objRelationshipCombo;

  private FieldEditorHelper fieldEditorHelper;


  public FieldEditor() {
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    fieldEditorHelper = new FieldEditorHelper(this);
  }
  void jbInit() throws Exception {
    this.setLayout(borderLayout);
    this.add(buildPanel(), BorderLayout.CENTER);
  }

  private void initComponents() {
    viewNameField = new JTextField();
    objEntityCombo  = new JComboBox();
    fieldNameField = new JTextField();
    dataTypeCombo = new JComboBox(new String[] {
      "String", "Money", "Integer", "Double", "Percent",
      "Date", "Datetime", "Boolean", "Object" });
    calcTypeCombo = new JComboBox(new String[] {"No Calculation", "Lookup", "Code"});
    lookupViewCombo = new JComboBox();
    lookupFieldCombo = new JComboBox();
    objRelationshipCombo = new JComboBox();
    objAttributeCombo = new JComboBox();
    defaultValueField = new JTextField();
    captionField = new JTextField();
    tooltipField = new JTextField();
    editableCheckBox = new JCheckBox("", true);
    visibleCheckBox = new JCheckBox("", true);
    displayClassField = new JTextField();
    displayPatternField = new JTextField();
    editClassField = new JTextField();
    editPatternField = new JTextField();
    SpinnerNumberModel preferredIndexNumberModel = new SpinnerNumberModel(-1, -1, 1024, 1);
    preferredIndexField = new JSpinner(preferredIndexNumberModel);
  }


  private JComponent buildPanel() {
    initComponents();

    FormLayout layout = new FormLayout(
        "right:55dlu, 3dlu, 170dlu, 3dlu, " +
        "right:25dlu, 3dlu, 55dlu",
        "");
    DefaultFormBuilder generalBuilder = new DefaultFormBuilder(layout);
    generalBuilder.setDefaultDialogBorder();

    generalBuilder.append("Name:", fieldNameField);
    generalBuilder.append("Type:", dataTypeCombo);
    generalBuilder.append("Default:", defaultValueField);
    generalBuilder.appendUnrelatedComponentsGapRow();
    generalBuilder.nextLine(2);

    generalBuilder.append("Calc Type:", calcTypeCombo);
    generalBuilder.nextLine();
    generalBuilder.append("Attribute:", objAttributeCombo);
    generalBuilder.nextLine();

    generalBuilder.appendSeparator("Value Lookup");

    generalBuilder.append("Relationship:",   objRelationshipCombo);
    generalBuilder.nextLine();
    generalBuilder.append("Lookup View:",  lookupViewCombo);
    generalBuilder.nextLine();
    generalBuilder.append("Lookup Field:",   lookupFieldCombo);

    layout = new FormLayout(
        "right:55dlu, 3dlu, 170dlu, 3dlu, " +
        "right:25dlu, 3dlu, pref",
        "");
    DefaultFormBuilder displayBuilder = new DefaultFormBuilder(layout);
    displayBuilder.setDefaultDialogBorder();

    displayBuilder.append("Caption:", captionField);
    displayBuilder.append("Index:", preferredIndexField);
    displayBuilder.append("Tooltip:", tooltipField);
    displayBuilder.nextLine();
    displayBuilder.append("Visible:", visibleCheckBox);
    displayBuilder.nextLine();
    displayBuilder.append("Editable:", editableCheckBox);
    displayBuilder.appendUnrelatedComponentsGapRow();
    displayBuilder.nextLine(2);

    displayBuilder.append("Display Format:", displayClassField);
    displayBuilder.nextLine();
    displayBuilder.append("Display Pattern:", displayPatternField);
    displayBuilder.appendUnrelatedComponentsGapRow();
    displayBuilder.nextLine(2);

    displayBuilder.append("Edit Format:", editClassField);
    displayBuilder.nextLine();
    displayBuilder.append("Edit Pattern:", editPatternField);

    JScrollPane generalScrollPane = new JScrollPane(generalBuilder.getPanel());
    generalScrollPane.setBorder(Borders.EMPTY_BORDER);

    JScrollPane displayScrollPane = new JScrollPane(displayBuilder.getPanel());
    displayScrollPane.setBorder(Borders.EMPTY_BORDER);

    JTabbedPane fieldPane = new JTabbedPane(JTabbedPane.BOTTOM);
    fieldPane.putClientProperty(Options.NO_CONTENT_BORDER_KEY, Boolean.TRUE);

    fieldPane.addTab("General", generalScrollPane);
    fieldPane.addTab("Display", displayScrollPane);

    return fieldPane;
  }

  public void setFieldProperties(ObjEntityViewField field){
    fieldEditorHelper.setObjEntityViewField(field);

    objEntityViewField = field;
    ObjEntityView objEntityView = field.getObjEntityView();
    viewNameField.setText(objEntityView.getName());

    fieldNameField.setText(field.getName());
    dataTypeCombo.setSelectedItem(field.getDataType());
    
    if (field.getCalcType().equals(CalcTypeEnum.NO_CALC_TYPE_NAME))
    {
      calcTypeCombo.setSelectedItem("No Calculation");
      objRelationshipCombo.setModel(new DefaultComboBoxModel());
      objRelationshipCombo.setEnabled(false);
      lookupViewCombo.setModel(new DefaultComboBoxModel());
      lookupViewCombo.setEnabled(false);
      lookupFieldCombo.setModel(new DefaultComboBoxModel());
      lookupFieldCombo.setEnabled(false);
    }
    else if (field.getCalcType().equals(CalcTypeEnum.CODE_TYPE_NAME))
    {
        calcTypeCombo.setSelectedItem("Code");
        objRelationshipCombo.setModel(new DefaultComboBoxModel());
        objRelationshipCombo.setEnabled(false);
        lookupViewCombo.setModel(new DefaultComboBoxModel());
        lookupViewCombo.setEnabled(false);
        lookupFieldCombo.setModel(new DefaultComboBoxModel());
        lookupFieldCombo.setEnabled(false);
      }
    else if (field.getCalcType().equals(CalcTypeEnum.LOOKUP_TYPE_NAME))
    {
      calcTypeCombo.setSelectedItem("Lookup");
      objAttributeCombo.setModel(new DefaultComboBoxModel());
      objAttributeCombo.setEnabled(false);
    }

    defaultValueField.setText(field.getDefaultValue());
    captionField.setText(field.getCaption());
    tooltipField.setText(field.getTooltip());
    editableCheckBox.setSelected(field.getEditable());
    visibleCheckBox.setSelected(field.getVisible());
    displayClassField.setText(field.getDisplayFormat().getClassName());
    displayPatternField.setText(field.getDisplayFormat().getPattern());
    editClassField.setText(field.getEditFormat().getClassName());
    editPatternField.setText(field.getEditFormat().getPattern());
    preferredIndexField.setValue(new Integer(field.getPrefIndex()));
  }

  public ObjEntityViewField getObjEntityViewField(){
    return objEntityViewField;
  }

  public JTextField getViewNameField(){
    return viewNameField;
  }
  public JComboBox getObjEntityCombo(){
    return objEntityCombo;
  }

  public JTextField getFieldNameField(){
    return fieldNameField;
  }
  public JComboBox getDataTypeCombo(){
    return dataTypeCombo;
  }
  public JComboBox getCalcTypeCombo(){
    return calcTypeCombo;
  }
  public JComboBox getObjAttributeCombo(){
    return objAttributeCombo;
  }
  public JTextField getDefaultValueField(){
    return defaultValueField;
  }
  public JTextField getCaptionField(){
    return captionField;
  }
  public JTextField getTooltipField(){
      return tooltipField;
  }
  public JCheckBox getEditableCheckBox(){
    return editableCheckBox;
  }
  public JCheckBox getVisibleCheckBox(){
    return visibleCheckBox;
  }
  public JTextField getDisplayClassField(){
    return displayClassField;
  }
  public JTextField getDisplayPatternField(){
    return displayPatternField;
  }
  public JTextField getEditClassField(){
    return editClassField;
  }
  public JTextField getEditPatternField(){
    return editPatternField;
  }
  public JSpinner getPreferredIndexField(){
    return preferredIndexField;
  }
  public JComboBox  getLookupViewCombo(){
    return lookupViewCombo;
  }
  public JComboBox  getLookupFieldCombo(){
    return lookupFieldCombo;
  }
  public JComboBox getObjRelationshipCombo(){
    return objRelationshipCombo;
  }

  public void setModels(DataMapTreeModel mapModel,
                        DataViewTreeModel viewModel,
                        FieldsTableModel tableModel){
    fieldEditorHelper.setModels(mapModel, viewModel, tableModel);
  }
}

