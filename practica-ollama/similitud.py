import json, math, urllib.request

def embedding(texto):
    datos = json.dumps({"model": "bge-m3", "input": texto}).encode()
    req = urllib.request.Request("http://localhost:11434/api/embed", data=datos,
                                 headers={"Content-Type": "application/json"})
    return json.load(urllib.request.urlopen(req))["embeddings"][0]

def similitud(a, b):
    punto = sum(x * y for x, y in zip(a, b))
    return punto / (math.sqrt(sum(x * x for x in a)) * math.sqrt(sum(y * y for y in b)))

pregunta = embedding("¿Cuánto cuesta el agua?")
frases = [
    "Tarifa vigente del servicio 1/4 de paja: Q25.00 por periodo",
    "Cierra la llave de paso si tienes una fuga",
    "El partido de fútbol terminó empatado",
]
for f in frases:
    print(f"{similitud(pregunta, embedding(f)):.3f}  {f}")