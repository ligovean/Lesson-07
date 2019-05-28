package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

public class ServerMain {
    private Vector<ClientHandler> clientPool = new Vector();

    ServerSocket serverSocket = null;
    Socket socket = null;
    String clientName = "";

    public ServerMain() {

        {
            try {
                serverSocket = new ServerSocket(9999);
                System.out.println("Сервер запущен...");

                while (true){
                    socket = serverSocket.accept();
                    new ClientHandler(socket,this);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void connect(ClientHandler client){
        //Трансляция сообщения о том, что клиент вошел в чат
        clientPool.add(client);
        broadcastMsg("======/" + client.getName() + " вошел в чат!/======");
    }

    public void disconnect(ClientHandler client){
        //Трансляция сообщения о том, что клиент вышел из чата
        clientPool.remove(client);
        broadcastMsg("======/" + client.getName() + " покинул чат!/======");
    }

    public void privateMsg(ClientHandler client,String msg) {
        String[] parseMsg= msg.split("(?<!\\\\)#");
        for (ClientHandler clients:clientPool) {
            if (clients.getName().equals(parseMsg[1])){
                System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")) + client.getName() + ": " + msg);
                clients.sendMsg("Pr " + client.getName() +": " +  parseMsg[2]);
                client.sendMsg("Pr " + client.getName() +": " +  parseMsg[2]);
                break;
            }
        }
    }

    //Сообщение на всех от Сервера
    public void broadcastMsg(String msgAll) {
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")) +": "+ msgAll);
        for (ClientHandler clients:clientPool) {
            clients.sendMsg(msgAll);
        }
    }

    //Сообщение на всех от Клиента
    public void broadcastMsg(ClientHandler client,String msgAll) {
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))+": " + client.getName() + ": " + msgAll);
        for (ClientHandler clients:clientPool) {
            clients.sendMsg(client.getName() + ": " + msgAll);
        }
    }
}
