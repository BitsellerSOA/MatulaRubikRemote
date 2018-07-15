
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


const int inputFinCarrera = 22;  // entrada del din de carrera: HIGH significa que esta apretado

const int dirArr = 26;        // MOTOR ARRIBA
const int stepArr = 25;
const int enblArr = 24;

const int dirAbajo = 31;      // MOTOR ABAJO
const int stepAbajo = 30;
const int enblAbajo = 28;

const int dirCost = 36;       // MOTOR DE COSTADO
const int stepCost = 35;
const int enblCost = 33;

const int fwdPin = 39;        //nivel logico de salida para el puente-H(HIGH = adelate)
const int revPin = 38;         //nivel logico de salida para el puente-H(LOW = adelante)

const int pasos = 200;        //cantidad de pasos por vuelta del motor
const int grados180 = 100;    //cantidad de pasos necesarios para girar 180 grados
const int grados90 = 50;      //cantidad de pasos necesarios para girar 90 grados
const int grados45 = 25;      //cantidad de pasos necesarios para girar 45 grados
const int tiempoLectora = 170;    // tiempo que tarda la lectora en retroceder
const int pausa=5000;         // tiempo del pulso del paso a paso
int value;

/*
DRV8825 arriba(26,25,24);
DRV8825 abajo(31,30,28);
DRV8825 lateral(36,35,33);
*/
Cubo cubo( dirArr,     stepArr,    enblArr,
           dirAbajo,   stepAbajo,  enblAbajo,
           dirCost,    stepCost,   enblCost,
            fwdPin, revPin, inputFinCarrera, pasos, grados180, grados90, grados45, tiempoLectora, pausa);

void setup() {
  pinMode(inputFinCarrera, INPUT);
  
  pinMode(dirArr, OUTPUT);
  pinMode(stepArr, OUTPUT);
  
  pinMode(dirAbajo, OUTPUT);
  pinMode(stepAbajo, OUTPUT);
  
  pinMode(dirCost, OUTPUT);
  pinMode(stepCost, OUTPUT);
  
  pinMode(fwdPin, OUTPUT); 
  pinMode(revPin, OUTPUT); 

  // pongo la lectora en low por si las dudas para que no arranque
  digitalWrite(fwdPin, LOW);  
  digitalWrite(revPin, LOW);
  
  Serial.begin(9600);
//  pinMode(3, OUTPUT);
  Ethernet.begin(mac, ip);
  server.begin();
  delay(100);

/*
  // esto es por si la lectora no esta toda para atraz cuando epieza el programa
  value = digitalRead(inputFinCarrera);
  while( value == LOW ){    
     value = digitalRead(inputFinCarrera);
  }

  digitalWrite(revPin, LOW);
  digitalWrite(fwdPin, HIGH);
  
  delay(tiempoLectora);
  
  digitalWrite(revPin, LOW);
  digitalWrite(fwdPin, LOW);
  */
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
  if (leerBodyPost(mensaje, body_post) == false)
  {
    Serial.println("Error al leer el campo Body");
    enviarHttpResponse_BadRequest(client);
    return false;
  }
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

  Serial.println("Numero de Pin Incorrecto..");
  enviarHttpResponse_BadRequest(client);

  return false;
}

//RECUPERA DEL MENSAJE EL CUERPO, OSEA ELIMINA LA CABECERA Y SE QUEDA SOLO CON EL CONTENIDO
/*
 * EJEMPLO: Si hicimos un post para enviar el mensaje que indica mover una cara, al recuperar lo recibido obtenemos el siguiente paquete
 * *****************************************************************************
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

