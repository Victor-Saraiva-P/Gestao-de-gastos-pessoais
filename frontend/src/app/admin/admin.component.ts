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
  editingUser: User | null = null;
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
    this.editingUser = user;
    this.editUserForm.setValue({
      role: user.role,
    });
  }
  
  toggleEditMode() {
    this.isEditing = !this.isEditing;
  }


  async onSubmitEditRole(user: User) {
    if(this.editUserForm.valid) {
      const role = this.editUserForm.value.role;
      
      this.adminService.changeUserRole(user, role).then(() => {
        alert('Papel do usuário alterado com sucesso!')
        this.refreshPage(); 
      }).catch((err) => alert('Erro ao alterar papel do usuário: ' + err));
    }
  }

  
  home() {
    this.router.navigate(['/home']);
  }

}
