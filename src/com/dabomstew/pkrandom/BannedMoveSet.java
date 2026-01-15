package com.dabomstew.pkrandom;

        /*----------------------------------------------------------------------------*/
        /*--  BannedMoveSet.java - handles functionality related to banned          --*/
        /*--  Moves                                                                 --*/
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

import com.dabomstew.pkrandom.pokemon.Move;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class BannedMoveSet {
    private Map<String, String> bannedMoves;

    private static final int BANNED_MOVE_VERSION = 1;


    // Standard constructor: read binary data from an input stream.
    public BannedMoveSet(InputStream data) throws IOException {

        int version = data.read();
        if (version != BANNED_MOVE_VERSION) {
            throw new IOException("Invalid banned move file provided.");
        }

        bannedMoves = readMappingBlock(data);
    }

    private Set<String> readIDsBlock(InputStream in) throws IOException {
        // Read the size of the block to come.
        byte[] szData = FileFunctions.readFullyIntoBuffer(in, 4);
        int size = FileFunctions.readFullIntBigEndian(szData, 0);
        if (in.available() < size) {
            throw new IOException("Invalid size specified.");
        }

        // Read the block and translate it into a list of ID's.
        byte[] idData = FileFunctions.readFullyIntoBuffer(in, size);
        List<String> ids = new ArrayList<>();
        Scanner sc = new Scanner(new ByteArrayInputStream(idData), "UTF-8");
        while (sc.hasNextLine()) {
            String id = sc.nextLine().trim();
            if (!id.isEmpty()) {
                ids.add(id);
            }
        }
        sc.close();

        return new HashSet<>(ids);
    }

    private Map<String, String> readMappingBlock(InputStream in) throws IOException {
        // Read the size of the block to come.
        byte[] szData = FileFunctions.readFullyIntoBuffer(in, 4);
        int size = FileFunctions.readFullIntBigEndian(szData, 0);
        if (in.available() < size) {
            throw new IOException("Invalid size specified.");
        }

        // Read the block and translate it into a map of Name -> Replacement.
        byte[] mappingData = FileFunctions.readFullyIntoBuffer(in, size);
        Map<String, String> mapping = new HashMap<>();
        Scanner sc = new Scanner(new ByteArrayInputStream(mappingData), "UTF-8");
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) {
                if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    mapping.put(parts[0].trim(), parts[1].trim());
                } else {
                    mapping.put(line, "random");
                }
            }
        }
        sc.close();

        return mapping;
    }

    // Alternate constructor: blank all lists
    public BannedMoveSet() {
        bannedMoves = new HashMap<>();
    }

    private Set<String> readNamesBlock(InputStream in) throws IOException {
        // Read the size of the block to come.
        byte[] szData = FileFunctions.readFullyIntoBuffer(in, 4);
        int size = FileFunctions.readFullIntBigEndian(szData, 0);
        if (in.available() < size) {
            throw new IOException("Invalid size specified.");
        }

        // Read the block and translate it into a list of ID's.
        byte[] nameData = FileFunctions.readFullyIntoBuffer(in, size);
        List<String> names = new ArrayList<>();
        Scanner sc = new Scanner(new ByteArrayInputStream(nameData), "UTF-8");
        while (sc.hasNextLine()) {
            String name = sc.nextLine().trim();
            if (!name.isEmpty()) {
                names.add(name);
            }
        }
        sc.close();

        return new HashSet<>(names);
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos.write(BANNED_MOVE_VERSION);

        writeMoveBlock(baos, bannedMoves);

        return baos.toByteArray();
    }

    private void writeMoveBlock(OutputStream out, Map<String, String> moves) throws IOException {
        String newln = SysConstants.LINE_SEP;
        StringBuilder outNames = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : moves.entrySet()) {
            if (!first) {
                outNames.append(newln);
            }
            first = false;
            outNames.append(entry.getKey()).append(":").append(entry.getValue());
        }
        byte[] moveData = outNames.toString().getBytes("UTF-8");
        byte[] szData = new byte[4];
        FileFunctions.writeFullIntBigEndian(szData, 0, moveData.length);
        out.write(szData);
        out.write(moveData);
    }

    public Map<String, String> getBannedMoves() {
        return Collections.unmodifiableMap(bannedMoves);
    }


    public void addBannedMove(String move) {
        bannedMoves.put(move, "random");
    }
    public void addBannedMove(Move move) {
        bannedMoves.put(move.name, "random");
    }

    public void addBannedMove(List<String> moves) {
        moves.forEach(m -> bannedMoves.put(m, "random"));
    }

    public void removeBannedMove(String move) {
        bannedMoves.remove(move);
    }
    public void removeBannedMove(List<String> moves) { moves.forEach(bannedMoves::remove); }

    public void setBannedMoves(Map<String, String> moves) {
        bannedMoves.clear();
        bannedMoves.putAll(moves);
    }

    public boolean contains(Move move) {
        return bannedMoves.containsKey(move.name);
    }

    public boolean contains(String move) {
        return bannedMoves.containsKey(move);
    }

}
