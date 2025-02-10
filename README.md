# 🎟️ Event List App

## 📋 Overview
Event List App adalah aplikasi berbasis Kotlin yang menampilkan daftar event berdasarkan statusnya (akan datang & selesai) menggunakan Bottom Navigation. Data event diambil dari API, dan setiap event memiliki halaman detail yang menampilkan informasi lengkap acara.

## 🚀 Fitur Utama
✅ Menampilkan 2 jenis daftar event berdasarkan status:

Event yang akan datang (Upcoming Events) 🗓️
Event yang sudah selesai (Finished Events) ✅
✅ Menampilkan daftar event dari API dengan informasi utama:
📌 Gambar event (imageLogo/mediaCover)
🎤 Nama acara (name)
✅ Menampilkan detail event, termasuk:
📌 Gambar event (imageLogo/mediaCover)
🎤 Nama acara (name)
👤 Penyelenggara (ownerName)
⏰ Waktu acara (beginTime)
🎟️ Sisa kuota peserta (quota - registrant)
📝 Deskripsi acara (description)
🔗 Tombol untuk membuka link acara (link)
✅ Indikator Loading saat mengambil data dari API 🔄

## 🏗 Struktur Proyek
1️⃣ Menggunakan Bottom Navigation untuk berpindah antar menu
2️⃣ Menampilkan daftar event dengan RecyclerView
3️⃣ Mengambil data event dari API menggunakan Retrofit
4️⃣ Menampilkan halaman detail event dengan informasi lengkap
5️⃣ Menambahkan indikator loading untuk pengalaman pengguna yang lebih baik

## 🛠 Teknologi yang Digunakan
Kotlin (Bahasa Pemrograman)
MVVM (Model-View-ViewModel)
Bottom Navigation View
Retrofit (Networking/API)
Glide (Image Loading)
LiveData & ViewModel (State Management)

## 💡 Cara Menjalankan Proyek
1️⃣ Clone repository ini:

sh
Copy
Edit
git clone https://github.com/username/EventListApp.git
2️⃣ Buka project di Android Studio
3️⃣ Jalankan aplikasi di Emulator atau Perangkat Fisik
4️⃣ Pastikan API yang digunakan tersedia dan dapat diakses

## 🎯 Kesimpulan
Aplikasi Event List App memungkinkan pengguna untuk melihat dan menjelajahi acara berdasarkan statusnya. Dengan Bottom Navigation, API Integration, dan Detail View, aplikasi ini memberikan pengalaman yang intuitif dan informatif bagi pengguna.

## Application

<img width="344" src="https://github.com/user-attachments/assets/f25939fd-9274-449b-b8e2-e534debd7828">

<img width="344" src="https://github.com/user-attachments/assets/6cce802e-aaac-4a5e-9453-7b0d841487cf">

