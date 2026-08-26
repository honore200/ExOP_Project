package com.omp.web.bean;

import com.omp.port.entity.PortCall;
import com.omp.port.service.PortCallService;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class DashboardPortBean implements Serializable {

    @Inject
    private PortCallService portCallService;

    private List<PortCall> calls;
    private List<PortCall> callsAtBerth;

    public void init() {
        calls = portCallService.findRecent(50);
        callsAtBerth = portCallService.findAtBerth();
    }

    public List<PortCall> getCalls() {
        return calls;
    }

    public List<PortCall> getCallsAtBerth() {
        return callsAtBerth;
    }
}
