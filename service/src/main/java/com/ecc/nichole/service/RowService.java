package com.ecc.nichole.service;

import org.apache.commons.lang3.StringUtils;

import com.ecc.nichole.model.Cell;
import com.ecc.nichole.model.Row;


public class RowService {
    private final CellService cellService = new CellService();

    /**
     * Creates a Row object with a specified number of cells.
     * Each cell is initialized with a random value from CellService.
     *
     * @param maxCells the number of cells to create in the row
     * @return a Row object with the specified number of cells
     */
    public Row createRow(int maxCells) {
        Row row = new Row(maxCells);
        for (int i = 0; i < maxCells; i++) {
            row.getCells().set(i, cellService.createRandomCell());
        }
        return row;
    }

    /**
     * Prints the row with each cell formatted as [key, value].
     *
     * @param row the Row object to print
     */
    public void printRow(Row row) {
        row.getCells().forEach(cell -> 
            System.out.print(formatCellData(cell))
        );
        System.out.println();
    }

    /**
     * Formats the cell data as [key, value] for display purposes.
     * Uses Apache Commons StringUtils for null-safe operations.
     *
     * @param cell the Cell object to format
     * @return a formatted string in the form [key, value]
     */
    private String formatCellData(Cell cell) {
        String key = StringUtils.defaultString(cell.getKey(), "null");
        String value = StringUtils.defaultString(cell.getValue(), "null");
        return "[" + key + ", " + value + "] ";
    }
}
