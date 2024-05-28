package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionDetails {

    private final Socket socket;

    private ObjectOutputStream output;
    private ObjectInputStream input;

    private Request request;

    public ConnectionDetails(Socket socket) {
        this.socket = socket;
    }

    public void initStreams() {
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void closeStreams() {
        try {
            if (output != null) {
                this.output.close();
            }
            if (input != null) {
                this.input.close();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public Request receiveRequest() {
        try {
            this.request = (Request) input.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
        return request;
    }

    public void sendResponse(Request response) {
        try {
            output.writeObject(response);
            output.flush();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
