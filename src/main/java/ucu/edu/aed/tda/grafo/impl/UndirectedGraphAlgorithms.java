package ucu.edu.aed.tda.grafo.impl;

import ucu.edu.aed.tda.grafo.IUndirectedGraph;
import ucu.edu.aed.tda.grafo.IUndirectedGraphAlgorithm;
import ucu.edu.aed.tda.grafo.model.IGraph;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.UndirectedEdge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class UndirectedGraphAlgorithms implements IUndirectedGraphAlgorithm {

    @Override
    public <V, D extends WeightedEdge> IUndirectedGraph<V, D> kruskal(IUndirectedGraph<V, D> graph) {
        UndirectedGraph<V, D> bosque = new UndirectedGraph<>();
        graph.vertices().forEach(bosque::agregarVertice);
        List<Edge<V, D>> aristas = new ArrayList<>(graph.aristas());
        for (Edge<V, D> arista : aristas) {
            validarPeso(arista);
        }
        aristas.sort(Comparator.comparingDouble(arista -> arista.dato().getWeight()));
        UnionFind<V> componentes = new UnionFind<>(graph.vertices());
        for (Edge<V, D> arista : aristas) {
            if (componentes.unir(arista.source(), arista.target())) {
                bosque.agregarArista(arista.source(), arista.target(), arista.dato());
            }
        }
        return bosque;
    }

    @Override
    public <V, D extends WeightedEdge> IUndirectedGraph<V, D> prim(
            IUndirectedGraph<V, D> graph, Comparable<V> source) {
        UndirectedGraph<V, D> bosque = new UndirectedGraph<>();
        List<V> vertices = new ArrayList<>(graph.vertices());
        vertices.forEach(bosque::agregarVertice);
        for (Edge<V, D> arista : graph.aristas()) {
            validarPeso(arista);
        }
        if (vertices.isEmpty()) {
            return bosque;
        }
        V inicio = graph.buscarVertice(source);
        if (inicio == null) {
            throw new IllegalArgumentException("El vertice de origen no pertenece al grafo");
        }

        Set<V> visitados = new LinkedHashSet<>();
        List<V> inicios = new ArrayList<>();
        inicios.add(inicio);
        for (V vertice : vertices) {
            if (!vertice.equals(inicio)) {
                inicios.add(vertice);
            }
        }
        for (V origenComponente : inicios) {
            if (visitados.contains(origenComponente)) {
                continue;
            }
            visitados.add(origenComponente);
            while (true) {
                Set<V> restantes = new LinkedHashSet<>(vertices);
                restantes.removeAll(visitados);
                Edge<V, D> arista = searchMinEdge(graph, visitados, restantes);
                if (arista == null) {
                    break;
                }
                bosque.agregarArista(arista.source(), arista.target(), arista.dato());
                visitados.add(arista.source());
                visitados.add(arista.target());
            }
        }
        return bosque;
    }

    @Override
    public <V, D extends WeightedEdge> Edge<V, D> searchMinEdge(
            IUndirectedGraph<V, D> graph, Collection<V> U, Collection<V> V) {
        Edge<V, D> minima = null;
        for (Edge<V, D> arista : graph.aristas()) {
            V origen = null;
            V destino = null;
            if (U.contains(arista.source()) && V.contains(arista.target())) {
                origen = arista.source();
                destino = arista.target();
            } else if (U.contains(arista.target()) && V.contains(arista.source())) {
                origen = arista.target();
                destino = arista.source();
            }
            if (origen == null) {
                continue;
            }
            validarPeso(arista);
            if (minima == null || arista.dato().getWeight() < minima.dato().getWeight()) {
                minima = new UndirectedEdge<>(origen, destino, arista.dato());
            }
        }
        return minima;
    }

    @Override
    public <V, D> void bea(IUndirectedGraph<V, D> graph, Consumer<V> consumer) {
        Set<V> visitados = new LinkedHashSet<>();
        for (V inicio : graph.vertices()) {
            if (!visitados.contains(inicio)) {
                beaComponente(graph, inicio, visitados, consumer);
            }
        }
    }

    @Override
    public <V, D> void bea(IUndirectedGraph<V, D> graph, Comparable<V> source, Consumer<V> consumer) {
        V inicio = graph.buscarVertice(source);
        if (inicio == null) {
            throw new IllegalArgumentException("El vertice de origen no pertenece al grafo");
        }
        beaComponente(graph, inicio, new LinkedHashSet<>(), consumer);
    }

    public <V, D> IUndirectedGraph<V, D> arbolAmplitud(
            IUndirectedGraph<V, D> graph, Comparable<V> source) {
        V inicio = graph.buscarVertice(source);
        if (inicio == null) {
            throw new IllegalArgumentException("El vertice de origen no pertenece al grafo");
        }
        UndirectedGraph<V, D> arbol = new UndirectedGraph<>();
        Set<V> visitados = new LinkedHashSet<>();
        Deque<V> pendientes = new ArrayDeque<>();
        arbol.agregarVertice(inicio);
        visitados.add(inicio);
        pendientes.addLast(inicio);
        while (!pendientes.isEmpty()) {
            V actual = pendientes.removeFirst();
            for (Edge<V, D> arista : graph.adyacencias(graph.construirComparable(actual))) {
                V vecino = arista.target();
                if (visitados.add(vecino)) {
                    arbol.agregarVertice(vecino);
                    arbol.agregarArista(actual, vecino, arista.dato());
                    pendientes.addLast(vecino);
                }
            }
        }
        return arbol;
    }

    public <V, E> List<V> puntosDeArticulacion(IGraph<V, E> graph) {
        Map<V, Integer> tiemposDescubrimiento = new HashMap<>();
        Map<V, Integer> valoresBajos = new HashMap<>();
        Set<V> puntos = new HashSet<>();
        int[] tiempo = {0};
        for (V vertice : graph.vertices()) {
            if (!tiemposDescubrimiento.containsKey(vertice)) {
                puntosDeArticulacionAux(graph, vertice, null, tiemposDescubrimiento, valoresBajos, puntos, tiempo);
            }
        }
        List<V> resultado = new ArrayList<>();
        for (V vertice : graph.vertices()) {
            if (puntos.contains(vertice)) {
                resultado.add(vertice);
            }
        }
        return resultado;
    }

    private <V, E> void puntosDeArticulacionAux(
            IGraph<V, E> graph, V actual, V padre, Map<V, Integer> descubrimiento,
            Map<V, Integer> bajos, Set<V> puntos, int[] tiempo) {
        int actualTiempo = ++tiempo[0];
        descubrimiento.put(actual, actualTiempo);
        bajos.put(actual, actualTiempo);
        int hijos = 0;
        for (Edge<V, E> arista : graph.adyacencias(graph.construirComparable(actual))) {
            V vecino = arista.target();
            if (vecino.equals(actual) || vecino.equals(padre)) {
                continue;
            }
            if (!descubrimiento.containsKey(vecino)) {
                hijos++;
                puntosDeArticulacionAux(graph, vecino, actual, descubrimiento, bajos, puntos, tiempo);
                bajos.put(actual, Math.min(bajos.get(actual), bajos.get(vecino)));
                if (padre != null && bajos.get(vecino) >= descubrimiento.get(actual)) {
                    puntos.add(actual);
                }
            } else {
                bajos.put(actual, Math.min(bajos.get(actual), descubrimiento.get(vecino)));
            }
        }
        if (padre == null && hijos > 1) {
            puntos.add(actual);
        }
    }

    private <V, D> void beaComponente(
            IUndirectedGraph<V, D> graph, V inicio, Set<V> visitados, Consumer<V> consumer) {
        Deque<V> pendientes = new ArrayDeque<>();
        visitados.add(inicio);
        pendientes.addLast(inicio);
        while (!pendientes.isEmpty()) {
            V actual = pendientes.removeFirst();
            consumer.accept(actual);
            for (Edge<V, D> arista : graph.adyacencias(graph.construirComparable(actual))) {
                if (visitados.add(arista.target())) {
                    pendientes.addLast(arista.target());
                }
            }
        }
    }

    private <V, D extends WeightedEdge> void validarPeso(Edge<V, D> arista) {
        if (arista.dato() == null || !Double.isFinite(arista.dato().getWeight())) {
            throw new IllegalArgumentException("El algoritmo requiere pesos finitos en todas las aristas");
        }
    }

    private static final class UnionFind<V> {
        private final Map<V, V> padres = new HashMap<>();
        private final Map<V, Integer> rangos = new HashMap<>();

        private UnionFind(Collection<V> vertices) {
            for (V vertice : vertices) {
                padres.put(vertice, vertice);
                rangos.put(vertice, 0);
            }
        }

        private V buscar(V vertice) {
            V padre = padres.get(vertice);
            if (!padre.equals(vertice)) {
                padre = buscar(padre);
                padres.put(vertice, padre);
            }
            return padre;
        }

        private boolean unir(V primero, V segundo) {
            V raizPrimero = buscar(primero);
            V raizSegundo = buscar(segundo);
            if (raizPrimero.equals(raizSegundo)) {
                return false;
            }
            int rangoPrimero = rangos.get(raizPrimero);
            int rangoSegundo = rangos.get(raizSegundo);
            if (rangoPrimero < rangoSegundo) {
                padres.put(raizPrimero, raizSegundo);
            } else if (rangoPrimero > rangoSegundo) {
                padres.put(raizSegundo, raizPrimero);
            } else {
                padres.put(raizSegundo, raizPrimero);
                rangos.put(raizPrimero, rangoPrimero + 1);
            }
            return true;
        }
    }
}
