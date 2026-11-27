package Vistas;

import Controlador.GestorZonas;
import Controlador.Historial;
import Modelo.Jugador;
import Modelo.Obstaculo;
import Modelo.Recolectable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class PanelJuego extends JPanel {

    private static final int ANCHO = 770;
    private static final int ALTO = 425;

    // Posición del oso: y de la imagen cuando está en el suelo y x inicial
    private static final int SUELO_Y = 260;
    private static final int OSO_X = 150;

    // Línea del pasto donde se paran los obstáculos
    private static final int PISO = 346;

    // Paleta de colores (la misma del menú)
    private static final Color DORADO = new Color(234, 195, 68);
    private static final Color CREMA = new Color(245, 243, 220);
    private static final Color VERDE = new Color(20, 45, 35);

    // Vidas
    private static final int VIDAS_INICIALES = 3;
    private static final int VIDAS_MAXIMAS = 5;

    // Tiempos en frames (el timer corre cada 16 ms)
    private static final int DURACION_CONDOR = 8000 / 16;
    private static final int DURACION_ESCUDO = 6000 / 16;
    private static final int DURACION_LENTO = 3000 / 16;
    private static final int DURACION_INMUNIDAD = 90;
    private static final int DURACION_MUERTE = 2200 / 16;
    private static final int FRAMES_POR_NIVEL = 30000 / 16;
    private static final int FRAMES_POR_ACELERACION = 10000 / 16;

    // Velocidad del camino: empieza tranquila y sube poco a poco
    private static final int VELOCIDAD_INICIAL = 4;
    private static final int VELOCIDAD_MAXIMA = 13;

    private Image fondoEscalado;
    private int anchoFondo;
    private int desplazamientoFondo = 0;

    private Font fuentePixel;

    private Oso oso;
    private Timer gameLoop;
    private Random random = new Random();

    // Física vertical (salto y gravedad)
    private int osoY = SUELO_Y;
    private double velocidadY = 0;
    private final double GRAVEDAD = 0.6;
    private final double FUERZA_SALTO = -12;
    private final double FUERZA_DOBLE_SALTO = -10;
    private boolean enElAire = false;
    private int saltosHechos = 0;

    // Banderas de teclas presionadas
    private boolean saltoPresionado = false;
    private boolean arriba = false;
    private boolean abajo = false;

    // Elementos que aparecen en el camino
    private ArrayList<Obstaculo> obstaculos = new ArrayList<>();
    private ArrayList<Recolectable> objetos = new ArrayList<>();
    private int contadorObstaculos = 0;
    private int siguienteObstaculo = 60;
    private int contadorHumo = 0;
    private int siguienteHumo = 240;
    private int contadorObjetos = 0;
    private int siguienteObjeto = 50;

    // Estado de la partida
    private int vidas = VIDAS_INICIALES;
    private int velocidad = VELOCIDAD_INICIAL;
    private int nivel = 1;
    private int tiempoJugado = 0;
    private double metros = 0;
    private int puntosExtra = 0;
    private int gotasPartida = 0;
    private int tiempoCondor = 0;
    private int tiempoEscudo = 0;
    private int tiempoLento = 0;
    private int inmunidad = 0;
    private boolean juegoTerminado = false;
    private List<Jugador> top3;

    // Animación de muerte antes de la pantalla final
    private boolean muriendo = false;
    private int tiempoMuriendo = 0;

    // Mensajes en pantalla
    private String mensaje = "";
    private int tiempoMensaje = 0;
    private int tiempoAyuda = 5000 / 16;

    // Botones que aparecen al terminar la partida
    private JButton btnReintentar;
    private JButton btnZonas;
    private JButton btnMenu;
    private JButton btnMusica;

    public PanelJuego() {
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setFocusable(true);
        setLayout(null);

        cargarFondo();
        cargarFuente();
        oso = new Oso(OSO_X, osoY);

        crearBotones();
        configurarTeclado();
        iniciarBucleJuego();
    }

    // El fondo se escala una sola vez: la imagen normal y al lado la misma
    // volteada, así al repetirla no se nota el corte
    private void cargarFondo() {
        URL ruta = getClass().getResource("/resources/images/PanelBienvenida.png");
        if (ruta == null) {
            System.out.println("No se encontró la imagen de fondo en PanelJuego");
            return;
        }

        Image original = new ImageIcon(ruta).getImage();
        int altoOriginal = original.getHeight(null);

        // Solo se usa la parte izquierda de la imagen: a la derecha hay
        // una banca y un barranco que parecen plataformas al repetirse
        int anchoOriginal = Math.min(1480, original.getWidth(null));

        anchoFondo = anchoOriginal * ALTO / altoOriginal;

        BufferedImage imagen = new BufferedImage(anchoFondo * 2, ALTO, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagen.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(original, 0, 0, anchoFondo, ALTO,
                0, 0, anchoOriginal, altoOriginal, null);
        g.drawImage(original, anchoFondo, 0, anchoFondo * 2, ALTO,
                anchoOriginal, 0, 0, altoOriginal, null);
        g.dispose();

        fondoEscalado = imagen;
    }

    private void cargarFuente() {
        try {
            InputStream is = getClass().getResourceAsStream(
                    "/resources/fonts/PixelifySans-VariableFont_wght.ttf"
            );
            if (is != null) {
                fuentePixel = Font.createFont(Font.TRUETYPE_FONT, is);
            } else {
                fuentePixel = new Font("SansSerif", Font.BOLD, 14);
            }
        } catch (Exception e) {
            fuentePixel = new Font("SansSerif", Font.BOLD, 14);
        }
    }

    private void crearBotones() {
        btnReintentar = crearBoton("JUGAR DE NUEVO", DORADO, 205);
        btnZonas = crearBoton("ZONAS", new Color(150, 200, 235), 330);
        btnMenu = crearBoton("MENÚ", CREMA, 455);

        add(btnReintentar);
        add(btnZonas);
        add(btnMenu);

        // Botón para prender o apagar la música (también con la tecla M)
        btnMusica = new JButton("MÚSICA: SÍ");
        btnMusica.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnMusica.setMargin(new Insets(0, 0, 0, 0));
        btnMusica.setBackground(new Color(16, 41, 30));
        btnMusica.setForeground(DORADO);
        btnMusica.setOpaque(true);
        btnMusica.setBorderPainted(false);
        btnMusica.setFocusPainted(false);
        btnMusica.setFocusable(false);
        btnMusica.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMusica.setBounds(ANCHO / 2 - 50, 10, 100, 24);
        add(btnMusica);
    }

    private JButton crearBoton(String texto, Color color, int x) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 12));
        boton.setMargin(new Insets(0, 0, 0, 0));
        boton.setBackground(color);
        boton.setForeground(VERDE);
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        // Para que la barra espaciadora no presione el botón
        boton.setFocusable(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBounds(x, 322, 115, 36);
        boton.setVisible(false);
        return boton;
    }

    private void configurarTeclado() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int tecla = e.getKeyCode();

                // Saltar (W, Flecha Arriba o Espacio). Si es cóndor, sube.
                if (tecla == KeyEvent.VK_W || tecla == KeyEvent.VK_UP || tecla == KeyEvent.VK_SPACE) {
                    arriba = true;

                    // Solo salta una vez por cada vez que se presiona la tecla
                    if (!saltoPresionado) {
                        saltoPresionado = true;
                        saltar();
                    }
                }

                // Agacharse, o bajar cuando es cóndor (S o Flecha Abajo)
                if (tecla == KeyEvent.VK_S || tecla == KeyEvent.VK_DOWN) {
                    abajo = true;
                }

                // Prender o apagar la música
                if (tecla == KeyEvent.VK_M) {
                    btnMusica.doClick();
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {
                int tecla = e.getKeyCode();

                if (tecla == KeyEvent.VK_W || tecla == KeyEvent.VK_UP || tecla == KeyEvent.VK_SPACE) {
                    arriba = false;
                    saltoPresionado = false;
                }
                if (tecla == KeyEvent.VK_S || tecla == KeyEvent.VK_DOWN) {
                    abajo = false;
                }
            }
        });
    }

    private void saltar() {
        if (juegoTerminado || muriendo || tiempoCondor > 0) {
            return;
        }

        if (!enElAire) {
            velocidadY = FUERZA_SALTO;
            enElAire = true;
            saltosHechos = 1;
        } else if (saltosHechos < 2) {
            // Doble salto
            velocidadY = FUERZA_DOBLE_SALTO;
            saltosHechos = 2;
        }
    }

    private void iniciarBucleJuego() {
        gameLoop = new Timer(16, e -> {
            actualizar();
            repaint();
        });
        gameLoop.start();
    }

    private void actualizar() {
        if (juegoTerminado) {
            return;
        }

        // Mientras el oso muere todo se queda quieto
        if (muriendo) {
            animarMuerte();
            return;
        }

        tiempoJugado++;
        acelerar();
        subirNivel();

        metros += getVelocidadActual() / 20.0;
        desplazamientoFondo += getVelocidadActual();

        actualizarMovimiento();

        generarObstaculos();
        generarHumo();
        generarObjetos();

        moverElementos();
        revisarColisiones();
        actualizarTiempos();
    }

    private void actualizarMovimiento() {

        if (tiempoCondor > 0) {
            // Como cóndor se vuela con arriba y abajo
            if (arriba) osoY -= 5;
            if (abajo) osoY += 5;

            if (osoY < 20) osoY = 20;
            if (osoY > 240) osoY = 240;

            oso.volarComoCondor();
            oso.setY(osoY);
            return;
        }

        // Física de salto y caída
        if (enElAire) {
            osoY += (int) velocidadY;
            velocidadY += GRAVEDAD;

            if (osoY >= SUELO_Y) {
                osoY = SUELO_Y;
                velocidadY = 0;
                enElAire = false;
                saltosHechos = 0;
            }
        }

        // Agacharse (S o Flecha Abajo) solo en el suelo
        if (abajo && !enElAire) {
            oso.agacharse();
        } else {
            oso.correr();
        }
        oso.setY(osoY);
    }

    private void generarObstaculos() {
        contadorObstaculos++;
        if (contadorObstaculos < siguienteObstaculo) {
            return;
        }
        contadorObstaculos = 0;

        int x = ANCHO + 20;

        // Mitad trampas y mitad bolsas
        if (random.nextInt(100) < 50) {
            agregarTrampa(x);

            // Desde el nivel 3, a veces vienen dos trampas juntas
            // (se pasan con un solo salto)
            if (nivel >= 3 && random.nextInt(100) < 15 + nivel * 2) {
                agregarTrampa(x + 80);
            }
        } else {
            obstaculos.add(new Obstaculo(Obstaculo.BOLSA, x, PISO - 22, 38, 22));
        }

        // Entre más nivel, menos espacio entre obstáculos
        int base = Math.max(50, 75 - nivel * 5);
        int extra = Math.max(25, 70 - nivel * 8);
        siguienteObstaculo = base + random.nextInt(extra);
    }

    private void agregarTrampa(int x) {
        obstaculos.add(new Obstaculo(Obstaculo.TRAMPA, x, PISO - 16, 46, 16));
    }

    // Las nubes de humo son los enemigos: llegan por detrás persiguiendo al oso
    private void generarHumo() {
        contadorHumo++;
        if (contadorHumo < siguienteHumo) {
            return;
        }
        contadorHumo = 0;

        int nubes = 0;
        for (Obstaculo o : obstaculos) {
            if (o.getTipo() == Obstaculo.HUMO) nubes++;
        }

        // Con cada nivel pueden haber más nubes al tiempo
        int maximo = Math.min(1 + nivel / 2, 4);
        if (nubes < maximo) {
            // Unas llegan a ras del suelo (hay que saltarlas) y otras a la
            // altura de la cabeza del oso (hay que agacharse)
            int y = SUELO_Y + 26;
            if (random.nextInt(100) < 40) {
                y = SUELO_Y - 5;
            }
            if (tiempoCondor > 0) {
                y = 40 + random.nextInt(180);
            }
            obstaculos.add(new Obstaculo(Obstaculo.HUMO, -140, y, 70, 44));
        }

        siguienteHumo = Math.max(90, 300 - nivel * 25) + random.nextInt(60);
    }

    private void generarObjetos() {
        contadorObjetos++;
        if (contadorObjetos < siguienteObjeto) {
            return;
        }
        contadorObjetos = 0;

        int x = ANCHO + 20;

        // Como cóndor aparecen gotas en el cielo
        if (tiempoCondor > 0) {
            objetos.add(new Recolectable(Recolectable.GOTA, x, 40 + random.nextInt(200)));
            siguienteObjeto = 18 + random.nextInt(15);
            return;
        }

        // A veces hay que saltar para alcanzarlos
        int y = random.nextBoolean() ? 306 : 200;
        int suerte = random.nextInt(100);

        // La flor de frailejón sale más seguido si te faltan vidas
        if (suerte < 18 && vidas < VIDAS_MAXIMAS) {
            objetos.add(new Recolectable(Recolectable.FLOR, x, y - 6));
        } else if (suerte >= 18 && suerte < 25) {
            objetos.add(new Recolectable(Recolectable.PLUMA, x, y));
        } else if (suerte >= 25 && suerte < 32 && tiempoEscudo == 0) {
            objetos.add(new Recolectable(Recolectable.ESCUDO, x, y));
        } else {
            // Una fila de tres gotas
            for (int i = 0; i < 3; i++) {
                objetos.add(new Recolectable(Recolectable.GOTA, x + i * 35, y));
            }
        }

        siguienteObjeto = 40 + random.nextInt(50);
    }

    private void moverElementos() {
        // El escudo de niebla hace que los enemigos vayan 50% más lento
        // La nube tiene que pasar rápido por debajo del oso para poder saltarla
        double rapidezHumo = Math.min(4.5 + nivel * 0.4, 7.5);
        if (tiempoEscudo > 0) {
            rapidezHumo = rapidezHumo * 0.5;
        }

        Rectangle cuerpo = oso.getBounds();
        int centroX = cuerpo.x + cuerpo.width / 2;
        int centroY = cuerpo.y + cuerpo.height / 2;

        for (Obstaculo o : obstaculos) {
            if (o.getTipo() == Obstaculo.HUMO) {
                // Si eres cóndor la nube te sigue al cielo
                if (tiempoCondor > 0) {
                    o.perseguir(centroX, centroY, rapidezHumo);
                } else {
                    o.avanzar(rapidezHumo);
                }
            } else {
                o.mover(getVelocidadActual());
            }
        }
        for (Recolectable r : objetos) {
            r.mover(getVelocidadActual());
        }

        obstaculos.removeIf(o -> o.salioDePantalla());
        objetos.removeIf(r -> r.salioDePantalla());
    }

    private void revisarColisiones() {
        Rectangle cuerpo = oso.getBounds();

        Iterator<Obstaculo> itObstaculos = obstaculos.iterator();
        while (itObstaculos.hasNext()) {
            Obstaculo o = itObstaculos.next();

            if (!cuerpo.intersects(o.getBounds())) {
                continue;
            }

            if (o.getTipo() == Obstaculo.BOLSA) {
                // La bolsa no quita vida, pero te deja lento unos segundos
                if (tiempoCondor == 0) {
                    tiempoLento = DURACION_LENTO;
                    mostrarMensaje("¡PISASTE UNA BOLSA!");
                    itObstaculos.remove();
                }
            } else if (o.getTipo() == Obstaculo.TRAMPA && tiempoEscudo > 0) {
                // El escudo de niebla te protege de las trampas
            } else {
                recibirGolpe();
            }
        }

        Iterator<Recolectable> itObjetos = objetos.iterator();
        while (itObjetos.hasNext()) {
            Recolectable r = itObjetos.next();
            if (cuerpo.intersects(r.getBounds())) {
                recoger(r);
                itObjetos.remove();
            }
        }
    }

    private void recoger(Recolectable r) {
        switch (r.getTipo()) {
            case Recolectable.GOTA:
                puntosExtra += 10;
                gotasPartida++;
                break;

            case Recolectable.FLOR:
                if (vidas < VIDAS_MAXIMAS) {
                    vidas++;
                    mostrarMensaje("+1 VIDA");
                } else {
                    mostrarMensaje("VIDAS AL MÁXIMO");
                }
                break;

            case Recolectable.PLUMA:
                tiempoCondor = DURACION_CONDOR;
                tiempoLento = 0;
                enElAire = false;
                velocidadY = 0;
                saltosHechos = 0;
                if (osoY > 240) osoY = 240;
                oso.empezarCondor();
                mostrarMensaje("¡ERES UN CÓNDOR!");
                break;

            case Recolectable.ESCUDO:
                tiempoEscudo = DURACION_ESCUDO;
                mostrarMensaje("ESCUDO DE NIEBLA");
                break;
        }
    }

    private void recibirGolpe() {
        // Como cóndor eres invencible
        if (inmunidad > 0 || tiempoCondor > 0 || juegoTerminado) {
            return;
        }

        vidas--;
        inmunidad = DURACION_INMUNIDAD;

        if (vidas <= 0) {
            empezarMuerte();
        }
    }

    private void empezarMuerte() {
        muriendo = true;
        tiempoMuriendo = 0;
        inmunidad = 0;
        mensaje = "";
        tiempoMensaje = 0;
        oso.morir();
    }

    private void animarMuerte() {
        tiempoMuriendo++;
        oso.animarMuerte();

        // Si estaba saltando, cae al suelo
        if (osoY < SUELO_Y) {
            osoY = Math.min(SUELO_Y, osoY + 8);
            oso.setY(osoY);
        }

        if (tiempoMuriendo >= DURACION_MUERTE) {
            terminarJuego();
        }
    }

    private void actualizarTiempos() {
        if (tiempoCondor > 0) {
            tiempoCondor--;
            if (tiempoCondor == 0) {
                terminarCondor();
            }
        }

        if (tiempoEscudo > 0) tiempoEscudo--;
        if (tiempoLento > 0) tiempoLento--;
        if (inmunidad > 0) inmunidad--;
        if (tiempoMensaje > 0) tiempoMensaje--;
        if (tiempoAyuda > 0) tiempoAyuda--;
    }

    private void terminarCondor() {
        // El oso cae al suelo y todavía puede hacer un salto en el aire
        enElAire = true;
        velocidadY = 0;
        saltosHechos = 1;
        oso.correr();

        // Un momento sin recibir daño para que pueda caer tranquilo
        if (inmunidad < 60) {
            inmunidad = 60;
        }
    }

    // El oso corre solo y cada 10 segundos va un poco más rápido
    private void acelerar() {
        if (tiempoJugado % FRAMES_POR_ACELERACION == 0 && velocidad < VELOCIDAD_MAXIMA) {
            velocidad++;
        }
    }

    // La bolsa plástica te hace perder velocidad unos segundos
    private int getVelocidadActual() {
        if (tiempoLento > 0) {
            return Math.max(2, velocidad / 2);
        }
        return velocidad;
    }

    // Cada 30 segundos sube el nivel: aparecen más enemigos
    private void subirNivel() {
        int nuevoNivel = tiempoJugado / FRAMES_POR_NIVEL + 1;

        if (nuevoNivel > nivel) {
            nivel = nuevoNivel;
            mostrarMensaje("NIVEL " + nivel + " - ¡MÁS ENEMIGOS!");
        }
    }

    private void mostrarMensaje(String texto) {
        mensaje = texto;
        tiempoMensaje = 90;
    }

    public int getPuntaje() {
        return (int) metros + puntosExtra;
    }

    private void terminarJuego() {
        juegoTerminado = true;
        gameLoop.stop();

        // Las gotas se guardan para limpiar zonas contaminadas
        GestorZonas.agregarGotas(gotasPartida);
        repaint();

        // Se pide el nombre después de pintar la pantalla final
        SwingUtilities.invokeLater(() -> guardarPuntaje());
    }

    private void guardarPuntaje() {
        String nombre = JOptionPane.showInputDialog(
                this,
                "Puntaje: " + getPuntaje() + "\nEscribe tu nombre:",
                "Fin del juego",
                JOptionPane.QUESTION_MESSAGE
        );

        if (nombre == null || nombre.trim().isEmpty()) {
            nombre = "Anónimo";
        }
        nombre = nombre.trim();
        if (nombre.length() > 12) {
            nombre = nombre.substring(0, 12);
        }

        Historial.guardarPuntaje(new Jugador(nombre, getPuntaje()));
        top3 = Historial.getTop3();

        btnReintentar.setVisible(true);
        btnZonas.setVisible(true);
        btnMenu.setVisible(true);
        repaint();
    }

    public void reiniciar() {
        obstaculos.clear();
        objetos.clear();

        osoY = SUELO_Y;
        velocidadY = 0;
        enElAire = false;
        saltosHechos = 0;
        saltoPresionado = false;
        arriba = false;
        abajo = false;

        vidas = VIDAS_INICIALES;
        velocidad = VELOCIDAD_INICIAL;
        nivel = 1;
        tiempoJugado = 0;
        metros = 0;
        puntosExtra = 0;
        gotasPartida = 0;
        tiempoCondor = 0;
        tiempoEscudo = 0;
        tiempoLento = 0;
        inmunidad = 0;
        contadorObstaculos = 0;
        siguienteObstaculo = 60;
        contadorHumo = 0;
        siguienteHumo = 240;
        contadorObjetos = 0;
        siguienteObjeto = 50;
        tiempoMensaje = 0;
        tiempoAyuda = 5000 / 16;
        top3 = null;
        juegoTerminado = false;
        muriendo = false;
        tiempoMuriendo = 0;

        oso.setY(osoY);
        oso.correr();

        btnReintentar.setVisible(false);
        btnZonas.setVisible(false);
        btnMenu.setVisible(false);

        gameLoop.start();
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D dibujo = (Graphics2D) g.create();

        dibujo.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        dibujo.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        dibujarFondo(dibujo);

        // Primero lo que está en el suelo
        for (Obstaculo o : obstaculos) {
            if (o.getTipo() != Obstaculo.HUMO) o.dibujar(dibujo);
        }
        for (Recolectable r : objetos) {
            r.dibujar(dibujo);
        }

        // Al morir la pantalla se oscurece poco a poco, menos el oso
        if (muriendo) {
            int oscuridad = Math.min(140, tiempoMuriendo * 2);
            dibujo.setColor(new Color(0, 0, 0, oscuridad));
            dibujo.fillRect(0, 0, getWidth(), getHeight());
        }

        dibujarOso(dibujo);

        // El humo va por encima de todo
        for (Obstaculo o : obstaculos) {
            if (o.getTipo() == Obstaculo.HUMO) o.dibujar(dibujo);
        }

        dibujarHud(dibujo);
        dibujarMensajes(dibujo);

        if (juegoTerminado) {
            dibujarFinDelJuego(dibujo);
        }

        dibujo.dispose();
    }

    private void dibujarFondo(Graphics2D dibujo) {
        if (fondoEscalado == null) {
            dibujo.setColor(VERDE);
            dibujo.fillRect(0, 0, getWidth(), getHeight());
            return;
        }

        int largo = anchoFondo * 2;
        int x = -(desplazamientoFondo % largo);

        dibujo.drawImage(fondoEscalado, x, 0, this);
        dibujo.drawImage(fondoEscalado, x + largo, 0, this);

        // Con el escudo de niebla se ve una neblina suave en todo el páramo
        if (tiempoEscudo > 0) {
            dibujo.setColor(new Color(225, 235, 245, 55));
            dibujo.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void dibujarOso(Graphics2D dibujo) {
        // Parpadea mientras es inmune después de un golpe
        if (inmunidad > 0 && (inmunidad / 5) % 2 == 0) {
            return;
        }

        oso.dibujar(dibujo);

        // Aura del escudo de niebla (parpadea cuando se va a acabar)
        if (tiempoEscudo > 0 && (tiempoEscudo > 120 || (tiempoEscudo / 8) % 2 == 0)) {
            Rectangle cuerpo = oso.getBounds();
            dibujo.setColor(new Color(215, 235, 250, 90));
            dibujo.fillOval(cuerpo.x - 14, cuerpo.y - 14, cuerpo.width + 28, cuerpo.height + 28);
            dibujo.setColor(new Color(200, 230, 255, 200));
            dibujo.setStroke(new BasicStroke(2));
            dibujo.drawOval(cuerpo.x - 14, cuerpo.y - 14, cuerpo.width + 28, cuerpo.height + 28);
            dibujo.setStroke(new BasicStroke(1));
        }

        // Aviso en el borde cuando viene una nube de humo por detrás
        for (Obstaculo o : obstaculos) {
            if (o.getTipo() == Obstaculo.HUMO && o.getX() < -10 && o.getX() > -140) {
                int ay = o.getY() + 10;
                dibujo.setColor(new Color(220, 60, 50));
                int[] px = {6, 30, 18};
                int[] py = {ay + 22, ay + 22, ay};
                dibujo.fillPolygon(px, py, 3);
                dibujo.setColor(Color.WHITE);
                dibujo.setFont(new Font("SansSerif", Font.BOLD, 14));
                dibujo.drawString("!", 15, ay + 20);
            }
        }

        // Rayitas detrás del oso cuando está lento por la bolsa
        if (tiempoLento > 0) {
            Rectangle cuerpo = oso.getBounds();
            dibujo.setColor(new Color(255, 255, 255, 170));
            dibujo.setStroke(new BasicStroke(2));
            for (int i = 0; i < 3; i++) {
                int ly = cuerpo.y + 12 + i * 16;
                dibujo.drawLine(cuerpo.x - 22, ly, cuerpo.x - 6, ly);
            }
            dibujo.setStroke(new BasicStroke(1));
        }
    }

    private void dibujarHud(Graphics2D dibujo) {
        // Recuadro de vidas, puntos y gotas
        dibujo.setColor(new Color(16, 41, 30, 170));
        dibujo.fillRoundRect(10, 10, 160, 80, 14, 14);

        for (int i = 0; i < VIDAS_MAXIMAS; i++) {
            dibujarCorazon(dibujo, 20 + i * 29, 18, i < vidas);
        }

        dibujo.setFont(fuentePixel.deriveFont(Font.BOLD, 17f));
        dibujo.setColor(DORADO);
        dibujo.drawString("PUNTOS " + getPuntaje(), 20, 60);

        dibujarGotaPequena(dibujo, 20, 66);
        dibujo.setColor(new Color(150, 205, 245));
        dibujo.drawString("x " + gotasPartida, 38, 82);

        // Recuadro de metros y nivel
        dibujo.setColor(new Color(16, 41, 30, 170));
        dibujo.fillRoundRect(ANCHO - 150, 10, 140, 58, 14, 14);

        dibujo.setColor(CREMA);
        dibujo.drawString("METROS " + (int) metros, ANCHO - 140, 34);
        dibujo.setColor(DORADO);
        dibujo.drawString("NIVEL " + nivel, ANCHO - 140, 58);

        // Barras de los poderes y efectos activos
        int y = 98;
        if (tiempoCondor > 0) {
            dibujarBarra(dibujo, "CÓNDOR", y, tiempoCondor, DURACION_CONDOR, new Color(40, 40, 45));
            y += 24;
        }
        if (tiempoEscudo > 0) {
            dibujarBarra(dibujo, "NIEBLA", y, tiempoEscudo, DURACION_ESCUDO, new Color(150, 200, 235));
            y += 24;
        }
        if (tiempoLento > 0) {
            dibujarBarra(dibujo, "LENTO", y, tiempoLento, DURACION_LENTO, new Color(220, 90, 70));
        }
    }

    private void dibujarGotaPequena(Graphics2D dibujo, int x, int y) {
        dibujo.setColor(new Color(60, 150, 230));
        int[] px = {x + 7, x + 2, x + 12};
        int[] py = {y, y + 8, y + 8};
        dibujo.fillPolygon(px, py, 3);
        dibujo.fillOval(x + 1, y + 4, 12, 12);
    }

    private void dibujarCorazon(Graphics2D dibujo, int x, int y, boolean lleno) {
        int[] px = {x, x + 22, x + 11};
        int[] py = {y + 6, y + 6, y + 19};

        if (lleno) {
            dibujo.setColor(new Color(225, 55, 65));
        } else {
            dibujo.setColor(new Color(0, 0, 0, 90));
        }
        dibujo.fillOval(x, y, 12, 12);
        dibujo.fillOval(x + 10, y, 12, 12);
        dibujo.fillPolygon(px, py, 3);

        if (lleno) {
            dibujo.setColor(new Color(255, 170, 175));
            dibujo.fillOval(x + 3, y + 3, 4, 4);
        }
    }

    private void dibujarBarra(Graphics2D dibujo, String nombre, int y, int tiempo, int total, Color color) {
        dibujo.setColor(new Color(16, 41, 30, 170));
        dibujo.fillRoundRect(10, y, 160, 20, 10, 10);

        dibujo.setFont(fuentePixel.deriveFont(Font.BOLD, 12f));
        dibujo.setColor(CREMA);
        dibujo.drawString(nombre, 18, y + 15);

        int largo = 80 * tiempo / total;
        dibujo.setColor(new Color(255, 255, 255, 60));
        dibujo.fillRoundRect(80, y + 6, 80, 8, 6, 6);
        dibujo.setColor(color);
        dibujo.fillRoundRect(80, y + 6, largo, 8, 6, 6);
    }

    private void dibujarMensajes(Graphics2D dibujo) {
        if (tiempoMensaje > 0) {
            dibujarTextoCentrado(dibujo, mensaje, fuentePixel.deriveFont(Font.BOLD, 26f), CREMA, 130);
        }

        if (tiempoAyuda > 0 && !juegoTerminado) {
            String ayuda = "↑ / W / ESPACIO saltar (x2 = doble salto)     ↓ / S agacharse     M música";
            Font fuente = new Font("SansSerif", Font.BOLD, 13);
            dibujo.setFont(fuente);
            int ancho = dibujo.getFontMetrics().stringWidth(ayuda) + 30;

            dibujo.setColor(new Color(16, 41, 30, 180));
            dibujo.fillRoundRect((ANCHO - ancho) / 2, ALTO - 42, ancho, 28, 14, 14);
            dibujarTextoCentrado(dibujo, ayuda, fuente, Color.WHITE, ALTO - 23);
        }
    }

    private void dibujarFinDelJuego(Graphics2D dibujo) {
        dibujo.setColor(new Color(0, 0, 0, 150));
        dibujo.fillRect(0, 0, getWidth(), getHeight());

        int anchoCaja = 390;
        int altoCaja = 330;
        int x = (ANCHO - anchoCaja) / 2;
        int y = 45;

        dibujo.setColor(new Color(16, 41, 30, 235));
        dibujo.fillRoundRect(x, y, anchoCaja, altoCaja, 25, 25);
        dibujo.setColor(new Color(180, 185, 139));
        dibujo.setStroke(new BasicStroke(1.5f));
        dibujo.drawRoundRect(x, y, anchoCaja, altoCaja, 25, 25);

        dibujarTextoCentrado(dibujo, "FIN DEL JUEGO", fuentePixel.deriveFont(Font.BOLD, 36f), CREMA, y + 46);

        dibujarTextoCentrado(dibujo,
                "Puntaje: " + getPuntaje() + "     Metros: " + (int) metros,
                new Font("SansSerif", Font.BOLD, 15), Color.WHITE, y + 76);

        dibujarTextoCentrado(dibujo,
                "Recogiste " + gotasPartida + " gotas  ·  Tienes " + GestorZonas.getGotas() + " para limpiar zonas",
                new Font("SansSerif", Font.PLAIN, 13), new Color(150, 205, 245), y + 100);

        // Línea decorativa
        dibujo.setColor(DORADO);
        dibujo.drawLine(ANCHO / 2 - 85, y + 114, ANCHO / 2 + 85, y + 114);

        dibujarTextoCentrado(dibujo, "RANKING", fuentePixel.deriveFont(Font.BOLD, 20f), DORADO, y + 142);

        if (top3 == null) {
            dibujarTextoCentrado(dibujo, "Guardando puntaje...",
                    new Font("SansSerif", Font.PLAIN, 14), Color.WHITE, y + 178);
            return;
        }

        dibujo.setFont(new Font("SansSerif", Font.BOLD, 15));
        for (int i = 0; i < 3; i++) {
            int fila = y + 170 + i * 26;

            String nombre = "---";
            String puntos = "";
            if (i < top3.size()) {
                Jugador j = top3.get(i);
                nombre = j.getNombre() == null ? "Anónimo" : j.getNombre();
                puntos = String.valueOf(j.getPuntaje());
            }

            dibujo.setColor(i == 0 ? DORADO : CREMA);
            dibujo.drawString((i + 1) + ".  " + nombre, x + 90, fila);

            int anchoPuntos = dibujo.getFontMetrics().stringWidth(puntos);
            dibujo.drawString(puntos, x + anchoCaja - 90 - anchoPuntos, fila);
        }
    }

    private void dibujarTextoCentrado(Graphics2D dibujo, String texto, Font fuente, Color color, int y) {
        dibujo.setFont(fuente);
        int x = (getWidth() - dibujo.getFontMetrics().stringWidth(texto)) / 2;

        dibujo.setColor(new Color(0, 0, 0, 170));
        dibujo.drawString(texto, x + 2, y + 2);

        dibujo.setColor(color);
        dibujo.drawString(texto, x, y);
    }

    public JButton getBtnReintentar() {
        return btnReintentar;
    }

    public JButton getBtnZonas() {
        return btnZonas;
    }

    public JButton getBtnMenu() {
        return btnMenu;
    }

    public JButton getBtnMusica() {
        return btnMusica;
    }

    public void detenerJuego() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }
}
