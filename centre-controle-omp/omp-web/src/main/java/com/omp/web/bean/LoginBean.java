package com.omp.web.bean;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Named
@RequestScoped
public class LoginBean implements Serializable {

    @Inject
    private SecurityContext securityContext;

    @Inject
    private HttpServletRequest request;

    @Inject
    private HttpServletResponse response;

    private String username;
    private String password;

    public String login() {
        UsernamePasswordCredential credential = new UsernamePasswordCredential(username, password);
        AuthenticationStatus status = securityContext.authenticate(
                request, response, AuthenticationParameters.withParams().credential(credential));

        if (status == AuthenticationStatus.SUCCESS) {
            try {
                response.sendRedirect(request.getContextPath() + "/pages/dashboard-global.xhtml");
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            return null;
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Identifiants invalides", null));
        return null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
