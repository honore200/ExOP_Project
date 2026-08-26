package com.omp.common.service;

import com.omp.common.entity.Domain;
import com.omp.common.repository.DomainRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;

@Stateless
public class DomainService {

    @Inject
    private DomainRepository domainRepository;

    public List<Domain> findAll() {
        return domainRepository.findAll();
    }

    public Domain findByCode(String code) {
        return domainRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Domaine inconnu: " + code));
    }
}
