// src/app/home/home.component.ts

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router'; // Importa el servicio Router para la navegación
import { CommonModule } from '@angular/common'; // Importa CommonModule para directivas como ngIf, ngFor (aunque no se usan aquí, es buena práctica)
import { RouterModule } from '@angular/router'; // Importa RouterModule para usar routerLink

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  standalone: true, // Declara el componente como Standalone
  imports: [CommonModule, RouterModule] // Lista los módulos necesarios para el template
})
export class HomeComponent implements OnInit {

  // Inyecta el servicio Router en el constructor
  constructor(private router: Router) { } 

  ngOnInit(): void {
    // Lógica de inicialización del componente, si es necesaria
  }

  // Método para navegar al componente Avatar
  navigateToAvatar(): void {
    this.router.navigate(['/avatar']); // Navega programáticamente a la ruta '/avatar'
  }
}