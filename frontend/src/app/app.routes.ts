import { Routes } from "@angular/router";
import { RegisterComponent } from "./auth/register/register.component";
import { LoginComponent } from "./auth/login/login.component";
import { HomeComponent } from "./home/home.component";


const routeConfig: Routes = [
    { 
        path: 'register', component: RegisterComponent, 
        title: 'Cadastro' 
    },
    { 
        path: 'login', component: LoginComponent, 
        title: 'Login' 
    },
    { 
        path: 'home', component: HomeComponent, 
        title: 'Home' 
    },
]; 

export default routeConfig;