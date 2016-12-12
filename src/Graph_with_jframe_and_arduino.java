/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author KME Hasan
 */
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import com.fazecast.jSerialComm.SerialPort;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;


public class Graph_with_jframe_and_arduino extends javax.swing.JFrame {

    /**
     * Creates new form NewJFrame
     */
    public Graph_with_jframe_and_arduino() {
        initComponents();
    }
    //global variable
    static SerialPort chosenPort;
    static int x = 0;//serise index
    private static OutputStream Output;
        
        

	public static void main(String[] args) {
		
            // create and configure the window
            JFrame window = new JFrame();
            window.setTitle("Sensor Graph GUI");
            window.setSize(600, 400);
            window.setLayout(new BorderLayout());
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // create a drop-down box and connect button, then place them at the top of the window
            JComboBox<String> portList_combobox = new JComboBox<String>();
            Dimension d =new Dimension(300,100);
            portList_combobox.setSize(d);
            JButton connectButton = new JButton("Connect");
            JPanel topPanel = new JPanel();
            topPanel.add(portList_combobox);
            topPanel.add(connectButton);
            window.add(topPanel, BorderLayout.NORTH);
            //pause button
            JButton Pause_btn = new JButton("Start");

            // populate the drop-down box
            SerialPort[] portNames;
            portNames = SerialPort.getCommPorts();
            //check for new port available
            Thread thread_port = new Thread(){
                @Override
                public void run() {
                    while(true){
                        SerialPort[] sp = SerialPort.getCommPorts();
                        if(sp.length>0)
                        {
                            for(SerialPort sp_name : sp)
                            {
                                int l=portList_combobox.getItemCount(),i;
                                for(i=0;i<l;i++)
                                {
                                    //check port name already exist or not
                                    if(sp_name.getSystemPortName().equalsIgnoreCase(portList_combobox.getItemAt(i))){
                                        break;
                                    }
                                }
                                if(i==l){
                                    portList_combobox.addItem(sp_name.getSystemPortName());
                                }

                            }

                        }
                        else {
                            portList_combobox.removeAllItems();

                        }
                        portList_combobox.repaint();

                    }

                }

            };
            thread_port.start();
            for(SerialPort sp_name : portNames)
                portList_combobox.addItem(sp_name.getSystemPortName());

            //for(int i = 0; i < portNames.length; i++)
            //	portList.addItem(portNames[i].getSystemPortName());

            // create the line graph
            XYSeries series = new XYSeries("line 1");
            XYSeries series2 = new XYSeries("line 2");
            XYSeries series3 = new XYSeries("line 3");
            XYSeries series4 = new XYSeries("line 4");
            for(int i=0;i<100;i++)
            {
                series.add(x,0);
                series2.add(x,0);
                series3.add(x,0);
                series4.add(x,10);
                x++;
            }
            
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries(series);
            dataset.addSeries(series2);
            XYSeriesCollection dataset2 = new XYSeriesCollection();
            dataset2.addSeries(series3);
            dataset2.addSeries(series4);
            
            
            //create jfree chart
            JFreeChart chart = ChartFactory.createXYLineChart("Sensor Readings", "Time (seconds)", "Arduino Reading", dataset);
            JFreeChart chart2 = ChartFactory.createXYLineChart("Sensor Readings", "Time (seconds)", "Arduino Reading 2", dataset2);
            
            //color render for chart 1
            XYLineAndShapeRenderer r1 = new XYLineAndShapeRenderer();
            r1.setSeriesPaint(0, Color.RED); 
            r1.setSeriesPaint(1, Color.GREEN); 
            r1.setSeriesShapesVisible(0,  false);
            r1.setSeriesShapesVisible(1,  false);
            
            XYPlot plot = chart.getXYPlot();
            plot.setRenderer(0,r1);
            plot.setRenderer(1,r1);
            
            plot.setBackgroundPaint(Color.WHITE);
            plot.setDomainGridlinePaint(Color.DARK_GRAY);
            plot.setRangeGridlinePaint(Color.blue);
            
            //color render for chart 2
            XYLineAndShapeRenderer r2 = new XYLineAndShapeRenderer();
            r2.setSeriesPaint(0, Color.BLUE); 
            r2.setSeriesPaint(1, Color.ORANGE); 
            r2.setSeriesShapesVisible(0,  false);
            r2.setSeriesShapesVisible(1,  false);

            XYPlot plot2 = chart2.getXYPlot();
            plot2.setRenderer(0,r2);
            plot2.setRenderer(1,r2);
            
            
            ChartPanel cp = new ChartPanel(chart);
            ChartPanel cp2 = new ChartPanel(chart2);
            
            //multiple graph container
            JPanel graph_container = new JPanel();
            graph_container.setLayout(new BoxLayout(graph_container, BoxLayout.X_AXIS));
            graph_container.add(cp);
            graph_container.add(cp2);
            
            


            //add chart panel in main window
            window.add(graph_container, BorderLayout.CENTER);
            //window.add(cp2, BorderLayout.WEST);
            



            window.add(Pause_btn,BorderLayout.AFTER_LAST_LINE);
            Pause_btn.setEnabled(false);
            //pause btn action
            Pause_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Pause_btn.getText().equalsIgnoreCase("Pause")) {
                    
                    
                    if(chosenPort.isOpen())
                    {
                        try {
                            Output.write(0);
                        } catch (IOException ex) {
                            Logger.getLogger(Graph_with_jframe_and_arduino.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                                        
                    Pause_btn.setText("Start");
                }
                else{
                    if(chosenPort.isOpen())
                    {
                        try {
                            Output.write(1);
                        } catch (IOException ex) {
                            Logger.getLogger(Graph_with_jframe_and_arduino.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    Pause_btn.setText("Pause");
                }
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
		
            // configure the connect button and use another thread to listen for data
            connectButton.addActionListener(new ActionListener(){
                    @Override public void actionPerformed(ActionEvent arg0) {
                            if(connectButton.getText().equals("Connect")) {
                                    // attempt to connect to the serial port
                                    chosenPort = SerialPort.getCommPort(portList_combobox.getSelectedItem().toString());
                                    chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
                                    if(chosenPort.openPort()) {
                                            Output = chosenPort.getOutputStream();
                                            connectButton.setText("Disconnect");
                                            Pause_btn.setEnabled(true);
                                            portList_combobox.setEnabled(false);
                                    }

                                    // create a new thread that listens for incoming text and populates the graph
                                    Thread thread = new Thread(){
                                            @Override public void run() {
                                                    Scanner scanner = new Scanner(chosenPort.getInputStream());
                                                    while(scanner.hasNextLine()) {
                                                            try {
                                                                    String line = scanner.nextLine();
                                                                    int number = Integer.parseInt(line);
                                                                    series.add(x, number);
                                                                    series2.add(x,number/2);
                                                                    series3.add(x,number/1.5);
                                                                    series4.add(x,number/5);

                                                                    if(x>100)
                                                                        {
                                                                            series.remove(0);
                                                                            series2.remove(0);
                                                                            series3.remove(0);
                                                                            series4.remove(0);
                                                                        }

                                                                    x++;
                                                                    window.repaint();
                                                            } catch(Exception e) {}
                                                    }
                                                    scanner.close();
                                            }
                                    };
                                    thread.start();
                            } else {
                                    // disconnect from the serial port
                                    chosenPort.closePort();
                                    portList_combobox.setEnabled(true);
                                    Pause_btn.setEnabled(false);
                                    connectButton.setText("Connect");


                            }
                    }
            });

            // show the window
            window.setVisible(true);
	}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(Graph_with_jframe_and_arduino.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(Graph_with_jframe_and_arduino.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(Graph_with_jframe_and_arduino.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(Graph_with_jframe_and_arduino.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new Graph_with_jframe_and_arduino().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
