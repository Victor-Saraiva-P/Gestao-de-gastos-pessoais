import { Routes } from "@angular/router";
import { RegisterComponent } from "./auth/register/register.component";


const routeConfig: Routes = [
    { 
        path: 'register', component: RegisterComponent, 
        title: 'Cadastro' 
    },
]; 

export default routeConfig;