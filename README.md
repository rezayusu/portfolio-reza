# Scrapbook Developer Portfolio

Aplikasi portofolio pribadi bergaya scrapbook. Frontend publik menggunakan HTML, CSS, dan JavaScript; backend menggunakan Java 25 dan Spring Boot; data disimpan di PostgreSQL. Dashboard admin dirender dengan Thymeleaf dan dilindungi Spring Security.

## Daftar isi

- [Fitur](#fitur)
- [Teknologi](#teknologi)
- [Gambaran arsitektur](#gambaran-arsitektur)
- [Alur frontend ke backend](#alur-frontend-ke-backend)
- [Alur backend ke database](#alur-backend-ke-database)
- [MVC di proyek ini](#mvc-di-proyek-ini)
- [Struktur proyek](#struktur-proyek)
- [Endpoint](#endpoint)
- [Menjalankan aplikasi](#menjalankan-aplikasi)
- [Upload file](#upload-file)
- [Keamanan dan deployment](#keamanan-dan-deployment)
- [Tes](#tes)

## Fitur

- Halaman publik responsif dengan bagian perkenalan, About, project, pendidikan, sertifikat, toolbox, dan kontak.
- Konten halaman dan data portofolio publik diambil dari API backend.
- Dashboard admin untuk mengubah konten, tampilan, project, pendidikan, sertifikat, toolbox, dan melihat pesan.
- Upload gambar dan lagu untuk foto About. Lagu bisa diputar saat foto di-hover; tersedia juga URL media.
- Form kontak menyimpan pesan dan mencoba mengirimkan notifikasi email melalui SMTP.
- Tema terang/gelap, navigasi mobile, animasi reveal, dan detail project dalam modal.

## Teknologi

| Bagian | Teknologi |
|---|---|
| Frontend publik | HTML, CSS, JavaScript, Tailwind CSS melalui CDN |
| View dashboard admin | Thymeleaf |
| Backend | Java 25, Spring Boot 3.5 |
| Web/API | Spring Web MVC |
| Database access | Spring Data JPA dan Hibernate |
| Database | PostgreSQL |
| Validasi | Jakarta Bean Validation |
| Autentikasi dan perlindungan | Spring Security, BCrypt, CSRF |
| Build dan tes | Maven, JUnit, Spring Boot Test |

## Gambaran arsitektur

Aplikasi ini adalah **monolith Spring Boot**: backend Java dan file frontend publik berada dalam satu proyek dan dapat dikemas bersama sebagai satu executable JAR.

```text
Browser
  ├─ Halaman publik: HTML + CSS + JavaScript
  │    └─ fetch()/JSON ───────────────┐
  └─ Dashboard admin: formulir HTML  │
       + Thymeleaf                    │
       └─ form submit / multipart ────┤
                                      ▼
                           Spring Boot / Spring MVC
                           ├─ Controller (routing/API)
                           ├─ Service (aturan aplikasi)
                           ├─ Repository (akses data)
                           └─ ImageStorageService (file)
                               ├─ PostgreSQL
                               └─ folder uploads/
```

Kedua tampilan memakai backend yang sama, tetapi cara komunikasinya berbeda:

- Halaman publik adalah frontend statis di `src/main/resources/static`. JavaScript meminta data lewat endpoint REST dan menerima JSON.
- Dashboard admin adalah halaman server-rendered di `src/main/resources/templates`. Controller mengisi model Thymeleaf, lalu browser mengirim formulir kembali ke backend.

## Alur frontend ke backend

### Halaman publik

Saat browser membuka `/`, Spring Boot menyajikan `static/index.html`. File tersebut memuat `styles.css` untuk tampilan dan `app.js` untuk interaksi. Setelah halaman dimuat, JavaScript meminta data dari API:

| Data yang ditampilkan | Endpoint |
|---|---|
| Konten nama, hero, About, warna, foto, dan URL media | `GET /api/site-content` |
| Project | `GET /api/projects` |
| Toolbox | `GET /api/toolbox` |
| Pendidikan | `GET /api/education` |
| Sertifikat | `GET /api/certificates` |

Spring mengubah objek Java yang dikembalikan controller menjadi JSON. JavaScript menggunakan `fetch()`, kemudian mengisi elemen halaman atau membuat kartu-kartu konten.

Form kontak mengirim JSON ke `POST /api/contact`. Sebelum mengirim, frontend mengambil token CSRF dari `GET /api/csrf`, lalu menyertakan token tersebut pada request. Backend memvalidasi isi pesan dan menyimpannya sebelum mencoba mengirim notifikasi email.

### Dashboard admin

`AdminController` menangani halaman `/admin` dan menu-menunya. Controller menyiapkan data yang diperlukan lalu mengembalikan view `admin`; Thymeleaf menggabungkan view itu dengan data sebelum HTML dikirim ke browser. Saat formulir admin dikirim, browser mengirim request POST yang diproses controller, data divalidasi/disimpan, lalu browser diarahkan kembali ke halaman admin.

Form upload menggunakan `multipart/form-data`. Berbeda dengan foto atau lagu yang diunggah, URL media yang dimasukkan admin hanya disimpan sebagai alamat rujukan.

## Alur backend ke database

1. **Controller** menerima HTTP request, membaca parameter/path/JSON/form, dan memilih operasi yang sesuai.
2. **Service**, jika diperlukan, menjalankan aturan aplikasi. Contohnya `SiteContentService` memastikan konten halaman tunggal dan menyalin perubahan yang boleh disimpan.
3. **Repository** berbasis `JpaRepository` menyediakan operasi simpan, baca, ubah, dan hapus. Spring Data JPA/Hibernate menerjemahkan pemanggilan repository menjadi SQL.
4. **PostgreSQL** menyimpan record terstruktur. Hasil pembacaan dikembalikan melalui controller sebagai JSON untuk API atau sebagai data model untuk Thymeleaf.

Tabel utama yang dipetakan oleh entity:

| Entity | Tabel |
|---|---|
| `SiteContent` | `portfolio_content` |
| `Project` | `portfolio_projects` |
| `ToolboxItem` | `portfolio_toolbox` |
| `EducationEntry` | `portfolio_education` |
| `Certificate` | `portfolio_certificates` |
| `ContactMessage` | `contact_messages` |

Hibernate dikonfigurasi dengan `spring.jpa.hibernate.ddl-auto=update`; saat aplikasi berjalan, Hibernate menyesuaikan skema berdasarkan entity. `SeedData` menambahkan contoh project dan toolbox hanya ketika tabel terkait masih kosong. Konten halaman dibuat saat pertama kali diminta jika belum tersedia.

File gambar/audio **tidak disimpan sebagai byte di PostgreSQL**. `ImageStorageService` memeriksa tipe file, memberi nama acak, lalu menyimpan file ke folder upload; yang disimpan di entity/database adalah URL file tersebut. `UploadResourceConfig` memetakan URL `/uploads/**` ke folder upload agar file dapat dilayani browser.

## MVC di proyek ini

MVC adalah pola pemisahan **Model**, **View**, dan **Controller**. Dalam Spring MVC proyek ini, pola tersebut terutama terlihat pada halaman dashboard admin; endpoint API memakai controller Spring MVC yang sama tetapi mengembalikan data JSON, bukan view HTML.

### Model

Model menggambarkan data dan aturan validasinya. Entity JPA menjadi representasi record database, misalnya `Project`, `SiteContent`, dan `ContactMessage`. Repository menyediakan akses penyimpanan untuk entity tersebut. Pada dashboard, data yang dibutuhkan view juga dikirim lewat `org.springframework.ui.Model`.

### View

- **Publik:** `src/main/resources/static/index.html`, `styles.css`, dan `app.js`. Ini adalah halaman statis; konten dinamisnya diminta melalui API.
- **Admin:** `src/main/resources/templates/admin.html` dan `login.html`. Thymeleaf merender nilai yang disiapkan controller ke dalam HTML.

### Controller

- `AdminController`: halaman dashboard dan aksi formulir admin.
- `SiteContentController`, `ProjectController`, `ToolboxController`, `EducationController`, `CertificateController`: API konten dan data portofolio.
- `ContactController`: menerima pesan kontak dan menyediakan operasi pesan admin.
- `LoginPageController`: halaman login.

Dengan pemisahan ini, view tidak menjalankan query SQL. Controller menghubungkan request dengan service/repository, sedangkan data dan penyimpanannya berada di model/entity dan repository.

## Struktur proyek

```text
src/
├─ main/
│  ├─ java/dev/portfolio/
│  │  ├─ admin/          # Controller dashboard admin
│  │  ├─ certificate/    # Entity, repository, API sertifikat
│  │  ├─ config/         # Security, upload, seed data, konfigurasi
│  │  ├─ contact/        # Pesan kontak dan notifikasi email
│  │  ├─ content/        # Konten halaman, service, repository, upload
│  │  ├─ education/      # Data dan API pendidikan
│  │  ├─ project/        # Data dan API project
│  │  └─ toolbox/        # Data dan API toolbox
│  └─ resources/
│     ├─ static/         # Frontend publik dan aset statis
│     ├─ templates/      # View Thymeleaf admin dan login
│     └─ application.properties
└─ test/java/            # Tes unit dan tes controller
```

## Endpoint

### API publik

| Method | Path | Kegunaan |
|---|---|---|
| `GET` | `/api/site-content` | Mengambil konten umum halaman |
| `GET` | `/api/projects` | Mengambil daftar project |
| `GET` | `/api/toolbox` | Mengambil daftar toolbox |
| `GET` | `/api/education` | Mengambil pendidikan secara kronologis |
| `GET` | `/api/certificates` | Mengambil sertifikat terbaru |
| `GET` | `/api/csrf` | Mengambil token CSRF untuk request browser |
| `POST` | `/api/contact` | Memvalidasi dan menyimpan pesan kontak |

### API admin berbasis JSON

Endpoint berikut memerlukan role admin melalui Spring Security HTTP Basic:

| Method | Path | Kegunaan |
|---|---|---|
| `POST` | `/api/admin/projects` | Membuat project |
| `PUT` | `/api/admin/projects/{id}` | Mengubah project |
| `DELETE` | `/api/admin/projects/{id}` | Menghapus project |
| `POST` | `/api/admin/certificates` | Membuat sertifikat |
| `PUT` | `/api/admin/certificates/{id}` | Mengubah sertifikat |
| `DELETE` | `/api/admin/certificates/{id}` | Menghapus sertifikat |
| `GET` | `/api/admin/messages` | Mengambil pesan kontak |

### Halaman admin

- `/admin` — dashboard.
- `/admin/content` — teks, foto About, lagu hover, logo, dan tautan sosial.
- `/admin/projects`, `/admin/education`, `/admin/certificates`, `/admin/toolbox` — pengelolaan konten terkait.
- `/admin/appearance` — warna situs dan tema dashboard.
- `/admin/messages` — melihat dan mengelola pesan.
- `/admin/preview` — preview dashboard dalam mode baca.

Halaman admin menggunakan login form. Operasi admin melalui browser memakai session dan perlindungan CSRF Spring Security.

## Menjalankan aplikasi

### Prasyarat

- JDK 25 atau lebih baru.
- Maven 3.9 atau lebih baru.
- PostgreSQL yang dapat diakses aplikasi.

### 1. Buat role dan database PostgreSQL

Jalankan sebagai administrator PostgreSQL dan ganti placeholder password dengan password kuat:

```sql
CREATE USER portfolio_app WITH PASSWORD 'GANTI_DENGAN_PASSWORD_KUAT';
CREATE DATABASE scrapbook_portfolio OWNER portfolio_app;
```

### 2. Atur konfigurasi lokal

Aplikasi membaca konfigurasi dari environment variable. Untuk pengembangan lokal, buat file `.env` di root proyek (jangan commit file ini):

```properties
DB_URL=jdbc:postgresql://localhost:5432/scrapbook_portfolio
DB_USERNAME=portfolio_app
DB_PASSWORD=password-database-kamu
ADMIN_USERNAME=portfolio-admin
ADMIN_PASSWORD=password-admin-yang-kuat
COOKIE_SECURE=false
```

`ADMIN_PASSWORD` wajib diisi; aplikasi menolak startup bila password tidak dikonfigurasi. Untuk SMTP, isi juga `MAIL_USERNAME`, `MAIL_APP_PASSWORD`, dan bila perlu `FEEDBACK_NOTIFICATION_TO`. Tanpa SMTP, pesan tetap tersimpan, tetapi notifikasi email tidak terkirim.

### 3. Jalankan dalam mode development

Dari root repository:

```bash
mvn spring-boot:run
```

Buka `http://localhost:8080`. Login admin tersedia di `/login`.

### 4. Build dan jalankan JAR

```bash
mvn clean package
java -jar target/scrapbook-portfolio.jar
```

JAR memuat backend dan frontend statis. Database PostgreSQL serta folder `uploads/` tetap merupakan sumber daya eksternal dan harus tersedia di lingkungan tempat aplikasi berjalan.

## Upload file

- Secara default file disimpan di folder `uploads/` pada working directory aplikasi.
- Atur `UPLOAD_DIR` untuk memilih lokasi lain. Pada deployment, gunakan volume persisten dan backup untuk direktori tersebut.
- Gambar yang diterima: PNG, JPEG, GIF, WebP, serta format video yang didukung oleh upload project.
- Lagu foto About menerima MP3, WAV, OGG, FLAC, atau M4A, maksimal 20 MB.
- Backend membatasi ukuran file gambar/media umum hingga 50 MB; batas request multipart Spring Boot adalah 60 MB.
- File disajikan dari `/uploads/**`; database hanya menyimpan URL-nya.

## Keamanan dan deployment

- Kredensial database, admin, dan SMTP harus disediakan lewat environment variable atau secret manager, bukan ditulis ke source code.
- Password admin disimpan sebagai BCrypt hash oleh Spring Security.
- API admin dibatasi untuk role admin. Form browser menggunakan CSRF; endpoint admin JSON memakai HTTP Basic.
- Aktifkan HTTPS di deployment dan set `COOKIE_SECURE=true` bila berjalan di belakang HTTPS.
- Konfigurasi `ddl-auto=update` cocok untuk pengembangan awal. Untuk production, gunakan Flyway/Liquibase dan tinjau perubahan skema secara terkontrol.
- Gunakan database dan penyimpanan upload persisten, atur backup, batasi akses jaringan, serta jangan mengekspos HTTP Basic tanpa TLS.

## Tes

Jalankan seluruh tes:

```bash
mvn test
```

Jalankan tes tertentu:

```bash
mvn -Dtest=SiteContentServiceTest test
```

## Kustomisasi

- Konten awal dan markup publik: `src/main/resources/static/index.html`
- Styling publik: `src/main/resources/static/styles.css`
- Interaksi serta pemanggilan API publik: `src/main/resources/static/app.js`
- Tampilan dan formulir admin: `src/main/resources/templates/admin.html`
- Controller dashboard: `src/main/java/dev/portfolio/admin/AdminController.java`
- Data contoh: `src/main/java/dev/portfolio/config/SeedData.java`
- Konfigurasi Spring: `src/main/resources/application.properties`

Ganti nama dan konten profil contoh dengan informasi portofolio yang ingin ditampilkan.
