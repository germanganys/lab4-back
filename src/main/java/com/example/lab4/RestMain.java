package com.example.lab4;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;

@Stateless
@Path("/points")
public class RestMain {
    @EJB
    private PointBean pointBean;

    @EJB
    private UserBean userBean;

    @POST
    @Path("clear")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String clear(String s) {
        Gson gson = new Gson();
        ResponseStructure resp = new ResponseStructure();
        JsonElement root = new JsonParser().parse(s);
        String key = root.getAsJsonObject().get("key").getAsString();
        String username = userBean.isValidUser(key);
        if (username != null) {
            try {
                pointBean.clear(username);
                resp.status = "ok";
            } catch (Exception e) {
                resp.status = "failed";
            }
        } else {
            resp.status = "failed";
        }
        return gson.toJson(resp, ResponseStructure.class);
    }

    @POST
    @Path("get")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getPoints(String s) {
        Gson gson = new Gson();
        ResponseStructure resp = new ResponseStructure();

        JsonElement root = new JsonParser().parse(s);
        String key = root.getAsJsonObject().get("key").getAsString();
        String username = userBean.isValidUser(key);

        if (username != null) {
            try {
                resp.data = pointBean.getPoints(username);
            } catch (Exception e) {
                resp.data = new ArrayList<>();
            }
        } else {
            resp.status = "failed";
        }
        return gson.toJson(resp, ResponseStructure.class);
    }

    @POST
    @Path("add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addPoint(String s) {
        Gson gson = new Gson();
        ResponseStructure resp = new ResponseStructure();
        resp.status = "failed";

        JsonElement root = new JsonParser().parse(s);
        String key = root.getAsJsonObject().get("key").getAsString();
        String username = userBean.isValidUser(key);

        if (username != null) {
            try {
                String x = root.getAsJsonObject().get("x").getAsString();
                String y = root.getAsJsonObject().get("y").getAsString();
                String r = root.getAsJsonObject().get("r").getAsString();

                if (!Arrays.asList(1., 2., 3., 4.).contains(Double.parseDouble(r))) {
                    throw new Exception("Invalid R value");
                }

                resp.last_point = pointBean.addPoint(
                        Double.parseDouble(x),
                        Double.parseDouble(y),
                        Double.parseDouble(r),
                        username
                );
                resp.status = "ok";
            } catch (Exception e) {
                resp.status = "failed";
            }
        } else {
            resp.status = "failed";
        }
        return gson.toJson(resp, ResponseStructure.class);
    }
}
