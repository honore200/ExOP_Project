package com.omp.rail.entity;

import com.omp.common.entity.Client;
import com.omp.common.entity.ClientDetail;
import com.omp.common.entity.Operation;
import com.omp.common.entity.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Specialisation Railway d'une Operation (1:0..1, cf plan section 1.2). Les trains sont traites
 * comme des identifiants/attributs de la rotation (pas de table "train" maitre), car une rotation
 * est une composition temporaire de wagons+locomotive, pas un actif persistant.
 */
@Entity
@Table(name = "railway_rotation")
public class RailwayRotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "operation_id", nullable = false, unique = true)
    private Operation operation;

    @Column(name = "rotation_number", nullable = false, length = 40)
    private String rotationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_detail_id")
    private ClientDetail clientDetail;

    @Column(name = "train_arrival_number", length = 30)
    private String trainArrivalNumber;

    @Column(name = "train_return_number", length = 30)
    private String trainReturnNumber;

    @Column(name = "train_code_gsez", length = 30)
    private String trainCodeGsez;

    @Column(name = "train_code_arise", length = 30)
    private String trainCodeArise;

    @Column(precision = 12, scale = 2)
    private java.math.BigDecimal tonnage;

    @Column(name = "declared_tonnage", precision = 12, scale = 2)
    private java.math.BigDecimal declaredTonnage;

    @Column(name = "wagon_count")
    private Integer wagonCount;

    @Column(name = "arrival_datetime")
    private LocalDateTime arrivalDatetime;

    @Column(name = "departure_datetime")
    private LocalDateTime departureDatetime;

    @Column(name = "announced_departure_datetime")
    private LocalDateTime announcedDepartureDatetime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private Status status;

    public Long getId() {
        return id;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getRotationNumber() {
        return rotationNumber;
    }

    public void setRotationNumber(String rotationNumber) {
        this.rotationNumber = rotationNumber;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ClientDetail getClientDetail() {
        return clientDetail;
    }

    public void setClientDetail(ClientDetail clientDetail) {
        this.clientDetail = clientDetail;
    }

    public String getTrainArrivalNumber() {
        return trainArrivalNumber;
    }

    public void setTrainArrivalNumber(String trainArrivalNumber) {
        this.trainArrivalNumber = trainArrivalNumber;
    }

    public String getTrainReturnNumber() {
        return trainReturnNumber;
    }

    public void setTrainReturnNumber(String trainReturnNumber) {
        this.trainReturnNumber = trainReturnNumber;
    }

    public String getTrainCodeGsez() {
        return trainCodeGsez;
    }

    public void setTrainCodeGsez(String trainCodeGsez) {
        this.trainCodeGsez = trainCodeGsez;
    }

    public String getTrainCodeArise() {
        return trainCodeArise;
    }

    public void setTrainCodeArise(String trainCodeArise) {
        this.trainCodeArise = trainCodeArise;
    }

    public java.math.BigDecimal getTonnage() {
        return tonnage;
    }

    public void setTonnage(java.math.BigDecimal tonnage) {
        this.tonnage = tonnage;
    }

    public java.math.BigDecimal getDeclaredTonnage() {
        return declaredTonnage;
    }

    public void setDeclaredTonnage(java.math.BigDecimal declaredTonnage) {
        this.declaredTonnage = declaredTonnage;
    }

    public Integer getWagonCount() {
        return wagonCount;
    }

    public void setWagonCount(Integer wagonCount) {
        this.wagonCount = wagonCount;
    }

    public LocalDateTime getArrivalDatetime() {
        return arrivalDatetime;
    }

    public void setArrivalDatetime(LocalDateTime arrivalDatetime) {
        this.arrivalDatetime = arrivalDatetime;
    }

    public LocalDateTime getDepartureDatetime() {
        return departureDatetime;
    }

    public void setDepartureDatetime(LocalDateTime departureDatetime) {
        this.departureDatetime = departureDatetime;
    }

    public LocalDateTime getAnnouncedDepartureDatetime() {
        return announcedDepartureDatetime;
    }

    public void setAnnouncedDepartureDatetime(LocalDateTime announcedDepartureDatetime) {
        this.announcedDepartureDatetime = announcedDepartureDatetime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
