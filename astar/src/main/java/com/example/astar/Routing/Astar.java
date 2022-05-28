package com.example.astar.Routing;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class Astar {
    ArrayList<Node> nodesList;
    ArrayList<ArrayList<Edge>> adjList;

    public Astar() {
        nodesList = new ArrayList<>();
        nodesList.add(new Node(0, "init", 0, 0));
        adjList = new ArrayList<>();
        adjList.add(new ArrayList<>());
        getDataFromDB();
        //        for (int i = 1; i < adjList.size(); i++) {
//            for (int j = 0; j < adjList.get(i).size(); j++) {
//                System.out.print(adjList.get(i).get(j).start + " " + adjList.get(i).get(j).end+" ");
//            }
//            System.out.println();
//        }

    }


    public int getNearestNodeId(double lat, double lon, String target) {
        List<Node> targetNodeList = getDestinationsByName(target);
        int nearestNodeNum = 0;
        double minLength = Double.MAX_VALUE;
        for (int i = 1; i < targetNodeList.size(); i++) {
            double latGap = (targetNodeList.get(i).lat - lat);
            double lonGap = (targetNodeList.get(i).lon - lon);
            double tempLength = Math.sqrt(latGap * latGap + lonGap * lonGap);
            // 최소 갱신
            if (minLength > tempLength) {
                nearestNodeNum = i;
                minLength = tempLength;
            }
        }

        return nearestNodeNum;
    }

    public List<Node> getDestinationsByName(String target){

        List<Node> result = new ArrayList<>();

        for (int i = 1; i < nodesList.size(); i++) {
            if (nodesList.get(i).name.contains(target)) {
                result.add(nodesList.get(i));
            }
        }

        return result;
    }

    public List<String> edgeParser(String edge) {
        List<String> result = new ArrayList<>();

        return null;
    }

//    public List<Integer> aStar_weighByCoordinate(double s_lat, double s_lon, String target) {
//
//        // 목적지 선택 필요
//
//        return aStar_weigh(getNearestNode(s_lat, s_lon).id, 0);
//    }

    public JsonArray getNodeList_JsonArray(int s, int e){
        JsonArray jsonArray = new JsonArray();
        for (Node node : getNodeList_aStar_weigh(s, e)) {
            JsonObject nodeJson = new JsonObject();
            try {
                nodeJson.addProperty("id", node.id);
                nodeJson.addProperty("lat", node.lat);
                nodeJson.addProperty("lon", node.lon);
                nodeJson.addProperty("name", node.name);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            jsonArray.add(nodeJson);
        }
        return jsonArray;
    }

    public JsonArray getEdgeList_JsonArray(int s, int e){
        JsonArray jsonArray = new JsonArray();
        for (Edge edge : getEdgeList_aStar_weigh(s, e)) {
            JsonObject edgeJson = new JsonObject();
            try {
                edgeJson.addProperty("path", edge.path);
                edgeJson.addProperty("start", edge.start);
                edgeJson.addProperty("end", edge.end);
                edgeJson.addProperty("type", edge.pathType);
                edgeJson.addProperty("weigh", edge.weigh);
                edgeJson.addProperty("distance", edge.distance);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            jsonArray.add(edgeJson);
        }
        return jsonArray;
    }

    public List<Node> getNodeList_aStar_weigh(int s, int e){
        List<Node> nodeList = new ArrayList<>();

        for (Integer index : aStar_weigh(s, e)) {
            nodeList.add(nodesList.get(index));
        }

        return nodeList;
    }

    public List<Edge> getEdgeList_aStar_weigh(int s, int e){
        List<Edge> edgeList = new ArrayList<>();
        List<Integer> nList = aStar_weigh(s, e);

        if (nList.size() > 1) {
            for (int startIndex = 1; startIndex < nList.size(); startIndex++) {
                int startNode = nList.get(startIndex - 1);
                int endNode = nList.get(startIndex);

                for (int j = 0; j < adjList.get(startNode).size(); j++) {
                    if (adjList.get(startNode).get(j).end == endNode) {
                        edgeList.add(adjList.get(startNode).get(j));
                        break;
                    }
                }
            }
        }

        return edgeList;
    }

    public List<Integer> aStar_weigh(int s, int t){
        Node start = nodesList.get(s);
        Node target = nodesList.get(t);
        PriorityQueue<Node> closedList = new PriorityQueue<>();
        PriorityQueue<Node> openList = new PriorityQueue<>();

        start.f = start.g + start.calculateHeuristic(target);
        openList.add(start);

        // 갈수있는 곳이 더이상 없을 때까지
        while(!openList.isEmpty()){
            Node n = openList.peek();
            if(n == target){
                return getPath(target);
            }

            for(Edge edge : adjList.get(n.id)){
                Node m = nodesList.get(edge.end);
                double totalWeight = n.g + edge.weigh;
                //System.out.println(totalWeight + " " + m.g);
                // 아직 탐색해본적이 없으면
                if(!openList.contains(m) && !closedList.contains(m)){
                    m.parent = n;
                    m.g = totalWeight; //이전까지의 g + 이번 링크의 가중치
                    m.f = m.g + m.calculateHeuristic(target); // f= g+h
                    openList.add(m);
                } else {
                    // 탐색해본적 있으면서 최소값 갱신하는 경우
                    if(totalWeight < m.g){
                        m.parent = n;
                        m.g = totalWeight;
                        m.f = m.g + m.calculateHeuristic(target);

                        // 만약 닫힌 리스트에 있었다면 열린 리스트로 이동
                        if(closedList.contains(m)){
                            closedList.remove(m);
                            openList.add(m);
                        }
                    }
                }
            }

            openList.remove(n);
            closedList.add(n);
        }
        return null;
    }

    public List<Integer> aStar_distance(int s, int t){
        Node start = nodesList.get(s);
        Node target = nodesList.get(t);
        PriorityQueue<Node> closedList = new PriorityQueue<>();
        PriorityQueue<Node> openList = new PriorityQueue<>();

        start.f = start.g + start.calculateHeuristic(target);
        openList.add(start);

        // 갈수있는 곳이 더이상 없을 때까지
        while(!openList.isEmpty()){
            Node n = openList.peek();
            if(n == target){
                return getPath(target);
            }

            for(Edge edge : adjList.get(n.id)){
                Node m = nodesList.get(edge.end);
                double totalWeight = n.g + edge.distance;
                //System.out.println(totalWeight + " " + m.g);
                // 아직 탐색해본적이 없으면
                if(!openList.contains(m) && !closedList.contains(m)){
                    m.parent = n;
                    m.g = totalWeight;
                    m.f = m.g + m.calculateHeuristic(target);
                    openList.add(m);
                } else {
                    if(totalWeight < m.g){
                        m.parent = n;
                        m.g = totalWeight;
                        m.f = m.g + m.calculateHeuristic(target);

                        if(closedList.contains(m)){
                            closedList.remove(m);
                            openList.add(m);
                        }
                    }
                }
            }

            openList.remove(n);
            closedList.add(n);
        }
        return null;
    }

    public List<Integer> getPath(Node target){
        Node n = target;

        if(n==null)
            return null;

        List<Integer> ids = new ArrayList<>();

        while (n.parent != null) {
            ids.add(n.id);
            n = n.parent;
        }
        ids.add(n.id);
        Collections.reverse(ids);

        return ids;
    }

    public void printPath(List<Integer> ids) {
            StringBuffer result = new StringBuffer();
            for (int id : ids) {
            result.append(id + " ");
        }
        result.append("\n");

        result.append(nodesList.get(ids.get(0)).name);
        for (int i = 1; i < ids.size(); i++) {
            result.append(" => " + nodesList.get(ids.get(i)).name);
        }
        result.append("\n");

        System.out.println(result);
    }

    public String getPath(List<Integer> ids) {
        StringBuffer result = new StringBuffer();
        for (int id : ids) {
            result.append(id + " ");
        }
        result.append("\n");

        result.append(nodesList.get(ids.get(0)).name);
        for (int i = 1; i < ids.size(); i++) {
            result.append(" => " + nodesList.get(ids.get(i)).name);
        }
        result.append("\n");

        return result.toString();
    }


    // DB에서 노드, 링크 데이터 긁어오는 것
    public void getDataFromDB(){
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int index = 1;

        try {
            con = DBConnection.getConnection();

            StringBuffer sql = new StringBuffer();

            sql.append("select * from nodes");

            pstmt = con.prepareStatement(sql.toString());
            //insert, update, delete, select
            rs = pstmt.executeQuery();	//쿼리 실행
            while(rs.next()) { //데이터베이스형식과 java 형식이 다름.
                index = 1; 	//1부터 시작
                int id = rs.getInt(index++);

                Double lat = rs.getDouble(index++);
                Double lon = rs.getDouble(index++);
                String name = rs.getString(index++);

                //System.out.println(id + " " + lat + " " + lon + " " + name);
                nodesList.add(new Node(id, name, lat, lon));
                adjList.add(new ArrayList<>());
            }

            pstmt = con.prepareStatement("select * from links");
            //insert, update, delete, select
            rs = pstmt.executeQuery();	//쿼리 실행
            while(rs.next()) { //데이터베이스형식과 java 형식이 다름.
                index = 1; 	//1부터 시작
                int id = rs.getInt(index++);

                String path = rs.getString(index++);
                int start = rs.getInt(index++);
                int end = rs.getInt(index++);
                String type = rs.getString(index++);
                int weigh = rs.getInt(index++);
                int distance = rs.getInt(index++);
                //System.out.println(path + " " + type + " " + end + " " + weigh + " " + distance);
                adjList.get(start).add(new Edge(path, start, end, type, weigh, distance));
                adjList.get(end).add(new Edge(path, end, start, type, weigh, distance));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if(rs != null) rs.close();
                if(pstmt != null) pstmt.close();
                if(con != null) con.close();
            } catch (SQLException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }
        }
    }
}
class Node implements Comparable<Node>{
    // Id for readability of result purposes

    int id;
    Node parent;
    double f = 0; // f = g+h
    double g = 0;
    double h;
    String name;
    double lat, lon;

    public Node(int id, String name, double lat, double lon) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }


    // 두 좌표 거리 m로 구하기
    public int calculateHeuristic(Node target) {
        double x = (Math.cos(lat) * 6400 * 2 * 3.14 / 360) * Math.abs(lon - target.lon);
        double y = 111 * Math.abs(lat - target.lat);
        double d = Math.sqrt(x * x + y * y);
        return (int) (d * 1000);
    }

    @Override
    public int compareTo(Node o) {
        return Double.compare(this.f, o.f);
    }

}

class Edge{
    String path, pathType;
    int weigh, distance,start, end;

    public Edge(String path, int start, int end, String pathType, int weigh, int distance) {
        this.path = path;
        this.pathType = pathType;
        this.weigh = weigh;
        this.distance = distance;
        this.start = start;
        this.end = end;
    }
}
