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
- Navigasi mobile, animasi reveal, dan detail project dalam modal.

## Teknologi

| Bagian | Teknologi |
|---|---|
| Frontend publik | HTML, CSS, JavaScript; Google Fonts melalui CDN |
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

### Kenapa frontend bisa terhubung ke backend?

Frontend publik dan backend berjalan dalam aplikasi, host, dan port yang sama. Saat browser membuka `http://localhost:8080`, Spring Boot menyajikan `index.html`, CSS, dan JavaScript dari folder `static/`. JavaScript memanggil alamat relatif seperti `/api/projects`; browser otomatis mengirim request ke origin yang sama, yaitu `http://localhost:8080/api/projects`.

Alur ini menggunakan **HTTP**, bukan koneksi langsung dari JavaScript ke PostgreSQL:

1. Browser menjalankan `fetch()` dari `app.js`, misalnya ke `GET /api/projects`.
2. Spring MVC menerima request dan mencocokkan method serta URL ke controller yang sesuai.
3. Controller membaca data melalui repository (atau service bila ada aturan aplikasi yang perlu dijalankan).
4. Spring mengubah hasil Java menjadi JSON dan mengirimkannya sebagai HTTP response.
5. JavaScript membaca JSON tersebut dan mengisi bagian halaman seperti kartu project.

Karena browser hanya berkomunikasi dengan server aplikasi, kredensial database tidak pernah dikirim ke browser. Penggunaan URL relatif dan satu origin juga membuat frontend dan API tidak memerlukan konfigurasi CORS terpisah pada penggunaan normal. Jika kelak frontend dipindah ke domain/port berbeda, aturan CORS dan alamat API perlu disesuaikan.

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

Contoh satu siklus baca project:

```text
app.js
  └─ fetch("/api/projects")
       └─ GET /api/projects
            └─ ProjectController.listPublicProjects()
                 └─ ProjectRepository
                      └─ PostgreSQL
                 ← daftar Project
            ← JSON (HTTP 200)
       ← JavaScript merender kartu project
```

Request publik di atas tidak membutuhkan URL database atau password database di frontend. Yang digunakan browser hanya URL HTTP aplikasi.

### Dashboard admin

`AdminController` menangani halaman `/admin` dan menu-menunya. Controller menyiapkan data yang diperlukan lalu mengembalikan view `admin`; Thymeleaf menggabungkan view itu dengan data sebelum HTML dikirim ke browser. Saat formulir admin dikirim, browser mengirim request POST yang diproses controller, data divalidasi/disimpan, lalu browser diarahkan kembali ke halaman admin.

Form upload menggunakan `multipart/form-data`. Berbeda dengan foto atau lagu yang diunggah, URL media yang dimasukkan admin hanya disimpan sebagai alamat rujukan.

## Alur backend ke database

Koneksi ke PostgreSQL dikonfigurasi **di sisi backend** pada `application.properties`. Driver PostgreSQL tersedia sebagai dependency runtime di Maven. Saat aplikasi mulai, Spring Boot membaca `DB_URL`, `DB_USERNAME`, dan `DB_PASSWORD` (atau nilai default URL/user di konfigurasi), lalu menyiapkan koneksi database. Contoh URL lokalnya adalah `jdbc:postgresql://localhost:5432/scrapbook_portfolio`: `localhost` adalah host database, `5432` port PostgreSQL, dan bagian terakhir nama database. User database harus memiliki hak akses yang sesuai.

Setelah koneksi tersedia, alur data berjalan seperti berikut:

1. **Controller** menerima HTTP request, membaca path, JSON, parameter, atau field form, lalu menentukan operasi yang dilakukan.
2. **Service**, bila dibutuhkan, menjalankan aturan aplikasi atau mengoordinasikan operasi data. Contohnya `SiteContentService` memastikan satu record konten halaman tersedia dan menyalin field yang boleh diperbarui.
3. **Repository** berbasis Spring Data `JpaRepository` menyediakan operasi baca/simpan/perbarui/hapus. Pada beberapa fitur sederhana controller menggunakan repository langsung; fitur yang memiliki aturan khusus dapat melewati service.
4. **Hibernate/JPA** memetakan entity Java ke tabel dan menerjemahkan operasi repository menjadi SQL melalui koneksi JDBC.
5. **PostgreSQL** menyimpan record. Hasil query kembali ke controller, lalu disajikan sebagai JSON untuk API atau dimasukkan ke model Thymeleaf untuk HTML.

Ringkasnya: **browser —HTTP/JSON→ Spring MVC —repository/JPA/JDBC→ PostgreSQL**. Jalur baliknya juga melalui lapisan yang sama. Browser tidak pernah membuat koneksi JDBC dan database tidak mengirim response langsung ke browser.

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

MVC adalah pola pemisahan tanggung jawab **Model**, **View**, dan **Controller**. Pada proyek ini, pola server-side MVC paling terlihat di dashboard admin yang dirender Thymeleaf. Halaman publik juga menggunakan controller Spring MVC, tetapi endpoint-nya mengembalikan JSON untuk dirender oleh JavaScript di browser (sering disebut pola REST/API).

### Model

Model menggambarkan data yang diproses aplikasi. Entity JPA seperti `Project`, `SiteContent`, `EducationEntry`, dan `ContactMessage` memetakan data ke record database, sekaligus dapat memiliki aturan validasi. Repository bukan view ataupun controller: repository adalah komponen akses data yang digunakan backend untuk membaca dan menyimpan model. Untuk halaman admin, `org.springframework.ui.Model` adalah wadah atribut yang dikirim controller kepada view; ini berbeda dari entity/model domain.

### View

- **Publik:** `src/main/resources/static/index.html`, file CSS, dan `app.js`. HTML/CSS/JavaScript disajikan sebagai aset statis; JavaScript mengambil konten dinamis melalui API lalu memperbarui DOM browser.
- **Admin:** `src/main/resources/templates/admin.html` dan `login.html`. Thymeleaf menggunakan atribut pada `Model` untuk merender HTML di server sebelum response dikirim ke browser.

### Controller

- `AdminController`: menangani request halaman dashboard dan submit form admin; menyiapkan data pada `Model`, lalu memilih template atau redirect.
- `SiteContentController`, `ProjectController`, `ToolboxController`, `EducationController`, `CertificateController`: API konten dan data portofolio.
- `ContactController`: menerima serta memvalidasi request kontak, menyimpan pesan, dan memulai pengiriman notifikasi.
- `LoginPageController`: memilih view halaman login; proses autentikasi dan otorisasi ditangani Spring Security.

Controller adalah penghubung, bukan tempat view mengakses SQL secara langsung. Untuk alur dashboard: browser mengirim request → controller mengambil/mengubah data melalui service atau repository → controller mengisi `Model` → Thymeleaf merender view → HTML dikirim kembali. Untuk alur API: browser mengirim request → controller mengambil data → Spring membuat response JSON → JavaScript memperbarui view publik. Pemisahan ini memudahkan perubahan tampilan tanpa menaruh query database di HTML/JavaScript.

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
- Upload foto menerima PNG, JPEG, GIF, atau WebP hingga 50 MB. Upload media project juga menerima MP4, WebM, atau MOV hingga 150 MB.
- Lagu foto About menerima MP3, WAV, OGG, FLAC, atau M4A, maksimal 20 MB.
- Batas request multipart Spring Boot dikonfigurasi maksimal 150 MB per file dan 160 MB per request; endpoint tertentu menerapkan batas lebih kecil sesuai jenis media.
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
