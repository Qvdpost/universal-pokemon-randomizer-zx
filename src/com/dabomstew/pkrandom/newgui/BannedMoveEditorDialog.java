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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BannedMoveEditorDialog extends javax.swing.JDialog {

    private List<Move> allMoves;
    private List<Move> validMoves;
    private int allMovesSize;
    RomHandler romHandler;
    private BannedMoveSet bannedMoves;
    private ImageIcon emptyIcon = new ImageIcon(getClass().getResource("/com/dabomstew/pkrandom/newgui/emptyIcon.png"));
    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/dabomstew/pkrandom/newgui/Bundle");

    private DefaultTableModel tableModel;

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
        validMoves = allMoves.stream().filter(Objects::nonNull).collect(Collectors.toList());
        validMoves.sort(Comparator.comparing(m -> m.name));
        allMovesSize = allMoves.size();

        initComponents();
        setLocationRelativeTo(parent);

        java.awt.EventQueue.invokeLater(() -> setVisible(true));

        bannedFileChooser.setCurrentDirectory(new File(SysConstants.ROOT_PATH));

        try {
            bannedMoves = FileFunctions.getBannedMoves();
            populateMoves(bannedMoves.getBannedMoves());

        } catch (IOException ex) {
            java.awt.EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(BannedMoveEditorDialog.this,
                    "Your banned moves file is for a different randomizer version or otherwise corrupt."));
        }

        pendingChanges = false;
        updateMoveDetails();
    }

    private void updateMoveDetails() {
        int index = moveCombo.getSelectedIndex();
        if (index == -1) {
            moveDetailsLabel.setText("");
            return;
        }
        Move m = validMoves.get(index);
        StringBuilder sb = new StringBuilder("<html>");
        sb.append("<b>Type:</b> ").append(m.type.camelCase()).append("<br/>");
        sb.append("<b>Category:</b> ").append(com.dabomstew.pkrandom.RomFunctions.camelCase(m.category.toString())).append("<br/>");
        sb.append("<b>Power:</b> ").append(m.power == 0 ? "---" : m.power).append("<br/>");
        sb.append("<b>Accuracy:</b> ").append(m.hitratio == 0 ? "---" : (int) m.hitratio).append("<br/>");
        sb.append("<b>PP:</b> ").append(m.pp).append("<br/>");
        sb.append("<b>Priority:</b> ").append(m.priority).append("<br/>");
        sb.append("</html>");
        moveDetailsLabel.setText(sb.toString());
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

    private void populateMoves(Map<String, String> mapping) {
        tableModel.setRowCount(0);
        List<String> sortedNames = new ArrayList<>(mapping.keySet());
        Collections.sort(sortedNames);
        for (String name : sortedNames) {
            tableModel.addRow(new Object[]{name, mapping.get(name)});
        }
        updateBannedCount();
    }

    private Map<String, String> getNamesFromTable() {
        Map<String, String> mapping = new HashMap<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            mapping.put((String) tableModel.getValueAt(i, 0), (String) tableModel.getValueAt(i, 1));
        }
        return mapping;
    }

    private void updateBannedCount() {
        Set<String> namesInGame = new HashSet<>();
        validMoves.forEach(m -> namesInGame.add(m.name));

        Map<String, String> bannedMapping = getNamesFromTable();
        long inGameCount = bannedMapping.keySet().stream().filter(namesInGame::contains).count();
        currentlyBannedNumber.setText(String.format("<html><center>%s<br/>%d<br/>%s %d %s",
                bundle.getString("BannedMoveEditorDialog.currentlyBannedNumber0.text"),
                inGameCount,
                bundle.getString("BannedMoveEditorDialog.currentlyBannedNumber1.text"),
                validMoves.size(),
                bundle.getString("BannedMoveEditorDialog.currentlyBannedNumber2.text")));
    }

    private void initComponents() {
        bannedFileChooser = new javax.swing.JFileChooser();
        bannedFileChooser.setFileFilter(new BannedMoveFileFilter());

        mainPanel = new javax.swing.JPanel();
        moveLabel = new javax.swing.JLabel();
        moveCombo = new javax.swing.JComboBox<>();
        banBtn = new javax.swing.JButton();
        unbanBtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        bannedMovesTable = new javax.swing.JTable();
        currentlyBannedNumber = new javax.swing.JLabel();
        clearBtn = new javax.swing.JButton();
        saveBtn = new javax.swing.JButton();
        loadBtn = new javax.swing.JButton();
        closeBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(bundle.getString("BannedMoveEditorDialog.title")); // NOI18N
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                formWindowClosing();
            }
        });

        moveLabel.setText("Move:");
        moveCombo.setModel(new javax.swing.DefaultComboBoxModel<>(validMoves.stream().map(m -> m.name).toArray(String[]::new)));
        moveCombo.addActionListener(evt -> updateMoveDetails());

        moveDetailsLabel = new javax.swing.JLabel();
        moveDetailsLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        moveDetailsLabel.setText("");

        banBtn.setText(bundle.getString("BannedMoveEditorDialog.banBtn.text")); // NOI18N
        banBtn.addActionListener(evt -> banBtnActionPerformed());

        unbanBtn.setText(bundle.getString("BannedMoveEditorDialog.unbanBtn.text")); // NOI18N
        unbanBtn.addActionListener(evt -> unbanBtnActionPerformed());

        tableModel = new DefaultTableModel(new Object[]{"Banned Move", "Replacement"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1;
            }
        };
        tableModel.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() == 1) {
                int row = e.getFirstRow();
                String bannedMove = (String) tableModel.getValueAt(row, 0);
                String replacement = (String) tableModel.getValueAt(row, 1);

                if (!replacement.equalsIgnoreCase("random")) {
                    boolean alreadyBanned = false;
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        if (((String) tableModel.getValueAt(i, 0)).equalsIgnoreCase(replacement)) {
                            alreadyBanned = true;
                            break;
                        }
                    }

                    if (alreadyBanned) {
                        final int finalRow = row;
                        java.awt.EventQueue.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this,
                                    "The selected replacement move is already banned. Reverting to RANDOM.",
                                    "Invalid Replacement", JOptionPane.WARNING_MESSAGE);
                            tableModel.setValueAt("RANDOM", finalRow, 1);
                        });
                    }
                }
            }
            pendingChanges = true;
            updateBannedCount();
        });
        bannedMovesTable.setModel(tableModel);
        bannedMovesTable.setToolTipText(bundle.getString("BannedMoveEditorDialog.bannedMoveTextArea.tooltipText")); // NOI18N

        JComboBox<String> replacementCombo = new JComboBox<>();
        replacementCombo.addItem("RANDOM");
        for (Move m : validMoves) {
            replacementCombo.addItem(m.name);
        }
        bannedMovesTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(replacementCombo));

        jScrollPane1.setViewportView(bannedMovesTable);

        currentlyBannedNumber.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        currentlyBannedNumber.setText(bundle.getString("BannedMoveEditorDialog.currentlyBannedNumber0.text")); // NOI18N

        clearBtn.setText(bundle.getString("BannedMoveEditorDialog.clearbtn.text")); // NOI18N
        clearBtn.addActionListener(evt -> clearBtnActionPerformed());

        saveBtn.setText(bundle.getString("BannedMoveEditorDialog.saveBtn.text")); // NOI18N
        saveBtn.addActionListener(evt -> saveBtnActionPerformed());

        loadBtn.setText(bundle.getString("BannedMoveEditorDialog.loadBtn.text")); // NOI18N
        loadBtn.addActionListener(evt -> loadBtnActionPerformed());

        closeBtn.setText(bundle.getString("BannedMoveEditorDialog.closeBtn.text")); // NOI18N
        closeBtn.addActionListener(evt -> closeBtnActionPerformed());

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
                                        .addComponent(moveDetailsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(currentlyBannedNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(clearBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(saveBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(loadBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(closeBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                                                .addComponent(moveDetailsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(currentlyBannedNumber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                )
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addComponent(clearBtn)
                                                .addGap(18, 18, 18)
                                                .addComponent(saveBtn)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(loadBtn)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(closeBtn))
                                        )
                                .addGap(45, 45, 45)
                        )
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
    }

    private void banBtnActionPerformed() {
        int index = moveCombo.getSelectedIndex();
        Move m = validMoves.get(index);
        Map<String, String> names = getNamesFromTable();
        names.put(m.name, "RANDOM");
        populateMoves(names);
    }

    private void unbanBtnActionPerformed() {
        int index = moveCombo.getSelectedIndex();
        Move m = validMoves.get(index);
        Map<String, String> names = getNamesFromTable();
        names.remove(m.name);
        populateMoves(names);
    }

    private void clearBtnActionPerformed() {
        populateMoves(new HashMap<>());
    }

    private void saveBtnActionPerformed() {
        bannedFileChooser.setSelectedFile(new File(SysConstants.ROOT_PATH + SysConstants.bannedMovesFile));
        int returnVal = bannedFileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File fh = bannedFileChooser.getSelectedFile();

            fh = FileFunctions.fixFilename(fh, "rnbm");
            try {
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(fh));
                bannedMoves.setBannedMoves(getNamesFromTable());
                byte[] data = bannedMoves.getBytes();

                dos.write(data);
                dos.close();

                FileFunctions.writeBytesToFile(SysConstants.ROOT_PATH + SysConstants.bannedMovesFile, data);

                pendingChanges = false;
                JOptionPane.showMessageDialog(this, "Banned moves saved successfully.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving banned moves: " + ex.getMessage());
            }
        }
    }

    private void loadBtnActionPerformed() {
        bannedFileChooser.setSelectedFile(null);
        int returnVal = bannedFileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = bannedFileChooser.getSelectedFile();
            try {
                BannedMoveSet loaded = new BannedMoveSet(new java.io.FileInputStream(file));
                bannedMoves = loaded;
                populateMoves(loaded.getBannedMoves());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading banned moves: " + ex.getMessage());
            }
        }
    }

    private void closeBtnActionPerformed() {
        formWindowClosing();
    }

    private javax.swing.JButton banBtn;
    private javax.swing.JFileChooser bannedFileChooser;
    private javax.swing.JTable bannedMovesTable;
    private javax.swing.JButton clearBtn;
    private javax.swing.JButton closeBtn;
    private javax.swing.JLabel currentlyBannedNumber;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton loadBtn;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JComboBox<String> moveCombo;
    private javax.swing.JLabel moveDetailsLabel;
    private javax.swing.JLabel moveLabel;
    private javax.swing.JButton saveBtn;
    private javax.swing.JButton unbanBtn;

    private boolean pendingChanges = false;
}
