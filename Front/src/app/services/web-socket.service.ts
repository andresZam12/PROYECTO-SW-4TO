import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private socket!: WebSocket;
  private messageSubject = new Subject<any>();
  private connectionStatusSubject = new Subject<string>();
  public isConnected = false;

  connect(url: string): void {
    this.socket = new WebSocket(url);
    this.connectionStatusSubject.next('Conectando...');

    this.socket.onopen = () => {
      this.isConnected = true;
      this.connectionStatusSubject.next('Conectado');
    };

    this.socket.onmessage = (event) => {
      try {
        const message = JSON.parse(event.data);
        this.messageSubject.next(message);
      } catch (error) {
        console.error('Error parsing message:', error);
        this.messageSubject.next({
          type: 'error',
          error: 'Invalid message format'
        });
      }
    };

    this.socket.onerror = (error: Event) => {
      console.error('WebSocket error:', error);
      this.isConnected = false;
      this.connectionStatusSubject.next('Error de conexión');
    };

    this.socket.onclose = () => {
      this.isConnected = false;
      this.connectionStatusSubject.next('Desconectado');
    };
  }

  send(data: Blob | string): void {
    if (this.socket.readyState === WebSocket.OPEN) {
      if (data instanceof Blob) {
        this.socket.send(data);
      } else {
        this.socket.send(JSON.stringify({ text: data }));
      }
    } else {
      console.error('WebSocket not connected');
      this.messageSubject.next({
        type: 'error',
        error: 'Not connected'
      });
    }
  }

  getMessages(): Observable<any> {
    return this.messageSubject.asObservable();
  }

  getConnectionStatus(): Observable<string> {
    return this.connectionStatusSubject.asObservable();
  }

  disconnect(): void {
    if (this.socket) {
      this.socket.close();
    }
  }
}