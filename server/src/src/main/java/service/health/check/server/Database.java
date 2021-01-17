package service.health.check.server;

import service.health.check.models.Address;
import service.health.check.models.HibernateUtil;

import javax.persistence.EntityManager;
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
}
