package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");
        List<Question> questions;
        try {
            questions = questionDao.findAll();
        } catch (QuestionReadException e) {
            String errorMessage = getErrorMessage(e);
            ioService.printLine(errorMessage);
            return new TestResult(student);
        }
        var testResult = new TestResult(student);

        processQuestions(questions, testResult);
        return testResult;
    }

    private void processQuestions(List<Question> questions, TestResult testResult) {
        for (Question question : questions) {
            ioService.printLine(question.text());
            String correctAnswer = displayAnswersAndGetCorrectAnswer(question.answers());
            String userAnswer = ioService.readString();

            boolean isAnswerValid = userAnswer.equalsIgnoreCase(correctAnswer);
            testResult.applyAnswer(question, isAnswerValid);
        }
    }

    private String displayAnswersAndGetCorrectAnswer(List<Answer> answers) {
        StringBuilder correctAnswer = new StringBuilder();

        for (int i = 0; i < answers.size(); i++) {
            char letter = (char) ('A' + i); // A, B, C...
            var answer = answers.get(i);
            ioService.printLine(letter + ") " + answer.text());

            if (answer.isCorrect()) {
                correctAnswer.append(letter);
            }
        }
        return correctAnswer.toString();
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
