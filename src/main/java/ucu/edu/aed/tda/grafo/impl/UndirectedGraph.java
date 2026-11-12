package ucu.edu.aed.tda.grafo.impl;

import ucu.edu.aed.tda.grafo.IUndirectedGraph;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.UndirectedEdge;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UndirectedGraph<V, D> implements IUndirectedGraph<V, D> {

    private final Map<V, List<Edge<V, D>>> adyacencias = new LinkedHashMap<>();

    @Override
    public boolean agregarVertice(V vertex) {
        if (vertex == null || adyacencias.containsKey(vertex)) {
            return false;
        }
        adyacencias.put(vertex, new ArrayList<>());
        return true;
    }

    @Override
    public V buscarVertice(Comparable<V> criterio) {
        if (criterio == null) {
            return null;
        }
        for (V vertice : adyacencias.keySet()) {
            if (criterio.compareTo(vertice) == 0) {
                return vertice;
            }
        }
        return null;
    }

    @Override
    public boolean agregarArista(V source, V target, D dato) {
        if (!adyacencias.containsKey(source) || !adyacencias.containsKey(target)
                || obtenerArista(source, target) != null) {
            return false;
        }
        Edge<V, D> arista = new UndirectedEdge<>(source, target, dato);
        adyacencias.get(source).add(arista);
        if (!source.equals(target)) {
            adyacencias.get(target).add(arista);
        }
        return true;
    }

    @Override
    public boolean eliminarArista(Comparable<V> source, Comparable<V> target) {
        V origen = buscarVertice(source);
        V destino = buscarVertice(target);
        if (origen == null || destino == null) {
            return false;
        }
        boolean eliminada = adyacencias.get(origen).removeIf(arista -> conecta(arista, origen, destino));
        if (!origen.equals(destino)) {
            adyacencias.get(destino).removeIf(arista -> conecta(arista, origen, destino));
        }
        return eliminada;
    }

    @Override
    public boolean removerVertice(Comparable<V> criteria) {
        V vertice = buscarVertice(criteria);
        if (vertice == null) {
            return false;
        }
        adyacencias.remove(vertice);
        for (List<Edge<V, D>> aristas : adyacencias.values()) {
            aristas.removeIf(arista -> arista.source().equals(vertice) || arista.target().equals(vertice));
        }
        return true;
    }

    @Override
    public Set<V> vertices() {
        return Collections.unmodifiableSet(adyacencias.keySet());
    }

    @Override
    public Set<Edge<V, D>> aristas() {
        Set<Edge<V, D>> todas = new LinkedHashSet<>();
        for (List<Edge<V, D>> aristas : adyacencias.values()) {
            todas.addAll(aristas);
        }
        return Collections.unmodifiableSet(todas);
    }

    @Override
    public boolean existeArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        return obtenerArista(sourceCriteria, targetCriteria) != null;
    }

    @Override
    public Edge<V, D> obtenerArista(Comparable<V> sourceCriteria, Comparable<V> targetCriteria) {
        V origen = buscarVertice(sourceCriteria);
        V destino = buscarVertice(targetCriteria);
        if (origen == null || destino == null) {
            return null;
        }
        for (Edge<V, D> arista : adyacencias.get(origen)) {
            if (otroExtremo(arista, origen).equals(destino)) {
                return new UndirectedEdge<>(origen, destino, arista.dato());
            }
        }
        return null;
    }

    @Override
    public List<Edge<V, D>> adyacencias(Comparable<V> verticeCriteria) {
        V vertice = buscarVertice(verticeCriteria);
        if (vertice == null) {
            return Collections.emptyList();
        }
        List<Edge<V, D>> vecinos = new ArrayList<>();
        for (Edge<V, D> arista : adyacencias.get(vertice)) {
            vecinos.add(new UndirectedEdge<>(vertice, otroExtremo(arista, vertice), arista.dato()));
        }
        return Collections.unmodifiableList(vecinos);
    }

    @Override
    public boolean esConexo() {
        if (adyacencias.isEmpty()) {
            return true;
        }
        V inicio = adyacencias.keySet().iterator().next();
        Set<V> visitados = new LinkedHashSet<>();
        Deque<V> pendientes = new ArrayDeque<>();
        visitados.add(inicio);
        pendientes.addLast(inicio);
        while (!pendientes.isEmpty()) {
            V actual = pendientes.removeFirst();
            for (Edge<V, D> arista : adyacencias(construirComparable(actual))) {
                if (visitados.add(arista.target())) {
                    pendientes.addLast(arista.target());
                }
            }
        }
        return visitados.size() == adyacencias.size();
    }

    @Override
    public void vaciar() {
        adyacencias.clear();
    }

    @Override
    public boolean tieneCiclos() {
        Set<V> visitados = new LinkedHashSet<>();
        for (V vertice : adyacencias.keySet()) {
            if (!visitados.contains(vertice) && tieneCiclosAux(vertice, null, visitados)) {
                return true;
            }
        }
        return false;
    }

    private boolean tieneCiclosAux(V actual, V padre, Set<V> visitados) {
        visitados.add(actual);
        for (Edge<V, D> arista : adyacencias(construirComparable(actual))) {
            V vecino = arista.target();
            if (vecino.equals(actual)) {
                return true;
            }
            if (vecino.equals(padre)) {
                continue;
            }
            if (visitados.contains(vecino) || tieneCiclosAux(vecino, actual, visitados)) {
                return true;
            }
        }
        return false;
    }

    private V otroExtremo(Edge<V, D> arista, V vertice) {
        return arista.source().equals(vertice) ? arista.target() : arista.source();
    }

    private boolean conecta(Edge<V, D> arista, V primero, V segundo) {
        return (arista.source().equals(primero) && arista.target().equals(segundo))
                || (arista.source().equals(segundo) && arista.target().equals(primero));
    }
}
