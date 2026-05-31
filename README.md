##  Opis Aplikacji

**CMMS Mobile** to mobilna aplikacja Android wspomagająca zarządzanie utrzymaniem ruchu maszyn (Computerized Maintenance Management System). Aplikacja stanowi mobilne rozszerzenie systemu webowego i umożliwia pracownikom zakładu zarządzanie zleceniami serwisowymi, maszynami oraz powiadomieniami bezpośrednio z urządzenia mobilnego.

##  Instrukcje uruchomienia

#### Backend

- Wymagania:

Docker Desktop, Node.js 22.x

Zaczynamy od sklonowania repozytorium zawierającego api oraz instalacji zależności:

```bash
git clone https://github.com/stafecki/cmms.git
cd cmms
npm install
```

Skopiuj i uzupełnij zmienne środowiskowe

```bash
cp .env.example .env
```

Polecenie buduje i uruchamia wszystkie kontenery:

```bash
npm run docker:up
```

Uruchom migracje:

```bash
npm run docker:migrate
```

Załaduj dane testowe:

```bash
npm run docker:seed
```

Domyślne konto administratora po seedowaniu bazy danych dostępne jest w pliku `apps/api/prisma/seed.ts`.

Serwer domyślnie działa na `http://localhost:3000`.

#### Zatrzymanie aplikacji

```bash
npm run docker:down
```

#### Aplikacja mobilna

**Wymagania:** Android Studio, Android SDK, urządzenie fizyczne lub emulator (min. Android 8.0 API 26)

1. Otwórz folder `app/` w Android Studio
2. Upewnij się że backend działa lokalnie
3. W pliku `app/src/main/java/com/example/cmms/data/remote/ApiClient.java` sprawdź adres bazowy API:
    - Emulator: `http://10.0.2.2:3000/`
    - Urządzenie fizyczne: `http://<IP-komputera>:3000/`
4. Kliknij **Run** lub `Shift+F10`
