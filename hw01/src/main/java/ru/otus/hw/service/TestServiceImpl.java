package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        // Получить вопросы из дао и вывести их с вариантами ответов
        try {
            var questions = questionDao.findAll();
            printQuestions(questions);
        } catch (QuestionReadException e) {
            String errorMessage = getErrorMessage(e);
            ioService.printLine(errorMessage);
        }
    }

    private void printQuestions(List<Question> questions) {
        for (int i = 0; i < questions.size(); i++) {
            var question = questions.get(i);
            ioService.printLine(i + 1 + " - " + question.text());
            printAnswers(question.answers());
        }
    }

    private void printAnswers(List<Answer> answers) {
        for (int i = 0; i < answers.size(); i++) {
            var answer = answers.get(i);
            char letter = (char) ('A' + i); // A, B, C...
            ioService.printLine("\t" + letter + ") " + answer.text());
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
