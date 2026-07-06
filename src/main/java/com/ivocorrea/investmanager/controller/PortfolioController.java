package com.ivocorrea.investmanager.controller;

import com.ivocorrea.investmanager.dto.asset.AddAssetDTO;
import com.ivocorrea.investmanager.dto.asset.AssetResponseDTO;
import com.ivocorrea.investmanager.dto.asset.PutAssetDTO;
import com.ivocorrea.investmanager.dto.portfolio.CreatePortfolioDTO;
import com.ivocorrea.investmanager.dto.portfolio.PortfolioResponseDTO;
import com.ivocorrea.investmanager.entity.User;
import com.ivocorrea.investmanager.service.PortfolioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping
    private ResponseEntity<PortfolioResponseDTO> createPortfolio(
            @AuthenticationPrincipal User user,
            @RequestBody CreatePortfolioDTO portfolioDTO
    ) {
        PortfolioResponseDTO createdPortfolio = portfolioService.createPortfolio(user.getUserid(), portfolioDTO.name());
        return ResponseEntity.created(URI.create("/portfolio/" + createdPortfolio.id().toString())).body(createdPortfolio);
    }

    @GetMapping("/{portfolioId}")
    public ResponseEntity<PortfolioResponseDTO> getPortfolioById(
            @PathVariable String portfolioId, @AuthenticationPrincipal User user
    ) {
        PortfolioResponseDTO portfolio = portfolioService.getPortfolioById(portfolioId, user.getUserid());
        return ResponseEntity.ok(portfolio);
    }

    @GetMapping
    public ResponseEntity<List<PortfolioResponseDTO>> getAllPortfolios(
            @AuthenticationPrincipal User user
    ) {
        List<PortfolioResponseDTO> portfolioList = portfolioService.getAllPortfolios(user.getUserid());
        return ResponseEntity.ok(portfolioList);
    }

    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<Void> deletePortfolio(
            @PathVariable String portfolioId,
            @AuthenticationPrincipal User user
    ) {
        portfolioService.deletePortfolio(portfolioId, user.getUserid());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{portfolioId}/asset")
    public ResponseEntity<PortfolioResponseDTO> addAssetToPortfolio(
            @RequestBody AddAssetDTO addAssetDTO,
            @PathVariable String portfolioId,
            @AuthenticationPrincipal User user
    ) {
        PortfolioResponseDTO portfolio = portfolioService.addAssetToPortfolio(addAssetDTO, portfolioId, user.getUserid());
        return ResponseEntity.ok(portfolio);
    }

    @PatchMapping("/{portfolioId}/asset/{assetId}")
    public ResponseEntity<AssetResponseDTO> updateAsset(
            @RequestBody PutAssetDTO putAssetDTO,
            @PathVariable String portfolioId,
            @PathVariable String assetId,
            @AuthenticationPrincipal User user
    ) {
        AssetResponseDTO asset = AssetResponseDTO.fromEntity(portfolioService.updateAsset(putAssetDTO, portfolioId, assetId, user.getUserid()));
        return ResponseEntity.ok(asset);
    }

    @DeleteMapping("/{portfolioId}/asset/{assetId}")
    public ResponseEntity<Void> deleteAssetInPortfolio(
            @PathVariable String portfolioId,
            @PathVariable String assetId,
            @AuthenticationPrincipal User user
    ) {
        portfolioService.deleteAssetInPortfolio(portfolioId, assetId, user.getUserid());
        return ResponseEntity.noContent().build();
    }
}
