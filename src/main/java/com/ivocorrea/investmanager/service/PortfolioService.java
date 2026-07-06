package com.ivocorrea.investmanager.service;

import com.ivocorrea.investmanager.dto.asset.AddAssetDTO;
import com.ivocorrea.investmanager.dto.asset.PutAssetDTO;
import com.ivocorrea.investmanager.dto.portfolio.PortfolioResponseDTO;
import com.ivocorrea.investmanager.entity.Asset;
import com.ivocorrea.investmanager.entity.Portfolio;
import com.ivocorrea.investmanager.entity.User;
import com.ivocorrea.investmanager.repository.AssetRepository;
import com.ivocorrea.investmanager.repository.PortfolioRepository;
import com.ivocorrea.investmanager.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;

    public PortfolioService(PortfolioRepository portfolioRepository, UserRepository userRepository, AssetRepository assetRepository) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
    }

    public PortfolioResponseDTO getPortfolioById(String portfolioId, UUID userid) {
        Portfolio portfolio = portfolioRepository.findById(UUID.fromString(portfolioId))
                .orElseThrow(() -> new RuntimeException("Portfolio Not Found"));

        if (!userid.equals(portfolio.getUser().getUserid())) throw new AccessDeniedException("Access Denied");

        return PortfolioResponseDTO.fromEntity(portfolio);
    }

    public List<PortfolioResponseDTO> getAllPortfolios(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return portfolioRepository.findAllByUser(user).stream()
                .map(PortfolioResponseDTO::fromEntity)
                .toList();
    }

    public PortfolioResponseDTO createPortfolio(UUID userId, String name) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Portfolio portfolioEntity = new Portfolio();
        portfolioEntity.setUser(user);
        portfolioEntity.setPortfolioName(name);
        portfolioEntity.setAssets(new ArrayList<>());
        portfolioEntity.setCreatedAt(Instant.now());

        return PortfolioResponseDTO.fromEntity(portfolioRepository.save(portfolioEntity));
    }

    public PortfolioResponseDTO addAssetToPortfolio(AddAssetDTO assetDTO, String portfolioId, UUID userid) {

        Portfolio portfolioToBePut = portfolioRepository.findById(UUID.fromString(portfolioId))
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        if (!portfolioToBePut.getUser().getUserid().equals(userid)) throw new AccessDeniedException("Access Denied");

        Asset newAsset = new Asset();
        newAsset.setTicker(assetDTO.ticker());
        newAsset.setType(assetDTO.type());
        newAsset.setQuantity(assetDTO.quantity());
        newAsset.setCurrentPrice(assetDTO.currentPrice());
        newAsset.setPurchasePrice(assetDTO.purchasePrice());
        newAsset.setPortfolio(portfolioToBePut);

        try {
            portfolioToBePut.addNewAsset(newAsset);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return PortfolioResponseDTO.fromEntity(portfolioRepository.save(portfolioToBePut));
    }

    public Asset updateAsset(PutAssetDTO putAssetDTO, String portfolioId, String assetId, UUID userid) {
        Asset assetToBeUpdated = assetRepository.findById(UUID.fromString(assetId))
                .orElseThrow(() -> new RuntimeException("Asset Not Found"));

        if (!assetToBeUpdated.getPortfolio().getPortfolioId().equals(UUID.fromString(portfolioId))) throw new IllegalArgumentException("Asset does not belong to the specified portfolio");
        if (!assetToBeUpdated.getPortfolio().getUser().getUserid().equals(userid)) throw new AccessDeniedException("Access Denied");

        try {
            assetToBeUpdated.setQuantity(putAssetDTO.quantity());
            assetToBeUpdated.setCurrentPrice(putAssetDTO.currentPrice());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Illegal Arguments");
        }

        return assetRepository.save(assetToBeUpdated);
    }

    public void deletePortfolio(String portfolioId, UUID userid) {
        Portfolio portfolio = portfolioRepository.findById(UUID.fromString(portfolioId))
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not Found"));

        if (!portfolio.getUser().getUserid().equals(userid)) throw new AccessDeniedException("Access Denied");

        portfolioRepository.deleteById(portfolio.getPortfolioId());
    }

    public void deleteAssetInPortfolio(String portfolioId, String assetId, UUID userid) {
        Portfolio portfolio = portfolioRepository.findById(UUID.fromString(portfolioId))
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not Found"));

          Asset asset = assetRepository.findById(UUID.fromString(assetId))
                .orElseThrow(() -> new EntityNotFoundException("Asset not found"));

        if (!asset.getPortfolio().getPortfolioId().equals(portfolio.getPortfolioId())) throw new IllegalArgumentException("Asset does not belong to the specified portfolio");
        if (!portfolio.getUser().getUserid().equals(userid)) throw new AccessDeniedException("Access Denied");

        assetRepository.deleteById(asset.getAssetId());
    }
}
