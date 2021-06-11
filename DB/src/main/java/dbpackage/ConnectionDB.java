package dbpackage;

import jakarta.persistence.*;


import java.util.List;


public class ConnectionDB {

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("PU");


    public static List<Person> getAllPersonsFromDb() {

        EntityManager em = emf.createEntityManager();

        TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p", Person.class);
        List<Person> list = query.getResultList();

        em.close();

        return list;
    }

    public static Person NameUpdateSender(int dbId, String updatedName) {
        EntityManager em = emf.createEntityManager();
        Person person = sendIdResponse(dbId);

        em.getTransaction().begin();
        person.setName(updatedName);
        em.merge(person);
        em.getTransaction().commit();
        em.close();

        return person;
    }

    public static Person sendIdResponse(int id) {
        EntityManager em = emf.createEntityManager();

        TypedQuery<Person> query = em.createQuery("SELECT q FROM Person q WHERE q.id=:id", Person.class);
        query.setParameter("id", id);

        Person person = query.getSingleResult();

        em.close();

        return person;
    }

}
