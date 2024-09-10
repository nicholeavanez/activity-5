package com.ecc.nichole.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a row in a board, containing a list of cells.
 */
public class Row {
    private List<Cell> cells;

    /**
     * Default constructor initializes an empty list of cells.
     */
    public Row() {
        this.cells = new ArrayList<>();
    }

    /**
     * Constructor initializes the row with a specific number of columns.
     *
     * @param initialColumns the number of columns (cells) to initialize the row with
     */
    public Row(int initialColumns) {
        this.cells = new ArrayList<>();
        for (int i = 0; i < initialColumns; i++) {
            cells.add(new Cell());
        }
    }

    /**
     * Returns the list of cells in this row.
     *
     * @return the list of cells
     */
    public List<Cell> getCells() {
        return cells;
    }

    /**
     * Returns the number of columns (cells) in this row.
     *
     * @return the number of columns
     */
    public int getColumnCount() {
        return cells.size();
    }

    /**
     * Adds a new cell to the row.
     *
     * @param cell the cell to add
     */
    public void addCell(Cell cell) {
        cells.add(cell);
    }

    /**
     * Removes the cell at the specified index.
     *
     * @param index the index of the cell to remove
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public void removeCell(int index) {
        if (index < 0 || index >= cells.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + cells.size());
        }
        cells.remove(index);
    }
}
