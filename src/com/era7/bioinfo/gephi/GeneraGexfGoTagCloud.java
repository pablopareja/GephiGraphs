/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.era7.bioinfo.gephi;

import com.era7.lib.bioinfoxml.ProteinXML;
import com.era7.lib.bioinfoxml.gexf.EdgeXML;
import com.era7.lib.bioinfoxml.gexf.GexfXML;
import com.era7.lib.bioinfoxml.gexf.GraphXML;
import com.era7.lib.bioinfoxml.gexf.NodeXML;
import com.era7.lib.bioinfoxml.gexf.viz.VizColorXML;
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
public class GeneraGexfGoTagCloud {

    public static int edgesIdCounter = 0;    

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("El programa espera dos parametros: \n"
                    + "1. Nombre del archivo de entrada xml con las anotaciones de go\n"
                    + "2. Nombre del archivo de salida gefx\n");
        } else {

            VizColorXML goColor = new VizColorXML();
            goColor.setR(21);
            goColor.setG(155);
            goColor.setB(241);
            goColor.setA(243);

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


                StringBuilder nodesXMLStBuilder = new StringBuilder("<nodes>\n");
                StringBuilder edgesXMLStBuilder = new StringBuilder("<edges>\n");

                List<GoTermXML> goTerms = goAnnotationXML.getAnnotatorGoTerms();

                //-----go terms-------------
                for (GoTermXML goTerm : goTerms) {
                    NodeXML nodeXML = new NodeXML();
                    nodeXML.setId(goTerm.getId());
                    nodeXML.setLabel(goTerm.getGoName());
                    nodeXML.setColor(new VizColorXML((Element) goColor.asJDomElement().clone()));
                    VizSizeXML goSize = new VizSizeXML();
                    goSize.setValue(goTerm.getAnnotationsCount());
                    nodeXML.setSize(goSize);
                    nodesXMLStBuilder.append((nodeXML.toString() + "\n"));
                }

                EdgeXML edge = new EdgeXML();
                edge.setId(String.valueOf(edgesIdCounter++));
                edge.setTarget("GO:0003904");
                edge.setSource("GO:0003887");
                edge.setType(EdgeXML.DIRECTED_TYPE);

                edgesXMLStBuilder.append((edge.toString() + "\n"));
                

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
