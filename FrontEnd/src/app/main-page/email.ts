export class email {
    id!: number;
    to!: string[];
    subject!:string;
    from!:string;
    body!:string;
    attachments!:string[];
    priority!:number;
    time!:Date;
    selected!:boolean;
    star!:boolean;
}
export interface backMail{
    id: number;
    sender:string;
    receiver: string[];
    emailSubject:string;
    emailBody:string;
    date:string;
    priority:number;
}
export interface responce{
    result:string;
}