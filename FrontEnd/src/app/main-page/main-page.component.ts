import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../services/login.service';
import { backMail, email } from './email';
import { MailFacade } from './mailFacade';
import { contact } from './contacts';
import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { HttpEvent } from '@angular/common/http';
import { saveAs } from 'file-saver';
import { ThisReceiver } from '@angular/compiler';
import { NgSelectComponent } from '@ng-select/ng-select';
@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css'],
})
export class MainPageComponent implements OnInit {
  isContact: boolean = false;
  disableFilterBox: boolean = true;
  disableSort: boolean = true;
  selectAllMails: boolean = false;
  time: Date = new Date('2022-04-21, 10:AM');
  time2: Date = new Date('2022-04-21, 11:AM');
  time3: Date = new Date(
    'Thu Apr 21 2022 10:00:00 GMT+0200 (Eastern European Standard Time)'
  );
  time4: Date = new Date('2022-04-21, 9:AM');
  cities = [
    { id: 1, name: 'Vilnius' },
    { id: 2, name: 'Kaunas' },
    { id: 3, name: 'Pavilnys', disabled: true },
    { id: 4, name: 'Pabradė' },
    { id: 5, name: 'Klaipėda' },
  ];
  emails: email[] = [];
  attachment: any;
  starred: email[] = [];
  trash: email[] = [];
  inbox: email[] = this.emails;
  sent: email[] = [];
  draft: email[] = [];
  contactsBack!: Map<number, contact>;
  name!: string;
  phoneNumber!: string;
  backEmails!: Map<number, backMail>;
  contacts: contact[] = [];
  file: any;
  p: number = 1;
  disableCompose: boolean = true;
  reciever!: string;
  subject: any;
  emailBody: any;
  user!: string;
  priority!: number;
  currentPage!: string;
  filename!: string;
  contactbox: boolean = true;
  searchString!: string;
  searchedEmails: any;
  searchlist!: boolean;
  openSearch!: boolean;
  filterSubject: any;
  filterSender: any;
  filterReciever: any;
  filterEmails!: email[];
  timeFormat:string='short';
  constructor(private auth: LoginService, private router: Router) {}
  ngOnInit(): void {
    this.changepage('inbox');
  }
  getemail(event: any) {
    this.reciever = event.target.value;
  }
  openCompose() {
    this.disableCompose = !this.disableCompose;
  }
  openFilter() {
    this.disableFilterBox = !this.disableFilterBox;
  }
  openSort() {
    this.disableSort = !this.disableSort;
  }
  logOut() {
    this.auth.logout();
  }
  openAddContact() {
    this.contactbox = !this.contactbox;
  }
  getSubject(event: any) {
    this.subject = event.target.value;
  }
  getEmailBody(event: any) {
    this.emailBody = event.target.value;
  }
  getname(event: any) {
    this.name = event.target.value;
  }
  getPhoneNumber(event: any) {
    this.phoneNumber = event.target.value;
  }
  select(i: email) {
    i.selected = !i.selected;
    console.log(i.selected);
  } 
  selectAll() {
    this.selectAllMails = !this.selectAllMails;
    this.emails.forEach((element) => {
      element.selected = this.selectAllMails;
    });
  }

