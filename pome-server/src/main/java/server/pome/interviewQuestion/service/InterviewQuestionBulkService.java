package server.pome.interviewQuestion.service;

import com.opencsv.CSVReader;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import server.pome.global.domain.InterviewQuestion;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.interviewQuestion.repository.InterviewQuestionRepository;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewQuestionBulkService {

    private final InterviewQuestionRepository repository;

    @Transactional
    public void bulkInsert() {
        try {
            ClassPathResource resource =
                    new ClassPathResource("data/interview_questions.csv");

            CSVReader reader = new CSVReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
            );

            List<String[]> rows = reader.readAll();

            // 첫 줄 제거
            rows.remove(0);

            for (String[] row : rows) {

                String questionText = row[0];
                String topic = row[1];
                String intent = row[2];
                String role = row[3];
                String scope = row[4];

                List<String> keywords = Arrays.stream(row[5].split(","))
                        .map(String::trim)
                        .toList();

                InterviewQuestion question = InterviewQuestion.builder()
                        .questionText(questionText)
                        .topic(topic)
                        .intent(intent)
                        .role(role)
                        .scope(scope)
                        .keywords(keywords)
                        .build();

                repository.save(question);
            }

        } catch (BaseException e) {
        throw e;

        } catch (IOException e) {
        throw new BaseException(BaseResponseStatus.CSV_PARSE_ERROR);

        } catch (Exception e) {
        throw new BaseException(BaseResponseStatus.INTERVIEW_BULK_INSERT_FAILED);
        }
    }
}
