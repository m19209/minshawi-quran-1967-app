import json

surahs_meta = [
    (1, 'الفاتحة', 'Al-Fatihah', 7, True),
    (2, 'البقرة', 'Al-Baqarah', 286, False),
    (3, 'آل عمران', 'Ali Imran', 200, False),
    (4, 'النساء', 'An-Nisa', 176, False),
    (5, 'المائدة', 'Al-Maidah', 120, False),
    (6, 'الأنعام', 'Al-Anam', 165, True),
    (7, 'الأعراف', 'Al-Araf', 206, True),
    (8, 'الأنفال', 'Al-Anfal', 75, False),
    (9, 'التوبة', 'At-Tawbah', 129, False),
    (10, 'يونس', 'Yunus', 109, True),
    (11, 'هود', 'Hud', 123, True),
    (12, 'يوسف', 'Yusuf', 111, True),
    (13, 'الرعد', 'Ar-Rad', 43, False),
    (14, 'إبراهيم', 'Ibrahim', 52, True),
    (15, 'الحجر', 'Al-Hijr', 99, True),
    (16, 'النحل', 'An-Nahl', 128, True),
    (17, 'الإسراء', 'Al-Isra', 111, True),
    (18, 'الكهف', 'Al-Kahf', 110, True),
    (19, 'مريم', 'Maryam', 98, True),
    (20, 'طه', 'Taha', 135, True),
    (21, 'الأنبياء', 'Al-Anbiya', 112, True),
    (22, 'الحج', 'Al-Hajj', 78, False),
    (23, 'المؤمنون', 'Al-Muminun', 118, True),
    (24, 'النور', 'An-Nur', 64, False),
    (25, 'الفرقان', 'Al-Furqan', 77, True),
    (26, 'الشعراء', 'Ash-Shuara', 227, True),
    (27, 'النمل', 'An-Naml', 93, True),
    (28, 'القصص', 'Al-Qasas', 88, True),
    (29, 'العنكبوت', 'Al-Ankabut', 69, True),
    (30, 'الروم', 'Ar-Rum', 60, True),
    (31, 'لقمان', 'Luqman', 34, True),
    (32, 'السجدة', 'As-Sajdah', 30, True),
    (33, 'الأحزاب', 'Al-Ahzab', 73, False),
    (34, 'سبأ', 'Saba', 54, True),
    (35, 'فاطر', 'Fatir', 45, True),
    (36, 'يس', 'Ya-Sin', 83, True),
    (37, 'الصافات', 'As-Saffat', 182, True),
    (38, 'ص', 'Sad', 88, True),
    (39, 'الزمر', 'Az-Zumar', 75, True),
    (40, 'غافر', 'Ghafir', 85, True),
    (41, 'فصلت', 'Fussilat', 54, True),
    (42, 'الشورى', 'Ash-Shura', 53, True),
    (43, 'الزخرف', 'Az-Zukhruf', 89, True),
    (44, 'الدخان', 'Ad-Dukhan', 59, True),
    (45, 'الجاثية', 'Al-Jathiyah', 37, True),
    (46, 'الأحقاف', 'Al-Ahqaf', 35, True),
    (47, 'محمد', 'Muhammad', 38, False),
    (48, 'الفتح', 'Al-Fath', 29, False),
    (49, 'الحجرات', 'Al-Hujurat', 18, False),
    (50, 'ق', 'Qaf', 45, True),
    (51, 'الذاريات', 'Adh-Dhariyat', 60, True),
    (52, 'الطور', 'At-Tur', 49, True),
    (53, 'النجم', 'An-Najm', 62, True),
    (54, 'القمر', 'Al-Qamar', 55, True),
    (55, 'الرحمن', 'Ar-Rahman', 78, False),
    (56, 'الواقعة', 'Al-Waqiah', 96, True),
    (57, 'الحديد', 'Al-Hadid', 29, False),
    (58, 'المجادلة', 'Al-Mujadila', 22, False),
    (59, 'الحشر', 'Al-Hashr', 24, False),
    (60, 'الممتحنة', 'Al-Mumtahanah', 13, False),
    (61, 'الصف', 'As-Saff', 14, False),
    (62, 'الجمعة', 'Al-Jumuah', 11, False),
    (63, 'المنافقون', 'Al-Munafiqun', 11, False),
    (64, 'التغابن', 'At-Taghabun', 18, False),
    (65, 'الطلاق', 'At-Talaq', 12, False),
    (66, 'التحريم', 'At-Tahrim', 12, False),
    (67, 'الملك', 'Al-Mulk', 30, True),
    (68, 'القلم', 'Al-Qalam', 52, True),
    (69, 'الحاقة', 'Al-Haqqah', 52, True),
    (70, 'المعارج', 'Al-Maarij', 44, True),
    (71, 'نوح', 'Nuh', 28, True),
    (72, 'الجن', 'Al-Jinn', 28, True),
    (73, 'المزمل', 'Al-Muzzammil', 20, True),
    (74, 'المدثر', 'Al-Muddaththir', 56, True),
    (75, 'القيامة', 'Al-Qiyamah', 40, True),
    (76, 'الإنسان', 'Al-Insan', 31, False),
    (77, 'المرسلات', 'Al-Mursalat', 50, True),
    (78, 'النبأ', 'An-Naba', 40, True),
    (79, 'النازعات', 'An-Naziat', 46, True),
    (80, 'عبس', 'Abasa', 42, True),
    (81, 'التكوير', 'At-Takwir', 29, True),
    (82, 'الانفطار', 'Al-Infitar', 19, True),
    (83, 'المطففين', 'Al-Mutaffifin', 36, True),
    (84, 'الانشقاق', 'Al-Inshiqaq', 25, True),
    (85, 'البروج', 'Al-Buruj', 22, True),
    (86, 'الطارق', 'At-Tariq', 17, True),
    (87, 'الأعلى', 'Al-Ala', 19, True),
    (88, 'الغاشية', 'Al-Ghashiyah', 26, True),
    (89, 'الفجر', 'Al-Fajr', 30, True),
    (90, 'البلد', 'Al-Balad', 20, True),
    (91, 'الشمس', 'Ash-Shams', 15, True),
    (92, 'الليل', 'Al-Layl', 21, True),
    (93, 'الضحى', 'Ad-Duha', 11, True),
    (94, 'الشرح', 'Ash-Sharh', 8, True),
    (95, 'التين', 'At-Tin', 8, True),
    (96, 'العلق', 'Al-Alaq', 19, True),
    (97, 'القدر', 'Al-Qadr', 5, True),
    (98, 'البينة', 'Al-Bayyinah', 8, False),
    (99, 'الزلزلة', 'Az-Zalzalah', 8, False),
    (100, 'العاديات', 'Al-Adiyat', 11, True),
    (101, 'القارعة', 'Al-Qariah', 11, True),
    (102, 'التكاثر', 'At-Takathur', 8, True),
    (103, 'العصر', 'Al-Asr', 3, True),
    (104, 'الهمزة', 'Al-Humazah', 9, True),
    (105, 'الفيل', 'Al-Fil', 5, True),
    (106, 'قريش', 'Quraysh', 4, True),
    (107, 'الماعون', 'Al-Maun', 7, True),
    (108, 'الكوثر', 'Al-Kawthar', 3, True),
    (109, 'الكافرون', 'Al-Kafirun', 6, True),
    (110, 'النصر', 'An-Nasr', 3, False),
    (111, 'المسد', 'Al-Masad', 5, True),
    (112, 'الإخلاص', 'Al-Ikhlas', 4, True),
    (113, 'الفلق', 'Al-Falaq', 5, True),
    (114, 'الناس', 'An-Nas', 6, True)
]