  deleteAll() {
    var array: any = [];
    this.emails.forEach((element) => {
      if (element.selected) {
        array.push(element.id);
      }
    });
    this.auth.delete(array);
    this.changepage(this.currentPage);
  }
  starAll() {
    var array: any = [];
    this.emails.forEach((element) => {
      if (element.selected) {
        array.push(element.id);
      }
    });
    this.auth.star(array);
    this.changepage(this.currentPage);
  }
  onChangeStar(mail: any) {
    var array: any = [];
    array.push(mail.id);
    this.auth.star(array);
    this.changepage(this.currentPage);
  }
  onChangeDelete(mail: any) {
    var array: any = [];
    array.push(mail.id);
    this.auth.delete(array);
    this.changepage(this.currentPage);
  }
  changepage(page: string) {
    switch (page) {
      case 'inbox':
        this.currentPage = 'inbox';
        this.auth.getMails('inbox').subscribe((data: any) => {
          var back = data;
          var len = 0;
          var counter: number = 0;
          var array = [];
          while (counter < 1000) {
            if (back[counter]) {
              array.push(counter);
            }
            counter = counter + 1;
          }
          this.inbox = [];
          array.forEach((index) => {
            this.inbox.push({
              id: back[index].id,
              to: back[index].receiver,
              subject: back[index].emailSubject,
              from: back[index].sender,
              body: back[index].emailBody,
              attachments: back[index].names,
              priority: back[index].priority,
              time: new Date(back[index].date),
              selected: false,
              star: false,
            });
          });
        });
        this.emails = this.inbox;
        this.isContact = false;
        break;
      case 'trash':
        this.currentPage = 'trash';
        this.auth.getMails('trash').subscribe((data: any) => {
          var back = data;
          var len = 0;
          var counter: number = 0;
          var array = [];
          while (counter < 1000) {
            if (back[counter]) {
              array.push(counter);
            }
            counter = counter + 1;
          }

          this.trash = [];
          array.forEach((index) => {
            this.trash.push({
              id: back[index].id,
              to: back[index].receiver,
              subject: back[index].emailSubject,
              from: back[index].sender,
              body: back[index].emailBody,
              attachments: back[index].names,
              priority: back[index].priority,
              time: new Date(back[index].date),
              selected: false,
              star: false,
            });
          });
        });
        this.emails = this.trash;
        this.isContact = false;
        this.deleteAfterMonth(this.trash);
        break;
      case 'starred':
        this.currentPage = 'starred';
        this.auth.getMails('starred').subscribe((data: any) => {
          var back = data;
          var len = 0;
          var counter: number = 0;
          var array = [];
          while (counter < 1000) {
            if (back[counter]) {
              array.push(counter);
            }
            counter = counter + 1;
          }
          this.starred = [];
          array.forEach((index) => {
            this.starred.push({
              id: back[index].id,
              to: back[index].receiver,
              subject: back[index].emailSubject,
              from: back[index].sender,
              body: back[index].emailBody,
              attachments: back[index].names,
              priority: back[index].priority,
              time: new Date(back[index].date),
              selected: false,
              star: false,
            });
          });
        });
        this.emails = this.starred;
        this.isContact = false;
        break;
      case 'sent':
        this.currentPage = 'sent';
        this.auth.getMails('sent').subscribe((data: any) => {
          var back = data;
          var len = 0;
          var counter: number = 0;
          var array = [];
          while (counter < 1000) {
            if (back[counter]) {
              array.push(counter);
            }
            counter = counter + 1;
          }

          this.sent = [];
          array.forEach((index) => {
            this.sent.push({
              id: back[index].id,
              to: back[index].receiver,
              subject: back[index].emailSubject,
              from: back[index].sender,
              body: back[index].emailBody,
              attachments: back[index].names,
              priority: back[index].priority,
              time: new Date(back[index].date),
              selected: false,
              star: false,
            });
          });
        });
        this.emails = this.sent;
        this.isContact = false;
        break;
      case 'draft':
        this.currentPage = 'draft';
        this.auth.getMails('draft').subscribe((data: any) => {
          var back = data;
          var len = 0;
          var counter: number = 0;
          var array = [];
          while (counter < 1000) {
            if (back[counter]) {
              array.push(counter);
            }
            counter = counter + 1;
          }
          this.draft = [];
          array.forEach((index) => {
            this.draft.push({
              id: back[index].id,
              to: back[index].receiver,
              subject: back[index].emailSubject,
              from: back[index].sender,
              body: back[index].emailBody,
              attachments: back[index].names,
              priority: back[index].priority,
              time: new Date(back[index].date),
              selected: false,
              star: false,
            });
          });
        });
        this.emails = this.draft;
        
        this.isContact = false;
        break;
      case 'contact':
        this.currentPage = 'contact';
        this.isContact = true;
        this.auth.getcontacts().subscribe((data: any) => {
          this.contacts = [];
          const iter = Object.keys(data);
          for (let i of iter) {
            this.contacts.push({
              id: data[i].contactCounter,
              name: data[i].name,
              phoneNumber: data[i].phoneNumber,
              selected: false,
            });
          }
        });

        break;
    }
    this.emails.sort((objA, objB) => objB.time.getTime() - objA.time.getTime());
  }
  deleteAfterMonth(trash:email[]){
    console.log("trash len = "+trash.length);
    for (let index = 0; index < trash.length; index++) {
      const element = trash[index];
      var date = new Date();
      console.log(date);
      var time = date.getTime() - element.time.getTime();
      console.log("Time "+ time + " ms");
      if(time > 2592000000){
        this.onChangeDelete(element);
      }
    }
  }
  getFile(event: any) {
    const files = event.target.files;
    const attach = new FormData();
    for (const file of files) {
      attach.append('files', file, file.name);
    }
    this.auth.upload(attach).subscribe((data: string) => {
      console.log('Upload');
      console.log(JSON.parse(data));
    });
  }
  downlaod(filename: string) {
    this.auth.download(filename).subscribe((event) => {
      console.log(event);
    });
  }

  setPriority(event: any) {
    this.priority = event.target.value;
    var x = Number(this.priority);
    if (!(x == 0 || x == 1 || x == 2 || x == 3 || x == 4)) {
      this.priority = 1;
    }
  }

