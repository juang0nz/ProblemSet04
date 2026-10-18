package ucu.edu.aed.Ejerccios.ejercicio_7;

import ucu.edu.aed.tda.grafo.impl.DijkstraResult;
import ucu.edu.aed.tda.grafo.impl.DirectedGraph;
import ucu.edu.aed.tda.grafo.impl.DirectedGraphAlgorithms;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
import ucu.edu.aed.tda.grafo.model.result.IDijkstraResult;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

public class Ejercicio7 {

    public static DirectedGraph<String, WeightedEdge> crearGrafo() {
        DirectedGraph<String, WeightedEdge> grafo = new DirectedGraph<>();
        List<String> vertices = List.of("A", "B", "C", "D", "E");
        grafo.agregarVertices(vertices);
        int[][] matriz = {
                {-1, 1, 2, 7, -1},
                {7, -1, 1, 2, -1},
                {-1, -1, -1, 3, -1},
                {6, -1, 4, -1, 4},
                {-1, 2, -1, 8, -1}
        };
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < vertices.size(); j++) {
                if (matriz[i][j] >= 0) {
                    grafo.agregarArista(vertices.get(i), vertices.get(j), new WeightedEdge(matriz[i][j]));
                }
            }
        }
        return grafo;
    }

    public static void imprimirCamino(String origen, String destino,
                                      Map<String, String> predecesores, PrintStream salida) {
        List<String> camino = DijkstraResult.reconstruirCamino(origen, destino, predecesores);
        salida.println(camino.isEmpty() ? "No existe camino." : String.join(" -> ", camino));
    }

    public static void main(String[] args) {
        DirectedGraph<String, WeightedEdge> grafo = crearGrafo();
        System.out.println("=== Ejercicio 7: grafo dirigido (origen -> destino : costo) ===");
        for (Edge<String, WeightedEdge> arista : grafo.aristas()) {
            System.out.println(arista.source() + " -> " + arista.target() + " : " + arista.dato().getWeight());
        }
        IDijkstraResult<String> resultado = new DirectedGraphAlgorithms()
                .dijkstra(grafo.construirComparable("B"), grafo);
        System.out.println("\nPredecesores, orden A B C D E (sin predecesor: -):");
        for (String vertice : grafo.vertices()) {
            System.out.println(vertice + " <- " + resultado.getPredecessors().getOrDefault(vertice, "-"));
        }
        System.out.println("\nCaminos minimos desde B:");
        for (String destino : grafo.vertices()) {
            System.out.print(destino + " | costo " + resultado.getCost(destino) + " | ");
            imprimirCamino("B", destino, resultado.getPredecessors(), System.out);
        }
    }
}
