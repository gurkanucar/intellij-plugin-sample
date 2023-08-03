package com.example.demoplugin.action;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class HelloWorldActionGroup extends ActionGroup {
  public HelloWorldActionGroup() {
    super("Hello World Group", true);
  }

  @NotNull
  @Override
  public AnAction[] getChildren(@NotNull AnActionEvent e) {
    AnAction subAction1 =
        new AnAction("Sub Action 1") {
          @Override
          public void actionPerformed(@NotNull AnActionEvent e) {
            Messages.showInfoMessage("Sub Action 1!", "Info");
          }
        };

    AnAction subAction2 =
        new AnAction("Sub Action 2") {
          @Override
          public void actionPerformed(@NotNull AnActionEvent e) {
            Messages.showInfoMessage("Sub Action 2!", "Info");
          }
        };

    return new AnAction[] {subAction1, subAction2};
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    // This method will not be used as we're returning sub-actions in getChildren.
  }
}
