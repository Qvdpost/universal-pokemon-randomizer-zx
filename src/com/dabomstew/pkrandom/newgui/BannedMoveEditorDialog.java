package com.dabomstew.pkrandom.newgui;

        /*----------------------------------------------------------------------------*/
        /*--  BannedMoveEditorDialog.java - editor for moves to be excluded from    --*/
        /*--                                selection                               --*/
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

import com.dabomstew.pkrandom.BannedMoveSet;
import com.dabomstew.pkrandom.FileFunctions;
import com.dabomstew.pkrandom.SysConstants;
import com.dabomstew.pkrandom.pokemon.Move;
import com.dabomstew.pkrandom.pokemon.Type;
import com.dabomstew.pkrandom.romhandlers.RomHandler;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BannedMoveEditorDialog extends javax.swing.JDialog {

    private List<Move> allMoves;
    private int allMovesSize;
    RomHandler romHandler;
    private BannedMoveSet bannedMoves;
    private ImageIcon emptyIcon = new ImageIcon(getClass().getResource("/com/dabomstew/pkrandom/newgui/emptyIcon.png"));
    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/dabomstew/pkrandom/newgui/Bundle");

    /**
     * Creates new form BannedMoveEditorDialog
     */
    public BannedMoveEditorDialog(java.awt.Frame parent, RomHandler initRomHandler) {
        super(parent, true);

        if (initRomHandler == null) {
            java.awt.EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(BannedMoveEditorDialog.this,
                    "You need to load a rom to use the Banned Move Editor."));
            return;
        }
        romHandler = initRomHandler;
        allMoves = initRomHandler.getMoves();
        allMovesSize = allMoves.size();

        initComponents();
        setLocationRelativeTo(parent);

        java.awt.EventQueue.invokeLater(() -> setVisible(true));

        bannedFileChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH));

        try {
            bannedMoves = FileFunctions.getBannedMoves();
            populateMoves(bannedMovesText, bannedMoves.getBannedMoves());

        } catch (IOException ex) {
            java.awt.EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(BannedMoveEditorDialog.this,
                    "Your banned moves file is for a different randomizer version or otherwise corrupt."));
        }

        pendingChanges = false;

        addDocListener(bannedMovesText);
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

    private void formWindowClosing() {
        if (pendingChanges) {
            int returnVal = JOptionPane.showConfirmDialog(this,
                    "You have unsaved changes. Are you sure you want to close this window?", "Unsaved changes",
                    JOptionPane.YES_NO_OPTION);
            if (returnVal == JOptionPane.YES_OPTION) {
                dispose();
            }
        } else {
            dispose();
        }
    }

    private void populateMoves(JTextArea textArea, Set<Integer> ids) {
        StringBuilder sb = new StringBuilder();
        List<Integer> sortedIds = new ArrayList<>(ids);
        Collections.sort(sortedIds);
        for (Integer id : sortedIds) {
            sb.append(id).append(SysConstants.LINE_SEP);
        }
        textArea.setText(sb.toString());
        updateBannedCount();
    }

    private Set<Integer> getIdsFromText(JTextArea textArea) {
        Set<Integer> ids = new HashSet<Integer>();
        String text = textArea.getText();
        Scanner sc = new Scanner(text);
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) {
                try {
                    ids.add(Integer.parseInt(line));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        sc.close();
        return ids;
    }

    private void updateBannedCount() {
        Set<Integer> idsInGame = new HashSet<>();
        allMoves.forEach(m -> idsInGame.add(m.number));

        Set<Integer> bannedIds = getIdsFromText(bannedMovesText);
        long inGameCount = bannedIds.stream().filter(idsInGame::contains).count();
        currentlyBannedNumber.setText(String.format("<html><center>%s<br/>%d<br/>%s %d %s",
                bundle.getString("BannedMoveEditorDialog.currentlyBannedNumber0.text"),
                inGameCount,
                bundle.getString("BannedMoveEditorDialog.currentlyBannedNumber1.text"),
                allMovesSize,
                bundle.getString("BannedMoveEditorDialog.currentlyBannedNumber2.text")));
    }

    private void addUndoStep() {
        bannedMoves = new BannedMoveSet(bannedMoves);
        bannedMoves.setBannedMoves(getIdsFromText(bannedMovesText));
        undoBtn.setEnabled(true);
        redoBtn.setEnabled(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        bannedFileChooser = new javax.swing.JFileChooser();
        mainPanel = new javax.swing.JPanel();
        moveLabel = new javax.swing.JLabel();
        moveCombo = new javax.swing.JComboBox<>();
        banBtn = new javax.swing.JButton();
        unbanBtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        bannedMovesText = new javax.swing.JTextArea();
        currentlyBannedNumber = new javax.swing.JLabel();
        clearBtn = new javax.swing.JButton();
        invertBtn = new javax.swing.JButton();
        saveBtn = new javax.swing.JButton();
        loadBtn = new javax.swing.JButton();
        closeBtn = new javax.swing.JButton();
        banByTypePanel = new javax.swing.JPanel();
        typeBanScroll = new javax.swing.JScrollPane();
        typeBanPanel = new javax.swing.JPanel();
        checkAllBtn = new javax.swing.JButton();
        banByTypeBtn = new javax.swing.JButton();
        unbanByTypeBtn = new javax.swing.JButton();
        banRandomTypePanel = new javax.swing.JPanel();
        banRandomTypeLabel = new javax.swing.JLabel();
        banRandomTypeSpinner = new javax.swing.JSpinner();
        banRandomTypeButton = new javax.swing.JButton();
        unbanRandomTypeButton = new javax.swing.JButton();
        banRandomTypeSpoiler = new javax.swing.JCheckBox();
        undoBtn = new javax.swing.JButton();
        redoBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(bundle.getString("BannedMoveEditorDialog.title")); // NOI18N
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                formWindowClosing();
            }
        });

        moveLabel.setText("Move:");

        List<String> moveNames = allMoves.stream().map(m -> m.name).collect(Collectors.toList());
        moveCombo.setModel(new javax.swing.DefaultComboBoxModel<>(moveNames.toArray(new String[0])));

        banBtn.setText(bundle.getString("BannedMoveEditorDialog.banBtn.text")); // NOI18N
        banBtn.addActionListener(evt -> banBtnActionPerformed());

        unbanBtn.setText(bundle.getString("BannedMoveEditorDialog.unbanBtn.text")); // NOI18N
        unbanBtn.addActionListener(evt -> unbanBtnActionPerformed());

        bannedMovesText.setColumns(10);
        bannedMovesText.setRows(5);
        bannedMovesText.setToolTipText(bundle.getString("BannedMoveEditorDialog.bannedMoveTextArea.tooltipText")); // NOI18N
        jScrollPane1.setViewportView(bannedMovesText);

        currentlyBannedNumber.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        currentlyBannedNumber.setText(bundle.getString("BannedMoveEditorDialog.currentlyBannedNumber0.text")); // NOI18N

        clearBtn.setText(bundle.getString("BannedMoveEditorDialog.clearbtn.text")); // NOI18N
        clearBtn.addActionListener(evt -> clearBtnActionPerformed());

        invertBtn.setText(bundle.getString("BannedMoveEditorDialog.invertBtn.text")); // NOI18N
        invertBtn.addActionListener(evt -> invertBtnActionPerformed());

        saveBtn.setText(bundle.getString("BannedMoveEditorDialog.saveBtn.text")); // NOI18N
        saveBtn.addActionListener(evt -> saveBtnActionPerformed());

        loadBtn.setText(bundle.getString("BannedMoveEditorDialog.loadBtn.text")); // NOI18N
        loadBtn.addActionListener(evt -> loadBtnActionPerformed());

        closeBtn.setText(bundle.getString("BannedMoveEditorDialog.closeBtn.text")); // NOI18N
        closeBtn.addActionListener(evt -> closeBtnActionPerformed());

        banByTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BannedMoveEditorDialog.banByTypeBtn.text"))); // NOI18N

        typeBanPanel.setLayout(new javax.swing.BoxLayout(typeBanPanel, javax.swing.BoxLayout.Y_AXIS));
        typeCheckBoxes = new ArrayList<>();
        for (com.dabomstew.pkrandom.pokemon.Type t : com.dabomstew.pkrandom.pokemon.Type.values()) {
            if (!romHandler.typeInGame(t)) {
                continue;
            }
            JCheckBox cb = new JCheckBox(t.toString());
            cb.setName(t.name());
            typeCheckBoxes.add(cb);
            typeBanPanel.add(cb);
        }
        typeBanScroll.setViewportView(typeBanPanel);

        checkAllBtn.setText(bundle.getString("BannedMoveEditorDialog.checkAllBtn.text")); // NOI18N
        checkAllBtn.addActionListener(evt -> checkAllBtnActionPerformed());

        banByTypeBtn.setText(bundle.getString("BannedMoveEditorDialog.banBtn.text")); // NOI18N
        banByTypeBtn.setToolTipText(bundle.getString("BannedMoveEditorDialog.banMoveTypeButton.tooltip")); // NOI18N
        banByTypeBtn.addActionListener(evt -> banByTypeBtnActionPerformed());

        unbanByTypeBtn.setText(bundle.getString("BannedMoveEditorDialog.unbanBtn.text")); // NOI18N
        unbanByTypeBtn.setToolTipText(bundle.getString("BannedMoveEditorDialog.unbanMoveTypeButton.tooltip")); // NOI18N
        unbanByTypeBtn.addActionListener(evt -> unbanByTypeBtnActionPerformed());

        javax.swing.GroupLayout banByTypePanelLayout = new javax.swing.GroupLayout(banByTypePanel);
        banByTypePanel.setLayout(banByTypePanelLayout);
        banByTypePanelLayout.setHorizontalGroup(
                banByTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(banByTypePanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(banByTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(typeBanScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                                        .addComponent(checkAllBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(banByTypePanelLayout.createSequentialGroup()
                                                .addComponent(banByTypeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(unbanByTypeBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        banByTypePanelLayout.setVerticalGroup(
                banByTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(banByTypePanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(typeBanScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(checkAllBtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(banByTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(banByTypeBtn)
                                        .addComponent(unbanByTypeBtn))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        banRandomTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BannedMoveEditorDialog.banRandomTypeLabel.text"))); // NOI18N

        banRandomTypeLabel.setText("Count:");

        banRandomTypeSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, typeCheckBoxes.size(), 1));

        banRandomTypeButton.setText(bundle.getString("BannedMoveEditorDialog.banBtn.text")); // NOI18N
        banRandomTypeButton.setToolTipText(bundle.getString("BannedMoveEditorDialog.banRandomTypeButton.tooltipText")); // NOI18N
        banRandomTypeButton.addActionListener(evt -> banRandomTypeButtonActionPerformed());

        unbanRandomTypeButton.setText(bundle.getString("BannedMoveEditorDialog.unbanBtn.text")); // NOI18N
        unbanRandomTypeButton.setToolTipText(bundle.getString("BannedMoveEditorDialog.unbanRandomTypeButton.tooltipText")); // NOI18N
        unbanRandomTypeButton.addActionListener(evt -> unbanRandomTypeButtonActionPerformed());

        banRandomTypeSpoiler.setText(bundle.getString("BannedMoveEditorDialog.banRandomTypeSpoiler.text")); // NOI18N
        banRandomTypeSpoiler.setToolTipText(bundle.getString("BannedMoveEditorDialog.banRandomTypeSpoiler.tooltipText")); // NOI18N

        javax.swing.GroupLayout banRandomTypePanelLayout = new javax.swing.GroupLayout(banRandomTypePanel);
        banRandomTypePanel.setLayout(banRandomTypePanelLayout);
        banRandomTypePanelLayout.setHorizontalGroup(
                banRandomTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(banRandomTypePanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(banRandomTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(banRandomTypePanelLayout.createSequentialGroup()
                                                .addComponent(banRandomTypeLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(banRandomTypeSpinner))
                                        .addGroup(banRandomTypePanelLayout.createSequentialGroup()
                                                .addComponent(banRandomTypeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(unbanRandomTypeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(banRandomTypePanelLayout.createSequentialGroup()
                                                .addComponent(banRandomTypeSpoiler)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        banRandomTypePanelLayout.setVerticalGroup(
                banRandomTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(banRandomTypePanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(banRandomTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(banRandomTypeLabel)
                                        .addComponent(banRandomTypeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(banRandomTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(banRandomTypeButton)
                                        .addComponent(unbanRandomTypeButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(banRandomTypeSpoiler)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        undoBtn.setText(bundle.getString("BannedMoveEditorDialog.undoBtn.text")); // NOI18N
        undoBtn.setEnabled(false);
        undoBtn.addActionListener(evt -> undoBtnActionPerformed());

        redoBtn.setText(bundle.getString("BannedMoveEditorDialog.redoBtn.text")); // NOI18N
        redoBtn.setEnabled(false);
        redoBtn.addActionListener(evt -> redoBtnActionPerformed());

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addComponent(moveLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(moveCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addComponent(banBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(unbanBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addComponent(banByTypePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(banRandomTypePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(currentlyBannedNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(clearBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(invertBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(saveBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(loadBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(closeBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(undoBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(redoBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(moveLabel)
                                                        .addComponent(moveCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(banBtn)
                                                        .addComponent(unbanBtn))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(banByTypePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(banRandomTypePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(currentlyBannedNumber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addComponent(undoBtn)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(redoBtn)
                                                .addGap(18, 18, 18)
                                                .addComponent(clearBtn)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(invertBtn)
                                                .addGap(18, 18, 18)
                                                .addComponent(saveBtn)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(loadBtn)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(closeBtn)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>

    private void banBtnActionPerformed() {
        addUndoStep();
        int index = moveCombo.getSelectedIndex();
        Move m = allMoves.get(index);
        Set<Integer> ids = getIdsFromText(bannedMovesText);
        ids.add(m.number);
        populateMoves(bannedMovesText, ids);
    }

    private void unbanBtnActionPerformed() {
        addUndoStep();
        int index = moveCombo.getSelectedIndex();
        Move m = allMoves.get(index);
        Set<Integer> ids = getIdsFromText(bannedMovesText);
        ids.remove(m.number);
        populateMoves(bannedMovesText, ids);
    }

    private void clearBtnActionPerformed() {
        addUndoStep();
        populateMoves(bannedMovesText, new HashSet<>());
    }

    private void invertBtnActionPerformed() {
        addUndoStep();
        Set<Integer> ids = getIdsFromText(bannedMovesText);
        Set<Integer> invertedIds = new HashSet<>();
        for (Move m : allMoves) {
            if (!ids.contains(m.number)) {
                invertedIds.add(m.number);
            }
        }
        populateMoves(bannedMovesText, invertedIds);
    }

    private void saveBtnActionPerformed() {
        try {
            byte[] data = bannedMoves.getBytes();
            FileOutputStream fos = new FileOutputStream(SysConstants.ROOT_PATH + SysConstants.bannedMovesFile);
            fos.write(data);
            fos.close();
            pendingChanges = false;
            JOptionPane.showMessageDialog(this, "Banned moves saved successfully.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving banned moves: " + ex.getMessage());
        }
    }

    private void loadBtnActionPerformed() {
        bannedFileChooser.setSelectedFile(null);
        int returnVal = bannedFileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = bannedFileChooser.getSelectedFile();
            try {
                BannedMoveSet loaded = new BannedMoveSet(new java.io.FileInputStream(file));
                addUndoStep();
                populateMoves(bannedMovesText, loaded.getBannedMoves());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading banned moves: " + ex.getMessage());
            }
        }
    }

    private void closeBtnActionPerformed() {
        formWindowClosing();
    }

    private void checkAllBtnActionPerformed() {
        boolean allChecked = typeCheckBoxes.stream().allMatch(AbstractButton::isSelected);
        typeCheckBoxes.forEach(cb -> cb.setSelected(!allChecked));
    }

    private void banByTypeBtnActionPerformed() {
        addUndoStep();
        Set<Type> selectedTypes = typeCheckBoxes.stream()
                .filter(AbstractButton::isSelected)
                .map(cb -> Type.valueOf(cb.getName()))
                .collect(Collectors.toSet());

        Set<Integer> ids = getIdsFromText(bannedMovesText);
        for (Move m : allMoves) {
            if (selectedTypes.contains(m.type)) {
                ids.add(m.number);
            }
        }
        populateMoves(bannedMovesText, ids);
    }

    private void unbanByTypeBtnActionPerformed() {
        addUndoStep();
        Set<Type> selectedTypes = typeCheckBoxes.stream()
                .filter(AbstractButton::isSelected)
                .map(cb -> Type.valueOf(cb.getName()))
                .collect(Collectors.toSet());

        Set<Integer> ids = getIdsFromText(bannedMovesText);
        for (Move m : allMoves) {
            if (selectedTypes.contains(m.type)) {
                ids.remove(m.number);
            }
        }
        populateMoves(bannedMovesText, ids);
    }

    private void banRandomTypeButtonActionPerformed() {
        addUndoStep();
        int count = (int) banRandomTypeSpinner.getValue();
        List<Type> allTypes = typeCheckBoxes.stream()
                .map(cb -> Type.valueOf(cb.getName()))
                .collect(Collectors.toList());
        Collections.shuffle(allTypes);
        List<Type> selectedTypes = allTypes.subList(0, Math.min(count, allTypes.size()));

        Set<Integer> ids = getIdsFromText(bannedMovesText);
        for (Move m : allMoves) {
            if (selectedTypes.contains(m.type)) {
                ids.add(m.number);
            }
        }
        populateMoves(bannedMovesText, ids);
        if (banRandomTypeSpoiler.isSelected()) {
            JOptionPane.showMessageDialog(this, "Banned types: " + selectedTypes.stream().map(Type::toString).collect(Collectors.joining(", ")));
        }
    }

    private void unbanRandomTypeButtonActionPerformed() {
        addUndoStep();
        int count = (int) banRandomTypeSpinner.getValue();
        List<Type> allTypes = typeCheckBoxes.stream()
                .map(cb -> Type.valueOf(cb.getName()))
                .collect(Collectors.toList());
        Collections.shuffle(allTypes);
        List<Type> selectedTypes = allTypes.subList(0, Math.min(count, allTypes.size()));

        Set<Integer> ids = getIdsFromText(bannedMovesText);
        for (Move m : allMoves) {
            if (selectedTypes.contains(m.type)) {
                ids.remove(m.number);
            }
        }
        populateMoves(bannedMovesText, ids);
        if (banRandomTypeSpoiler.isSelected()) {
            JOptionPane.showMessageDialog(this, "Unbanned types: " + selectedTypes.stream().map(Type::toString).collect(Collectors.joining(", ")));
        }
    }

    private void undoBtnActionPerformed() {
        if (bannedMoves.getPrevious() != null) {
            bannedMoves = bannedMoves.getPrevious();
            populateMoves(bannedMovesText, bannedMoves.getBannedMoves());
            redoBtn.setEnabled(true);
            if (bannedMoves.getPrevious() == null) {
                undoBtn.setEnabled(false);
            }
        }
    }

    private void redoBtnActionPerformed() {
        if (bannedMoves.getNext() != null) {
            bannedMoves = bannedMoves.getNext();
            populateMoves(bannedMovesText, bannedMoves.getBannedMoves());
            undoBtn.setEnabled(true);
            if (bannedMoves.getNext() == null) {
                redoBtn.setEnabled(false);
            }
        }
    }

    private javax.swing.JButton banBtn;
    private javax.swing.JButton banByTypeBtn;
    private javax.swing.JPanel banByTypePanel;
    private javax.swing.JButton banRandomTypeButton;
    private javax.swing.JLabel banRandomTypeLabel;
    private javax.swing.JPanel banRandomTypePanel;
    private javax.swing.JCheckBox banRandomTypeSpoiler;
    private javax.swing.JSpinner banRandomTypeSpinner;
    private javax.swing.JFileChooser bannedFileChooser;
    private javax.swing.JTextArea bannedMovesText;
    private javax.swing.JButton checkAllBtn;
    private javax.swing.JButton clearBtn;
    private javax.swing.JButton closeBtn;
    private javax.swing.JLabel currentlyBannedNumber;
    private javax.swing.JButton invertBtn;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton loadBtn;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JComboBox<String> moveCombo;
    private javax.swing.JLabel moveLabel;
    private javax.swing.JButton redoBtn;
    private javax.swing.JButton saveBtn;
    private javax.swing.JPanel typeBanPanel;
    private javax.swing.JScrollPane typeBanScroll;
    private javax.swing.JButton unbanBtn;
    private javax.swing.JButton unbanByTypeBtn;
    private javax.swing.JButton unbanRandomTypeButton;
    private javax.swing.JButton undoBtn;

    private List<JCheckBox> typeCheckBoxes;
    private boolean pendingChanges = false;
}
