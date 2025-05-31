package com.example.tdandroid.utils;

import org.junit.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class SensorUtilsTest {
    @Test
    public void testMapLabelsToCodes() {
        List<String> input = Arrays.asList("Température", "Luminosité", "Humidité", "Pression");
        List<String> expected = Arrays.asList("T", "L", "H", "P");

        List<String> result = SensorUtils.mapLabelsToCodes(input);
        assertEquals(expected, result);
    }

    @Test
    public void testMapLabelsToCodesWithUnknown() {
        List<String> input = Arrays.asList("Température", "Vent");  // "Vent" doit être ignoré
        List<String> result = SensorUtils.mapLabelsToCodes(input);

        assertEquals(1, result.size());
        assertEquals("T", result.get(0));
    }
}
