package com.ecc.nichole.service;

import com.ecc.nichole.model.Cell;
import com.ecc.nichole.model.Row;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

public class RowServiceTest {

    @InjectMocks
    private RowService rowService;

    @Mock
    private CellService mockCellService;
	
	@Mock
	private Row mockRow;
	
	@Mock
	private Cell mockCell;

    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setUpAll() {
        MockitoAnnotations.openMocks(this);
        System.setOut(new PrintStream(outContent));
    }

    private void setupMockRow(int maxCells) {
        List<Cell> mockCells = new ArrayList<>();
        for (int i = 0; i < maxCells; i++) {
            when(mockCell.getKey()).thenReturn("k_" + (i + 1));
            when(mockCell.getValue()).thenReturn("v_" + (i + 1));
            mockCells.add(mockCell);
        }
        when(mockRow.getCells()).thenReturn(mockCells);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @DisplayName("Test creating row with specified number of cells")
    public void shouldCreateRow(int maxCells) {
        setupMockRow(maxCells);
        rowService.createRow(maxCells);

        Assertions.assertNotNull(mockRow, "Row is null.");
        Assertions.assertFalse(mockRow.getCells().isEmpty(), "Row has 0 cells");
        Assertions.assertEquals(maxCells, mockRow.getCells().size(), "Row cells is not equal to the specified cells for row creation.");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @DisplayName("Test printRow prints correctly formatted cell data")
    public void shouldPrintRow(int maxCells) {
        setupMockRow(maxCells);
        rowService.printRow(mockRow);

        String output = outContent.toString();
        for (int i = 0; i < maxCells; i++) {
            String expectedOutput = "[k_" + (i + 1) + ", v_" + (i + 1) + "] ";
            Assertions.assertTrue(output.contains(expectedOutput), "Output should contain correctly formatted cell data");
        }
    }
}
