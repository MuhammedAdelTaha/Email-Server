package com.example.email.controller;

import com.example.email.controller.ProxyDP.ClientsProxy;
import com.example.email.controller.filterDP.AndCriteria;
import com.example.email.controller.filterDP.ReceiverFilter;
import com.example.email.controller.filterDP.SenderFilter;
import com.example.email.controller.filterDP.SubjectFilter;
import com.example.email.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("")
public class MainController {
    @Autowired
    FileUploadService fileUploadService;
    private Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private ClientsProxy clients = ClientsProxy.getInstance();
    private Client client = null;
    private String folderName = null;
    private EmailsController emails = new EmailsController();
    private Map<Integer, Email> temp = new HashMap<>();
    private ArrayList<String> names = null;
    private Integer id = 0;
    private ContactController contacts = new ContactController();
    private Integer contactCounter = 0;
    private FoldersController folders = new FoldersController();
    private Integer folderCounter = 0;


    @GetMapping("/signUp/{userName}/{password}")
    private String signUp(@PathVariable("userName") String userName, @PathVariable("password") String password) throws Exception {
        load("clients", "src/main/resources/data/clients/clients.json");
        String signedUp = this.clients.addClient(userName, password);
        save("clients", "src/main/resources/data/clients/clients.json");
        return signedUp;
    }

    @GetMapping("/logIn/{userName}/{password}")
    private String logIn(@PathVariable("userName") String userName, @PathVariable("password") String password) throws Exception {
        load("clients", "src/main/resources/data/clients/clients.json");
        if((this.clients.getClient(userName, password) != null) && (this.client == null)){
            this.client = new Client(userName, password);
            return "yes";
        }
        else if(this.client != null){
            return "denied";
        }
        return "no";
    }

    @GetMapping("/logOut")
    private void logOut(){
        if(this.client != null){
            this.client = null;
            this.folderName = null;
            this.emails.clear();
            this.temp.clear();
        }
    }

    @GetMapping("/getEmails/{folderName}")
    private String getEmails(@PathVariable("folderName") String folderName) throws Exception {
        if(this.client != null){
            this.folderName = folderName;
            load("emails", "src/main/resources/data/" + folderName + "/" + this.client.getUserName() + ".json");
            return gson.toJson(this.emails.getEmails());
        }
        System.out.println("log in first...");
        return null;
    }

    @GetMapping("/compose/{receivers}/{emailSubject}/{emailBody}/{date}/{priority}")
    private void compose(
            @PathVariable("receivers") LinkedList<String> receivers,
            @PathVariable("emailSubject") String emailSubject,
            @PathVariable("emailBody") String emailBody,@PathVariable("date") String date,@PathVariable("priority") int priority
    ) throws Exception {

        if(this.client != null){
            load("id", "src/main/resources/id/latestId.json");
            while(!receivers.isEmpty()){
                String receiver = receivers.poll();
                if(this.client != null && this.clients.getClients().containsKey(receiver) && !this.client.getUserName().equals(receiver)){
                    temp = clone(this.emails.getEmails());
                    load("emails", "src/main/resources/data/sent/" + this.client.getUserName() + ".json");
                    this.emails.add(id, this.client.getUserName(), receiver, emailSubject, emailBody, date, priority, this.names);
                    save("emails", "src/main/resources/data/sent/" + this.client.getUserName() + ".json");
                    load("emails", "src/main/resources/data/inbox/" + receiver + ".json");
                    this.emails.add(id, this.client.getUserName(), receiver, emailSubject, emailBody, date, priority, this.names);
                    save("emails", "src/main/resources/data/inbox/" + receiver + ".json");
                    this.emails.setEmails(temp);
                    id++;
                }
            }
            this.names = null;
            save("id", "src/main/resources/id/latestId.json"); return;
        }
        System.out.println("log in first...");
    }

    @GetMapping("/draft/{receivers}/{emailSubject}/{emailBody}/{date}/{priority}")
    private void draft(
            @PathVariable("receivers") LinkedList<String> receivers,
            @PathVariable("emailSubject") String emailSubject,
            @PathVariable("emailBody") String emailBody,@PathVariable("date") String date,@PathVariable("priority") int priority
    ) throws Exception {

        if(this.client != null){
            load("id", "src/main/resources/id/latestId.json");
            while(!receivers.isEmpty()){
                String receiver = receivers.poll();
                if(this.client != null && this.clients.getClients().containsKey(receiver) && !this.client.getUserName().equals(receiver)){
                    temp = clone(this.emails.getEmails());
                    load("emails", "src/main/resources/data/draft/" + this.client.getUserName() + ".json");
                    this.emails.add(id, this.client.getUserName(), receiver, emailSubject, emailBody, date, priority, this.names);
                    save("emails", "src/main/resources/data/draft/" + this.client.getUserName() + ".json");
                    this.emails.setEmails(temp);
                    id++;
                }
            }
            this.names = null;
            save("id", "src/main/resources/id/latestId.json"); return;
        }
        System.out.println("log in first...");
    }

