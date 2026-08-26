package com.omp.common.service;

import com.omp.common.entity.Resource;
import com.omp.common.interceptor.Audited;
import com.omp.common.repository.ResourceRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;

@Stateless
public class ResourceService {

    @Inject
    private ResourceRepository resourceRepository;

    public List<Resource> findAll() {
        return resourceRepository.findAll();
    }

    @Audited(action = "CREATE", entityType = "Resource")
    public Resource create(Resource resource) {
        return resourceRepository.save(resource);
    }
}
