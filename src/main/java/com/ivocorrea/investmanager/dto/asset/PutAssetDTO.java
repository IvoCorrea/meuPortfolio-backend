package com.ivocorrea.investmanager.dto.asset;

import java.math.BigDecimal;

public record PutAssetDTO(Integer quantity, BigDecimal currentPrice) {
}
