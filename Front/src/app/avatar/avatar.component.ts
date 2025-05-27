// src/app/avatar/avatar.component.ts

import { Component, OnInit, OnDestroy, Inject, PLATFORM_ID } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { RouterModule } from '@angular/router';

declare var webkitSpeechRecognition: any; // Declaración para el reconocimiento de voz

@Component({
  selector: 'app-avatar',
  templateUrl: './avatar.component.html',
  styleUrls: ['./avatar.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class AvatarComponent implements OnInit, OnDestroy {
  btnMicrofono: HTMLButtonElement | null = null;
  textoTranscripcion: HTMLElement | null = null;
  avatar: HTMLElement | null = null;
  mouth: HTMLElement | null = null;

  recognition: any; // Instancia del reconocimiento de voz

  constructor(
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object // Para verificar si estamos en el navegador
  ) { }

  ngOnInit(): void {
    // Asegurarse de que el código se ejecute solo en el navegador
    if (isPlatformBrowser(this.platformId)) {
      // Obtener referencias a los elementos del DOM
      this.btnMicrofono = document.getElementById('btnMicrofono') as HTMLButtonElement;
      this.textoTranscripcion = document.getElementById('texto-transcripcion');
      this.avatar = document.querySelector('.avatar');
      this.mouth = document.querySelector('.mouth');

      this.avatarReposo(); // Poner el avatar en estado de reposo al inicio

      // Verificar si el reconocimiento de voz es compatible con el navegador
      if ('webkitSpeechRecognition' in window) {
        this.recognition = new webkitSpeechRecognition();
        
        // ****** ¡¡¡ ESTE ES EL CAMBIO CLAVE !!! ******
        this.recognition.continuous = true; // Permite que el micrófono permanezca abierto hasta que se detenga manualmente
        // ********************************************
        
        this.recognition.interimResults = true; // Mostrar resultados interinos
        this.recognition.lang = 'es-ES'; // Idioma español de España

        // Evento cuando el reconocimiento de voz comienza
        this.recognition.onstart = () => {
          if (this.btnMicrofono) {
            this.btnMicrofono.classList.add('escuchando'); // Añade la clase 'escuchando' al botón
          }
          if (this.textoTranscripcion) this.textoTranscripcion.textContent = "Escuchando... Habla ahora";
          this.avatarEscuchando(); // Poner el avatar en estado de escucha
        };

        // Evento cuando se reciben resultados de voz
        this.recognition.onresult = (event: any) => {
          let interimTranscript = '';
          let finalTranscript = '';

          // Recorrer los resultados para obtener la transcripción
          for (let i = event.resultIndex; i < event.results.length; i++) {
            const transcript = event.results[i][0].transcript;
            if (event.results[i].isFinal) {
              finalTranscript += transcript; // Transcripción final
            } else {
              interimTranscript += transcript; // Transcripción interina (mientras se habla)
            }
          }

          // Mostrar la transcripción en la pantalla
          if (this.textoTranscripcion) {
            // Si la transcripción final tiene contenido, lo muestra; si no, muestra la interina
            this.textoTranscripcion.innerHTML = finalTranscript || interimTranscript;
          }

          // Si hay una transcripción final y el micrófono sigue abierto, puedes decidir qué hacer.
          // Para que el usuario decida cuándo cerrar, NO detendremos el reconocimiento aquí automáticamente.
          // La lógica de procesar la consulta se hará con el texto final.
          if (finalTranscript) {
              // Si tienes lógica para procesar la consulta solo cuando se termina de hablar (por ejemplo, si finalTranscript es una frase completa)
              // deberías refinar cómo se procesa o cuándo se detiene.
              // Por ahora, solo muestra el texto y el usuario sigue controlando el micrófono.
              console.log("Transcripción final recibida (reconocimiento continuo):", finalTranscript);
          }
        };

        // Evento cuando ocurre un error en el reconocimiento de voz
        this.recognition.onerror = (event: any) => {
          console.error('Error del reconocimiento de voz:', event.error);
          if (this.textoTranscripcion) this.textoTranscripcion.textContent = "Error: " + event.error + ". Intenta nuevamente.";
          if (this.btnMicrofono) {
            this.btnMicrofono.classList.remove('escuchando'); // Quita la clase 'escuchando' si hay error
          }
          this.avatarReposo(); // Volver el avatar a reposo
        };

        // Evento cuando el reconocimiento de voz termina (por stop() manual o error grave)
        this.recognition.onend = () => {
          if (this.btnMicrofono) {
            this.btnMicrofono.classList.remove('escuchando'); // Quita la clase 'escuchando' cuando termina
          }
          // Aquí podríamos querer procesar la última transcripción final si el usuario detuvo manualmente.
          // Si el texto transcrito actual es un resultado final (y no solo interino), procesarlo.
          if (this.textoTranscripcion && this.textoTranscripcion.textContent && !this.textoTranscripcion.textContent.includes('Escuchando')) {
             const finalText = this.textoTranscripcion.textContent;
             // Si el texto final ya se procesó en onresult con la lógica de finalTranscript, no lo hagas de nuevo.
             // Si quieres que solo se procese AL DETENER, entonces la lógica de finalTranscript en onresult debería cambiar.
             // Para simplificar, la lógica de procesarConsulta se hará al obtener una transcripción final.
          }
          this.avatarReposo(); // El avatar vuelve a reposo cuando el micrófono se cierra.
        };

      } else {
        // Si el reconocimiento de voz no es compatible
        if (this.btnMicrofono) {
          this.btnMicrofono.disabled = true; // Deshabilitar el botón
        }
        if (this.textoTranscripcion) {
          this.textoTranscripcion.textContent = "El reconocimiento de voz no es compatible con tu navegador. Prueba con Chrome o Edge.";
        }
        this.avatarReposo();
      }
    }
  }

  // Limpiar el reconocimiento de voz al destruir el componente
  ngOnDestroy(): void {
    if (isPlatformBrowser(this.platformId) && this.recognition) {
      this.recognition.stop();
      this.recognition = null;
    }
  }

  // Método para alternar el estado del micrófono (INICIAR / DETENER)
  toggleMicrophone(): void {
    if (isPlatformBrowser(this.platformId) && this.btnMicrofono && this.recognition) {
        if (this.btnMicrofono.classList.contains('escuchando')) {
            // Si ya está escuchando (está rojo), lo detenemos.
            this.recognition.stop();
            // La clase 'escuchando' se quitará automáticamente en recognition.onend.
            // Aquí puedes llamar a procesarConsulta con el último texto final si no lo haces en onresult
            // O si quieres que la IA responda SOLO al detener.
            // Para tu caso actual, la respuesta se da después de 3 segundos en procesarConsulta.
        } else {
            // Si no está escuchando (está verde), lo iniciamos.
            this.recognition.start();
            // La clase 'escuchando' se añadirá automáticamente en recognition.onstart.
        }
    }
  }

  // Métodos para controlar la animación del avatar
  avatarHablando(): void {
    if (this.avatar) this.avatar.classList.remove('listening');
    if (this.mouth) {
        this.mouth.style.animation = 'talking 0.5s infinite';
        this.mouth.style.height = '10px';
        this.mouth.style.borderRadius = '20px';
    }
  }

  avatarEscuchando(): void {
    if (this.avatar) this.avatar.classList.add('listening');
    if (this.mouth) {
        this.mouth.style.animation = 'listening 1.5s infinite';
    }
  }

  avatarReposo(): void {
    if (this.avatar) this.avatar.classList.remove('listening');
    if (this.mouth) {
      this.mouth.style.animation = 'none';
      this.mouth.style.height = '10px';
      this.mouth.style.borderRadius = '20px';
    }
  }

  // Lógica para procesar la consulta de voz
  // NOTA: Con recognition.continuous = true, procesarConsulta se llamará
  // cuando finalTranscript tenga contenido (en onresult).
  // Si quieres que se procese SOLO cuando el usuario presiona para DETENER,
  // la lógica de `onresult` debería almacenar el `finalTranscript` y
  // `procesarConsulta` se llamaría dentro de `toggleMicrophone` cuando se detiene el micro,
  // o dentro de `onend`. Por ahora, lo dejaremos como está, procesando cada frase final.
  procesarConsulta(consulta: string): void {
    this.avatarHablando(); // El avatar "habla" mientras procesa

    const consultaLower = consulta.toLowerCase();
    let recomendacion = "";

    if (consultaLower.includes('matemáticas') || consultaLower.includes('números') || consultaLower.includes('software')) {
      recomendacion = "Ingeniería de Software, Matemáticas o Física";
    } else if (consultaLower.includes('medicina') || consultaLower.includes('salud') || consultaLower.includes('enfermería')) {
      recomendacion = "Medicina, Enfermería o Biología";
    } else if (consultaLower.includes('derecho') || consultaLower.includes('leyes') || consultaLower.includes('legal')) {
      recomendacion = "Derecho, Ciencias Políticas o Relaciones Internacionales";
    } else if (consultaLower.includes('industrial') || consultaLower.includes('procesos') || consultaLower.includes('producción')) {
      recomendacion = "Ingeniería Industrial, Administración o Logística";
    }
    else {
      recomendacion = "explorar todas nuestras carreras universitarias";
    }

    setTimeout(() => {
      if (this.textoTranscripcion) {
        this.textoTranscripcion.innerHTML += `<br><br><strong>Recomendación:</strong> Te sugerimos ${recomendacion}.`;
      }
      this.avatarReposo(); // El avatar vuelve a reposo después de responder
    }, 3000); // Simula un tiempo de respuesta de la IA
  }

  // Función para navegar de vuelta a la página de inicio
  goToHome(): void {
    this.router.navigate(['/home']);
  }
}