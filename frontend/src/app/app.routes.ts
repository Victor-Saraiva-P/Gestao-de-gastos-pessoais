import { Routes } from "@angular/router";
import { RegisterComponent } from "./auth/register/register.component";
import { LoginComponent } from "./auth/login/login.component";
import { HomeComponent } from "./home/home.component";
import { UnauthorizedComponent } from "./auth/unauthorized/unauthorized.component";
import { authGuard } from "./auth/auth.guard";
import { IncomeComponent } from "./home/income/income.component";
import { AppComponent } from "./app.component";
import { RootComponent } from "./root/root.component";


const routeConfig: Routes = [
    { 
        path: '', component: RootComponent, 
        title: 'root' 
    },
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
        canActivate: [authGuard],
        title: 'Home' 
    },
    { 
        path: 'home/admin', component: HomeComponent, 
        canActivate: [authGuard],
        data: {role: 'ADMIN'},
        title: 'Teste filtro de role' 
    },
    {
        path: 'unauthorized', component: UnauthorizedComponent, title: 'Unauthorized'
    },
]; 

export default routeConfig;