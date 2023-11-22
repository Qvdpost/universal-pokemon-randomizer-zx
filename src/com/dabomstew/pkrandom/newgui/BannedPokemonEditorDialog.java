package com.dabomstew.pkrandom.newgui;

/*----------------------------------------------------------------------------*/
/*--  BannedPokemonEditorDIalog.java - a GUI interface to allow users to    --*/
/*--                                 edit the Pokemon they don't want to    --*/
/*--                                 have show up in the randomizers.       --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer ZX" by the UPR-ZX team          --*/
/*--  Originally part of "Universal Pokemon Randomizer" by Dabomstew        --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2020.                                 --*/
/*--                                                                        --*/
/*--  The custom code written here is licensed under the terms of the GPL:  --*/
/*--                                                                        --*/
/*--  This program is free software: you can redistribute it and/or modify  --*/
/*--  it under the terms of the GNU General Public License as published by  --*/
/*--  the Free Software Foundation, either version 3 of the License, or     --*/
/*--  (at your option) any later version.                                   --*/
/*--                                                                        --*/
/*--  This program is distributed in the hope that it will be useful,       --*/
/*--  but WITHOUT ANY WARRANTY; without even the implied warranty of        --*/
/*--  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the          --*/
/*--  GNU General Public License for more details.                          --*/
/*--                                                                        --*/
/*--  You should have received a copy of the GNU General Public License     --*/
/*--  along with this program. If not, see <http://www.gnu.org/licenses/>.  --*/
/*----------------------------------------------------------------------------*/

import com.dabomstew.pkrandom.BannedPokemonSet;
import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.SysConstants;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BannedPokemonEditorDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = -1421503126547242929L;
    private boolean pendingChanges;

    /**
     * Creates new form BannedPokemonEditorDialog
     */
    public BannedPokemonEditorDialog(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        setLocationRelativeTo(parent);

        java.awt.EventQueue.invokeLater(() -> setVisible(true));

        // load trainer names etc
        try {
            BannedPokemonSet bnd = FileFunctions.getBannedPokemon();
            populatePokemon(bannedPokemonText, bnd.getBannedPokemon());

        } catch (IOException ex) {
            java.awt.EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(BannedPokemonEditorDialog.this,
                    "Your banned pokemon file is for a different randomizer version or otherwise corrupt."));
        }

        // dialog if there's no custom names file yet
        if (!new File(SysConstants.ROOT_PATH + SysConstants.bannedPokemonFile).exists()) {
            java.awt.EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(
                    BannedPokemonEditorDialog.this,
                    String.format(
                            "Welcome to the banned Pokemon editor!\nThis is where you can edit the Dex ID's used for the option in \"Limit Pokemon\".\nThe ID's are initially empty.\nYou can share your banned ID sets with others, too!\nJust send them the %s file created in the randomizer directory.",
                            SysConstants.bannedPokemonFile)));
        }

        pendingChanges = false;

        addDocListener(bannedPokemonText);
    }

    private void addDocListener(JTextArea textArea) {
        textArea.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                pendingChanges = true;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                pendingChanges = true;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                pendingChanges = true;
            }
        });

    }

    private void formWindowClosing() {// GEN-FIRST:event_formWindowClosing
        attemptClose();
    }// GEN-LAST:event_formWindowClosing

    private void saveBtnActionPerformed() {// GEN-FIRST:event_saveBtnActionPerformed
        save();
    }// GEN-LAST:event_saveBtnActionPerformed

    private void closeBtnActionPerformed() {// GEN-FIRST:event_closeBtnActionPerformed
        attemptClose();
    }// GEN-LAST:event_closeBtnActionPerformed

    private boolean save() {
        BannedPokemonSet bnd = new BannedPokemonSet();
        try {
            bnd.setBannedPokemon(getPokemonList(bannedPokemonText));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Banned Pokemon are only allowed as numeric Pokedex ID's.");
            return false;
        }
        try {
            byte[] data = bnd.getBytes();
            FileFunctions.writeBytesToFile(SysConstants.ROOT_PATH + SysConstants.bannedPokemonFile, data);
            pendingChanges = false;
            JOptionPane.showMessageDialog(this, "Banned Pokemon saved.");
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not save changes.");
            return false;
        }
    }

    private void attemptClose() {
        if (pendingChanges) {
            int result = JOptionPane
                    .showConfirmDialog(this,
                            "You've made some unsaved changes to your banned Pokemon.\nDo you want to save them before closing the editor?");
            if (result == JOptionPane.YES_OPTION) {
                if (save()) {
                    dispose();
                }
            } else if (result == JOptionPane.NO_OPTION) {
                dispose();
            }
        } else {
            dispose();
        }
    }

    private List<Integer> getPokemonList(JTextArea textArea) throws NumberFormatException {
        String contents = textArea.getText();
        // standardize newlines
        contents = contents.replace("\r\n", "\n");
        contents = contents.replace("\r", "\n");
        // split by them
        String[] pokemon = contents.split("\n");
        List<Integer> results = new ArrayList<>();
        for (String id : pokemon) {
            String ln = id.trim();
            if (!ln.isEmpty()) {
                results.add(Integer.parseInt(ln));
            }
        }
        return results;
    }

    private void populatePokemon(JTextArea textArea, List<Integer> pokemon) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Integer uid : pokemon) {
            if (!first) {
                sb.append(SysConstants.LINE_SEP);
            }
            first = false;
            sb.append(uid);
        }
        textArea.setText(sb.toString());
    }

    /* @formatter:off */
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        editorTabsPane = new javax.swing.JTabbedPane();
        bannedPokemonSP = new javax.swing.JScrollPane();
        bannedPokemonText = new JTextArea();
        saveBtn = new javax.swing.JButton();
        closeBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/dabomstew/pkrandom/newgui/Bundle");
        setTitle(bundle.getString("BannedPokemonEditorDialog.title"));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing();
            }
        });

        bannedPokemonSP.setHorizontalScrollBar(null);

        bannedPokemonText.setColumns(20);
        bannedPokemonText.setRows(5);
        bannedPokemonSP.setViewportView(bannedPokemonText);

        editorTabsPane.addTab(bundle.getString("BannedPokemonEditorDialog.bannedPokemonSP.TabConstraints.tabTitle"), bannedPokemonSP);

        saveBtn.setText(bundle.getString("BannedPokemonEditorDialog.saveBtn.text"));
        saveBtn.addActionListener(evt -> saveBtnActionPerformed());

        closeBtn.setText(bundle.getString("BannedPokemonEditorDialog.closeBtn.text"));
        closeBtn.addActionListener(evt -> closeBtnActionPerformed());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(editorTabsPane, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(saveBtn)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(closeBtn)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(editorTabsPane, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(saveBtn)
                                        .addComponent(closeBtn))
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeBtn;
    private javax.swing.JTabbedPane editorTabsPane;
    private javax.swing.JButton saveBtn;
    private javax.swing.JScrollPane bannedPokemonSP;
    private JTextArea bannedPokemonText;
    // End of variables declaration//GEN-END:variables
    /* @formatter:on */
}
