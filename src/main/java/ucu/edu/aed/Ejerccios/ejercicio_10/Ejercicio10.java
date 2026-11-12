package ucu.edu.aed.Ejerccios.ejercicio_10;

import ucu.edu.aed.tda.grafo.impl.UndirectedGraph;
import ucu.edu.aed.tda.grafo.impl.UndirectedGraphAlgorithms;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;

import java.util.ArrayList;
import java.util.List;

/**
 * Ejemplo de creación y recorrido de un grafo no dirigido ponderado.
 */
public class Ejercicio10 {

    /**
     * Crea el grafo del ejercicio con los vértices A–H y sus aristas ponderadas.
     *
     * @return el grafo no dirigido construido
     */
    public static UndirectedGraph<String, WeightedEdge> crearGrafo() {
        // Inicializa un grafo no dirigido cuyos vértices se identifican con texto.
        UndirectedGraph<String, WeightedEdge> grafo = new UndirectedGraph<>();

        // Agrega todos los vértices antes de definir las conexiones entre ellos.
        grafo.agregarVertices(List.of("A", "B", "C", "D", "E", "F", "G", "H"));

        // Conexiones de la parte izquierda del grafo.
        grafo.agregarArista("A", "B", new WeightedEdge(2));
        grafo.agregarArista("A", "C", new WeightedEdge(3));
        grafo.agregarArista("B", "D", new WeightedEdge(2));
        grafo.agregarArista("C", "D", new WeightedEdge(1));

        // Conexiones alrededor del vértice central D y del vértice E.
        grafo.agregarArista("D", "E", new WeightedEdge(2));
        grafo.agregarArista("D", "F", new WeightedEdge(4));
        grafo.agregarArista("E", "F", new WeightedEdge(1));
        grafo.agregarArista("E", "G", new WeightedEdge(2));

        // Conexiones de la parte derecha del grafo.
        grafo.agregarArista("F", "G", new WeightedEdge(2));
        grafo.agregarArista("F", "H", new WeightedEdge(1));
        grafo.agregarArista("G", "H", new WeightedEdge(3));

        // Devuelve el grafo completo para que pueda ser recorrido o analizado.
        return grafo;
    }

    public static void main(String[] args) {
        // Construye el grafo y prepara los algoritmos que se utilizarán.
        UndirectedGraph<String, WeightedEdge> grafo = crearGrafo();
        UndirectedGraphAlgorithms algoritmos = new UndirectedGraphAlgorithms();

        // Esta lista guardará los vértices en el orden en que se visitan.
        List<String> recorrido = new ArrayList<>();

        // Ejecuta la búsqueda en amplitud (BEA/BFS) desde E y agrega cada visita a la lista.
        algoritmos.bea(grafo, grafo.construirComparable("E"), recorrido::add);

        // Muestra el orden de visita, separado por flechas.
        System.out.println("Recorrido en amplitud desde E: " + String.join(" -> ", recorrido));

        // Calcula e imprime las aristas que forman el árbol de amplitud desde E.
        System.out.println("Aristas del arbol abarcador:");
        algoritmos.arbolAmplitud(grafo, grafo.construirComparable("E")).aristas()
                .forEach(arista -> System.out.println(arista.source() + " -- " + arista.target()));
    }
}
