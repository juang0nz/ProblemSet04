package ucu.edu.aed.Ejerccios.ejercicio_3;

import ucu.edu.aed.tda.grafo.impl.DirectedGraph;
import ucu.edu.aed.tda.grafo.impl.DirectedGraphAlgorithms;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
import ucu.edu.aed.tda.grafo.model.result.IFloydWarshallResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Ejercicio3 {

    // La Tabla 2 difiere del recurso compartido conexiones.txt.
    public static final String ARCHIVO_CONEXIONES = "ejercicio_3/conexiones.txt";

    public static void main(String[] args) {
        DirectedGraph<String, WeightedEdge> grafo = cargarGrafo("aeropuertos.txt", ARCHIVO_CONEXIONES);
        atenderConsultas(grafo, new Scanner(System.in, StandardCharsets.UTF_8), System.out);
    }

    public static DirectedGraph<String, WeightedEdge> cargarGrafo(String archivoVertices, String archivoAristas) {
        DirectedGraph<String, WeightedEdge> grafo = new DirectedGraph<>();
        for (String vertice : leerLineas(archivoVertices)) {
            if (!grafo.agregarVertice(vertice)) {
                throw new IllegalArgumentException("Aeropuerto duplicado en " + archivoVertices + ": " + vertice);
            }
        }
        for (String linea : leerLineas(archivoAristas)) {
            String[] partes = linea.split(",", -1);
            if (partes.length != 3) {
                throw new IllegalArgumentException("Conexion invalida en " + archivoAristas + ": " + linea);
            }
            double costo;
            try {
                costo = Double.parseDouble(partes[2].trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Costo invalido en " + archivoAristas + ": " + linea, e);
            }
            if (!Double.isFinite(costo) || costo < 0) {
                throw new IllegalArgumentException("El costo debe ser finito y no negativo: " + linea);
            }
            if (!grafo.agregarArista(partes[0].trim(), partes[1].trim(), new WeightedEdge(costo))) {
                throw new IllegalArgumentException("Conexion duplicada o aeropuerto inexistente en "
                        + archivoAristas + ": " + linea);
            }
        }
        return grafo;
    }

    private static List<String> leerLineas(String archivo) {
        InputStream entrada = Ejercicio3.class.getResourceAsStream("/" + archivo);
        if (entrada == null) {
            throw new IllegalStateException("No se encontro el recurso: " + archivo);
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
            throw new UncheckedIOException("No se pudo leer " + archivo, e);
        }
        return lineas;
    }

    public static void atenderConsultas(DirectedGraph<String, WeightedEdge> grafo,
                                        Scanner entrada, PrintStream salida) {
        DirectedGraphAlgorithms algoritmos = new DirectedGraphAlgorithms();
        IFloydWarshallResult<String> resultado = algoritmos.floyd(grafo);
        salida.println("=== VUELE SEGURO - Ejercicio 3 ===");
        salida.println("Aeropuertos: " + grafo.cantidadDeVertices() + " | Vuelos: " + grafo.cantidadDeAristas());
        salida.println(String.join(", ", grafo.vertices()).replace('_', ' '));
        while (true) {
            salida.println("\n1. Menor costo entre ciudades");
            salida.println("2. Itinerario de menor costo");
            salida.println("3. Centro de mantenimiento y logistica");
            salida.println("0. Salir");
            if (!entrada.hasNextLine()) {
                return;
            }
            String opcion = entrada.nextLine().trim();
            if (opcion.equals("0")) {
                return;
            }
            if (opcion.equals("3")) {
                String centro = algoritmos.obtenerCentroGrafo(grafo);
                if (centro == null) {
                    salida.println("No hay un aeropuerto alcanzable desde todos los demas.");
                } else {
                    salida.println("Centro recomendado: " + centro.replace('_', ' '));
                    salida.println("Excentricidad de entrada: "
                            + algoritmos.obtenerExcentricidad(grafo, grafo.construirComparable(centro)));
                }
                continue;
            }
            if (!opcion.equals("1") && !opcion.equals("2")) {
                salida.println("Opcion invalida.");
                continue;
            }
            salida.println("Ciudad de origen:");
            if (!entrada.hasNextLine()) {
                return;
            }
            String origen = buscarCiudad(grafo, entrada.nextLine());
            salida.println("Ciudad de destino:");
            if (!entrada.hasNextLine()) {
                return;
            }
            String destino = buscarCiudad(grafo, entrada.nextLine());
            if (origen == null || destino == null) {
                salida.println("Aeropuerto desconocido. Use una ciudad de la lista.");
                continue;
            }
            if (!resultado.connected(origen, destino)) {
                salida.println("No existe un itinerario desde " + origen.replace('_', ' ')
                        + " hasta " + destino.replace('_', ' ') + ".");
                continue;
            }
            if (opcion.equals("2")) {
                salida.println("Itinerario: " + String.join(" -> ", resultado.getPath(origen, destino)).replace('_', ' '));
            }
            salida.println("Costo minimo: " + resultado.getCost(origen, destino));
        }
    }

    private static String buscarCiudad(DirectedGraph<String, WeightedEdge> grafo, String ciudad) {
        String nombre = normalizar(ciudad);
        for (String vertice : grafo.vertices()) {
            if (normalizar(vertice).equalsIgnoreCase(nombre)) {
                return vertice;
            }
        }
        return null;
    }

    private static String normalizar(String ciudad) {
        return Normalizer.normalize(ciudad.trim().replace('_', ' '), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
    }
}
