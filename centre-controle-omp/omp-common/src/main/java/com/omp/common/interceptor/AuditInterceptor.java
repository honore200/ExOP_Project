package com.omp.common.interceptor;

import com.omp.common.entity.AuditLog;
import com.omp.common.entity.User;
import com.omp.common.repository.UserRepository;
import com.omp.common.security.CurrentUserContext;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.lang.reflect.Method;

/**
 * Alimente audit_log sur toute methode de service annotee @Audited (creation/modification/
 * suppression). Capture qui/quoi/quand ; ne tente pas de diff generique old/new (audit_log.old_value
 * /new_value restent nullables pour un usage manuel plus fin si besoin - pas de sur-ingenierie ici).
 */
@Audited
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class AuditInterceptor {

    @PersistenceContext(unitName = "ompPU")
    private EntityManager em;

    @Inject
    private CurrentUserContext currentUserContext;

    @Inject
    private UserRepository userRepository;

    @AroundInvoke
    public Object audit(InvocationContext ctx) throws Exception {
        Object result = ctx.proceed();

        Audited annotation = resolveAnnotation(ctx);
        String action = !annotation.action().isEmpty() ? annotation.action() : ctx.getMethod().getName();
        String entityType = !annotation.entityType().isEmpty()
                ? annotation.entityType()
                : (result != null ? result.getClass().getSimpleName() : ctx.getTarget().getClass().getSimpleName());

        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(extractId(result));

        String username = currentUserContext.getUsername();
        if (username != null) {
            userRepository.findByUsername(username).map(User::getId)
                    .ifPresent(userId -> log.setUser(em.getReference(User.class, userId)));
        }

        em.persist(log);
        return result;
    }

    private Audited resolveAnnotation(InvocationContext ctx) {
        Method method = ctx.getMethod();
        if (method.isAnnotationPresent(Audited.class)) {
            return method.getAnnotation(Audited.class);
        }
        return ctx.getTarget().getClass().getAnnotation(Audited.class);
    }

    private Long extractId(Object result) {
        if (result == null) {
            return null;
        }
        try {
            Method getId = result.getClass().getMethod("getId");
            Object id = getId.invoke(result);
            return id instanceof Long ? (Long) id : null;
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
}
