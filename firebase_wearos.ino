//Adecuar para el tipo de Tarjeta 
#include <ESP8266WiFi.h>
/////////////////////////////////
#include "DHT.h"

#include <Firebase_ESP_Client.h>
#include "addons/TokenHelper.h"


//1.- Conexión a Internet 
#define WIFI_SSID "IZZI-84F0"
#define WIFI_PASSWORD "2WC468400115"


//2.- API KEY DE FIREBASE 
#define API_KEY "AIzaSyBGuKRrhR72yXaFEDkZsbGuZEB0Ge9Kdlg"
//3.- URL DE LA BASE DE DATOS 
#define DATABASE_URL "esp8266-demo-9c927-default-rtdb.firebaseio.com/"
//5.- USUARIO DE FIREBASE
#define USER_EMAIL "testuser@gmail.com" 
#define USER_PASSWORD "123456"
//6.- Firebase Auth data object 
FirebaseAuth auth;
//7.- Firebase data object 
FirebaseData fbdo;
//8.- Config Obj 
FirebaseConfig config;
int contador = 0;

/************* DHT11 Setup ********************************************/
#define DHTPIN 2
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

int pin_led = 5;

void setup() {
  Serial.begin(115200);
  //pinMode(LED_PIN,OUTPUT);
  pinMode(pin_led, OUTPUT);
  WiFi.begin(WIFI_SSID , WIFI_PASSWORD);
  while(WiFi.status()!= WL_CONNECTED){
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Conectado con IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();
  //Asignar variables de Firebase 
  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;


  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;
  //LLAMAR A LA FUNCION PARA GENERAR EL TOKEN DE FIREBASE
  config.token_status_callback = tokenStatusCallback;


  //Inicializar Firebase 
  Firebase.begin(&config,&auth);
  //Reconectar 


  Firebase.reconnectWiFi(true); 
  dht.begin();
}


void loop() {
  float temp = dht.readTemperature();
  //float temp = 10;
  if(isnan(temp)){
    Serial.println("Error al leer temperatura");
    return;
  }

   //Verificar que la conexión a firebase exista 


   if(Firebase.ready()){


    //Obtener un valor entero desde Firebase
    if(Firebase.RTDB.getString(&fbdo,"/IoT1/FirebaseIOT/Led_Status")){
      String ledState = fbdo.stringData();
      int miInt = ledState.toInt();
      Serial.print("ESTADO LED:  ");
      Serial.println(miInt);
      if(miInt == 0){
        digitalWrite(pin_led,miInt);
      }else if (miInt == 1){
        digitalWrite(pin_led,miInt);
        }
    }
    else{
      Serial.print("Error: ");
      Serial.println(fbdo.errorReason());
    }


     
    /******************Subir datos desde el ESP8266***************************/
    if(Firebase.RTDB.setInt(&fbdo,"/IoT1/FirebaseIOT/temperatura",temp)){
      contador = contador +=1; 
      Serial.println(temp);
      Serial.println("Valor guardado en Firebase");     
      
    }
    else{
      Serial.print("Error subiendo datos: ");
      Serial.println(fbdo.errorReason());
    }
    delay(3000);
   }
  
}
