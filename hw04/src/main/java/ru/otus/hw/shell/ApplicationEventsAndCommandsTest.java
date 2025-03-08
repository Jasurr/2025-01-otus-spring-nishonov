package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.service.TestRunnerService;

@ShellComponent
@RequiredArgsConstructor
public class ApplicationEventsAndCommandsTest {
    private final TestRunnerService testRunnerService;

    @ShellMethod(key = "run-tests", value = "Executes all configured tests")
    public void runTests() {
        testRunnerService.run();
    }
}
