package gui;

import java.io.IOException;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultListenableGraph;


import editor.SchemaEditor;
import editor.SchemaEditorMenuBar;

public class GraphHandler {

	public static void displayXchema(OdataClientHandler odataHandler) {
		try {
			Edm edm = odataHandler.readEdm();
			EdmGraphManager edmManager = new EdmGraphManager();
			DefaultListenableGraph<EdmEntityType, DefaultEdge> graph = edmManager.createEdmGraph(edm);
					JGraphXAdapterDisplayer<EdmEntityType, DefaultEdge> adapter = JGraphXAdapterDisplayer.getAdapter(graph);
					SchemaEditor editor = new SchemaEditor(adapter.getJgxAdapter());
					editor.createFrame(new SchemaEditorMenuBar(editor)).setVisible(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
				
	}

}
