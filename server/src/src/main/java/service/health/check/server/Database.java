package service.health.check.server;

import service.health.check.models.Address;
import service.health.check.models.HibernateUtil;
import service.health.check.models.Server;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class Database {
    private final EntityManager entityManager;

    public Database() {
        this.entityManager = HibernateUtil.getEntityManagerFactory()
                .createEntityManager();
    }

    public <T> List<T> getAll(Class<T> entityClass) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<T> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(root);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public void saveServer(Server server) {
        Server currentlyPersistedVersion = entityManager.find(Server.class, server.getId());

        EntityTransaction t = entityManager.getTransaction();
        t.begin();
        // sorry this is ugly, but I don't know how to do it better with Hibernate
        if (currentlyPersistedVersion == null) {
            entityManager.persist(server);
        } else {
            currentlyPersistedVersion.setLastHeartbeat(server.getLastHeartbeat());
            entityManager.persist(currentlyPersistedVersion);
        }
        t.commit();
    }
}
