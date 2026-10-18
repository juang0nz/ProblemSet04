package ucu.edu.aed.Ejerccios.ejercicio_5;

import ucu.edu.aed.tda.grafo.impl.DirectedGraph;
import ucu.edu.aed.tda.grafo.impl.DirectedGraphAlgorithms;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
import ucu.edu.aed.tda.grafo.model.result.Path;

import java.util.List;

public class Ejercicio5 {

    public static void main(String[] args) {
        DirectedGraph<String, WeightedEdge> grafo = new DirectedGraph<>();

        grafo.agregarVertice("Asuncion");
        grafo.agregarVertice("Buenos Aires");
        grafo.agregarVertice("Curitiba");
        grafo.agregarVertice("Montevideo");
        grafo.agregarVertice("Porto Alegre");
        grafo.agregarVertice("Rio de Janeiro");

        grafo.agregarArista("Asuncion", "Buenos Aires", new WeightedEdge(1600));
        grafo.agregarArista("Asuncion", "Curitiba", new WeightedEdge(800));
        grafo.agregarArista("Asuncion", "Porto Alegre", new WeightedEdge(700));
        grafo.agregarArista("Buenos Aires", "Asuncion", new WeightedEdge(1600));
        grafo.agregarArista("Buenos Aires", "Montevideo", new WeightedEdge(200));
        grafo.agregarArista("Buenos Aires", "Porto Alegre", new WeightedEdge(1250));
        grafo.agregarArista("Critiba", "Rio de Janeiro", new WeightedEdge(1500));
        grafo.agregarArista("Montevideo", "Buenos Aires", new WeightedEdge(200));
        grafo.agregarArista("Montevideo", "Porto Alegre", new WeightedEdge(1000));
        grafo.agregarArista("Porto Alegre", "Montevideo", new WeightedEdge(1000));
        grafo.agregarArista("Rio de Janeiro", "Curitiba", new WeightedEdge(1500));

        DirectedGraphAlgorithms algoritmos = new DirectedGraphAlgorithms();
        List<Path<String>> caminos = algoritmos.obtenerTodosLosCaminos(
                grafo.construirComparable("Montevideo"),
                grafo.construirComparable("Porto Alegre"),
                grafo);

        System.out.println("Caminos de Montevideo a Porto Alegre:");
        for (Path<String> camino : caminos) {
            System.out.println(String.join(" -> ", camino.getPath()) + " | costo: " + camino.getCost());
        }
    }
}