package Controlador;

import javax.sound.sampled.*;
import java.net.URL;

public class TareaAudio implements Runnable {
    private String nombre;
    private String ruta;
    private boolean esLoop;
    private float volumenDecibelios;
    private Clip clip;

    public TareaAudio(String nombre, String ruta, boolean esLoop, float volumenDecibelios) {
        this.nombre = nombre;
        this.ruta = ruta;
        this.esLoop = esLoop;
        this.volumenDecibelios = volumenDecibelios;
    }

    @Override
    public void run() {
        try {
            URL url = getClass().getResource(ruta);
            if (url == null) {
                System.out.println("No se encontró el audio: " + ruta);
                return;
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(ais);
            ajustarVolumen();

            if (esLoop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            clip.start();

            // Mantiene el hilo en espera controlada mientras suena
            while (clip != null && clip.isOpen() && (esLoop || clip.isRunning() || clip.getFramePosition() < clip.getFrameLength())) {
                Thread.sleep(100);
            }

        } catch (InterruptedException e) {
            // Si el hilo es interrumpido, cerramos el audio
            detener();
            return;
        } catch (Exception e) {
            System.err.println("Error en hilo de audio (" + nombre + "): " + e.getMessage());
        } finally {
            detener();
        }
    }

    public void detener() {
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.close();
            clip = null;
        }
    }

    private void ajustarVolumen() {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float valorSeguro = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), volumenDecibelios));
            gainControl.setValue(valorSeguro);
        }
    }
}
