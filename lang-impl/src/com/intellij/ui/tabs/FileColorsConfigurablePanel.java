package com.intellij.ui.tabs;

import com.intellij.ide.ui.UISettings;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.ui.MessageType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author spleaner
 */
public class FileColorsConfigurablePanel extends JPanel implements Disposable {
  private FileColorManagerImpl myManager;
  private JCheckBox myEnabledCheckBox;
  private JCheckBox myTabsEnabledCheckBox;
  private FileColorSettingsTable myLocalTable;
  private FileColorSettingsTable mySharedTable;

  public FileColorsConfigurablePanel(@NotNull final FileColorManagerImpl manager) {
    setLayout(new BorderLayout());

    myManager = manager;

    final JPanel topPanel = new JPanel();
    //topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
    topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

    myEnabledCheckBox = new JCheckBox("Enable File Colors");
    myEnabledCheckBox.setMnemonic('F');
    topPanel.add(myEnabledCheckBox);

    myTabsEnabledCheckBox = new JCheckBox("Enable Colors in Editor Tabs");
    myTabsEnabledCheckBox.setMnemonic('T');
    topPanel.add(myTabsEnabledCheckBox);
    topPanel.add(Box.createHorizontalGlue());

    final JButton addButton = new JButton("Add...");
    addButton.setMnemonic('A');
    addButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        final FileColorConfigurationEditDialog dialog = new FileColorConfigurationEditDialog(myManager, null);
        dialog.show();

        if (dialog.getExitCode() == 0) {
          if (dialog.isShared()) {
            mySharedTable.addConfiguration(dialog.getConfiguration());
          } else {
            myLocalTable.addConfiguration(dialog.getConfiguration());
          }
        }
      }
    });

    topPanel.add(addButton);
    add(topPanel, BorderLayout.NORTH);

    final JPanel mainPanel = new JPanel(new GridLayout(2, 1));
    mainPanel.setPreferredSize(new Dimension(300, 500));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    myLocalTable = new FileColorSettingsTable(manager, manager.getLocalConfigurations()) {
      protected void apply(@NotNull List<FileColorConfiguration> configurations) {
        final List<FileColorConfiguration> copied = new ArrayList<FileColorConfiguration>();
        for (final FileColorConfiguration configuration : configurations) {
          try {
            copied.add(configuration.clone());
          }
          catch (CloneNotSupportedException e) {
            assert false : "Should not happen!";
          }
        }
        manager.getModel().setConfigurations(copied, false);
      }
    };

    final JPanel localPanel = new JPanel(new BorderLayout());
    localPanel.setBorder(BorderFactory.createTitledBorder("Local colors:"));
    //localPanel.add(new JLabel("Local colors:"), BorderLayout.NORTH);
    localPanel.add(BetterJTable.createStripedJScrollPane(myLocalTable), BorderLayout.CENTER);
    localPanel.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);
    localPanel.add(buildButtons(myLocalTable, "Share", new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        share();
      }
    }), BorderLayout.EAST);
    mainPanel.add(localPanel);

    mySharedTable = new FileColorSettingsTable(manager, manager.getSharedConfigurations()) {
      protected void apply(@NotNull List<FileColorConfiguration> configurations) {
        final List<FileColorConfiguration> copied = new ArrayList<FileColorConfiguration>();
        for (final FileColorConfiguration configuration : configurations) {
          try {
            copied.add(configuration.clone());
          }
          catch (CloneNotSupportedException e) {
            assert false : "Should not happen!";
          }
        }
        manager.getModel().setConfigurations(copied, true);
      }
    };

    final JPanel sharedPanel = new JPanel(new BorderLayout());
    sharedPanel.setBorder(BorderFactory.createTitledBorder("Shared colors:"));
    //sharedPanel.add(new JLabel("Shared colors:"), BorderLayout.NORTH);
    sharedPanel.add(BetterJTable.createStripedJScrollPane(mySharedTable), BorderLayout.CENTER);
    sharedPanel.add(buildButtons(mySharedTable, "Unshare", new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        unshare();
      }
    }), BorderLayout.EAST);
    mainPanel.add(sharedPanel);

    add(mainPanel, BorderLayout.CENTER);

    final JPanel warningPanel = new JPanel(new BorderLayout());
    warningPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    warningPanel.add(new JLabel("Scopes are processed from top to bottom with Local colors first.",
                             MessageType.WARNING.getDefaultIcon(), SwingConstants.LEFT));
    add(warningPanel, BorderLayout.SOUTH);
  }

  private void unshare() {
    final int rowCount = mySharedTable.getSelectedRowCount();
    if (rowCount > 0) {
      final int[] rows = mySharedTable.getSelectedRows();
      for (int i = rows.length - 1; i >= 0; i--) {
        FileColorConfiguration removed = mySharedTable.removeConfiguration(rows[i]);
        if (removed != null) {
          myLocalTable.addConfiguration(removed);
        }
      }
    }
  }

  private void share() {
    final int rowCount = myLocalTable.getSelectedRowCount();
    if (rowCount > 0) {
      final int[] rows = myLocalTable.getSelectedRows();
      for (int i = rows.length - 1; i >= 0; i--) {
        FileColorConfiguration removed = myLocalTable.removeConfiguration(rows[i]);
        if (removed != null) {
          mySharedTable.addConfiguration(removed);
        }
      }
    }
  }

  private static Component buildButtons(final FileColorSettingsTable table, final String shareButtonText, final ActionListener shareButtonListener) {
    final JPanel result = new JPanel();
    result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));

    final JButton removeButton = new JButton("Remove");
    result.add(removeButton);
    removeButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, removeButton.getMaximumSize().height));
    removeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        table.performRemove();
      }
    });

    final JButton shareButton = new JButton(shareButtonText);
    result.add(shareButton);
    shareButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, shareButton.getMaximumSize().height));
    shareButton.addActionListener(shareButtonListener);

    final JButton upButton = new JButton("Move up");
    result.add(upButton);
    upButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, upButton.getMaximumSize().height));
    upButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        table.moveUp();
      }
    });

    final JButton downButton = new JButton("Move down");
    result.add(downButton);
    downButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, downButton.getMaximumSize().height));
    downButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        table.moveDown();
      }
    });

    return result;
  }

  public void dispose() {
    myManager = null;
  }

  public boolean isModified() {
    boolean modified;

    modified = myEnabledCheckBox.isSelected() != myManager.isEnabled();
    modified |= myTabsEnabledCheckBox.isSelected() != myManager.isEnabledForTabs();
    modified |= myLocalTable.isModified() || mySharedTable.isModified();

    return modified;
  }

  public void apply() {
    myManager.setEnabled(myEnabledCheckBox.isSelected());
    myManager.setEnabledForTabs(myTabsEnabledCheckBox.isSelected());

    myLocalTable.apply();
    mySharedTable.apply();

    UISettings.getInstance().fireUISettingsChanged();
  }

  public void reset() {
    myEnabledCheckBox.setSelected(myManager.isEnabled());
    myTabsEnabledCheckBox.setSelected(myManager.isEnabledForTabs());

    if(myLocalTable.isModified()) myLocalTable.reset();
    if(mySharedTable.isModified()) mySharedTable.reset();
  }
}