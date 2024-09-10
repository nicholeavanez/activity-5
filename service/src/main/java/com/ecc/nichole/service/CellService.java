package com.ecc.nichole.service;

import com.ecc.nichole.model.Cell;

import java.util.Random;
import java.time.Instant;

public class CellService {
    private final Random random;
    private static final int CELL_MAX_CHARACTERS = 3;

    /**
     * Initializes CellService with a Random instance seeded by the current time.
     */
    public CellService() {
        this.random = new Random(Instant.now().toEpochMilli());
    }

    /**
     * Generates a random string of printable ASCII characters.
     * The length of the string is defined by CELL_MAX_CHARACTERS.
     *
     * @return A string containing random printable characters.
     */
    public String generateValue() {
        StringBuilder cellValue = new StringBuilder(CELL_MAX_CHARACTERS);
        for (int characterIndex = 0; characterIndex < CELL_MAX_CHARACTERS; characterIndex++) {
            int asciiValue = random.nextInt(95) + 32; // ASCII range for printable characters (32-126)
            cellValue.append((char) asciiValue);
        }
        return cellValue.toString();
    }

    /**
     * Creates a new Cell object with randomly generated key and value.
     *
     * @return A new Cell with random key and value.
     */
    public Cell createRandomCell() {
        Cell cell = new Cell();
        cell.setKey(generateValue());
        cell.setValue(generateValue());
        return cell;
    }
}
