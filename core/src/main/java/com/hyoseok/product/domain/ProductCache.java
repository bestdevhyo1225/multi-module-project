package com.hyoseok.product.domain;

import com.hyoseok.product.usecase.dto.ProductImageDetailDto;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@RedisHash(value = "product", timeToLive = 60 * 60) // timeToLive는 seconds
public class ProductCache implements Serializable {

    @Id
    private String id;
    private Boolean isSale;
    private Boolean isUsed;
    private int supplierId;
    private double supplyPrice;
    private double recommendPrice;
    private double consumerPrice;
    private int maximum;
    private int minimum;
    private LocalDateTime refreshDatetime;
    private Map<String, String> productDescriptionText;
    private Map<String, String> productDescriptionVarchar;
    private List<ProductImageDetailDto> productImages;

    public static ProductCache create(String id,
                                      Boolean isSale,
                                      Boolean isUsed,
                                      int supplierId,
                                      double supplyPrice,
                                      double recommendPrice,
                                      double consumerPrice,
                                      int maximum,
                                      int minimum,
                                      Map<String, String> productDescriptionText,
                                      Map<String, String> productDescriptionVarchar,
                                      List<ProductImageDetailDto> productImages,
                                      LocalDateTime refreshDatetime) {
        ProductCache productCache = new ProductCache();

        productCache.id = id;
        productCache.isSale = isSale;
        productCache.isUsed = isUsed;
        productCache.supplierId = supplierId;
        productCache.supplyPrice = supplyPrice;
        productCache.recommendPrice = recommendPrice;
        productCache.consumerPrice = consumerPrice;
        productCache.maximum = maximum;
        productCache.minimum = minimum;
        productCache.refreshDatetime = refreshDatetime;
        productCache.productDescriptionText = productDescriptionText != null ? productDescriptionText : new HashMap<>();
        productCache.productDescriptionVarchar = productDescriptionVarchar != null ? productDescriptionVarchar : new HashMap<>();
        productCache.productImages = productImages != null ? productImages : new ArrayList<>();

        return productCache;
    }

    public void refresh(Boolean isSale,
                        Boolean isUsed,
                        int supplierId,
                        double supplyPrice,
                        double recommendPrice,
                        double consumerPrice,
                        int maximum,
                        int minimum,
                        ProductDescriptionText productDescriptionText,
                        List<ProductDescriptionVarchar> productDescriptionVarchars,
                        List<ProductImage> productImages,
                        LocalDateTime refreshDatetime) {
        if (refreshDatetime.isBefore(this.refreshDatetime)) return;

        this.isSale = isSale;
        this.isUsed = isUsed;
        this.supplierId = supplierId;
        this.supplyPrice = supplyPrice;
        this.recommendPrice = recommendPrice;
        this.consumerPrice = consumerPrice;
        this.maximum = maximum;
        this.minimum = minimum;
        this.refreshDatetime = refreshDatetime;

        this.productDescriptionText.clear();
        this.productDescriptionVarchar.clear();
        this.productImages.clear();

        if (productDescriptionText != null) {
            this.productDescriptionText.put(productDescriptionText.getKey(), productDescriptionText.getValue());
        }

        if (productDescriptionVarchars != null) {
            productDescriptionVarchars.forEach(descriptionVarchar ->
                    this.productDescriptionVarchar.put(descriptionVarchar.getKey(), descriptionVarchar.getValue())
            );
        }

        if (productImages != null) {
            productImages.forEach(productImage -> this.productImages.add(new ProductImageDetailDto(productImage)));
        }
    }

}
