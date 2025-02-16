package ru.otus.hw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.service.StreamsIOService;

import java.io.PrintStream;

import static org.mockito.Mockito.*;

public class StreamsIOServiceTest {
    private PrintStream mockPrintStream;
    private StreamsIOService ioService;

    @BeforeEach
    void setUp() {
        // Mock a PrintStream object
        mockPrintStream = mock(PrintStream.class);
        ioService = new StreamsIOService(mockPrintStream);
    }

    @Test
    void testPrintLine() {
        // Given
        String testMessage = "Hello, World!";

        // When
        ioService.printLine(testMessage);

        // Check
        verify(mockPrintStream, times(1)).println(testMessage);
    }

    @Test
    void testPrintFormattedLine() {
        // Given
        String format = "Number: %d";
        int number = 42;

        // When
        ioService.printFormattedLine(format, number);

        // Check
        verify(mockPrintStream, times(1)).printf(format + "%n", number);
    }

}
