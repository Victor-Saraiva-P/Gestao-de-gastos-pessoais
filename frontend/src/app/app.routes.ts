import { Routes } from "@angular/router";
import { RegisterComponent } from "./auth/register/register.component";
import { LoginComponent } from "./auth/login/login.component";


const routeConfig: Routes = [
    { 
        path: 'register', component: RegisterComponent, 
        title: 'Cadastro' 
    },
    { 
        path: 'login', component: LoginComponent, 
        title: 'Login' 
    },
]; 

export default routeConfig;