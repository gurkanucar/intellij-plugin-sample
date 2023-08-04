package com.example.demoplugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

public class MyStartupActivity implements StartupActivity {
  @Override
  public void runActivity(@NotNull Project project) {
    System.out.println("IntelliJ IDEA plugin is opening.");
  }
}
