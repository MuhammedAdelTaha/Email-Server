import { email } from "./email";
import { TimeConverter } from "./timeConverter";
export class MailFacade {
    to!:string;
    from!:string;
    body!:string;
    subject!:string;
    attachments!:any[];
    time!:string;
    date!:Date;
    converter!:TimeConverter;
    priority!:number;
    id!:number;
    constructor(to?: string, from?: string, body?: string, subject?: string,priority?:number,id?:number) {
        this.to = to || '';
        this.from = from || '';
        this.body = body || '';
        this.subject = subject || '';
        this.priority = priority || 1;
        this.converter = new TimeConverter();
        this.date = new Date();
        this.time = this.date.toDateString();
        this.id = id || 1;
        console.log("Facade created");
    }
    getFrontMail():any{
        return {
            id: this.id,
            to: this.to,
            subject:this.subject,
            from:this.from,
            body: this.body,
            attachments:[],
            priority: this.priority,
            time:this.date,
            selected: false,
            star: false,
        }
    }
    getBackMail():any{
        return {
            receivers:this.to.split(' ',10),
            subject:this.subject,
            body:this.body,
            time:this.date,
            priority:this.priority
        }
    }
}