
    package Vistas;

    import javax.swing.*;
    import java.awt.*;
    import java.net.URL;

    public class Oso {

        private int x;
        private int y;

        // Velocidad de desplazamiento
        private int velocidad = 1;

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
            INTRO,
            AGACHADO,
            CONDOR,
            MUERTO
        }

        // Animación cuando el oso pierde todas las vidas
        private Image[] spritesMuerte = new Image[8];
        private int tiempoMuerte = 0;

        // Animación del cóndor: primero despega y luego aletea
        private Image[] spritesDespegue = new Image[5];
        private Image[] spritesVuelo = new Image[8];
        private int aleteo = 0;
        private final int TAMANO_CONDOR = 115;

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

            // Animación del cóndor
            for (int i = 0; i < spritesDespegue.length; i++) {
                spritesDespegue[i] = cargarImagen(
                        "/resources/images/Condor/despegue_" + (i + 1) + ".png"
                );
            }
            for (int i = 0; i < spritesVuelo.length; i++) {
                spritesVuelo[i] = cargarImagen(
                        "/resources/images/Condor/vuelo_" + (i + 1) + ".png"
                );
            }

            // Animación de muerte
            for (int i = 0; i < spritesMuerte.length; i++) {
                spritesMuerte[i] = cargarImagen(
                        "/resources/images/Oso/Muerte/muerte_" + (i + 1) + ".png"
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

        // Métodos para el PanelJuego

        public void correr() {
            estado = Estado.CAMINAR_DERECHA;
            animarCaminata(4);
        }

        public void agacharse() {
            estado = Estado.AGACHADO;
            animarCaminata(6);
        }

        // Se llama cuando el oso recoge la pluma para empezar desde el despegue
        public void empezarCondor() {
            estado = Estado.CONDOR;
            aleteo = 0;
        }

        public void volarComoCondor() {
            estado = Estado.CONDOR;
            aleteo++;
        }

        // Empieza la animación de muerte
        public void morir() {
            estado = Estado.MUERTO;
            tiempoMuerte = 0;
        }

        public void animarMuerte() {
            tiempoMuerte++;
        }

        // Caja de colisión: solo el cuerpo, sin el espacio vacío del sprite
        public Rectangle getBounds() {
            if (estado == Estado.CONDOR) {
                return new Rectangle(x + 22, y + 22, 70, 66);
            }
            if (estado == Estado.AGACHADO) {
                return new Rectangle(x + 15, y + 50, 80, 36);
            }
            return new Rectangle(x + 15, y + 24, 75, 62);
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

                case AGACHADO:

                    // El mismo sprite pero aplastado, con las patas en el suelo
                    imagenActual = spritesEste[fotograma];

                    if (imagenActual != null) {

                        dibujo.drawImage(
                                imagenActual,
                                x - 5,
                                y + 30,
                                TAMANO + 10,
                                72,
                                null
                        );
                    }

                    break;

                case CONDOR:

                    dibujarCondor(dibujo);

                    break;

                case MUERTO:

                    dibujarMuerte(dibujo);

                    break;
            }

            dibujo.dispose();
        }

        // El oso se tambalea y cae; cuando queda en el suelo le salen estrellitas
        private void dibujarMuerte(Graphics2D dibujo) {

            // Cada cuadro dura 7 frames y el último se queda quieto
            int cuadro = Math.min(tiempoMuerte / 7, spritesMuerte.length - 1);
            Image imagenActual = spritesMuerte[cuadro];

            if (imagenActual != null) {
                dibujo.drawImage(imagenActual, x, y, TAMANO, TAMANO, null);
            }

            // Estrellitas de mareo girando sobre la cabeza
            if (cuadro >= spritesMuerte.length - 2) {
                dibujo.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                int centroX = x + 85;
                int centroY = y + 52;

                for (int i = 0; i < 3; i++) {
                    double angulo = tiempoMuerte * 0.12 + i * (Math.PI * 2 / 3);
                    int ex = centroX + (int) (Math.cos(angulo) * 20);
                    int ey = centroY + (int) (Math.sin(angulo) * 6);
                    dibujarEstrella(dibujo, ex, ey);
                }
            }
        }

        private void dibujarEstrella(Graphics2D dibujo, int cx, int cy) {
            int[] px = new int[10];
            int[] py = new int[10];

            for (int i = 0; i < 10; i++) {
                double radio = (i % 2 == 0) ? 6 : 2.5;
                double angulo = -Math.PI / 2 + i * Math.PI / 5;
                px[i] = cx + (int) (Math.cos(angulo) * radio);
                py[i] = cy + (int) (Math.sin(angulo) * radio);
            }

            dibujo.setColor(new Color(255, 220, 60));
            dibujo.fillPolygon(px, py, 10);
            dibujo.setColor(new Color(180, 130, 20));
            dibujo.drawPolygon(px, py, 10);
        }

        // Primero se ve el despegue (se agacha y salta) y después aletea sin parar
        private void dibujarCondor(Graphics2D dibujo) {

            int cuadro = aleteo / 4;
            Image imagenActual;

            if (cuadro < spritesDespegue.length) {
                imagenActual = spritesDespegue[cuadro];
            } else {
                int indice = (aleteo / 5) % spritesVuelo.length;
                imagenActual = spritesVuelo[indice];
            }

            if (imagenActual != null) {
                dibujo.drawImage(
                        imagenActual,
                        x,
                        y,
                        TAMANO_CONDOR,
                        TAMANO_CONDOR,
                        null
                );
            }
        }
    }
