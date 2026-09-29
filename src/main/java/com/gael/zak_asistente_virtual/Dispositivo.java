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
public class Dispositivo {
    protected boolean activacion;
    protected boolean cerrar;

    public Dispositivo(boolean activacion, boolean cerrar) {
        this.activacion = activacion;
        this.cerrar = cerrar;
    }
    
    //para no pasar parametros al iniciar
    public Dispositivo() {
        this.activacion = true;
        this.cerrar = false;
    }
    
    public void apagarDispositivo(){
        this.activacion = false;
        try{
        System.out.println("Apagando dispositivo...");
        new ProcessBuilder("cmd", "/c", "shutdown", "/s", "/t", "0").start();
        } catch (IOException e) {
            System.out.println("Error al apagar");
        }
    }
    
    public void cerrarTodo(){
        this.cerrar = true;
        try{
        System.out.println("cerrando aplicaciones activas dispositivo...");
        String comandoPowerShell = "Get-Process | Where-Object { $_.MainWindowHandle -ne 0 } | ForEach-Object { $_.CloseMainWindow() }";
        new ProcessBuilder("powershell", "-c", comandoPowerShell).start();
        } catch (IOException e) {
            System.out.println("Error al cerrar aplicaciones");
        }
    }
    
}
