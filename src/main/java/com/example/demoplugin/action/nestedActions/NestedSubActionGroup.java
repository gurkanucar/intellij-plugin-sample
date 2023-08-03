package com.example.demoplugin.action.nestedActions;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class NestedSubActionGroup extends ActionGroup {
  public NestedSubActionGroup() {
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

    AnAction subGroupAction = new SubGroup();

    return new AnAction[] {subAction1, subAction2, subGroupAction};
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
  }
}

class SubGroup extends ActionGroup {
  public SubGroup() {
    super("Nested SubGroup", true);
  }

  @NotNull
  @Override
  public AnAction[] getChildren(@NotNull AnActionEvent e) {
    AnAction subSubAction1 =
        new AnAction("Sub Sub Action 1") {
          @Override
          public void actionPerformed(@NotNull AnActionEvent e) {
            Messages.showInfoMessage("Sub Sub Action 1!", "Info");
          }
        };

    AnAction subSubAction2 =
        new AnAction("Sub Sub Action 2") {
          @Override
          public void actionPerformed(@NotNull AnActionEvent e) {
            Messages.showInfoMessage("Sub Sub Action 2!", "Info");
          }
        };

    return new AnAction[] {subSubAction1, subSubAction2};
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
  }
}
