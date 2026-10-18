package ucu.edu.aed.Ejerccios.ejercicio_6;

import ucu.edu.aed.tda.grafo.impl.DirectedGraph;
import ucu.edu.aed.tda.grafo.impl.DirectedGraphAlgorithms;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
import ucu.edu.aed.tda.grafo.model.result.Path;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Ejercicio6 {

    public static void main(String[] args) {
        DirectedGraphAlgorithms algoritmos = new DirectedGraphAlgorithms();

        System.out.println("=== Parte 1: deteccion de ciclos ===");
        DirectedGraph<String, WeightedEdge> aeropuertos = cargarGrafo("aeropuertos_2.txt", "conexiones_2.txt");
        System.out.println("Aeropuertos cargados: " + aeropuertos.cantidadDeVertices());
        System.out.println("Conexiones cargadas: " + aeropuertos.cantidadDeAristas());
        System.out.println("Tiene ciclos: " + aeropuertos.tieneCiclos());

        System.out.println();
        DirectedGraph<String, WeightedEdge> proyecto = new DirectedGraph<>();

        proyecto.agregarVertice("Inicio");
        proyecto.agregarVertice("A");
        proyecto.agregarVertice("B");
        proyecto.agregarVertice("C");
        proyecto.agregarVertice("D");
        proyecto.agregarVertice("Fin");

        proyecto.agregarArista("Inicio", "A", new WeightedEdge(3));
        proyecto.agregarArista("Inicio", "B", new WeightedEdge(2));
        proyecto.agregarArista("A", "C", new WeightedEdge(4));
        proyecto.agregarArista("B", "C", new WeightedEdge(2));
        proyecto.agregarArista("B", "D", new WeightedEdge(5));
        proyecto.agregarArista("C", "Fin", new WeightedEdge(3));
        proyecto.agregarArista("D", "Fin", new WeightedEdge(2));

        System.out.println("=== Parte 2: camino critico ===");
        System.out.println("Tiene ciclos: " + proyecto.tieneCiclos());
        List<Path<String>> caminos = algoritmos.caminoCritico(proyecto);
        Path<String> critico = caminos.get(0);
        System.out.println("Camino critico: " + String.join(" -> ", critico.getPath()) + " | costo: " + critico.getCost());
        System.out.println("Otras secuencias:");
        for (int i = 1; i < caminos.size(); i++) {
            Path<String> camino = caminos.get(i);
            double holgura = critico.getCost() - camino.getCost();
            System.out.println(String.join(" -> ", camino.getPath()) + " | costo: " + camino.getCost() + " | holgura: " + holgura);
        }

        System.out.println();
        System.out.println("=== Parte 3: clasificacion topologica ===");
        List<String> orden = algoritmos.calcularClasificacionTopologica(proyecto);
        System.out.println(String.join(" -> ", orden));
    }

    private static DirectedGraph<String, WeightedEdge> cargarGrafo(String archivoVertices, String archivoAristas) {
        DirectedGraph<String, WeightedEdge> grafo = new DirectedGraph<>();
        for (String linea : leerLineas(archivoVertices)) {
            grafo.agregarVertice(linea);
        }
        for (String linea : leerLineas(archivoAristas)) {
            String[] partes = linea.split(",");
            String origen = partes[0].trim();
            String destino = partes[1].trim();
            double costo = Double.parseDouble(partes[2].trim());
            grafo.agregarArista(origen, destino, new WeightedEdge(costo));
        }
        return grafo;
    }

    private static List<String> leerLineas(String archivo) {
        InputStream entrada = Ejercicio6.class.getResourceAsStream("/" + archivo);
        if (entrada == null) {
            throw new IllegalStateException("No se encontro " + archivo + " en src/main/resources");
        }
        List<String> lineas = new ArrayList<>();
        try (BufferedReader lector = new BufferedReader(new InputStreamReader(entrada, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    lineas.add(linea);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return lineas;
    }
}