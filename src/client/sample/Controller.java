package client.sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Controller{
    @FXML
    HBox topPanel;
    @FXML
    TextField loginFiled;
    @FXML
    PasswordField passwordField;

    @FXML
    HBox bottomPanel;
    @FXML
    TextArea textArea;
    @FXML
    TextField textField;
    @FXML
    Button btn1;
    @FXML
    ComboBox comboBox;

    @FXML
    List<String> clietntsList;

    Socket socket;
    DataInputStream input;
    DataOutputStream output;

    final String IP_ADRESS = "localhost";
    final int PORT = 9999;
    String selfName="";

    private boolean isAuthorized;

    public void setAuthorized(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;

        if(!isAuthorized) {
            topPanel.setVisible(true);
            topPanel.setManaged(true);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
        } else {
            topPanel.setVisible(false);
            topPanel.setManaged(false);
            bottomPanel.setVisible(true);
            bottomPanel.setManaged(true);
        }
    }


    //Подключение к серверу
    public void connect() {
        try {
            socket = new Socket(IP_ADRESS, PORT);
            input = new DataInputStream(socket.getInputStream()); //Данные входящего потока с Сервера
            output = new DataOutputStream(socket.getOutputStream()); //Данные исходящего потока на Сервер

            Thread streamT = new Thread(new Runnable() {
                @Override
                public void run() {
                    String msg = null;
                    try {
                        //Авторизация
                        while (true) {
                            String str = input.readUTF();
                            if(str.startsWith("/authok")) {
                                setAuthorized(true);
                                break;
                            } else {
                                textArea.appendText(str + "\n");
                            }
                        }

                        //Сообщения

                        while (true) {
                                msg = input.readUTF();
                                if (msg.startsWith("/cL#")) {
                                    //msg=msg.replace(selfName+";","");
                                    msg.substring(msg.indexOf('#') + 1, msg.length()).split(";");
                                    clietntsList = Arrays.asList(msg.substring(msg.indexOf('#') + 1, msg.length()).split(";"));

                                    comboBox.getItems().clear();
                                    //comboBox.getItems().addAll("Всем");
                                    comboBox.getItems().addAll(clietntsList);
                                }
                               /* else if (msg.startsWith("/sN#")){
                                    selfName = msg.substring(msg.indexOf('#') + 1, msg.length());
                                    System.out.println(selfName);
                                }*/
                                else
                                    textArea.appendText(msg + "\n");
                        }
                    } catch (IOException e) {
                        disconnect();
                        //e.printStackTrace();
                    }
                    finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            streamT.setDaemon(true);
            streamT.start();

        } catch (IOException e) {
            disconnect();
            //e.printStackTrace();
        }
    }
    //Авторизация
    public void auth() {
        if(socket == null || socket.isClosed()) {
            connect();
        }

        try {
            output.writeUTF("/auth " + loginFiled.getText() + " " + passwordField.getText());
            loginFiled.clear();
            passwordField.clear();
        } catch (IOException e) {
            disconnect();
            //e.printStackTrace();
        }
    }

    //Отправка сообщения
    public void sendMsg() {
        if (!textField.getText().isEmpty()) {
            //textArea.appendText(textField.getText() + "\n");
            try {
                System.out.println(comboBox.getValue());
                if (comboBox.getValue() == null)
                    output.writeUTF(textField.getText());
                else
                    output.writeUTF("/w#"+comboBox.getValue()+"#"+textField.getText());
                textField.clear();
                textField.requestFocus();
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }

    public void disconnect(){
        textArea.appendText("======/Нет связи с сервером/=====\n");
//        btn1.setDisable(true);
//        textField.setDisable(true);
        textField.setText("Нет связи с сервером");
    }
}