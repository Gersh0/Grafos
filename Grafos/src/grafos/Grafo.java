package grafos;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

class ExcepcionVertice extends Exception {
	private static final long serialVersionUID = 1L;

	public ExcepcionVertice() {
		super("Error en vertice :D");
	}

	public ExcepcionVertice(String e) {
		super(e);
	}
}

public class Grafo<E extends Comparable<E>> {
	private LinkedList<Vertice<E>> vertices;

	public Grafo(LinkedList<Vertice<E>> vertices) {
		this.vertices = vertices;
	}

	public void inicializarVertices() {
		ListIterator<Vertice<E>> list = vertices.listIterator();
		while (list.hasNext()) {
			Vertice<E> v = list.next();
			v.setAnterior(null);
			v.setDistance(Double.POSITIVE_INFINITY);
		}
	}

	public Stack<Vertice<E>> MenorCaminoSinPesos(Vertice<E> inicio, Vertice<E> destino) throws ExcepcionVertice {
		inicializarVertices();

		Queue<Vertice<E>> camino = new LinkedList<Vertice<E>>();
		inicio.setDistance(0);
		camino.add(inicio);

		while (!camino.isEmpty()) {
			Vertice<E> v = camino.poll();
			LinkedList<Arista<E>> adyacentes = v.getAdyacentes();
			ListIterator<Arista<E>> list = adyacentes.listIterator();
			while (list.hasNext()) {
				Vertice<E> temp = list.next().getDestino();
				if (temp.getAnterior() == null) {
					temp.setAnterior(v);
					temp.setDistance(temp.getAnterior().getDistance() + 1);
					camino.add(temp);
				}
			}
		}
		Stack<Vertice<E>> stack = new Stack<Vertice<E>>();
		Vertice<E> v = destino;
		stack.add(v);
		while (v.getAnterior() != null) {
			v = v.getAnterior();
			stack.add(v);
		}
		return stack;
	}

	public Stack<Vertice<E>> dijkstra1(Vertice<E> inicio, Vertice<E> destino) {
		// El primer método que hice, funciona y es parecido al método que hice en clase
		// pero con una condición adicional.
		inicializarVertices();
		Queue<Vertice<E>> camino = new LinkedList<Vertice<E>>();
		inicio.setDistance(0);
		camino.add(inicio);
		while (!camino.isEmpty()) {
			Vertice<E> v = camino.poll();
			LinkedList<Arista<E>> adyacentes = v.getAdyacentes();
			ListIterator<Arista<E>> a = adyacentes.listIterator();
			while (a.hasNext()) {
				Arista<E> aTemp = a.next();
				Vertice<E> temp = aTemp.getDestino();

				if (temp.getDistance() > aTemp.getPeso()) { // Condición adicional.
					temp.setAnterior(v);
					temp.setDistance(temp.getAnterior().getDistance() + aTemp.getPeso());
					camino.add(temp);
				}
				if (temp.getAnterior() == null) {
					temp.setAnterior(v);
					temp.setDistance(temp.getAnterior().getDistance() + aTemp.getPeso());
					camino.add(temp);
				}
			}
		}

		Stack<Vertice<E>> stack = new Stack<Vertice<E>>();
		Vertice<E> v = destino;
		stack.add(v);
		while (v.getAnterior() != null) {
			v = v.getAnterior();
			stack.add(v);
		}
		return stack;
	}

	public Stack<Vertice<E>> dijkstra2(Vertice<E> inicio, Vertice<E> destino) {
		// Parecido al método dijkstra1 pero sin la condición que evalúa si el anterior
		// es
		// null ya que siempre revisa si tiene un peso menor.
		inicializarVertices();
		Queue<Vertice<E>> camino = new LinkedList<Vertice<E>>();
		inicio.setDistance(0);
		camino.add(inicio);

		while (!camino.isEmpty()) {
			Vertice<E> v = camino.poll();
			LinkedList<Arista<E>> adyacentes = v.getAdyacentes();

			for (Arista<E> aTemp : adyacentes) {
				Vertice<E> temp = aTemp.getDestino();
				double newDistance = v.getDistance() + aTemp.getPeso();

				if (newDistance < temp.getDistance()) {
					temp.setAnterior(v);
					temp.setDistance(newDistance);
					camino.add(temp);
				}
			}
		}

		Stack<Vertice<E>> stack = new Stack<Vertice<E>>();
		Vertice<E> v = destino;
		stack.push(v);

		while (v.getAnterior() != null) {
			v = v.getAnterior();
			stack.push(v);
		}

		return stack;
	}

