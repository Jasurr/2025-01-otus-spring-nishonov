package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;

import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        // Получить вопросы из дао и вывести их с вариантами ответов
        var questions = questionDao.findAll();
        AtomicInteger index = new AtomicInteger(1);
        questions.forEach(question -> {
            ioService.printLine(index.getAndIncrement() + " - " + question.text());
            AtomicInteger answerIndex = new AtomicInteger(0);
            question.answers()
                    .forEach(answer -> {
                        char letter = (char) ('A' + answerIndex.getAndIncrement()); // A, B, C...
                        ioService.printLine("\t" + letter + ") " + answer.text());
                    });
        });
    }
}
