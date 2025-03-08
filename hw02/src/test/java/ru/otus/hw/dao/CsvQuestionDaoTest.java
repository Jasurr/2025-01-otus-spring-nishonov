package ru.otus.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CsvQuestionDaoTest {

    private CsvQuestionDao csvQuestionDao;

    @BeforeEach
    void setUp() {
        var fileName = mock(TestFileNameProvider.class);
        when(fileName.getTestFileName()).thenReturn("questions.csv");
        csvQuestionDao = new CsvQuestionDao(fileName);
    }

    @Test
    void findAll_shouldReturnListOfQuestions() {
        List<Question> questions = csvQuestionDao.findAll();

        assertNotNull(questions);
        assertEquals(5, questions.size());
        // first question
        Question firstQuestion = questions.get(0);
        assertEquals("Is there life on Mars?", firstQuestion.text());
        assertEquals(3, firstQuestion.answers().size());
        // second question
        Question secondQuestion = questions.get(1);
        assertEquals("How should resources be loaded form jar in Java?", secondQuestion.text());
        assertEquals(3, secondQuestion.answers().size());
    }
}