/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gael.zak_asistente_virtual;

import java.net.http.HttpClient;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

// ------------------------------------------ COMPLETO POR CLOUDE: ----------------------------------------------------

public class PreguntaleGemini {

    // gemini-2.5-flash ya fue retirado para nuevos usuarios (según el propio
    // error 404 de la API); modelo vigente indicado por Google:
    private static final String MODELO = "gemini-3.6-flash";

    protected String apiKey;
    protected HttpClient http;

    public PreguntaleGemini() {
        this.apiKey = cargarApiKey();
        this.http = HttpClient.newHttpClient();
    }

    private String cargarApiKey() {
        Properties propiedades = new Properties();
        // Debe existir en src/main/resources/config.properties para que el classloader lo encuentre
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("Error: No se encontro config.properties (debe estar en src/main/resources)");
                return null;
            }
            propiedades.load(input);
            return propiedades.getProperty("gemini.api.key");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // Antes se llamaba "ConsultarGemini", era "void" y tenia "return" de String (no compilaba).
    // Ahora SI devuelve el texto de la respuesta, listo para imprimir.
    public String consultarGemini(String pregunta) {
        if (this.apiKey == null || this.apiKey.isBlank()) {
            return "Error: la API Key de Gemini no esta configurada en config.properties.";
        }

        String urlCompleta = "https://generativelanguage.googleapis.com/v1beta/models/"
                + MODELO + ":generateContent?key=" + this.apiKey;

        // Escapamos la pregunta para no romper el JSON si trae comillas, saltos de linea, etc.
        String cuerpoJson = "{\"contents\": [{\"parts\":[{\"text\": \"" + escaparJson(pregunta) + "\"}]}]}";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlCompleta))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(cuerpoJson))
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Gemini respondio codigo " + response.statusCode() + ": " + response.body());
                return "Gemini devolvio un error (codigo " + response.statusCode()
                        + "). Revisa que la API Key sea valida y tenga cuota disponible.";
            }

            String texto = extraerTexto(response.body());
            return texto != null ? texto : "No pude interpretar la respuesta de Gemini.";

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error al conectar con Gemini: " + e.getMessage();
        }
    }

    // Pide a Gemini un texto más largo y estructurado, pensado para
    // insertarse directo en un documento (Word/PDF/Excel/texto), sin usar
    // markdown ni símbolos de formato que no se verían bien en esos archivos.
    public String consultarGeminiParaDocumento(String tema) {
        String prompt = "Escribe un documento completo, claro y bien organizado sobre el siguiente tema. "
                + "No uses markdown (nada de asteriscos, numerales, guiones de lista ni negritas con simbolos), "
                + "porque el texto se va a insertar directo en un archivo de Word, PDF o Excel. "
                + "Usa saltos de linea para separar parrafos. Tema: " + tema;
        return consultarGemini(prompt);
    }

    // Escapa caracteres especiales para poder incrustar texto libre dentro de un string JSON
    private String escaparJson(String texto) {
        StringBuilder resultado = new StringBuilder();
        for (char c : texto.toCharArray()) {
            switch (c) {
                case '"':
                    resultado.append("\\\"");
                    break;
                case '\\':
                    resultado.append("\\\\");
                    break;
                case '\n':
                    resultado.append("\\n");
                    break;
                case '\r':
                    resultado.append("\\r");
                    break;
                case '\t':
                    resultado.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        resultado.append(String.format("\\u%04x", (int) c));
                    } else {
                        resultado.append(c);
                    }
            }
        }
        return resultado.toString();
    }

    // Saca el primer campo "text": "..." de la respuesta JSON de Gemini.
    // IMPORTANTE: antes esto usaba una regex tipo "((?:\\.|[^"\\])*)", pero el
    // motor de regex de Java maneja el cuantificador * dentro de grupos con
    // RECURSIÓN, no con un bucle. Con respuestas largas de Gemini (cientos o
    // miles de caracteres) esto agotaba la pila y tiraba un StackOverflowError.
    // Por eso aquí se hace el parseo a mano, con un bucle simple (sin recursión).
    private String extraerTexto(String jsonRespuesta) {
        String clave = "\"text\"";
        int idxClave = jsonRespuesta.indexOf(clave);
        if (idxClave == -1) {
            return null;
        }

        // La primera comilla después de la clave "text" es la comilla de
        // apertura del valor (antes solo hay ":" y espacios en blanco)
        int inicio = jsonRespuesta.indexOf('"', idxClave + clave.length());
        if (inicio == -1) {
            return null;
        }
        inicio++; // nos movemos justo después de esa comilla de apertura

        StringBuilder valorBruto = new StringBuilder();
        int i = inicio;
        while (i < jsonRespuesta.length()) {
            char c = jsonRespuesta.charAt(i);
            if (c == '\\' && i + 1 < jsonRespuesta.length()) {
                // Guardamos la secuencia de escape tal cual; se traduce después
                valorBruto.append(c).append(jsonRespuesta.charAt(i + 1));
                i += 2;
            } else if (c == '"') {
                // Comilla sin escapar: aquí termina el valor
                break;
            } else {
                valorBruto.append(c);
                i++;
            }
        }

        return desescaparJson(valorBruto.toString());
    }

    // Convierte de vuelta los caracteres escapados (\n, \", \\, XXXX...) a texto normal
    private String desescaparJson(String texto) {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == '\\' && i + 1 < texto.length()) {
                char siguiente = texto.charAt(i + 1);
                switch (siguiente) {
                    case 'n':
                        resultado.append('\n');
                        i++;
                        break;
                    case 'r':
                        resultado.append('\r');
                        i++;
                        break;
                    case 't':
                        resultado.append('\t');
                        i++;
                        break;
                    case '"':
                        resultado.append('"');
                        i++;
                        break;
                    case '\\':
                        resultado.append('\\');
                        i++;
                        break;
                    case 'u':
                        if (i + 5 < texto.length()) {
                            String hex = texto.substring(i + 2, i + 6);
                            resultado.append((char) Integer.parseInt(hex, 16));
                            i += 5;
                        }
                        break;
                    default:
                        resultado.append(siguiente);
                        i++;
                }
            } else {
                resultado.append(c);
            }
        }
        return resultado.toString();
    }
}
