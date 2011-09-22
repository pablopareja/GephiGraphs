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
import com.era7.lib.bioinfoxml.gexf.viz.VizPositionXML;
import com.era7.lib.bioinfoxml.gexf.viz.VizSizeXML;
import com.era7.lib.bioinfoxml.go.GoAnnotationXML;
import com.era7.lib.bioinfoxml.go.GoTermXML;
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
public class GeneraGexfGoAnnotation {

    public static int edgesIdCounter = 0;
    public static double proteinSizeValue = 10.0;
    public static double goSizeValue = 5.0;

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("This program expects two parameters: \n"
                    + "1. XML input file with go annotations\n"
                    + "2. Output gexf filename\n");
        } else {

            VizColorXML proteinColor = new VizColorXML();
            proteinColor.setR(241);
            proteinColor.setG(134);
            proteinColor.setB(21);
            proteinColor.setA(255);

            VizSizeXML proteinSize = new VizSizeXML();
            proteinSize.setValue(proteinSizeValue);

            VizColorXML goColor = new VizColorXML();
            goColor.setR(21);
            goColor.setG(155);
            goColor.setB(241);
            goColor.setA(243);

            VizSizeXML goSize = new VizSizeXML();
            goSize.setValue(goSizeValue);

            BufferedReader inBuff = null;
            BufferedWriter outBuff = null;
            try {


                inBuff = new BufferedReader(new FileReader(new File(args[0])));

                String line = null;
                StringBuilder inStBuilder = new StringBuilder();
                while ((line = inBuff.readLine()) != null) {
                    inStBuilder.append(line);
                }
                GoAnnotationXML goAnnotationXML = new GoAnnotationXML(inStBuilder.toString());
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


                List<GoTermXML> goTerms = goAnnotationXML.getAnnotatorGoTerms();

                Element proteinAnnotations = goAnnotationXML.getProteinAnnotations();
                List<Element> proteins = proteinAnnotations.getChildren(ProteinXML.TAG_NAME);
                
                //System.out.println("proteins.size() = " + proteins.size());

                //-----go terms-------------
                for (GoTermXML goTerm : goTerms) {
                    NodeXML nodeXML = new NodeXML();
                    nodeXML.setId(goTerm.getId());
                    nodeXML.setLabel(goTerm.getGoName());
                    nodeXML.setColor(new VizColorXML((Element) goColor.asJDomElement().clone()));
                    //nodeXML.setSize(new VizSizeXML((Element) goSize.asJDomElement().clone()));

                    //---------size---------------------
//                    VizSizeXML goSize = new VizSizeXML();
//                    goSize.setValue(goTerm.getAnnotationsCount());
                    nodeXML.setSize(new VizSizeXML((Element) goSize.asJDomElement().clone()));

                    //---------position--------------------
                    nodeXML.setPosition(new VizPositionXML(0, 0, 0));

                    AttValuesXML attValuesXML = new AttValuesXML();

                    AttValueXML goIdAttValueXML = new AttValueXML();
                    goIdAttValueXML.setFor(0);
                    goIdAttValueXML.setValue(goTerm.getId());
                    attValuesXML.addAttValue(goIdAttValueXML);

                    AttValueXML goNameAttValueXML = new AttValueXML();
                    goNameAttValueXML.setFor(1);
                    goNameAttValueXML.setValue(goTerm.getName());
                    attValuesXML.addAttValue(goNameAttValueXML);

                    AttValueXML aspectAttValue = new AttValueXML();
                    aspectAttValue.setFor(2);
                    aspectAttValue.setValue(goTerm.getAspect());
                    attValuesXML.addAttValue(aspectAttValue);

                    nodeXML.setAttvalues(attValuesXML);

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
                    //---------position--------------------
                    nodeXML.setPosition(new VizPositionXML(0, 0, 0));
                    
                    AttValuesXML attValuesXML = new AttValuesXML();

                    AttValueXML proteinIdAttValueXML = new AttValueXML();
                    proteinIdAttValueXML.setFor(0);
                    proteinIdAttValueXML.setValue(proteinXML.getId());
                    attValuesXML.addAttValue(proteinIdAttValueXML);

                    AttValueXML proteinAspectAttValueXML = new AttValueXML();
                    proteinAspectAttValueXML.setFor(2);
                    proteinAspectAttValueXML.setValue(proteinXML.getName());
                    attValuesXML.addAttValue(proteinAspectAttValueXML);

                    nodeXML.setAttvalues(attValuesXML);

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
