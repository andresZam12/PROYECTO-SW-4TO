// src/app/home/home.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Client } from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class HomeComponent implements OnInit, OnDestroy {
  private stompClient: Client;
  private mediaRecorder: MediaRecorder | null = null;
  private audioChunks: Blob[] = [];
  isRecording = false;
  voiceStatus = 'Presiona el botón para hablar con Elliot';
  isSocketConnected = false;

  constructor(private router: Router) {
    // Configuración inicial del cliente STOMP
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/voice-ws'),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000
    });
  }

  ngOnInit(): void {
    this.connectWebSocket();
  }

  ngOnDestroy(): void {
    this.disconnectWebSocket();
    this.stopRecording();
  }

  private connectWebSocket(): void {
    this.stompClient.onConnect = () => {
      this.isSocketConnected = true;
      console.log('Conexión WebSocket establecida');
      
      this.stompClient.subscribe('/user/queue/voice-response', (message: any) => {
        this.handleVoiceResponse(message);
      });
    };

    this.stompClient.onStompError = (error) => {
      console.error('Error en WebSocket:', error);
      this.voiceStatus = 'Error de conexión con el servidor';
      this.isSocketConnected = false;
    };

    this.stompClient.onDisconnect = () => {
      this.isSocketConnected = false;
    };

    this.stompClient.activate();
  }

  private disconnectWebSocket(): void {
    if (this.stompClient && this.stompClient.active) {
      this.stompClient.deactivate();
    }
  }

  async toggleRecording(): Promise<void> {
    if (this.isRecording) {
      this.stopRecording();
    } else {
      await this.startRecording();
    }
  }

  private async startRecording(): Promise<void> {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      this.mediaRecorder = new MediaRecorder(stream, {
        mimeType: 'audio/webm; codecs=opus',
        audioBitsPerSecond: 16000
      });

      this.mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          this.audioChunks.push(event.data);
        }
      };

      this.mediaRecorder.onstop = () => {
        if (this.audioChunks.length > 0) {
          this.sendAudioToServer();
          this.voiceStatus = 'Procesando tu pregunta...';
        }
      };

      this.mediaRecorder.start(1000);
      this.isRecording = true;
      this.voiceStatus = 'Escuchando... habla ahora';
    } catch (error) {
      console.error('Error al acceder al micrófono:', error);
      this.voiceStatus = 'Error al acceder al micrófono. Por favor, verifica los permisos.';
      this.isRecording = false;
    }
  }

  private stopRecording(): void {
    if (this.mediaRecorder && this.mediaRecorder.state !== 'inactive') {
      this.mediaRecorder.stop();
      this.mediaRecorder.stream.getTracks().forEach(track => track.stop());
    }
    this.isRecording = false;
    this.voiceStatus = 'Presiona el botón para hablar con Elliot';
  }

  private sendAudioToServer(): void {
    if (!this.isSocketConnected) {
      this.voiceStatus = 'No conectado al servidor. Intenta recargar la página.';
      return;
    }

    const audioBlob = new Blob(this.audioChunks, { type: 'audio/webm' });
    const reader = new FileReader();

    reader.onload = () => {
      const audioData = new Uint8Array(reader.result as ArrayBuffer);
      this.stompClient.publish({
        destination: '/app/voice/ask',
        body: audioData,
        binary: true
      });
      this.audioChunks = [];
    };

    reader.onerror = () => {
      this.voiceStatus = 'Error al procesar el audio';
    };

    reader.readAsArrayBuffer(audioBlob);
  }

  private handleVoiceResponse(message: any): void {
    try {
      const audioBlob = new Blob([message.body], { type: 'audio/webm' });
      const audioUrl = URL.createObjectURL(audioBlob);
      const audioElement = new Audio(audioUrl);
      
      audioElement.play();
      this.voiceStatus = 'Elliot está respondiendo...';
      
      audioElement.onended = () => {
        this.voiceStatus = 'Presiona el botón para hablar con Elliot';
        URL.revokeObjectURL(audioUrl);
      };
    } catch (error) {
      console.error('Error al reproducir la respuesta:', error);
      this.voiceStatus = 'Error al reproducir la respuesta';
    }
  }

  navigateToAvatar(): void {
    this.router.navigate(['/avatar']);
  }
}