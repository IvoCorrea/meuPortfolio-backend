package com.ivocorrea.investmanager.dto.portfolio;

import com.ivocorrea.investmanager.dto.asset.AssetResponseDTO;
import com.ivocorrea.investmanager.entity.Portfolio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PortfolioResponseDTO(
        UUID id,
        String name,
        List<AssetResponseDTO> assets,
        BigDecimal totalInvested,
        BigDecimal totalValue,
        BigDecimal totalProfit,
        BigDecimal profitPercentage,
        Instant createdAt
) {
    public static PortfolioResponseDTO fromEntity(Portfolio portfolio) {

        List<AssetResponseDTO> assetDTOs = portfolio.getAssets()
                .stream()
                .map(AssetResponseDTO::fromEntity)
                .toList();

        BigDecimal totalInvested = portfolio.getAssets()
                .stream()
                .map(asset -> asset.getPurchasePrice()
                        .multiply(BigDecimal.valueOf(asset.getQuantity()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalValue = portfolio.getAssets()
                .stream()
                .map(asset -> asset.getCurrentPrice()
                        .multiply(BigDecimal.valueOf(asset.getQuantity()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal profitPercentage = BigDecimal.ZERO;
        BigDecimal totalProfit = totalValue.subtract(totalInvested);

        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {

            profitPercentage = totalProfit
                    .divide(totalInvested, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return new PortfolioResponseDTO(
                portfolio.getPortfolioId(),
                portfolio.getPortfolioName(),
                assetDTOs,
                totalInvested,
                totalValue,
                totalProfit,
                profitPercentage,
                portfolio.getCreatedAt()
        );
    }
}
