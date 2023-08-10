package com.example.demoplugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jetbrains.annotations.NotNull;

public class MyStartupActivity implements StartupActivity {
  @Override
  public void runActivity(@NotNull Project project) {
    // System.out.println("IntelliJ IDEA plugin is opening.");
    // String filePath = "D:\\test.txt";
    // readAndPrintFile(filePath);
  }

  private static void readAndPrintFile(String filePath) {
    try {
      Path path = Paths.get(filePath);
      byte[] fileBytes = Files.readAllBytes(path);
      String fileContent = new String(fileBytes, StandardCharsets.UTF_8);
      System.out.println("File content:");
      System.out.println(fileContent);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
