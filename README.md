# Silinen Mesajlar

WhatsApp'ta sana yazılıp sonradan silinen mesajları gösterir.

## Nasıl çalışır
Uygulama sadece WhatsApp **bildirimlerini** okur ve kaydeder. Biri mesaj atıp silerse, WhatsApp bildirimi "Bu mesaj silindi" yapar; uygulama o kişiye ait son kaydedilmiş mesajı "silindi" diye işaretler ve orijinal metni gösterir.

## APK nasıl derlenir (Termux'tan)
1. GitHub'da boş bir repo aç.
2. Termux'ta bu klasörde:
   ```
   git init && git add . && git commit -m "ilk"
   git branch -M main
   git remote add origin https://github.com/KULLANICI/REPO.git
   git push -u origin main
   ```
3. GitHub'da repo > **Actions** sekmesi > son çalışmanın içinden `silinen-mesajlar-apk` dosyasını indir (zip, içinde app-debug.apk var).
4. APK'yı telefona kur ("bilinmeyen kaynaklara izin ver" gerekebilir).

## Kurulumdan sonra
1. Uygulamayı aç > "Bildirim erişimini aç" > Silinen Mesajlar'a izin ver.
2. Android'de pil tasarrufundan uygulamayı muaf tut (yoksa arka planda kapanabilir).
3. WhatsApp ayarlarında bildirimler açık olmalı ve mesaj önizlemesi kapalı olmamalı.

## Sınırlar
- Sadece **bildirim gelen** mesajlar kaydedilir. Sohbet açıkken gelen mesajlarda bildirim çıkmaz, o yüzden kaydolmaz.
- Uygulama kurulmadan / izin verilmeden önce gelen mesajlar yakalanmaz.
- Fotoğraf/ses gibi medyalarda sadece "📷 Fotoğraf" gibi bildirim metni kaydedilir.
- "Benden sil" yapılan mesajlar bildirim üretmez.
- Telefonuna gelen kendi bildirimlerini okur; başkasının telefonuna kurma.
