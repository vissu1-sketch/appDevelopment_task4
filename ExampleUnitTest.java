package com.example.textprint;

import org.junit.Test;
import static org.junit.Assert.*;

public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() {
        assertEquals(4.0, addNumbers(2.0, 2.0), 0.0001);
    }

    @Test
    public void subtraction_isCorrect() {
        assertEquals(5.0, subtractNumbers(10.0, 5.0), 0.0001);
    }

    @Test
    public void multiplication_isCorrect() {
        assertEquals(12.0, multiplyNumbers(3.0, 4.0), 0.0001);
    }

    @Test
    public void division_isCorrect() {
        assertEquals(5.0, divideNumbers(20.0, 4.0), 0.0001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void divisionByZero_throwsException() {
        divideNumbers(10.0, 0.0);
    }

    private double addNumbers(double a, double b) {
        return a + b;
    }

    private double subtractNumbers(double a, double b) {
        return a - b;
    }

    private double multiplyNumbers(double a, double b) {
        return a * b;
    }

    private double divideNumbers(double a, double b) {
        if (b == 0.0) {
            throw new IllegalArgumentException("Cannot divide by zero");
        }
        return a / b;
    }
}