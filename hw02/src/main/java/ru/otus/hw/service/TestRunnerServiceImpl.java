package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.FileNotFoundException;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

    private final TestService testService;

    private final StudentService studentService;

    private final ResultService resultService;

    private final IOService ioService;

    @Override
    public void run() {
        var student = studentService.determineCurrentStudent();
        try {
            var testResult = testService.executeTestFor(student);
            resultService.showResult(testResult);
        } catch (QuestionReadException e) {
            String errorMessage = getErrorMessage(e);
            ioService.printLine(errorMessage);
        }
    }

    private String getErrorMessage(QuestionReadException e) {
        Throwable cause = e.getCause();
        if (cause instanceof FileNotFoundException) {
            return "Error: The test file is missing. Please contact support.";
        } else if (cause instanceof IOException) {
            return "Error: Unable to read the test file. Please try again later.";
        } else {
            return "Error: An unexpected error occurred. Please contact support.";
        }
    }
}
