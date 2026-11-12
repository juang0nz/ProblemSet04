package ucu.edu.aed.Ejerccios.ejercicio_9;

import ucu.edu.aed.Ejerccios.ejercicio_8.Ejercicio8;
import ucu.edu.aed.tda.grafo.IUndirectedGraph;
import ucu.edu.aed.tda.grafo.impl.UndirectedGraph;
import ucu.edu.aed.tda.grafo.impl.UndirectedGraphAlgorithms;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;

public class Ejercicio9 {

    private static final int[][] DISTANCIAS = {
            {-1, 3, 5, 1, 7, 2},
            {3, -1, 1, 3, 5, 6},
            {5, 1, -1, 2, 3, 5},
            {1, 3, 2, -1, 7, 1},
            {7, 5, 3, 7, -1, 4},
            {2, 6, 5, 1, 4, -1}
    };

    public static UndirectedGraph<String, WeightedEdge> crearGrafo() {
        return Ejercicio8.crearGrafo(DISTANCIAS);
    }

    public static void main(String[] args) {
        UndirectedGraph<String, WeightedEdge> grafo = crearGrafo();
        UndirectedGraphAlgorithms algoritmos = new UndirectedGraphAlgorithms();
        IUndirectedGraph<String, WeightedEdge> arbol = algoritmos.kruskal(grafo);
        Ejercicio8.imprimirArbolMinimo(arbol, "Kruskal");
    }
}
