# Rice Retail Master

An Android inventory app for a Batangas rice retailer. Real-time stock tracking
across two role-based interfaces (Staff and Admin/Owner), backed by Supabase
Auth + Postgres + Realtime, with a Material 3 Compose UI.

- **Version:** 1.0.4 (versionCode 1)
- **Platform:** Android 8.0+ (`minSdk 26`, `targetSdk 34`)
- **Stack:** Kotlin 2.0 · Jetpack Compose · Material 3 · Supabase 3.1 · Ktor (OkHttp engine)

---

## What it does

A two-role app over a shared inventory database:

- **Staff** log deliveries and releases, look up current stock per variety.
- **Owner (Admin)** sees a live dashboard, low-stock alerts, full movement
  history, and manages the rice variety catalog (add / edit / delete).

Every change syncs in real time across all signed-in devices.

### Features at a glance

| Area | Capability |
|---|---|
| **Auth** | Email/password sign-in, persisted sessions (auto-login on next launch), email-based password reset via deep link, friendly error messages |
| **Staff — Stock Release** | Pick variety, enter sacks or kg, validates against current stock so you cannot over-release |
| **Staff — Delivery Log** | Record a delivery with supplier name; inventory and any alerts update automatically |
| **Staff — Quick Check Lookup** | One-tap current stock per variety with low-stock badge |
| **Owner — Live Dashboard** | Totals, low-stock count, today's moves, stock distribution chart, per-variety stock bars; tap a bar to edit / delete; pull-down to refresh |
| **Owner — Low-Stock Alerts** | Auto-raised by a DB trigger when inventory crosses a variety's threshold; mark as read |
| **Owner — Movement History** | Day picker + filter (All / Deliveries / Releases), daily summary card |
| **Owner — Variety Catalog** | Add, rename, retune threshold/capacity, or delete a variety (cascades to its inventory + history + alerts) — case-insensitive duplicate-name guard |
| **Realtime** | Inventory, movements, and alerts auto-update on every screen via Supabase Realtime (WebSocket via Ktor OkHttp engine) |

---

## Repo layout

```
RiceRetailMaster/
├─ app/                              Android module (single)
│  ├─ build.gradle.kts               Reads SUPABASE_URL/ANON_KEY from local.properties
│  └─ src/main/java/com/rodriguez/riceretailmaster/
│     ├─ MainActivity.kt             Hosts Compose + handles password-reset deep links
│     ├─ auth/RecoveryEvents.kt      Activity → Navigation signal for deep-link recovery
│     ├─ data/
│     │  ├─ SupabaseClient.kt        Singleton client (Auth + Postgres + Realtime)
│     │  ├─ RealtimeExt.kt           tableChanges() flow helper
│     │  ├─ model/                   Row models, insert payloads, domain enums
│     │  └─ repository/              Auth, Variety, Inventory, Movement, Alert repos
│     ├─ navigation/AppNavigation.kt SPLASH → LOGIN / STAFF / ADMIN / RESET_PASSWORD
│     ├─ ui/
│     │  ├─ components/              ScreenHeader, Cards, Inputs, Badges, charts…
│     │  ├─ theme/                   Color, Type, Theme (design tokens)
│     │  ├─ login/                   LoginScreen / ResetPasswordScreen + VMs
│     │  ├─ staff/                   release/, delivery/, lookup/ screens + VMs
│     │  └─ admin/                   dashboard/, alerts/, history/ screens + VMs
│     └─ util/                       Validators, Errors (userMessage), Formatters
├─ supabase/
│  ├─ migrations/                    0001 schema → 0004 owner edit/delete RLS
│  └─ scripts/reset_data.sql         One-off: wipe app data for client handover
├─ docs/
│  ├─ SUPABASE_SETUP.md              First-time backend setup (~10 min)
│  ├─ CLIENT_GUIDE.md                Non-technical operator manual
│  └─ ARCHITECTURE.md                Layers, data flow, realtime, navigation
└─ README.md                         You are here
```

---

## Build & run

### Prerequisites

- Android Studio Iguana (or newer) with the Android SDK 26+ platform
- A Supabase project — follow [`docs/SUPABASE_SETUP.md`](docs/SUPABASE_SETUP.md) once
- `local.properties` at the repo root with:
  ```properties
  sdk.dir=/path/to/Android/sdk
  SUPABASE_URL=https://YOUR-PROJECT-ref.supabase.co
  SUPABASE_ANON_KEY=eyJhbGciOi...
  ```

### From Android Studio

Open the project → wait for Gradle sync → **Run 'app'** on a device or
emulator (API 26+).

### From the command line

```sh
# Build a debug APK
./gradlew :app:assembleDebug

# Install on the connected device
./gradlew :app:installDebug
```

The APK lands at `app/build/outputs/apk/debug/app-debug.apk`.

---

## More docs

- [`docs/SUPABASE_SETUP.md`](docs/SUPABASE_SETUP.md) — first-time backend setup
- [`docs/CLIENT_GUIDE.md`](docs/CLIENT_GUIDE.md) — non-technical operator manual
  (how the owner and staff use the app, account management, password reset)
- [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) — layers, data flow, realtime,
  navigation graph, auth/session model
