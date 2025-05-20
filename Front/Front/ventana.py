import tkinter as tk

ventana = tk.Tk()

ventana.title("")
ventana.geometry("500x200")
boton = tk.Button(ventana, text= "agregar")
boton.pack()


ventana.mainloop()