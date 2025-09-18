# Airline Ticketing API

Bu proje uçak bileti rezervasyonu, satın alma ve yönetimi için geliştirilmiş bir Spring Boot uygulamasıdır.

## Çalıştırma

```bash
mvn spring-boot:run
```

Uygulama varsayılan olarak `http://localhost:8080` adresinde çalışır.

## Dokümantasyon

Tüm servislerin örnek istek ve yanıtları Postman Collection içerisinde paylaşılmıştır.  
Dosya: [docs/Airline_Ticketing_API.postman_collection.json](docs/Airline_Ticketing_API.postman_collection.json)

---

## Servisler

### 1. Airline Servisi

**POST /api/airlines** → Yeni airline oluşturur  

Request:
```json
{
  "name": "Turkish Airlines",
  "iataCode": "TK"
}
```

Response (201 Created):
```json
{
  "id": 1,
  "name": "Turkish Airlines",
  "iataCode": "TK"
}
```

**GET /api/airlines/{id}** → ID ile airline getirir  

Response (404 Not Found):
```json
{
  "timestamp": "2025-09-19T00:00:00",
  "status": 404,
  "error": "Airline with id=99 not found"
}
```

---

### 2. Airport Servisi

**POST /api/airports** → Yeni havaalanı oluşturur  

Request:
```json
{
  "name": "Istanbul Airport",
  "code": "IST"
}
```

Response (201 Created):
```json
{
  "id": 1,
  "name": "Istanbul Airport",
  "code": "IST"
}
```

**GET /api/airports/{id}** → ID ile havaalanı getirir  

Response (404 Not Found):
```json
{
  "timestamp": "2025-09-19T00:00:00",
  "status": 404,
  "error": "Airport with id=42 not found"
}
```

---

### 3. Route Servisi

**POST /api/routes** → Yeni rota oluşturur  

Request:
```json
{
  "originId": 1,
  "destinationId": 2
}
```

Response (201 Created):
```json
{
  "id": 1,
  "origin": { "id": 1, "name": "Istanbul Airport", "code": "IST" },
  "destination": { "id": 2, "name": "Berlin Airport", "code": "BER" }
}
```

**GET /api/routes/{id}** → ID ile rota getirir  

Response (404 Not Found):
```json
{
  "timestamp": "2025-09-19T00:00:00",
  "status": 404,
  "error": "Route with id=15 not found"
}
```

---

### 4. Flight Servisi

**POST /api/flights** → Yeni uçuş oluşturur  

Request:
```json
{
  "airlineId": 1,
  "routeId": 1,
  "departureTime": "2025-09-20T10:00:00",
  "arrivalTime": "2025-09-20T14:00:00",
  "capacity": 180,
  "basePrice": 150.0
}
```

Response (201 Created):
```json
{
  "id": 1,
  "airline": { "id": 1, "name": "Turkish Airlines", "iataCode": "TK" },
  "route": { "id": 1 },
  "departureTime": "2025-09-20T10:00:00",
  "arrivalTime": "2025-09-20T14:00:00",
  "capacity": 180,
  "seatsSold": 0,
  "basePrice": 150.0
}
```

**GET /api/flights/{id}/price** → Sıradaki koltuk fiyatını getirir  

Response (200 OK):
```json
120.0
```

---

### 5. Ticket Servisi

**POST /api/tickets/purchase** → Bilet satın alır  

Request:
```json
{
  "flightId": 1,
  "passengerName": "Ali Veli",
  "passengerEmail": "ali@example.com",
  "cardNumber": "4221-1611-2233-0005"
}
```

Response (201 Created):
```json
{
  "id": 1,
  "ticketNumber": "FL-1-ABCD1234",
  "passengerName": "Ali Veli",
  "passengerEmail": "ali@example.com",
  "pricePaid": 150.0,
  "maskedCardNumber": "422116******0005",
  "status": "ACTIVE",
  "purchasedAt": "2025-09-19T12:00:00",
  "flight": { "id": 1 }
}
```

**POST /api/tickets/{ticketNumber}/cancel** → Bileti iptal eder  

Response (200 OK):
```json
{
  "id": 1,
  "ticketNumber": "FL-1-ABCD1234",
  "status": "CANCELLED"
}
```

Response (404 Not Found):
```json
{
  "timestamp": "2025-09-19T00:00:00",
  "status": 404,
  "error": "Ticket with number=FL-1-XXXX not found"
}
```

---

## Postman Collection

Tüm endpointler için örnek istek ve yanıtlar Postman koleksiyonunda tanımlanmıştır.  
Dosya: `docs/Airline_Ticketing_API.postman_collection.json`
