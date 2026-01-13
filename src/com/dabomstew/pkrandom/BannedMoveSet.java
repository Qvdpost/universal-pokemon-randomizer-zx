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

    private Set<Integer> bannedMoves;
    private BannedMoveSet next;
    private BannedMoveSet previous;

    private static final int BANNED_MOVE_VERSION = 1;


    // Standard constructor: read binary data from an input stream.
    public BannedMoveSet(InputStream data) throws IOException {

        if (data.read() != BANNED_MOVE_VERSION) {
            throw new IOException("Invalid banned move file provided.");
        }

        bannedMoves = readIDsBlock(data);
    }

    public BannedMoveSet(BannedMoveSet bannedSet) {
        bannedMoves = new HashSet<>();
        this.setBannedMoves(bannedSet.getBannedMoves());
        this.setPrevious(bannedSet);
        bannedSet.setNext(this);
    }

    // Alternate constructor: blank all lists
    public BannedMoveSet() {
        bannedMoves = new HashSet<>();
    }



    public BannedMoveSet getNext() {
        return this.next;
    }
    public void setNext(BannedMoveSet bannedSet) {
        this.next = bannedSet;
    }
    public BannedMoveSet getPrevious() {
        return this.previous;
    }
    public void setPrevious(BannedMoveSet bannedSet) {
        this.previous = bannedSet;
    }

    private Set<Integer> readIDsBlock(InputStream in) throws IOException {
        // Read the size of the block to come.
        byte[] szData = FileFunctions.readFullyIntoBuffer(in, 4);
        int size = FileFunctions.readFullIntBigEndian(szData, 0);
        if (in.available() < size) {
            throw new IOException("Invalid size specified.");
        }

        // Read the block and translate it into a list of ID's.
        byte[] idData = FileFunctions.readFullyIntoBuffer(in, size);
        List<Integer> ids = new ArrayList<>();
        Scanner sc = new Scanner(new ByteArrayInputStream(idData), "UTF-8");
        while (sc.hasNextLine()) {
            String id = sc.nextLine().trim();
            if (!id.isEmpty()) {
                try {
                    ids.add(Integer.parseInt(id));
                } catch (NumberFormatException e) {
                    throw new IOException("File contains non-numeric values.");
                }
            }
        }
        sc.close();

        return new HashSet<>(ids);
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos.write(BANNED_MOVE_VERSION);

        writeMoveBlock(baos, bannedMoves);

        return baos.toByteArray();
    }

    private void writeMoveBlock(OutputStream out, Set<Integer> moves) throws IOException {
        String newln = SysConstants.LINE_SEP;
        StringBuilder outIDs = new StringBuilder();
        boolean first = true;
        for (Integer move : moves) {
            if (!first) {
                outIDs.append(newln);
            }
            first = false;
            outIDs.append(move);
        }
        byte[] moveData = outIDs.toString().getBytes("UTF-8");
        byte[] szData = new byte[4];
        FileFunctions.writeFullIntBigEndian(szData, 0, moveData.length);
        out.write(szData);
        out.write(moveData);
    }

    public Set<Integer> getBannedMoves() {
        return Collections.unmodifiableSet(bannedMoves);
    }


    public void addBannedMove(Integer move) {
        bannedMoves.add(move);
    }
    public void addBannedMove(Move move) {
        bannedMoves.add(move.number);
    }

    public void addBannedMove(List<Integer> moves) {
        bannedMoves.addAll(moves);
    }

    public void removeBannedMove(Integer move) {
        bannedMoves.remove(move);
    }
    public void removeBannedMove(List<Integer> moves) { moves.forEach(bannedMoves::remove); }

    public void setBannedMoves(Collection<Integer> moves) {
        bannedMoves.clear();
        bannedMoves.addAll(moves);
    }

    public boolean contains(Move move) {
        return bannedMoves.contains(move.number);
    }

    public boolean contains(Integer move) {
        return bannedMoves.contains(move);
    }

}
