package com.ecc.nichole.service;

import com.ecc.nichole.model.Board;
import com.ecc.nichole.model.Cell;
import com.ecc.nichole.model.Row;
import com.ecc.nichole.service.BoardService;
import com.ecc.nichole.service.RowService;
import com.ecc.nichole.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileServiceTest {

    @InjectMocks
    private FileService fileService;

    @Mock
    private Board mockBoard;

    @Mock
    private BoardService mockBoardService;

    @Mock
    private Row mockRow;

    @Mock
    private RowService mockRowService;

    @Mock
    private Cell mockCell;

    @Mock
    private CellService mockCellService;

    private static final char KEY_VALUE_SEPARATOR = '\u001D';  // Group separator
    private static final char CELL_SEPARATOR = '\u001F';       // Unit separator

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Board createMockBoard(int rows, int columns) {
        List<Row> mockRows = new ArrayList<>();

        when(mockRowService.createRow(columns)).thenAnswer(invocation -> {
            Row mockRow = mock(Row.class);
            List<Cell> mockCells = new ArrayList<>();
            for (int i = 0; i < columns; i++) {
                Cell mockCell = mock(Cell.class);
                when(mockCell.getKey()).thenReturn("k_" + (i + 1));
                when(mockCell.getValue()).thenReturn("v_" + (i + 1));
                mockCells.add(mockCell);
            }
            when(mockRow.getCells()).thenReturn(mockCells);
            return mockRow;
        });

        for (int i = 0; i < rows; i++) {
            Row mockRow = mockRowService.createRow(columns);
            mockRows.add(mockRow);
        }

        mockBoard = mock(Board.class);
        when(mockBoard.getRowCount()).thenReturn(rows);
        when(mockBoard.getRows()).thenReturn(mockRows);

        return mockBoard;
    }

    private String getExpectedOutput(Board board) {
        StringBuilder stringBuilder = new StringBuilder();
        int rows = board.getRowCount();
        List<Row> rowsList = board.getRows();

        for (int row = 0; row < rows; row++) {
            Row mockRow = rowsList.get(row);
            List<Cell> cells = mockRow.getCells();
            for (Cell cell : cells) {
                stringBuilder.append(cell.getKey())
                        .append(KEY_VALUE_SEPARATOR)
                        .append(cell.getValue())
                        .append(CELL_SEPARATOR);
            }
            stringBuilder.append(System.lineSeparator());
        }

        return stringBuilder.toString().trim();
    }
	
	private File createMockFile(String content) throws IOException {
        File mockFile = File.createTempFile("testBoard", ".txt");
        FileUtils.writeStringToFile(mockFile, content, StandardCharsets.UTF_8);
        return mockFile;
    }

    @ParameterizedTest
    @CsvSource({
        "1, 3, file.txt",
        "2, 4, abcd.txt",
        "3, 5, efgh.txt",
        "4, 6, ijkl.txt",
        "5, 7, mnop.txt"
    })
    public void shouldSaveBoardToTextFile(int rows, int columns, String fileName) throws IOException {
        mockBoard = createMockBoard(rows, columns);
		fileService.saveBoardToTextFile(mockBoard, fileName);
		Path filePath = Paths.get(fileName);

		try {
			assertTrue(Files.exists(filePath), "File should exist");
			String actualContent = Files.readString(filePath, StandardCharsets.UTF_8).trim();
			String expectedContent = getExpectedOutput(mockBoard).trim();

			assertEquals(expectedContent, actualContent, "File content does not match expected board content");
		} finally {
			Files.deleteIfExists(filePath);
		}
    }

    @ParameterizedTest
    @ValueSource(strings = {"k_1\u001Dv_1\u001Fk_2\u001Dv_2\u001F\nk_3\u001Dv_3\u001Fk_4\u001Dv_4\u001F"})
    public void shouldLoadBoardFromTextFile(String fileContent) throws IOException {
        File mockFile = createMockFile(fileContent);
        String fileName = mockFile.getAbsolutePath();

        List<Row> mockRows = new ArrayList<>();
        for (String line : fileContent.split("\n")) {
            Row row = mock(Row.class);
            List<Cell> cells = new ArrayList<>();
            for (String cellData : line.split("\u001F")) {
                String[] parts = cellData.split("\u001D");
                if (parts.length == 2) {
                    Cell cell = mock(Cell.class);
                    when(cell.getKey()).thenReturn(parts[0]);
                    when(cell.getValue()).thenReturn(parts[1]);
                    cells.add(cell);
                }
            }
            when(row.getCells()).thenReturn(cells);
            mockRows.add(row);
        }

        when(mockBoardService.createBoard(any(RowService.class), anyInt(), anyInt())).thenReturn(mockBoard);
        when(mockBoard.getRows()).thenReturn(mockRows);
        when(mockBoard.getRowCount()).thenReturn(mockRows.size());

        Board loadedBoard = fileService.loadBoardFromTextFile(fileName);

        assertNotNull(loadedBoard, "Loaded board should not be null");
        assertEquals(mockRows.size(), loadedBoard.getRowCount(), "Row count should match");

        for (int i = 0; i < loadedBoard.getRowCount(); i++) {
            Row loadedRow = loadedBoard.getRows().get(i);
            Row mockRow = mockRows.get(i);

            for (int j = 0; j < loadedRow.getCells().size(); j++) {
                Cell loadedCell = loadedRow.getCells().get(j);
                Cell mockCell = mockRow.getCells().get(j);

                assertEquals(mockCell.getKey(), loadedCell.getKey(), "Cell key should match");
                assertEquals(mockCell.getValue(), loadedCell.getValue(), "Cell value should match");
            }
        }
    }
}
