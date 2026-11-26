
    package Vistas;

    import javax.swing.*;
    import java.awt.*;
    import java.net.URL;

    public class Oso {

        private int x;
        private int y;

        // Velocidad de desplazamiento
        private int velocidad = 1;

        // Velocidad de juego
        private int velocidadJuego = 3;

        // Control de los fotogramas
        private int fotograma = 0;
        private int contador = 0;

        // Velocidad de las animaciones
        private int velocidadCaminata = 8;
        private int velocidadIntro = 4;

        // Dimensiones de los sprites
        private final int TAMANO = 110;
        private final int ANCHO_INTRO = 230;
        private final int ALTO_INTRO = 129;

        // Fotograma donde comienza la interacción
        private final int INICIO_INTRO = 30;

        private int xIntro;

        // Arreglos de imágenes
        private Image[] spritesEste = new Image[8];
        private Image[] spritesOeste = new Image[8];
        private Image[] spritesIntro = new Image[140];

        // Estados del oso
        private enum Estado {
            CAMINAR_DERECHA,
            CAMINAR_IZQUIERDA,
            INTRO
        }

        private Estado estado = Estado.CAMINAR_DERECHA;

        public Oso(int x, int y) {

            this.x = x;
            this.y = y;

            cargarSprites();
        }

        private void cargarSprites() {

            // Animaciones de caminata
            for (int i = 0; i < 8; i++) {

                spritesEste[i] = cargarImagen(
                        "/resources/images/Oso/Este/oso_"
                                + (i + 1) + ".png"
                );

                spritesOeste[i] = cargarImagen(
                        "/resources/images/Oso/Oeste/oso_"
                                + (i + 1) + ".png"
                );
            }

            // Animación del frailejón
            for (int i = 0; i < 140; i++) {

                String nombre = String.format(
                        "intro_%03d.png", i + 1
                );

                spritesIntro[i] = cargarImagen(
                        "/resources/images/Oso/Intro/" + nombre
                );
            }
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        private Image cargarImagen(String ruta) {

            URL ubicacion = getClass().getResource(ruta);

            if (ubicacion != null) {

                return new ImageIcon(ubicacion).getImage();

            } else {

                System.out.println("No se encontró: " + ruta);

                return null;
            }
        }

        public void mover(int anchoPanel) {

            switch (estado) {

                case CAMINAR_DERECHA:

                    x += velocidad;

                    animarCaminata();

                    // Posición de la escena del frailejón
                    xIntro = anchoPanel - 176;

                    // Cuando el oso llega al lado derecho
                    if (x >= xIntro + 7) {

                        estado = Estado.INTRO;

                        fotograma = INICIO_INTRO;
                        contador = 0;
                    }

                    break;

                case INTRO:

                    animarIntro();

                    break;

                case CAMINAR_IZQUIERDA:

                    x -= velocidad;

                    animarCaminata();

                    // Cuando llega al lado izquierdo
                    if (x <= 20) {

                        x = 20;

                        estado = Estado.CAMINAR_DERECHA;

                        fotograma = 0;
                        contador = 0;
                    }

                    break;
            }
        }

        // Método para controlar al oso con el teclado en PanelJuego

        public void moverManual(int direccion, int anchoPanel) {
            if (direccion > 0) {
                estado = Estado.CAMINAR_DERECHA;
                x += velocidadJuego;
                if (x > anchoPanel - TAMANO) {
                    x = anchoPanel - TAMANO;
                }
                animarCaminata(4); // Cambia de sprite cada 4 frames para sincronizar con velocidadJuego
            } else if (direccion < 0) {
                estado = Estado.CAMINAR_IZQUIERDA;
                x -= velocidadJuego;
                if (x < 0) {
                    x = 0;
                }
                animarCaminata(4);
            } else {
                fotograma = 0;
                contador = 0;
            }
        }

        private void animarCaminata() {
            animarCaminata(velocidadCaminata);
        }

        private void animarCaminata(int ritmo) {
            contador++;
            if (contador >= ritmo) {
                fotograma++;
                if (fotograma >= 8) {
                    fotograma = 0;
                }
                contador = 0;
            }
        }


        private void animarIntro() {

            contador++;

            if (contador >= velocidadIntro) {

                fotograma++;
                contador = 0;

                // Finalizar la animación del frailejón
                if (fotograma >= spritesIntro.length) {

                    estado = Estado.CAMINAR_IZQUIERDA;

                    x = xIntro + 60;

                    fotograma = 0;
                    contador = 0;
                }
            }
        }

        public void dibujar(Graphics g) {

            Graphics2D dibujo = (Graphics2D) g.create();

            // Conservar el estilo pixel art
            dibujo.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
            );

            Image imagenActual;

            switch (estado) {

                case CAMINAR_DERECHA:

                    imagenActual = spritesEste[fotograma];

                    if (imagenActual != null) {

                        dibujo.drawImage(
                                imagenActual,
                                x,
                                y,
                                TAMANO,
                                TAMANO,
                                null
                        );
                    }

                    break;

                case CAMINAR_IZQUIERDA:

                    imagenActual = spritesOeste[fotograma];

                    if (imagenActual != null) {

                        dibujo.drawImage(
                                imagenActual,
                                x,
                                y,
                                TAMANO,
                                TAMANO,
                                null
                        );
                    }

                    break;

                case INTRO:

                    imagenActual = spritesIntro[fotograma];

                    if (imagenActual != null) {

                        dibujo.drawImage(
                                imagenActual,
                                xIntro,
                                y - 26,
                                ANCHO_INTRO,
                                ALTO_INTRO,
                                null
                        );
                    }

                    break;
            }

            dibujo.dispose();
        }
    }
