/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gael.zak_asistente_virtual;

import java.io.IOException;

/**
 *
 * @author gaell
 */
public class Volumen {
    protected String volumen;

    public Volumen() {
        //dejamos vacio porque no le añadiremos algun valor
    }
    
    public void bajarVolumen(){
        try{
            for (int i = 0; i < 10; i++){
            new ProcessBuilder("powershell", "-c", "(New-Object -ComObject WScript.Shell).SendKeys([char]174)").start();
            System.out.println("Bajando volumen");
            }
        } catch (IOException e) {
            System.out.println("Se produjo un error al tratar de bajar el volumen" + e.getMessage());
        }
    }
    
    public void subirVolumen(){
        try{
            for (int i = 0; i < 10; i++){
            new ProcessBuilder("powershell", "-c", "(New-Object -ComObject WScript.Shell).SendKeys([char]175)").start();
            System.out.println("Subiendo volumen");
            }
        } catch (IOException e) {
            System.out.println("Se produjo un error al tratar de subir el volumen" + e.getMessage());
        }
    }
    
     public void silenciarVolumen(){
        try{
            new ProcessBuilder("powershell", "-c", "(New-Object -ComObject WScript.Shell).SendKeys([char]173)").start();
            System.out.println("Silenciando dispositivo");
        } catch (IOException e) {
            System.out.println("Se produjo un error al tratar de silenciar el dispositivo" + e.getMessage());
        }
    }
     
     public void reactivarVolumen() {
    try {
        new ProcessBuilder("powershell", "-c", "(New-Object -ComObject WScript.Shell).SendKeys([char]173)").start();
        System.out.println("Sonido reactivado");
    } catch (IOException e) {
        System.err.println("Se produjo un error al tratar de reactivar el sonido: " + e.getMessage());
    }
}
}
