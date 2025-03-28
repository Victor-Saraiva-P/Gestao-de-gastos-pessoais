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


  async onSubmitEdit(id: string) {
      
  }
  
  
  home() {
    this.router.navigate(['/home']);
  }

}
