import { Routes } from "@angular/router";
import { RegisterComponent } from "./auth/register/register.component";
import { LoginComponent } from "./auth/login/login.component";
import { HomeComponent } from "./home/home.component";
import { UnauthorizedComponent } from "./auth/unauthorized/unauthorized.component";
import { authGuard } from "./auth/auth.guard";
import { IncomeComponent } from "./home/income/income.component";
import { RootComponent } from "./root/root.component";
import { ExpenseComponent } from "./home/expense/expense.component";
import { AdminComponent } from "./admin/admin.component";
import { CostTargetsComponent } from "./home/cost-targets/cost-targets.component";
import { CustomCategoryExpenseComponent } from "./home/custom-category/expense/custom-category-expense.component";
import { CustomCategoryIncomeComponent } from "./home/custom-category/income/custom-category-income.component";


const routeConfig: Routes = [
    { 
        path: '', component: RootComponent, 
        title: 'Início' 
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
        path: 'home/admin', component: AdminComponent, 
        canActivate: [authGuard],
        data: {role: 'ADMIN'},
        title: 'Admin' 
    },
    {
        path: 'unauthorized', component: UnauthorizedComponent, title: 'Unauthorized'
    },
    { 
        path: 'home/income', component: IncomeComponent, 
        canActivate: [authGuard],
        title: 'Receitas' 
    },
    { 
        path: 'home/expense', component: ExpenseComponent, 
        canActivate: [authGuard],
        title: 'Despesas' 
    },
    { 
        path: 'home/expense/cost-targets', component: CostTargetsComponent, 
        canActivate: [authGuard],
        title: 'Metas de despesas' 
    },
    { 
        path: 'home/expense/custom-category', component: CustomCategoryExpenseComponent, 
        canActivate: [authGuard],
        title: 'Categorias de despesas' 
    },
    { 
        path: 'home/income/custom-category', component: CustomCategoryIncomeComponent, 
        canActivate: [authGuard],
        title: 'Categorias de receitas' 
    },
]; 

export default routeConfig;