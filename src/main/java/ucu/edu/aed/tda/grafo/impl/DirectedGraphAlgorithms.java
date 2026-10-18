package ucu.edu.aed.tda.grafo.impl;

import ucu.edu.aed.tda.grafo.IDirectedGraphAlgorithms;
import ucu.edu.aed.tda.grafo.IDirectedIGraph;
import ucu.edu.aed.tda.grafo.model.IGraph;
import ucu.edu.aed.tda.grafo.model.edge.Edge;
import ucu.edu.aed.tda.grafo.model.edge.WeightedEdge;
import ucu.edu.aed.tda.grafo.model.result.IDijkstraResult;
import ucu.edu.aed.tda.grafo.model.result.IFloydWarshallResult;
import ucu.edu.aed.tda.grafo.model.result.Path;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class DirectedGraphAlgorithms implements IDirectedGraphAlgorithms {

    @Override
    public <V, D extends WeightedEdge> List<Path<V>> obtenerTodosLosCaminos(Comparable<V> source, Comparable<V> target, IGraph<V, D> grafo) {
        List<Path<V>> caminos = new ArrayList<>();
        V origen = grafo.buscarVertice(source);
        V destino = grafo.buscarVertice(target);
        if (origen == null || destino == null) {
            return caminos;
        }
        Set<V> visitados = new HashSet<>();
        LinkedList<V> stack = new LinkedList<>();
        obtenerTodosLosCaminosAux(grafo, origen, destino, 0, visitados, stack, caminos);
        return caminos;
    }

    private <V, D extends WeightedEdge> void obtenerTodosLosCaminosAux(IGraph<V, D> grafo, V nodo, V destino, double costo,
                                                                       Set<V> visitados, LinkedList<V> stack, List<Path<V>> caminos) {
        if (visitados.contains(nodo)) {
            return;
        }
        if (nodo.equals(destino)) {
            List<V> copia = new ArrayList<>(stack);
            copia.add(nodo);
            caminos.add(new Path<>(copia, costo));
            return;
        }
        visitados.add(nodo);
        stack.addLast(nodo);
        for (Edge<V, D> arista : grafo.adyacencias(grafo.construirComparable(nodo))) {
            obtenerTodosLosCaminosAux(grafo, arista.target(), destino, costo + arista.dato().getWeight(), visitados, stack, caminos);
        }
        visitados.remove(nodo);
        stack.removeLast();
    }

    @Override
    public <V, D extends WeightedEdge> IDijkstraResult<V> dijkstra(Comparable<V> source, IDirectedIGraph<V, D> grafo) {
        throw new UnsupportedOperationException("Pendiente");
    }

    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> floyd(IDirectedIGraph<V, D> grafo) {
        throw new UnsupportedOperationException("Pendiente");
    }

    @Override
    public <V, D extends WeightedEdge> IFloydWarshallResult<V> warshall(IDirectedIGraph<V, D> grafo) {
        throw new UnsupportedOperationException("Pendiente");
    }

    @Override
    public <V, D extends WeightedEdge> V obtenerCentroGrafo(IDirectedIGraph<V, D> grafo) {
        throw new UnsupportedOperationException("Pendiente");
    }

    @Override
    public <V, D extends WeightedEdge> double obtenerExcentricidad(IDirectedIGraph<V, D> grafo, Comparable<V> vertexCriteria) {
        throw new UnsupportedOperationException("Pendiente");
    }

    @Override
    public <V, D> void recorridoEnProfundidad(IGraph<V, D> grafo, Comparable<V> sourceCriteria, Consumer<V> consumer) {
        throw new UnsupportedOperationException("Pendiente");
    }

    @Override
    public <V, D> void recorridoEnAmplitud(IGraph<V, D> grafo, Comparable<V> sourceCriteria, Consumer<V> consumer) {
        throw new UnsupportedOperationException("Pendiente");
    }

    @Override
    public <V, D> List<V> calcularClasificacionTopologica(IDirectedIGraph<V, D> grafo) {
        Set<V> visitados = new HashSet<>();
        LinkedList<V> listaNodos = new LinkedList<>();
        for (V vertice : grafo.vertices()) {
            clasificacionTopologicaAux(grafo, vertice, visitados, listaNodos);
        }
        return listaNodos;
    }

    private <V, D> void clasificacionTopologicaAux(IDirectedIGraph<V, D> grafo, V nodo, Set<V> visitados, LinkedList<V> lista) {
        if (!visitados.contains(nodo)) {
            visitados.add(nodo);
            for (Edge<V, D> arista : grafo.adyacencias(grafo.construirComparable(nodo))) {
                clasificacionTopologicaAux(grafo, arista.target(), visitados, lista);
            }
            lista.addFirst(nodo);
        }
    }

    public <V, D extends WeightedEdge> List<Path<V>> caminoCritico(IDirectedIGraph<V, D> grafo) {
        if (grafo.tieneCiclos()) {
            throw new IllegalArgumentException("El grafo tiene ciclos, no se puede calcular el camino critico");
        }
        List<Path<V>> caminos = new ArrayList<>();
        for (V inicio : grafo.vertices()) {
            if (grafo.gradoDeEntrada(grafo.construirComparable(inicio)) == 0) {
                for (V fin : grafo.vertices()) {
                    if (grafo.gradoDeSalida(grafo.construirComparable(fin)) == 0) {
                        caminos.addAll(obtenerTodosLosCaminos(grafo.construirComparable(inicio), grafo.construirComparable(fin), grafo));
                    }
                }
            }
        }
        caminos.sort((a, b) -> Double.compare(b.getCost(), a.getCost()));
        return caminos;
    }
}