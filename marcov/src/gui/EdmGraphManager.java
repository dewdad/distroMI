package gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientEntitySetIterator;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.EdmNamed;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.Multigraph;

import graph.GraphElementsSelector;
import graph.GraphElementsSelector.GraphElement;


public class EdmGraphManager {

	public static class RelationshipEdge<V> extends DefaultEdge {
		private V v1;
		private V v2;
		private String label;

		public RelationshipEdge(V v1, V v2, String label) {
			this.v1 = v1;
			this.v2 = v2;
			this.label = label;
		}

		public V getV1() {
			return v1;
		}

		public V getV2() {
			return v2;
		}

		public String toString() {
			return label;
		}
	}
	
	private static Edm edm;


	public DefaultListenableGraph<EdmEntityType, DefaultEdge> createEdmGraph(Edm anEdm) {

		DefaultListenableGraph<EdmEntityType,DefaultEdge> graph = new DefaultListenableGraph<>(new DefaultDirectedGraph<>(DefaultEdge.class));		
        edm = anEdm;
		List<FullQualifiedName> ctFqns = new ArrayList<FullQualifiedName>();
		List<FullQualifiedName> etFqns = new ArrayList<FullQualifiedName>();
		for (EdmSchema schema : anEdm.getSchemas()) {
			for (EdmComplexType complexType : schema.getComplexTypes()) {
				ctFqns.add(complexType.getFullQualifiedName());
			}
			for (EdmEntityType entityType : schema.getEntityTypes()) {
				etFqns.add(entityType.getFullQualifiedName());			
				//graph.addVertex(entityType.getName());
				graph.addVertex(entityType);	
			}

			for (EdmEntityType entityType : schema.getEntityTypes()) {			
				for(String reference:entityType.getNavigationPropertyNames()) {
					EdmNavigationProperty relationship = entityType.getNavigationProperty(reference);
					//					graph.addEdge(entityType.getName(),relationship.getType().getName());
					graph.addEdge(entityType,relationship.getType(),new RelationshipEdge<>(entityType, relationship.getType(), relationship.getName()));
				}
			}
		}

		//	print("Found ComplexTypes", ctFqns);
		//	print("Found EntityTypes", etFqns);
		//
		//	print("\n----- Inspect each property and its type of the first entity: " + etFqns.get(0) + "----");
		//	EdmEntityType etype = anEdm.getEntityType(etFqns.get(0));
		//	for (String propertyName : etype.getPropertyNames()) {
		//		EdmProperty property = etype.getStructuralProperty(propertyName);
		//		FullQualifiedName typeName = property.getType().getFullQualifiedName();
		//		print("property '" + propertyName + "' " + typeName);
		//	}
		return graph;

	}


	public static ListenableGraph<EdmElement, DefaultEdge> getEdmInitialGraph(HashMap<Object, GraphElement> elements) {
		DefaultListenableGraph<EdmElement,DefaultEdge> graph = new DefaultListenableGraph<>(new Multigraph<EdmElement, DefaultEdge>(DefaultEdge.class));
		if (edm != null) {			
			for (GraphElement element : elements.values()) {
				for (String elementproperty: element.getProperties()) {
//					try {
//						ClientEntitySetIterator<ClientEntitySet, ClientEntity> propertyData = OdataClientHandler.getClientHandler(null,null,null).readEntities(edm, elementproperty);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					graph.addVertex(((EdmEntityType) element.getSource()).getProperty(elementproperty));
				}
			}
			return graph;			
		}
		return null;
	}


	public static ClientEntitySetIterator<ClientEntitySet, ClientEntity> queryDataBase(HashMap<Object, GraphElement>selectedElements) {
		ClientEntitySetIterator<ClientEntitySet,ClientEntity> response = null;
		if (edm != null) {			 
			for (GraphElement element : selectedElements.values()) {
				EdmEntityType elementType = (EdmEntityType) element.getSource();
				try {
					String elementName = "";
					EdmEntityContainer entityContainer = edm.getEntityContainer();
					if (elementType.getBaseType() != null) {
					for (EdmEntitySet entityset:entityContainer.getEntitySets())
						for(EdmNavigationPropertyBinding navBindings:entityset.getNavigationPropertyBindings()) {
							if (navBindings.getPath().equals (elementType.getBaseType().getName())) {
								elementName =elementName.concat(navBindings.getTarget());
								break;
							}
						}
					elementName = elementName.concat("/");
					elementName = elementName.concat(elementType.getFullQualifiedName().getFullQualifiedNameAsString());
					}else {
						elementName = elementName.concat(elementType.getName());
					}
					response = OdataClientHandler.getClientHandler(true, null, null, null).readEntitiesWithSelect(edm, elementName, element.getProperties());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}

	
}
