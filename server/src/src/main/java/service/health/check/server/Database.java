package service.health.check.server;

import org.hibernate.Session;
import service.health.check.models.Address;
import service.health.check.models.HibernateUtil;
import service.health.check.models.Server;
import service.health.check.models.Server_;
import service.health.check.server.sharding.HashFunction;
import service.health.check.server.sharding.HashRange;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class Database {
    private final EntityManager entityManager;

    public Database() {
        this.entityManager = HibernateUtil.getEntityManagerFactory()
                .createEntityManager();
    }

    public synchronized <T> List<T> getAll(Class<T> entityClass) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<T> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(root);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public synchronized List<String> getActiveServersIds(Instant maxServerAge) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<Server> root = query.from(Server.class);
        query.select(root.get(Server_.id));
        query.where(cb.greaterThanOrEqualTo(root.get(Server_.lastHeartbeat),
                maxServerAge));
        return entityManager.createQuery(query).getResultList();
    }

    public synchronized List<Address> getAddressesForHashRange(HashRange rng) {
        List<Address> allAddresses = getAll(Address.class);
        // TODO: this can be optimized on the DB level
        //   but we need to store the hash values as well
        return allAddresses.stream()
                .filter(address -> rng.contains(
                        HashFunction.getHashValue(address.getHost())))
                .collect(Collectors.toList());
    }

    public synchronized void saveServer(Server server) {
        Session session = (Session) entityManager.getDelegate();
        session.beginTransaction();
        session.saveOrUpdate(server);
        session.getTransaction().commit();
    }
}
