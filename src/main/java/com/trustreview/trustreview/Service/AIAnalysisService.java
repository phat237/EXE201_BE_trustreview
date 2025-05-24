package com.trustreview.trustreview.Service;

import com.trustreview.trustreview.Enums.AIAnalysisResultStatus;
import com.trustreview.trustreview.Model.AIResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AIAnalysisService {

    private final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions"; // 🔶

    @Value("${openai.api.key}")
    private String apiKey;

    public AIResponse analyzeText(Integer star, String content) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String prompt = String.format(
                "Dưới đây là một đánh giá người dùng:\n" +
                        "Số sao (do người dùng đánh giá về sản phẩm): %d\n" +
                        "Nội dung: \"%s\"\n\n" +
                        "Hãy xác định xem đánh giá này là hợp lệ (GOOD) hay không hợp lệ (SPAM), đồng thời chỉ định giá trị enum phân loại đánh giá. " +
                        "Trả về đúng một dòng theo format: GOOD - [ENUM] - [giải thích] hoặc SPAM - [ENUM] - [giải thích].\n\n" +
                        "Quy tắc phân loại như sau:\n" +
                        "1. Nếu nội dung thể hiện sự hài lòng, khen sản phẩm và số sao cao (4 hoặc 5) → GOOD - REAL_POSITIVE\n" +
                        "2. Nếu nội dung thể hiện sự không hài lòng, chê sản phẩm và số sao thấp (1 hoặc 2) → GOOD - REAL_NEGATIVE\n" +
                        "3. Nếu nội dung khen nhưng số sao thấp → SPAM - FAKE_POSITIVE\n" +
                        "4. Nếu nội dung chê nhưng số sao cao → SPAM - FAKE_NEGATIVE\n" +
                        "5. Nếu nội dung quá ngắn (dưới 10 từ hoặc dưới 40 ký tự) hoặc không rõ ràng → SPAM - SPAM\n" +
                        "6. Nếu số sao là 3, nội dung phải thể hiện rõ quan điểm trung lập → nếu rõ thì GOOD - NEUTRAL, nếu không thì SPAM - SPAM\n" +
                        "7. Nếu đánh giá có lời lẽ không phù hợp, xúc phạm, chửi thề → SPAM - SPAM\n" +
                        "8. Nếu không đủ dữ kiện để xác định → SPAM - INCONCLUSIVE\n\n" +
                        "Không viết thêm bất kỳ từ nào khác, không markdown, chỉ đúng một dòng trả lời bằng tiếng Việt.",
                star, content
        );


        Map<String, Object> systemMessage = Map.of(
                "role", "system",
                "content", "Bạn là một AI giúp phân tích đánh giá của người dùng về sản phẩm mà họ đã trải nghiệm."
        );

        Map<String, Object> userMessage = Map.of(
                "role", "user",
                "content", prompt
        );

        Map<String, Object> payload = Map.of(
                "model", "gpt-4-1106-preview", // 🔶 model của GPT-4.1 mini
                "messages", List.of(systemMessage, userMessage),
                "temperature", 0.3
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
//        try {
//            ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_API_URL, request, Map.class);
//            Map body = response.getBody();
//
//            if (body == null || body.get("choices") == null) {
//                return new AIResponse("ERROR", "Không có phản hồi từ GPT (choices=null)");
//            }
//
//            Map choices = (Map) ((List) body.get("choices")).get(0);
//            Map messageResponse = (Map) choices.get("message");
//            String fullResponse = (String) messageResponse.get("content");
//
//            String[] parts = fullResponse.split(" - ", 2);
//            if (parts.length == 2) {
//                return new AIResponse(parts[0].trim(), parts[1].trim());
//            } else {
//                return new AIResponse("UNKNOWN", fullResponse);
//            }
//        } catch (Exception e) {
//            return new AIResponse("ERROR", "Lỗi khi gọi OpenAI: " + e.getMessage());
//        }
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_API_URL, request, Map.class);
            Map body = response.getBody();

            if (body == null || body.get("choices") == null) {
                return new AIResponse("ERROR", null, "Không có phản hồi từ GPT (choices=null)");
            }

            Map choices = (Map) ((List) body.get("choices")).get(0);
            Map messageResponse = (Map) choices.get("message");
            String fullResponse = (String) messageResponse.get("content");

            // 👇 TÁCH THEO FORMAT: STATUS - ENUM - GIẢI THÍCH
            String[] parts = fullResponse.split(" - ", 3);
            if (parts.length == 3) {
                String status = parts[0].trim();
                String enumValue = parts[1].trim();
                String explanation = parts[2].trim();

                AIAnalysisResultStatus resultEnum;
                try {
                    resultEnum = AIAnalysisResultStatus.valueOf(enumValue);
                } catch (IllegalArgumentException e) {
                    resultEnum = null;
                }

                return new AIResponse(status, resultEnum, explanation);
            } else {
                return new AIResponse("UNKNOWN", null, fullResponse);
            }
        } catch (Exception e) {
            return new AIResponse("ERROR", null, "Lỗi khi gọi OpenAI: " + e.getMessage());
        }

    }

}
