# Scrapbook Developer Portfolio

Portfolio aesthetic scrapbook yang menggunakan HTML, Tailwind CSS (CDN), CSS custom, JavaScript, Spring Boot, Spring Security, Spring Data JPA, dan PostgreSQL. Aplikasi web statis ikut dipaketkan di dalam executable JAR Spring Boot.

## Fitur
- Responsive scrapbook-style portfolio.
- Project list diambil dari PostgreSQL melalui `GET /api/projects`.
- Project detail modal, theme toggle, mobile nav, scroll reveal, contact form.
- Pesan kontak disimpan ke tabel `contact_messages`.
- Masukan baru juga mengirim notifikasi email melalui SMTP ke alamat yang dikonfigurasi.
- Endpoint admin untuk CRUD project dan membaca pesan, dilindungi Spring Security.
- Dashboard Thymeleaf di `/admin` untuk mengubah pengenalan, bagian about, foto profil, warna portfolio, dan project langsung di PostgreSQL.
- Timeline pendidikan dari SD sampai SMK dapat ditambah, diedit, dan dihapus dari dashboard; daftar ditampilkan kronologis di portfolio publik.
- Sertifikat dapat ditambah, diedit, dan dihapus dari dashboard; data disimpan di PostgreSQL dan ditampilkan di portfolio publik.
- Project dapat diedit/dihapus dan item toolbox dapat ditambah, diedit, dihapus melalui dashboard; daftar toolbox publik dibaca dari PostgreSQL.
- Upload foto profil, logo navigasi portfolio/dashboard, dan gambar project (JPEG, PNG, GIF, WebP; maksimal 5 MB), disimpan di folder `uploads/`.
- Validasi input di backend, BCrypt untuk password admin, CSRF tetap aktif untuk form kontak.
- Tidak menyimpan credential database/admin di source code.

## Prasyarat
- Java 25+ (latest LTS)
- Maven 3.9+
- PostgreSQL 14+ (atau versi yang didukung oleh lingkunganmu)

## 1. Siapkan PostgreSQL

Login sebagai user administrator PostgreSQL, lalu jalankan:

```sql
CREATE USER portfolio_app WITH PASSWORD 'GANTI_DENGAN_PASSWORD_RANDOM_PANJANG';
CREATE DATABASE scrapbook_portfolio OWNER portfolio_app;
```

Gunakan password yang kuat. Jangan gunakan akun superuser `postgres` untuk aplikasi sehari-hari.

## 2. Configure local secrets

Copy `.env.example` to `.env` in the project root, then replace both placeholder passwords with your real PostgreSQL password and a strong, unique admin password. Spring Boot loads `.env` automatically for local development, including when launched from the IDE.

Contoh PowerShell Windows alternatif (gunakan di terminal yang sama dengan Maven):

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/scrapbook_portfolio"
$env:DB_USERNAME="portfolio_app"
$env:DB_PASSWORD="PASSWORD_DATABASE_KAMU"
$env:ADMIN_USERNAME="portfolio-admin"
$env:ADMIN_PASSWORD="PASSWORD_ADMIN_PANJANG_DAN_UNIK"
$env:COOKIE_SECURE="false"
```

Di Linux/macOS gunakan `export NAME=value` jika tidak memakai `.env`.

Jangan masukkan password asli ke Git, screenshot, atau file konfigurasi yang dibagikan. Untuk production, gunakan secret manager dan set `COOKIE_SECURE=true` jika berjalan di belakang HTTPS.

### Notifikasi email masukan

Untuk Gmail, aktifkan verifikasi 2 langkah lalu buat Google App Password. Isi `MAIL_USERNAME` dengan alamat Gmail pengirim dan `MAIL_APP_PASSWORD` dengan App Password tersebut di `.env` lokal atau secret manager deployment. Jangan gunakan password akun Gmail biasa. `FEEDBACK_NOTIFICATION_TO` secara default diarahkan ke `rezayusuf651@gmail.com`; ubah hanya jika ingin menerima notifikasi di alamat lain. Jika SMTP belum dikonfigurasi atau pengiriman gagal, pesan tetap tersimpan dan form akan menampilkan status bahwa notifikasi email belum terkirim.

## 3. Jalankan mode development

Dari folder project:

```bash
mvn spring-boot:run
```

Buka `http://localhost:8080`. Pada start pertama, Hibernate membuat tabel dan aplikasi mengisi tiga contoh project jika tabel project masih kosong.

