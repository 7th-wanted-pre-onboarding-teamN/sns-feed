package wanted.n.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import wanted.n.dto.HashtagsResponseDTO;
import wanted.n.dto.LogOnFireDTO;
import wanted.n.exception.ErrorCode;

import java.util.*;

import static wanted.n.enums.MailComponents.*;

@Service
@RequiredArgsConstructor
public class LogService {
    private final RedisService redisService;
    private final EmailService emailService;
    private static final Logger log = LoggerFactory.getLogger(LogService.class);

    private final static String KEY_TAG = "tag:"; // 태그를 저장하는 키
    private final static String KEY_HOT_HASHTAG = "tags"; // 핫 해시태그 리스트를 저장하는 키
    private final static String KEY_POSTING = "posting:"; // 리스트를 저장하는 키

    private final static Integer MAX_POSTING = 10;

    /**
     * 최근 3시간 동안 많이 사용된 해시태그 리스트를 조회
     * 조건 1) 최대 5개 = MAX_TAG
     */

    // 최근 3시간 동안 많이 사용된 해시태그 저장
    public HashtagsResponseDTO getSortedTags() {
        List<Object> hashtags = new ArrayList<>(redisService.findHotTags());
        return new HashtagsResponseDTO(ErrorCode.HOT_HASHTAG_OK, hashtags);
    }

    // 많이 사용된 해시태그 데이터 조회
    public void getCountByTagForLast3Hours() {
        redisService.saveSortedTags();
    }


    /**
     * on Fire 조건 확인
     * 조건 1) 12시간 동안 100번 이상 조회한 경우
     * 조건 2) 12시간 동안 전체의 50% 보다 많은 경우 (생성된지 3시간 이상인 posting 조건이 포함)
     * 조건 3) preView 높은 순으로 최대 10개 = MAX_POSTING
     */

    // 메일을 보내야 하는 posting 조회 및 전송
    public void checkPostingOnFireEmail() {
        PriorityQueue<LogOnFireDTO> candidateKeys = getCandidateKeysWithKeyPattern();

        int count = 0;
        for (LogOnFireDTO dto : candidateKeys) {

            // dto.getPostingId()로 postingRepository에서 viewCount 조회 -> 임시 값!
            long viewCount = 200;

            if (dto.getPreView() > viewCount / 2 && count < MAX_POSTING) { // 조건 2 : 3시간 동안 전체 조회수의 50% 이상
                // 임시 값!
                long id = 123;
                String email = "1212guswjd@gmail.com";
                String title = "-제--목-";

                emailService.sendEmail(
                        email,
                        NOTIFICATION_SUBJECT,
                        NOTIFICATION_MESSAGE,
                        new Object[]{title, String.valueOf(viewCount)}
                );

                log.info("단기간 급상승 게시물 메일 전송 id :" + id);
                count += 1;
            }
        }
    }

    // keyPattern에 해당하는 각 key마다 데이터의 개수 조회
    private PriorityQueue<LogOnFireDTO> getCandidateKeysWithKeyPattern() {
        String postingKey = KEY_POSTING + "*";
        Set<String> keys = redisService.findKeyWithPattern(postingKey);

        Comparator<LogOnFireDTO> comparator = Comparator.comparingLong(LogOnFireDTO::getPreView).reversed();

        // 조건에 맞는 posting 탐색 최적화
        PriorityQueue<LogOnFireDTO> priorityQueue = new PriorityQueue<>(comparator);

        for (String key : keys) {
            long prevView = redisService.getListSize(key);

            if (prevView >= 100) { // on Fire 조건 1 : 100번 이상 조회
                long pid = Long.parseLong(key.substring(KEY_POSTING.length()));
                LogOnFireDTO dto = new LogOnFireDTO(pid, prevView);
                priorityQueue.offer(dto);
            }
        }

        return priorityQueue;
    }

}
