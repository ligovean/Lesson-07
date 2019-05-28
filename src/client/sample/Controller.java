package client.sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller  implements Initializable {

    @FXML
    TextArea textArea;
    @FXML
    TextField textField;
    @FXML
    Button btn1;
    @FXML
    ComboBox comboBox;

    Socket socket;
    DataInputStream input;
    DataOutputStream output;

    final String IP_ADRESS = "localhost";
    final int PORT = 9999;

    //Инициализация подключения к серверу
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            socket = new Socket(IP_ADRESS, PORT);
            input = new DataInputStream(socket.getInputStream()); //Данные входящего потока с Сервера
            output = new DataOutputStream(socket.getOutputStream()); //Данные исходящего потока на Сервер

            Thread streamT = new Thread(new Runnable() {
                @Override
                public void run() {
                    String msg = null;
                    //comboBox.setDisable(true);
                    comboBox.getItems().addAll("Всем: ","11111","22222","3333");
                    comboBox.setValue("Всем: ");
                    try {
                        while (true) {
                                msg = input.readUTF();
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

    //Отправка сообщения
    public void sendMsg() {
        if (!textField.getText().isEmpty()) {
            //textArea.appendText(textField.getText() + "\n");
            try {
                output.writeUTF(textField.getText());
                textField.clear();
                textField.requestFocus();
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
    }

    public void disconnect(){
        textArea.appendText("======/Нет связи с сервером/=====");
        btn1.setDisable(true);
        textField.setDisable(true);
        textField.setText("Нет связи с сервером");
    }
}