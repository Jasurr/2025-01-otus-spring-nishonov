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

    private final LocalizedIOService ioService;

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
            return ioService.getMessage("Error.file.not.found");
        } else if (cause instanceof IOException) {
            return ioService.getMessage("Error.file.read");
        } else {
            return ioService.getMessage("Error.unexpected");
        }
    }
}
