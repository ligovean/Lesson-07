package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private String clientName;
    private Socket socket;
    private ServerMain server;
    private DataInputStream input;
    private DataOutputStream output;

    public ClientHandler(Socket socket, ServerMain server) {
            this.socket = socket;
            this.server = server;
            this.clientName = ""+socket.getRemoteSocketAddress();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    input = new DataInputStream(socket.getInputStream());
                    output = new DataOutputStream(socket.getOutputStream());

                    //АВТОРИЗАЦИЯ
                    //TODO;
                    //while (true){
                        //Удачная авторизация

                     //clientName ="nickName";
                    server.connect(ClientHandler.this);
                    //}

                    //СООБЩЕНИЯ
                    while (true) {
                        String msg = input.readUTF();

                        if(msg.startsWith("/end")) {
                            //Остановка клиента по стопслову /end
                            server.disconnect(ClientHandler.this);
                            socket.close();
                            break;
                        } else
                        if(msg.startsWith("/w#")) {
                            //Личное сообщение
                            server.privateMsg(ClientHandler.this,msg);
                        } else
                        //Трансляция сообщения во все клиенты
                            server.broadcastMsg(ClientHandler.this,msg);

                    }
                } catch (IOException e) {
                    server.disconnect(ClientHandler.this);
                }finally {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public String getName() {
        return clientName;
    }

    public void sendMsg(String msg) {
        try {
            output.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}