package com.trustreview.trustreview.Model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class VoucherCreateRequest {
    private String codes;
    private String description;
    private Integer requiredPoint;
}
