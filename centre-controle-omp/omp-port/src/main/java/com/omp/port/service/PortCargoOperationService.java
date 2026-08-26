package com.omp.port.service;

import com.omp.common.interceptor.Audited;
import com.omp.port.entity.CargoType;
import com.omp.port.entity.PortCall;
import com.omp.port.entity.PortCargoOperation;
import com.omp.port.enums.CargoDirection;
import com.omp.port.repository.CargoTypeRepository;
import com.omp.port.repository.PortCallRepository;
import com.omp.port.repository.PortCargoOperationRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class PortCargoOperationService {

    @Inject
    private PortCargoOperationRepository portCargoOperationRepository;

    @Inject
    private PortCallRepository portCallRepository;

    @Inject
    private CargoTypeRepository cargoTypeRepository;

    public List<PortCargoOperation> findByPortCallId(Long portCallId) {
        return portCargoOperationRepository.findByPortCallId(portCallId);
    }

    @Audited(action = "CREATE", entityType = "PortCargoOperation")
    public PortCargoOperation create(Long portCallId, String cargoTypeCode, CargoDirection direction,
                                      BigDecimal tonnage, Integer containerCount) {
        PortCall portCall = portCallRepository.findById(portCallId)
                .orElseThrow(() -> new IllegalArgumentException("PortCall inconnue: " + portCallId));
        CargoType cargoType = cargoTypeRepository.findByCode(cargoTypeCode)
                .orElseThrow(() -> new IllegalArgumentException("CargoType inconnu: " + cargoTypeCode));

        PortCargoOperation cargoOperation = new PortCargoOperation();
        cargoOperation.setPortCall(portCall);
        cargoOperation.setCargoType(cargoType);
        cargoOperation.setDirection(direction);
        cargoOperation.setTonnage(tonnage);
        cargoOperation.setContainerCount(containerCount);
        cargoOperation.setStartDatetime(LocalDateTime.now());

        return portCargoOperationRepository.save(cargoOperation);
    }

    @Audited(action = "COMPLETE", entityType = "PortCargoOperation")
    public PortCargoOperation complete(Long cargoOperationId) {
        PortCargoOperation cargoOperation = portCargoOperationRepository.findById(cargoOperationId)
                .orElseThrow(() -> new IllegalArgumentException("PortCargoOperation inconnue: " + cargoOperationId));
        cargoOperation.setEndDatetime(LocalDateTime.now());
        return portCargoOperationRepository.update(cargoOperation);
    }
}
