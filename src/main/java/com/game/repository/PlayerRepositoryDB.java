package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {
    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        Properties properties = new Properties();
//        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
//        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3305/rpg");

       properties.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
        properties.put(Environment.URL, "jdbc:mysql://localhost:3305/rpg");
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "root");
        properties.put(Environment.HBM2DDL_AUTO, "update");

        sessionFactory = new Configuration()
                .addAnnotatedClass(Player.class)
                .addProperties(properties)
                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        try (Session session = sessionFactory.openSession()) {
            NativeQuery<Player> query = session.createNativeQuery("SELECT * FROM rpg.player", Player.class);
            query.setFirstResult(pageSize * pageNumber);
            query.setMaxResults(pageSize);
            return query.list();
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving players", e);
    }
    }

    @Override
    public int getAllCount() {
        try (Session session = sessionFactory.openSession()) {
           Query<Long>query= session.createNamedQuery("player_allCount",Long.class);
           if(query.uniqueResult()!=null){
            return Math.toIntExact(query.uniqueResult());}
           else return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving player count", e);}
        }


    @Override
    public Player save(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction=session.beginTransaction();
            session.save(player);
            transaction.commit();
            return player;
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving player", e);
        }
    }

    @Override
    public Player update(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction=session.beginTransaction();
            session.update(player);
            transaction.commit();
            return player;
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error update player", e);
        }
    }

    @Override
    public Optional<Player> findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            Player player=session.find(Player.class,id);
        return Optional.ofNullable(player);}
    catch (Exception e) {
        e.printStackTrace();
        return Optional.empty(); }
    }

    @Override
    public void delete(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction=session.beginTransaction();
            session.remove(player);
            transaction.commit();
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error remove player", e);
        }

    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();

    }
}