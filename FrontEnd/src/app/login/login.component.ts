import { Component, OnInit } from '@angular/core';
import { ActivatedRoute,Router } from '@angular/router';
import { FormControl,FormGroup } from '@angular/forms';
import { LoginService } from '../services/login.service';
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm = new FormGroup({
    email: new FormControl(''),
    password: new FormControl(''),
  });
  signupForm = new FormGroup({
    email: new FormControl(''),
    password: new FormControl(''),
  });
  constructor(private auth: LoginService, private router: Router) {}

  ngOnInit(): void {
  }
  login(): void {
    if (this.loginForm.valid) {
      var result:string;
      this.auth.login(this.loginForm.value).subscribe((data)=>{
        console.log(data);
        result = data.toString();
        console.log("Final " + result);
        if(result == 'yes'){
          console.log("True login");
          this.router.navigate(['/home']);
        }
        else if(result == 'denied'){
          alert("There is an Email runnig");
        }
        else{
          alert("Wrong mail or password");
        }
      })
    }
  }
  signup():void{
    if(!(this.signupForm.value.email)?.includes("@gmail.com")){
      alert("Email must end with @gmail.com");
      return;
    }
    const passwordLength = this.signupForm.value.password?.length;
    if(passwordLength != undefined){
      if(passwordLength <= 8){
        alert("Password length must be at least 8 characters");
        return;
      }
    }
    if (this.signupForm.valid) {
      var result:string;
      this.auth.signup(this.signupForm.value).subscribe((data)=>{
        console.log(data);
        result = data.toString();
        console.log("Final " + result);
        if(result == 'yes'){
          alert("Signed up successfully");
        }
        else{
          alert("Email is already registered");
        }
      })
    }
  }
}
