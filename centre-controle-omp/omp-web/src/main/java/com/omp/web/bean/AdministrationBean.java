package com.omp.web.bean;

import com.omp.common.entity.Location;
import com.omp.common.entity.Role;
import com.omp.common.entity.User;
import com.omp.common.enums.LocationType;
import com.omp.common.repository.RoleRepository;
import com.omp.common.service.LocationService;
import com.omp.common.service.UserService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Administration : utilisateurs/roles/localisations. Phase 1 - le journal d'audit et les
 * referentiels operation_type/step_type/... rejoignent cette page en Phase 2+ (entites pas
 * encore modelisees a ce stade).
 */
@Named
@ViewScoped
public class AdministrationBean implements Serializable {

    @Inject
    private UserService userService;

    @Inject
    private LocationService locationService;

    @Inject
    private RoleRepository roleRepository;

    private List<User> users;
    private List<Location> locations;
    private List<Role> roles;

    private String newUsername;
    private String newFullName;
    private String newPassword;
    private Set<String> newUserRoleCodes = new HashSet<>();

    private String newLocationCode;
    private String newLocationName;
    private LocationType newLocationType;

    public void init() {
        users = userService.findAll();
        locations = locationService.findAll();
        roles = roleRepository.findAll();
    }

    public void createUser() {
        userService.createUser(newUsername, newFullName, newPassword, newUserRoleCodes);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Utilisateur cree", newUsername));
        newUsername = null;
        newFullName = null;
        newPassword = null;
        newUserRoleCodes = new HashSet<>();
        init();
    }

    public void createLocation() {
        Location location = new Location();
        location.setCode(newLocationCode);
        location.setName(newLocationName);
        location.setLocationType(newLocationType);
        locationService.create(location);
        newLocationCode = null;
        newLocationName = null;
        newLocationType = null;
        init();
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public LocationType[] getLocationTypes() {
        return LocationType.values();
    }

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }

    public String getNewFullName() {
        return newFullName;
    }

    public void setNewFullName(String newFullName) {
        this.newFullName = newFullName;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public Set<String> getNewUserRoleCodes() {
        return newUserRoleCodes;
    }

    public void setNewUserRoleCodes(Set<String> newUserRoleCodes) {
        this.newUserRoleCodes = newUserRoleCodes;
    }

    public String getNewLocationCode() {
        return newLocationCode;
    }

    public void setNewLocationCode(String newLocationCode) {
        this.newLocationCode = newLocationCode;
    }

    public String getNewLocationName() {
        return newLocationName;
    }

    public void setNewLocationName(String newLocationName) {
        this.newLocationName = newLocationName;
    }

    public LocationType getNewLocationType() {
        return newLocationType;
    }

    public void setNewLocationType(LocationType newLocationType) {
        this.newLocationType = newLocationType;
    }
}
