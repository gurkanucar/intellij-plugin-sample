package com.example.demoplugin.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.*;

public class GetterSetterTestGenerator extends AnAction {

  public GetterSetterTestGenerator() {
    super("GetterSetterTestGenerator");
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
        import org.junit.jupiter.api.Test;
        import java.time.LocalDate;
        import static org.junit.jupiter.api.Assertions.assertEquals;

        class %sTest {

            @Test
            void getterSetterTest() {
                LocalDate date = LocalDate.now();
                %s obj = new %s();
        """
            .formatted(mainClass.getName(), mainClass.getName(), mainClass.getName()));

    for (PsiField field : fields) {
      String fieldName = field.getName();
      String capitalizedFieldName =
          fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
      String exampleValue = generateExampleValue(field.getType(), psiJavaFile);

      testClassContent.append(
          """
                obj.set%s(%s);
                assertEquals(%s, obj.get%s());
        """
              .formatted(capitalizedFieldName, exampleValue, exampleValue, capitalizedFieldName));
    }

    testClassContent.append("    }\n").append("}\n");

    WriteCommandAction.runWriteCommandAction(
        psiFile.getProject(),
        () -> {
          PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(psiJavaFile.getProject());
          PsiFile testFile =
              psiFileFactory.createFileFromText(
                  mainClass.getName() + "Test.java", testClassContent.toString());
          psiJavaFile.getContainingDirectory().add(testFile);
        });
  }

  private String generateExampleValue(PsiType fieldType, PsiJavaFile psiJavaFile) {
    if (PsiType.INT.equals(fieldType)) {
      return "123";
    } else if (PsiType.LONG.equals(fieldType)) {
      return "123L";
    } else if (PsiType.FLOAT.equals(fieldType)) {
      return "123.45f";
    } else if (PsiType.DOUBLE.equals(fieldType)) {
      return "123.45";
    } else if (PsiType.CHAR.equals(fieldType)) {
      return "'A'";
    } else if (PsiType.BOOLEAN.equals(fieldType)) {
      return "true";
    } else if ("java.lang.String".equals(fieldType.getCanonicalText())) {
      return "\"value\"";
    } else if ("java.math.BigDecimal".equals(fieldType.getCanonicalText())) {
      return "new java.math.BigDecimal(\"123.45\")";
    } else if ("java.lang.Long".equals(fieldType.getCanonicalText())) {
      return "123L";
    } else if ("java.lang.Integer".equals(fieldType.getCanonicalText())) {
      return "123";
    } else if (PsiType.getTypeByName(
            "java.time.LocalDate", psiJavaFile.getProject(), psiJavaFile.getResolveScope())
        .equals(fieldType)) {
      return "date";
    } else {
      return "null";
    }
  }
}
