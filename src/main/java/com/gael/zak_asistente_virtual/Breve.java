/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gael.zak_asistente_virtual;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gaell
 */
public class Breve extends Aplicaciones{
    protected String paginas;

    public Breve(String nombreApp, boolean estado, List<String> Notifiaciones) {
        super("Brave", false, new ArrayList<>());
    }
    
    public Breve() {
        super("Brave", false, new ArrayList<>());
    }
    
    @Override
    public void abrir(){
       try{
           System.out.println("Abriendo aplicacion brave...");
           new ProcessBuilder("cmd", "/c", "start", "brave").start();
       } catch (IOException e) {
           System.out.println("Error al abrir aplicacion");
       }
    }
    
    
    @Override
    public void cerrar(){
        try{
           System.out.println("Cerrando aplicacion brave...");
           new ProcessBuilder("taskkill", "/F", "/IM", "brave.exe").start();
       } catch (IOException e) {
           System.out.println("Error al cerrar aplicacion");
       }
    }
    
    public void abrirGemini(){
        try{
           System.out.println("Navegando en gemini...");
           new ProcessBuilder("cmd", "/c", "start", "\"\"", "https://gemini.google.com").start();
       } catch (IOException e) {
           System.out.println("Error al buscar Gemini");
       }
    }
    
    public void abrirClasrroom(){
        try{
           System.out.println("Navegando en clasrroom...");
           new ProcessBuilder("cmd", "/c", "start", "\"\"", "https://classroom.google.com").start();
       } catch (IOException e) {
           System.out.println("Error al buscar clasrroom");
       }
    }
    
    public void cerrarPestañas(){
        try{
        System.out.println("Cerrando todas las pestañas...");
        new ProcessBuilder("taskkill", "/F", "/IM", "brave.exe").start();
        } catch  (IOException e) {
            System.out.println("Error al cerrar pestañas");
        }
    }
}
