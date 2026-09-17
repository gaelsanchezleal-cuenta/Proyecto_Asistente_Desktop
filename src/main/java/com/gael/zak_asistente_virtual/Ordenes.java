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
public class Ordenes {

    //llamar a las aplicaciones que usaremos
    //estructura : modificadorDeAcceso + Clase + Variable
    private Spotify spotify;
    private Reloj reloj;
    private Dispositivo dispositivos;
    private Breve breve;
    private PreguntaleGemini gemini;
    private Volumen nivelVol;
    private Voz voz;
    private GeneradorArchivos generadorArchivos;

    //craer constructor con parametros
    public Ordenes(Spotify spotify, Reloj reloj, Dispositivo dispositivos, Breve breve, Volumen nivelVol) {
        this.spotify = spotify;
        this.reloj = reloj;
        this.dispositivos = dispositivos;
        this.breve = breve;
        this.gemini = new PreguntaleGemini();
        this.nivelVol = nivelVol;
        this.voz = new Voz();
        this.generadorArchivos = new GeneradorArchivos();
    }

    public void comandoPorVoz(String comandoVoz) {

        // Texto original (con acentos) en minúsculas: se usa para lo que se
        // le manda tal cual a Gemini, para no perder tildes en la pregunta.
        String comandoOriginal = comandoVoz.toLowerCase();

        // Versión sin acentos: se usa SOLO para reconocer los comandos con
        // .contains(...), así una tilde mal transcrita no rompe la comparación.
        String comandoVozSinAcentos = java.text.Normalizer.normalize(comandoOriginal, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        // Palabra de activación: si no se menciona "leal" o "real" o "lea" en la frase, se ignora
        if (!comandoVozSinAcentos.contains("leal") && !comandoVozSinAcentos.contains("real")
                && !comandoVozSinAcentos.contains("lea")) {
            return;
        }

        // Quitamos el nombre de ambas versiones para que no estorbe al comparar
        comandoVoz = comandoVozSinAcentos.replace("leal", "")
                .replace("real", "")
                .replace("eal", "")
                .trim();
        comandoOriginal = comandoOriginal.replace("leal", "")
                .replace("real", "")
                .replace("eal", "")
                .trim();

        /*
                    PRUEBA
        //compara lo que diga con lo que esta  escrito por el .contains
        if (comandoVoz.contains("Abrir musica")) {
            //para abrir una aplicacion debemos llamar al constructor  y al nombre del objeto
            //estructura : nombreVariable.nombreMetodo();
            spotify.abrir();
        } else if (comandoVoz.contains("Abrir Reloj")) {
            reloj.abrir();
        } else if (comandoVoz.contains("Cerrar musica")) {
            spotify.cerrar();
        } else if (comandoVoz.contains("Cerrar Reloj")) {
            reloj.cerrar();
        } else {
            System.out.println("Accion no reconocida");
        }
    }*/
        // ---------------------- Spotify ----------------------
        if (comandoVoz.contains("abrir spotify") || comandoVoz.contains("abrir tema") || comandoVoz.contains("abré spotify")
                || comandoVoz.contains("abré tema") || comandoVoz.contains("abre spotify") || comandoVoz.contains("abre tema")) {
            spotify.abrir();
            voz.hablar("Abriendo Spotify");

        } else if (comandoVoz.contains("quita spotify") || comandoVoz.contains("cierra spotify")
                || comandoVoz.contains("cierra tema") || comandoVoz.contains("quita tema")) {
            spotify.cerrar();
            voz.hablar("Cerrando Spotify");

        } else if (comandoVoz.contains("siguiente cancion") || comandoVoz.contains("siguiente canción")) {
            spotify.siguienteCancion();
            voz.hablar("Siguiente canción");

        } else if (comandoVoz.contains("anterior cancion") || comandoVoz.contains("anterior canción")) {
            spotify.anteriorCancion();
            voz.hablar("Canción anterior");

        } else if (comandoVoz.contains("pausa la cancion") || comandoVoz.contains("pon pausa")
                || comandoVoz.contains("pausa la canción") || comandoVoz.contains("reproduce la cancion")
                || comandoVoz.contains("reproduce la canción")) {
            spotify.pausarOReanudar();
            voz.hablar("Listo");

            // ---------------------- Reloj ----------------------
        } else if (comandoVoz.contains("abrir reloj") || comandoVoz.contains("abré el reloj")
                || comandoVoz.contains("abre el reloj")) {
            reloj.abrir();
            voz.hablar("Abriendo el reloj");

        } else if (comandoVoz.contains("cerrar el reloj")) {
            reloj.cerrar();
            voz.hablar("Cerrando el reloj");

        } else if (comandoVoz.contains("que hora es") || comandoVoz.contains("dime la hora")) {
            String horaTexto = reloj.darHora();
            voz.hablar(horaTexto);

        } else if (comandoVoz.contains("programa una alarma") || comandoVoz.contains("programa un alarma") || comandoVoz.contains("programar alarma")) {
            // Se pasa la frase completa para que Reloj intente entender la hora dicha
            reloj.programarAlarma(comandoVoz);
            voz.hablar("Alarma programada");

            // ---------------------- Sistema ----------------------
        } else if (comandoVoz.contains("apaga la computadora") || comandoVoz.contains("apagar la computadora")) {
            // Hablamos ANTES de apagar, porque el equipo se apaga casi de inmediato
            voz.hablar("Apagando el equipo");
            dispositivos.apagarDispositivo();

        } else if (comandoVoz.contains("cerrar todo") || comandoVoz.contains("cierra todo")
                || comandoVoz.contains("serrar todo") || comandoVoz.contains("sierra todo")) {
            dispositivos.cerrarTodo();
            voz.hablar("Cerrando todo");

            // ---------------------- Navegador ----------------------
        } else if (comandoVoz.contains("abré el navegador") || comandoVoz.contains("abre el navegador")) {
            breve.abrir();
            voz.hablar("Abriendo el navegador");

        } else if (comandoVoz.contains("cerrar el navegador")
                || comandoVoz.contains("serrar el navegador") || comandoVoz.contains("finalizar la tarea")) {
            breve.cerrar();
            voz.hablar("Cerrando el navegador");

        } else if (comandoVoz.contains("abrir gemini") || comandoVoz.contains("abré gemini")
                || comandoVoz.contains("abre gemini")) {
            breve.abrirGemini();
            voz.hablar("Abriendo Gemini");

        } else if (comandoVoz.contains("abrir clase") || comandoVoz.contains("abrir tareas")
                || comandoVoz.contains("abrir classroom") || comandoVoz.contains("abré classroom")
                || comandoVoz.contains("abre classroom")) {
            breve.abrirClasrroom();
            voz.hablar("Abriendo Classroom");

        } else if (comandoVoz.contains("serrar las hojas") || comandoVoz.contains("cerrar las hojas")) {
            breve.cerrarPestañas();
            voz.hablar("Cerrando las pestañas");

            // ---------------------- Gemini (preguntas) ----------------------
        } else if (comandoVoz.contains("pregunta") || comandoVoz.contains("preguntale")) {
            // Usamos comandoOriginal (CON acentos) para no perder tildes en la pregunta
            String pregunta = extraerPregunta(comandoOriginal);
            if (pregunta.isBlank()) {
                System.out.println("¿Qué quieres preguntarle a Gemini?");
                voz.hablar("¿Qué quieres preguntarle a Gemini?");
            } else {
                System.out.println("Preguntando a Gemini: " + pregunta);
                String respuesta = gemini.consultarGemini(pregunta);
                System.out.println("Gemini responde: " + respuesta);
                // Leemos en voz alta lo que Gemini contestó
                voz.hablar(respuesta);
            }

            // ---------------------- Gemini (generar documentos) ----------------------
        } else if (comandoVoz.contains("documento") || comandoVoz.contains("archivo")) {
            manejarGeneracionDeArchivo(comandoVoz, comandoOriginal);

            // ---------------------- Volumen ----------------------
        } else if (comandoVoz.contains("bajar el volumen") || comandoVoz.contains("baja el volumen")) {
            nivelVol.bajarVolumen();
            voz.hablar("Bajando el volumen");
        } else if (comandoVoz.contains("subir el volumen") || comandoVoz.contains("sube el volumen")) {
            nivelVol.subirVolumen();
            voz.hablar("Subiendo el volumen");
        } else if (comandoVoz.contains("silenciar el dispositivo") || comandoVoz.contains("silenciate")) {
            nivelVol.silenciarVolumen();
            voz.hablar("Silenciando");
        } else if (comandoVoz.contains("activa el volumen") || comandoVoz.contains("activa el sonido")) {
            nivelVol.reactivarVolumen();
            voz.hablar("Sonido activado");

            // ---------------------- Voz ----------------------
        } else if (comandoVoz.contains("callate") || comandoVoz.contains("detente") || comandoVoz.contains("para de hablar") || comandoVoz.contains("guarda silencio")) {
            voz.callar();
            System.out.println("Voz detenida");
        } else {
            System.out.println("Accion no reconocida");
            voz.hablar("No reconocí esa orden");
        }
    }

    //CLOUDE:
    // Quita frases disparadoras como "pregúntale a gemini" / "pregunta" y deja
    // solo el texto de la pregunta que se le va a mandar a la API.
    private String extraerPregunta(String comandoVoz) {
        String pregunta = comandoVoz;
        String[] disparadores = {"preguntale a gemini", "pregunta a gemini", "preguntale", "pregunta"};
        for (String disparador : disparadores) {
            if (pregunta.contains(disparador)) {
                pregunta = pregunta.replaceFirst(disparador, "");
                break;
            }
        }
        // por si queda "gemini" o "a gemini" suelto al inicio de la frase
        pregunta = pregunta.replaceFirst("^\\s*a\\s+gemini", "");
        pregunta = pregunta.replaceFirst("^\\s*gemini", "");
        return pregunta.trim();
    }

    // Detecta en qué formato se pidió el archivo, según palabras clave dichas.
    private String detectarFormato(String comandoVoz) {
        if (comandoVoz.contains("pdf")) {
            return "pdf";
        }
        if (comandoVoz.contains("word") || comandoVoz.contains("docx")) {
            return "docx";
        }
        if (comandoVoz.contains("excel") || comandoVoz.contains("xlsx") || comandoVoz.contains("hoja de calculo")) {
            return "xlsx";
        }
        if (comandoVoz.contains("texto") || comandoVoz.contains("txt")) {
            return "txt";
        }
        return null; // no se especificó ningún formato reconocido
    }

    // Saca el tema del documento buscando "sobre"/"acerca de"/"de" en la
    // frase y quedándose con lo que sigue después de esa palabra.
    private String extraerTemaDocumento(String comandoOriginal) {
        String[] separadores = {" sobre ", " acerca de ", " de "};
        for (String separador : separadores) {
            int idx = comandoOriginal.indexOf(separador);
            if (idx != -1) {
                return comandoOriginal.substring(idx + separador.length()).trim();
            }
        }
        return comandoOriginal.trim();
    }

    // Pide el contenido a Gemini y genera el archivo (.docx/.pdf/.xlsx/.txt)
    // correspondiente, guardándolo directo en la carpeta de Descargas.
    private void manejarGeneracionDeArchivo(String comandoVoz, String comandoOriginal) {
        String formato = detectarFormato(comandoVoz);
        if (formato == null) {
            System.out.println("¿En qué formato quieres el archivo? (Word, PDF, Excel o texto)");
            voz.hablar("¿En qué formato quieres el archivo? Puede ser Word, PDF, Excel o texto");
            return;
        }

        String tema = extraerTemaDocumento(comandoOriginal);
        if (tema.isBlank()) {
            System.out.println("¿Sobre qué tema quieres el documento?");
            voz.hablar("¿Sobre qué tema quieres el documento?");
            return;
        }

        System.out.println("Generando ." + formato + " sobre: " + tema);
        voz.hablar("Generando tu documento, dame un momento");

        String contenido = gemini.consultarGeminiParaDocumento(tema);

        try {
            String rutaArchivo;
            switch (formato) {
                case "pdf":
                    rutaArchivo = generadorArchivos.crearPdf(contenido, tema);
                    break;
                case "docx":
                    rutaArchivo = generadorArchivos.crearDocx(contenido, tema);
                    break;
                case "xlsx":
                    rutaArchivo = generadorArchivos.crearXlsx(contenido, tema);
                    break;
                default:
                    rutaArchivo = generadorArchivos.crearTxt(contenido, tema);
            }
            System.out.println("Archivo generado: " + rutaArchivo);
            voz.hablar("Listo, descargué tu documento en la carpeta de descargas");
        } catch (IOException e) {
            System.err.println("Error al generar el archivo: " + e.getMessage());
            voz.hablar("Hubo un error al generar el documento");
        }
    }

}
