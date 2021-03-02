package com.example.lab4;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class PointBean {
    public Point addPoint(Double x, Double y, Double r, String uname) {
        Point p = new Point(x, y, r, Point.calculate(x, y, r), uname);
        Database.pointEM.getTransaction().begin();
        Database.pointEM.persist(p);
        Database.pointEM.flush();
        Database.pointEM.getTransaction().commit();
        return p;
    }

    public List<Point> getPoints(String username) {
        return Database.pointEM.createQuery(String.format("select c from results44_table c where c.username='%s'", username)).getResultList();
    }

    public void clear(String username) {
        Database.pointEM.getTransaction().begin();
        Database.pointEM.createQuery(String.format("delete from results44_table where username='%s'", username)).executeUpdate();
        Database.pointEM.flush();
        Database.pointEM.getTransaction().commit();
    }
}
