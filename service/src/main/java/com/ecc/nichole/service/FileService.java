package com.ecc.nichole.service;

import org.apache.commons.io.FileUtils;

import com.ecc.nichole.model.Board;
import com.ecc.nichole.model.Cell;
import com.ecc.nichole.model.Row;

import com.ecc.nichole.service.BoardService;
import com.ecc.nichole.service.CellService;
import com.ecc.nichole.service.RowService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for handling file operations related to the Board.
 */
public class FileService {
    private static final char KEY_VALUE_SEPARATOR = '\u001D';  // Group separator
    private static final char CELL_SEPARATOR = '\u001F';       // Unit separator

    /**
     * Saves the given board to a text file. Each cell is stored in the format key=value,
     * and rows are stored line by line.
     *
     * @param board   The board object to be saved
     * @param fileName The file where the board data will be stored
     */
    public void saveBoardToTextFile(Board board, String fileName) {
        File file = new File(fileName);
        List<String> lines = new ArrayList<>();
        List<Row> rows = board.getRows();
        
        for (Row row : rows) {
            List<Cell> cells = row.getCells();
            StringBuilder line = new StringBuilder();
            for (Cell cell : cells) {
                line.append(cell.getKey())
                    .append(KEY_VALUE_SEPARATOR)
                    .append(cell.getValue())
                    .append(CELL_SEPARATOR);
            }
            lines.add(line.toString());
        }

        try {
            FileUtils.writeLines(file, lines);
        } catch (IOException e) {
            System.err.println("Error: An I/O error occurred while saving the board to the file.");
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Loads the board data from a text file, assuming that the format matches the way it was saved.
     *
     * @param fileName The file from which the board data is to be loaded
     * @return The reconstructed Board object
     */
    public Board loadBoardFromTextFile(String fileName) {
        File file = new File(fileName);
        List<Row> rows = new ArrayList<>();
        Board board = null;
		FileService fileService = new FileService();
		CellService cellService = new CellService();
		RowService rowService = new RowService();
        BoardService boardService = new BoardService(cellService, rowService, fileService);

        try {
            List<String> lines = FileUtils.readLines(file, "UTF-8");

            if (!lines.isEmpty()) {
                String firstLine = lines.get(0);
                String[] cellValues = firstLine.split(Character.toString(CELL_SEPARATOR));
                board = new Board();
                board = boardService.createBoard(rowService, 0, cellValues.length);

                for (String line : lines) {
                    Row row = new Row();
                    String[] cellValuesInRow = line.split(Character.toString(CELL_SEPARATOR));
                    for (String cellValue : cellValuesInRow) {
                        String[] splitCell = cellValue.split(Character.toString(KEY_VALUE_SEPARATOR));

                        if (splitCell.length == 2) {
                            Cell cell = new Cell();
                            cell.setKey(splitCell[0]);
                            cell.setValue(splitCell[1]);
                            row.getCells().add(cell);
                        } else {
                            System.err.println("Error: Invalid cell data format in file.");
                        }
                    }
                    rows.add(row);
                }

                if (board != null) {
                    board.setRows(rows);
                }
            }

        } catch (IOException e) {
            System.err.println("Error: An I/O error occurred while loading the board from the file.");
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }

        return board;
    }
}
