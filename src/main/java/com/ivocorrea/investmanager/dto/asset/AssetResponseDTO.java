package com.ivocorrea.investmanager.dto.asset;

import com.ivocorrea.investmanager.entity.Asset;
import com.ivocorrea.investmanager.entity.Enum.AssetTypeEnum;

import java.math.BigDecimal;
import java.util.UUID;

public record AssetResponseDTO(
        UUID id,
        String ticker,
        AssetTypeEnum type,
        Integer quantity,
        BigDecimal purchasePrice,
        BigDecimal currentPrice,
        BigDecimal totalValue
) {
    public static AssetResponseDTO fromEntity(Asset asset) {
        BigDecimal totalValue = asset.getCurrentPrice()
                .multiply(BigDecimal.valueOf(asset.getQuantity()));

        return new AssetResponseDTO(
                asset.getAssetId(),
                asset.getTicker(),
                asset.getType(),
                asset.getQuantity(),
                asset.getPurchasePrice(),
                asset.getCurrentPrice(),
                totalValue
        );
    }
}
