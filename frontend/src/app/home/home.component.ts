import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  template: `
    <section>
     <h2>Usuario logado</h2>
    </section>
  `,
  styleUrls: ['./home.component.css']
})
export class HomeComponent {

}