## 4. Build menjadi satu JAR

```bash
mvn clean package
```

Hasilnya:

```text
target/scrapbook-portfolio.jar
```

JAR tersebut memuat backend Java dan file HTML/CSS/JS dari `src/main/resources/static`.

Jalankan dengan environment variables yang sama:

```bash
java -jar target/scrapbook-portfolio.jar
```

Buka `http://localhost:8080`.

## API
### Dashboard admin
- Buka `http://localhost:8080/admin` dan login dengan `ADMIN_USERNAME` serta `ADMIN_PASSWORD`.
- Gunakan `http://localhost:8080/admin/preview` untuk melihat dashboard tanpa login dalam mode baca saja.
- Pengaturan halaman disimpan pada tabel `portfolio_content`; project tetap disimpan pada tabel `portfolio_projects`.
- Item toolbox disimpan di tabel `portfolio_toolbox`; daftar awal ditambahkan otomatis jika tabel masih kosong.
- Riwayat pendidikan disimpan di tabel `portfolio_education` dan dapat dikelola melalui menu Education di dashboard.
- Sertifikat disimpan di tabel `portfolio_certificates` dan dapat dikelola melalui menu Certificates di dashboard, termasuk penerbit, tahun, deskripsi, tautan verifikasi, dan gambar.
- Upload gambar langsung dari dashboard atau gunakan URL gambar langsung berawalan `https://`. File upload disimpan di `uploads/`; atur `UPLOAD_DIR` ke penyimpanan persisten saat deployment.
- Perubahan warna, teks, dan project akan tampil pada portfolio publik setelah disimpan.

### Public
- `GET /api/projects` — daftar project.
- `GET /api/education` — riwayat pendidikan terurut dari tahun mulai paling awal.
- `GET /api/certificates` — sertifikat terbaru lebih dulu.
- `POST /api/contact` — menyimpan pesan kontak; validasi backend dan CSRF browser berlaku.

### Admin (HTTP Basic, username/password dari environment variables)
Gunakan Postman atau klien API. Autentikasi admin memakai Spring Security HTTP Basic.

- `POST /api/admin/projects` — buat project.
- `PUT /api/admin/projects/{id}` — ubah project.
- `DELETE /api/admin/projects/{id}` — hapus project.
- `POST /api/admin/certificates` — buat sertifikat.
- `PUT /api/admin/certificates/{id}` — ubah sertifikat.
- `DELETE /api/admin/certificates/{id}` — hapus sertifikat.
- `GET /api/admin/messages` — baca pesan kontak.

Contoh JSON untuk membuat project:

```json
{
  "title": "My New Project",
  "description": "A small project built with JavaScript.",
  "category": "Web App",
  "imageUrl": "https://example.com/preview.jpg",
  "projectUrl": "https://example.com",
  "techStack": "HTML, CSS, JavaScript",
  "featured": true
}
```

Kirim Basic Auth dan `Content-Type: application/json`. Jangan gunakan endpoint admin melalui HTTP tanpa TLS di jaringan publik. Di production, wajib HTTPS.

## Catatan keamanan sebelum production
1. Ganti semua credential contoh dan simpan sebagai environment variables/secret manager.
2. Aktifkan HTTPS dan `COOKIE_SECURE=true`.
3. Tambahkan rate limiting/CAPTCHA pada contact endpoint, logging, backup database, dan monitoring.
4. Gunakan Flyway/Liquibase untuk migrasi skema; jangan mengandalkan `ddl-auto=update` untuk production.
5. Tailwind CDN memudahkan prototipe, tetapi untuk deployment yang lebih ketat build Tailwind secara lokal agar tidak bergantung pada CDN dan sesuaikan Content Security Policy.
6. Batasi akses database ke host aplikasi dan gunakan firewall serta least-privilege database role.
7. Tambahkan pengujian, pagination, dan kebijakan retensi untuk pesan kontak sebelum menerima traffic publik.

## Kustomisasi
- Nama dan teks: `src/main/resources/static/index.html`
- Tampilan: `src/main/resources/static/styles.css`
- Interaksi dan fetch API: `src/main/resources/static/app.js`
- Data contoh: `src/main/java/dev/portfolio/config/SeedData.java`
- Konfigurasi: `src/main/resources/application.properties`

Ganti nama “Reza” dan isi profil dengan informasi milikmu sendiri.
