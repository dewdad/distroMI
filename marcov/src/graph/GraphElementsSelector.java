package graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultListenableGraph;

import gui.EdmGraphManager;

public class GraphElementsSelector {

	public class GraphElement {
		Object source;
		LinkedList<String> properties = new LinkedList<>();
		LinkedList<ValueElementDiscretization> valueProcessors = new LinkedList<>();

		public GraphElement(Object source, String property, ValueElementDiscretization valueProcessor) {
			super();
			this.source = source;
			properties.add(property);
			valueProcessors.add(valueProcessor);
		}

		public Object getSource() {
			return source;
		}

		public LinkedList<String> getProperties() {
			return properties;
		}

		public LinkedList<ValueElementDiscretization> getValueProcessors() {
			return valueProcessors;
		}


	}

	HashMap<Object,GraphElement> elements = new HashMap<>();
	private DefaultListenableGraph graph;

	public GraphElement addVertex(Object value, Object property,ValueElementDiscretization valueProcessor) {
		
		GraphElement element = elements.get(value);
		ValueElementDiscretization processor = valueProcessor;
		if (element == null)
			elements.put(value,new GraphElement(value, (String) property,processor));
		else {
			element.getProperties().add((String) property);
			element.getValueProcessors().add(processor);			
		}			
		return element;
	}

	public void removeVertex(Object value, Object property) {
		GraphElement graphElement = elements.get(value);
		if (graphElement != null) {		
			Iterator<String> itr = graphElement.getProperties().iterator();
			Iterator<ValueElementDiscretization> itr2 = graphElement.getValueProcessors().iterator();
			while (itr.hasNext()) {
				ValueElementDiscretization processor = itr2.next();
				if (itr.next().equals(property)) {
					graphElement.getProperties().remove(property);
					graphElement.getValueProcessors().remove(processor);
					break;
				}
			}
			if(graphElement.getProperties().size() == 0) {				
				elements.remove(value);
			}
		}
	}

	public <V, E> ListenableGraph<V, E> commit(){		
		return (ListenableGraph<V, E>) EdmGraphManager.getEdmInitialGraph(elements);	
	}

	public boolean hasSelections() {
		return !elements.isEmpty();
	}

	public List<String> getHeaders() {
		LinkedList<String> stringlist = new LinkedList<>();
		for (GraphElement element:elements.values()) {
			stringlist.addAll(element.getProperties());
		}
		return stringlist;
	}
	
	public  Set<Object> getSelectedTables() {
		return elements.keySet();
	}

	public HashMap<Object, GraphElement> getSelectedElements() {
		return elements;
	}
}

