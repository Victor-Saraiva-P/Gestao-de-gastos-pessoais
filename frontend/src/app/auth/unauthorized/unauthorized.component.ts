import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-unauthorized',
  standalone: true,
  imports: [CommonModule],
  template: `
    <p>
      Seu usuário não tem permissão para acessar essa página.
    </p>
  `,
  styleUrls: ['./unauthorized.component.css']
})
export class UnauthorizedComponent {

}