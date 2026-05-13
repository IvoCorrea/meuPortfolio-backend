package com.ivocorrea.investmanager.dto.asset;

import com.ivocorrea.investmanager.entity.Enum.AssetTypeEnum;

import java.math.BigDecimal;

public record AddAssetDTO(String ticker, AssetTypeEnum type, Integer quantity, BigDecimal currentPrice) {
}
