package ucu.edu.aed.Ejerccios.ejercicio_11;

import ucu.edu.aed.tda.grafo.impl.UndirectedGraph;
import ucu.edu.aed.tda.grafo.impl.UndirectedGraphAlgorithms;

import java.util.List;

public class Ejercicio11 {

    public static UndirectedGraph<String, String> crearGrafo() {
        UndirectedGraph<String, String> grafo = new UndirectedGraph<>();
        grafo.agregarVertices(List.of("Nairobi", "Cairo", "Monrovia", "Garoua", "Mekele", "Praia"));
        grafo.agregarArista("Nairobi", "Cairo", "canal");
        grafo.agregarArista("Nairobi", "Monrovia", "canal");
        grafo.agregarArista("Nairobi", "Garoua", "canal");
        grafo.agregarArista("Monrovia", "Garoua", "canal");
        grafo.agregarArista("Monrovia", "Mekele", "canal");
        grafo.agregarArista("Garoua", "Mekele", "canal");
        grafo.agregarArista("Mekele", "Praia", "canal");
        return grafo;
    }

    public static void main(String[] args) {
        UndirectedGraph<String, String> grafo = crearGrafo();
        System.out.println("Puntos de articulacion: "
                + new UndirectedGraphAlgorithms().puntosDeArticulacion(grafo));
    }
}
