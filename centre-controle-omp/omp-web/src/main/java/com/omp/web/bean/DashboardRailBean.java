package com.omp.web.bean;

import com.omp.rail.entity.RailwayRotation;
import com.omp.rail.service.RailwayRotationService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class DashboardRailBean implements Serializable {

    @Inject
    private RailwayRotationService railwayRotationService;

    private List<RailwayRotation> rotations;

    public void init() {
        rotations = railwayRotationService.findRecent(50);
    }

    public List<RailwayRotation> getRotations() {
        return rotations;
    }

    public long getRotationsEnCours() {
        return rotations == null ? 0 : rotations.stream()
                .filter(r -> r.getDepartureDatetime() == null)
                .count();
    }
}
