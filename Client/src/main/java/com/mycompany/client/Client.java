/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.mycompany.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thiago
 */
public class Client {

    public static void main(String[] args) {
        String message = "Hello";
        
        try {
            //create client-server connection
            System.out.println("Trying connection!");
            Socket socket = new Socket("localhost", 5555);
            System.out.println("Connection sucessufly");
            
            //create I/O streams
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            System.out.println("Sending message...");
            output.writeUTF(message);
            // libera o buffer para envio (pelo fato de esperar um objeto, ou seja, um numero de bytes que pode não ser um string)
            output.flush();
            
            System.out.println("Message " + message + " has been sent!");
            
            message = input.readUTF();
            System.out.println("Server answer: " + message);
            
            input.close();
            output.close();
            socket.close();
            
        } catch (IOException ex) {
            System.out.println("Erro ao se conectar no servidor!");
        }
    }
}
