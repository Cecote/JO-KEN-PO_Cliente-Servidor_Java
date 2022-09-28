/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.mycompany.servidor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author thiago
 */
public class Servidor extends Thread {

    private static ArrayList<BufferedWriter> clientes;
    private static ServerSocket server;
    private String nome;
    private Socket con;
    private InputStream in;
    private InputStreamReader inr;
    private BufferedReader bfr;
    private static Semaphore semaforoInicial = new Semaphore(2);
    private static Semaphore semaforoJogo = new Semaphore(1);

    public Servidor(Socket con) throws InterruptedException {

        this.con = con;
        try {
            semaforoJogo.acquire();
            in = con.getInputStream();
            inr = new InputStreamReader(in);
            bfr = new BufferedReader(inr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void run() {
//
//        try {
//
//            String msg;
//            OutputStream ou = this.con.getOutputStream();
//            Writer ouw = new OutputStreamWriter(ou);
//            BufferedWriter bfw = new BufferedWriter(ouw);
//            clientes.add(bfw);
//            nome = msg = bfr.readLine();
//
//            while (!"Sair".equalsIgnoreCase(msg) && msg != null) {
//                msg = bfr.readLine();
//                
//                sendToAll(bfw, msg);
//                System.out.println(msg);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//    }

    public void sendToAll(BufferedWriter bwSaida, String msg) throws IOException {

        BufferedWriter bwS;

        for (BufferedWriter bw : clientes) {
            bwS = (BufferedWriter) bw;
            if (!(bwSaida == bwS)) {
                bw.write(nome + " -> " + msg + "\r\n");
                bw.flush();
            }
        }
    }

    public static void main(String[] args) {
        
        try {
            //Cria os objetos necessário para instânciar o servidor
            JLabel lblMessage = new JLabel("Porta do Servidor:");
            JTextField txtPorta = new JTextField("Informe a porta");
            Object[] texts = {lblMessage, txtPorta};
            JOptionPane.showMessageDialog(null, texts);
            server = new ServerSocket(Integer.parseInt(txtPorta.getText()));
            clientes = new ArrayList<BufferedWriter>();
            JOptionPane.showMessageDialog(null, "Servidor ativo na porta: " + txtPorta.getText());

            while (true) {
                semaforoInicial.acquire();
                System.out.println("Aguardando conexão...");
                Socket con = server.accept();
                System.out.println("Cliente conectado...");
                Thread t = new Servidor(con);
                t.start();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
