const SURAHS_DATA = [
  {
    "number": 1,
    "arabicName": "الفاتحة",
    "englishName": "Al-Fatihah",
    "ayahCount": 7,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/001%20-%20%D8%A7%D9%84%D9%81%D8%A7%D8%AA%D8%AD%D8%A9.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/001.mp3"
  },
  {
    "number": 2,
    "arabicName": "البقرة",
    "englishName": "Al-Baqarah",
    "ayahCount": 286,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/002%20-%20%D8%A7%D9%84%D8%A8%D9%82%D8%B1%D8%A9.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/002.mp3"
  },
  {
    "number": 3,
    "arabicName": "آل عمران",
    "englishName": "Ali Imran",
    "ayahCount": 200,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/003%20-%20%D8%A2%D9%84%20%D8%B9%D9%85%D8%B1%D8%A7%D9%86.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/003.mp3"
  },
  {
    "number": 4,
    "arabicName": "النساء",
    "englishName": "An-Nisa",
    "ayahCount": 176,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/004%20-%20%D8%A7%D9%84%D9%86%D8%B3%D8%A7%D8%A1.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/004.mp3"
  },
  {
    "number": 5,
    "arabicName": "المائدة",
    "englishName": "Al-Maidah",
    "ayahCount": 120,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/005%20-%20%D8%A7%D9%84%D9%85%D8%A7%D8%A6%D8%AF%D8%A9.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/005.mp3"
  },
  {
    "number": 6,
    "arabicName": "الأنعام",
    "englishName": "Al-Anam",
    "ayahCount": 165,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/006%20-%20%D8%A7%D9%84%D8%A3%D9%86%D8%B9%D8%A7%D9%85.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/006.mp3"
  },
  {
    "number": 7,
    "arabicName": "الأعراف",
    "englishName": "Al-Araf",
    "ayahCount": 206,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/007%20-%20%D8%A7%D9%84%D8%A3%D8%B9%D8%B1%D8%A7%D9%81.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/007.mp3"
  },
  {
    "number": 8,
    "arabicName": "الأنفال",
    "englishName": "Al-Anfal",
    "ayahCount": 75,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/008%20-%20%D8%A7%D9%84%D8%A3%D9%86%D9%81%D8%A7%D9%84.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/008.mp3"
  },
  {
    "number": 9,
    "arabicName": "التوبة",
    "englishName": "At-Tawbah",
    "ayahCount": 129,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/009%20-%20%D8%A7%D9%84%D8%AA%D9%88%D8%A8%D8%A9.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/009.mp3"
  },
  {
    "number": 10,
    "arabicName": "يونس",
    "englishName": "Yunus",
    "ayahCount": 109,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/010%20-%20%D9%8A%D9%88%D9%86%D8%B3.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/010.mp3"
  },
  {
    "number": 11,
    "arabicName": "هود",
    "englishName": "Hud",
    "ayahCount": 123,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/011%20-%20%D9%87%D9%88%D8%AF.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/011.mp3"
  },
  {
    "number": 12,
    "arabicName": "يوسف",
    "englishName": "Yusuf",
    "ayahCount": 111,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/012%20-%20%D9%8A%D9%88%D8%B3%D9%81.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/012.mp3"
  },
  {
    "number": 13,
    "arabicName": "الرعد",
    "englishName": "Ar-Rad",
    "ayahCount": 43,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/013%20-%20%D8%A7%D9%84%D8%B1%D8%B9%D8%AF.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/013.mp3"
  },
  {
    "number": 14,
    "arabicName": "إبراهيم",
    "englishName": "Ibrahim",
    "ayahCount": 52,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/014%20-%20%D8%A5%D8%A8%D8%B1%D8%A7%D9%87%D9%8A%D9%85.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/014.mp3"
  },
  {
    "number": 15,
    "arabicName": "الحجر",
    "englishName": "Al-Hijr",
    "ayahCount": 99,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/015%20-%20%D8%A7%D9%84%D8%AD%D8%AC%D8%B1.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/015.mp3"
  },
  {
    "number": 16,
    "arabicName": "النحل",
    "englishName": "An-Nahl",
    "ayahCount": 128,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/016%20-%20%D8%A7%D9%84%D9%86%D8%AD%D9%84.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/016.mp3"
  },
  {
    "number": 17,
    "arabicName": "الإسراء",
    "englishName": "Al-Isra",
    "ayahCount": 111,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/017%20-%20%D8%A7%D9%84%D8%A5%D8%B3%D8%B1%D8%A7%D8%A1.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/017.mp3"
  },
  {
    "number": 18,
    "arabicName": "الكهف",
    "englishName": "Al-Kahf",
    "ayahCount": 110,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/018%20-%20%D8%A7%D9%84%D9%83%D9%87%D9%81.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/018.mp3"
  },
  {
    "number": 19,
    "arabicName": "مريم",
    "englishName": "Maryam",
    "ayahCount": 98,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/019%20-%20%D9%85%D8%B1%D9%8A%D9%85.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/019.mp3"
  },
  {
    "number": 20,
    "arabicName": "طه",
    "englishName": "Taha",
    "ayahCount": 135,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/020%20-%20%D8%B7%D9%87.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/020.mp3"
  },
  {
    "number": 21,
    "arabicName": "الأنبياء",
    "englishName": "Al-Anbiya",
    "ayahCount": 112,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/021%20-%20%D8%A7%D9%84%D8%A3%D9%86%D8%A8%D9%8A%D8%A7%D8%A1.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/021.mp3"
  },
  {
    "number": 22,
    "arabicName": "الحج",
    "englishName": "Al-Hajj",
    "ayahCount": 78,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/022%20-%20%D8%A7%D9%84%D8%AD%D8%AC.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/022.mp3"
  },
  {
    "number": 23,
    "arabicName": "المؤمنون",
    "englishName": "Al-Muminun",
    "ayahCount": 118,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/023%20-%20%D8%A7%D9%84%D9%85%D8%A4%D9%85%D9%86%D9%88%D9%86.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/023.mp3"
  },
  {
    "number": 24,
    "arabicName": "النور",
    "englishName": "An-Nur",
    "ayahCount": 64,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/024%20-%20%D8%A7%D9%84%D9%86%D9%88%D8%B1.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/024.mp3"
  },
  {
    "number": 25,
    "arabicName": "الفرقان",
    "englishName": "Al-Furqan",
    "ayahCount": 77,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/025%20-%20%D8%A7%D9%84%D9%81%D8%B1%D9%82%D8%A7%D9%86.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/025.mp3"
  },
  {
    "number": 26,
    "arabicName": "الشعراء",
    "englishName": "Ash-Shuara",
    "ayahCount": 227,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/026%20-%20%D8%A7%D9%84%D8%B4%D8%B9%D8%B1%D8%A7%D8%A1.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/026.mp3"
  },
  {
    "number": 27,
    "arabicName": "النمل",
    "englishName": "An-Naml",
    "ayahCount": 93,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/027%20-%20%D8%A7%D9%84%D9%86%D9%85%D9%84.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/027.mp3"
  },
  {
    "number": 28,
    "arabicName": "القصص",
    "englishName": "Al-Qasas",
    "ayahCount": 88,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/028%20-%20%D8%A7%D9%84%D9%82%D8%B5%D8%B5.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/028.mp3"
  },
  {
    "number": 29,
    "arabicName": "العنكبوت",
    "englishName": "Al-Ankabut",
    "ayahCount": 69,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/029%20-%20%D8%A7%D9%84%D8%B9%D9%86%D9%83%D8%A8%D9%88%D8%AA.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/029.mp3"
  },
  {
    "number": 30,
    "arabicName": "الروم",
    "englishName": "Ar-Rum",
    "ayahCount": 60,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/030%20-%20%D8%A7%D9%84%D8%B1%D9%88%D9%85.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/030.mp3"
  },
  {
    "number": 31,
    "arabicName": "لقمان",
    "englishName": "Luqman",
    "ayahCount": 34,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/031%20-%20%D9%84%D9%82%D9%85%D8%A7%D9%86.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/031.mp3"
  },
  {
    "number": 32,
    "arabicName": "السجدة",
    "englishName": "As-Sajdah",
    "ayahCount": 30,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/032%20-%20%D8%A7%D9%84%D8%B3%D8%AC%D8%AF%D8%A9.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/032.mp3"
  },
  {
    "number": 33,
    "arabicName": "الأحزاب",
    "englishName": "Al-Ahzab",
    "ayahCount": 73,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/033%20-%20%D8%A7%D9%84%D8%A3%D8%AD%D8%B2%D8%A7%D8%A8.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/033.mp3"
  },
  {
    "number": 34,
    "arabicName": "سبأ",
    "englishName": "Saba",
    "ayahCount": 54,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/034%20-%20%D8%B3%D8%A8%D8%A3.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/034.mp3"
  },
  {
    "number": 35,
    "arabicName": "فاطر",
    "englishName": "Fatir",
    "ayahCount": 45,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/035%20-%20%D9%81%D8%A7%D8%B7%D8%B1.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/035.mp3"
  },
  {
    "number": 36,
    "arabicName": "يس",
    "englishName": "Ya-Sin",
    "ayahCount": 83,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/036%20-%20%D9%8A%D8%B3.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/036.mp3"
  },
  {
    "number": 37,
    "arabicName": "الصافات",
    "englishName": "As-Saffat",
    "ayahCount": 182,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/037%20-%20%D8%A7%D9%84%D8%B5%D8%A7%D9%81%D8%A7%D8%AA.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/037.mp3"
  },
  {
    "number": 38,
    "arabicName": "ص",
    "englishName": "Sad",
    "ayahCount": 88,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/038%20-%20%D8%B5.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/038.mp3"
  },
  {
    "number": 39,
    "arabicName": "الزمر",
    "englishName": "Az-Zumar",
    "ayahCount": 75,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/039%20-%20%D8%A7%D9%84%D8%B2%D9%85%D8%B1.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/039.mp3"
  },
  {
    "number": 40,
    "arabicName": "غافر",
    "englishName": "Ghafir",
    "ayahCount": 85,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/040%20-%20%D8%BA%D8%A7%D9%81%D8%B1.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/040.mp3"
  },
  {
    "number": 41,
    "arabicName": "فصلت",
    "englishName": "Fussilat",
    "ayahCount": 54,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/041%20-%20%D9%81%D8%B5%D9%84%D8%AA.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/041.mp3"
  },
  {
    "number": 42,
    "arabicName": "الشورى",
    "englishName": "Ash-Shura",
    "ayahCount": 53,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/042%20-%20%D8%A7%D9%84%D8%B4%D9%88%D8%B1%D9%89.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/042.mp3"
  },
  {
    "number": 43,
    "arabicName": "الزخرف",
    "englishName": "Az-Zukhruf",
    "ayahCount": 89,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/043%20-%20%D8%A7%D9%84%D8%B2%D8%AE%D8%B1%D9%81.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/043.mp3"
  },
  {
    "number": 44,
    "arabicName": "الدخان",
    "englishName": "Ad-Dukhan",
    "ayahCount": 59,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/044%20-%20%D8%A7%D9%84%D8%AF%D8%AE%D8%A7%D9%86.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/044.mp3"
  },
  {
    "number": 45,
    "arabicName": "الجاثية",
    "englishName": "Al-Jathiyah",
    "ayahCount": 37,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/045%20-%20%D8%A7%D9%84%D8%AC%D8%A7%D8%AB%D9%8A%D8%A9.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/045.mp3"
  },
  {
    "number": 46,
    "arabicName": "الأحقاف",
    "englishName": "Al-Ahqaf",
    "ayahCount": 35,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/046%20-%20%D8%A7%D9%84%D8%A3%D8%AD%D9%82%D8%A7%D9%81.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/046.mp3"
  },
  {
    "number": 47,
    "arabicName": "محمد",
    "englishName": "Muhammad",
    "ayahCount": 38,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/047%20-%20%D9%85%D8%AD%D9%85%D8%AF.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/047.mp3"
  },
  {
    "number": 48,
    "arabicName": "الفتح",
    "englishName": "Al-Fath",
    "ayahCount": 29,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/048%20-%20%D8%A7%D9%84%D9%81%D8%AA%D8%AD.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/048.mp3"
  },
  {
    "number": 49,
    "arabicName": "الحجرات",
    "englishName": "Al-Hujurat",
    "ayahCount": 18,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/049%20-%20%D8%A7%D9%84%D8%AD%D8%AC%D8%B1%D8%A7%D8%AA.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/049.mp3"
  },
  {
    "number": 50,
    "arabicName": "ق",
    "englishName": "Qaf",
    "ayahCount": 45,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/050%20-%20%D9%82.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/050.mp3"
  },
  {
    "number": 51,
    "arabicName": "الذاريات",
    "englishName": "Adh-Dhariyat",
    "ayahCount": 60,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/051%20-%20%D8%A7%D9%84%D8%B0%D8%A7%D8%B1%D9%8A%D8%A7%D8%AA.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/051.mp3"
  },
  {
    "number": 52,
    "arabicName": "الطور",
    "englishName": "At-Tur",
    "ayahCount": 49,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/052%20-%20%D8%A7%D9%84%D8%B7%D9%88%D8%B1.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/052.mp3"
  },
  {
    "number": 53,
    "arabicName": "النجم",
    "englishName": "An-Najm",
    "ayahCount": 62,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/053%20-%20%D8%A7%D9%84%D9%86%D8%AC%D9%85.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/053.mp3"
  },
  {
    "number": 54,
    "arabicName": "القمر",
    "englishName": "Al-Qamar",
    "ayahCount": 55,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/054%20-%20%D8%A7%D9%84%D9%82%D9%85%D8%B1.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/054.mp3"
  },
  {
    "number": 55,
    "arabicName": "الرحمن",
    "englishName": "Ar-Rahman",
    "ayahCount": 78,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/055%20-%20%D8%A7%D9%84%D8%B1%D8%AD%D9%85%D9%86.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/055.mp3"
  },
  {
    "number": 56,
    "arabicName": "الواقعة",
    "englishName": "Al-Waqiah",
    "ayahCount": 96,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/056%20-%20%D8%A7%D9%84%D9%88%D8%A7%D9%82%D8%B9%D8%A9.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/056.mp3"
  },
  {
    "number": 57,
    "arabicName": "الحديد",
    "englishName": "Al-Hadid",
    "ayahCount": 29,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/057%20-%20%D8%A7%D9%84%D8%AD%D8%AF%D9%8A%D8%AF.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/057.mp3"
  },
  {
    "number": 58,
    "arabicName": "المجادلة",
    "englishName": "Al-Mujadila",
    "ayahCount": 22,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/058%20-%20%D8%A7%D9%84%D9%85%D8%AC%D8%A7%D8%AF%D9%84%D8%A9.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/058.mp3"
  },
  {
    "number": 59,
    "arabicName": "الحشر",
    "englishName": "Al-Hashr",
    "ayahCount": 24,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/059%20-%20%D8%A7%D9%84%D8%AD%D8%B4%D8%B1.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/059.mp3"
  },
  {
    "number": 60,
    "arabicName": "الممتحنة",
    "englishName": "Al-Mumtahanah",
    "ayahCount": 13,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/060%20-%20%D8%A7%D9%84%D9%85%D9%85%D8%AA%D8%AD%D9%86%D8%A9.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/060.mp3"
  },
  {
    "number": 61,
    "arabicName": "الصف",
    "englishName": "As-Saff",
    "ayahCount": 14,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/061%20-%20%D8%A7%D9%84%D8%B5%D9%81.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/061.mp3"
  },
  {
    "number": 62,
    "arabicName": "الجمعة",
    "englishName": "Al-Jumuah",
    "ayahCount": 11,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/062%20-%20%D8%A7%D9%84%D8%AC%D9%85%D8%B9%D8%A9.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/062.mp3"
  },
  {
    "number": 63,
    "arabicName": "المنافقون",
    "englishName": "Al-Munafiqun",
    "ayahCount": 11,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/063%20-%20%D8%A7%D9%84%D9%85%D9%86%D8%A7%D9%81%D9%82%D9%88%D9%86.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/063.mp3"
  },
  {
    "number": 64,
    "arabicName": "التغابن",
    "englishName": "At-Taghabun",
    "ayahCount": 18,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/064%20-%20%D8%A7%D9%84%D8%AA%D8%BA%D8%A7%D8%A8%D9%86.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/064.mp3"
  },
  {
    "number": 65,
    "arabicName": "الطلاق",
    "englishName": "At-Talaq",
    "ayahCount": 12,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/065%20-%20%D8%A7%D9%84%D8%B7%D9%84%D8%A7%D9%82.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/065.mp3"
  },
  {
    "number": 66,
    "arabicName": "التحريم",
    "englishName": "At-Tahrim",
    "ayahCount": 12,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/066%20-%20%D8%A7%D9%84%D8%AA%D8%AD%D8%B1%D9%8A%D9%85.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/066.mp3"
  },
  {
    "number": 67,
    "arabicName": "الملك",
    "englishName": "Al-Mulk",
    "ayahCount": 30,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/067%20-%20%D8%A7%D9%84%D9%85%D9%84%D9%83.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/067.mp3"
  },
  {
    "number": 68,
    "arabicName": "القلم",
    "englishName": "Al-Qalam",
    "ayahCount": 52,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/068%20-%20%D8%A7%D9%84%D9%82%D9%84%D9%85.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/068.mp3"
  },
  {
    "number": 69,
    "arabicName": "الحاقة",
    "englishName": "Al-Haqqah",
    "ayahCount": 52,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/069%20-%20%D8%A7%D9%84%D8%AD%D8%A7%D9%82%D8%A9.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/069.mp3"
  },
  {
    "number": 70,
    "arabicName": "المعارج",
    "englishName": "Al-Maarij",
    "ayahCount": 44,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/070%20-%20%D8%A7%D9%84%D9%85%D8%B9%D8%A7%D8%B1%D8%AC.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/070.mp3"
  },
  {
    "number": 71,
    "arabicName": "نوح",
    "englishName": "Nuh",
    "ayahCount": 28,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/071%20-%20%D9%86%D9%88%D8%AD.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/071.mp3"
  },
  {
    "number": 72,
    "arabicName": "الجن",
    "englishName": "Al-Jinn",
    "ayahCount": 28,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/072%20-%20%D8%A7%D9%84%D8%AC%D9%86.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/072.mp3"
  },
  {
    "number": 73,
    "arabicName": "المزمل",
    "englishName": "Al-Muzzammil",
    "ayahCount": 20,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/073%20-%20%D8%A7%D9%84%D9%85%D8%B2%D9%85%D9%84.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/073.mp3"
  },
  {
    "number": 74,
    "arabicName": "المدثر",
    "englishName": "Al-Muddaththir",
    "ayahCount": 56,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/074%20-%20%D8%A7%D9%84%D9%85%D8%AF%D8%AB%D8%B1.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/074.mp3"
  },
  {
    "number": 75,
    "arabicName": "القيامة",
    "englishName": "Al-Qiyamah",
    "ayahCount": 40,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/075%20-%20%D8%A7%D9%84%D9%82%D9%8A%D8%A7%D9%85%D8%A9.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/075.mp3"
  },
  {
    "number": 76,
    "arabicName": "الإنسان",
    "englishName": "Al-Insan",
    "ayahCount": 31,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/076%20-%20%D8%A7%D9%84%D8%A5%D9%86%D8%B3%D8%A7%D9%86.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/076.mp3"
  },
  {
    "number": 77,
    "arabicName": "المرسلات",
    "englishName": "Al-Mursalat",
    "ayahCount": 50,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/077%20-%20%D8%A7%D9%84%D9%85%D8%B1%D8%B3%D9%84%D8%A7%D8%AA.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/077.mp3"
  },
  {
    "number": 78,
    "arabicName": "النبأ",
    "englishName": "An-Naba",
    "ayahCount": 40,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/078%20-%20%D8%A7%D9%84%D9%86%D8%A8%D8%A3.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/078.mp3"
  },
  {
    "number": 79,
    "arabicName": "النازعات",
    "englishName": "An-Naziat",
    "ayahCount": 46,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/079%20-%20%D8%A7%D9%84%D9%86%D8%A7%D8%B2%D8%B9%D8%A7%D8%AA.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/079.mp3"
  },
  {
    "number": 80,
    "arabicName": "عبس",
    "englishName": "Abasa",
    "ayahCount": 42,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/080%20-%20%D8%B9%D8%A8%D8%B3.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/080.mp3"
  },
  {
    "number": 81,
    "arabicName": "التكوير",
    "englishName": "At-Takwir",
    "ayahCount": 29,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/081%20-%20%D8%A7%D9%84%D8%AA%D9%83%D9%88%D9%8A%D8%B1.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/081.mp3"
  },
  {
    "number": 82,
    "arabicName": "الانفطار",
    "englishName": "Al-Infitar",
    "ayahCount": 19,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/082%20-%20%D8%A7%D9%84%D8%A7%D9%86%D9%81%D8%B7%D8%A7%D8%B1.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/082.mp3"
  },
  {
    "number": 83,
    "arabicName": "المطففين",
    "englishName": "Al-Mutaffifin",
    "ayahCount": 36,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/083%20-%20%D8%A7%D9%84%D9%85%D8%B7%D9%81%D9%81%D9%8A%D9%86.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/083.mp3"
  },
  {
    "number": 84,
    "arabicName": "الانشقاق",
    "englishName": "Al-Inshiqaq",
    "ayahCount": 25,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/084%20-%20%D8%A7%D9%84%D8%A7%D9%86%D8%B4%D9%82%D8%A7%D9%82.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/084.mp3"
  },
  {
    "number": 85,
    "arabicName": "البروج",
    "englishName": "Al-Buruj",
    "ayahCount": 22,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/085%20-%20%D8%A7%D9%84%D8%A8%D8%B1%D9%88%D8%AC.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/085.mp3"
  },
  {
    "number": 86,
    "arabicName": "الطارق",
    "englishName": "At-Tariq",
    "ayahCount": 17,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/086%20-%20%D8%A7%D9%84%D8%B7%D8%A7%D8%B1%D9%82.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/086.mp3"
  },
  {
    "number": 87,
    "arabicName": "الأعلى",
    "englishName": "Al-Ala",
    "ayahCount": 19,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/087%20-%20%D8%A7%D9%84%D8%A3%D8%B9%D9%84%D9%89.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/087.mp3"
  },
  {
    "number": 88,
    "arabicName": "الغاشية",
    "englishName": "Al-Ghashiyah",
    "ayahCount": 26,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/088%20-%20%D8%A7%D9%84%D8%BA%D8%A7%D8%B4%D9%8A%D8%A9.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/088.mp3"
  },
  {
    "number": 89,
    "arabicName": "الفجر",
    "englishName": "Al-Fajr",
    "ayahCount": 30,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/089%20-%20%D8%A7%D9%84%D9%81%D8%AC%D8%B1.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/089.mp3"
  },
  {
    "number": 90,
    "arabicName": "البلد",
    "englishName": "Al-Balad",
    "ayahCount": 20,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/090%20-%20%D8%A7%D9%84%D8%A8%D9%84%D8%AF.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/090.mp3"
  },
  {
    "number": 91,
    "arabicName": "الشمس",
    "englishName": "Ash-Shams",
    "ayahCount": 15,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/091%20-%20%D8%A7%D9%84%D8%B4%D9%85%D8%B3.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/091.mp3"
  },
  {
    "number": 92,
    "arabicName": "الليل",
    "englishName": "Al-Layl",
    "ayahCount": 21,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/092%20-%20%D8%A7%D9%84%D9%84%D9%8A%D9%84.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/092.mp3"
  },
  {
    "number": 93,
    "arabicName": "الضحى",
    "englishName": "Ad-Duha",
    "ayahCount": 11,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/093%20-%20%D8%A7%D9%84%D8%B6%D8%AD%D9%89.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/093.mp3"
  },
  {
    "number": 94,
    "arabicName": "الشرح",
    "englishName": "Ash-Sharh",
    "ayahCount": 8,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/094%20-%20%D8%A7%D9%84%D8%B4%D8%B1%D8%AD.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/094.mp3"
  },
  {
    "number": 95,
    "arabicName": "التين",
    "englishName": "At-Tin",
    "ayahCount": 8,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/095%20-%20%D8%A7%D9%84%D8%AA%D9%8A%D9%86.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/095.mp3"
  },
  {
    "number": 96,
    "arabicName": "العلق",
    "englishName": "Al-Alaq",
    "ayahCount": 19,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/096%20-%20%D8%A7%D9%84%D8%B9%D9%84%D9%82.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/096.mp3"
  },
  {
    "number": 97,
    "arabicName": "القدر",
    "englishName": "Al-Qadr",
    "ayahCount": 5,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/097%20-%20%D8%A7%D9%84%D9%82%D8%AF%D8%B1.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/097.mp3"
  },
  {
    "number": 98,
    "arabicName": "البينة",
    "englishName": "Al-Bayyinah",
    "ayahCount": 8,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/098%20-%20%D8%A7%D9%84%D8%A8%D9%8A%D9%86%D8%A9.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/098.mp3"
  },
  {
    "number": 99,
    "arabicName": "الزلزلة",
    "englishName": "Az-Zalzalah",
    "ayahCount": 8,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/099%20-%20%D8%A7%D9%84%D8%B2%D9%84%D8%B2%D9%84%D8%A9.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/099.mp3"
  },
  {
    "number": 100,
    "arabicName": "العاديات",
    "englishName": "Al-Adiyat",
    "ayahCount": 11,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/100%20-%20%D8%A7%D9%84%D8%B9%D8%A7%D8%AF%D9%8A%D8%A7%D8%AA.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/100.mp3"
  },
  {
    "number": 101,
    "arabicName": "القارعة",
    "englishName": "Al-Qariah",
    "ayahCount": 11,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/101%20-%20%D8%A7%D9%84%D9%82%D8%A7%D8%B1%D8%B9%D8%A9.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/101.mp3"
  },
  {
    "number": 102,
    "arabicName": "التكاثر",
    "englishName": "At-Takathur",
    "ayahCount": 8,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/102%20-%20%D8%A7%D9%84%D8%AA%D9%83%D8%A7%D8%AB%D8%B1.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/102.mp3"
  },
  {
    "number": 103,
    "arabicName": "العصر",
    "englishName": "Al-Asr",
    "ayahCount": 3,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/103%20-%20%D8%A7%D9%84%D8%B9%D8%B5%D8%B1.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/103.mp3"
  },
  {
    "number": 104,
    "arabicName": "الهمزة",
    "englishName": "Al-Humazah",
    "ayahCount": 9,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/104%20-%20%D8%A7%D9%84%D9%87%D9%85%D8%B2%D8%A9.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/104.mp3"
  },
  {
    "number": 105,
    "arabicName": "الفيل",
    "englishName": "Al-Fil",
    "ayahCount": 5,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/105%20-%20%D8%A7%D9%84%D9%81%D9%8A%D9%84.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/105.mp3"
  },
  {
    "number": 106,
    "arabicName": "قريش",
    "englishName": "Quraysh",
    "ayahCount": 4,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/106%20-%20%D9%82%D8%B1%D9%8A%D8%B4.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/106.mp3"
  },
  {
    "number": 107,
    "arabicName": "الماعون",
    "englishName": "Al-Maun",
    "ayahCount": 7,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/107%20-%20%D8%A7%D9%84%D9%85%D8%A7%D8%B9%D9%88%D9%86.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/107.mp3"
  },
  {
    "number": 108,
    "arabicName": "الكوثر",
    "englishName": "Al-Kawthar",
    "ayahCount": 3,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/108%20-%20%D8%A7%D9%84%D9%83%D9%88%D8%AB%D8%B1.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/108.mp3"
  },
  {
    "number": 109,
    "arabicName": "الكافرون",
    "englishName": "Al-Kafirun",
    "ayahCount": 6,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/109%20-%20%D8%A7%D9%84%D9%83%D8%A7%D9%81%D8%B1%D9%88%D9%86.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/109.mp3"
  },
  {
    "number": 110,
    "arabicName": "النصر",
    "englishName": "An-Nasr",
    "ayahCount": 3,
    "isMakki": false,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/110%20-%20%D8%A7%D9%84%D9%86%D8%B5%D8%B1.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/110.mp3"
  },
  {
    "number": 111,
    "arabicName": "المسد",
    "englishName": "Al-Masad",
    "ayahCount": 5,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/111%20-%20%D8%A7%D9%84%D9%85%D8%B3%D8%AF.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/111.mp3"
  },
  {
    "number": 112,
    "arabicName": "الإخلاص",
    "englishName": "Al-Ikhlas",
    "ayahCount": 4,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/112%20-%20%D8%A7%D9%84%D8%A5%D8%AE%D9%84%D8%A7%D8%B5.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/112.mp3"
  },
  {
    "number": 113,
    "arabicName": "الفلق",
    "englishName": "Al-Falaq",
    "ayahCount": 5,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/113%20-%20%D8%A7%D9%84%D9%81%D9%84%D9%82.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/113.mp3"
  },
  {
    "number": 114,
    "arabicName": "الناس",
    "englishName": "An-Nas",
    "ayahCount": 6,
    "isMakki": true,
    "url1967": "https://archive.org/download/a00ssssss260908ddd/114%20-%20%D8%A7%D9%84%D9%86%D8%A7%D8%B3.mp3",
    "urlFallback": "https://server10.mp3quran.net/minsh/114.mp3"
  }
];
