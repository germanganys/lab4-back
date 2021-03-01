package com.example.lab4;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class PointBean {
    public void addPoint(Double x, Double y, Double r, String uname) {
        Point p = new Point(x, y, r, Point.calculate(x, y, r), uname);
        Database.pointEM.getTransaction().begin();
        Database.pointEM.persist(p);
        Database.pointEM.flush();
        Database.pointEM.getTransaction().commit();
    }

    public List<Point> getPoints() {
        return Database.pointEM.createQuery("select c from results4_table c").getResultList();
    }

    public void clear() {
        Database.pointEM.getTransaction().begin();
        Database.pointEM.createQuery("delete from results4_table").executeUpdate();
        Database.pointEM.flush();
        Database.pointEM.getTransaction().commit();
    }
}
