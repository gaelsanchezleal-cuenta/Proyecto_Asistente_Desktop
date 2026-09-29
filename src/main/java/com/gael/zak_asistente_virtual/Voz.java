/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gael.zak_asistente_virtual;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Voz {

    private final List<Process> procesosActivos = new CopyOnWriteArrayList<>();
    
    public void hablar(String texto) {
        if (texto == null || texto.isBlank()) {
            return;
        }
        try {
            String textoSeguro = texto.replace("'", "''");

            String script = "Add-Type -AssemblyName System.Speech; "
                    + "$sintetizador = New-Object System.Speech.Synthesis.SpeechSynthesizer; "
                    + "$sintetizador.Speak('" + textoSeguro + "');";

            byte[] bytesScript = script.getBytes(StandardCharsets.UTF_16LE);
            String comandoBase64 = Base64.getEncoder().encodeToString(bytesScript);

            Process proceso = new ProcessBuilder("powershell", "-NoProfile", "-EncodedCommand", comandoBase64).start();

            // Lo guardamos para poder detenerlo si se pide silencio, y lo
            // quitamos solo de la lista cuando termine de hablar por su cuenta.
            procesosActivos.add(proceso);
            proceso.onExit().thenAccept(procesosActivos::remove);
        } catch (IOException e) {
            System.err.println("Error al usar la voz: " + e.getMessage());
        }
    }
    
    public void callar(){
        for (Process proceso : procesosActivos) {
            if (proceso.isAlive()) {
                proceso.destroyForcibly();
            }
        }
         procesosActivos.clear();
    }
}
