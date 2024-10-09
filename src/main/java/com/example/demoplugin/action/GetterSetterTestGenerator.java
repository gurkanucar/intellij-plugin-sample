package com.example.demoplugin.action;

import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;

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

        String testClassContent = generateTestClassContent(mainClass, psiJavaFile);

        WriteCommandAction.runWriteCommandAction(
                psiFile.getProject(),
                () -> createTestFile(psiJavaFile, mainClass.getName(), testClassContent)
        );
    }

    private PsiClass getMainClass(PsiJavaFile psiJavaFile) {
        PsiClass[] classes = psiJavaFile.getClasses();
        return classes.length > 0 ? classes[0] : null;
    }

    private String generateTestClassContent(PsiClass mainClass, PsiJavaFile psiJavaFile) {
        StringBuilder testClassContent = new StringBuilder();
        String packageName = psiJavaFile.getPackageName();
        testClassContent.append("package ").append(packageName).append(";\n\n");
        testClassContent.append(generateTestClassHeader(mainClass.getName()));

        for (PsiField field : mainClass.getFields()) {
            testClassContent.append(generateTestForField(field, psiJavaFile));
        }

        testClassContent.append("    }\n").append("}\n");
        return testClassContent.toString();
    }

    private String generateTestClassHeader(String className) {
        return String.format("""
                import org.junit.jupiter.api.Test;
                import java.time.LocalDate;
                import static org.junit.jupiter.api.Assertions.assertEquals;
                
                class %sTest {
                
                    @Test
                    void getterSetterTest() {
                        %s obj = new %s();
                """, className, className, className);
    }

    private String generateTestForField(PsiField field, PsiJavaFile psiJavaFile) {
        String fieldName = field.getName();
        String capitalizedFieldName = capitalizeFirstLetter(fieldName);
        String exampleValue = generateExampleValue(field.getType(), psiJavaFile);

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
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    private void createTestFile(PsiJavaFile psiJavaFile, String className, String content) {
        PsiDirectory mainDirectory = psiJavaFile.getContainingDirectory();
        PsiDirectory testRootDirectory = findOrCreateTestRootDirectory(mainDirectory);

        String packageName = psiJavaFile.getPackageName();
        JavaDirectoryService javaDirectoryService = JavaDirectoryService.getInstance();

        PsiPackage testPackage = JavaPsiFacade.getInstance(psiJavaFile.getProject()).findPackage(packageName);
        PsiDirectory packageDirectory;

        if (testPackage != null) {
            PsiDirectory[] directories = testPackage.getDirectories(GlobalSearchScope.projectScope(psiJavaFile.getProject()));
            // Find the directory under testRootDirectory
            packageDirectory = Arrays.stream(directories)
                    .filter(dir -> VfsUtil.isAncestor(testRootDirectory.getVirtualFile(), dir.getVirtualFile(), false))
                    .findFirst()
                    .orElse(null);
            if (packageDirectory == null) {
                // Package exists but not under testRootDirectory, so we need to create it
                packageDirectory = createPackageDirectories(testRootDirectory, packageName);
            }
        } else {
            // Package doesn't exist, create it under testRootDirectory
            packageDirectory = createPackageDirectories(testRootDirectory, packageName);
        }

        PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(psiJavaFile.getProject());
        PsiFile testFile = psiFileFactory.createFileFromText(className + "Test.java", JavaLanguage.INSTANCE, content);
        packageDirectory.add(testFile);
    }

    private PsiDirectory createPackageDirectories(PsiDirectory rootDirectory, String packageName) {
        String[] packageComponents = packageName.split("\\.");
        PsiDirectory currentDirectory = rootDirectory;

        for (String packageComponent : packageComponents) {
            PsiDirectory subdirectory = currentDirectory.findSubdirectory(packageComponent);
            if (subdirectory == null) {
                subdirectory = currentDirectory.createSubdirectory(packageComponent);
            }
            currentDirectory = subdirectory;
        }
        return currentDirectory;
    }

    private PsiDirectory findOrCreateTestRootDirectory(PsiDirectory mainDirectory) {
        // Get the project and PsiManager
        PsiManager psiManager = PsiManager.getInstance(mainDirectory.getProject());

        // Get the project root as a VirtualFile
        VirtualFile projectRootVirtualFile = mainDirectory.getProject().getBaseDir();
        if (projectRootVirtualFile == null) {
            throw new IllegalStateException("Cannot find the project root directory.");
        }

        // Convert the VirtualFile to a PsiDirectory
        PsiDirectory projectRoot = psiManager.findDirectory(projectRootVirtualFile);
        if (projectRoot == null) {
            throw new IllegalStateException("Cannot convert the project root VirtualFile to a PsiDirectory.");
        }

        // Navigate to or create src directory
        PsiDirectory srcDirectory = projectRoot.findSubdirectory("src");
        if (srcDirectory == null) {
            srcDirectory = projectRoot.createSubdirectory("src");
        }

        // Navigate to or create test directory
        PsiDirectory testDirectory = srcDirectory.findSubdirectory("test");
        if (testDirectory == null) {
            testDirectory = srcDirectory.createSubdirectory("test");
        }

        // Navigate to or create java directory under test
        PsiDirectory javaDirectory = testDirectory.findSubdirectory("java");
        if (javaDirectory == null) {
            javaDirectory = testDirectory.createSubdirectory("java");
        }

        return javaDirectory;
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
        } else if ("java.util.Date".equals(fieldType.getCanonicalText())) {
            return "new java.util.Date();";
        }  else if ("java.time.LocalDate".equals(fieldType.getCanonicalText())) {
            return "new java.time.LocalDate.now()";
        } else if ("java.time.LocalDateTime".equals(fieldType.getCanonicalText())) {
            return "new java.time.LocalDateTime.now()";
        } else if ("java.time.LocalTime".equals(fieldType.getCanonicalText())) {
            return "new java.time.LocalTime.now()";
        } else if ("java.time.OffsetDateTime".equals(fieldType.getCanonicalText())) {
            return "new java.time.OffsetDateTime.now()";
        } else if ("java.time.ZonedDateTime".equals(fieldType.getCanonicalText())) {
            return "new java.time.ZonedDateTime.now()";
        } else if ("java.time.Instant".equals(fieldType.getCanonicalText())) {
            return "new java.time.Instant.now()";
        } else {
            return "null";
        }
    }

}