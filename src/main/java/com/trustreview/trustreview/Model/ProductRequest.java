package com.trustreview.trustreview.Model;

import com.trustreview.trustreview.Enums.ProductCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ProductRequest {
    private String name;
    private String brandName;
    private String sourceUrl;
}
