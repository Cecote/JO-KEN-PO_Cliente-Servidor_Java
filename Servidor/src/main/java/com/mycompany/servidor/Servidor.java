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
    private static BufferedWriter player1Buffer;
    private static BufferedWriter player2Buffer;
    private static ServerSocket server;
    private static int round = 1;
    private String nome;
    private Socket con;
    private InputStream in;
    private InputStreamReader inr;
    private BufferedReader bfr;
    private static int player1 = 0;
    private static int player2 = 0;
    private static Semaphore semaforoInicial = new Semaphore(2);
    private static Semaphore semaforoJogo = new Semaphore(1);
    private static Semaphore semaforoPlayer1 = new Semaphore(1);
    private static Semaphore semaforoPlayer2 = new Semaphore(1);
    private static String jogadaP1;
    private static String jogadaP2;
    private static int placarP1 = 0;
    private static int placarP2 = 0;

    public Servidor(Socket con) throws InterruptedException {

        this.con = con;
        try {
            //semaforoJogo.acquire();
            in = con.getInputStream();
            inr = new InputStreamReader(in);
            bfr = new BufferedReader(inr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        try {

            String msg = null;
            OutputStream ou = this.con.getOutputStream();
            Writer ouw = new OutputStreamWriter(ou);
            BufferedWriter bfw = new BufferedWriter(ouw);

            if (this.con.getPort() == player1) {
                player1Buffer = bfw;
                clientes.add(bfw);
                System.out.println("player1: " + player1Buffer);
                sendToPlayer(bfw, "Bem vindo ao PPT ! Aguarde enquanto procuramos outro jogador ! \r\n\n");
            }
            if (this.con.getPort() == player2) {
                player2Buffer = bfw;
                clientes.add(bfw);
                System.out.println("player2");
                sendToPlayer(bfw, "Bem vindo ao PPT !\r\n");
                sendToAll(bfw, "O jogo está prestes a começar !");
                sleep(2000);
            }

            if (semaforoInicial.availablePermits() == 0) {
                sendToAll(bfw, "Rodada: " + round + "\r\nInforme sua jogada!");
            }
            System.out.println("Saiu do if");
//            OutputStream ou = this.con.getOutputStream();
//            Writer ouw = new OutputStreamWriter(ou);
//            BufferedWriter bfw = new BufferedWriter(ouw);
//            clientes.add(bfw);
            nome = msg = bfr.readLine();

            while (!"Sair".equalsIgnoreCase(msg) && msg != null) {

                
                if (placarP1 == 3 || placarP2 == 3) {
                    semaforoJogo.acquire();
                    if (placarP1 == 3) {
                        placarP1 = 0;
                        placarP2 = 0;
                        round = 1;
                        sendToPlayer(player1Buffer, "Você ganhou a partida!\r\n");
                        sendToPlayer(player2Buffer, "Você perdeu a partida!\r\n");
                    } else if (placarP2 == 3) {
                        placarP1 = 0;
                        placarP2 = 0;
                        round = 1;
                        sendToPlayer(player1Buffer, "Você perdeu a partida!\r\n");
                        sendToPlayer(player2Buffer, "Você ganhou a partida!\r\n");
                    }
                    sendToAll(bfw, "Deseja jogar mais uma partida ?");
                    msg = bfr.readLine();
                    if (bfw == player1Buffer) {
                        jogadaP1 = msg;
                        semaforoPlayer1.acquire();
                    }
                    if (bfw == player2Buffer) {
                        jogadaP2 = msg;
                        semaforoPlayer2.acquire();
                    }
                    if (semaforoPlayer1.availablePermits() == 0 && semaforoPlayer2.availablePermits() == 0) {
                        if ("Sim".equalsIgnoreCase(jogadaP1) && "Sim".equalsIgnoreCase(jogadaP2)) {
                            semaforoPlayer1.release();
                            semaforoPlayer2.release();
                            semaforoJogo.release();
                            
                            sendToAll(bfw, "Rodada: " + round + "\r\nInforme sua jogada!");
                        }
                    }

                }

                if (semaforoJogo.availablePermits() == 1) {

                    msg = bfr.readLine();
                    System.out.println("Digitou: ");
                    if (bfw == player1Buffer) {
                        jogadaP1 = msg;
                        semaforoPlayer1.acquire();
                    }
                    if (bfw == player2Buffer) {
                        jogadaP2 = msg;
                        semaforoPlayer2.acquire();
                    }
                    if (semaforoPlayer1.availablePermits() == 0 && semaforoPlayer2.availablePermits() == 0) {
                        if (("Tesoura".equalsIgnoreCase(jogadaP1) && "Papel".equalsIgnoreCase(jogadaP2)) || ("Papel".equalsIgnoreCase(jogadaP1) && "Pedra".equalsIgnoreCase(jogadaP2)) || ("Pedra".equalsIgnoreCase(jogadaP1) && "Tesoura".equalsIgnoreCase(jogadaP2))) {
                            sendToPlayer(player1Buffer, "Você ganhou essa Rodada!\r\n");
                            sendToPlayer(player2Buffer, "Você perdeu essa Rodada!\r\n");
                            placarP1++;
                        } else {
                            sendToPlayer(player1Buffer, "Você perdeu essa Rodada!\r\n");
                            sendToPlayer(player2Buffer, "Você ganhou essa Rodada!\r\n");
                            placarP2++;
                        }
                        semaforoPlayer1.release();
                        semaforoPlayer2.release();
                        round++;
                        
                        if (placarP1 < 3 && placarP2 < 3) {
                            
                            sendToAll(bfw, "Rodada: " + round + "\r\nInforme sua jogada!");
                        }
                    }
                    //sendToAll(bfw, msg);
                    System.out.println(msg);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void sendToAll(BufferedWriter bwSaida, String msg) throws IOException {

        BufferedWriter bwS;
        //System.out.println(clientes);
        //System.out.println(bwSaida);
        for (BufferedWriter bw : clientes) {
            bwS = (BufferedWriter) bw;
            bw.write(msg + "\r\n");
            bw.flush();

        }
    }

    public void sendToPlayer(BufferedWriter bwSaida, String msg) throws IOException {
        BufferedWriter bwS;
        //System.out.println(clientes);
        //System.out.println(bwSaida);
        for (BufferedWriter bw : clientes) {
            bwS = (BufferedWriter) bw;
            if (bwSaida == bwS) {
                bw.write(msg);
                bw.flush();
            }
        }
    }

    public static void main(String[] args) {

        try {
            //Cria os objetos necessário para instânciar o servidor
            JLabel lblMessage = new JLabel("Porta do Servidor:");
            JTextField txtPorta = new JTextField("5555");
            Object[] texts = {lblMessage, txtPorta};
            JOptionPane.showMessageDialog(null, texts);
            server = new ServerSocket(Integer.parseInt(txtPorta.getText()));
            clientes = new ArrayList<BufferedWriter>();
            JOptionPane.showMessageDialog(null, "Servidor ativo na porta: " + txtPorta.getText());

            while (true) {
                if (semaforoInicial.availablePermits() == 0) {
                    sleep(2000);
                    System.out.println(player1);
                    System.out.println(player2);
                }

                System.out.println("Aguardando conexão...");

                System.out.println("Aguardando conexão...");
                Socket con = server.accept();
                semaforoInicial.acquire();
                System.out.println();
                if (semaforoInicial.availablePermits() == 1) {
                    player1 = con.getPort();
                } else if (semaforoInicial.availablePermits() == 0) {
                    player2 = con.getPort();
                }

                Thread t = new Servidor(con);
                t.start();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
