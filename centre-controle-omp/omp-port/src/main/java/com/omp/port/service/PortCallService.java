package com.omp.port.service;

import com.omp.common.entity.Asset;
import com.omp.common.entity.AssetType;
import com.omp.common.entity.Domain;
import com.omp.common.entity.Location;
import com.omp.common.entity.Operation;
import com.omp.common.interceptor.Audited;
import com.omp.common.repository.AssetRepository;
import com.omp.common.repository.AssetTypeRepository;
import com.omp.common.repository.LocationRepository;
import com.omp.common.repository.StatusRepository;
import com.omp.common.service.DomainService;
import com.omp.common.service.OperationService;
import com.omp.port.entity.PortCall;
import com.omp.port.entity.VesselDetail;
import com.omp.port.repository.PortCallRepository;
import com.omp.port.repository.VesselDetailRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Cree l'Operation (domaine PORT) et sa PortCall dans la meme transaction ; le navire est resolu
 * ou cree comme Asset(asset_type=VESSEL) + VesselDetail (cf plan section 1.3).
 */
@Stateless
public class PortCallService {

    private static final String DOMAIN_CODE = "PORT";
    private static final String VESSEL_ASSET_TYPE = "VESSEL";

    @Inject
    private PortCallRepository portCallRepository;

    @Inject
    private VesselDetailRepository vesselDetailRepository;

    @Inject
    private AssetRepository assetRepository;

    @Inject
    private AssetTypeRepository assetTypeRepository;

    @Inject
    private LocationRepository locationRepository;

    @Inject
    private StatusRepository statusRepository;

    @Inject
    private OperationService operationService;

    @Inject
    private DomainService domainService;

    public Optional<PortCall> findByOperationId(Long operationId) {
        return portCallRepository.findByOperationId(operationId);
    }

    public List<PortCall> findRecent(int limit) {
        return portCallRepository.findRecent(limit);
    }

    public List<PortCall> findAtBerth() {
        return portCallRepository.findAtBerth();
    }

    @Audited(action = "CREATE", entityType = "PortCall")
    public PortCall create(String operationTypeCode, String imoNumber, String vesselName, String flag,
                            String vesselType, BigDecimal lengthM, BigDecimal grossTonnage,
                            Long quayLocationId, LocalDateTime etaDatetime, BigDecimal declaredTonnage) {
        Domain domain = domainService.findByCode(DOMAIN_CODE);
        Asset vessel = resolveOrCreateVessel(imoNumber, vesselName, flag, vesselType, lengthM, grossTonnage);

        Operation operation = operationService.create(
                "PORT-" + imoNumber + "-" + System.currentTimeMillis(),
                operationTypeCode,
                domain,
                null,
                quayLocationId,
                etaDatetime != null ? etaDatetime : LocalDateTime.now(),
                "Escale " + vesselName);

        PortCall portCall = new PortCall();
        portCall.setOperation(operation);
        portCall.setVessel(vessel);
        if (quayLocationId != null) {
            Location quay = locationRepository.findById(quayLocationId).orElse(null);
            portCall.setQuay(quay);
        }
        portCall.setEtaDatetime(etaDatetime);
        portCall.setDeclaredTonnage(declaredTonnage);
        statusRepository.findByCategoryAndCode("PORT_CALL", "ANNOUNCED").ifPresent(portCall::setStatus);

        return portCallRepository.save(portCall);
    }

    private Asset resolveOrCreateVessel(String imoNumber, String vesselName, String flag, String vesselType,
                                         BigDecimal lengthM, BigDecimal grossTonnage) {
        Optional<VesselDetail> existing = vesselDetailRepository.findByImoNumber(imoNumber);
        if (existing.isPresent()) {
            return existing.get().getAsset();
        }

        AssetType assetType = assetTypeRepository.findByCode(VESSEL_ASSET_TYPE)
                .orElseThrow(() -> new IllegalStateException("AssetType VESSEL absent du referentiel"));

        Asset asset = new Asset();
        asset.setAssetCode("IMO-" + imoNumber);
        asset.setAssetName(vesselName);
        asset.setAssetType(assetType);
        assetRepository.save(asset);

        VesselDetail detail = new VesselDetail();
        detail.setAsset(asset);
        detail.setImoNumber(imoNumber);
        detail.setFlag(flag);
        detail.setVesselType(vesselType);
        detail.setLengthM(lengthM);
        detail.setGrossTonnage(grossTonnage);
        vesselDetailRepository.save(detail);

        return asset;
    }

    @Audited(action = "RECORD_BERTHING", entityType = "PortCall")
    public PortCall recordBerthing(Long portCallId) {
        PortCall portCall = portCallRepository.findById(portCallId)
                .orElseThrow(() -> new IllegalArgumentException("PortCall inconnue: " + portCallId));
        portCall.setAtaDatetime(LocalDateTime.now());
        statusRepository.findByCategoryAndCode("PORT_CALL", "AT_BERTH").ifPresent(portCall::setStatus);
        return portCallRepository.update(portCall);
    }

    @Audited(action = "RECORD_DEPARTURE", entityType = "PortCall")
    public PortCall recordDeparture(Long portCallId) {
        PortCall portCall = portCallRepository.findById(portCallId)
                .orElseThrow(() -> new IllegalArgumentException("PortCall inconnue: " + portCallId));
        portCall.setAtdDatetime(LocalDateTime.now());
        statusRepository.findByCategoryAndCode("PORT_CALL", "DEPARTED").ifPresent(portCall::setStatus);
        operationService.changeStatus(portCall.getOperation().getId(), "COMPLETED");
        return portCallRepository.update(portCall);
    }
}
