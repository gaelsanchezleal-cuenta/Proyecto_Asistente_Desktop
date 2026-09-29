/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gael.zak_asistente_virtual;

import java.util.ArrayList;
import java.util.List;

public class Aplicaciones {
    
    //creacion de atributos que tendran todas las apliaciones con "protected"
    //para que las clases hijas puedan acceder a ellos
    protected String nombreApp;
    protected boolean estado;
    protected List<String> Notificaciones;

    //metodo constructor
    public Aplicaciones(String nombreApp, boolean estado, List<String> Notifiaciones) {
        this.nombreApp = nombreApp;
        //pone el estado de la aplicacion en apagado
        this.estado = false;
        //crea una lista nueva de notifiaciones vacias
        this.Notificaciones = new ArrayList<>();
    }
    
    //metodo para abrir una aplicacion
    public void abrir(){
        this.estado = true;
        System.out.println("Abriendo la aplicacion de " + nombreApp);
    }
    
    //metodo para cerrar una aplicacion
    public void cerrar(){
        this.estado = false;
        System.out.println("Cerrando la aplicacion de " + nombreApp);
    }
    
    //metodo para agregarnotificaciones
    public void agregarNotificacion(String mensaje){
        Notificaciones.add(mensaje);
    }
    
    public void leerNotificaciones() {
        //utlizamos .isEmpty() para ver si  una lista esta vacia
        if (Notificaciones.isEmpty()) {
            //si esta vacia desplegara este mensaje
            System.out.println(nombreApp + " No tienes notificaciones pendientes.");
        } else {
            System.out.println("Tienes notificaciones de " + nombreApp);
            for (int i = 0; i < Notificaciones.size(); i++) {
                System.out.println((i + 1) + ". " + Notificaciones.get(i));
            }
        }
    }
    
    public void eliminarNotificacion(int indice) {
        //ocupamos .size para contar la cantidad de elementos dentro de Notificaciones
        if (indice >= 0 && indice < Notificaciones.size()) {
            //ocupamos .remove para eliminar elementos de la lista de Notificaciones en el punto indice
            String removida = Notificaciones.remove(indice);
            System.out.println("Notificación eliminada: " + removida);
        } else {
            System.out.println("No hay notificaciones que borrar");
        }
    }
    
}
