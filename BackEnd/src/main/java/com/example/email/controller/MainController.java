package com.example.email.controller;

import com.example.email.controller.ProxyDP.ClientsProxy;
import com.example.email.controller.filterDP.AndCriteria;
import com.example.email.controller.filterDP.ReceiverFilter;
import com.example.email.controller.filterDP.SenderFilter;
import com.example.email.controller.filterDP.SubjectFilter;
import com.example.email.model.Client;
import com.example.email.model.Email;
import com.example.email.model.Folder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("")
public class MainController {
    final
    FileUploadService fileUploadService;
    private final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
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
    private final String dataDirectory = "resources/";

    public MainController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @GetMapping("/signUp/{userName}/{password}")
    private String signUp(@PathVariable("userName") String userName, @PathVariable("password") String password) {
        load("clients", dataDirectory + "clients/clients.json");
        String signedUp = this.clients.addClient(userName, password);
        save("clients", dataDirectory + "clients/clients.json");
        return signedUp;
    }

    @GetMapping("/logIn/{userName}/{password}")
    private String logIn(@PathVariable("userName") String userName, @PathVariable("password") String password) {
        load("clients", dataDirectory + "clients/clients.json");
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
    private String getEmails(@PathVariable("folderName") String folderName) {
        if(this.client != null){
            this.folderName = folderName;
            load("emails", dataDirectory +  folderName + "/" + this.client.getUserName() + ".json");
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
    ) {

        if(this.client != null){
            load("id", dataDirectory + "id/latestId.json");
            while(!receivers.isEmpty()){
                String receiver = receivers.poll();
                if(this.client != null && this.clients.getClients().containsKey(receiver) && !this.client.getUserName().equals(receiver)){
                    temp = clone(this.emails.getEmails());
                    load("emails", dataDirectory + "sent/" + this.client.getUserName() + ".json");
                    this.emails.add(id, this.client.getUserName(), receiver, emailSubject, emailBody, date, priority, this.names);
                    save("emails", dataDirectory + "sent/" + this.client.getUserName() + ".json");
                    load("emails", dataDirectory + "inbox/" + receiver + ".json");
                    this.emails.add(id, this.client.getUserName(), receiver, emailSubject, emailBody, date, priority, this.names);
                    save("emails", dataDirectory + "inbox/" + receiver + ".json");
                    this.emails.setEmails(temp);
                    id++;
                }
            }
            this.names = null;
            save("id", dataDirectory + "id/latestId.json"); return;
        }
        System.out.println("log in first...");
    }

    @GetMapping("/draft/{receivers}/{emailSubject}/{emailBody}/{date}/{priority}")
    private void draft(
            @PathVariable("receivers") LinkedList<String> receivers,
            @PathVariable("emailSubject") String emailSubject,
            @PathVariable("emailBody") String emailBody,@PathVariable("date") String date,@PathVariable("priority") int priority
    ) {

        if(this.client != null){
            load("id", dataDirectory + "id/latestId.json");
            while(!receivers.isEmpty()){
                String receiver = receivers.poll();
                if(this.client != null && this.clients.getClients().containsKey(receiver) && !this.client.getUserName().equals(receiver)){
                    temp = clone(this.emails.getEmails());
                    load("emails", dataDirectory + "draft/" + this.client.getUserName() + ".json");
                    this.emails.add(id, this.client.getUserName(), receiver, emailSubject, emailBody, date, priority, this.names);
                    save("emails", dataDirectory + "draft/" + this.client.getUserName() + ".json");
                    this.emails.setEmails(temp);
                    id++;
                }
            }
            this.names = null;
            save("id", dataDirectory + "id/latestId.json"); return;
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
            String path = dataDirectory + "/attachments" + fileName;
            File file = new File(path);
            try {
                if (file.exists()) {
                    //This command is for opening files
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
    private void delete(@PathVariable("ids") ArrayList<Integer> ids) {

        if(this.client != null){
            for(Integer id : ids){
                Email toBeDelete = null;
                if(this.emails.isContain(id)){
                    toBeDelete = this.emails.getById(id);
                    this.emails.remove(id);
                    save("emails", dataDirectory + this.folderName + "/" + this.client.getUserName() + ".json");
                }
                temp = clone(this.emails.getEmails());
                if(!this.folderName.equals("trash")){
                    if(toBeDelete != null){
                        load("emails", dataDirectory + "trash/" + this.client.getUserName() + ".json");
                        this.emails.add(toBeDelete.getId(), toBeDelete.getSender(), toBeDelete.getReceiver(), toBeDelete.getEmailSubject()
                                , toBeDelete.getEmailBody(), toBeDelete.getDate(), toBeDelete.getPriority(), toBeDelete.getNames());
                        save("emails", dataDirectory + "trash/" + this.client.getUserName() + ".json");
                    }
                }
                this.emails.setEmails(temp);
            }
            return;
        }
        System.out.println("log in first...");
    }

    @GetMapping("/star/{ids}")
    private void star(@PathVariable("ids") ArrayList<Integer> ids) {

        if(this.client != null){
            for(Integer id : ids){
                if(this.client != null && this.emails.isContain(id)){
                    temp = clone(this.emails.getEmails());
                    load("emails", dataDirectory + "starred/" + this.client.getUserName() + ".json");
                    this.emails.add(id, temp.get(id).getSender(), temp.get(id).getReceiver()
                            , temp.get(id).getEmailSubject(), temp.get(id).getEmailBody()
                            , temp.get(id).getDate(), temp.get(id).getPriority(), temp.get(id).getNames());
                    save("emails", dataDirectory + "starred/" + this.client.getUserName() + ".json");
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
    private String getContacts() {
        if(this.client != null){
            this.folderName = "contacts";
            load("contacts", dataDirectory + "contacts/" + this.client.getUserName() + ".json");
            return gson.toJson(this.contacts.getContacts());
        }
        return null;
    }

    @GetMapping("/addContact/{name}/{phoneNumber}/{emails}")
    private void addContact(@PathVariable("name") String name, @PathVariable("phoneNumber") String phoneNumber,
                            @PathVariable("emails") ArrayList<String> emails) {

        if(this.client != null){
            load("contactCounter", dataDirectory + "contactCounter/latestContactCounter.json");
            load("contacts", dataDirectory + "contacts/" + this.client.getUserName() + ".json");
            this.contacts.add(contactCounter++ , name , phoneNumber, emails);
            save("contacts", dataDirectory + "contacts/" + this.client.getUserName() + ".json");
            save("contactCounter", dataDirectory + "contactCounter/latestContactCounter.json");
            return;
        }
        System.out.println("log in first...");
    }

    @GetMapping("/deleteContact/{ids}")
    private void deleteContacts(@PathVariable("ids") ArrayList<Integer> ids) {
        if(this.client != null){
            load("contacts", dataDirectory + "contacts/" + this.client.getUserName() + ".json");
            for(Integer contactCounter : ids){
                if(this.contacts.contains(contactCounter)){
                    this.contacts.remove(contactCounter);
                }
            }
            save("contacts", dataDirectory + "contacts/" + this.client.getUserName() + ".json");
            return;
        }
        System.out.println("log in first...");
    }

    @GetMapping("/getFolders")
    private String getFolders() {
        if(this.client != null){
            load("folders", dataDirectory + "folders/" + this.client.getUserName() + ".json");
            return gson.toJson(this.folders.getFolders());
        }
        return null;
    }

    @GetMapping("/addFolder/{name}/{ids}")
    private void addFolder(@PathVariable("name") String name, @PathVariable("ids") ArrayList<Integer> ids) {
        if(this.client != null){
            load("folderCounter", dataDirectory + "folderCounter/latestFolderCounter.json");
            load("folders", dataDirectory + "folders/" + this.client.getUserName() + ".json");
            load("emails", dataDirectory + folderName + "/" + this.client.getUserName() + ".json");
            System.out.println(gson.toJson(this.emails.getEmails()));
            this.folders.add(name, ids, this.folderCounter++, this.folderName, this.emails);
            save("folderCounter", dataDirectory + "folderCounter/latestFolderCounter.json");
            save("folders", dataDirectory + "folders/" + this.client.getUserName() + ".json");
            return;
        }
        System.out.println("log in first...");
    }

    @GetMapping("/renameFolder/{id}/{newName}")
    private void renameFolder(@PathVariable("id") Integer id, @PathVariable("newName") String newName) {
        if(this.client != null){
            load("folders", dataDirectory + "folders/" + this.client.getUserName() + ".json");
            if(this.folders.contains(id)){
                this.folders.getById(id).setName(newName);
            }
            save("folders", dataDirectory + "folders/" + this.client.getUserName() + ".json");
            return;
        }
        System.out.println("log in first...");
    }

    @GetMapping("/deleteFolders/{ids}")
    private void deleteFolders(@PathVariable("ids") ArrayList<Integer> ids) {
        if(this.client != null){
            load("folders", dataDirectory + "folders/" + this.client.getUserName() + ".json");
            for(Integer folderCounter : ids){
                if(this.folders.contains(folderCounter)) {
                    this.folders.remove(folderCounter);
                }
            }
            save("folders", dataDirectory + "folders/" + this.client.getUserName() + ".json");
            return;
        }
        System.out.println("log in first...");
    }

    @GetMapping("/deleteFolderEmails/{folderCounter}/{ids}")
    private void deleteFolderEmails(@PathVariable("folderCounter") Integer folderCounter, @PathVariable("ids") ArrayList<Integer> ids) {
        if(this.client != null){
            load("folders", dataDirectory + "folders/" + this.client.getUserName() + ".json");
            if(this.folders.contains(folderCounter)){
                Folder tempFolders = this.folders.getById(folderCounter);
                for(Integer id : ids){
                    tempFolders.getFolderMails().remove(id);
                }
            }
            save("folders", dataDirectory + "folders/" + this.client.getUserName() + ".json");
            return;
        }
        System.out.println("log in first...");
    }

    private void save(String localFolderName, String path){

        File file = new File(path);
        File parentDirectories = file.getParentFile();
        if (parentDirectories != null)
            parentDirectories.mkdirs();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {

            String jsonString = null;
            switch (localFolderName) {
                case "id" -> jsonString = gson.toJson(this.id);
                case "contactCounter" -> jsonString = gson.toJson(this.contactCounter);
                case "folderCounter" -> jsonString = gson.toJson(this.folderCounter);
                case "clients" -> jsonString = gson.toJson(this.clients);
                case "emails" -> jsonString = gson.toJson(this.emails);
                case "contacts" -> jsonString = gson.toJson(this.contacts);
                case "folders" -> jsonString = gson.toJson(this.folders);
            }
            if (jsonString != null) {
                writer.write(jsonString);
            }
        }catch (Exception e){
            System.out.println("System Error...");
        }
    }

    private void load(String localFolderName, String path) {
        this.emails.clear();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            switch (localFolderName) {
                case "id" -> this.id = gson.fromJson(bufferedReader, Integer.class);
                case "contactCounter" -> this.contactCounter = gson.fromJson(bufferedReader, Integer.class);
                case "folderCounter" -> this.folderCounter = gson.fromJson(bufferedReader, Integer.class);
                case "clients" -> this.clients = gson.fromJson(bufferedReader, ClientsProxy.class);
                case "emails" -> this.emails = gson.fromJson(bufferedReader, EmailsController.class);
                case "contacts" -> this.contacts = gson.fromJson(bufferedReader, ContactController.class);
                case "folders" -> this.folders = gson.fromJson(bufferedReader, FoldersController.class);
            }
        }catch (Exception e){
            System.out.println("System Error...");
        }finally {
            if(bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    System.out.println("System Error...");
                }
            }
        }
    }

    private Map<Integer, Email> clone(Map<Integer, Email> emails){
        return new HashMap<>(emails);
    }
}