    @PostMapping("/uploadFile/")
    private void uploadFile(@RequestParam("attachments") ArrayList<MultipartFile> attachments) throws IllegalStateException, IOException {

        if(this.client != null){
            this.names = new ArrayList<>();
            for (MultipartFile attachment : attachments){
                this.fileUploadService.uploadFile(attachment);
                this.names.add(attachment.getOriginalFilename());
            }
            return;
        }
        System.out.println("log in first...");
    }

    @GetMapping("/openFile/{fileName}")
    private void openFile(@PathVariable("fileName") String fileName) {

        if(this.client != null){
            String path = "C:\\Users\\Mohamed Adel\\IdeaProjects\\email\\src\\main\\resources\\attachments\\" + fileName;
            File file = new File(path);
            try {
                if (file.exists()) {
                    Process process = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + path);
                    process.waitFor();
                } else {
                    System.out.println("file does not exist");
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            return;
        }
        System.out.println("log in first...");
    }

    @GetMapping("/delete/{ids}")
    private void delete(@PathVariable("ids") ArrayList<Integer> ids) throws Exception {

        if(this.client != null){
            for(Integer id : ids){
                Email toBeDelete = null;
                if(this.emails.isContain(id)){
                    toBeDelete = this.emails.getById(id);
                    this.emails.remove(id);
                    save("emails", "src/main/resources/data/" + this.folderName + "/" + this.client.getUserName() + ".json");
                }
                temp = clone(this.emails.getEmails());
                if(!this.folderName.equals("trash")){
                    if(toBeDelete != null){
                        load("emails", "src/main/resources/data/trash/" + this.client.getUserName() + ".json");
                        this.emails.add(toBeDelete.getId(), toBeDelete.getSender(), toBeDelete.getReceiver(), toBeDelete.getEmailSubject()
                                , toBeDelete.getEmailBody(), toBeDelete.getDate(), toBeDelete.getPriority(), toBeDelete.getNames());
                        save("emails", "src/main/resources/data/trash/" + this.client.getUserName() + ".json");
                    }
                }
                this.emails.setEmails(temp);
            }
            return;
        }
        System.out.println("log in first...");
    }

    @GetMapping("/star/{ids}")
    private void star(@PathVariable("ids") ArrayList<Integer> ids) throws Exception {

        if(this.client != null){
            for(Integer id : ids){
                if(this.client != null && this.emails.isContain(id)){
                    temp = clone(this.emails.getEmails());
                    load("emails", "src/main/resources/data/starred/" + this.client.getUserName() + ".json");
                    this.emails.add(id, temp.get(id).getSender(), temp.get(id).getReceiver()
                            , temp.get(id).getEmailSubject(), temp.get(id).getEmailBody()
                            , temp.get(id).getDate(), temp.get(id).getPriority(), temp.get(id).getNames());
                    save("emails", "src/main/resources/data/starred/" + this.client.getUserName() + ".json");
                    this.emails.setEmails(temp);
                }
            }
            return;
        }
        System.out.println("log in first...");
    }

    @GetMapping("/filter/{sender}/{receiver}/{subject}")
    private String filter(@PathVariable("sender") String sender, @PathVariable("receiver") String receiver, @PathVariable("subject") String subject){

        if(this.client != null){
            Map<Integer, Email> filteredEmails = new AndCriteria(new SenderFilter(sender), new ReceiverFilter(receiver)
                    , new SubjectFilter(subject)).meetsCriteria(this.emails.getEmails());
            return gson.toJson(filteredEmails);
        }
        System.out.println("log in first...");
        return null;
    }

    @GetMapping("/getContacts")
    private String getContacts() throws Exception {
        if(this.client != null){
            this.folderName = "contacts";
            load("contacts", "src/main/resources/data/contacts/" + this.client.getUserName() + ".json");
            return gson.toJson(this.contacts.getContacts());
        }
        return null;
    }

    @GetMapping("/addContact/{name}/{phoneNumber}/{emails}")
    private void addContact(@PathVariable("name") String name, @PathVariable("phoneNumber") String phoneNumber,
                            @PathVariable("emails") ArrayList<String> emails) throws Exception {

        if(this.client != null){
            load("contactCounter", "src/main/resources/contactCounter/latestContactCounter.json");
            load("contacts", "src/main/resources/data/contacts/" + this.client.getUserName() + ".json");
            this.contacts.add(contactCounter++ , name , phoneNumber, emails);
            save("contacts", "src/main/resources/data/contacts/" + this.client.getUserName() + ".json");
            save("contactCounter", "src/main/resources/contactCounter/latestContactCounter.json");
            return;
        }
        System.out.println("log in first...");
    }

    @GetMapping("/deleteContact/{ids}")
    private void deleteContacts(@PathVariable("ids") ArrayList<Integer> ids) throws Exception {
        if(this.client != null){
            load("contacts", "src/main/resources/data/contacts/" + this.client.getUserName() + ".json");
            for(Integer contactCounter : ids){
                if(this.contacts.contains(contactCounter)){
                    this.contacts.remove(contactCounter);
                }
            }
            save("contacts", "src/main/resources/data/contacts/" + this.client.getUserName() + ".json");
            return;
        }
        System.out.println("log in first...");
    }

    @GetMapping("/getFolders")
    private String getFolders() throws Exception {
        if(this.client != null){
            load("folders", "src/main/resources/data/folders/" + this.client.getUserName() + ".json");
            return gson.toJson(this.folders.getFolders());
        }
        return null;
    }

    @GetMapping("/addFolder/{name}/{ids}")
    private void addFolder(@PathVariable("name") String name, @PathVariable("ids") ArrayList<Integer> ids) throws Exception {
        if(this.client != null){
            load("folderCounter", "src/main/resources/folderCounter/latestFolderCounter.json");
            load("folders", "src/main/resources/data/folders/" + this.client.getUserName() + ".json");
            load("emails", "src/main/resources/data/" + folderName + "/" + this.client.getUserName() + ".json");
            System.out.println(gson.toJson(this.emails.getEmails()));
            this.folders.add(name, ids, this.folderCounter++, this.folderName, this.emails);
            save("folderCounter", "src/main/resources/folderCounter/latestFolderCounter.json");
            save("folders", "src/main/resources/data/folders/" + this.client.getUserName() + ".json");
            return;
        }
        System.out.println("log in first...");
    }

    @GetMapping("/renameFolder/{id}/{newName}")
    private void renameFolder(@PathVariable("id") Integer id, @PathVariable("newName") String newName) throws Exception {
        if(this.client != null){
            load("folders", "src/main/resources/data/folders/" + this.client.getUserName() + ".json");
            if(this.folders.contains(id)){
                this.folders.getById(id).setName(newName);
            }
            save("folders", "src/main/resources/data/folders/" + this.client.getUserName() + ".json");
            return;
        }
        System.out.println("log in first...");
    }

    @GetMapping("/deleteFolders/{ids}")
    private void deleteFolders(@PathVariable("ids") ArrayList<Integer> ids) throws Exception {
        if(this.client != null){
            load("folders", "src/main/resources/data/folders/" + this.client.getUserName() + ".json");
            for(Integer folderCounter : ids){
                if(this.folders.contains(folderCounter)) {
                    this.folders.remove(folderCounter);
                }
            }
            save("folders", "src/main/resources/data/folders/" + this.client.getUserName() + ".json");
            return;
        }
        System.out.println("log in first...");
    }

    @GetMapping("/deleteFolderEmails/{folderCounter}/{ids}")
    private void deleteFolderEmails(@PathVariable("folderCounter") Integer folderCounter, @PathVariable("ids") ArrayList<Integer> ids) throws Exception {
        if(this.client != null){
            load("folders", "src/main/resources/data/folders/" + this.client.getUserName() + ".json");
            if(this.folders.contains(folderCounter)){
                Folder tempFolders = this.folders.getById(folderCounter);
                for(Integer id : ids){
                    if(tempFolders.getFolderMails().containsKey(id)) {
                        tempFolders.getFolderMails().remove(id);
                    }
                }
            }
            save("folders", "src/main/resources/data/folders/" + this.client.getUserName() + ".json");
            return;
        }
        System.out.println("log in first...");
    }


    private void save(String localFolderName, String path) throws Exception {
        try {
            String jsonString = null;
            switch (localFolderName){
                case "id": {
                    jsonString = gson.toJson(this.id); break;
                }
                case "contactCounter": {
                    jsonString = gson.toJson(this.contactCounter); break;
                }
                case "folderCounter": {
                    jsonString = gson.toJson(this.folderCounter); break;
                }
                case "clients": {
                    jsonString = gson.toJson(this.clients); break;
                }
                case "emails": {
                    jsonString = gson.toJson(this.emails); break;
                }
                case "contacts": {
                    jsonString = gson.toJson(this.contacts); break;
                }
                case "folders": {
                    jsonString = gson.toJson(this.folders);
                }
            }
            File file = new File(path);
            FileWriter writer = new FileWriter(file);
            writer.write(jsonString);
            writer.flush();
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void load(String localFolderName, String path) throws Exception {
        this.emails.clear();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            switch (localFolderName){
                case "id": {
                    this.id = gson.fromJson(bufferedReader, Integer.class); break;
                }
                case "contactCounter": {
                    this.contactCounter = gson.fromJson(bufferedReader, Integer.class); break;
                }
                case "folderCounter": {
                    this.folderCounter = gson.fromJson(bufferedReader, Integer.class); break;
                }
                case "clients": {
                    this.clients = gson.fromJson(bufferedReader, ClientsProxy.class); break;
                }
                case "emails":{
                    this.emails = gson.fromJson(bufferedReader, EmailsController.class); break;
                }
                case "contacts": {
                    this.contacts = gson.fromJson(bufferedReader, ContactController.class); break;
                }
                case "folders": {
                    this.folders = gson.fromJson(bufferedReader, FoldersController.class);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private Map<Integer, Email> clone(Map<Integer, Email> emails){
        Map<Integer, Email> clonedEmails = new HashMap<>();
        clonedEmails.clear();
        for(Map.Entry<Integer, Email> email : emails.entrySet()){
            clonedEmails.put(email.getKey(), email.getValue());
        }
        return clonedEmails;
    }
}
