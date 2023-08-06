package com.example.demoplugin.views;

import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import java.util.List;
import javax.swing.*;

public class DialogPanelWithList {

  private final JPanel panel;
  private final JBList<String> list;
  private final JTextField searchField;

  private final List<String> itemList =
      List.of(
          "Item 1", "Item 2", "Item 3", "Item 4", "Item 6", "Item 7", "Item 8", "Item 9", "Item 10",
          "Item 11", "Item 12", "Item 13", "Item 14", "Item 15", "Item 16", "Item 17");

  public DialogPanelWithList() {
    panel = new JPanel();

    list = new JBList<>();
    list.setListData(itemList.toArray(new String[0]));
    JScrollPane scrollPane = new JBScrollPane(list);
    panel.add(scrollPane);

    searchField = new JTextField(20);
    panel.add(searchField);

    JButton filterButton = new JButton("Filter");
    filterButton.addActionListener(
        e -> {
          String filterText = searchField.getText();
          String[] filteredItems =
              itemList.stream()
                  .filter(item -> item.toLowerCase().contains(filterText.toLowerCase()))
                  .toArray(String[]::new);
          list.setListData(filteredItems);
          list.revalidate();
          list.repaint();
        });
    panel.add(filterButton);
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
