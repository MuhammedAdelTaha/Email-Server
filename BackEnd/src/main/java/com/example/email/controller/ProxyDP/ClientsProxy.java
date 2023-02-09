package com.example.email.controller.ProxyDP;

import com.example.email.model.Client;

import java.util.HashMap;
import java.util.Map;

public class ClientsProxy {
    private static ClientsProxy clientsProxy;
    private Map<String, Client> clients = new HashMap<>();

    private ClientsProxy(){}

    public static ClientsProxy getInstance(){
        if(clientsProxy == null){
            synchronized (ClientsProxy.class){
                if(clientsProxy == null){
                    clientsProxy = new ClientsProxy();
                }
            }
        }
        return clientsProxy;
    }

    public String addClient(String userName, String password){
        if(this.clients.containsKey(userName)){
            return "no";
        }
        this.clients.put(userName, new Client(userName, password));
        return "yes";
    }

    public Client getClient(String userName, String password){
        if((this.clients.containsKey(userName)) && (this.clients.get(userName).getPassword().equals(password))){
            return this.clients.get(userName);
        }
        return null;
    }

    public Map<String, Client> getClients() {
        return clients;
    }
}
