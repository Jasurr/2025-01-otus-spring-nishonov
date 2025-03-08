package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class TestServiceImplTest {
    private IOService ioService;
    private QuestionDao questionDao;
    private TestServiceImpl testService;

    @BeforeEach
    void setUp() {
        this.ioService = mock(IOService.class);
        this.questionDao = mock(QuestionDao.class);
        this.testService = new TestServiceImpl(ioService, questionDao);
    }

    @Test
    void shouldExecuteTestForStudent() {
        var student = new Student("Ivan", "Ivanov");
        var answers = List.of(
                new Answer("Answer 1", true),
                new Answer("Answer 2", false),
                new Answer("Answer 3", false)
        );
        var question = new Question("What is 2 + 2?", answers);
        when(questionDao.findAll()).thenReturn(List.of(question));
        when(ioService.readString()).thenReturn("A");

        // When
        TestResult testResult = testService.executeTestFor(student);

        // Then
        assertNotNull(testResult);
        assertEquals(student, testResult.getStudent());
        assertEquals(1, testResult.getAnsweredQuestions().size());
        assertTrue(testResult.getAnsweredQuestions().get(0)
                .answers()
                .get(0)// Answer A is index 0
                .isCorrect()
        );

        verify(ioService).printLine("What is 2 + 2?");
        verify(ioService, timeout(1000)).readString();
    }

    @Test
    void shouldHandleIncorrectAnswer() {
        var student = new Student("Ivan", "Ivanov");
        var answers = List.of(
                new Answer("Answer 1", true),
                new Answer("Answer 2", false),
                new Answer("Answer 3", false)
        );
        var question = new Question("What is 2 + 2?", answers);
        when(questionDao.findAll()).thenReturn(List.of(question));
        when(ioService.readString()).thenReturn("B");

        // When
        TestResult testResult = testService.executeTestFor(student);

        // Then
        assertNotNull(testResult);
        assertEquals(student, testResult.getStudent());
        assertEquals(1, testResult.getAnsweredQuestions().size());
        assertFalse(testResult.getAnsweredQuestions().get(0)
                .answers()
                .get(1)// Answer B is index 1
                .isCorrect()
        );

        verify(ioService).printLine("What is 2 + 2?");
        verify(ioService, timeout(1000)).readString();
    }
}