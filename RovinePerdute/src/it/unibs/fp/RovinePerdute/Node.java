package it.unibs.fp.RovinePerdute;

import java.util.LinkedList;

public class Node {

    private int id;
    private String name;
    private long x;
    private long y;
    private long h;
    private LinkedList<Integer> edgesTo = new LinkedList<>();

    public Node(int id, String name, int x, int y, int h ){
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.h = h;
    }

    public Node(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public long getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public long getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public LinkedList<Integer> getEdgesTo() {
        return edgesTo;
    }

    public void setEdgesTo(LinkedList<Integer> edgesTo) {
        this.edgesTo = edgesTo;
    }

    public void addEdge(int edge) {
        this.edgesTo.add(edge);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString(){
        return "ID: " + id + " nome città : " + name + " ( " + x + " , " + y + " , " + h + " ) ";
    }


    //Metodo che calcola la distanza euclidea tra 2 punti in 2 dimensioone
    public double calculateDistance(Node theOtherNode){
        double coordinateX = this.getX() - theOtherNode.getX();
        double coordinateY = this.getY() - theOtherNode.getY();
        return Math.sqrt( Math.pow(coordinateX, 2.0) + Math.pow(coordinateY, 2.0) );
    }

    public long calculateAltitudeDifference(Node theOtherNode){
        return Math.abs(this.getH() - theOtherNode.getH());
    }

}
