import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AdminService } from './admin.service';
import { User } from '../entity/user';

@Component({
  selector: 'app-admin',
  imports: [CommonModule],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css'
})
export class AdminComponent implements OnInit{

  users: User[] = [];

  private adminService = inject(AdminService);
  private router = inject(Router);
  isEditing = false;


  ngOnInit(): void {
    this.loadUsers();
  }

  async loadUsers() {
    const response = await this.adminService.getUsers();
    if (response) {
      this.users = response;
    }
  }

  toggleEditMode() {
    this.isEditing = !this.isEditing;
  }
  
  
  home() {
    this.router.navigate(['/home']);
  }

}
