
#include <Cubo.h>
/// LIBS requeridas por el shield
#include <SPI.h>
#include <Ethernet.h>

//////////////////////////////////
/// LIBS JSON
#include <ArduinoJson.h>
//////////////////////////////////

/// DIRECCION FISICA
byte mac[] = {0x98, 0x4F, 0xEE, 0x01, 0x11, 0x90 };
/// DIRECCION IP
IPAddress ip(192, 168, 1, 50);
/// PUERTO DE ESCUCHA
EthernetServer server(8080);
/// SOCK CLIENTE
EthernetClient client;
/// TAMANIO MAX MENSAJE
const size_t MAX_BODY_SIZE = 200;

String mensaje = "";


const int fwdPin=52;
const int revPin=53;
const int pasos=200;
const int steps=100;
const int grados120=100;
const int grados90=50;
const int grados45=25;
const int tiempoLectora=10;
const int pausa=5000;

DRV8825 arriba(40,39,28);
DRV8825 abajo(45,44,42);
DRV8825 lateral(50,49,47);

Cubo cubo(arriba, abajo, lateral/*, fwdPin, revPin, pasos, steps, grados120, grados90, grados45, tiempoLectora, pausa*/);

void setup() {
  Serial.begin(9600);
  pinMode(3, OUTPUT);
  Ethernet.begin(mac, ip);
  server.begin();
  delay(100);
}



void loop()
{
  client = server.available();
  if (client)
  {
    boolean currentLineIsBlank = true;
    mensaje = "";
    while (client.connected())
    {
      if (client.available())
      {
        char c = client.read();
        mensaje += c;
        if (c == '\n' && currentLineIsBlank && mensaje.startsWith("POST"))
        {
          analizarPost();
          break;
        }
        if (c == '\n')
        {
          currentLineIsBlank = true;
        }
        else if (c != '\r')
        {
          currentLineIsBlank = false;
        }
      }
    }
    // delay(1);
    client.stop();
  }
}
bool analizarPost()
{
  char body_post[MAX_BODY_SIZE] = "";
  char cara[2] = "";
  String respuesta_aux = mensaje.substring(mensaje.indexOf("/led/"));
  int num_pin = '3';
  if (leerBodyPost(mensaje, body_post) == false)
  {
    Serial.println("Error al leer el campo Body");
    enviarHttpResponse_BadRequest(client);
    return false;
  }
  if (num_pin == '3')
  {
    if (parserBodyPost(body_post, cara) == false)
    {
      Serial.println("Error al realizar el parser");
      enviarHttpResponse_BadRequest(client);
      return false;
    }
    else
    {
      cubo.Mover(cara[0]);
      enviarHttpResponse_OK(client);
      return true;
    }

  }
  Serial.println("Numero de Pin Incorrecto..");
  enviarHttpResponse_BadRequest(client);

  return false;
}

//RECUPERA DEL MENSAJE EL CUERPO, OSEA ELIMINA LA CABECERA Y SE QUEDA SOLO CON EL CONTENIDO
/*
 * EJEMPLO: Si hicimos un post para enviar el mensaje que indica mover una cara, al recuperar lo recibido obtenemos el siguiente paquete
 * *****************************************************************************
 * Cliente Conectado...
 * POST / HTTP/1.1
 * Content-Type: application/json;charset=UTF-8
 * Accept: application/json
 * User-Agent: Dalvik/2.1.0 (Linux; U; Android 6.0.1; LG-H850 Build/MMB29M)
 * Host: 192.168.1.50:8080
 * Connection: Keep-Alive
 * Accept-Encoding: gzip
 * Content-Length: 11
 * **************************MENSAJE JSON***************************************
 * {"MOV":"B"}
 * *****************************************************************************
 * Notar que lo ultimo es el JSON que nos interesa ({"MOV":"B"}), su longitud esta indicada por Content-Length
 */
bool leerBodyPost(String respuesta, char * body_post)
{
  int longAreaDatos = -1;
  int indexCaracterRecup = 0;

  //RECUPERAR=> Cuanto mide el mensaje sin la cabecera.
  String body_tam = respuesta.substring(respuesta.indexOf("Content-Length:") + 15); //15 es la longitud de "Content-Length:"
  longAreaDatos = body_tam.toInt(); // Convertimos en entero la logitud recuperada
  
  if (longAreaDatos > MAX_BODY_SIZE) // Verificamos que no se supere el tamanio maximo del mensaje comprometido para leer
    return false;

  // RECUPERAMOS EL MENSAJE JSON EN EL ARREGLO BODY_POST CARACTER A CARACTER. NOTAR QUE AUN NO SE RECUPERO DEL SOCK por eso el CLient.read();
  while (indexCaracterRecup < longAreaDatos)
  {
    body_post[indexCaracterRecup] = client.read(); //Leer un caracter y mandarlo al arreglo
    indexCaracterRecup++; // avanzamos una posicion en el arreglo
  }
  
  return true;
}

// ANALIZAMOS EL Cuerpo del POST, precisamente en este caso el JSON.
bool parserBodyPost(char * content, char * cara)
{
  const size_t BUFFER_SIZE = JSON_OBJECT_SIZE(1);
  DynamicJsonBuffer jsonBuffer(BUFFER_SIZE);
  JsonObject& root = jsonBuffer.parseObject(content);
  if (!root.success())
    return false;

  ////////////////////////////////////
  ///ACONTINUACION POR KEY ACCEDEMOS AL VALOR
  strcpy(cara, root["MOV"]);
  ////////////////////////////////////
  
  return true;
}


void enviarHttpResponse_OK(EthernetClient& client)
{
  client.println("HTTP/1.1 200 OK");
  //client.println("Content-Type: application/json");
  client.println("Connection: close");
  client.println();
}

void enviarHttpResponse_BadRequest(EthernetClient& client)
{
  client.println("HTTP/1.1 400 BAD REQUEST");
  client.println("Content-Type: text/html");
  client.println("Connection: close");
  client.println();
  client.println("<!DOCTYPE HTML>");
  client.println("<html> <body>");
  client.println("BAD REQUEST");
  client.println("</body> </html>");
  client.println();
}

