package com.example.demoplugin.action;

import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;

import java.util.Arrays;

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

        PsiClass mainClass = getMainClass(psiJavaFile);
        if (mainClass == null) {
            return;
        }

        String testClassContent = generateTestClassContent(mainClass, psiJavaFile.getPackageName());

        WriteCommandAction.runWriteCommandAction(
                psiFile.getProject(),
                () -> createTestFile(psiJavaFile, mainClass.getName(), testClassContent)
        );
    }

    private PsiClass getMainClass(PsiJavaFile psiJavaFile) {
        PsiClass[] classes = psiJavaFile.getClasses();
        return classes.length > 0 ? classes[0] : null;
    }

    private String generateTestClassContent(PsiClass mainClass, String packageName) {
        StringBuilder testClassContent = new StringBuilder();
        testClassContent.append("package ").append(packageName).append(";\n\n");
        testClassContent.append(generateTestClassHeader(mainClass.getName()));

        for (PsiField field : mainClass.getFields()) {
            testClassContent.append(generateTestForField(field));
        }

        testClassContent.append("    }\n").append("}\n");
        return testClassContent.toString();
    }

    private String generateTestClassHeader(String className) {
        return String.format("""
                import org.junit.jupiter.api.Test;
                import static org.junit.jupiter.api.Assertions.assertEquals;
                import static org.junit.jupiter.api.Assertions.assertNull;

                class %sTest {

                    @Test
                    void getterSetterTest() {
                        %s obj = new %s();
                """, className, className, className);
    }

    private String generateTestForField(PsiField field) {
        String fieldName = field.getName();
        String capitalizedFieldName = capitalizeFirstLetter(fieldName);
        String exampleValue = generateExampleValue(field.getType());

        if ("null".equals(exampleValue)) {
            return String.format("""
                        obj.set%s(%s);
                        assertNull(obj.get%s());
                """, capitalizedFieldName, exampleValue, capitalizedFieldName);
        } else {
            return String.format("""
                        obj.set%s(%s);
                        assertEquals(%s, obj.get%s());
                """, capitalizedFieldName, exampleValue, exampleValue, capitalizedFieldName);
        }
    }

    private String capitalizeFirstLetter(String str) {
        return (str == null || str.isEmpty()) ? str : Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    private void createTestFile(PsiJavaFile psiJavaFile, String className, String content) {
        PsiDirectory testRootDirectory = findOrCreateTestRootDirectory(psiJavaFile.getContainingDirectory());
        PsiDirectory packageDirectory = createPackageDirectories(testRootDirectory, psiJavaFile.getPackageName());

        PsiFile testFile = PsiFileFactory.getInstance(psiJavaFile.getProject())
                .createFileFromText(className + "Test.java", JavaLanguage.INSTANCE, content);
        packageDirectory.add(testFile);
    }

    private PsiDirectory createPackageDirectories(PsiDirectory rootDirectory, String packageName) {
        PsiDirectory currentDirectory = rootDirectory;
        for (String packageComponent : packageName.split("\\.")) {
            PsiDirectory finalCurrentDirectory = currentDirectory;
            currentDirectory = Arrays.stream(currentDirectory.getSubdirectories())
                    .filter(dir -> dir.getName().equals(packageComponent))
                    .findFirst()
                    .orElseGet(() -> finalCurrentDirectory.createSubdirectory(packageComponent));
        }
        return currentDirectory;
    }

    private PsiDirectory findOrCreateTestRootDirectory(PsiDirectory mainDirectory) {
        VirtualFile projectRootVirtualFile = mainDirectory.getProject().getBaseDir();
        if (projectRootVirtualFile == null) {
            throw new IllegalStateException("Cannot find the project root directory.");
        }

        PsiDirectory projectRoot = PsiManager.getInstance(mainDirectory.getProject()).findDirectory(projectRootVirtualFile);
        if (projectRoot == null) {
            throw new IllegalStateException("Cannot convert the project root VirtualFile to a PsiDirectory.");
        }

        String[] pathComponents = {"src", "test", "java"};
        PsiDirectory currentDirectory = projectRoot;
        for (String dirName : pathComponents) {
            PsiDirectory finalCurrentDirectory = currentDirectory;
            currentDirectory = Arrays.stream(currentDirectory.getSubdirectories())
                    .filter(dir -> dir.getName().equals(dirName))
                    .findFirst()
                    .orElseGet(() -> finalCurrentDirectory.createSubdirectory(dirName));
        }
        return currentDirectory;
    }

    private String generateExampleValue(PsiType fieldType) {
        String typeText = fieldType.getCanonicalText();
        switch (typeText) {
            case "int", "java.lang.Integer":
                return "123";
            case "long", "java.lang.Long":
                return "123L";
            case "float":
                return "123.45f";
            case "double":
                return "123.45";
            case "char":
                return "'A'";
            case "boolean":
                return "true";
            case "java.lang.String":
                return "\"value\"";
            case "java.math.BigDecimal":
                return "new java.math.BigDecimal(\"123.45\")";
            case "java.util.Date":
                return "new java.util.Date()";
            case "java.time.LocalDate":
                return "java.time.LocalDate.now()";
            case "java.time.LocalDateTime":
                return "java.time.LocalDateTime.now()";
            case "java.time.LocalTime":
                return "java.time.LocalTime.now()";
            case "java.time.OffsetDateTime":
                return "java.time.OffsetDateTime.now()";
            case "java.time.ZonedDateTime":
                return "java.time.ZonedDateTime.now()";
            case "java.time.Instant":
                return "java.time.Instant.now()";
            default:
                return "null";
        }
    }
}
