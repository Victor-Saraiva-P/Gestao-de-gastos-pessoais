import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AdminService } from './admin.service';
import { User } from '../entity/user';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-admin',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css'
})
export class AdminComponent implements OnInit{

  users: User[] = [];

  modalType: 'edit' | null = null;
  editingUserId: string | null = null;
  isEditing = false;

  private adminService = inject(AdminService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  editUserForm: FormGroup = this.fb.group({
    role: ['', Validators.required],
  });


  ngOnInit(): void {
    this.loadUsers();
  }

  async loadUsers() {
    const response = await this.adminService.getUsers();
    if (response) {
      this.users = response;
    }
  }

  refreshPage() {
    window.location.reload();
  }

  openModal(type: 'edit') {
    this.modalType = type;
  }

  closeModal() {
    this.modalType = null;
  }
  
  openEditModal(user: User) {
    this.modalType = 'edit';
    this.editingUserId = user.uuid!;
    this.editUserForm.setValue({
      role: user.role,
    });
  }
  
  toggleEditMode() {
    this.isEditing = !this.isEditing;
  }


  async onSubmitEditRole(id: string) {
    if(this.editUserForm.valid) {
      const role = this.editUserForm.value.role;
      
      this.adminService.changeUserRole(id, role).then(() => {
        alert('Papel do usuário alterado com sucesso!')
        this.refreshPage(); 
      }).catch((err) => alert('Erro ao alterar papel do usuário: ' + err));
    }
  }

  loadingStatus: { [key: string]: boolean } = {};
    async toggleUserStatus(user: User): Promise<void> {
      this.loadingStatus[user.uuid!] = true;
    
      const confirmation = confirm(
        `Deseja ${user.estaAtivo ? 'desativar' : 'ativar'} o usuário ${user.email}?`
      );
    
      if (confirmation) {
        try {
          const requestBody = {
            estaAtivo: !user.estaAtivo, 
            role: user.role
          };
    
          const success = await this.adminService.toggleUserStatus(user.uuid!, requestBody);
    
          if (success) {
            await this.loadUsers(); 
          }
        } catch (error) {
          console.error('Erro ao alterar status:', error);
        } finally {
          this.loadingStatus[user.uuid!] = false;
        }
      } else {
        this.loadingStatus[user.uuid!] = false;
      }
    }

isManagingStatus = false;
toggleStatusMode() {
  this.isManagingStatus = !this.isManagingStatus;
  if (this.isEditing) this.isEditing = false;
}  

  home() {
    this.router.navigate(['/home']);
  }

}
