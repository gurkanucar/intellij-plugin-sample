package com.example.demoplugin.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.*;

public class CreateFields extends AnAction {

  public CreateFields() {
    super("CreateFields");
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

    for (PsiField field : fields) {
      System.out.println("Field: " + field.getName() + ", Type: " + field.getType().getPresentableText());
    }

    WriteCommandAction.runWriteCommandAction(psiFile.getProject(), () -> {
      PsiElementFactory factory = JavaPsiFacade.getElementFactory(psiJavaFile.getProject());
      PsiField newField = factory.createField("newField", factory.createTypeFromText("int", null));
      mainClass.add(newField);

      System.out.println("Added Field: " + newField.getName() + ", Type: " + newField.getType().getPresentableText());
    });
  }


}
