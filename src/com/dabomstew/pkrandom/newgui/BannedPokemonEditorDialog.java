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
import com.dabomstew.pkrandom.pokemon.Pokemon;
import com.dabomstew.pkrandom.pokemon.Type;
import com.dabomstew.pkrandom.romhandlers.RomHandler;
import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BannedPokemonEditorDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = -1421503126547242929L;
    private boolean pendingChanges;
    private List<Pokemon> allPokemon;
    private BannedPokemonSet bannedPokemon;


    /**
     * Creates new form BannedPokemonEditorDialog
     */
    public BannedPokemonEditorDialog(java.awt.Frame parent, RomHandler romHandler) {
        super(parent, true);

        if (romHandler == null) {
            java.awt.EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(BannedPokemonEditorDialog.this,
                    "You need to load a rom to use the Banned Pokemon Editor."));
            return;
        }

        initComponents();
        setLocationRelativeTo(parent);

        java.awt.EventQueue.invokeLater(() -> setVisible(true));

        // load trainer names etc
        try {
            bannedPokemon = FileFunctions.getBannedPokemon();
            populatePokemon(bannedPokemonText, bannedPokemon.getBannedPokemon());

        } catch (IOException ex) {
            java.awt.EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(BannedPokemonEditorDialog.this,
                    "Your banned pokemon file is for a different randomizer version or otherwise corrupt."));
        }

        // dialog if there's no custom names file yet
        if (!new File(SysConstants.ROOT_PATH + SysConstants.bannedPokemonFile).exists()) {
            java.awt.EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(
                    BannedPokemonEditorDialog.this,
                    String.format(
                            "Welcome to the banned Pokemon editor!\nThis is where you can edit the PokeDex ID's used for the option in \"Limit Pokemon\".\nThe ID's are initially empty, but you can either add Pokemon ID's (eg. '1' to ban Bulbasaur) or use the buttons on the side.\nYou can share your banned ID sets with others, too!\nJust send them the %s file created in the randomizer directory.",
                            SysConstants.bannedPokemonFile)));
        }

        pendingChanges = false;
        allPokemon = romHandler.getPokemon();

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

    private void populatePokemon(JTextArea textArea, Set<Integer> pokemon) {
        textArea.setText("");
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
        textArea.update(textArea.getGraphics());
    }

    private JCheckBox[] getPokemonTypes() {
        return new JCheckBox[]{checkBoxNORMAL, checkBoxFIRE, checkBoxWATER, checkBoxGRASS, checkBoxFLYING, checkBoxFIGHTING, checkBoxPOISON,
                               checkBoxELECTRIC, checkBoxGROUND, checkBoxROCK, checkBoxPSYCHIC, checkBoxICE, checkBoxBUG, checkBoxGHOST,
                               checkBoxSTEEL, checkBoxDRAGON, checkBoxDARK, checkBoxFAIRY
        };
    }

    private void selectAllTypesActionPerformed() {
        List<Boolean> typeBoxesSelected = new ArrayList<>();

        for (JCheckBox typeBox : getPokemonTypes()) {
            typeBoxesSelected.add(typeBox.isSelected());
        }

        boolean allSelected = !typeBoxesSelected.contains(false);

        for (JCheckBox typeBox : getPokemonTypes()) {
            typeBox.setSelected(!allSelected);
        }
    }

    private boolean typeIsSelected(com.dabomstew.pkrandom.pokemon.Type pokeType) {
        switch (pokeType) {
            case NORMAL:
                return checkBoxNORMAL.isSelected();
            case FIGHTING:
                return checkBoxFIGHTING.isSelected();
            case FLYING:
				return checkBoxFLYING.isSelected();
            case GRASS:
				return checkBoxGRASS.isSelected();
            case WATER:
				return checkBoxWATER.isSelected();
            case FIRE:
				return checkBoxFIRE.isSelected();
            case ROCK:
				return checkBoxROCK.isSelected();
            case GROUND:
				return checkBoxGROUND.isSelected();
            case PSYCHIC:
				return checkBoxPSYCHIC.isSelected();
            case BUG:
				return checkBoxBUG.isSelected();
            case DRAGON:
				return checkBoxDRAGON.isSelected();
            case ELECTRIC:
				return checkBoxELECTRIC.isSelected();
            case GHOST:
				return checkBoxGHOST.isSelected();
            case POISON:
				return checkBoxPOISON.isSelected();
            case ICE:
				return checkBoxICE.isSelected();
            case STEEL:
				return checkBoxSTEEL.isSelected();
            case DARK:
				return checkBoxDARK.isSelected();
            case FAIRY:
				return checkBoxFAIRY.isSelected();

        }
        return false;
    }

    private void banPokemon(Pokemon poke) {
        bannedPokemon.addBannedPokemon(poke.number);
    }

    private void unbanPokemon(Pokemon poke) {
        bannedPokemon.removeBannedPokemon(poke.number);
    }
    private void banByTypeActionPerformed() {

        for (int pokeID = 1; pokeID < allPokemon.size(); pokeID ++) {
            Pokemon poke = allPokemon.get(pokeID);
            if (banByMonoType.isSelected()) {
                if (poke.secondaryType == null && typeIsSelected(poke.primaryType)) {
                    banPokemon(poke);
                }
            }
            else if (banByPrimaryType.isSelected()) {
                if (typeIsSelected(poke.primaryType)) {
                    banPokemon(poke);
                }
            }
            else if (typeIsSelected(poke.primaryType) || (poke.secondaryType != null && typeIsSelected(poke.secondaryType))) {
                banPokemon(poke);
            }
        }

        populatePokemon(bannedPokemonText, bannedPokemon.getBannedPokemon());

    }

    private void unBanByTypeActionPerformed() {

        for (int pokeID = 1; pokeID < allPokemon.size(); pokeID ++) {
            Pokemon poke = allPokemon.get(pokeID);
            if (banByMonoType.isSelected() && poke.secondaryType == null && typeIsSelected(poke.primaryType)) {
                unbanPokemon(poke);
            }
            else if (banByPrimaryType.isSelected() && typeIsSelected(poke.primaryType)) {
                unbanPokemon(poke);
            }
            else if (typeIsSelected(poke.primaryType) || (poke.secondaryType != null && typeIsSelected(poke.secondaryType))) {
                unbanPokemon(poke);
            }
        }

        populatePokemon(bannedPokemonText, bannedPokemon.getBannedPokemon());

    }


    private void initComponents() {
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/dabomstew/pkrandom/newgui/Bundle");

        editorSplitPane = new javax.swing.JSplitPane();
        bannedPokemonSP = new javax.swing.JScrollPane();
        bannedPokemonText = new JTextArea();
        banFeaturesSP = new JPanel();

        banByTypePane = new JPanel();
        banByTypeCheckBoxPane = new JPanel();
        banByTypeButtonPane = new JPanel();
        banByTypeBtn = new JButton();
        unbanByTypeBtn = new JButton();
        selectAllTypesBtn = new JButton();
        banByPrimaryType = new JCheckBox();
        banByMonoType = new JCheckBox();

        saveBtn = new javax.swing.JButton();
        closeBtn = new javax.swing.JButton();

        checkBoxNORMAL= new JCheckBox(bundle.getString("PokemonTypeLabelNormal"));
        checkBoxFIRE= new JCheckBox(bundle.getString("PokemonTypeLabelFire"));
        checkBoxWATER= new JCheckBox(bundle.getString("PokemonTypeLabelWater"));
        checkBoxGRASS= new JCheckBox(bundle.getString("PokemonTypeLabelGrass"));
        checkBoxFLYING= new JCheckBox(bundle.getString("PokemonTypeLabelFlying"));
        checkBoxFIGHTING= new JCheckBox(bundle.getString("PokemonTypeLabelFighting"));
        checkBoxPOISON= new JCheckBox(bundle.getString("PokemonTypeLabelPoison"));
        checkBoxELECTRIC= new JCheckBox(bundle.getString("PokemonTypeLabelElectric"));
        checkBoxGROUND= new JCheckBox(bundle.getString("PokemonTypeLabelGround"));
        checkBoxROCK= new JCheckBox(bundle.getString("PokemonTypeLabelRock"));
        checkBoxPSYCHIC= new JCheckBox(bundle.getString("PokemonTypeLabelPsychic"));
        checkBoxICE= new JCheckBox(bundle.getString("PokemonTypeLabelIce"));
        checkBoxBUG= new JCheckBox(bundle.getString("PokemonTypeLabelBug"));
        checkBoxGHOST= new JCheckBox(bundle.getString("PokemonTypeLabelGhost"));
        checkBoxSTEEL= new JCheckBox(bundle.getString("PokemonTypeLabelSteel"));
        checkBoxDRAGON= new JCheckBox(bundle.getString("PokemonTypeLabelDragon"));
        checkBoxDARK= new JCheckBox(bundle.getString("PokemonTypeLabelDark"));
        checkBoxFAIRY= new JCheckBox(bundle.getString("PokemonTypeLabelFairy"));

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(bundle.getString("BannedPokemonEditorDialog.title"));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing();
            }
        });

        bannedPokemonSP.setHorizontalScrollBar(null);

        bannedPokemonSP.setMinimumSize(new Dimension(100, 50));
        bannedPokemonText.setColumns(20);
        bannedPokemonText.setRows(5);
        bannedPokemonSP.setViewportView(bannedPokemonText);

        selectAllTypesBtn.setText("(Un)Check All");
        banByTypeBtn.setText("Ban by Type");
        banByTypeBtn.setToolTipText(bundle.getString("BannedPokemonEditorDialog.banPokeTypeButton.tooltip"));
        unbanByTypeBtn.setText("Unban by Type");
        unbanByTypeBtn.setToolTipText(bundle.getString("BannedPokemonEditorDialog.unbanPokeTypeButton.tooltip"));

        banByTypeCheckBoxPane.setLayout(new GridLayout(7,4));

        javax.swing.GroupLayout rightPaneLayout = new javax.swing.GroupLayout(banByTypePane);
        banByTypePane.setLayout(rightPaneLayout);

        rightPaneLayout.setHorizontalGroup(
                rightPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(rightPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(banByTypeCheckBoxPane)
                                .addComponent(banByTypeButtonPane))
        );
        rightPaneLayout.setVerticalGroup(
                rightPaneLayout.createSequentialGroup()
                        .addGroup(rightPaneLayout.createSequentialGroup()
                                .addComponent(banByTypeCheckBoxPane)
                                .addComponent(banByTypeButtonPane))
        );


        for (JCheckBox typeBox : getPokemonTypes()) {
            banByTypeCheckBoxPane.add(typeBox);
        }

        banByPrimaryType.setText("by Primary Type");
        banByPrimaryType.setToolTipText(bundle.getString("BannedPokemonEditorDialog.banByPrimaryType.tooltip"));
        banByTypeButtonPane.add(banByPrimaryType);
        banByMonoType.setText("Mono Type Only");
        banByMonoType.setToolTipText(bundle.getString("BannedPokemonEditorDialog.banByMonoType.tooltip"));

        banByTypeButtonPane.add(banByMonoType);

        banByTypeButtonPane.add(selectAllTypesBtn);
        selectAllTypesBtn.addActionListener(evt -> selectAllTypesActionPerformed());
        banByTypeButtonPane.add(banByTypeBtn);
        banByTypeBtn.addActionListener(evt -> banByTypeActionPerformed());
        banByTypeButtonPane.add(unbanByTypeBtn);
        unbanByTypeBtn.addActionListener(evt -> unBanByTypeActionPerformed());


        banFeaturesSP.add(banByTypePane);
        banByTypePane.add(banByTypeCheckBoxPane);
        banByTypePane.add(banByTypeButtonPane);

        editorSplitPane.setLeftComponent(bannedPokemonSP);
        editorSplitPane.setRightComponent(banFeaturesSP);

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
                                        .addComponent(editorSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
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
                                .addComponent(editorSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(saveBtn)
                                        .addComponent(closeBtn))
                                .addContainerGap())
        );

        pack();
    }

    private javax.swing.JButton closeBtn;
    private javax.swing.JSplitPane editorSplitPane;
    private javax.swing.JButton saveBtn;
    private javax.swing.JScrollPane bannedPokemonSP;
    private javax.swing.JPanel banFeaturesSP;
    private javax.swing.JPanel banByTypePane;
    private javax.swing.JPanel banByTypeButtonPane;
    private javax.swing.JButton banByTypeBtn;
    private javax.swing.JButton unbanByTypeBtn;
    private javax.swing.JButton selectAllTypesBtn;
    private javax.swing.JPanel banByTypeCheckBoxPane;
    private javax.swing.JCheckBox checkBoxNORMAL;
    private javax.swing.JCheckBox checkBoxFIRE;
    private javax.swing.JCheckBox checkBoxWATER;
    private javax.swing.JCheckBox checkBoxGRASS;
    private javax.swing.JCheckBox checkBoxFLYING;
    private javax.swing.JCheckBox checkBoxFIGHTING;
    private javax.swing.JCheckBox checkBoxPOISON;
    private javax.swing.JCheckBox checkBoxELECTRIC;
    private javax.swing.JCheckBox checkBoxGROUND;
    private javax.swing.JCheckBox checkBoxROCK;
    private javax.swing.JCheckBox checkBoxPSYCHIC;
    private javax.swing.JCheckBox checkBoxICE;
    private javax.swing.JCheckBox checkBoxBUG;
    private javax.swing.JCheckBox checkBoxGHOST;
    private javax.swing.JCheckBox checkBoxSTEEL;
    private javax.swing.JCheckBox checkBoxDRAGON;
    private javax.swing.JCheckBox checkBoxDARK;
    private javax.swing.JCheckBox checkBoxFAIRY;
    private javax.swing.JCheckBox banByPrimaryType;
    private javax.swing.JCheckBox banByMonoType;
    private JTextArea bannedPokemonText;
}
