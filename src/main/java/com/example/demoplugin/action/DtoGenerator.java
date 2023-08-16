package com.example.demoplugin.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.*;

public class DtoGenerator extends AnAction {

  public DtoGenerator() {
    super("DtoGenerator");
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
    if (!(psiFile instanceof PsiJavaFile psiJavaFile)) {
      return;
    }

    PsiClass[] classes = psiJavaFile.getClasses();
    if (classes.length == 0) {
      return;
    }

    PsiClass mainClass = classes[0];

    PsiField[] fields = mainClass.getFields();

    StringBuilder testClassContent = new StringBuilder();

    testClassContent.append(
        """

        import lombok.Getter;
        import lombok.Setter;

        @Getter
        @Setter
        class %sDto {

        """
            .formatted(mainClass.getName()));

    for (PsiField field : fields) {
      String type = field.getType().getCanonicalText();
      String fieldName = field.getName();

      testClassContent.append(
          """
              private %s %s;
        """.formatted(type, fieldName));
    }

    testClassContent.append("    }\n").append("}\n");

    WriteCommandAction.runWriteCommandAction(
        psiFile.getProject(),
        () -> {
          PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(psiJavaFile.getProject());
          PsiFile testFile =
              psiFileFactory.createFileFromText(
                  mainClass.getName() + "Dto.java", testClassContent.toString());
          psiJavaFile.getContainingDirectory().add(testFile);
        });
  }
}
