# VGS Collect Android SDK

For SDK API reference, field types, validation rules, submission patterns, and security policies see [AGENTS.md](AGENTS.md).

## Build

```sh
./gradlew :vgscollect:assembleDebug
./gradlew :vgscollect:compileDebugKotlin    # compile check only
./gradlew :app:assembleDebug                # demo app
```

## Tests

```sh
./gradlew :vgscollect:testDebugUnitTest     # unit tests
./gradlew :app:connectedDebugAndroidTest    # instrumentation tests (requires emulator/device)
```

## Lint

```sh
./gradlew :vgscollect:detekt
```

## Modules

- `vgscollect` — core SDK (View widgets + Compose + CMP)
- `vgscollect-blinkcard` — BlinkCard scanner integration
- `vgs-sdk-analytics:VGSClientSDKAnalytics` — analytics (local debug, published release)
- `app` — demo application

## CMP (Card Management Platform)

Session-based card lifecycle via `VGSCollect.session()` → `createCard()` / `updateCard()`.
Key files: `VGSCollect.kt` (session factory, cmpRequest), `CardAttributesManager.kt`,
`VgsAuthHandler.kt`, `model/network/cmp/` (request classes), `api/UrlExtension.kt` (CMP URL).
CMP routes to `vgsapi.com` (not vault proxy). Card attributes lookup via `card-enrichment-api.verygoodvault.com`.