archive_files = json.load(open(r'C:\Users\مصطفى\.gemini\antigravity\scratch\minshawi-quran-azan-app\surahs_archive_files.json', encoding='utf-8'))

lines = ['package com.minshawi.quran1967.data', '', 'object QuranRepository {', '    val surahs: List<Surah> = listOf(']

for num, ar, en, ayahs, is_makki in surahs_meta:
    fn = archive_files.get(str(num), f'{num:03d} - {ar}.mp3')
    fn_escaped = fn.replace('"', '\\"')
    lines.append(f'        Surah({num}, "{ar}", "{en}", {ayahs}, {str(is_makki).lower()}, "{fn_escaped}"),')

lines.append('    )')
lines.append('')
lines.append('    fun getSurah(number: Int): Surah? = surahs.find { it.number == number }')
lines.append('')
lines.append('    fun searchSurahs(query: String): List<Surah> {')
lines.append('        if (query.isBlank()) return surahs')
lines.append('        val clean = query.trim().lowercase()')
lines.append('        return surahs.filter {')
lines.append('            it.arabicName.contains(clean) || it.englishName.lowercase().contains(clean) || it.number.toString() == clean')
lines.append('        }')
lines.append('    }')
lines.append('}')

out_path = r'C:\Users\مصطفى\.gemini\antigravity\scratch\minshawi-quran-azan-app\android\app\src\main\java\com\minshawi\quran1967\data\QuranRepository.kt'
with open(out_path, 'w', encoding='utf-8') as f:
    f.write('\n'.join(lines))
print("Successfully generated QuranRepository.kt with 114 surahs")
