import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpEvent } from '@angular/common/http';
import { backMail, email } from '../main-page/email';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  url = 'http://localhost:8080/';

  constructor(private router: Router, private http: HttpClient) {}
  
  logout() {
    console.log(this.url+'logOut');
    this.http.get(this.url+'logOut').subscribe((data)=>{
      console.log(data);
    });
    this.router.navigate(['login']);
  }

  login({ email, password }: any): Observable<any> {
    console.log("Log in "+email + "##" + password);
    var result:string;
    return this.http.get(this.url+'logIn/'+email+'/'+password,{responseType:'text'}) 
  }
  delete(id:number[]){
    console.log(this.url+'delete/'+id);
    var result;
    this.http.get(this.url+'delete/'+id).subscribe((data)=>{
      result = data;
      console.log(result);
    });
  }
  star(id:number[]){ 
    console.log(this.url+'star/'+id);
    var result;
    this.http.get(this.url+'star/'+id).subscribe((data)=>{
      result = data;
      console.log(result);
    });
  }
  signup({ email, password }: any): Observable<string>{
    console.log("Sign up "+email + "##" + password);
    var result:string;
    return this.http.get(this.url+'signUp/'+email+'/'+password,{responseType:'text'})
  }
  
  send(mail:any){
    console.log("Sending");
    var result;
    console.log(mail);
    console.log(this.url+'compose/'+mail.receivers+'/'+mail.subject+'/'+mail.body+'/'+mail.time+'/'+mail.priority);
    this.http.get(this.url+'compose/'+mail.receivers+'/'+mail.subject+'/'+mail.body+'/'+mail.time+'/'+mail.priority).subscribe((data)=>{
      result = data;
      console.log(result);
    });
  }
  draft(mail:any){
    console.log("Draft");
    var result;
    console.log(mail);
    console.log(this.url+'draft/'+mail.receivers+'/'+mail.subject+'/'+mail.body+'/'+mail.time+'/'+mail.priority);
    this.http.get(this.url+'draft/'+mail.receivers+'/'+mail.subject+'/'+mail.body+'/'+mail.time+'/'+mail.priority).subscribe((data)=>{
      result = data;
      console.log(result);
    });
  }
  getMails(folder:string): any{
    console.log("Get Inbox from\n"+this.url+'getEmails/'+folder);
    return this.http.get(this.url+'getEmails/'+folder);
  }
  convertMailsIntoFront(mails:backMail[]):email[]{
    var len = 0; 
    var output=[];
    while (true) {
      if(mails[len]){
        len++;
      }
      else{
        break;
      }
    }
    console.log("Length "+len);
    
    for (let index = 0; index < len; index++) {
      const element = mails[index];
      console.log(index);
      console.log(element);
      var mail:email = {
        id: element.id,
        to:element.receiver,
        subject:element.emailSubject,
        from:element.sender,
        body: element.emailBody,
        attachments:[],
        priority: element.priority,
        time: new Date(element.date),
        star: false,
        selected: false,
      };
      console.log("@@\n"+mail.id);
      
      output.push(mail);
    }
    console.log("Result\n"+output);
    
    // mails.forEach(element => {
    //   const mail = {
    //     id: element.id,
    //     subject:element.emailSubject,
    //     to:element.receiver,
    //     from:element.sender,
    //     body: element.emailBody,
    //     attachments:[],
    //     star: false,
    //     selected: false,
    //     time: new Date(element.date),
    //     priority: element.priority,
    //   };
    //   output.push(mail);
    // });
    if(output){
      return output;
    }
    return [];
  }
  upload(formData: FormData): any {
    return this.http.post(`${this.url}upload`, formData, {responseType: 'text'});
  }
  download(filename: string): Observable<HttpEvent<Blob>> {
    return this.http.get(`${this.url}download/${filename}`, {
      reportProgress: true,
      observe: 'events',
      responseType: 'blob'
    });
  }
  getcontacts(){
    return this.http.get(`${this.url}getContacts`);
  }
  addcontact(name:string, phoneNumber:string){
    return this.http.get(`${this.url}addContact/${name}/${phoneNumber}`).subscribe();
  }
  deletecontact(id:number[]){
    return this.http.get(`${this.url}deleteContact/${id}`).subscribe();
  } 
  filter( sender:string, receiver:string, subject:string){
    return this.http.get(`${this.url}/${sender}/${receiver}/${subject}`);
  }
}
