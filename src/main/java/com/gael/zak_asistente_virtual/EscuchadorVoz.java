/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gael.zak_asistente_virtual;

/**
 *
 * @author gaell
 */
    import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.*;
import java.io.IOException;

// ------------------------------------ COMPLETO POR CLAUDE. ---------------------------------------------------

public class EscuchadorVoz {

    private Model modelo;
    private Recognizer reconocedor;
    private Ordenes gestorOrdenes;

    public EscuchadorVoz(Ordenes gestorOrdenes) throws IOException {
        this.gestorOrdenes = gestorOrdenes;
        
        // Silenciamos los logs técnicos de Vosk en consola
        LibVosk.setLogLevel(LogLevel.WARNINGS);
        
        // Cargamos el modelo en español guardado en la raíz
        this.modelo = new Model("modelo-es");
        
        // Configuramos el reconocedor con la tasa de muestreo del audio (16000 Hz)
        this.reconocedor = new Recognizer(modelo, 16000.0f);
    }

    public void iniciarEscucha() {
        // Configuración del formato de audio que entiende Vosk
        AudioFormat format = new AudioFormat(16000.0f, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            System.err.println("El micrófono no es compatible con el formato requerido.");
            return;
        }

        try (TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info)) {
            line.open(format);
            line.start();
            System.out.println(">>> Escuchando comandos de voz... <<<");

            byte[] buffer = new byte[4096];
            int bytesRead;

            // Bucle continuo para leer el micrófono
            while ((bytesRead = line.read(buffer, 0, buffer.length)) >= 0) {
                if (reconocedor.acceptWaveForm(buffer, bytesRead)) {
                    // Texto final cuando terminas una frase
                    String resultadoJson = reconocedor.getResult();
                    String texto = extraerTextoJson(resultadoJson);
                    
                    if (!texto.trim().isEmpty()) {
                        System.out.println("Escuchado: " + texto);
                        // Enviamos la voz procesada a tu clase Ordenes
                        gestorOrdenes.comandoPorVoz(texto);
                    }
                }
            }
        } catch (LineUnavailableException e) {
            System.err.println("No se pudo acceder al micrófono: " + e.getMessage());
        }
    }

    // Extrae el valor de la propiedad "text" en el JSON devuelto por Vosk
    private String extraerTextoJson(String json) {
        if (json.contains("\"text\" : \"")) {
            int inicio = json.indexOf("\"text\" : \"") + 10;
            int fin = json.indexOf("\"", inicio);
            return json.substring(inicio, fin);
        }
        return "";
    }
}
