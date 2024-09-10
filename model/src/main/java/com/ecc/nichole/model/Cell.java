package com.ecc.nichole.model;

/**
 * Represents a cell in the board with a key and a value.
 */
public class Cell {
    private String key;
    private String value;

    /**
     * Gets the key of the cell.
     *
     * @return the key of the cell
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key of the cell.
     *
     * @param key the new key for the cell
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the value of the cell.
     *
     * @return the value of the cell
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the cell.
     *
     * @param value the new value for the cell
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Returns a string representation of the cell.
     * The format is "Key: [key], Value: [value]".
     *
     * @return a string representation of the cell
     */
    @Override
    public String toString() {
        return "Key: " + key + ", Value: " + value;
    }
}
