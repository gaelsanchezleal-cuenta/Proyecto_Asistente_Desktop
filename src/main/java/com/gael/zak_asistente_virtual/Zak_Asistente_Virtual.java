/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.gael.zak_asistente_virtual;

import java.io.IOException;

/**
 *
 * @author gaell
 */
public class Zak_Asistente_Virtual {

    public static void main(String[] args) {

        System.setProperty("jna.encoding", "UTF-8");

        // Forzamos que la consola imprima usando UTF-8, para evitar que
        // tildes/eñes se muestren como el símbolo "�" (esto no afecta la
        // lógica de comparación de comandos, solo cómo se ve el texto impreso).
        System.setOut(new java.io.PrintStream(System.out, true, java.nio.charset.StandardCharsets.UTF_8));
        System.setErr(new java.io.PrintStream(System.err, true, java.nio.charset.StandardCharsets.UTF_8));

        /*
                PRUEBAS DE FUNCIONALIDAD
        //creamos una estancia de los objetos
        //estructura : nombreClase + variable = new + nombreClase();
        Spotify aplicacionSpotify = new Spotify();
        Reloj aplicacionReloj = new Reloj();
        
        //llamamos a los metodos creados
        //estructura : nombreObjeto.nombreMetodo(valor);
        aplicacionSpotify.reproducir();
        aplicacionReloj.programarAlarma("07:00 AM");*/
        
        Spotify spotify = new Spotify();
        Reloj reloj = new Reloj();
        Dispositivo dispositivos = new Dispositivo();
        Breve breve = new Breve();
        Volumen nivelVol = new Volumen();

        try {
            //Instancias la clase de las órdenes
            Ordenes misOrdenes = new Ordenes(spotify, reloj, dispositivos, breve, nivelVol);

            //Pasamos ordenes al escuchador para que se comuniquen
            EscuchadorVoz escuchador = new EscuchadorVoz(misOrdenes);

            //Encendemos el micrófono
            escuchador.iniciarEscucha();

        } catch (IOException e) {
            System.err.println("Error al cargar el modelo de voz: " + e.getMessage());
            System.err.println("Asegúrate de que la carpeta 'modelo-es' esté en la raíz del proyecto.");
        }
    }
}
