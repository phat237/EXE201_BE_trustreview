package com.trustreview.trustreview.Service;

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
                        "Hãy xác định xem đánh giá này là hợp lệ (GOOD) hay không hợp lệ (SPAM) theo các quy tắc sau:\n" +
                        "1. Nếu nội dung thể hiện sự hài lòng, khen sản phẩm thì số sao phải cao (4 hoặc 5).\n" +
                        "2. Nếu nội dung thể hiện sự không hài lòng, chê sản phẩm thì số sao phải thấp (1 hoặc 2).\n" +
                        "3. Nếu nội dung và số sao mâu thuẫn thì đánh giá là SPAM.\n" +
                        "4. Nội dung phải rõ ràng, có lý do cụ thể, dài ít nhất 10 từ hoặc 40 ký tự. Nếu quá ngắn, xem là SPAM.\n" +
                        "5. Nếu số sao là 3 (trung tính), nội dung phải có lập luận trung tính rõ ràng, nếu không đủ thì xem là SPAM.\n" +
                        "6. Nếu đánh giá chứa ngôn từ không phù hợp, chửi thề, xúc phạm thì xem là SPAM ngay lập tức.\n" +
                        "Chỉ trả về đúng một dòng duy nhất theo format: SPAM - [giải thích] hoặc GOOD - [giải thích]. " +
                        "Không viết thêm bất kỳ từ nào khác, không sử dụng markdown, không in đậm hay in nghiêng. " +
                        "Vui lòng trả lời bằng tiếng Việt.",
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
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_API_URL, request, Map.class);
            Map body = response.getBody();

            if (body == null || body.get("choices") == null) {
                return new AIResponse("ERROR", "Không có phản hồi từ GPT (choices=null)");
            }

            Map choices = (Map) ((List) body.get("choices")).get(0);
            Map messageResponse = (Map) choices.get("message");
            String fullResponse = (String) messageResponse.get("content");

            String[] parts = fullResponse.split(" - ", 2);
            if (parts.length == 2) {
                return new AIResponse(parts[0].trim(), parts[1].trim());
            } else {
                return new AIResponse("UNKNOWN", fullResponse);
            }
        } catch (Exception e) {
            return new AIResponse("ERROR", "Lỗi khi gọi OpenAI: " + e.getMessage());
        }
    }

}
