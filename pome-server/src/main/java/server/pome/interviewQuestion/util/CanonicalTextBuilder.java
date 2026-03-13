package server.pome.interviewQuestion.util;

import server.pome.global.domain.InterviewQuestion;

import java.util.StringJoiner;

public class CanonicalTextBuilder {

    public static String build(InterviewQuestion q) {

        StringJoiner joiner = new StringJoiner("\n");

        if (q.getTopic() != null)
            joiner.add("TOPIC: " + q.getTopic());

        if (q.getIntent() != null)
            joiner.add("INTENT: " + q.getIntent());

        if (q.getRole() != null)
            joiner.add("ROLE: " + q.getRole());

        if (q.getKeywords() != null && !q.getKeywords().isEmpty())
            joiner.add("KEYWORDS: " + String.join(", ", q.getKeywords()));

        joiner.add("QUESTION: " + q.getQuestionText());

        return joiner.toString();
    }
}
