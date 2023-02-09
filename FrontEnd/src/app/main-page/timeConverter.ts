export class TimeConverter{
    convertIntoTime(input:string):Date{
        return new Date(input);
    }
    convertIntoString(input:Date):string{
        return input.toDateString();
    }
}