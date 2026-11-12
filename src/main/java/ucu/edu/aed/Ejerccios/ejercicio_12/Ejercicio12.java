package ucu.edu.aed.Ejerccios.ejercicio_12;

import ucu.edu.aed.tda.grafo.impl.UndirectedGraph;
import ucu.edu.aed.tda.grafo.model.IGraph;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

public class Ejercicio12 {

    private static final String KEVIN_BACON = "Kevin_Bacon";
    private static final List<String> ACTORES_CONSULTA = List.of(
            "John_Goodman", "Tom_Cruise", "Jason_Statham", "Lukas_Haas", "Djimon_Hounsou");

    public static int numBacon(IGraph<String, ?> grafo, String actor) {
        if (actor == null || grafo.buscarVertice(grafo.construirComparable(actor)) == null
                || grafo.buscarVertice(grafo.construirComparable(KEVIN_BACON)) == null) {
            return -1;
        }
        if (actor.equals(KEVIN_BACON)) {
            return 0;
        }
        Map<String, Integer> distancias = new HashMap<>();
        Queue<String> pendientes = new ArrayDeque<>();
        distancias.put(actor, 0);
        pendientes.add(actor);
        while (!pendientes.isEmpty()) {
            String actual = pendientes.remove();
            int distancia = distancias.get(actual);
            for (var arista : grafo.adyacencias(grafo.construirComparable(actual))) {
                String vecino = arista.target();
                if (distancias.containsKey(vecino)) {
                    continue;
                }
                int nuevaDistancia = distancia + 1;
                if (vecino.equals(KEVIN_BACON)) {
                    return nuevaDistancia;
                }
                distancias.put(vecino, nuevaDistancia);
                pendientes.add(vecino);
            }
        }
        return -1;
    }

    public static UndirectedGraph<String, String> cargarGrafo(Path archivoActores, Path archivoPeliculas)
            throws IOException {
        UndirectedGraph<String, String> grafo = new UndirectedGraph<>();
        for (List<String> fila : leerCsv(archivoActores)) {
            if (fila.isEmpty() || fila.get(0).isBlank() || esEncabezadoActor(fila.get(0))) {
                continue;
            }
            grafo.agregarVertice(fila.get(0).trim());
        }

        List<List<String>> filasPeliculas = leerCsv(archivoPeliculas);
        int indiceActorUno = 0;
        int indiceActorDos = 1;
        int indicePelicula = 2;
        if (!filasPeliculas.isEmpty() && esEncabezadoRelacion(filasPeliculas.get(0))) {
            int[] indices = indicesEncabezado(filasPeliculas.get(0));
            indiceActorUno = indices[0];
            indiceActorDos = indices[1];
            indicePelicula = indices[2];
            filasPeliculas = filasPeliculas.subList(1, filasPeliculas.size());
        }
        for (List<String> fila : filasPeliculas) {
            int mayorIndice = Math.max(indicePelicula, Math.max(indiceActorUno, indiceActorDos));
            if (fila.size() <= mayorIndice) {
                throw new IllegalArgumentException("Fila de pelicula incompleta: " + fila);
            }
            String actorUno = fila.get(indiceActorUno).trim();
            String actorDos = fila.get(indiceActorDos).trim();
            String pelicula = fila.get(indicePelicula).trim();
            if (grafo.buscarVertice(grafo.construirComparable(actorUno)) == null
                    || grafo.buscarVertice(grafo.construirComparable(actorDos)) == null) {
                throw new IllegalArgumentException("La pelicula referencia un actor ausente del archivo de actores: " + fila);
            }
            if (!grafo.agregarArista(actorUno, actorDos, pelicula)) {
                throw new IllegalArgumentException("Relacion duplicada entre actores: " + actorUno + " y " + actorDos);
            }
        }
        return grafo;
    }

    private static List<List<String>> leerCsv(Path archivo) throws IOException {
        List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
        char separador = lineas.stream().filter(linea -> !linea.isBlank()).findFirst()
                .map(linea -> linea.indexOf(';') >= 0 && linea.indexOf(',') < 0 ? ';' : ',')
                .orElse(',');
        List<List<String>> filas = new ArrayList<>();
        for (String linea : lineas) {
            if (!linea.isBlank()) {
                filas.add(parsearFila(linea, separador));
            }
        }
        if (!filas.isEmpty() && !filas.get(0).isEmpty()) {
            filas.get(0).set(0, filas.get(0).get(0).replace("\uFEFF", ""));
        }
        return filas;
    }

    private static List<String> parsearFila(String linea, char separador) {
        List<String> campos = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean entreComillas = false;
        for (int i = 0; i < linea.length(); i++) {
            char caracter = linea.charAt(i);
            if (caracter == '"') {
                if (entreComillas && i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
                    campo.append('"');
                    i++;
                } else {
                    entreComillas = !entreComillas;
                }
            } else if (caracter == separador && !entreComillas) {
                campos.add(campo.toString());
                campo.setLength(0);
            } else {
                campo.append(caracter);
            }
        }
        if (entreComillas) {
            throw new IllegalArgumentException("Campo CSV con comillas sin cerrar: " + linea);
        }
        campos.add(campo.toString());
        return campos;
    }

    private static boolean esEncabezadoActor(String campo) {
        String encabezado = normalizar(campo);
        return encabezado.startsWith("actor") || encabezado.startsWith("name")
                || encabezado.startsWith("nombre");
    }

    private static boolean esEncabezadoRelacion(List<String> fila) {
        return fila.stream().map(Ejercicio12::normalizar)
                .anyMatch(campo -> campo.contains("actor") || campo.contains("pelicula")
                        || campo.contains("movie") || campo.contains("film"));
    }

    private static int[] indicesEncabezado(List<String> encabezado) {
        int primero = -1;
        int segundo = -1;
        int pelicula = -1;
        for (int i = 0; i < encabezado.size(); i++) {
            String campo = normalizar(encabezado.get(i));
            if (campo.contains("pelicula") || campo.contains("movie") || campo.contains("film")) {
                pelicula = i;
            } else if (campo.contains("actor") || campo.contains("interprete")) {
                if (primero == -1) {
                    primero = i;
                } else if (segundo == -1) {
                    segundo = i;
                }
            }
        }
        if (primero == -1 || segundo == -1 || pelicula == -1) {
            throw new IllegalArgumentException(
                    "El encabezado de peliculas debe identificar dos actores y una pelicula");
        }
        return new int[]{primero, segundo, pelicula};
    }

    private static String normalizar(String texto) {
        return Normalizer.normalize(texto.trim().toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").replaceAll("[^a-z0-9]", "");
    }

    public static void main(String[] args) throws IOException {
        Path archivoActores = Path.of(args.length > 0 ? args[0] : "actores.csv");
        Path archivoPeliculas = args.length > 1
                ? Path.of(args[1])
                : Files.exists(Path.of("en_películas.csv"))
                ? Path.of("en_películas.csv")
                : Path.of("en_peliculas.csv");
        UndirectedGraph<String, String> grafo = cargarGrafo(archivoActores, archivoPeliculas);
        for (String actor : ACTORES_CONSULTA) {
            System.out.println(actor + ": " + numBacon(grafo, actor));
        }
    }
}
