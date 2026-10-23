const SPRING_URL = process.env.SPRING_URL;

export async function POST(request) {
  // 1. Leer y validar lo que mandó el navegador
  let pregunta = "";
  try {
    const body = await request.json();
    pregunta = typeof body.pregunta === "string" ? body.pregunta.trim() : "";
  } catch {
    // cuerpo inválido
  }
  if (!pregunta || pregunta.length > 500) {
    return Response.json({ error: "Escribe una pregunta de hasta 500 caracteres." },
                         { status: 400 });
  }

  // 2. Reenviar al microservicio de Spring (solo el servidor conoce esta URL)
  try {
    const url = `${SPRING_URL}/api/ia/rag?pregunta=${encodeURIComponent(pregunta)}`;
    const respuesta = await fetch(url, { cache: "no-store" });
    if (!respuesta.ok) {
      return Response.json({ error: "El asistente no pudo responder." }, { status: 502 });
    }
    const datos = await respuesta.json();

    // 3. Devolver al navegador solo lo que necesita
    return Response.json({ respuesta: datos.respuesta });
  } catch {
    return Response.json({ error: "El asistente no está disponible." }, { status: 503 });
  }
}