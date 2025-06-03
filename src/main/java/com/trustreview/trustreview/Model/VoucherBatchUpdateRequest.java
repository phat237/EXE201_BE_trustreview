package com.trustreview.trustreview.Model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter

public class VoucherBatchUpdateRequest {
    private String batchCode;
    private String description;
    private Integer requiredPoint;
    private boolean isActive;
}
