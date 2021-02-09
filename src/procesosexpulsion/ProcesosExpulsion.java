
package procesosexpulsion;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class ProcesosExpulsion extends JFrame implements Runnable ,ActionListener{

    JScrollPane scrollPane = new JScrollPane();
    JScrollPane scrollPane1 = new JScrollPane();
    
    JScrollPane scrollPane2 = new JScrollPane();
    JScrollPane scrollPane3 = new JScrollPane();
    
    JScrollPane scrollPane4 = new JScrollPane();
    JScrollPane scrollPane5 = new JScrollPane();
    
    JLabel semaforo = new JLabel();
    
    JLabel label1 = new JLabel("Nombre del proceso: ");
    JLabel label2 = new JLabel("Prioridad del proceso:");
    JLabel label3 = new JLabel("Proceso en ejecucion: Ninguno");
    JLabel label4 = new JLabel("Tiempo: ");
    JLabel label5 = new JLabel("Tabla de procesos:");
    JLabel label6 = new JLabel("Diagrama de Gant:");
    JLabel label7 = new JLabel("Tabla de Bloqueados:");
    JLabel label8 = new JLabel("Rafaga restante del proceso: 0");
    
    JButton botonIngresar = new JButton("Ingresar proceso");
    JButton botonIniciar = new JButton("Iniciar ejecucion");
    
    JTextField tfNombre = new JTextField("P1");
    
    JTextField[][] tabla = new JTextField[100][7];
    JTextField[][] tablaBloqueados = new JTextField[100][3];
    JLabel[][] diagrama = new JLabel[40][100];  
    
    ListaCircular cola = new ListaCircular();
    
    Nodo nodoEjecutado;
    
    int filas = 0, rafagaTemporal;
    int tiempoGlobal = 0;
    int coorX = 0;
    
    Thread procesos;
    
    public static void main(String[] args) {

        ProcesosExpulsion pe = new ProcesosExpulsion(); 
        pe.setBounds(0, 0, 1200, 730);
        pe.setTitle("Procesos con expulsion");
        pe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pe.setVisible(true);
        
    }

    ProcesosExpulsion(){
        
        Container c = getContentPane();
        c.setLayout(null);
        this.getContentPane().setBackground(Color.GRAY);
        
        c.add(label1);
        c.add(label2);
        c.add(label3);
        c.add(label4);
        c.add(label5);
        c.add(label6);
        c.add(label7);
        c.add(label8);
        c.add(semaforo);
        
        c.add(scrollPane1);
        c.add(scrollPane3);
        c.add(scrollPane5);
        
        c.add(botonIngresar);
        c.add(botonIniciar);
        
        c.add(tfNombre);
        
        label1.setBounds(800, 40, 300, 20);
        label2.setBounds(800, 70, 300, 20);
        label3.setBounds(800, 250, 300, 20);
        label4.setBounds(1020, 250, 300, 20);
        label5.setBounds(50, 20, 300, 20);
        label6.setBounds(50, 280, 300, 20);
        label7.setBounds(800, 280, 300, 20);
        label8.setBounds(800, 265, 300, 20);
        
        scrollPane.setBounds(50, 40, 2500, 2500);
        scrollPane.setPreferredSize(new Dimension(2500, 2500));  
        scrollPane.setBackground(Color.lightGray);
        
        scrollPane1.setBounds(50, 40, 700, 230);
        scrollPane1.setPreferredSize(new Dimension(1150, 400)); 
        scrollPane1.setBackground(Color.lightGray);
        
        scrollPane2.setBounds(50, 300, 2500, 2500);
        scrollPane2.setPreferredSize(new Dimension(2500, 2500));  
        scrollPane2.setBackground(Color.lightGray);
        
        scrollPane3.setBounds(50, 300, 700, 350);
        scrollPane3.setPreferredSize(new Dimension(1150, 400)); 
        scrollPane3.setBackground(Color.lightGray);
        
        scrollPane2.setBounds(50, 300, 2500, 2500);
        scrollPane2.setPreferredSize(new Dimension(2500, 2500));  
        scrollPane2.setBackground(Color.lightGray);
        
        scrollPane3.setBounds(50, 300, 700, 350);
        scrollPane3.setPreferredSize(new Dimension(700, 350)); 
        scrollPane3.setBackground(Color.lightGray);
        
        scrollPane4.setBounds(800, 300, 500, 1000);
        scrollPane4.setPreferredSize(new Dimension(500, 1000));  
        scrollPane4.setBackground(Color.lightGray);
        
        scrollPane5.setBounds(800, 300, 350, 350);
        scrollPane5.setPreferredSize(new Dimension(350, 350)); 
        scrollPane5.setBackground(Color.lightGray);
        
        tfNombre.setBounds(930, 40, 70, 20);
        
        botonIngresar.addActionListener(this);
        botonIngresar.setBounds(800, 100, 200, 60);
        botonIngresar.setBackground(Color.CYAN);
        
        botonIniciar.addActionListener(this);
        botonIniciar.setBounds(800, 180, 200, 60);
        botonIniciar.setBackground(Color.GREEN);
        
        dibujarSemaforo("Verde.jpg");
        
    }
    
    public void dibujarSemaforo(String color){
        
        JLabel img = new JLabel();
        
        ImageIcon imgIcon = new ImageIcon(getClass().getResource(color));

        Image imgEscalada = imgIcon.getImage().getScaledInstance(130, 200, Image.SCALE_SMOOTH);
        Icon iconoEscalado = new ImageIcon(imgEscalada);
        semaforo.setBounds(1020 , 40, 130, 200);
        semaforo.setIcon(iconoEscalado);
     
    }
    
    public void dibujarTabla(String nombre, int rafaga, int tiempo){
        
        scrollPane.removeAll();
        
        JLabel texto1 = new JLabel("Proceso");
        JLabel texto2 = new JLabel("T. llegada");
        JLabel texto3 = new JLabel("Rafaga");
        JLabel texto4 = new JLabel("T. comienzo");
        JLabel texto5 = new JLabel("T. final");
        JLabel texto6 = new JLabel("T. retorno");
        JLabel texto7 = new JLabel("T. espera");
        
        texto1.setBounds(20, 20, 150, 20);
        texto2.setBounds(100, 20, 150, 20);
        texto3.setBounds(180, 20, 150, 20);
        texto4.setBounds(260, 20, 150, 20);
        texto5.setBounds(340, 20, 150, 20);
        texto6.setBounds(420, 20, 150, 20);
        texto7.setBounds(500, 20, 150, 20);
        
        scrollPane.add(texto1);
        scrollPane.add(texto2);
        scrollPane.add(texto3);
        scrollPane.add(texto4);
        scrollPane.add(texto5);
        scrollPane.add(texto6);
        scrollPane.add(texto7);
        
        for(int i = 0; i<filas; i++){
            
            for(int j = 0; j<7; j++){
            
                if(tabla[i][j] != null){
                    
                    scrollPane.add(tabla[i][j]);
                    
                } else {
                
                    tabla[i][j] = new JTextField("-");
                    tabla[i][j].setBounds(20 + (j*80), 40 + (i*25), 70, 20);
                    
                    scrollPane.add(tabla[i][j]);
                    
                }

            }
        
        }
        
        tabla[filas-1][0].setText(nombre);
        tabla[filas-1][1].setText(Integer.toString(tiempo));
        tabla[filas-1][2].setText(Integer.toString(rafaga));

        scrollPane.repaint();
        scrollPane1.setViewportView(scrollPane);
            
    }
    
    public void llenarBloqueados(){
        
        scrollPane4.removeAll();
        
        JLabel texto1 = new JLabel("Proceso");
        JLabel texto2 = new JLabel("T. llegada");
        JLabel texto3 = new JLabel("Rafaga");
        
        texto1.setBounds(20, 20, 150, 20);
        texto2.setBounds(100, 20, 150, 20);
        texto3.setBounds(180, 20, 150, 20);
        
        scrollPane4.add(texto1);
        scrollPane4.add(texto2);
        scrollPane4.add(texto3);
        
        if(cola.getCabeza() != null){
        
        Nodo temp = cola.getCabeza().getSiguiente();
        
            for(int i = 0; i<cola.getTamaño()-1; i++){

                for(int j = 0; j<3 ; j++){

                        tablaBloqueados[i][j] = new JTextField("");
                        tablaBloqueados[i][j].setBounds(20 + (j*80), 40 + (i*25), 70, 20);

                        scrollPane4.add(tablaBloqueados[i][j]);

                }

                tablaBloqueados[i][0].setText(temp.getLlave());
                tablaBloqueados[i][1].setText(Integer.toString(temp.getLlegada()));
                tablaBloqueados[i][2].setText(Integer.toString(temp.getRafaga()));
                
                temp = temp.getSiguiente();

            }
        
        }
        
        scrollPane4.repaint();
        scrollPane5.setViewportView(scrollPane4);
        
    }
    
    public void llenarRestante(){
        
        tabla[nodoEjecutado.getIndice()-1][3].setText(Integer.toString(nodoEjecutado.getComienzo()));
        tabla[nodoEjecutado.getIndice()-1][4].setText(Integer.toString(nodoEjecutado.getFinalizacion()));
        tabla[nodoEjecutado.getIndice()-1][5].setText(Integer.toString(nodoEjecutado.getFinalizacion() - nodoEjecutado.getLlegada()));
        tabla[nodoEjecutado.getIndice()-1][6].setText(Integer.toString(nodoEjecutado.getComienzo() - nodoEjecutado.getLlegada()));
 
    }
    
    public void dibujarDiagrama(String nombre, int coorX, int coorY){
        
        scrollPane2.removeAll();
        
        for(int i = 0; i<100; i++){
            
            diagrama[0][i] = new JLabel(Integer.toString(i));
            diagrama[0][i].setBounds(20 + (i*20), 20, 20, 20);

            scrollPane2.add(diagrama[0][i]);
            
        }
        
        diagrama[nodoEjecutado.getIndice()][0] = new JLabel("  " + nombre);
        diagrama[nodoEjecutado.getIndice()][0].setBounds(0, 20 + (nodoEjecutado.getIndice()*20), 30, 20);
        
        scrollPane2.add(diagrama[nodoEjecutado.getIndice()][0]);
        
        JLabel img = new JLabel();
        
        ImageIcon imgIcon = new ImageIcon(getClass().getResource("barra.jpg"));

        Image imgEscalada = imgIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        Icon iconoEscalado = new ImageIcon(imgEscalada);
        
        for(int i = 1; i < filas + 1; i++){
            
            for(int j = 0; j < coorX+1; j++){
                
                if(diagrama[i][j] != null){
                
                    scrollPane2.add(diagrama[i][j]);
                    
                }
                
            }
            
        }
        
        diagrama[nodoEjecutado.getIndice()][coorX+1] = new JLabel();
        diagrama[nodoEjecutado.getIndice()][coorX+1].setBounds(20 + (coorX*20), 20 + (nodoEjecutado.getIndice()*20), 20, 20);
        diagrama[nodoEjecutado.getIndice()][coorX+1].setIcon(iconoEscalado);
        
        scrollPane2.add(diagrama[nodoEjecutado.getIndice()][coorX+1]);
        
        scrollPane2.repaint();
        scrollPane3.setViewportView(scrollPane2);
            
    }
    
    public void ingresar(String nombre, int rafaga, int tiempo, int filas){
        
        cola.insertar(nombre, rafaga, tiempo, filas);
        
    }
    
    public int calcularRafaga(){
        
        return 1 + ((int) (Math.random()*12));
        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
    
        if(e.getSource() == botonIngresar){
            
            filas++;
            
            String nombre = tfNombre.getText();
            rafagaTemporal = calcularRafaga();
            
            ingresar(nombre, rafagaTemporal, tiempoGlobal, filas);
            dibujarTabla(nombre, rafagaTemporal, tiempoGlobal);
            
            tfNombre.setText("P" + (filas + 1));
            
        } else if(e.getSource() == botonIniciar){
        
            procesos = new Thread( this );
            procesos.start();  
            
        } 
        
    }
    
    @Override
    public void run() {
    
            try{

            while(cola.getTamaño() != 0){
                
                dibujarSemaforo("Rojo.jpg");
                
                nodoEjecutado = cola.getCabeza();
                nodoEjecutado.setComienzo(tiempoGlobal);
                
                int tiempoEjecutado = 0;
                
                while(nodoEjecutado.getRafaga() > 0 && tiempoEjecutado < 4){
                    
                    nodoEjecutado.setRafaga(nodoEjecutado.getRafaga()-1);
                    
                    label3.setText("Proceso en ejecucion: " + nodoEjecutado.getLlave());
                    label4.setText("Tiempo: " + String.valueOf(tiempoGlobal) + " Segundos.");
                    label8.setText("Rafaga restante del proceso: " + nodoEjecutado.getRafaga());
                    
                    dibujarDiagrama(nodoEjecutado.getLlave(), coorX, nodoEjecutado.getIndice());
                    llenarBloqueados();
                    
                    tiempoGlobal++;
                    coorX++;
                    tiempoEjecutado++;
                    
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ProcesosExpulsion.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
                
                nodoEjecutado.setFinalizacion(tiempoGlobal);
                llenarRestante();
                
                if(nodoEjecutado.getRafaga() == 0){
                
                    cola.eliminar(cola.getCabeza());
                    
                } else if (tiempoEjecutado == 4){
                
                    cola.getCabeza().setLlave(cola.getCabeza().getLlave());
                    cola.intercambiar(cola.getCabeza());
                    
                }
                               
                llenarBloqueados();
                
            }

            dibujarSemaforo("Verde.jpg");
            label3.setText("Proceso en ejecucion: Ninguno");
            
        } catch(Exception e){
        
            System.out.print("No se que poner aca :D");
            
        }
    
    }
    
}
