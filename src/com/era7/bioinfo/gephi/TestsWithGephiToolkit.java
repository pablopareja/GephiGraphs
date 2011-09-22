/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.gephi;

import com.itextpdf.text.PageSize;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.preview.PDFExporter;
import org.gephi.io.exporter.spi.CharacterExporter;
import org.gephi.io.exporter.spi.Exporter;
import org.gephi.io.exporter.spi.GraphExporter;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.force.yifanHu.YifanHuProportional;
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class TestsWithGephiToolkit {

    public static void main(String[] args) throws Exception {
        if (args.length != 0) {
            System.out.println("This program expects one parameter: \n"
                    + "1. XML input file with go annotations\n");
        } else {


            //Init a project - and therefore a workspace
            ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
            pc.newProject();
            Workspace workspace = pc.getCurrentWorkspace();
            ImportController importController = Lookup.getDefault().lookup(ImportController.class);

            //Import file
            Container container;
            try {
                File file = new File("pruebaGoGordo.gexf");
                container = importController.importFile(file);
                container.getLoader().setEdgeDefault(EdgeDefault.DIRECTED);   //Force DIRECTED
                container.setAllowAutoNode(false);  //Don't create missing nodes

                //Append container to graph structure
                importController.process(container, new DefaultProcessor(), workspace);

                //See if graph is well imported
                GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
                DirectedGraph graph = graphModel.getDirectedGraph();
                System.out.println("Nodes: " + graph.getNodeCount());
                System.out.println("Edges: " + graph.getEdgeCount());

                //Layout for 1 minute
                AutoLayout autoLayout = new AutoLayout(30, TimeUnit.SECONDS);
                autoLayout.setGraphModel(graphModel);
                //YifanHuLayout firstLayout = new YifanHuLayout(null, new StepDisplacement(1f));
                YifanHuLayout firstLayout = new YifanHuProportional().buildLayout();
                //ForceAtlasLayout secondLayout = new ForceAtlasLayout(null);
                //AutoLayout.DynamicProperty adjustBySizeProperty = AutoLayout.createDynamicProperty("Adjust by Sizes", Boolean.TRUE, 0.1f);//True after 10% of layout time
                //AutoLayout.DynamicProperty repulsionProperty = AutoLayout.createDynamicProperty("Repulsion strength", new Double(500.), 0f);//500 for the complete period
                autoLayout.addLayout(firstLayout, 1f);
                //autoLayout.addLayout(secondLayout, 0.5f, new AutoLayout.DynamicProperty[]{adjustBySizeProperty, repulsionProperty});
                autoLayout.execute();

                PreviewModel model = Lookup.getDefault().lookup(PreviewController.class).getModel();
                model.getNodeSupervisor().setShowNodeLabels(Boolean.TRUE);

                //Export
                ExportController ec = Lookup.getDefault().lookup(ExportController.class);
                try {
                    //ec.exportFile(new File("GoAnnotationAutomatic.pdf"));

                    //Exporter exporterGraphML = ec.getExporter("graphml");
                    //ec.exportFile(new File("GoAnnotationAutomatic.graphml"), exporterGraphML);

                    Exporter exporterGexf = ec.getExporter("gexf");
                    ec.exportFile(new File("goGordoAutomatic.gexf"), exporterGexf);

                } catch (IOException ex) {
                    ex.printStackTrace();
                    return;
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }
    }
}
