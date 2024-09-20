package com.ecc.nichole.service;

import org.apache.commons.lang3.StringUtils;

import com.ecc.nichole.model.Board;
import com.ecc.nichole.model.Cell;
import com.ecc.nichole.model.Row;
import com.ecc.nichole.service.RowService;
import com.ecc.nichole.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Service class for managing operations on a Board.
 */
public class BoardService {
    private final CellService cellService;
    private final RowService rowService;
    private final FileService fileService;
    private static final int MINIMUM_CELL_LENGTH = 1;
    private static final int MAXIMUM_CELL_LENGTH = 3;
    private static final int ASCENDING_ORDER = 1;
    private static final int DESCENDING_ORDER = 2;

    /**
     * Constructor initializes required services.
     */
    public BoardService(CellService cellService, RowService rowService, FileService fileService) {
        this.cellService = cellService;
        this.rowService = rowService;
        this.fileService = fileService;
    }

    /**
     * Loads a board from the given file.
     *
     * @param fileName the name of the file to load the board from
     * @return the loaded Board object
     */
    public Board loadBoard(String fileName) {
        return fileService.loadBoardFromTextFile(fileName);
    }

    /**
     * Saves the current board to the given file.
     *
     * @param board    the Board to save
     * @param fileName the file name to save the Board to
     */
    public void saveBoard(Board board, String fileName) {
        fileService.saveBoardToTextFile(board, fileName);
    }

    /**
     * Creates the board.
     *
     * @param rowService    the service for row generation
     * @param row           the number of rows to create
     * @param columns       the number of columns to create
     */
    public Board createBoard(RowService rowService, int rows, int columns) {
        Board board = new Board();
        board.setRows(new ArrayList());
        for (int i = 0; i < rows; i++) {
            Row row = rowService.createRow(columns);
            board.getRows().add(row);
        }
        return board;
    }

    /**
     * Prints the board to the console.
     *
     * @param board the Board to print
     */
    public void print(Board board) {
		System.out.println();
        board.getRows().forEach(rowService::printRow);
    }

    /**
     * Searches for a string within the board's cells and prints the occurrences and positions.
     *
     * @param board the Board to search
     */
    public void search(Board board) {
        int occurrences = 0;
        ArrayList<StringBuilder> stringMatches = new ArrayList<>();
        String stringToFind = Utils.getValidatedString("String to find: ", MINIMUM_CELL_LENGTH, MAXIMUM_CELL_LENGTH);

        for (int rowIndex = 0; rowIndex < board.getRowCount(); rowIndex++) {
            Row row = board.getRows().get(rowIndex);
            for (int colIndex = 0; colIndex < row.getColumnCount(); colIndex++) {
                Cell cell = row.getCells().get(colIndex);
                String cellValue = cell.getKey() + cell.getValue();
                int startIndex = StringUtils.indexOf(cellValue, stringToFind);

                while (startIndex != -1) {
                    occurrences++;
                    int endIndex = startIndex + stringToFind.length() - 1;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Found at cell position [").append(rowIndex + 1).append("][").append(colIndex + 1).append("]")
                            .append(" within value ").append(cellValue).append(" at position [").append(startIndex + 1).append("]");
                    if (stringToFind.length() > 1) {
                        stringBuilder.append(" to [").append(endIndex + 1).append("]");
                    }
                    stringMatches.add(stringBuilder);
                    startIndex = StringUtils.indexOf(cellValue, stringToFind, startIndex + 1);
                }
            }
        }

        if (occurrences > 0) {
            System.out.println("Total occurrences: " + occurrences);
            System.out.println("String matches for '" + stringToFind + "': ");
            for (StringBuilder stringMatch : stringMatches) {
                System.out.println(stringMatch);
            }
        } else {
            System.out.println("String '" + stringToFind + "' was not found in the board.");
        }
    }

