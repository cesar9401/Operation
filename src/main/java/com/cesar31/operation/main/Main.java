package com.cesar31.operation.main;

import com.cesar31.operation.ast.Operation;
import com.cesar31.operation.parser.*;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import java.io.*;
import java.util.*;

/**
 *
 * @author cesar31
 */
public class Main {

    public static void main(String[] args) {
        // String in = "10 + 2 * 1 + 4";
        // StringReader reader = new StringReader(in);
        // OperationParser parser = new OperationParser(reader);
        OperationParser parser = new OperationParser(System.in);
        Operation ast;
        try {
            ast = parser.Start();
            Integer result = (Integer) ast.run();
            System.out.println("\nResultado: " + result + "\n");
            getDot(ast);
        } catch (ParseException ex) {
            ex.printStackTrace(System.out);
        }
    }

    /**
     * obtener string con instrucciones para generar pgn del arbol
     *
     * @param node
     */
    public static void getDot(Operation node) {
        if (node != null) {
            String dot = "digraph ast {\n";
            dot += "label = \"" + postOrden(node) + "\";\n";
            dot += dot(node);
            dot += "}\n";

            /*
            writeDot("tree.dot", dot);
            try {
                execComand("dot -Tpng tree.dot -o tree.png");
            } catch (IOException ex) {
                ex.printStackTrace(System.out);
            }
             */
            generateGraph(dot);
        }
    }

    /**
     * Informaciond de nodos para instruccion dot
     *
     * @param node
     * @return
     */
    public static String dot(Operation node) {
        if (node != null) {
            // Label
            String label = node.getType().equals("NUM") ? String.valueOf(node.getValue()) : node.getType();
            String dot = "node" + node.hashCode() + "[ label = \"" + label + "\"];\n";
            dot += node.getLeft() != null ? "node" + node.hashCode() + " -> node" + node.getLeft().hashCode() + ";\n" : "";
            dot += node.getRight() != null ? "node" + node.hashCode() + " -> node" + node.getRight().hashCode() + ";\n" : "";

            // Hijos
            dot += dot(node.getLeft());
            dot += dot(node.getRight());
            return dot;
        }

        return "";
    }

    /**
     * Recorrido postOrden
     *
     * @param node
     * @return
     */
    public static String postOrden(Operation node) {
        if (node != null) {
            String label = "";

            // Left
            label += postOrden(node.getLeft());
            // Right
            label += postOrden(node.getRight());

            // visit here
            String value = node.getType().equals("NUM") ? String.valueOf(node.getValue()) : node.getType();
            label += " " + value + " ";
            return label;
        }
        return "";
    }

    /**
     * Generar imagen de arbol
     *
     * @param dot
     */
    public static void generateGraph(String dot) {
        try {
            MutableGraph g = new Parser().read(dot);
            // Graphviz.fromString(dot).width(600).render(Format.PNG).toFile(new File("tree2.png"));
            Graphviz.fromGraph(g).width(600).render(Format.PNG).toFile(new File("tree.png"));
        } catch (IOException ex) {
            ex.printStackTrace(System.out);
        }
    }

    /**
     * Escribir archivo .dot
     *
     * @param path
     * @param dot
     */
    public static void writeDot(String path, String dot) {
        File file = new File(path);
        try {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.write(dot);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace(System.out);
        }
    }

    /**
     * Ejecutar comando
     *
     * @param command
     * @throws IOException
     */
    public static void execComand(String command) throws IOException {
        List<String> cmdOutput = new ArrayList<>();
        Process p = Runtime.getRuntime().exec(command);
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String tmpLine;
        while ((tmpLine = br.readLine()) != null) {
            cmdOutput.add(tmpLine);
        }

        if (!cmdOutput.isEmpty()) {
            cmdOutput.forEach(s -> {
                System.out.println("Respuesta del sistema: " + s);
            });
        }
    }
}
