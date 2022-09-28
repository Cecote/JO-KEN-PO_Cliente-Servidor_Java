/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.mycompany.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thiago
 */
public class Server {
    
    private ServerSocket socketServer; 
    
    private void createServer(int port) throws IOException{

        socketServer = new ServerSocket(port);
    }
    
    private Socket waitingConnection() throws IOException{
        Socket socket = socketServer.accept();
        return socket;
    }
    
    private void treatConnection(Socket socket) throws IOException{
        String clientMessage;
        try {
            //Criar streams I/O do socket
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            
            clientMessage = input.readUTF();
            System.out.println("Mensagem Recebida...");
            
            output.writeUTF("Hello World!");
            // libera o buffer para envio (pelo fato de esperar um objeto, ou seja, um numero de bytes que pode não ser um string)
            output.flush();
            
            // Fechar os canais de comunicação I/O entre servidor e cliente
            output.close();
            input.close();
            
            
        } catch (IOException ex) {
            System.out.println("Erro ao tentar estabelecer uma conexão");
        }finally{
            // Fechar socket após a finalização da comunicação com o cliente
            
            closeSocket(socket);
        }
        
    }
    
    private void closeSocket(Socket socket) throws IOException{
        socket.close();
    }
    
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        System.out.println("Waiting connection...");
        server.createServer(5555);
        Socket socket = server.waitingConnection();
        System.out.println("Clent Connected!");
        server.treatConnection(socket);
        System.out.println("Client disconnected!");
    }
}
