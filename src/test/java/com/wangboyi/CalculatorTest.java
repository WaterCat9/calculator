package com.wangboyi;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {
    Calculator c = new Calculator();

    @Test void addWorks() {
        assertEquals(4.0, c.eval("2", "+", "2"));
    }

    @Test void subtractWorks() {
        assertEquals(1.0, c.eval("3", "-", "2"));
    }

    @Test void multiplyWorks() {
        assertEquals(81.0, c.eval("9", "*", "9"));
    }

    @Test void divideWorks() {
        assertEquals(2.0, c.eval("10", "/", "5"));
    }

    @Test void divideByZeroThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> c.eval("10", "/", "0"));
    }

    @Test void addNegativeNumbers() {
        assertEquals(-2.0, c.eval("-5", "+", "3"));
    }

    @Test void multiplyByZero() {
        assertEquals(0.0, c.eval("0", "*", "99"));
    }

    @Test void divideDecimal() {
        assertEquals(3.0, c.eval("7.5", "/", "2.5"));
    }

    @Test void blankInputThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> c.eval("", "+", "2"));
    }
}
