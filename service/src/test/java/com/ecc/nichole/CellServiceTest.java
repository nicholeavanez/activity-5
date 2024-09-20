package com.ecc.nichole;

import com.ecc.nichole.model.Cell;
import com.ecc.nichole.service.CellService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CellServiceTest {

    private static final int CELL_MAX_CHARACTERS = 3;

    @InjectMocks
    private CellService cellService;
	
	@Mock
	private Cell mockCell;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @RepeatedTest(100)
    @DisplayName("Test generateValue functionality")
    public void shouldGenerateValue() {
        String value = cellService.generateValue();
        Assertions.assertNotNull(value, "Generated value should not be null.");
        Assertions.assertEquals(CELL_MAX_CHARACTERS, value.length(), "Generated value length is not equal to predefined max characters");
        for (char c : value.toCharArray()) {
            Assertions.assertTrue(c >= 32 && c <= 126, "Generated value character " + c + " is out of printable ASCII range");
        }
    }

    @RepeatedTest(100)
    @DisplayName("Test createRandomCell functionality")
    public void shouldCreateRandomCell() {
        mockCell = cellService.createRandomCell();
        Assertions.assertNotNull(mockCell, "Cell should not be null.");
        Assertions.assertEquals(CELL_MAX_CHARACTERS, mockCell.getKey().length(), "Key length is not equal to cell's predefined max characters");
        Assertions.assertEquals(CELL_MAX_CHARACTERS, mockCell.getValue().length(), "Value length is not equal to cell's predefined max characters");
        for (char c : mockCell.getKey().toCharArray()) {
            Assertions.assertTrue(c >= 32 && c <= 126, "Generated key character " + c + " is out of printable ASCII range");
        }
        for (char c : mockCell.getValue().toCharArray()) {
            Assertions.assertTrue(c >= 32 && c <= 126, "Generated value character " + c + " is out of printable ASCII range");
        }
    }
}
