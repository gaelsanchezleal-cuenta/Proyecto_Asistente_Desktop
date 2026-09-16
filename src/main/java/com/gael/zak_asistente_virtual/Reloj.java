/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gael.zak_asistente_virtual;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author gaell
 */
public class Reloj extends Aplicaciones {

    public Reloj() {
        super("Reloj", false, new ArrayList<>());
    }

    @Override
    public void abrir() {
        try {
            new ProcessBuilder("cmd", "/c", "start ms-clock:").start();
            System.out.println("Abriendo el Reloj de Windows...");
        } catch (IOException e) {
            System.err.println("Error al abrir el Reloj: " + e.getMessage());
        }
    }

    @Override
    public void cerrar() {
        try {
            new ProcessBuilder("taskkill", "/F", "/IM", "Time.exe").start();
            System.out.println("Cerrando el Reloj...");
        } catch (IOException e) {
            System.err.println("Error al cerrar el Reloj: " + e.getMessage());
        }
    }

    public String darHora() {
        LocalTime horaActual = LocalTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("hh:mm a");
        String mensaje = "La hora actual es: " + horaActual.format(formato);
        System.out.println(mensaje);
        return mensaje;
    }

    // Recibe la frase completa dicha por el usuario (ej: "programar alarma a las 7 30")
    public void programarAlarma(String comandoVoz) {
        int[] horaYMinuto = extraerHoraYMinuto(comandoVoz);

        if (horaYMinuto == null) {
            System.out.println("No entendí la hora para programar la alarma.");
            return;
        }

        programarAlarmaInterna(horaYMinuto[0], horaYMinuto[1]);
    }

    // Sobrecarga sin argumentos: abre la interfaz gráfica si el usuario lo solicita explícitamente
    public void programarAlarma() {
        try {
            new ProcessBuilder("cmd", "/c", "start ms-clock:alarm").start();
            System.out.println("Abriendo el panel de alarmas de Windows...");
        } catch (IOException e) {
            System.err.println("Error al abrir las alarmas: " + e.getMessage());
        }
    }

    // Automatiza la aplicación gráfica del Reloj de Windows usando Robot
    private void programarAlarmaInterna(int hora, int minuto) {
        try {
            // 1. Abrir la aplicación de Reloj directamente en la pestaña de Alarmas
            new ProcessBuilder("cmd", "/c", "start", "\"\"", "ms-clock:alarm").start();
            
            // Pausa de 2 segundos para dar tiempo a que Windows abra la ventana
            Thread.sleep(2000);

            Robot robot = new Robot();

            // 2. Presionar Ctrl + N (Atajo en Windows para "+ Agregar una alarma nueva")
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_N);
            robot.keyRelease(KeyEvent.VK_N);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            
            Thread.sleep(600);

            // 3. Escribir la hora (Ejemplo: "07")
            String horaStr = String.format("%02d", hora);
            for (char c : horaStr.toCharArray()) {
                int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
                robot.keyPress(keyCode);
                robot.keyRelease(keyCode);
                Thread.sleep(60);
            }

            // Pasar al campo de minutos con la tecla TAB
            robot.keyPress(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_TAB);
            Thread.sleep(100);

            // 4. Escribir los minutos (Ejemplo: "30")
            String minutoStr = String.format("%02d", minuto);
            for (char c : minutoStr.toCharArray()) {
                int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
                robot.keyPress(keyCode);
                robot.keyRelease(keyCode);
                Thread.sleep(60);
            }

            // 5. Presionar Enter para dar clic al botón "Guardar"
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);

            System.out.println("Alarma guardada visualmente en el Reloj a las " + horaStr + ":" + minutoStr);

        } catch (IOException | AWTException | InterruptedException e) {
            System.err.println("Error al interactuar con la app Reloj: " + e.getMessage());
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Intenta sacar hora y minuto de la frase dicha, en dígitos o en palabras.
    private int[] extraerHoraYMinuto(String comandoVoz) {
        // 1) Formato con dígitos tipo "7:30", "07 30", "7.30"
        Matcher m = Pattern.compile("(\\d{1,2})\\s*[:\\.]?\\s*(\\d{1,2})?").matcher(comandoVoz);
        while (m.find()) {
            String horaTexto = m.group(1);
            String minutoTexto = m.group(2);
            int hora = Integer.parseInt(horaTexto);
            if (hora > 23) {
                continue; // no parece una hora válida
            }
            int minuto = 0;
            if (minutoTexto != null && !minutoTexto.isEmpty()) {
                minuto = Integer.parseInt(minutoTexto);
            } else if (comandoVoz.contains("y media")) {
                minuto = 30;
            } else if (comandoVoz.contains("y cuarto")) {
                minuto = 15;
            } else if (comandoVoz.contains("menos cuarto")) {
                hora = (hora + 23) % 24;
                minuto = 45;
            }
            if (minuto <= 59) {
                hora = ajustarPorTurnoDelDia(hora, comandoVoz);
                return new int[]{hora, minuto};
            }
        }

        // 2) Hora dicha en palabras, ej: "programa la alarma a las siete"
        String[] tokens = comandoVoz.replace(",", " ").split("\\s+");
        for (String token : tokens) {
            int valor = numeroDesdePalabra(token);
            if (valor >= 0 && valor <= 23) {
                int minuto = 0;
                if (comandoVoz.contains("y media")) {
                    minuto = 30;
                } else if (comandoVoz.contains("y cuarto")) {
                    minuto = 15;
                } else if (comandoVoz.contains("menos cuarto")) {
                    valor = (valor + 23) % 24;
                    minuto = 45;
                }
                valor = ajustarPorTurnoDelDia(valor, comandoVoz);
                return new int[]{valor, minuto};
            }
        }

        return null;
    }

    // Ajusta la hora (0-12) a formato 24h según si se dijo "de la tarde"/"de la noche"
    private int ajustarPorTurnoDelDia(int hora, String comandoVoz) {
        boolean esTarde = comandoVoz.contains("de la tarde") || comandoVoz.contains("de la noche")
                || comandoVoz.contains("pm");
        boolean esManana = comandoVoz.contains("de la manana") || comandoVoz.contains("de la mañana")
                || comandoVoz.contains("am");

        if (esTarde && hora >= 1 && hora <= 11) {
            return hora + 12;
        }
        if (esManana && hora == 12) {
            return 0;
        }
        return hora;
    }

    // Traduce números básicos dichos en español (0-23) a su valor entero.
    private int numeroDesdePalabra(String palabra) {
        String[] numeros = {
            "cero", "una", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete",
            "ocho", "nueve", "diez", "once", "doce", "trece", "catorce", "quince",
            "dieciseis", "dieciséis", "diecisiete", "dieciocho", "diecinueve",
            "veinte", "veintiuno", "veintidos", "veintidós", "veintitres", "veintitrés"
        };
        int[] valores = {
            0, 1, 1, 2, 3, 4, 5, 6, 7,
            8, 9, 10, 11, 12, 13, 14, 15,
            16, 16, 17, 18, 19,
            20, 21, 22, 22, 23, 23
        };
        for (int i = 0; i < numeros.length; i++) {
            if (palabra.equalsIgnoreCase(numeros[i])) {
                return valores[i];
            }
        }
        return -1;
    }
}