package com.example.demoplugin.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

public class DetectClickedPath extends AnAction {

  public DetectClickedPath() {
    super("DetectClickedPath");
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    VirtualFile virtualFile = e.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);
    //    if (virtualFile == null || !virtualFile.isDirectory()) {
    //      System.out.println("err");
    //      return;
    //    }
    String packagePath = virtualFile.getPath();
    System.out.println("Clicked package folder path: " + packagePath);
    Messages.showInfoMessage("Clicked package folder path: " + packagePath, "Info");
  }
}
