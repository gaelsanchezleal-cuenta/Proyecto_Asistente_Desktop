/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gael.zak_asistente_virtual;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * GeneradorArchivos: crea archivos reales (.txt, .docx, .pdf, .xlsx) a partir
 * de un texto (normalmente la respuesta de Gemini) y los guarda directamente
 * en la carpeta de Descargas del usuario actual.
 *
 * @author claude
 */
public class GeneradorArchivos {

    // Carpeta de Descargas del usuario (Windows: C:\Users\<usuario>\Downloads)
    private String carpetaDescargas() {
        return System.getProperty("user.home") + File.separator + "Downloads";
    }

    // Convierte el tema dicho por voz en un nombre de archivo seguro (sin
    // acentos, espacios ni símbolos raros), agregando fecha y hora para que
    // dos documentos del mismo tema no se sobreescriban entre sí.
    private String nombreSeguro(String tema) {
        String sinAcentos = Normalizer.normalize(tema, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String limpio = sinAcentos.replaceAll("[^a-zA-Z0-9 ]", "").trim();
        if (limpio.isBlank()) {
            limpio = "documento";
        }
        limpio = limpio.replaceAll("\\s+", "_");
        if (limpio.length() > 40) {
            limpio = limpio.substring(0, 40);
        }
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return "Leal_" + limpio + "_" + fecha;
    }

    // Crea un archivo de texto plano (.txt) con el contenido dado.
    public String crearTxt(String contenido, String tema) throws IOException {
        File carpeta = new File(carpetaDescargas());
        carpeta.mkdirs();
        File archivo = new File(carpeta, nombreSeguro(tema) + ".txt");

        try (PrintWriter escritor = new PrintWriter(archivo, StandardCharsets.UTF_8)) {
            escritor.print(contenido);
        }
        return archivo.getAbsolutePath();
    }

    // Crea un documento de Word (.docx): cada línea del texto se vuelve un
    // párrafo dentro del documento.
    public String crearDocx(String contenido, String tema) throws IOException {
        File carpeta = new File(carpetaDescargas());
        carpeta.mkdirs();
        File archivo = new File(carpeta, nombreSeguro(tema) + ".docx");

        try (XWPFDocument documento = new XWPFDocument();
                FileOutputStream salida = new FileOutputStream(archivo)) {

            for (String linea : contenido.split("\n")) {
                XWPFParagraph parrafo = documento.createParagraph();
                XWPFRun texto = parrafo.createRun();
                texto.setText(linea);
                texto.setFontSize(12);
            }
            documento.write(salida);
        }
        return archivo.getAbsolutePath();
    }

    // Crea un PDF con el contenido dado, ajustando el texto en varias líneas
    // y varias páginas automáticamente si hace falta.
    public String crearPdf(String contenido, String tema) throws IOException {
        File carpeta = new File(carpetaDescargas());
        carpeta.mkdirs();
        File archivo = new File(carpeta, nombreSeguro(tema) + ".pdf");

        float margen = 50;
        float tamanoFuente = 12;
        float interlineado = 16;
        PDType1Font fuente = PDType1Font.HELVETICA;

        try (PDDocument documento = new PDDocument()) {
            PDPage pagina = new PDPage(PDRectangle.LETTER);
            documento.addPage(pagina);

            PDPageContentStream contenidoStream = new PDPageContentStream(documento, pagina);
            contenidoStream.beginText();
            contenidoStream.setFont(fuente, tamanoFuente);
            float yActual = pagina.getMediaBox().getHeight() - margen;
            contenidoStream.newLineAtOffset(margen, yActual);

            float anchoUtil = pagina.getMediaBox().getWidth() - (2 * margen);

            for (String parrafo : contenido.split("\n")) {
                for (String linea : ajustarLineas(parrafo, fuente, tamanoFuente, anchoUtil)) {
                    if (yActual <= margen) {
                        // Ya no cabe más texto: cerramos esta página y abrimos otra
                        contenidoStream.endText();
                        contenidoStream.close();

                        pagina = new PDPage(PDRectangle.LETTER);
                        documento.addPage(pagina);
                        contenidoStream = new PDPageContentStream(documento, pagina);
                        contenidoStream.beginText();
                        contenidoStream.setFont(fuente, tamanoFuente);
                        yActual = pagina.getMediaBox().getHeight() - margen;
                        contenidoStream.newLineAtOffset(margen, yActual);
                    }

                    try {
                        contenidoStream.showText(linea);
                    } catch (IllegalArgumentException iae) {
                        // Algún carácter no es compatible con la fuente estándar del
                        // PDF; lo reemplazamos para no detener todo el documento.
                        String lineaSegura = linea.replaceAll("[^\\x00-\\xFF]", "?");
                        contenidoStream.showText(lineaSegura);
                    }
                    contenidoStream.newLineAtOffset(0, -interlineado);
                    yActual -= interlineado;
                }
            }
            contenidoStream.endText();
            contenidoStream.close();

            documento.save(archivo);
        }
        return archivo.getAbsolutePath();
    }

    // Divide un párrafo largo en varias líneas que sí quepan dentro del
    // ancho disponible de la página del PDF.
    private List<String> ajustarLineas(String parrafo, PDType1Font fuente, float tamano, float anchoMaximo) throws IOException {
        List<String> lineas = new ArrayList<>();
        if (parrafo.isBlank()) {
            lineas.add("");
            return lineas;
        }
        String[] palabras = parrafo.split(" ");
        StringBuilder lineaActual = new StringBuilder();

        for (String palabra : palabras) {
            String prueba = lineaActual.isEmpty() ? palabra : lineaActual + " " + palabra;
            float ancho = fuente.getStringWidth(prueba) / 1000 * tamano;
            if (ancho > anchoMaximo && !lineaActual.isEmpty()) {
                lineas.add(lineaActual.toString());
                lineaActual = new StringBuilder(palabra);
            } else {
                lineaActual = new StringBuilder(prueba);
            }
        }
        if (!lineaActual.isEmpty()) {
            lineas.add(lineaActual.toString());
        }
        return lineas;
    }

    // Crea un Excel (.xlsx). Cada línea del texto se vuelve una fila; si la
    // línea trae comas, cada parte se reparte en columnas distintas (así,
    // si Gemini responde con datos tipo tabla, sí quedan en celdas separadas).
    public String crearXlsx(String contenido, String tema) throws IOException {
        File carpeta = new File(carpetaDescargas());
        carpeta.mkdirs();
        File archivo = new File(carpeta, nombreSeguro(tema) + ".xlsx");

        try (XSSFWorkbook libro = new XSSFWorkbook();
                FileOutputStream salida = new FileOutputStream(archivo)) {

            XSSFSheet hoja = libro.createSheet("Leal");
            String[] lineas = contenido.split("\n");

            for (int i = 0; i < lineas.length; i++) {
                Row fila = hoja.createRow(i);
                String[] columnas = lineas[i].split(",");
                for (int j = 0; j < columnas.length; j++) {
                    Cell celda = fila.createCell(j);
                    celda.setCellValue(columnas[j].trim());
                }
            }

            for (int j = 0; j < 10; j++) {
                hoja.autoSizeColumn(j);
            }

            libro.write(salida);
        }
        return archivo.getAbsolutePath();
    }
}
