package com.example.demoplugin.action.nestedActions;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class NestedSubActionGroup2 extends ActionGroup {
  public NestedSubActionGroup2() {
    super("Hello World Group", true);
  }

  @NotNull
  @Override
  public AnAction[] getChildren(@NotNull AnActionEvent e) {
    return new AnAction[] {
      createSubAction("Sub Action 1", "Sub Action 1!"),
      createSubAction("Sub Action 2", "Sub Action 2!"),
      createSubGroup("Nested SubGroup", "Sub Sub Action 1!", "Sub Sub Action 2!")
    };
  }

  private AnAction createSubAction(String name, String message) {
    return new AnAction(name) {
      @Override
      public void actionPerformed(@NotNull AnActionEvent e) {
        Messages.showInfoMessage(message, "Info");
      }
    };
  }

  private AnAction createSubGroup(String groupName, String... subActionNames) {
    ActionGroup subGroup =
        new ActionGroup(groupName, true) {
          @NotNull
          @Override
          public AnAction[] getChildren(@NotNull AnActionEvent e) {
            AnAction[] subActions = new AnAction[subActionNames.length];
            for (int i = 0; i < subActionNames.length; i++) {
              String name = subActionNames[i];
              subActions[i] = createSubAction(name, name + "!");
            }
            return subActions;
          }

          @Override
          public void actionPerformed(@NotNull AnActionEvent e) {}
        };

    return subGroup;
  }
}
