package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestServiceImplTest {

    @InjectMocks
    private TestServiceImpl testService;

    @Mock
    private LocalizedIOService ioService;

    @Mock
    private QuestionDao questionDao;

    @Test
    void shouldExecuteTestForStudent() {
        // Given
        Student student = new Student("Ivan", "Ivanov");
        List<Answer> answers = List.of(
                new Answer("Answer 1", false),
                new Answer("Answer 2", true),  // right answer
                new Answer("Answer 3", false)
        );
        List<Question> questions = List.of(new Question("What is 2 + 2?", answers));

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
        // Given
        Student student = new Student("Ivan", "Ivanov");
        List<Answer> answers = List.of(
                new Answer("Answer 1", false),
                new Answer("Answer 2", false),
                new Answer("Answer 3", false),
                new Answer("Answer 4", true)// right answer
        );
        List<Question> questions = List.of(new Question("What is 2 + 2?", answers));

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
}
