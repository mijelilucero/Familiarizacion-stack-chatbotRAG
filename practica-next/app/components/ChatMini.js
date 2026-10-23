"use client";

import { useState } from "react";

export default function ChatMini() {
  const [texto, setTexto] = useState("");
  const [cargando, setCargando] = useState(false);
  const [mensajes, setMensajes] = useState([
    { rol: "bot", texto: "¡Hola! Pregúntame sobre tarifas o fugas." },
  ]);

  async function enviar(e) {
    e.preventDefault();
    const pregunta = texto.trim();
    if (!pregunta || cargando) return;

    setMensajes((m) => [...m, { rol: "usuario", texto: pregunta }]);
    setTexto("");
    setCargando(true);

    try {
      const res = await fetch("/api/chat", {           // al BFF, mismo origen
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ pregunta }),
      });
      const datos = await res.json();
      const respuesta = res.ok ? datos.respuesta : datos.error;
      setMensajes((m) => [...m, { rol: "bot", texto: respuesta }]);
    } catch {
      setMensajes((m) => [...m, { rol: "bot", texto: "No pude conectarme." }]);
    } finally {
      setCargando(false);
    }
  }

  return (
    <div style={{ maxWidth: 500, margin: "40px auto", fontFamily: "sans-serif" }}>
      <h2>Asistente de la Oficina del Agua</h2>

      <div style={{ border: "1px solid #ccc", borderRadius: 8, padding: 12, minHeight: 200 }}>
        {mensajes.map((m, i) => (
          <p key={i} style={{ textAlign: m.rol === "usuario" ? "right" : "left" }}>
            <strong>{m.rol === "usuario" ? "Tú" : "Asistente"}:</strong> {m.texto}
          </p>
        ))}
        {cargando && <p><em>Escribiendo...</em></p>}
      </div>

      <form onSubmit={enviar} style={{ display: "flex", gap: 8, marginTop: 8 }}>
        <input
          value={texto}
          onChange={(e) => setTexto(e.target.value)}
          maxLength={500}
          placeholder="Escribe tu pregunta"
          style={{ flex: 1, padding: 8 }}
        />
        <button type="submit" disabled={cargando}>Enviar</button>
      </form>
    </div>
  );
}