package com.example.demoplugin.views;

import javax.swing.*;

public class DialogPanelSimple {

  private final JPanel panel;

  public DialogPanelSimple() {
    panel = new JPanel();
  }

  public void showDialog() {
    JDialog dialog = new JDialog();
    dialog.setTitle("Dialog");
    dialog.setModal(true);
    dialog.setSize(400, 300);
    dialog.setLocationRelativeTo(null);
    dialog.add(panel);
    dialog.setVisible(true);
  }
}
