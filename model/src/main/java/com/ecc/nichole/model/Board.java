package com.ecc.nichole.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a board consisting of rows and columns of cells.
 */
public class Board {
    private List<Row> rows;

    /**
     * Returns a list of rows in the board.
     *
     * @return the list of rows
     */
    public List<Row> getRows() {
        return rows;
    }

    /**
     * Sets the list of rows for the board.
     *
     * @param rows the new list of rows
     */
    public void setRows(List<Row> rows) {
        this.rows = new ArrayList<>(rows);
    }

    /**
     * Returns the number of rows in the board.
     *
     * @return the number of rows
     */
    public int getRowCount() {
        return rows.size();
    }

    /**
     * Returns the number of columns in the board. Assumes all rows have the same number of columns.
     *
     * @return the number of columns
     */
    public int getColumnCount() {
        return rows.isEmpty() ? 0 : rows.get(0).getColumnCount();
    }
}
