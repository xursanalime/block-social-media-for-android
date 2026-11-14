# ReelsBlock

Instagram **Reels** va YouTube **Shorts** ni bloklaydigan shaxsiy Android ilova.
Ilovalarning qolgan qismi (lenta, DM, oddiy videolar, qidiruv) odatdagidek ishlaydi.

Qo'shimcha: TikTok'ni butunlay bloklash (ixtiyoriy), 5/15 daqiqalik pauza va
necha marta bloklangani hisobi (bugun / jami).

## Qanday ishlaydi

Ilova Android'ning «Maxsus imkoniyatlar» (Accessibility) xizmatidan foydalanadi.
Instagram yoki YouTube ekranida Reels/Shorts pleyeri paydo bo'lsa, xizmat
avtomatik «Orqaga» tugmasini bosadi. Root kerak emas, internet ruxsati yo'q,
hech qanday ma'lumot saqlanmaydi yoki yuborilmaydi.

## O'rnatish

1. GitHub'da **Actions → Build APK → oxirgi muvaffaqiyatli run → Artifacts →
   `ReelsBlock-apk`** ni yuklab oling va zip ichidagi `app-debug.apk` ni telefonga o'rnating.
2. ReelsBlock'ni oching → **Maxsus imkoniyatlar sozlamalarini ochish** →
   «O'rnatilgan ilovalar» → ReelsBlock → yoqing.
3. Android 13+ da «Cheklangan sozlama» deb chiqsa:
   Sozlamalar → Ilovalar → ReelsBlock → ⋮ → **Cheklangan sozlamalarga ruxsat berish**,
   keyin 2-qadamni takrorlang.
4. Ba'zi telefonlar (Xiaomi, Samsung va h.k.) xizmatni fonda o'chirib qo'yadi —
   ReelsBlock uchun batareya tejashni o'chirib qo'ying.

Kompyuterda yig'ish: `./gradlew assembleDebug` (JDK 17+ va Android SDK kerak).

## Sozlash

Reels/Shorts ekrani `app/src/main/java/app/reelsblock/Targets.kt` dagi view ID'lar
orqali aniqlanadi. Instagram yoki YouTube yangilangach bloklash ishlamay qolsa,
shu ro'yxatni yangilash kerak. Yangi ilova qo'shish uchun `Targets.kt` ga yangi
`BlockTarget` va `res/xml/blocker_service.xml` dagi `packageNames` ga paket nomini qo'shing.

## Ma'lum cheklovlar

- DM orqali yuborilgan Reels ham bloklanadi (u ham o'sha pleyerda ochiladi).
- YouTube bosh sahifasidagi Shorts «javoni» ko'rinib turadi, lekin bosilsa darhol yopiladi.
- `debug.keystore` ataylab repoda: APK har doim bir xil kalit bilan imzolanadi va
  yangi versiya eskisining ustidan o'rnatiladi. Bu maxfiy kalit emas.