  sendMessage() {
    const mail = new MailFacade(
      this.reciever,
      this.user,
      this.emailBody,
      this.subject,
      this.priority, 
      this.emails.length
    );
    const frontMail = mail.getFrontMail();
    const backMail = mail.getBackMail();
    frontMail.id = this.emails.length;
    this.auth.send(backMail);
    this.disableCompose = !this.disableCompose;
  }
  sortPriority() {
    this.emails.sort((objA, objB) => objA.priority - objB.priority);
  }
  sortTime() {
    this.emails.sort((objA, objB) => objB.time.getTime() - objA.time.getTime());
  }
  sortSubject(){
    this.emails.sort((objA, objB) => objB.subject > objA.subject ? -1 : 1);
  }
  sortBody(){
    this.emails.sort((objA, objB) => objB.body > objA.body ? -1 : 1);
  }
  changeTimeFormat() {
    if(this.timeFormat == 'short'){
      this.timeFormat = 'medium';
    }else if(this.timeFormat == 'medium'){
      this.timeFormat = 'full';
    }else{
      this.timeFormat = 'short';
    }
  }
  cancelMessage() {
    const mail = new MailFacade(
      this.reciever,
      this.user,
      this.emailBody,
      this.subject,
      this.priority,
      this.emails.length
    );
    const frontMail = mail.getFrontMail();
    const backMail = mail.getBackMail();
    frontMail.id = this.emails.length;
    this.auth.draft(backMail);
    this.disableCompose = !this.disableCompose;
  }
  downloadAttachment(filename: string): void {
    this.filename = filename;
    this.auth.download(filename).subscribe(
      (event) => {
        console.log(event);
        this.resportProgress(event);
      },
      (error: HttpErrorResponse) => {
        console.log(error);
      }
    );
  }
  private resportProgress(httpEvent: HttpEvent<string[] | Blob>): void {
    switch (httpEvent.type) {
      case HttpEventType.Response:
        if (httpEvent.body instanceof Array) {
        } else {
          saveAs(
            new File([httpEvent.body!], this.filename.substring(15), {
              type: `${httpEvent.headers.get('Content-Type')};charset=utf-8`,
            })
          );
        }
        break;
    }
  }
  addcontact() {
    this.auth.addcontact(this.name, this.phoneNumber);
    this.contactbox = !this.contactbox;
  }
  deleteContact(contact:contact) { 
    var array = [];
    array.push(contact.id)
    this.auth.deletecontact(array);
  }
  cancelcontact() {
    this.contactbox = !this.contactbox;
  }
  setSearch(search: string) {
    this.searchString = search;
    this.openSearch = !this.openSearch;
  }
  search() {
    if (this.searchString != null) {
      switch (this.searchString) {
        case 'sender':
          for (let i = 0; i < this.emails.length; i++) {
            if (this.emails[i].from == this.searchString) {
              this.searchedEmails.push(this.emails[i]);
            }
          }
          this.emails = this.searchedEmails;
          break;
        case 'reciever':
          for (let i = 0; i < this.emails.length; i++) {
            if (this.emails[i].to.includes(this.searchString)) {
              this.searchedEmails.push(this.emails[i]);
            }
          }
          this.emails = this.searchedEmails;
          break;
        case 'subject':
          for (let i = 0; i < this.emails.length; i++) {
            if (this.emails[i].subject.includes(this.searchString)) {
              this.searchedEmails.push(this.emails[i]);
            }
          }
          this.emails = this.searchedEmails;
          break;
        case 'body':
          for (let i = 0; i < this.emails.length; i++) {
            if (this.emails[i].body.includes(this.searchString)) {
              this.searchedEmails.push(this.emails[i]);
            }
          }
          break;
        case 'attachment':
          for (let i = 0; i < this.emails.length; i++) {
            if (this.emails[i].attachments.includes(this.searchString)) {
              this.searchedEmails.push(this.emails[i]);
            }
          }
          this.emails = this.searchedEmails;
          break;
        case 'priority':
          for (let i = 0; i < this.emails.length; i++) {
            if (this.emails[i].priority == parseInt(this.searchString)) {
              this.searchedEmails.push(this.emails[i]);
            }
          }
          this.emails = this.searchedEmails;
      }
    }
  }
  openSearchlist() {
    this.openSearch = !this.openSearch;
  }
  getFilterSubject(event: { target: { value: any; }; }){
    this.filterSubject=event.target.value
  }
  getfilterSender(event: { target: { value: any; }; }){
    this.filterSender=event.target.value;
  }
  getfilterReciever(event: { target: { value: any; }; }){
    this.filterReciever=event.target.value;
  }
  filter(){
    if(this.filterReciever==""||this.filterReciever==null){
         this.filterReciever =null;
    }
    if(this.filterSender==""||this.filterSender==null){
      this.filterSender =null;
    }
    if(this.filterSubject==""||this.filterSubject==null){
      this.filterSubject =null;
    }
    this.auth.filter( this.filterSender,this.filterReciever,this.filterSubject,).subscribe((data)=>{
      
    //   var back = data;
    //   var len = 0;
    //   var counter: number = 0;
    //   var array = [];
    //   while (counter < 1000) {
    //     if (back[counter]) {
    //       array.push(counter);
    //     }
    //     counter = counter + 1;
    //   }
    //   this.filterEmails = [];
    //   array.forEach((index) => {
    //     this.filterEmails.push({
    //       id: back[index].id,
    //       to: back[index].receiver,
    //       subject: back[index].emailSubject,
    //       from: back[index].sender,
    //       body: back[index].emailBody,
    //       attachments: back[index].names,
    //       priority: back[index].priority,
    //       time: new Date(back[index].date),
    //       selected: false,
    //       star: false,
    //     });
    //   });
    // this.emails = this.filterEmails;
    })
  }
}
