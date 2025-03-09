package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class TestServiceImplTest {

    @InjectMocks
    private TestServiceImpl testService;

    @Mock
    private LocalizedIOService ioService;

    @Mock
    private QuestionDao questionDao;

    private Student student;
    private List<Question> questions;

    @BeforeEach
    void setUp() {
        student = getStudent();
        questions = getQuestions();
    }

    @Test
    void shouldExecuteTestForStudent() {
        // Given
        when(questionDao.findAll()).thenReturn(questions);
        when(ioService.readString()).thenReturn("B"); // user will give correct answer
        // When
        TestResult result = testService.executeTestFor(student);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getAnsweredQuestions().size());

        assertTrue(result.getAnsweredQuestions().get(0)
                .answers()
                .get(1) // get(1) - B answer
                .isCorrect()
        );

        // Verify correct interactions
        verify(ioService).printLineLocalized("TestService.answer.the.questions");
        verify(ioService, times(1)).readString();
    }

    @Test
    void shouldHandleIncorrectAnswer() {
        when(questionDao.findAll()).thenReturn(questions);
        when(ioService.readString()).thenReturn("A"); // user will give incorrect answer

        // When
        TestResult result = testService.executeTestFor(student);
        // Then
        assertNotNull(result);
        assertEquals(1, result.getAnsweredQuestions().size());
        assertFalse(result.getAnsweredQuestions().get(0)
                .answers()
                .get(0) // get(0) - A answer
                .isCorrect());
    }

    private Student getStudent() {
        return new Student("Ivan", "Ivanov");
    }

    private List<Question> getQuestions() {
        return List.of(
                new Question("What is 2 + 2?", getAnswers())
        );
    }

    private List<Answer> getAnswers() {
        return List.of(
                new Answer("Answer 1", false),
                new Answer("Answer 2", true),
                new Answer("Answer 3", false)
        );
    }
}