	public Stack<Vertice<E>> dijkstra(Vertice<E> inicio, Vertice<E> destino) {
		// Método final, con método que crea camino por fuera y cola de prioridad para
		// que siempre agregue el camino de menor ruta
		inicializarVertices();
		PriorityQueue<Vertice<E>> queue = new PriorityQueue<>(Comparator.comparing(Vertice::getDistance));
		inicio.setDistance(0);
		queue.add(inicio);

		while (!queue.isEmpty()) {
			Vertice<E> v = queue.poll();
			LinkedList<Arista<E>> adyacentes = v.getAdyacentes();
			ListIterator<Arista<E>> a = adyacentes.listIterator();
			if (v.compareTo(destino) == 0) {
				return construirCamino(destino);
			}
			while (a.hasNext()) {
				Arista<E> aTemp = a.next();
				Vertice<E> temp = aTemp.getDestino();
				double newDistance = v.getDistance() + aTemp.getPeso();
				if (newDistance < temp.getDistance()) {
					temp.setAnterior(v);
					temp.setDistance(newDistance);
					queue.add(temp);
				}

			}
		}

		return new Stack<>(); // En caso de que no encuentre camino :D
	}
	// Se pone así ya que la cola de prioridad se encarga de comparar y ver si vale
	// la pena agregar.

	/*
	 * Al ser cola de prioridad me ahorro este if if (newDistance <
	 * temp.getDistance()){ temp.setAnterior(v); temp.setDistance(newDistance);
	 * queue.add(temp); }
	 */

	private Stack<Vertice<E>> construirCamino(Vertice<E> destino) { // Crear el camino aparte para mejor lectura.
		Stack<Vertice<E>> stack = new Stack<>();
		Vertice<E> v = destino;

		while (v != null) {
			stack.push(v);
			v = v.getAnterior();
		}

		return stack;
	}

	public void imprimirCamino(Stack<Vertice<E>> s) {
		while (!s.isEmpty()) {
			Vertice<E> v = s.pop();
			System.out.print(v.getInfo() + "\s");
		}
		System.out.println();
	}

	public Grafo() {
		this.vertices = new LinkedList<Vertice<E>>();
	}

	public LinkedList<Vertice<E>> getVertices() {
		return vertices;
	}

	public void setVertices(LinkedList<Vertice<E>> vertices) {
		this.vertices = vertices;
	}

	public static void main(String[] args) {
		Vertice<Integer> v1 = new Vertice<Integer>(1);
		Vertice<Integer> v2 = new Vertice<Integer>(2);
		Vertice<Integer> v3 = new Vertice<Integer>(3);
		Vertice<Integer> v4 = new Vertice<Integer>(4);
		Vertice<Integer> v5 = new Vertice<Integer>(5);
		Vertice<Integer> v6 = new Vertice<Integer>(6);
		Vertice<Integer> v7 = new Vertice<Integer>(7);
		Vertice<Integer> v8 = new Vertice<Integer>(8);

		Arista<Integer> a12 = new Arista<Integer>(v2, 3);
		Arista<Integer> a13 = new Arista<Integer>(v3, 10);
		Arista<Integer> a14 = new Arista<Integer>(v4, 7);
		Arista<Integer> a25 = new Arista<Integer>(v5, 8);
		Arista<Integer> a24 = new Arista<Integer>(v4, 2);
		Arista<Integer> a36 = new Arista<Integer>(v6, 5);
		Arista<Integer> a47 = new Arista<Integer>(v7, 3);
		Arista<Integer> a57 = new Arista<Integer>(v7, 1);
		Arista<Integer> a67 = new Arista<Integer>(v7, 1);
		Arista<Integer> a46 = new Arista<Integer>(v6, 1);
		Arista<Integer> a82 = new Arista<Integer>(v2, 1);
		Arista<Integer> a87 = new Arista<Integer>(v7, 1);

		v1.getAdyacentes().add(a12);
		v1.getAdyacentes().add(a14);
		v1.getAdyacentes().add(a13);

		v2.getAdyacentes().add(a25);
		v2.getAdyacentes().add(a24);

		v3.getAdyacentes().add(a36);

		v4.getAdyacentes().add(a47);
		v4.getAdyacentes().add(a46);

		v5.getAdyacentes().add(a57);

		v6.getAdyacentes().add(a67);

		v8.getAdyacentes().add(a82);
		v8.getAdyacentes().add(a87);

		Grafo<Integer> grafo = new Grafo<Integer>();

		grafo.getVertices().add(v1);
		grafo.getVertices().add(v2);
		grafo.getVertices().add(v3);
		grafo.getVertices().add(v4);
		grafo.getVertices().add(v5);
		grafo.getVertices().add(v6);
		grafo.getVertices().add(v7);
		grafo.getVertices().add(v8);

		/*
		 * Caminos para probar: 1->4 1->6 2->7 4->7
		 */

		try {
			System.out.println("Sin Dijkstra");
			grafo.imprimirCamino(grafo.MenorCaminoSinPesos(v1, v4));
			grafo.imprimirCamino(grafo.MenorCaminoSinPesos(v1, v6));
			grafo.imprimirCamino(grafo.MenorCaminoSinPesos(v2, v7));
			grafo.imprimirCamino(grafo.MenorCaminoSinPesos(v4, v7));
		} catch (ExcepcionVertice e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Con Dijkstra");
		grafo.imprimirCamino(grafo.dijkstra(v1, v4));
		grafo.imprimirCamino(grafo.dijkstra(v1, v6));
		grafo.imprimirCamino(grafo.dijkstra(v2, v7));
		grafo.imprimirCamino(grafo.dijkstra(v4, v7));
	}

}
