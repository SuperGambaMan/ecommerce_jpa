package org.iesvdm.ecommerce_jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartProductDTO {
    private Long productId;
    private String name;
    private BigDecimal price;
    private Long quantity;
}
