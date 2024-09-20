package com.ecc.nichole.service;

import com.ecc.nichole.model.Board;
import com.ecc.nichole.model.Cell;
import com.ecc.nichole.model.Row;

import com.ecc.nichole.service.BoardService;
import com.ecc.nichole.service.CellService;
import com.ecc.nichole.service.RowService;
import com.ecc.nichole.service.FileService;

import com.ecc.nichole.util.Utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.ArgumentMatchers.any;


public class BoardServiceTest {

    @InjectMocks
    private BoardService boardService;
	
	@Mock
	private Board mockBoard;

    @Mock
    private RowService mockRowService;
	
	@Mock
	private Row mockRow;
	
	@Mock
    private CellService mockCellService;
	
	@Mock
	private Cell mockCell;

    @Mock
    private FileService mockFileService;
	
	private ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
		System.setOut(new PrintStream(outContent));
    }
	
	private Board createMockBoard(int rows, int columns) {
		List<Row> mockRows = new ArrayList<>();
		
		when(mockRowService.createRow(columns)).thenAnswer(invocation -> {
			List<Cell> mockCells = new ArrayList<>();
			for (int i = 0; i < columns; i++) {
				when(mockCell.getKey()).thenReturn("k_" + (i + 1));
				when(mockCell.getValue()).thenReturn("v_" + (i + 1));
				mockCells.add(mockCell);
			}
			when(mockRow.getColumnCount()).thenReturn(columns);
			when(mockRow.getCells()).thenReturn(mockCells);
			return mockRow;
		});
		
		doAnswer(invocation -> {
			mockRow = invocation.getArgument(0);
			mockRow.getCells().forEach(cell -> System.out.print("[" + cell.getKey() + ", " + cell.getValue() + "]"));
			System.out.println();
			return null;
		}).when(mockRowService).printRow(any(Row.class));
		
		for (int i = 0; i < rows; i++) {
			mockRow = mockRowService.createRow(columns);
			mockRows.add(mockRow);
		}

		when(mockBoard.getRowCount()).thenReturn(rows);
		when(mockBoard.getRows()).thenReturn(mockRows);

		return mockBoard;
	}
	
	@Nested
	class FileRelatedOperationsTests {
	
		@ParameterizedTest
		@ValueSource(strings = {"abc.txt", "def.txt"})
		public void shouldLoadBoard(String fileName) {
			mockBoard = boardService.loadBoard(fileName);
			verify(mockFileService).loadBoardFromTextFile(fileName);
		}
		
		@ParameterizedTest
		@CsvSource({
			"1, 3, abc.txt",
			"2, 4, def.txt",
			"3, 5, ghi.txt",
			"4, 6, jkl.txt",
			"5, 7, mno.txt"
		})
		public void shouldSaveBoardToFile(int rows, int columns, String fileName) {
			mockBoard = createMockBoard(rows, columns);
			boardService.saveBoard(mockBoard, fileName);
			verify(mockFileService).saveBoardToTextFile(mockBoard, fileName);
		}
	}
	
	@Nested
	class PrimaryBoardOperationTests {
		
		@Nested
		class ValidTestCases {
			
			@ParameterizedTest
			@CsvSource({
				"1, 3",
				"2, 4",
				"3, 5",
				"4, 6",
				"5, 7"
			})
			@DisplayName("Test createBoard creates correct number of rows and columns")
			public void shouldCreateBoard(int rows, int columns) {
				when(mockRow.getColumnCount()).thenReturn(columns);
				when(mockRowService.createRow(columns)).thenReturn(mockRow);

				Board board = boardService.createBoard(mockRowService, rows, columns);

				Assertions.assertEquals(rows, board.getRowCount(), "Board should have correct number of rows");
				for (Row r : board.getRows()) {
					Assertions.assertEquals(columns, r.getColumnCount(), "Each row should have correct number of columns");
				}
			}
			
			@ParameterizedTest
			@CsvSource({
				"2, 4, v",
				"3, 5, v_",
				"5, 7, k"
			})
			@DisplayName("Test search should successfully find matches")
			public void shouldFindMatches(int rows, int columns, String stringToFind) {
				mockBoard = createMockBoard(rows, columns);
				Utils.scanner = new Scanner(new ByteArrayInputStream((stringToFind).getBytes()));
				boardService.search(mockBoard);
				String output = outContent.toString().trim();
				Assertions.assertTrue(output.contains("String matches for"), "Expected to find matches for the string '" + stringToFind + "'.");
			}
			
			@ParameterizedTest
			@CsvSource({
				"1, 3, 1, 1, abc.txt",
				"2, 4, 1, 1, def.txt",
				"3, 5, 1, 1, ghi.txt",
				"4, 6, 1, 1, jkl.txt",
				"5, 7, 1, 1, mno.txt"
			})
			public void shouldEditBoard(int rows, int columns, int rowToEdit, int columnToEdit, String fileName) {
				mockBoard = createMockBoard(rows, columns);
				Utils.scanner = new Scanner(new ByteArrayInputStream((rowToEdit + "\n" + columnToEdit).getBytes()));
				boardService.edit(mockBoard, fileName);
				
				String output = outContent.toString().trim();
				Assertions.assertTrue(output.contains("Successfully updated cell"), "Cell was not updated.");
			}
			
			@ParameterizedTest
			@CsvSource({
				"1, 3, 1, 1, abc.txt",
				"2, 4, 1, 1, def.txt",
				"3, 5, 1, 1, ghi.txt",
				"4, 6, 1, 1, jkl.txt",
				"5, 7, 1, 1, mno.txt"
			})
			@DisplayName("Test search should add row to board")
			public void shouldAddRow(int rows, int columns, int cellsToAdd, int newRowIndex, String fileName) {
				mockBoard = createMockBoard(rows, columns);
				int originalRows = mockBoard.getRows().size();
				Utils.scanner = new Scanner(new ByteArrayInputStream((cellsToAdd + "\n" + newRowIndex).getBytes()));
				boardService.add(mockBoard, fileName);
				int newRows = mockBoard.getRows().size();
				
				Assertions.assertNotEquals(originalRows, newRows, "No row was added.");
			}
			
			@ParameterizedTest
			@CsvSource({
				"1, 3, 1, abc.txt",
				"2, 4, 2, def.txt",
				"3, 5, 1, ghi.txt",
				"4, 6, 2, jkl.txt",
				"5, 7, 1, mno.txt"
			})
			public void shouldSortBoard(int rows, int columns, int sortingChoice, String fileName) {
				int ascendingChoice = 1;
				mockBoard = createMockBoard(rows, columns);
				Utils.scanner = new Scanner(new ByteArrayInputStream((String.valueOf(sortingChoice)).getBytes()));
				boardService.sort(mockBoard, fileName);

				List<Row> rowsList = mockBoard.getRows();

				for (Row row : rowsList) {
					List<Cell> cells = row.getCells();
					for (int i = 0; i < cells.size() - 1; i++) {
						String currentConcatenated = cells.get(i).getKey() + cells.get(i).getValue();
						String nextConcatenated = cells.get(i + 1).getKey() + cells.get(i + 1).getValue();
						switch(sortingChoice) {
							case 1 -> Assertions.assertTrue(currentConcatenated.compareTo(nextConcatenated) <= 0, "Cells within row are not sorted in ascending order.");
							case 2 -> Assertions.assertTrue(currentConcatenated.compareTo(nextConcatenated) >= 0, "Cells within row are not sorted in descending order.");
						}
					}
				}

				for (int i = 0; i < rowsList.size() - 1; i++) {
					String currentRowFirstCell = rowsList.get(i).getCells().get(0).getKey() + rowsList.get(i).getCells().get(0).getValue();
					String nextRowFirstCell = rowsList.get(i + 1).getCells().get(0).getKey() + rowsList.get(i + 1).getCells().get(0).getValue();
					switch(sortingChoice) {
						case 1 -> Assertions.assertTrue(currentRowFirstCell.compareTo(nextRowFirstCell) <= 0, "Rows are not sorted in ascending order.");
						case 2 -> Assertions.assertTrue(currentRowFirstCell.compareTo(nextRowFirstCell) >= 0, "Rows are not sorted in descending order.");
					}
				}
			}
			
			@ParameterizedTest
			@CsvSource({
				"1, 3",
				"2, 4",
				"3, 5",
				"4, 6",
				"5, 7"
			})
			@DisplayName("Test printBoard should print the board")
			public void shouldPrintBoard(int rows, int columns) {
				mockBoard = createMockBoard(rows, columns);
				boardService.print(mockBoard);

				StringBuilder expectedOutput = new StringBuilder();
				mockBoard.getRows().forEach(row -> {
					row.getCells().forEach(cell -> {
						expectedOutput.append("[")
							.append(cell.getKey())
							.append(", ")
							.append(cell.getValue())
							.append("]");
					});
					expectedOutput.append(System.lineSeparator());
				});

				String output = outContent.toString().trim();
				String expected = expectedOutput.toString().trim();
				Assertions.assertEquals(expected, output, "Output should match the expected board data");
			}
			
			@ParameterizedTest
			@CsvSource({
				"1, 3, abc.txt",
				"2, 4, def.txt",
				"3, 5, ghi.txt",
				"4, 6, jkl.txt",
				"5, 7, mno.txt"
			})
			@DisplayName("Test reset should reset board")
			public void shouldResetBoard(int rows, int columns, String fileName) {
				mockBoard = createMockBoard(rows, columns);
				Utils.scanner = new Scanner(new ByteArrayInputStream((rows + "\n" + columns).getBytes()));
				Board newBoard = boardService.reset(fileName);
				
				Assertions.assertNotEquals(mockBoard, newBoard, "Mock board and new board is the same");
			}
		}
		
		@Nested
		class InvalidTestCases {
			
			@ParameterizedTest
			@CsvSource({
				"-1, 0",
				"0, -1",
				"0, 0",
				"-1, -1"
			})
			@DisplayName("Test createBoard creates correct number of rows and columns")
			public void shouldNotCreateBoard(int rows, int columns) {
				mockBoard = boardService.createBoard(mockRowService, rows, columns);

				String output = outContent.toString().trim();	
				Assertions.assertTrue(mockBoard.getRows().isEmpty(), "Board should not be created for invalid input.");
			}
			
			@ParameterizedTest
			@CsvSource({
				"1, 3, aaa",
				"2, 4, a",
				"3, 5, aa"
			})
			@DisplayName("Test search should not find matches")
			public void shouldNotFindMatches(int rows, int columns, String stringToFind) {
				mockBoard = createMockBoard(rows, columns);
				Utils.scanner = new Scanner(new ByteArrayInputStream((stringToFind).getBytes()));
				boardService.search(mockBoard);
				String output = outContent.toString().trim();
				Assertions.assertTrue(output.contains("was not found"), "Found matches for the string '" + stringToFind + "'.");
			}
			
			@ParameterizedTest
			@CsvSource({
				"2, 3, -1, 1, invalid.txt",
				"2, 4, 0, -1, invalid.txt",
				"3, 5, -1, 10, invalid.txt"
			})
			@DisplayName("Test shouldNotEditBoard should not edit the board")
			public void shouldNotEditBoard(int rows, int columns, int rowToEdit, int columnToEdit, String fileName) {
				mockBoard = createMockBoard(rows, columns);		
				
				String input = "";
				if (rowToEdit < 1 || rowToEdit > rows) {
					input += rowToEdit + "\n1\n";
				} else {
					input += rowToEdit + "\n";
				}
				
				if (columnToEdit < 1 || columnToEdit > columns) {
					input += columnToEdit + "\n1\n";
				} else {
					input += columnToEdit + "\n";
				}
				
				Utils.scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
				boardService.edit(mockBoard, fileName);

				String output = outContent.toString().trim();
				Assertions.assertTrue(output.contains("Value must be greater than"), "Board should not be edited for invalid input.");
			}
			
			@ParameterizedTest
			@CsvSource({
				"1, 3, 1, -1, abc.txt",
				"2, 4, -1, 1, def.txt",
				"3, 5, 1, -1, ghi.txt",
				"4, 6, 1, 0, jkl.txt",
				"5, 7, 0, 0, mno.txt"
			})
			@DisplayName("Test search should add row to board")
			 public void shouldNotAddRow(int rows, int columns, int cellsToAdd, int newRowIndex, String fileName) {
				mockBoard = createMockBoard(rows, columns);
				
				String input = "";
				if (cellsToAdd < 1) {
					input += cellsToAdd + "\n1\n";
				} else {
					input += cellsToAdd + "\n";
				} 
				
				if (newRowIndex < 1) {
					input += newRowIndex + "\n1\n";
				} else {
					input += newRowIndex + "\n";
				}
				
				Utils.scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
				boardService.add(mockBoard, fileName);
				
				String output = outContent.toString().trim();
				Assertions.assertTrue(output.contains("Value must be greater than"), "Board should not be added for invalid input.");
			}
			
			@ParameterizedTest
			@CsvSource({
				"1, 3, -1, abc.txt",
				"2, 4, 0, def.txt",
				"3, 5, 3, ghi.txt"
			})
			public void shouldNotSortBoard(int rows, int columns, int sortingChoice, String fileName) {
				
				String input = "";
				if (sortingChoice < 1 || sortingChoice > 2) {
					input += sortingChoice + "\n1\n";
				} else {
					input += sortingChoice + "\n";
				} 
				
				mockBoard = createMockBoard(rows, columns);
				Utils.scanner = new Scanner(new ByteArrayInputStream((input).getBytes()));
				boardService.sort(mockBoard, fileName);

				String output = outContent.toString().trim();
				Assertions.assertTrue(output.contains("value not allowed") || output.contains("Value must be greater than"), "Board should not be sorted for invalid input " + sortingChoice);
			}
			
			@ParameterizedTest
			@CsvSource({
				"0, 0",
				"0, -1",
				"-1, 0",
				"-1, -1"
			})
			@DisplayName("Test printBoard should print the board")
			public void shouldNotPrintBoard(int rows, int columns) {
				mockBoard = createMockBoard(rows, columns);
				boardService.print(mockBoard);
				
				String output = outContent.toString().trim();
				Assertions.assertTrue(output.isEmpty(), "Output should be empty for invalid board sizes");
			}
		}
	}
}