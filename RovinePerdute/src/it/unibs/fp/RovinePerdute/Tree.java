package it.unibs.fp.RovinePerdute;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;
import java.util.Arrays;


public class Tree {
    public static final String MSG_TIME = "processing time: (ms) ";
    public static final int INITIAL_VALUE_OF_DISTANCE_ARRAY = -1;
    public static final String FILE_NAME = "PgAr_Map_2000.xml";

    private Node root;
    private Node goal;
    private ArrayList<Node> nodes;

    public Tree(){
        nodes = new ArrayList<>();
        readTree();
        this.root = nodes.get(0);
        this.goal = nodes.get(nodes.size()-1);
    }

    public void readTree(){
        XMLInputFactory xmli = null;
        XMLStreamReader xmlr = null;
        try {
            xmli = XMLInputFactory.newInstance();
            xmlr = xmli.createXMLStreamReader(Tree.class.getResourceAsStream(FILE_NAME));
        }
        catch (Exception e) {
            System.out.println("Errore nell'inizializzazione del reader:");
            System.out.println(e.getMessage());
        }
        try
        {
            String tag;
            Node node = new Node();
            while(xmlr.hasNext()) {
                switch(xmlr.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT:
                        //acquisico il nome identificativo del tag e lo contollo con uno switch con 2 casi: city o link
                        tag = xmlr.getLocalName();
                            switch(tag) {
                                case "city":
                                    //in caso l'elemento sia city acquisco i dati del nodo
                                    for (int i = 0; i < xmlr.getAttributeCount(); i++) {
                                        String attributeName = xmlr.getAttributeLocalName(i);
                                        switch (attributeName) {
                                            case "id":
                                                node.setId(Integer.parseInt(xmlr.getAttributeValue(i)));
                                                break;
                                            case "name":
                                                node.setName(xmlr.getAttributeValue(i));
                                                break;
                                            case "x":
                                                node.setX(Integer.parseInt(xmlr.getAttributeValue(i)));
                                                break;
                                            case "y":
                                                node.setY(Integer.parseInt(xmlr.getAttributeValue(i)));
                                                break;
                                            case "h":
                                                node.setH(Integer.parseInt(xmlr.getAttributeValue(i)));
                                                break;
                                        }
                                    }
                                break;
                                //aggiungo gli archi del nodo che lo collegano ai nodi adiacenti
                                case "link":
                                    if(xmlr.getAttributeLocalName(0).contentEquals("to"))
                                         node.addEdge(Integer.parseInt(xmlr.getAttributeValue(0)));
                                    break;

                                default:
                                    break;
                            }
                    break;
                    /*controllo se stiamo chiudendo il tag city, se affermativo significa che abbiamo acquisito tutti i dettgli per la città
                    quindi possiamo aggiungerla alla lista*/
                    case XMLStreamConstants.END_ELEMENT:
                        if(xmlr.getLocalName().contentEquals("city")) {
                            nodes.add(node);
                            node = new Node();
                        }
                    break;

                    default:
                        break;

                }

            xmlr.next();
            }
        }
        catch (Exception e){
            System.out.println("Errore nella lettura dei Nodi:");
            System.out.println(e.getMessage());
        }
    }

    public Node getRoot() {
        return root;
    }

