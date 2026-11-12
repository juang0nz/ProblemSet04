package ucu.edu.aed.Ejerccios.ejercicio_8;

import ucu.edu.aed.tda.grafo.IUndirectedGraph;
import ucu.edu.aed.tda.grafo.impl.UndirectedGraph;
import ucu.edu.aed.tda.grafo.impl.UndirectedGraphAlgorithms;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;

import java.util.List;

public class Ejercicio8 {

    private static final List<String> ZONAS = List.of(
            "Zona 1", "Zona 2", "Zona 3", "Zona 4", "Zona 5", "Zona 6");
    private static final int[][] DISTANCIAS = {
            {-1, 2, 5, 1, 7, 2},
            {2, -1, 1, 3, 5, 6},
            {5, 1, -1, 2, 3, 5},
            {1, 3, 2, -1, 7, 1},
            {7, 5, 3, 7, -1, 4},
            {2, 6, 5, 1, 4, -1}
    };

    public static UndirectedGraph<String, WeightedEdge> crearGrafo() {
        return crearGrafo(DISTANCIAS);
    }

    public static UndirectedGraph<String, WeightedEdge> crearGrafo(int[][] distancias) {
        if (distancias.length != ZONAS.size()) {
            throw new IllegalArgumentException("La matriz debe tener seis filas");
        }
        UndirectedGraph<String, WeightedEdge> grafo = new UndirectedGraph<>();
        grafo.agregarVertices(ZONAS);
        for (int i = 0; i < distancias.length; i++) {
            if (distancias[i].length != ZONAS.size()) {
                throw new IllegalArgumentException("La matriz debe ser cuadrada de seis por seis");
            }
            for (int j = i + 1; j < distancias[i].length; j++) {
                if (distancias[i][j] != distancias[j][i]) {
                    throw new IllegalArgumentException("La matriz de un grafo no dirigido debe ser simetrica");
                }
                if (distancias[i][j] >= 0) {
                    grafo.agregarArista(ZONAS.get(i), ZONAS.get(j), new WeightedEdge(distancias[i][j]));
                }
            }
        }
        return grafo;
    }

    public static void imprimirArbolMinimo(IUndirectedGraph<String, WeightedEdge> arbol, String algoritmo) {
        System.out.println("Arbol de expansion minimo (" + algoritmo + "): ");
        double costoTotal = 0;
        for (Edge<String, WeightedEdge> arista : arbol.aristas()) {
            costoTotal += arista.dato().getWeight();
            System.out.println(arista.source() + " -- " + arista.target()
                    + " : " + arista.dato().getWeight() + " km | acumulado: " + costoTotal + " km");
        }
        System.out.println("Caminos construidos: " + arbol.cantidadDeAristas());
        System.out.println("Costo total: " + costoTotal + " km");
    }

    public static void main(String[] args) {
        UndirectedGraph<String, WeightedEdge> grafo = crearGrafo();
        UndirectedGraphAlgorithms algoritmos = new UndirectedGraphAlgorithms();
        IUndirectedGraph<String, WeightedEdge> arbol = algoritmos.prim(grafo);
        imprimirArbolMinimo(arbol, "Prim");
    }
}
