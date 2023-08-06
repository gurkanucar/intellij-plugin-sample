package com.example.demoplugin.action;

import com.example.demoplugin.views.DialogPanelSimple;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import javax.swing.*;

public class DialogPanelViewer extends AnAction {

  private final DialogPanelSimple dialogWrapper;

  public DialogPanelViewer() {
    super("DialogPanelViewer");
    dialogWrapper = new DialogPanelSimple();
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    dialogWrapper.showDialog();
  }
}
