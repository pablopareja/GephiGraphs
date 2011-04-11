/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.gephi;

import com.era7.lib.bioinfoxml.ProteinXML;
import com.era7.lib.bioinfoxml.gexf.AttValueXML;
import com.era7.lib.bioinfoxml.gexf.AttValuesXML;
import com.era7.lib.bioinfoxml.gexf.AttributeXML;
import com.era7.lib.bioinfoxml.gexf.AttributesXML;
import com.era7.lib.bioinfoxml.gexf.EdgeXML;
import com.era7.lib.bioinfoxml.gexf.GexfXML;
import com.era7.lib.bioinfoxml.gexf.GraphXML;
import com.era7.lib.bioinfoxml.gexf.NodeXML;
import com.era7.lib.bioinfoxml.gexf.viz.VizColorXML;
import com.era7.lib.bioinfoxml.gexf.viz.VizSizeXML;
import com.era7.lib.bioinfoxml.go.GOSlimXML;
import com.era7.lib.bioinfoxml.go.GoTermXML;
import com.era7.lib.era7xmlapi.model.XMLElement;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Element;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class GenerateGexfGoSlim {

    public static int MAX_NODE_SIZE = 200;
    public static int edgesIdCounter = 0;
    public static double proteinSizeValue = 10.0;
    public static double goSizeValue = 5.0;

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("This program expects two parameters: \n"
                    + "1. GoSlim xml file\n"
                    + "2. Gexf output filename\n");
        } else {

            VizColorXML proteinColor = new VizColorXML();
            proteinColor.setR(73);
            proteinColor.setG(112);
            proteinColor.setB(98);
            proteinColor.setA(255);

            VizSizeXML proteinSize = new VizSizeXML();
            proteinSize.setValue(proteinSizeValue);

            VizColorXML goColor = new VizColorXML();
            goColor.setR(21);
            goColor.setG(155);
            goColor.setB(241);
            goColor.setA(243);

            VizColorXML bioColor = new VizColorXML();
            bioColor.setR(241);
            bioColor.setG(134);
            bioColor.setB(21);
            bioColor.setA(255);
            VizColorXML molColor = new VizColorXML();
            molColor.setR(21);
            molColor.setG(155);
            molColor.setB(241);
            molColor.setA(243);
            VizColorXML cellColor = new VizColorXML();
            cellColor.setR(34);
            cellColor.setG(177);
            cellColor.setB(76);
            cellColor.setA(243);

//            VizSizeXML goSize = new VizSizeXML();
//            goSize.setValue(goSizeValue);

            BufferedReader inBuff = null;
            BufferedWriter outBuff = null;
            try {


                inBuff = new BufferedReader(new FileReader(new File(args[0])));

                String line = null;
                StringBuilder inStBuilder = new StringBuilder();
                while ((line = inBuff.readLine()) != null) {
                    inStBuilder.append(line);
                }
                GOSlimXML goSlimXML = new GOSlimXML(inStBuilder.toString());
                inStBuilder.delete(0, inStBuilder.length());

                outBuff = new BufferedWriter(new FileWriter(new File(args[1])));

                outBuff.write("<?xml version=\"1.0\" encoding=\"UTF8\"?>" + "\n");
                outBuff.write("<" + GexfXML.TAG_NAME + ">\n");
                outBuff.write("<" + GraphXML.TAG_NAME + " defaultedgetype=\"directed\">\n");

                //GexfXML gexfXML = new GexfXML();

//                GraphXML graphXML = new GraphXML();
//                graphXML.setDefaultEdgeType(GraphXML.DIRECTED_EDGE_TYPE);
//                gexfXML.setGraph(graphXML);

                //node attributes
                AttributesXML attributesXML = new AttributesXML();
                attributesXML.setClass(AttributesXML.NODE_CLASS);
                AttributeXML idAttributeXML = new AttributeXML();
                idAttributeXML.setId("0");
                idAttributeXML.setTitle("ID");
                idAttributeXML.setType("string");
                attributesXML.addAttribute(idAttributeXML);
                AttributeXML nameAttributeXML = new AttributeXML();
                nameAttributeXML.setId("1");
                nameAttributeXML.setTitle("Name");
                nameAttributeXML.setType("string");
                attributesXML.addAttribute(nameAttributeXML);
                AttributeXML aspectAttributeXML = new AttributeXML();
                aspectAttributeXML.setId("2");
                aspectAttributeXML.setTitle("Aspect");
                aspectAttributeXML.setType("string");
                attributesXML.addAttribute(aspectAttributeXML);

                outBuff.write(attributesXML.toString() + "\n");

                //graphXML.addAttributes(attributesXML);


//                NodesXML nodesXML = new NodesXML();
//                EdgesXML edgesXML = new EdgesXML();

                StringBuilder nodesXMLStBuilder = new StringBuilder("<nodes>\n");
                StringBuilder edgesXMLStBuilder = new StringBuilder("<edges>\n");

//                graphXML.setNodes(nodesXML);
//                graphXML.setEdges(edgesXML);
                //outBuff.write(gexfXML.toString());


                List<XMLElement> goSlimTerms = goSlimXML.getSlimSet().getChildrenWith(GoTermXML.TAG_NAME);
                Element proteinAnnotations = goSlimXML.asJDomElement().getChild("proteins");
                List<Element> proteins = proteinAnnotations.getChildren(ProteinXML.TAG_NAME);

                //-----go terms-------------
                for (XMLElement goTermElem : goSlimTerms) {
                    GoTermXML goTerm = new GoTermXML(goTermElem.asJDomElement());

                    NodeXML nodeXML = new NodeXML();
                    nodeXML.setId(goTerm.getId());
                    nodeXML.setLabel(goTerm.getGoName());

                    AttValuesXML attValues = new AttValuesXML();
                    AttValueXML aspectAttValue = new AttValueXML();
                    aspectAttValue.setFor(2);
                    aspectAttValue.setValue(goTerm.getAspect());
                    attValues.addAttValue(aspectAttValue);
                    nodeXML.setAttvalues(attValues);

                    if (goTerm.getAspect().equals(GoTermXML.ASPECT_PROCESS)) {
                        nodeXML.setColor(new VizColorXML((Element) bioColor.getRoot().clone()));
                    } else if (goTerm.getAspect().equals(GoTermXML.ASPECT_FUNCTION)) {
                        nodeXML.setColor(new VizColorXML((Element) molColor.getRoot().clone()));
                    } else if (goTerm.getAspect().equals(GoTermXML.ASPECT_COMPONENT)) {
                        nodeXML.setColor(new VizColorXML((Element) cellColor.getRoot().clone()));
                    }
                    //nodeXML.setSize(new VizSizeXML((Element) goSize.asJDomElement().clone()));
                    VizSizeXML goSize = new VizSizeXML();
                    goSize.setValue(goTerm.getAnnotationsCount() * MAX_NODE_SIZE / proteins.size());
                    nodeXML.setSize(goSize);
                    nodesXMLStBuilder.append((nodeXML.toString() + "\n"));
                }

                //-----------proteins-------------
                for (Element protElem : proteins) {
                    ProteinXML proteinXML = new ProteinXML(protElem);
                    NodeXML nodeXML = new NodeXML();
                    nodeXML.setId(proteinXML.getId());
                    nodeXML.setLabel(proteinXML.getId());
                    nodeXML.setColor(new VizColorXML((Element) proteinColor.asJDomElement().clone()));
                    nodeXML.setSize(new VizSizeXML((Element) proteinSize.asJDomElement().clone()));
                    nodesXMLStBuilder.append((nodeXML.toString() + "\n"));

                    //----edges----
                    List<GoTermXML> proteinTerms = new ArrayList<GoTermXML>();
                    List<GoTermXML> bioTerms = proteinXML.getBiologicalProcessGoTerms();
                    List<GoTermXML> cellTerms = proteinXML.getCellularComponentGoTerms();
                    List<GoTermXML> molTerms = proteinXML.getMolecularFunctionGoTerms();
                    if (bioTerms != null) {
                        proteinTerms.addAll(bioTerms);
                    }
                    if (cellTerms != null) {
                        proteinTerms.addAll(cellTerms);
                    }
                    if (molTerms != null) {
                        proteinTerms.addAll(molTerms);
                    }

                    for (GoTermXML goTermXML : proteinTerms) {
                        EdgeXML edge = new EdgeXML();
                        edge.setId(String.valueOf(edgesIdCounter++));
                        edge.setTarget(proteinXML.getId());
                        edge.setSource(goTermXML.getId());
                        edge.setType(EdgeXML.DIRECTED_TYPE);

                        edgesXMLStBuilder.append((edge.toString() + "\n"));
                    }

                }


                outBuff.write(nodesXMLStBuilder.toString() + "</nodes>\n");
                outBuff.write(edgesXMLStBuilder.toString() + "</edges>\n");

                outBuff.write("</" + GraphXML.TAG_NAME + ">\n");
                outBuff.write("</" + GexfXML.TAG_NAME + ">\n");
                outBuff.close();

                System.out.println("done!! :)");


            } catch (IOException ex) {
                Logger.getLogger(GeneraGexfGoAnnotation.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
