package com.example.demoplugin.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class HelloWorldAction extends AnAction {
  public HelloWorldAction() {
    super("Hello World");
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Messages.showInfoMessage("Hello World!", "Info");
  }
}
