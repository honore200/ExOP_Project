package com.omp.api.resource;

import com.omp.api.security.JwtUtil;
import com.omp.common.dto.LoginRequest;
import com.omp.common.dto.LoginResponse;
import com.omp.common.dto.UserDTO;
import com.omp.common.entity.User;
import com.omp.common.service.UserService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    private UserService userService;

    @Inject
    private JwtUtil jwtUtil;

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        Optional<User> userOpt = userService.findByUsername(request.username());
        if (userOpt.isEmpty() || !userService.verifyPassword(userOpt.get(), request.password())) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Identifiants invalides")
                    .build();
        }

        User user = userOpt.get();
        Set<String> roles = userService.roleCodesOf(user);
        String token = jwtUtil.issueToken(user.getUsername(), roles);

        UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getFullName(),
                user.isEnabled(), List.copyOf(roles));

        return Response.ok(new LoginResponse(token, jwtUtil.expirationFor(token), userDTO, List.copyOf(roles)))
                .build();
    }
}
