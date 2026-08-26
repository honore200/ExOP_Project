package com.omp.rail.service;

import com.omp.common.entity.Client;
import com.omp.common.entity.ClientDetail;
import com.omp.common.entity.Domain;
import com.omp.common.entity.Operation;
import com.omp.common.entity.Status;
import com.omp.common.interceptor.Audited;
import com.omp.common.repository.ClientDetailRepository;
import com.omp.common.repository.ClientRepository;
import com.omp.common.repository.StatusRepository;
import com.omp.common.service.DomainService;
import com.omp.common.service.OperationService;
import com.omp.rail.entity.RailwayRotation;
import com.omp.rail.repository.RailwayRotationRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** Cree l'Operation (domaine RAILWAY, type TRAIN_ROTATION) et sa RailwayRotation dans la meme transaction. */
@Stateless
public class RailwayRotationService {

    private static final String DOMAIN_CODE = "RAILWAY";

    @Inject
    private RailwayRotationRepository railwayRotationRepository;

    @Inject
    private OperationService operationService;

    @Inject
    private DomainService domainService;

    @Inject
    private ClientRepository clientRepository;

    @Inject
    private ClientDetailRepository clientDetailRepository;

    @Inject
    private StatusRepository statusRepository;

    public Optional<RailwayRotation> findByOperationId(Long operationId) {
        return railwayRotationRepository.findByOperationId(operationId);
    }

    public List<RailwayRotation> findRecent(int limit) {
        return railwayRotationRepository.findRecent(limit);
    }

    @Audited(action = "CREATE", entityType = "RailwayRotation")
    public RailwayRotation create(String rotationNumber, Long clientId, Long clientDetailId,
                                   String trainArrivalNumber, String trainReturnNumber,
                                   String trainCodeGsez, String trainCodeArise,
                                   BigDecimal declaredTonnage, Integer wagonCount,
                                   LocalDateTime announcedDepartureDatetime) {
        Domain domain = domainService.findByCode(DOMAIN_CODE);

        Operation operation = operationService.create(
                "RAIL-" + rotationNumber,
                "TRAIN_ROTATION",
                domain,
                clientId,
                null,
                LocalDateTime.now(),
                "Rotation ferroviaire " + rotationNumber);

        RailwayRotation rotation = new RailwayRotation();
        rotation.setOperation(operation);
        rotation.setRotationNumber(rotationNumber);
        if (clientId != null) {
            Client client = clientRepository.findById(clientId).orElse(null);
            rotation.setClient(client);
        }
        if (clientDetailId != null) {
            ClientDetail detail = clientDetailRepository.findById(clientDetailId).orElse(null);
            rotation.setClientDetail(detail);
        }
        rotation.setTrainArrivalNumber(trainArrivalNumber);
        rotation.setTrainReturnNumber(trainReturnNumber);
        rotation.setTrainCodeGsez(trainCodeGsez);
        rotation.setTrainCodeArise(trainCodeArise);
        rotation.setDeclaredTonnage(declaredTonnage);
        rotation.setWagonCount(wagonCount);
        rotation.setAnnouncedDepartureDatetime(announcedDepartureDatetime);

        Status status = statusRepository.findByCategoryAndCode("RAILWAY_ROTATION", "ANNOUNCED").orElse(null);
        rotation.setStatus(status);

        return railwayRotationRepository.save(rotation);
    }

    @Audited(action = "UPDATE_ARRIVAL", entityType = "RailwayRotation")
    public RailwayRotation recordArrival(Long rotationId, LocalDateTime arrivalDatetime) {
        RailwayRotation rotation = railwayRotationRepository.findById(rotationId)
                .orElseThrow(() -> new IllegalArgumentException("RailwayRotation inconnue: " + rotationId));
        rotation.setArrivalDatetime(arrivalDatetime);
        statusRepository.findByCategoryAndCode("RAILWAY_ROTATION", "AT_YARD").ifPresent(rotation::setStatus);
        return railwayRotationRepository.update(rotation);
    }

    @Audited(action = "UPDATE_DEPARTURE", entityType = "RailwayRotation")
    public RailwayRotation recordDeparture(Long rotationId, LocalDateTime departureDatetime, BigDecimal actualTonnage) {
        RailwayRotation rotation = railwayRotationRepository.findById(rotationId)
                .orElseThrow(() -> new IllegalArgumentException("RailwayRotation inconnue: " + rotationId));
        rotation.setDepartureDatetime(departureDatetime);
        rotation.setTonnage(actualTonnage);
        statusRepository.findByCategoryAndCode("RAILWAY_ROTATION", "DEPARTED").ifPresent(rotation::setStatus);
        operationService.changeStatus(rotation.getOperation().getId(), "COMPLETED");
        return railwayRotationRepository.update(rotation);
    }
}
