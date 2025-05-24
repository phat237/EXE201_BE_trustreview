package com.trustreview.trustreview.Model;

import com.trustreview.trustreview.Enums.AIAnalysisResultStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class AIResponse {
    private String status;
    private AIAnalysisResultStatus result;
    private String message;
}