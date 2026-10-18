package ucu.edu.aed.Ejerccios.ejercicio_3;

import junit.framework.TestCase;
import ucu.edu.aed.tda.grafo.impl.DirectedGraph;
import ucu.edu.aed.tda.grafo.impl.DirectedGraphAlgorithms;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
import ucu.edu.aed.tda.grafo.model.result.IFloydWarshallResult;
import ucu.edu.aed.tda.grafo.model.result.Path;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class Ejercicio3Test extends TestCase {

    private DirectedGraph<String, WeightedEdge> cargar() {
        return Ejercicio3.cargarGrafo("aeropuertos.txt", Ejercicio3.ARCHIVO_CONEXIONES);
    }

    public void testRecursosOriginalesSeConservanSeparadosDeTabla2() {
        DirectedGraph<String, WeightedEdge> original =
                Ejercicio3.cargarGrafo("aeropuertos.txt", "conexiones.txt");
        assertEquals(8, original.cantidadDeVertices());
        assertEquals(24, original.cantidadDeAristas());
        assertEquals(2000.0, original.obtenerArista("Montevideo", "San_Pablo").dato().getWeight(), 0);
        assertNull(original.obtenerArista("Montevideo", "Santos"));
        assertEquals(1000.0, original.obtenerArista("Porto_Alegre", "Montevideo").dato().getWeight(), 0);
        assertEquals(980.0, original.obtenerArista("Porto_Alegre", "San_Pablo").dato().getWeight(), 0);
        assertEquals(1200.0, original.obtenerArista("Porto_Alegre", "Santos").dato().getWeight(), 0);
    }

    public void testArchivosCoincidenConTodaLaTabla2() {
        DirectedGraph<String, WeightedEdge> grafo = cargar();
        List<String> ciudades = List.of("Asuncion", "Buenos_Aires", "Curitiba", "Montevideo",
                "Porto_Alegre", "Rio_de_Janeiro", "San_Pablo", "Santos");
        double[][] tabla = {
                {0, 1600, 800, 0, 700, 0, 0, 0},
                {1600, 0, 0, 200, 1250, 0, 0, 0},
                {0, 0, 0, 0, 0, 1500, 0, 0},
                {0, 200, 0, 0, 1000, 0, 980, 1200},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1500, 0, 0, 0, 1800, 1900},
                {1200, 0, 600, 2000, 980, 1800, 0, 220},
                {0, 0, 0, 0, 1200, 0, 220, 0}
        };
        assertEquals(ciudades, List.copyOf(grafo.vertices()));
        assertEquals(22, grafo.cantidadDeAristas());
        for (int i = 0; i < ciudades.size(); i++) {
            for (int j = 0; j < ciudades.size(); j++) {
                Edge<String, WeightedEdge> arista = grafo.obtenerArista(ciudades.get(i), ciudades.get(j));
                if (tabla[i][j] == 0) {
                    assertNull(arista);
                } else {
                    assertNotNull(arista);
                    assertEquals(tabla[i][j], arista.dato().getWeight(), 0);
                }
            }
        }
    }

    public void testFloydCoincideConEnumeracionDeCaminosParaLos64Pares() {
        DirectedGraph<String, WeightedEdge> grafo = cargar();
        DirectedGraphAlgorithms algoritmos = new DirectedGraphAlgorithms();
        IFloydWarshallResult<String> resultado = algoritmos.floyd(grafo);
        for (String origen : grafo.vertices()) {
            for (String destino : grafo.vertices()) {
                List<Path<String>> caminos = algoritmos.obtenerTodosLosCaminos(
                        grafo.construirComparable(origen), grafo.construirComparable(destino), grafo);
                double minimo = caminos.stream().mapToDouble(Path::getCost).min().orElse(Double.POSITIVE_INFINITY);
                assertEquals(origen + " -> " + destino, minimo, resultado.getCost(origen, destino), 0);
                assertEquals(!caminos.isEmpty(), resultado.connected(origen, destino));
                List<String> itinerario = resultado.getPath(origen, destino);
                if (caminos.isEmpty()) {
                    assertTrue(itinerario.isEmpty());
                } else {
                    assertEquals(origen, itinerario.get(0));
                    assertEquals(destino, itinerario.get(itinerario.size() - 1));
                    double costo = 0;
                    for (int i = 1; i < itinerario.size(); i++) {
                        Edge<String, WeightedEdge> tramo = grafo.obtenerArista(itinerario.get(i - 1), itinerario.get(i));
                        assertNotNull(tramo);
                        costo += tramo.dato().getWeight();
                    }
                    assertEquals(minimo, costo, 0);
                }
            }
        }
        assertEquals("Porto_Alegre", algoritmos.obtenerCentroGrafo(grafo));
        assertEquals(4280.0, algoritmos.obtenerExcentricidad(grafo,
                grafo.construirComparable("Porto_Alegre")), 0);
    }

    public void testConsultasInteractivasYRecuperacionDeEntradaInvalida() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        String consultas = "9\n1\nAtlantida\nSantos\n1\nPorto Alegre\nMontevideo\n"
                + "1\nmontevideo\nCuritiba\n2\nMontevideo\nRio de Janeiro\n"
                + "2\nasunci\u00f3n\nAsuncion\n3\n0\n";
        Ejercicio3.atenderConsultas(cargar(), new Scanner(consultas),
                new PrintStream(bytes, true, StandardCharsets.UTF_8));
        String salida = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(salida.contains("Aeropuertos: 8 | Vuelos: 22"));
        assertTrue(salida.contains("Opcion invalida"));
        assertTrue(salida.contains("Aeropuerto desconocido"));
        assertTrue(salida.contains("No existe un itinerario desde Porto Alegre hasta Montevideo"));
        assertTrue(salida.contains("Costo minimo: 1580.0"));
        assertTrue(salida.contains("Itinerario: Montevideo -> San Pablo -> Rio de Janeiro"));
        assertTrue(salida.contains("Costo minimo: 2780.0"));
        assertTrue(salida.contains("Itinerario: Asuncion"));
        assertTrue(salida.contains("Costo minimo: 0.0"));
        assertTrue(salida.contains("Centro recomendado: Porto Alegre"));
        assertTrue(salida.contains("Excentricidad de entrada: 4280.0"));
    }

    public void testFinDeEntradaNoProduceError() {
        for (String entrada : List.of("", "1\n", "2\nMontevideo\n")) {
            Ejercicio3.atenderConsultas(cargar(), new Scanner(entrada),
                    new PrintStream(new ByteArrayOutputStream()));
        }
    }

    public void testRecursoInexistenteFallaExplicitamente() {
        try {
            Ejercicio3.cargarGrafo("no-existe.txt", Ejercicio3.ARCHIVO_CONEXIONES);
            fail("No debe cargar un grafo parcial si falta un archivo");
        } catch (IllegalStateException expected) {
            assertTrue(expected.getMessage().contains("no-existe.txt"));
        }
    }
}
