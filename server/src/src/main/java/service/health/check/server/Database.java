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

    public List<Address> getAllAddresses() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Address> criteriaQuery = criteriaBuilder.createQuery(
                service.health.check.models.Address.class);
        Root<Address> root = criteriaQuery.from(
                service.health.check.models.Address.class);
        criteriaQuery.select(root);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<Server> getAllServers() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Server> criteriaQuery = criteriaBuilder.createQuery(
                service.health.check.models.Server.class);
        Root<Server> root = criteriaQuery.from(
                service.health.check.models.Server.class);
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
