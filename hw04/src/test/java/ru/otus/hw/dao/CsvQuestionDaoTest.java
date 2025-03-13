package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
class CsvQuestionDaoTest {
    @Autowired
    private CsvQuestionDao csvQuestionDao;

    @Test
    void findAll_shouldReturnListOfQuestions() {
        List<Question> questions = csvQuestionDao.findAll();

        assertNotNull(questions);
        assertEquals(5, questions.size());

        Question firstQuestion = questions.get(0);
        assertEquals("Is there life on Mars?", firstQuestion.text());
        assertEquals(3, firstQuestion.answers().size());

        Question secondQuestion = questions.get(1);
        assertEquals("How should resources be loaded form jar in Java?", secondQuestion.text());
        assertEquals(3, secondQuestion.answers().size());
    }
}