    /**
     * Edits a specific cell in the board by generating a new random value for it.
     *
     * @param board    the Board to edit
     * @param fileName the file name to save the modified Board
     */
    public void edit(Board board, String fileName) {
        int specificRow = Utils.getValidIndex("Enter row to edit: ", board.getRowCount()) - 1;
        int specificColumn = Utils.getValidIndex("Enter column to edit: ", board.getRows().get(specificRow).getCells().size()) - 1;

        Row row = board.getRows().get(specificRow);
        Cell cell = row.getCells().get(specificColumn);

        String originalKey = cell.getKey();
        String previousValue = cell.getValue();
        String newValue = cellService.generateValue();
        cell.setKey(originalKey);
        cell.setValue(newValue);
        fileService.saveBoardToTextFile(board, fileName);

        System.out.println("Successfully updated cell [" + (specificRow + 1) + "][" + (specificColumn + 1) + "] at key [" + originalKey + "] from " + previousValue + " to " + newValue);
    }

    /**
     * Resets the board with a new number of rows and columns.
     *
     * @param fileName the file name to save the reset Board
     * @return the reset Board object
     */
    public Board reset(String fileName) {
        int newRows = Utils.getIntegerInput("Enter new number of rows: ");
        int newColumns = Utils.getIntegerInput("Enter new number of columns: ");

        Board board = new Board();
        board = createBoard(new RowService(), newRows, newColumns);
        fileService.saveBoardToTextFile(board, fileName);
        print(board);
        System.out.println("Board has been reset to " + newRows + "x" + newColumns + ".");

        return board;
    }

    /**
     * Adds a new row with a specific number of cells to the board.
     *
     * @param board    the Board to modify
     * @param fileName the file name to save the modified Board
     */
    public void add(Board board, String fileName) {
        int newRowCellsToAdd = Utils.getIntegerInput("Number of cells to add in the new row: ");
        int index = Utils.getValidIndex("Insert before row: ", board.getRowCount()) - 1;

        Row newRow = rowService.createRow(newRowCellsToAdd);

        List<Row> rows = board.getRows();

        if (index == board.getRowCount()) {
            rows.add(newRow);
        } else {
            rows.add(index, newRow);
        }

        board.setRows(rows);
        fileService.saveBoardToTextFile(board, fileName);
        print(board);
        System.out.println("Successfully added a new row with " + newRowCellsToAdd + " cells before row " + (index + 1) + ".");
    }

    /**
     * Sorts the board's rows and cells either in ascending or descending order.
     *
     * @param board    the Board to sort
     * @param fileName the file name to save the sorted Board
     */
    public void sort(Board board, String fileName) {
        System.out.println("\n****************************************");
        System.out.println("               Sorting order");
        System.out.println("****************************************");
        System.out.println("1. Ascending");
        System.out.println("2. Descending");

        int sortingChoice = Utils.getValidIndex("Sorting choice: ", 2);
        int sortingOrder = (sortingChoice == 1) ? ASCENDING_ORDER : DESCENDING_ORDER;

        Comparator<Cell> cellComparator = (c1, c2) -> {
            String thisConcatenated = c1.getKey() + c1.getValue();
            String anotherConcatenated = c2.getKey() + c2.getValue();
            return sortingOrder == ASCENDING_ORDER
                ? StringUtils.compare(thisConcatenated, anotherConcatenated)
                : StringUtils.compare(anotherConcatenated, thisConcatenated);
        };

        Comparator<Row> rowComparator = (r1, r2) -> {
            String thisFirstCellConcatenated = r1.getCells().get(0).getKey() + r1.getCells().get(0).getValue();
            String anotherFirstCellConcatenated = r2.getCells().get(0).getKey() + r2.getCells().get(0).getValue();
            return sortingOrder == ASCENDING_ORDER
                ? StringUtils.compare(thisFirstCellConcatenated, anotherFirstCellConcatenated)
                : StringUtils.compare(anotherFirstCellConcatenated, thisFirstCellConcatenated);
        };

        List<Row> rows = board.getRows();
        rows.forEach(row -> Collections.sort(row.getCells(), cellComparator));
        Collections.sort(rows, rowComparator);

        print(board);
        fileService.saveBoardToTextFile(board, fileName);
        System.out.print("Board has been sorted.");
    }
}
