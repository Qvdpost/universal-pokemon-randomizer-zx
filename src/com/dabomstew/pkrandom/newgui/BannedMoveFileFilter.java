package com.dabomstew.pkrandom.newgui;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class BannedMoveFileFilter extends FileFilter {

    @Override
    public boolean accept(File arg0) {
        if (arg0.isDirectory()) {
            return true; // needed to allow directory navigation
        }
        String filename = arg0.getName();
        if (!filename.contains(".")) {
            return false;
        }
        String extension = arg0.getName().substring(
                arg0.getName().lastIndexOf('.') + 1);
        return extension.toLowerCase().equals("rnbm");
    }

    @Override
    public String getDescription() {
        return "Banned Move File (*.rnbm)";
    }

}
