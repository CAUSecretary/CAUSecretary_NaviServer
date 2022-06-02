package com.example.astar.Controller;

import com.example.astar.DTO.NodeDTO;
import com.example.astar.Routing.Astar;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@RestController
public class AstarController {
    @RequestMapping(value="/", method={ RequestMethod.GET, RequestMethod.POST })
    public String home( HttpServletRequest request ) throws Exception {
        JSONObject json = new JSONObject();


        Astar astar = new Astar();

        astar.aStar_weigh(38, 1);

        json.put("weigh routing", astar.getPath(astar.aStar_weigh(38, 1)));
        //json.put("distance routing", astar.getPath(astar.aStar_distance(47, 1)));

        return json.toString(4);
    }

    @RequestMapping(value="/test", method={ RequestMethod.GET, RequestMethod.POST })
    public String test( HttpServletRequest request ) throws Exception {


        return "hello";
    }


    @RequestMapping(value="/search/weigh", method={ RequestMethod.GET, RequestMethod.POST })
    public String getRoute_weigh(HttpServletRequest request ) throws Exception {

        JsonObject json = new JsonObject();
        String startLat = request.getParameter("startLat");
        String startLon = request.getParameter("startLon");
        String endPoint = request.getParameter("endPoint");
        System.out.println("길찾기 시작");
        System.out.println("startLat: " +startLat);
        System.out.println("startLon: " +startLon);
        System.out.println("endPoint: " + endPoint);

        double sLat = Double.parseDouble(startLat);
        double sLon = Double.parseDouble(startLon);

        Astar astar = new Astar();

        int startPointId = astar.getNearestNodeId_ByLatLon(sLat,sLon);
        int endPointId = astar.getNearestNodeId_InTargetName(sLat, sLon, endPoint);
        System.out.println("startPointId : " + startPointId + " endPointId : " + endPointId);
        System.out.println(astar.getPath(astar.aStar_weigh(startPointId, endPointId)));

        json.add("nodes", astar.getWeighNodeList_JsonArray(startPointId, endPointId));
        json.add("edges", astar.getWeighEdgeList_JsonArray(startPointId, endPointId));


        return json.toString();
    }

    @RequestMapping(value="/search/distance", method={ RequestMethod.GET, RequestMethod.POST })
    public String getRoute_distance(HttpServletRequest request ) throws Exception {

        JsonObject json = new JsonObject();
        String startLat = request.getParameter("startLat");
        String startLon = request.getParameter("startLon");
        String endPoint = request.getParameter("endPoint");

        double sLat = Double.parseDouble(startLat);
        double sLon = Double.parseDouble(startLon);
        Astar astar = new Astar();

        int startPointId = astar.getNearestNodeId_ByLatLon(sLat,sLon);
        int endPointId = astar.getNearestNodeId_InTargetName(sLat, sLon, endPoint);

        json.add("nodes", astar.getWeighNodeList_JsonArray(startPointId, endPointId));
        json.add("edges", astar.getWeighEdgeList_JsonArray(startPointId, endPointId));

        return json.toString();
    }
}
