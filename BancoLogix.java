import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class BancoLogix extends JFrame {

    private JTextField usuarioField;
    private JPasswordField contrasenaField;
    private static Map<String, Usuario> usuarios = new HashMap<>();
    private Usuario usuarioActual;

    /**
    * Constructor de la clase BancoLogix.
    * Este constructor inicializa la interfaz grafica del simulador de cajero automatico.
    * La interfaz incluye campos para ingresar el usuario y la contrasena, asi como botones
      para ingresar, registrar y salir. Ademas, se carga una lista de usuarios predefinidos.
    */

    public BancoLogix() {
        setTitle("Banco Logix");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        // Funcion cargar usuarios desde archivo txt
        cargarUsuarios();

        // Panel superior
        JPanel panelSuperior = new JPanel();
        panelSuperior.setBackground(new Color(0, 0, 128));
        panelSuperior.setBounds(0, 0, 400, 60);
        panelSuperior.setLayout(null);

        JLabel etiquetaBienvenida = new JLabel("BIENVENIDO");
        etiquetaBienvenida.setForeground(Color.YELLOW);
        etiquetaBienvenida.setFont(new Font("Arial", Font.BOLD, 18));
        etiquetaBienvenida.setBounds(150, 5, 200, 20);
        panelSuperior.add(etiquetaBienvenida);

        JLabel etiquetaBanco = new JLabel("BANCO LOGIX");
        etiquetaBanco.setForeground(Color.YELLOW);
        etiquetaBanco.setFont(new Font("Arial", Font.BOLD, 18));
        etiquetaBanco.setBounds(142, 30, 200, 20);
        panelSuperior.add(etiquetaBanco);

        add(panelSuperior);

        // Campos de texto y etiquetas
        JLabel usuarioLabel = new JLabel("Usuario:");
        usuarioLabel.setBounds(50, 80, 100, 25);
        add(usuarioLabel);

        usuarioField = new JTextField();
        usuarioField.setBounds(150, 80, 150, 25);
        add(usuarioField);

        JLabel contrasenaLabel = new JLabel("Contrasena:");
        contrasenaLabel.setBounds(50, 120, 100, 25);
        add(contrasenaLabel);

        contrasenaField = new JPasswordField();
        contrasenaField.setBounds(150, 120, 150, 25);
        add(contrasenaField);

        // Botones
        JButton ingresarButton = new JButton("Ingresar");
        ingresarButton.setBounds(50, 170, 100, 25);
        add(ingresarButton);

        JButton registrarButton = new JButton("Registrar");
        registrarButton.setBounds(160, 170, 100, 25);
        add(registrarButton);

        JButton salirButton = new JButton("Salir");
        salirButton.setBounds(270, 170, 100, 25);
        add(salirButton);

        // Funcionalidad de los botones
        ingresarButton.addActionListener(e -> ingresar());

        registrarButton.addActionListener(e -> registrar());

        salirButton.addActionListener(e -> System.exit(0));
    }

    /**
    * Carga los usuarios desde un archivo de texto y los almacena en un mapa.
    * El archivo debe tener el formato: nombreUsuario,contrasena,saldo,numeroCuenta en cada linea.
    * 
    * Variables:
    * - BufferedReader reader: Se utiliza para leer el archivo "usuarios.txt".
    * - String linea: Almacena cada linea leida del archivo.
    * - String[] datos: Almacena los datos de cada usuario separados por comas.
    * - String nombreUsuario: Almacena el nombre de usuario.
    * - String contrasena: Almacena la contrasena del usuario.
    * - double saldo: Almacena el saldo del usuario.
    * - String numeroCuenta: Almacena el numero de cuenta del usuario.
    * - Usuario usuario: Objeto que representa al usuario con sus datos.
    * 
    * @throws IOException Si ocurre un error al leer el archivo. En este caso, se muestra un mensaje de error.
    */
    private void cargarUsuarios() {
        try (BufferedReader reader = new BufferedReader(new FileReader("usuarios.txt"))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                String nombreUsuario = datos[0];
                String contrasena = datos[1];
                double saldo = Double.parseDouble(datos[2]);
                String numeroCuenta = datos[3];
                Usuario usuario = new Usuario(nombreUsuario, contrasena, saldo, numeroCuenta);
                usuarios.put(nombreUsuario, usuario);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios");
        }
    }

     /**
     * Registra un nuevo usuario en el sistema.
     * Solicita al usuario que ingrese un nombre de usuario y una contrasena.
     * Si el nombre de usuario ya existe o los campos estan vacios, muestra un mensaje de error.
     * Genera un numero de cuenta unico y asigna un saldo inicial de 0.0 al nuevo usuario.
     * Guarda el nuevo usuario en el mapa de usuarios y en el archivo correspondiente.
     * 
     *  * @throws HeadlessException Si se utiliza en un entorno sin interfaz grafica.
     */
    private void registrar() {
        String nuevoUsuario = JOptionPane.showInputDialog(this, "Ingrese nuevo nombre de usuario:");
        if (nuevoUsuario == null || nuevoUsuario.isEmpty()) {
            return;
        }
        if (usuarios.containsKey(nuevoUsuario)) {
            JOptionPane.showMessageDialog(this, "El usuario ya existe. Intente con otro nombre.");
            return;
        }
        String nuevaContrasena = JOptionPane.showInputDialog(this, "Ingrese una contrasena:");
        if (nuevaContrasena == null || nuevaContrasena.isEmpty()) {
            return;
        }

        String numeroCuenta = generarNumeroCuenta();
        double saldoInicial = 0.0;

        Usuario nuevoUsuarioObj = new Usuario(nuevoUsuario, nuevaContrasena, saldoInicial, numeroCuenta);
        usuarios.put(nuevoUsuario, nuevoUsuarioObj);

        guardarUsuarioEnArchivo(nuevoUsuarioObj);
        crearArchivoHistorial(nuevoUsuarioObj);

        JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente. Su numero de cuenta es: " + numeroCuenta);
    }

     /**
     * Crea un archivo de historial de transacciones para un usuario especifico.
     * El archivo se nombra como "historial_nombreUsuario.txt" y se inicializa con una linea de encabezado.
     * 
     * @param usuario El objeto Usuario para el cual se crea el archivo de historial.
     * 
     * @throws IOException Si ocurre un error al crear o escribir en el archivo. En este caso, se muestra un mensaje de error.
     */
    private void crearArchivoHistorial(Usuario usuario) {
        try (FileWriter writer = new FileWriter("historial_" + usuario.getNombreUsuario() + ".txt", true)) {
            writer.write("Historial de transacciones de " + usuario.getNombreUsuario() + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al crear el historial del usuario");
        }
    }

    /**
     * Guarda la informacion de un usuario en el archivo de texto "usuarios.txt".
     * La informacion se guarda en el formato: nombreUsuario,contrasena,saldo,numeroCuenta en una nueva linea.
     * 
     * @param usuario El objeto Usuario cuya informacion se va a guardar.
     * 
     * @throws IOException Si ocurre un error al escribir en el archivo. En este caso, se muestra un mensaje de error.
     */
    private void guardarUsuarioEnArchivo(Usuario usuario) {
        try (FileWriter writer = new FileWriter("usuarios.txt", true)) {
            writer.write(usuario.getNombreUsuario() + "," + usuario.getContrasena() + "," + usuario.getSaldo() + "," + usuario.getNumeroCuenta() + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el usuario");
        }
    }

    /**
     * Guarda la informacion de todos los usuarios en el archivo de texto "usuarios.txt".
     * La informacion de cada usuario se guarda en el formato: nombreUsuario,contrasena,saldo,numeroCuenta en una nueva linea.
     * 
     * @throws IOException Si ocurre un error al escribir en el archivo. En este caso, se muestra un mensaje de error.
     */
    private void guardarTodosLosUsuarios() {
        try (FileWriter writer = new FileWriter("usuarios.txt")) {
            for (Usuario usuario : usuarios.values()) {
                writer.write(usuario.getNombreUsuario() + "," + usuario.getContrasena() + "," + usuario.getSaldo() + "," + usuario.getNumeroCuenta() + "\n");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar los usuarios");
        }
    }

    /**
    * genera un valor entero aleatorio para asociarlo a un usuario como su numero de cuenta
    */
    private String generarNumeroCuenta() {
        return String.valueOf((int) (Math.random() * 900000000) + 100000000);
    }

    /**
     * Verifica las credenciales del usuario y permite el acceso si son correctas.
     * Obtiene el nombre de usuario y la contrasena de los campos de texto correspondientes.
     * Si el usuario existe y la contrasena es correcta, establece el usuario actual y abre el menu principal.
     * Si las credenciales son incorrectas, muestra un mensaje de error.
     * 
     * @throws HeadlessException Si se utiliza en un entorno sin interfaz grafica.
     */
    private void ingresar() {
        String usuario = usuarioField.getText();
        String contrasena = new String(contrasenaField.getPassword());

        if (usuarios.containsKey(usuario)) {
            Usuario usuarioObj = usuarios.get(usuario);
            if (usuarioObj.getContrasena().equals(contrasena)) {
                usuarioActual = usuarioObj;
                abrirMenuPrincipal();
            } else {
                JOptionPane.showMessageDialog(this, "Contrasena incorrecta");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Usuario no encontrado");
        }
    }
    
    /**
     * Abre la interfaz del menu principal del cajero automatico.
     * Elimina todos los componentes actuales de la ventana y repinta la interfaz.
     * Configura el titulo y el tamano de la ventana.
     * Muestra la informacion del usuario actual y proporciona botones para las diferentes operaciones:
     * - Consultar saldo
     * - Depositar dinero
     * - Retirar dinero
     * - Transferir dinero
     * - Ver historial
     * - Salir
     * 
     * Cada boton tiene un ActionListener asociado que llama al método correspondiente para realizar la operacion.
     * 
     * @throws HeadlessException Si se utiliza en un entorno sin interfaz grafica.
     */
    private void abrirMenuPrincipal() {
        getContentPane().removeAll();
        repaint();
        setTitle("Banco Logix - Menu Principal");
        setSize(400, 420);

        JLabel usuarioInfo = new JLabel("Usuario: " + usuarioActual.getNombreUsuario());
        usuarioInfo.setBounds(50, 0, 150, 25);
        add(usuarioInfo);

        JLabel numeroDeCuenta = new JLabel("N de Cuenta: " + usuarioActual.getNumeroCuenta());
        numeroDeCuenta.setBounds(50, 20, 150, 25);
        add(numeroDeCuenta);

        JButton consultarSaldoButton = new JButton("Consultar saldo");
        consultarSaldoButton.setBounds(50, 80, 150, 30);
        add(consultarSaldoButton);

        JButton depositarButton = new JButton("Depositar dinero");
        depositarButton.setBounds(50, 120, 150, 30);
        add(depositarButton);

        JButton retirarButton = new JButton("Retirar dinero");
        retirarButton.setBounds(50, 160, 150, 30);
        add(retirarButton);


        JButton transferirButton = new JButton("Transferir dinero");
        transferirButton.setBounds(50, 200, 150, 30);
        add(transferirButton);


        JButton verHistorialButton = new JButton("Ver Historial");
        verHistorialButton.setBounds(50, 240, 150, 30);
        add(verHistorialButton);

        JButton salirButton = new JButton("Salir");
        salirButton.setBounds(150, 320, 100, 30);
        add(salirButton);

        salirButton.addActionListener(e -> System.exit(0));

        consultarSaldoButton.addActionListener(e ->abrirVentanaSaldo());

        depositarButton.addActionListener(e -> abrirVentanaDepositar());

        retirarButton.addActionListener(e -> abrirVentanaRetirar());

        transferirButton.addActionListener(e -> abrirVentanaTransferencia());

        verHistorialButton.addActionListener(e -> abrirVentanaHistorial());
    }

    /**
     * Abre una nueva ventana para mostrar el saldo disponible del usuario actual.
     * La ventana incluye un titulo, una etiqueta que muestra el saldo y un boton para cerrar la ventana.
     * 
     * @throws HeadlessException Si se utiliza en un entorno sin interfaz grafica.
     */
    private void abrirVentanaSaldo() {
        JFrame saldoFrame = new JFrame("Consulta de Saldo");
        saldoFrame.setSize(400, 200);
        saldoFrame.setLocationRelativeTo(this);
        saldoFrame.setLayout(null);

        JLabel saldoLabel = new JLabel("Consulta de Saldo");
        saldoLabel.setForeground(new Color(255, 69, 0)); // Naranja
        saldoLabel.setFont(new Font("Arial", Font.BOLD, 18));
        saldoLabel.setBounds(80, 5, 200, 30);
        
        JLabel saldoMontoLabel = new JLabel("Saldo disponible: " + usuarioActual.getSaldo() + " Bs.");
        saldoMontoLabel.setBounds(110, 60, 200, 25);
        saldoFrame.add(saldoMontoLabel);

        JButton atrasButton = new JButton("Atras");
        atrasButton.setBounds(150, 120, 100, 30);
        saldoFrame.add(atrasButton);

        atrasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saldoFrame.dispose(); // Cierra la ventana de saldo
            }
        });

        saldoFrame.setVisible(true);
    }

    /**
     * Abre una nueva ventana para permitir al usuario depositar dinero en su cuenta.
     * La ventana incluye un campo de texto para ingresar la cantidad a depositar y botones para confirmar o cancelar la operacion.
     * 
     * @throws HeadlessException Si se utiliza en un entorno sin interfaz grafica.
     */
    private void abrirVentanaDepositar() {
        JFrame ventanaDeposito = new JFrame("Depositar Dinero");
        ventanaDeposito.setSize(400, 200);
        ventanaDeposito.setLocationRelativeTo(this);
        ventanaDeposito.setLayout(null);

        JLabel cantidadLabel = new JLabel("Cantidad a depositar:");
        cantidadLabel.setBounds(50, 30, 150, 25);
        ventanaDeposito.add(cantidadLabel);

        JTextField cantidadField = new JTextField();
        cantidadField.setBounds(140, 60, 150, 25);
        ventanaDeposito.add(cantidadField);

        JButton atrasButton = new JButton("Atras");
        atrasButton.setBounds(240, 100, 100, 25);
        ventanaDeposito.add(atrasButton);

        atrasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventanaDeposito.dispose(); // Cierra la ventana de saldo
            }
        });
        JButton confirmarButton = new JButton("Confirmar");
        confirmarButton.setBounds(80, 100, 100, 25);
        ventanaDeposito.add(confirmarButton);

        confirmarButton.addActionListener(e -> {
            double cantidad = Double.parseDouble(cantidadField.getText());
            usuarioActual.depositar(cantidad);
            guardarTodosLosUsuarios();
            registrarHistorial(usuarioActual, "Deposito", cantidad);
            ventanaDeposito.dispose();
        });

        ventanaDeposito.setVisible(true);
    }

    /**
     * Abre una nueva ventana para permitir al usuario retirar dinero de su cuenta.
     * La ventana incluye un campo de texto para ingresar la cantidad a retirar y botones para confirmar o cancelar la operacion.
     * 
     * @throws HeadlessException Si se utiliza en un entorno sin interfaz grafica.
     */
    private void abrirVentanaRetirar() {
        JFrame ventanaRetiro = new JFrame("Retirar Dinero");
        ventanaRetiro.setSize(400, 200);
        ventanaRetiro.setLocationRelativeTo(this);
        ventanaRetiro.setLayout(null);

        JLabel cantidadLabel = new JLabel("Cantidad a retirar:");
        cantidadLabel.setBounds(50, 30, 150, 25);
        ventanaRetiro.add(cantidadLabel);

        JTextField cantidadField = new JTextField();
        cantidadField.setBounds(140, 60, 150, 25);
        ventanaRetiro.add(cantidadField);

        JButton atrasButton = new JButton("Atras");
        atrasButton.setBounds(240, 100, 100, 25);
        ventanaRetiro.add(atrasButton);

        atrasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventanaRetiro.dispose(); // Cierra la ventana de saldo
            }
        });
        

        JButton confirmarButton = new JButton("Confirmar");
        confirmarButton.setBounds(80, 100, 100, 25);
        ventanaRetiro.add(confirmarButton);

        confirmarButton.addActionListener(e -> {
            double cantidad = Double.parseDouble(cantidadField.getText());
            if (cantidad <= usuarioActual.getSaldo()) {
                usuarioActual.retirar(cantidad);
                guardarTodosLosUsuarios();
                registrarHistorial(usuarioActual, "Retiro", cantidad);
                ventanaRetiro.dispose();
            } else {
                JOptionPane.showMessageDialog(ventanaRetiro, "Saldo insuficiente");
            }
        });

        ventanaRetiro.setVisible(true);
    }

    /**
     * Abre una nueva ventana para permitir al usuario transferir dinero a otra cuenta.
     * La ventana incluye campos de texto para ingresar el numero de cuenta destino y la cantidad a transferir,
     * asi como botones para confirmar o cancelar la operacion.
     * 
     * @throws HeadlessException Si se utiliza en un entorno sin interfaz grafica.
     */

    private void abrirVentanaTransferencia() {
        JFrame ventanaTransferencia = new JFrame("Transferir Dinero");
        ventanaTransferencia.setSize(400, 300);
        ventanaTransferencia.setLocationRelativeTo(this);
        ventanaTransferencia.setLayout(null);
    
        JLabel cuentaDestinoLabel = new JLabel("Numero de cuenta destino:");
        cuentaDestinoLabel.setBounds(50, 30, 180, 25);
        ventanaTransferencia.add(cuentaDestinoLabel);
    
        JTextField cuentaDestinoField = new JTextField();
        cuentaDestinoField.setBounds(140, 60, 150, 25);
        ventanaTransferencia.add(cuentaDestinoField);
    
        JLabel cantidadLabel = new JLabel("Cantidad a transferir:");
        cantidadLabel.setBounds(50, 100, 150, 25);
        ventanaTransferencia.add(cantidadLabel);
    
        JTextField cantidadField = new JTextField();
        cantidadField.setBounds(140, 130, 150, 25);
        ventanaTransferencia.add(cantidadField);
    
        JButton atrasButton = new JButton("Atras");
        atrasButton.setBounds(240, 200, 100, 25);
        ventanaTransferencia.add(atrasButton);
    
        JButton confirmarButton = new JButton("Confirmar");
        confirmarButton.setBounds(80, 200, 100, 25);
        ventanaTransferencia.add(confirmarButton);
    
        atrasButton.addActionListener(e -> ventanaTransferencia.dispose());
    
        confirmarButton.addActionListener(e -> {
            String numeroCuentaDestino = cuentaDestinoField.getText();
    
            /**
             *  Verificar si el numero de cuenta destino existe
             */
            Usuario destinatario = buscarUsuarioPorNumeroCuenta(numeroCuentaDestino);
            if (destinatario == null) {
                JOptionPane.showMessageDialog(ventanaTransferencia, "El numero de cuenta destino no existe.");
                return;
            }
            
            /**
             * La cantidad ingresada no debe ser de un tipo distinto de double
             * @throws NumberFormatException en caso de que la cantidad sea dsitinta del tipo double
             */
             double cantidad;
            try {
                cantidad = Double.parseDouble(cantidadField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(ventanaTransferencia, "Ingrese una cantidad valida.");
                return;
            }
    
            /**
             * Verificar si el saldo es suficiente
             */ 
            if (cantidad <= usuarioActual.getSaldo()) {
                /**
                 * Realizar la transferencia
                 */
                usuarioActual.retirar(cantidad);
                destinatario.depositar(cantidad);
    
                /**
                 * Guardar los cambios de ambos usuarios
                 */
                guardarTodosLosUsuarios();
    
                /**
                 * Registrar en el historial del usuario actual y del destinatario
                 */
                registrarHistorial(usuarioActual, "Transferencia a cuenta " + destinatario.getNumeroCuenta(), cantidad);
                registrarHistorial(destinatario, "Transferencia recibida de cuenta " + usuarioActual.getNumeroCuenta(), cantidad);
    
                JOptionPane.showMessageDialog(ventanaTransferencia, "Transferencia realizada con éxito.");
                ventanaTransferencia.dispose();
            } else {
                JOptionPane.showMessageDialog(ventanaTransferencia, "Saldo insuficiente.");
            }
        });
    
        ventanaTransferencia.setVisible(true);
    }
    
    /**
     * busca al usuario por su numero de cuenta mediante un recorrido
     * @param numeroCuenta
     * @return al objeto "usuario" en caso de encontrarlo, caso contrario retorna null
     */
    private Usuario buscarUsuarioPorNumeroCuenta(String numeroCuenta) {
        for (Usuario usuario : usuarios.values()) {
            if (usuario.getNumeroCuenta().equals(numeroCuenta)) {
                return usuario;
            }
        }
        return null; // Si no se encuentra un usuario con ese numero de cuenta
    }
    
    /**
     * Metodo para abrir la ventana de historial
     */
    private void abrirVentanaHistorial() {
        JFrame ventanaHistorial = new JFrame("Historial de Transacciones");
        ventanaHistorial.setSize(500, 400);
        ventanaHistorial.setLocationRelativeTo(this);
        ventanaHistorial.setLayout(new BorderLayout());

        JTextArea historialArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(historialArea);
        ventanaHistorial.add(scrollPane, BorderLayout.CENTER);

    /**
     * Boton para cerrar la ventana de historial
     */
    JButton cerrarButton = new JButton("Cerrar");
    cerrarButton.setBounds(200, 320, 100, 30); 
    ventanaHistorial.add(cerrarButton, BorderLayout.SOUTH); 
    cerrarButton.addActionListener(e -> ventanaHistorial.dispose()); 

        /**
         * intenta leer el archivo "usuarios.txt", si tiene algun problema al encontrar la ubicacion salta una excepcion
         */
        try (BufferedReader reader = new BufferedReader(new FileReader("historial_" + usuarioActual.getNombreUsuario() + ".txt"))) {
            String linea;
            StringBuilder historial = new StringBuilder();
            while ((linea = reader.readLine()) != null) {
                historial.append(linea).append("\n");
            }
            historialArea.setText(historial.toString());
        } catch (IOException e) {
            historialArea.setText("Error al leer el historial");
        }

        ventanaHistorial.setVisible(true);
    }
    /**
     * registra los nuevos cambios hechos por la cuenta o por una cuenta externa
     * @param usuario 
     * @param tipoTransaccion
     * @param cantidad
     */
    private void registrarHistorial(Usuario usuario, String tipoTransaccion, double cantidad) {
        try (FileWriter writer = new FileWriter("historial_" + usuario.getNombreUsuario() + ".txt", true)) {
            writer.write(tipoTransaccion + ": " + cantidad + " Bs. - Saldo actual: " + usuario.getSaldo() + " Bs.\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar historial");
        }
    }
    /**
     * clase principal usuario
     * tiene un constructor 
     * metodos para:
     * -dar su nombre
     * - dar la contrasenia
     * - dar su saldo
     * - dar su numero de cuenta
     * - actualizar su saldo con el metodo "depositar" y "retirar" 
     */
    public static class Usuario {
        private String nombreUsuario;
        private String contrasena;
        private double saldo;
        private String numeroCuenta;

        public Usuario(String nombreUsuario, String contrasena, double saldo, String numeroCuenta) {
            this.nombreUsuario = nombreUsuario;
            this.contrasena = contrasena;
            this.saldo = saldo;
            this.numeroCuenta = numeroCuenta;
        }

        public String getNombreUsuario() {
            return nombreUsuario;
        }

        public String getContrasena() {
            return contrasena;
        }

        public double getSaldo() {
            return saldo;
        }

        public String getNumeroCuenta() {
            return numeroCuenta;
        }

        public void depositar(double cantidad) {
            saldo += cantidad;
        }

        public void retirar(double cantidad) {
            saldo -= cantidad;
        }
    }

    public static void main(String[] args) {
        BancoLogix ventana = new BancoLogix();
        ventana.setVisible(true);
    }
}
