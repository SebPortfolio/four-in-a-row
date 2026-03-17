package de.paulm.four_in_a_row.service;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.paulm.four_in_a_row.domain.security.CustomRevisionEntity;
import de.paulm.four_in_a_row.domain.security.User;
import de.paulm.four_in_a_row.web.dtos.Audit;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public <T> List<Audit<T>> getHistory(Class<T> entityClass, Object id) {
        AuditReader reader = AuditReaderFactory.get(entityManager);

        List<?> results = reader.createQuery()
                .forRevisionsOfEntity(entityClass, false, true)
                .add(AuditEntity.id().eq(id))
                .addOrder(AuditEntity.revisionNumber().desc())
                .getResultList();

        return results.stream().map(result -> {
            Object[] row = (Object[]) result;
            T entity = entityClass.cast(row[0]);

            if (entity instanceof User user) {
                Hibernate.initialize(user.getRoles());
                Hibernate.initialize(user.getCustomPermissions());
                Hibernate.initialize(user.getBans());
            }

            CustomRevisionEntity rev = (CustomRevisionEntity) row[1];
            RevisionType type = (RevisionType) row[2];

            return Audit.<T>builder()
                    .revisionId(rev.getId())
                    .timestamp(rev.getRevisionDateTime())
                    .modifierUserId(rev.getModifierUserId())
                    .revisionType(type.name())
                    .entity(entity)
                    .build();
        }).toList();
    }
}
