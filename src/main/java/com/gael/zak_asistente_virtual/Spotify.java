/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gael.zak_asistente_virtual;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author gaell
 */
public class Spotify extends Aplicaciones{
    
    //rrellenar el constructor predeterminado con sus valores "nuevos"
    public Spotify() {
        super("Spotify", false, new ArrayList<>());
    }

    // Se sobrescribe abrir() porque el de Aplicaciones solo imprime texto,
    // no lanza ningún proceso real.
    @Override
    public void abrir() {
        try {
            // processBuilder se usa para realizar acciones en el sistema operativo (dispositivo)
            // usamos el cmd para adentrarnos y "/c", "start", "\"\"" para faciliatra acceso y solo poner el nombre
            new ProcessBuilder("cmd", "/c", "start", "\"\"", "spotify:").start();
            this.estado = true;
            System.out.println("Abriendo Spotify...");
        } catch (IOException e) {
            System.err.println("Error al abrir Spotify: " + e.getMessage());
        }
    }

    // Se sobrescribe cerrar() para cerrar de verdad el proceso de Spotify.
    @Override
    public void cerrar() {
        try {
            new ProcessBuilder("taskkill", "/F", "/IM", "Spotify.exe").start();
            this.estado = false;
            System.out.println("Cerrando Spotify...");
        } catch (IOException e) {
            System.err.println("Error al cerrar Spotify: " + e.getMessage());
        }
    }
    
    public void siguienteCancion() {
        enviarTeclaMultimedia(0xB0, "Cambiando a la siguiente canción...");
    }

    public void anteriorCancion() {
        enviarTeclaMultimedia(0xB1, "Volviendo a la canción anterior...");
    }

    public void pausarOReanudar() {
        enviarTeclaMultimedia(0xB3, "Pausando/Reanudando música...");
    }

    // usando la función keybd_event de la API de Windows
    // envia códigos de tecla virtuales de verdad (0xB0 = siguiente pista,
    // 0xB1 = pista anterior, 0xB3 = play/pausa)
    private void enviarTeclaMultimedia(int codigoVk, String mensaje) {
        try {
            File carpetaTemporal = new File(System.getProperty("java.io.tmpdir"), "LealMediaKeys");
            carpetaTemporal.mkdirs();
            File script = new File(carpetaTemporal, "tecla_" + codigoVk + ".ps1");

            String contenidoScript =
                    "$firma = @'\r\n"
                    + "using System;\r\n"
                    + "public static class LealMediaKeys {\r\n"
                    + "    [System.Runtime.InteropServices.DllImport(\"user32.dll\")]\r\n"
                    + "    public static extern void keybd_event(byte bVk, byte bScan, uint dwFlags, UIntPtr dwExtraInfo);\r\n"
                    + "}\r\n"
                    + "'@\r\n"
                    + "Add-Type -TypeDefinition $firma -ErrorAction SilentlyContinue\r\n"
                    + "[LealMediaKeys]::keybd_event(" + codigoVk + ", 0, 0, [UIntPtr]::Zero)\r\n"
                    + "Start-Sleep -Milliseconds 50\r\n"
                    + "[LealMediaKeys]::keybd_event(" + codigoVk + ", 0, 2, [UIntPtr]::Zero)\r\n";

            try (PrintWriter escritor = new PrintWriter(script, "UTF-8")) {
                escritor.print(contenidoScript);
            }

            new ProcessBuilder("powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-File", script.getAbsolutePath()).start();
            System.out.println(mensaje);
        } catch (IOException e) {
            System.err.println("Error al enviar tecla multimedia: " + e.getMessage());
        }
    }
    
    /*
            PRUEBAS
    public void abrir(){
        System.out.println("Abriendo aplicacion");
    }
    
    //metodo para reproducir musica
    public  void reproducir(){
        System.out.println("Iniciando la reproduccion de la musica");
    }
    
    //metodo para reproducir musica
    public  void pausar(){
        System.out.println("Pausando la musica en reproduccion");
    }
    
    //metodo para cambiar de cancion
    public void siguienteCancion(){
        System.out.println("Reproduciendo siguiente cancion");
    }
    
    //metodo para reproducir playlist
    public void reproducirPlaylist(String nombrePlaylist){
        System.out.println("Reproduciendo tu playlist" + nombrePlaylist);
    }*/
    
}