    public Node getGoal() {
        return goal;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public String nodeToString(Node node){
        StringBuilder str =  new StringBuilder(node.toString());
        for(int edge : node.getEdgesTo()) {
            str.append("\n      collegato a: ");
            str.append(nodes.get(edge).toString());
        }

        return str.toString();
    }

    public String treeToString(){
        StringBuilder str =  new StringBuilder();
        for(Node node : nodes){
            str.append("\n" + nodeToString(node));
        }
        return  str.toString();
    }

    public ArrayList<Node> minimizeAltitudeDiff(){

        double start = System.currentTimeMillis();
        //array che contiene le differenze di altitudine tra i vari nodi nel percorso (più si va avanti nel percorso più aumenta la diff.
        //di altitudine (a meno che i nodi si trovino tutti sullo stesso piano))
        double[] altitudeDiff = new double[nodes.size()];
        //inizializzo l'array con un valore di comodo -1 significante infinta differenza
        Arrays.fill(altitudeDiff, INITIAL_VALUE_OF_DISTANCE_ARRAY);
        //array di nodi che tiene conto del nodo precdente a un altro nel percorso che si sta navigando
        Node[] previous = new Node[nodes.size()];
        //I nodi che non sono ancora stati visitati. All'inizio non lo è nessuno.
        ArrayList<Node> notVisitedNodes = new ArrayList<>(nodes);
        //inizializzo la differnza di alt. del nodo iniziale a 0
        altitudeDiff[root.getId()] = (long)0;

        //TAG che servirà per rompere il ciclo una volta arrivati al nodo finale
        ONE:
        //finchè ci sono nodi non visitati
        while(!notVisitedNodes.isEmpty()){
            //reperisco il nodo con la minore differenza di altitudine tra quelli non visitati
            Node currentNode = vertexWithMinDistance(altitudeDiff, notVisitedNodes);
            //rimuovo il nodo che sta per essere iterato dai nodi non visitati
            notVisitedNodes.remove(currentNode);
            //se il nodo che si sta visitando è RovinePerdute abbiamo trovato il passo meno costoso e terminiamo il ciclo
            if(currentNode.equals(goal))
                break ONE;
            //per ogni nodo adiacente a quello corrente (che si sta visitando)
            for (int i = 0; i < currentNode.getEdgesTo().size(); i++) {
                //reperisco il nodo
                Node adjOfCurrentNode = nodes.get(currentNode.getEdgesTo().get(i));
                //se è già stato iterato con questo ciclo vado al successivo
                if(!notVisitedNodes.contains(adjOfCurrentNode) )
                    continue;
                //calcolo la differnza di altitudine del nodo figlio con quello del nodo padre e aggiungo la differnza accumulata
                //dal nodo padre
                double dist = currentNode.calculateAltitudeDifference(adjOfCurrentNode) + altitudeDiff[currentNode.getId()] ;
                //se questa differenza è minore di una già calcolata precedentemente o è la prima volta che la si calcola la segno
                //e memorizzo anche il nodo precedente al nodo figlio che ha portato al passo col minor costo
                if(dist < altitudeDiff[adjOfCurrentNode.getId()] || (altitudeDiff[adjOfCurrentNode.getId()] == INITIAL_VALUE_OF_DISTANCE_ARRAY)){
                    altitudeDiff[adjOfCurrentNode.getId()] = dist;
                    previous[adjOfCurrentNode.getId()] = currentNode;
                }
            }

        }
        //quano ho trovato il nodo obiettivo a ritroso recupero i nodi che hanno portato al passo più corto
        ArrayList<Node> optimalPath = new ArrayList<>();
        Node tmp = goal;
        while(!tmp.equals(root) && tmp != null){
            optimalPath.add(tmp);
            tmp = previous[tmp.getId()];
        }
        optimalPath.add(root);
        double finish = System.currentTimeMillis();
        System.out.println("Dijkstra "+ MSG_TIME + (finish - start));
        //Efficiency = nodes on the optimal path / total nodes scanned
        System.out.println("Efficiency: " + (double)optimalPath.size()/(nodes.size() - (notVisitedNodes.size() - 1) ));
        return  optimalPath;
    }

    /*
    Questo metodo non è altro che l'algoritmo di Dijkstra che viene raffinato con una funione euritisca. La funzione euristica consiste nel
    dare un peso maggiore a quei nodi che si trovano più lonatno da quello finale così da poter iterare prima quelli più vicini. Però a causa
    della natura direzionale quasi casuale di questo grafo il miglioramento di efficenza e tempo è quasi nullo.
     */
    public ArrayList<Node> bestPath(){

        double start = System.currentTimeMillis();
        double[] distance = new double[nodes.size()];
        //array che contiene le distanze pesate dei nodi: distanza del percorso fino al nodo in questione + distanza euclidea dal nodo da raggiunger
        double[] weightedDistance = new double[nodes.size()];
        //inizializzazione degli array con valori iniziali di comodo
        Arrays.fill(distance, INITIAL_VALUE_OF_DISTANCE_ARRAY);
        Arrays.fill(weightedDistance, INITIAL_VALUE_OF_DISTANCE_ARRAY);
        //array per rintracciare il percorso
        Node[] previous = new Node[nodes.size()];
        ArrayList<Node> notVisitedNodes = new ArrayList<>(nodes);
        //inizializzazione a 0 della distanza del nodo di partenza
        distance[root.getId()] = 0L;
        weightedDistance[root.getId()] = 0L;
        double dist;
        double weightedDist;

        //TAG per la terminazione del ciclo una volta arrivata la condizione
        ONE:
        while(!notVisitedNodes.isEmpty()){
            //recupero del nodo con minor distanza percorsa tra quelli visitati
            Node currentNode = vertexWithMinDistance(weightedDistance, notVisitedNodes);
            notVisitedNodes.remove(currentNode);
            for (int i = 0; i < currentNode.getEdgesTo().size(); i++) {
                Node adjOfCurrentNode = nodes.get(currentNode.getEdgesTo().get(i));
                if(!notVisitedNodes.contains(adjOfCurrentNode) )
                    continue;
                 dist = currentNode.calculateDistance(adjOfCurrentNode) + distance[currentNode.getId()];
                 //la distanza pesate è data dalla somma della distanza del percorso + la distanza euclidea tra il nodo corrente e quello finale
                 weightedDist = dist + adjOfCurrentNode.calculateDistance(goal);
                if(weightedDist < weightedDistance[adjOfCurrentNode.getId()] || (weightedDistance[adjOfCurrentNode.getId()] == INITIAL_VALUE_OF_DISTANCE_ARRAY)){
                    distance[adjOfCurrentNode.getId()] = dist;
                    weightedDistance[adjOfCurrentNode.getId()] = weightedDist;
                    previous[adjOfCurrentNode.getId()] = currentNode;
                }
                if(adjOfCurrentNode.equals(goal))
                    break ONE;

            }
        }

        ArrayList<Node> optimalPath = new ArrayList<>();
        Node tmp = goal;
        while(!tmp.equals(root) && tmp != null){
            optimalPath.add(tmp);
            tmp = previous[tmp.getId()];
        }
        optimalPath.add(root);

        double finish = System.currentTimeMillis();
        System.out.println("A* " + MSG_TIME + (finish - start));
        //Efficiency = nodes on the optimal path / total scanned nodes
        System.out.println("Efficiency: " + (double)optimalPath.size()/(nodes.size() - (notVisitedNodes.size() - 1)));
        return  optimalPath;
    }

    public Node vertexWithMinDistance(double[] distance, ArrayList<Node> notVisitedNodes){
        Node nodeWithMinDist = nodes.get(0);
        //iniziallizo la minima distanza con valore infinto
        double minDistance = INITIAL_VALUE_OF_DISTANCE_ARRAY;
        for (int i = 0; i < distance.length ; i++){
            //se la distanza del nodo i == infinto passo al successivo
            if(distance[i] == INITIAL_VALUE_OF_DISTANCE_ARRAY)
                continue;
            //se la distanza di i è minore dell'attuale valore oppure il valore della minima distanza e il valore di comodo (infinto) e se il nodo non
            //è già stato visitato definisco una nuova minima distanza
            else if((distance[i] < minDistance || (minDistance - INITIAL_VALUE_OF_DISTANCE_ARRAY < 0.001)) && notVisitedNodes.contains(nodes.get(i))){
                minDistance = distance[i];
                nodeWithMinDist = nodes.get(i);
            }
        }
        return nodeWithMinDist;
    }

}

