package com.omp.port.service;

import com.omp.common.entity.Asset;
import com.omp.common.entity.AssetType;
import com.omp.common.entity.Domain;
import com.omp.common.entity.Operation;
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
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortCallServiceTest {

    @Mock
    private PortCallRepository portCallRepository;
    @Mock
    private VesselDetailRepository vesselDetailRepository;
    @Mock
    private AssetRepository assetRepository;
    @Mock
    private AssetTypeRepository assetTypeRepository;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private StatusRepository statusRepository;
    @Mock
    private OperationService operationService;
    @Mock
    private DomainService domainService;

    @InjectMocks
    private PortCallService portCallService;

    @Test
    void create_shouldReuseExistingVesselByImo() {
        Domain domain = new Domain();
        domain.setCode("PORT");
        Asset vesselAsset = new Asset();
        vesselAsset.setAssetCode("IMO-1234567");
        VesselDetail existingDetail = new VesselDetail();
        existingDetail.setAsset(vesselAsset);
        existingDetail.setImoNumber("1234567");

        Operation operation = new Operation();
        operation.setOperationCode("PORT-1234567-1");

        when(domainService.findByCode("PORT")).thenReturn(domain);
        when(vesselDetailRepository.findByImoNumber("1234567")).thenReturn(Optional.of(existingDetail));
        when(operationService.create(anyString(), anyString(), any(), any(), any(), any(), anyString()))
                .thenReturn(operation);
        when(portCallRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PortCall result = portCallService.create("SHIP_CALL_UNLOADING", "1234567", "MV Test", "Panama",
                "vraquier", null, null, null, LocalDateTime.now(), null);

        assertEquals(vesselAsset, result.getVessel());
        verify(assetRepository, never()).save(any());
    }

    @Test
    void create_shouldCreateNewVesselAssetWhenImoUnknown() {
        Domain domain = new Domain();
        domain.setCode("PORT");
        AssetType vesselType = new AssetType();
        vesselType.setCode("VESSEL");
        Operation operation = new Operation();

        when(domainService.findByCode("PORT")).thenReturn(domain);
        when(vesselDetailRepository.findByImoNumber("7654321")).thenReturn(Optional.empty());
        when(assetTypeRepository.findByCode("VESSEL")).thenReturn(Optional.of(vesselType));
        when(operationService.create(anyString(), anyString(), any(), any(), any(), any(), anyString()))
                .thenReturn(operation);
        when(portCallRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        portCallService.create("SHIP_CALL_LOADING", "7654321", "MV New", "Liberia",
                "porte-conteneurs", null, null, null, LocalDateTime.now(), null);

        verify(assetRepository).save(any(Asset.class));
        verify(vesselDetailRepository).save(any(VesselDetail.class));
    }
}
