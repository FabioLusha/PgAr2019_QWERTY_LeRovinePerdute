package it.unibs.fp.RovinePerdute;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileOutputStream;
import java.util.ArrayList;


public class RovinePerduteMain{


    public static void main(String[] args){
        //inizializzo lo streamWritrer
        XMLOutputFactory xof = null;
        XMLStreamWriter xw = null;
        String fileName = "routes.xml";
        try
        {
            xof = XMLOutputFactory.newInstance();
            xw = xof.createXMLStreamWriter(new FileOutputStream(fileName), "utf-8");
            xw.writeStartDocument("utf-8", "1.0");
        }
        catch (Exception e)
        {
            System.out.println("Errore nell'inizializzazione del writer:");
            System.out.println(e.getMessage());
        }
        //itero un oggetto Tree
        Tree myTree = new Tree();
        //ottengo l'array che contine le città del percorso col minor costo N.B. il percorso è al contrario, dal nodo finale a quello iniziale,
        //quindi nella visualizzazione si partirà di consequenza dalla posizione finale.
        ArrayList<Node> minAltitudePath = new ArrayList<>(myTree.minimizeAltitudeDiff());
        ArrayList<Node> bestPath = new ArrayList<>(myTree.bestPath());
        String teamGoWhereverYouWant = "Tonatiuh";
        String teamMinAltitude = "Metzili";
        double dist = myTree.getRoot().calculateDistance(myTree.getGoal());

        writeXML(xw, bestPath, teamGoWhereverYouWant);
        writeXML(xw, minAltitudePath, teamMinAltitude);

        try {
            xw.writeEndDocument();
            xw.flush();
            xw.close();
        }catch(Exception e){
            System.out.println("Errore nella scrittura");
            System.out.println(e.getMessage());
        }

    }

    public static long pathLength(ArrayList<Node> nodes){
        long cost = 0;
        for(int i = 1; i < nodes.size(); i++ ){
            cost += nodes.get(i-1).calculateDistance(nodes.get(i));
        }
        return cost;
    }

    public static void writeXML(XMLStreamWriter xw, ArrayList<Node> path, String teamName) {
        ArrayList<Node>[] paths = new ArrayList[3];
        paths[0] = path;


        try
        {
            //apertura output
            xw.writeStartElement("routes");
            //apertura persone

            xw.writeStartElement("route");
            xw.writeAttribute("team", teamName);
            xw.writeAttribute("cost", String.valueOf(pathLength(path)));
            xw.writeAttribute("cities", String.valueOf(path.size()));

            for(int j = path.size() - 1; j >= 0; j--)
            {
                xw.writeStartElement("city");
                xw.writeAttribute("id", String.valueOf(path.get(j).getId()));
                xw.writeAttribute("name", (path.get(j).getName()));
                xw.writeEndElement();
            }
            //chiusura route
            xw.writeEndElement();
        }
        catch (Exception e)
        {
            System.out.println("Errore nella scrittura");
            System.out.println(e.getMessage());
        }
    }

}
