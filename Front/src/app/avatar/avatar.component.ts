import { Component, OnInit, OnDestroy, Inject, PLATFORM_ID } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { RouterModule } from '@angular/router';
import { WebSocketService } from '../services/web-socket.service';
import { Subscription } from 'rxjs';

interface SocketMessage {
  type: string;
  text?: string;
  audio?: string;
  error?: string;
}

@Component({
  selector: 'app-avatar',
  templateUrl: './avatar.component.html',
  styleUrls: ['./avatar.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class AvatarComponent implements OnInit, OnDestroy {
  // Elementos del DOM
  btnMicrofono: HTMLButtonElement | null = null;
  textoTranscripcion: HTMLElement | null = null;
  avatar: HTMLElement | null = null;
  mouth: HTMLElement | null = null;

  // Propiedades públicas para la plantilla
  isRecording = false;
  isProcessing = false;
  socketStatus = 'Desconectado';

  // Variables privadas para la lógica
  private mediaRecorder: MediaRecorder | null = null;
  private audioChunks: Blob[] = [];
  private audioContext: AudioContext | null = null;
  private messageSubscription!: Subscription;
  private statusSubscription!: Subscription;

  constructor(
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
    private webSocketService: WebSocketService
  ) { }

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.initElements();
      this.initWebSocket();
    }
  }

  private initElements(): void {
    this.btnMicrofono = document.getElementById('btnMicrofono') as HTMLButtonElement;
    this.textoTranscripcion = document.getElementById('texto-transcripcion');
    this.avatar = document.querySelector('.avatar');
    this.mouth = document.querySelector('.mouth');
    this.avatarReposo();
  }

  private initWebSocket(): void {
    this.webSocketService.connect('wss://tu-backend-api.com/voice-assistant');

    this.messageSubscription = this.webSocketService.getMessages().subscribe({
      next: (message: SocketMessage) => this.handleSocketMessage(message),
      error: (err: any) => console.error('Error en WebSocket:', err)
    });

    this.statusSubscription = this.webSocketService.getConnectionStatus().subscribe({
      next: (status: string) => this.socketStatus = status,
      error: (err: any) => console.error('Error en estado:', err)
    });
  }

  private handleSocketMessage(message: SocketMessage): void {
    switch (message.type) {
      case 'transcription':
        if (message.text) this.updateTranscription(message.text);
        break;
      case 'response':
        if (message.text && message.audio) this.handleResponse(message.text, message.audio);
        break;
      case 'error':
        if (message.error) this.handleError(message.error);
        break;
      default:
        console.warn('Tipo de mensaje desconocido:', message);
    }
  }

  private updateTranscription(text: string): void {
    if (this.textoTranscripcion) {
      this.textoTranscripcion.textContent = text;
    }
  }

  private handleResponse(text: string, audioBase64: string): void {
    if (this.textoTranscripcion) {
      this.textoTranscripcion.innerHTML = text;
    }
    
    this.avatarHablando();
    this.playAudioResponse(audioBase64);
  }

  private playAudioResponse(audioBase64: string): void {
    if (!this.audioContext) {
      this.audioContext = new AudioContext();
    }

    const audioData = this.base64ToArrayBuffer(audioBase64);
    this.audioContext.decodeAudioData(audioData).then((buffer) => {
      const source = this.audioContext!.createBufferSource();
      source.buffer = buffer;
      source.connect(this.audioContext!.destination);
      source.start(0);
      
      source.onended = () => {
        this.avatarReposo();
        this.isProcessing = false;
      };
    }).catch((error: any) => {
      console.error('Error al reproducir audio:', error);
      this.avatarReposo();
      this.isProcessing = false;
    });
  }

  private base64ToArrayBuffer(base64: string): ArrayBuffer {
    const binaryString = atob(base64);
    const len = binaryString.length;
    const bytes = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
      bytes[i] = binaryString.charCodeAt(i);
    }
    return bytes.buffer;
  }

  private handleError(error: string): void {
    console.error('Error del servidor:', error);
    if (this.textoTranscripcion) {
      this.textoTranscripcion.textContent = `Error: ${error}`;
    }
    this.isProcessing = false;
    this.avatarReposo();
  }

  async toggleMicrophone(): Promise<void> {
    if (this.isRecording) {
      this.stopRecording();
    } else {
      await this.startRecording();
    }
  }

  private async startRecording(): Promise<void> {
    try {
      this.audioChunks = [];
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      
      this.mediaRecorder = new MediaRecorder(stream, {
        mimeType: 'audio/webm;codecs=opus'
      });

      this.mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          this.audioChunks.push(event.data);
        }
      };

      this.mediaRecorder.onstop = () => {
        this.sendAudioToBackend();
        stream.getTracks().forEach(track => track.stop());
      };

      this.mediaRecorder.start(1000);
      this.isRecording = true;
      this.isProcessing = true;
      this.avatarEscuchando();
      
      if (this.btnMicrofono) {
        this.btnMicrofono.classList.add('escuchando');
      }
      if (this.textoTranscripcion) {
        this.textoTranscripcion.textContent = "Escuchando... Habla ahora";
      }
    } catch (error: any) {
      console.error('Error al acceder al micrófono:', error);
      if (this.textoTranscripcion) {
        this.textoTranscripcion.textContent = "Error al acceder al micrófono. Asegúrate de permitir el acceso.";
      }
      this.isProcessing = false;
    }
  }

  private stopRecording(): void {
    if (this.mediaRecorder && this.isRecording) {
      this.mediaRecorder.stop();
      this.isRecording = false;
      
      if (this.btnMicrofono) {
        this.btnMicrofono.classList.remove('escuchando');
      }
    }
  }

  private sendAudioToBackend(): void {
    const audioBlob = new Blob(this.audioChunks, { type: 'audio/webm;codecs=opus' });
    this.webSocketService.send(audioBlob);
  }

  procesarConsulta(consulta: string): void {
    this.isProcessing = true;
    this.avatarHablando();
    
    if (this.textoTranscripcion) {
      this.textoTranscripcion.textContent = "Procesando tu pregunta...";
    }

    this.webSocketService.send(consulta);
  }

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

  goToHome(): void {
    this.router.navigate(['/home']);
  }

  ngOnDestroy(): void {
    if (this.messageSubscription) this.messageSubscription.unsubscribe();
    if (this.statusSubscription) this.statusSubscription.unsubscribe();
    if (this.mediaRecorder && this.isRecording) {
      this.stopRecording();
    }
    this.webSocketService.disconnect();
  }
}