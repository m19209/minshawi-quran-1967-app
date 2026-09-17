/**
 * مصحف المنشاوي 1967 والأذان الذكي
 * Application Logic & Player Controller
 */

// =========================================================================
// 0. CONFIGURATION — single place for all constants
// =========================================================================
const CONFIG = {
    defaultCityIndex: 0,
    defaultAzanVoice: 'minshawi',
    repeatModes: ['off', 'all', 'one'],
    azanSources: {
        minshawi: 'https://archive.org/download/90---azan---90---azan--many----sound----mp3---alazan/048-.mp3',
        makkah:   'https://archive.org/download/90---azan---90---azan--many----sound----mp3---alazan/019--1.mp3',
        madinah:  'https://archive.org/download/90---azan---90---azan--many----sound----mp3---alazan/018-.mp3'
    }
};

// =========================================================================
// 1. CITIES & PRAYER CALCULATION DATA
// =========================================================================
const CITIES = [
    { country: 'مصر', city: 'القاهرة',       lat: 30.0444, lng: 31.2357, fajrAngle: 19.5, ishaAngle: 17.5 },
    { country: 'مصر', city: 'الإسكندرية',    lat: 31.2001, lng: 29.9187, fajrAngle: 19.5, ishaAngle: 17.5 },
    { country: 'مصر', city: 'الجيزة',        lat: 30.0131, lng: 31.2089, fajrAngle: 19.5, ishaAngle: 17.5 },
    { country: 'مصر', city: 'المنصورة',      lat: 31.0409, lng: 31.3785, fajrAngle: 19.5, ishaAngle: 17.5 },
    { country: 'مصر', city: 'أسوان',         lat: 24.0889, lng: 32.8998, fajrAngle: 19.5, ishaAngle: 17.5 },
    { country: 'المملكة العربية السعودية', city: 'مكة المكرمة',   lat: 21.3891, lng: 39.8579, fajrAngle: 18.5, ishaInterval: 90 },
    { country: 'المملكة العربية السعودية', city: 'المدينة المنورة', lat: 24.5247, lng: 39.5692, fajrAngle: 18.5, ishaInterval: 90 },
    { country: 'المملكة العربية السعودية', city: 'الرياض',        lat: 24.7136, lng: 46.6753, fajrAngle: 18.5, ishaInterval: 90 },
    { country: 'الإمارات', city: 'دبي',      lat: 25.2048, lng: 55.2708, fajrAngle: 18.2, ishaAngle: 18.2 },
    { country: 'الإمارات', city: 'أبوظبي',   lat: 24.4539, lng: 54.3773, fajrAngle: 18.2, ishaAngle: 18.2 },
    { country: 'الأردن',   city: 'عمان',     lat: 31.9454, lng: 35.9284, fajrAngle: 18.0, ishaAngle: 17.0 },
    { country: 'فلسطين',   city: 'القدس الشريف', lat: 31.7683, lng: 35.2137, fajrAngle: 18.0, ishaAngle: 17.0 },
    { country: 'سوريا',    city: 'دمشق',     lat: 33.5138, lng: 36.2765, fajrAngle: 18.5, ishaAngle: 17.5 },
    { country: 'العراق',   city: 'بغداد',    lat: 33.3152, lng: 44.3661, fajrAngle: 18.0, ishaAngle: 17.0 },
    { country: 'الكويت',   city: 'الكويت',   lat: 29.3759, lng: 47.9774, fajrAngle: 18.0, ishaAngle: 17.5 },
    { country: 'المغرب',   city: 'الرباط',   lat: 34.0209, lng: -6.8416, fajrAngle: 19.0, ishaAngle: 17.0 }
];

let currentCity       = CITIES[CONFIG.defaultCityIndex];
let selectedAzanVoice = CONFIG.defaultAzanVoice;
let todayPrayers      = null;

// =========================================================================
// 2. DOM ELEMENTS
// =========================================================================
const quranAudio = document.getElementById('quranAudio');
const azanAudio  = document.getElementById('azanAudio');

const statusClock          = document.getElementById('statusClock');
const currentLocationLabel = document.getElementById('currentLocationLabel');
const hijriDateLabel       = document.getElementById('hijriDateLabel');
const nextPrayerName       = document.getElementById('nextPrayerName');
const prayerCountdown      = document.getElementById('prayerCountdown');
const prayersTimetable     = document.getElementById('prayersTimetable');
const btnSimulateAzan      = document.getElementById('btnSimulateAzan');
const btnChangeLocation    = document.getElementById('btnChangeLocation');
const btnOpenSettings      = document.getElementById('btnOpenSettings');

// Player Elements
const playerSurahNum     = document.getElementById('playerSurahNum');
const playerSurahName    = document.getElementById('playerSurahName');
const playerSurahDetails = document.getElementById('playerSurahDetails');
const audioVisualizer      = document.getElementById('audioVisualizer');
const seekSlider           = document.getElementById('seekSlider');
const timeCurrent          = document.getElementById('timeCurrent');
const timeDuration         = document.getElementById('timeDuration');
const btnPlayPause         = document.getElementById('btnPlayPause');
const playIcon             = document.getElementById('playIcon');
const btnPrev              = document.getElementById('btnPrev');
const btnNext              = document.getElementById('btnNext');
const btnRewind10          = document.getElementById('btnRewind10');
const btnForward10         = document.getElementById('btnForward10');
const btnRepeat            = document.getElementById('btnRepeat');

// Surahs List & Filters
const surahsList       = document.getElementById('surahsList');
const surahSearchInput = document.getElementById('surahSearchInput');
const btnClearSearch   = document.getElementById('btnClearSearch');
const surahCountLabel  = document.getElementById('surahCountLabel');
const filterChips      = document.querySelectorAll('.filter-chips .chip');

// Azan Overlay
const azanOverlay       = document.getElementById('azanOverlay');
const azanPrayerTitle   = document.getElementById('azanPrayerTitle');
const azanReciterName   = document.getElementById('azanReciterName');
const azanProgressFill  = document.getElementById('azanProgressFill');
const btnStopAzanResume = document.getElementById('btnStopAzanResume');

// Settings Modal
const settingsModal    = document.getElementById('settingsModal');
const btnCloseSettings = document.getElementById('btnCloseSettings');
const btnSaveSettings  = document.getElementById('btnSaveSettings');
const citiesPickerList = document.getElementById('citiesPickerList');
const azanVoiceSelect  = document.getElementById('azanVoiceSelect');
const currentCityTitle = document.getElementById('currentCityTitle');
const currentCityMethod = document.getElementById('currentCityMethod');
const cityFilterChips  = document.getElementById('cityFilterChips');
let activeCountryFilter = 'all';

// =========================================================================
// 3. APPLICATION STATE
// =========================================================================
let currentSurahIndex = 0;
let isPlaying   = false;
let repeatIndex = 0;
let activeFilter = 'all';

// Smart Azan interruption state
let wasPlayingBeforeAzan = false;
let savedQuranPosition   = 0;
let isAzanActive         = false;

// =========================================================================
// 4. SVG ICON HELPERS
// =========================================================================

/** Play triangle */
function svgPlay(size) {
    size = size || 26;
    return '<svg viewBox="0 0 24 24" width="' + size + '" height="' + size + '" fill="currentColor" aria-hidden="true"><path d="M8 5v14l11-7z"/></svg>';
}

/** Pause bars */
function svgPause(size) {
    size = size || 26;
    return '<svg viewBox="0 0 24 24" width="' + size + '" height="' + size + '" fill="currentColor" aria-hidden="true"><path d="M6 19h4V5H6v14zm8-14v14h4V5h-4z"/></svg>';
}

/** Download icon */
function svgDownload(size) {
    size = size || 16;
    return '<svg viewBox="0 0 24 24" width="' + size + '" height="' + size + '" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>';
}

/** Repeat arrows, optionally with "1" badge for single-surah mode */
function svgRepeat(withOne) {
    var label = withOne ? '<text x="12" y="14.5" font-size="6.5" font-family="Arial,sans-serif" font-weight="bold" fill="currentColor" stroke="none" text-anchor="middle">1</text>' : '';
    return '<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="M17 1l4 4-4 4"/><path d="M3 11V9a4 4 0 0 1 4-4h14"/><path d="M7 23l-4-4 4-4"/><path d="M21 13v2a4 4 0 0 1-4 4H3"/>' + label + '</svg>';
}

// =========================================================================
// 5. UTILITY HELPERS & TIME FORMATTING
// =========================================================================

/** Formats seconds as MM:SS (or HH:MM:SS if >= 3600) safely */
function formatAudioTime(secs) {
    if (!isFinite(secs) || secs < 0) return '00:00';
    var totalSecs = Math.floor(secs);
    var h = Math.floor(totalSecs / 3600);
    var m = Math.floor((totalSecs % 3600) / 60);
    var s = totalSecs % 60;
    if (h > 0) {
        return String(h).padStart(2, '0') + ':' + String(m).padStart(2, '0') + ':' + String(s).padStart(2, '0');
    }
    return String(m).padStart(2, '0') + ':' + String(s).padStart(2, '0');
}

/**
 * Returns the best available duration for current Surah:
 * Uses HTML5 audio.duration if valid and finite, otherwise falls back to
 * the exact pre-computed duration from SURAHS_DATA (e.g. 50s for Al-Fatihah, 10187s for Al-Baqarah)
 */
function getEffectiveDuration(dur) {
    if (isFinite(dur) && dur > 0) return dur;
    var surah = SURAHS_DATA[currentSurahIndex];
    if (surah && isFinite(surah.duration) && surah.duration > 0) return surah.duration;
    return 0;
}

/**
 * Updates the time indicators in full harmony:
 * - timeCurrent counts UP: 00:00, 00:01, 00:02...
 * - timeDuration counts DOWN from the Surah's original total duration:
 *   e.g. for Al-Fatihah (50s): 00:50, 00:49, 00:48... down to 00:00 (without minus sign)
 */
function updateTimeDisplay(cur, dur) {
    var effectiveDur = getEffectiveDuration(dur);
    timeCurrent.textContent = formatAudioTime(cur);
    var remaining = Math.max(0, effectiveDur - cur);
    timeDuration.textContent = formatAudioTime(remaining);
}

/** Debounce utility */
function debounce(fn, delay) {
    delay = delay || 200;
    var timer;
    return function () {
        var args = arguments;
        clearTimeout(timer);
        timer = setTimeout(function () { fn.apply(null, args); }, delay);
    };
}

/** Download recitation to user's device */
function triggerDownloadSurah(surahIndex, btnEl) {
    var surah = SURAHS_DATA[surahIndex];
    if (!surah) return;
    var url = surah.url1967 || surah.urlFallback;
    var filename = 'المنشاوي_1967_سورة_' + surah.arabicName + '_' + String(surah.number).padStart(3, '0') + '.mp3';

    if (btnEl) {
        btnEl.classList.add('downloading');
    }

    var a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.target = '_blank';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);

    setTimeout(function () {
        if (btnEl) {
            btnEl.classList.remove('downloading');
        }
    }, 2500);
}

// =========================================================================
// 6. ASTRONOMICAL PRAYER CALCULATIONS
// =========================================================================
function degToRad(deg) { return deg * Math.PI / 180.0; }
function radToDeg(rad) { return rad * 180.0 / Math.PI; }

function calculatePrayersForDate(date, city) {
    var lat      = city.lat;
    var lng      = city.lng;
    var timeZone = -date.getTimezoneOffset() / 60;

    var start     = new Date(date.getFullYear(), 0, 0);
    var dayOfYear = Math.floor((date - start) / (1000 * 60 * 60 * 24));

    var B           = 2 * Math.PI * (dayOfYear - 81) / 365;
    var EoT         = 9.87 * Math.sin(2 * B) - 7.53 * Math.cos(B) - 1.5 * Math.sin(B);
    var declination = 23.45 * Math.sin(degToRad(360 / 365 * (dayOfYear - 81)));
    var solarNoon   = 12 + (4 * (timeZone * 15 - lng) - EoT) / 60;

    function getHourAngle(angle) {
        var cosH = (Math.sin(degToRad(angle)) - Math.sin(degToRad(lat)) * Math.sin(degToRad(declination))) /
                   (Math.cos(degToRad(lat)) * Math.cos(degToRad(declination)));
        if (cosH > 1 || cosH < -1) return 0;
        return radToDeg(Math.acos(cosH)) / 15.0;
    }

    var asrAlt    = radToDeg(Math.atan(1.0 / (1.0 + Math.tan(degToRad(Math.abs(lat - declination))))));
    var asrHA     = getHourAngle(asrAlt);
    var fajrHA    = getHourAngle(-city.fajrAngle);
    var sunriseHA = getHourAngle(-0.833);
    var ishaHA    = city.ishaAngle ? getHourAngle(-city.ishaAngle) : null;

    var maghribTime = solarNoon + sunriseHA;
    var ishaTime    = ishaHA
        ? (solarNoon + ishaHA)
        : (maghribTime + (city.ishaInterval || 90) / 60);

    function toDateObj(decimalHours) {
        var d    = new Date(date);
        var h    = Math.floor(decimalHours);
        var mins = Math.floor((decimalHours - h) * 60);
        var secs = Math.floor(((decimalHours - h) * 60 - mins) * 60);
        d.setHours(h, mins, secs, 0);
        return d;
    }

    return [
        { name: 'الفجر',  date: toDateObj(solarNoon - fajrHA) },
        { name: 'الشروق', date: toDateObj(solarNoon - sunriseHA) },
        { name: 'الظهر',  date: toDateObj(solarNoon) },
        { name: 'العصر',  date: toDateObj(solarNoon + asrHA) },
        { name: 'المغرب', date: toDateObj(maghribTime) },
        { name: 'العشاء', date: toDateObj(ishaTime) }
    ];
}

function formatArabicTime(date) {
    var hours   = date.getHours();
    var minutes = String(date.getMinutes()).padStart(2, '0');
    var isPm    = hours >= 12;
    hours = hours % 12 || 12;
    return hours + ':' + minutes + ' ' + (isPm ? 'م' : 'ص');
}

function getFormattedHijriDate() {
    try {
        var d = new Date();
        var formatter = new Intl.DateTimeFormat('ar-TN-u-ca-islamic-umalqura', {
            day: 'numeric',
            month: 'long',
            year: 'numeric'
        });
        var str = formatter.format(d);
        if (!str.includes('هـ')) str += ' هـ';
        return str;
    } catch (e) {
        return 'التقويم الهجري';
    }
}

function updatePrayerTimes() {
    var now = new Date();
    todayPrayers = calculatePrayersForDate(now, currentCity);

    var tomorrow = new Date(now);
    tomorrow.setDate(now.getDate() + 1);
    var tomorrowPrayers = calculatePrayersForDate(tomorrow, currentCity);

    var currentPrayer = todayPrayers[5];
    var nextPrayer    = null;

    for (var i = 0; i < todayPrayers.length; i++) {
        if (now < todayPrayers[i].date) {
            nextPrayer    = todayPrayers[i];
            currentPrayer = (i > 0) ? todayPrayers[i - 1] : todayPrayers[5];
            break;
        }
    }

    if (!nextPrayer) {
        nextPrayer    = tomorrowPrayers[0];
        currentPrayer = todayPrayers[5];
    }

    var nextAzanPrayer = nextPrayer;
    if (nextPrayer.name === 'الشروق') {
        nextAzanPrayer = todayPrayers[2];
    }

    currentLocationLabel.textContent = currentCity.country + ' - ' + currentCity.city;
    if (hijriDateLabel) {
        hijriDateLabel.textContent = getFormattedHijriDate();
    }
    nextPrayerName.textContent       = nextAzanPrayer.name;

    var diffMs    = Math.max(0, nextAzanPrayer.date - now);
    var totalSecs = Math.floor(diffMs / 1000);
    var hh = String(Math.floor(totalSecs / 3600)).padStart(2, '0');
    var mm = String(Math.floor((totalSecs % 3600) / 60)).padStart(2, '0');
    var ss = String(Math.floor(totalSecs % 60)).padStart(2, '0');
    prayerCountdown.textContent = hh + ':' + mm + ':' + ss;

    var prayerStateKey = currentCity.city + '_' + nextAzanPrayer.name;
    if (window._lastPrayerStateKey !== prayerStateKey) {
        window._lastPrayerStateKey = prayerStateKey;
        prayersTimetable.innerHTML = todayPrayers.map(function (p) {
            var isNext = (p.name === nextAzanPrayer.name);
            return '<div class="prayer-col ' + (isNext ? 'active-next' : '') + '">' +
                   '<div class="prayer-name">' + p.name + '</div>' +
                   '<div class="prayer-time">' + formatArabicTime(p.date) + '</div></div>';
        }).join('');
    }

    if (totalSecs === 0 && !isAzanActive) {
        triggerAzan(nextAzanPrayer.name);
    }
}

// =========================================================================
// 7. SMART AZAN INTERRUPTION & AUTO-RESUME
// =========================================================================

function triggerAzan(prayerName) {
    prayerName = prayerName || 'الظهر';
    if (isAzanActive) return;
    isAzanActive = true;

    wasPlayingBeforeAzan = !quranAudio.paused && quranAudio.currentTime > 0;
    if (wasPlayingBeforeAzan) {
        savedQuranPosition = quranAudio.currentTime;
        quranAudio.pause();
        updatePlayState(false);
    }

    azanPrayerTitle.textContent  = 'حان الآن موعد أذان ' + prayerName;
    
    var azanVoiceLabels = {
        minshawi: 'بصوت الشيخ محمد صديق المنشاوي رحمه الله',
        makkah:   'بصوت مؤذن الحرم المكي الشريف',
        madinah:  'بصوت مؤذن المسجد النبوي الشريف'
    };
    if (azanReciterName) {
        azanReciterName.textContent = azanVoiceLabels[selectedAzanVoice] || azanVoiceLabels.minshawi;
    }

    azanOverlay.classList.add('show');
    azanProgressFill.style.width = '0%';

    var azanUrl = CONFIG.azanSources[selectedAzanVoice] || CONFIG.azanSources.minshawi;
    azanAudio.src         = azanUrl;
    azanAudio.currentTime = 0;
    azanAudio.play().catch(function (e) { console.warn('Azan play note:', e); });
}

function stopAzanAndResumeRecitation() {
    if (!isAzanActive) return;
    azanAudio.pause();
    azanAudio.currentTime = 0;
    azanOverlay.classList.remove('show');
    isAzanActive = false;

    if (wasPlayingBeforeAzan) {
        quranAudio.currentTime = savedQuranPosition;
        quranAudio.play().then(function () {
            updatePlayState(true);
            wasPlayingBeforeAzan = false;
        }).catch(function (err) { console.error('Resume error:', err); });
    }
}

azanAudio.addEventListener('timeupdate', function () {
    if (azanAudio.duration && isFinite(azanAudio.duration)) {
        azanProgressFill.style.width = ((azanAudio.currentTime / azanAudio.duration) * 100) + '%';
    }
});

azanAudio.addEventListener('ended', stopAzanAndResumeRecitation);
btnStopAzanResume.addEventListener('click', stopAzanAndResumeRecitation);
btnSimulateAzan.addEventListener('click', function () {
    if (settingsModal) settingsModal.classList.remove('show');
    triggerAzan(nextPrayerName.textContent || 'الظهر');
});

// =========================================================================
// 8. QURAN PLAYER CONTROLLER (1967 PURE EDITION)
// =========================================================================

function updateMediaSession(surah) {
    if ('mediaSession' in navigator) {
        navigator.mediaSession.metadata = new MediaMetadata({
            title: 'سورة ' + surah.arabicName,
            artist: 'الشيخ محمد صديق المنشاوي',
            album: 'ختمة 1967 النقية النادرة',
            artwork: [
                { src: 'https://archive.org/download/a00ssssss260908ddd/cover.jpg', sizes: '512x512', type: 'image/jpeg' }
            ]
        });

        navigator.mediaSession.setActionHandler('play', function () {
            togglePlay();
        });
        navigator.mediaSession.setActionHandler('pause', function () {
            togglePlay();
        });
        navigator.mediaSession.setActionHandler('previoustrack', function () {
            loadSurah((currentSurahIndex - 1 + SURAHS_DATA.length) % SURAHS_DATA.length, true);
        });
        navigator.mediaSession.setActionHandler('nexttrack', function () {
            loadSurah((currentSurahIndex + 1) % SURAHS_DATA.length, true);
        });
        navigator.mediaSession.setActionHandler('seekbackward', function () {
            quranAudio.currentTime = Math.max(0, quranAudio.currentTime - 10);
            updateTimeDisplay(quranAudio.currentTime, quranAudio.duration);
        });
        navigator.mediaSession.setActionHandler('seekforward', function () {
            var dur = getEffectiveDuration(quranAudio.duration);
            quranAudio.currentTime = Math.min(dur > 0 ? dur : 999999, quranAudio.currentTime + 10);
            updateTimeDisplay(quranAudio.currentTime, dur);
        });
        try {
            navigator.mediaSession.setActionHandler('seekto', function (details) {
                if (details.seekTime !== undefined) {
                    quranAudio.currentTime = details.seekTime;
                    updateTimeDisplay(quranAudio.currentTime, quranAudio.duration);
                }
            });
        } catch (e) {}
    }
}

function loadSurah(index, autoPlay) {
    autoPlay = autoPlay || false;
    if (index < 0 || index >= SURAHS_DATA.length) return;
    currentSurahIndex = index;
    var surah = SURAHS_DATA[index];

    playerSurahNum.textContent     = String(surah.number).padStart(3, '0');
    playerSurahName.textContent    = 'سورة ' + surah.arabicName;
    playerSurahDetails.textContent = surah.englishName + ' \u2022 ' + (surah.isMakki ? 'مكية' : 'مدنية') +
                                     ' (' + surah.ayahCount + ' آية) \u2022 ختمة 1967';

    seekSlider.setAttribute('aria-label', 'موضع التلاوة - سورة ' + surah.arabicName);

    updateMediaSession(surah);

    document.querySelectorAll('.surah-item').forEach(function (el, idx) {
        el.classList.toggle('current-active', idx === index);
    });

    quranAudio.src          = surah.url1967;
    quranAudio.playbackRate = 1.0;
    seekSlider.value        = 0;
    seekSlider.setAttribute('aria-valuenow', 0);
    // Immediately set start time: 00:00 on left, full original duration on right (counting down)
    updateTimeDisplay(0, surah.duration);

    if (autoPlay) {
        quranAudio.play().then(function () {
            updatePlayState(true);
        }).catch(function () {
            console.warn('Falling back to CDN mirror for Surah', surah.number);
            quranAudio.src = surah.urlFallback;
            quranAudio.play().then(function () { updatePlayState(true); });
        });
    } else {
        updatePlayState(false);
    }
}

function updatePlayState(playing) {
    isPlaying = playing;
    if (playIcon) {
        playIcon.innerHTML = playing ? svgPause(26) : svgPlay(26);
    }
    btnPlayPause.setAttribute('aria-label', playing ? 'إيقاف مؤقت' : 'تشغيل');
    if (audioVisualizer) {
        audioVisualizer.classList.toggle('active', playing);
    }
    if ('mediaSession' in navigator) {
        navigator.mediaSession.playbackState = playing ? 'playing' : 'paused';
    }

    // Fast targeted in-place DOM update without re-rendering 114 HTML items
    var items = surahsList.querySelectorAll('.surah-item');
    items.forEach(function (el) {
        var idx = parseInt(el.dataset.index);
        var isCurrent = (idx === currentSurahIndex);
        el.classList.toggle('current-active', isCurrent);
        var playBtn = el.querySelector('.btn-item-play');
        if (playBtn) {
            playBtn.innerHTML = (isCurrent && playing) ? svgPause(15) : svgPlay(15);
        }
    });
}

function togglePlay() {
    if (quranAudio.paused) {
        if (!quranAudio.src || quranAudio.src === window.location.href) {
            loadSurah(currentSurahIndex, true);
        } else {
            quranAudio.play().then(function () { updatePlayState(true); });
        }
    } else {
        quranAudio.pause();
        updatePlayState(false);
    }
}

// Immediately update duration when metadata is ready
quranAudio.addEventListener('loadedmetadata', function () {
    var cur = quranAudio.currentTime || 0;
    updateTimeDisplay(cur, quranAudio.duration);
});

quranAudio.addEventListener('durationchange', function () {
    var cur = quranAudio.currentTime || 0;
    updateTimeDisplay(cur, quranAudio.duration);
});

// Quran audio timeupdate — counts up on left, counts down from original duration on right
quranAudio.addEventListener('timeupdate', function () {
    var dur = getEffectiveDuration(quranAudio.duration);
    if (!dur || dur <= 0) return;
    var cur = quranAudio.currentTime || 0;
    var pct = Math.min(100, (cur / dur) * 100);
    seekSlider.value = pct;
    seekSlider.setAttribute('aria-valuenow', Math.round(pct));
    updateTimeDisplay(cur, dur);
});

quranAudio.addEventListener('ended', function () {
    var mode = CONFIG.repeatModes[repeatIndex];
    if (mode === 'one') {
        quranAudio.currentTime = 0;
        quranAudio.play();
    } else if (mode === 'all') {
        loadSurah((currentSurahIndex + 1) % SURAHS_DATA.length, true);
    } else {
        if (currentSurahIndex < SURAHS_DATA.length - 1) {
            loadSurah(currentSurahIndex + 1, true);
        } else {
            updatePlayState(false);
        }
    }
});

seekSlider.addEventListener('input', function () {
    var dur = getEffectiveDuration(quranAudio.duration);
    if (!dur || dur <= 0) return;
    var newTime = (seekSlider.value / 100) * dur;
    quranAudio.currentTime = newTime;
    updateTimeDisplay(newTime, dur);
});

btnPlayPause.addEventListener('click', togglePlay);

btnPrev.addEventListener('click', function () {
    loadSurah((currentSurahIndex - 1 + SURAHS_DATA.length) % SURAHS_DATA.length, true);
});

btnNext.addEventListener('click', function () {
    loadSurah((currentSurahIndex + 1) % SURAHS_DATA.length, true);
});

btnRewind10.addEventListener('click', function () {
    quranAudio.currentTime = Math.max(0, quranAudio.currentTime - 10);
    updateTimeDisplay(quranAudio.currentTime, quranAudio.duration);
});

btnForward10.addEventListener('click', function () {
    var dur = getEffectiveDuration(quranAudio.duration);
    quranAudio.currentTime = Math.min(dur > 0 ? dur : 999999, quranAudio.currentTime + 10);
    updateTimeDisplay(quranAudio.currentTime, dur);
});

btnRepeat.addEventListener('click', function () {
    repeatIndex = (repeatIndex + 1) % CONFIG.repeatModes.length;
    var mode    = CONFIG.repeatModes[repeatIndex];
    if (mode === 'off') {
        btnRepeat.className = 'player-btn-circle';
        btnRepeat.title     = 'تكرار: معطل';
        btnRepeat.innerHTML = svgRepeat(false);
    } else if (mode === 'all') {
        btnRepeat.className = 'player-btn-circle active-repeat';
        btnRepeat.title     = 'تكرار: كل السور';
        btnRepeat.innerHTML = svgRepeat(false);
    } else {
        btnRepeat.className = 'player-btn-circle active-repeat';
        btnRepeat.title     = 'تكرار: نفس السورة';
        btnRepeat.innerHTML = svgRepeat(true);
    }
});

// =========================================================================
// 9. SURAHS LIST RENDERING & SEARCH
// =========================================================================

function renderSurahs() {
    var query = surahSearchInput.value.trim().toLowerCase();

    var filtered = SURAHS_DATA.filter(function (s) {
        var matchesQuery = !query ||
            s.arabicName.includes(query) ||
            s.englishName.toLowerCase().includes(query) ||
            String(s.number) === query;

        var matchesType = true;
        if (activeFilter === 'makki')  matchesType = s.isMakki;
        if (activeFilter === 'madani') matchesType = !s.isMakki;

        return matchesQuery && matchesType;
    });

    surahCountLabel.textContent = filtered.length + ' سورة';

    if (filtered.length === 0) {
        surahsList.innerHTML = '<div style="text-align:center;padding:30px;color:var(--text-muted)">لا توجد سور مطابقة لبحثك</div>';
        return;
    }

    surahsList.innerHTML = filtered.map(function (surah) {
        var isCurrent = (surah.number - 1) === currentSurahIndex;
        var playState = (isCurrent && isPlaying) ? svgPause(15) : svgPlay(15);
        var playLabel = (isCurrent && isPlaying ? 'إيقاف مؤقت' : 'تشغيل') + ' سورة ' + surah.arabicName;
        var typeLabel = surah.isMakki ? 'مكية' : 'مدنية';

        return '<div class="surah-item ' + (isCurrent ? 'current-active' : '') + '" data-index="' + (surah.number - 1) + '">' +
            '<div class="surah-right">' +
            '<div class="surah-num-badge">' + surah.number + '</div>' +
            '<div class="surah-names-col">' +
            '<h4>سورة ' + surah.arabicName + '</h4>' +
            '<div class="surah-meta-text">' + surah.englishName + ' \u2022 ' + typeLabel + ' (' + surah.ayahCount + ' آية)</div>' +
            '</div></div>' +
            '<button class="btn-item-play" data-index="' + (surah.number - 1) + '" aria-label="' + playLabel + '">' + playState + '</button>' +
            '</div>';
    }).join('');

    surahsList.querySelectorAll('.surah-item').forEach(function (el) {
        el.addEventListener('click', function () {
            var idx = parseInt(el.dataset.index);
            if (idx === currentSurahIndex) { togglePlay(); } else { loadSurah(idx, true); }
        });
    });
}

// Debounced search
var handleSearch = debounce(function () {
    btnClearSearch.style.display = surahSearchInput.value ? 'block' : 'none';
    renderSurahs();
}, 200);

surahSearchInput.addEventListener('input', handleSearch);

btnClearSearch.addEventListener('click', function () {
    surahSearchInput.value       = '';
    btnClearSearch.style.display = 'none';
    renderSurahs();
});

filterChips.forEach(function (chip) {
    chip.addEventListener('click', function () {
        filterChips.forEach(function (c) { c.classList.remove('active'); });
        chip.classList.add('active');
        activeFilter = chip.dataset.filter;
        renderSurahs();
    });
});

// =========================================================================
// 10. SETTINGS & LOCATION MODAL
// =========================================================================

function renderCitiesPicker() {
    if (currentCityTitle) {
        currentCityTitle.textContent = currentCity.country + ' - ' + currentCity.city;
    }
    if (currentCityMethod) {
        var methodDesc = 'طريقة الحساب: ' + (currentCity.country.includes('السعودية') ? 'أم القرى (مكة المكرمة)' : (currentCity.country === 'مصر' ? 'الهيئة المصرية العامة للمساحة' : 'رابطة العالم الإسلامي'));
        currentCityMethod.textContent = methodDesc;
    }

    var filtered = CITIES.map(function (c, idx) { return { city: c, idx: idx }; }).filter(function (item) {
        if (activeCountryFilter === 'all') return true;
        if (activeCountryFilter === 'مصر') return item.city.country === 'مصر';
        if (activeCountryFilter === 'السعودية') return item.city.country.includes('السعودية');
        if (activeCountryFilter === 'الإمارات') return item.city.country.includes('الإمارات');
        if (activeCountryFilter === 'other') return item.city.country !== 'مصر' && !item.city.country.includes('السعودية') && !item.city.country.includes('الإمارات');
        return true;
    });

    citiesPickerList.innerHTML = filtered.map(function (entry) {
        var c = entry.city;
        var idx = entry.idx;
        var isSelected = (c.city === currentCity.city && c.country === currentCity.country);
        return '<div class="city-pick-item ' + (isSelected ? 'selected' : '') + '" data-city-idx="' + idx + '" ' +
               'role="option" aria-selected="' + isSelected + '">' +
               '<span>' + c.country + ' - ' + c.city + '</span><span>' + (isSelected ? '\u2713' : '') + '</span></div>';
    }).join('');

    citiesPickerList.querySelectorAll('.city-pick-item').forEach(function (el) {
        el.addEventListener('click', function () {
            currentCity = CITIES[parseInt(el.dataset.cityIdx)];
            renderCitiesPicker();
            updatePrayerTimes();
        });
    });
}

if (cityFilterChips) {
    cityFilterChips.querySelectorAll('.chip').forEach(function (chip) {
        chip.addEventListener('click', function () {
            cityFilterChips.querySelectorAll('.chip').forEach(function (c) { c.classList.remove('active'); });
            chip.classList.add('active');
            activeCountryFilter = chip.dataset.country;
            renderCitiesPicker();
        });
    });
}

btnChangeLocation.addEventListener('click', function () {
    renderCitiesPicker();
    if (azanVoiceSelect) azanVoiceSelect.value = selectedAzanVoice;
    settingsModal.classList.add('show');
});
btnOpenSettings.addEventListener('click', function () {
    renderCitiesPicker();
    if (azanVoiceSelect) azanVoiceSelect.value = selectedAzanVoice;
    settingsModal.classList.add('show');
});
btnCloseSettings.addEventListener('click',  function () { settingsModal.classList.remove('show'); });
btnSaveSettings.addEventListener('click',   function () {
    if (azanVoiceSelect) selectedAzanVoice = azanVoiceSelect.value;
    settingsModal.classList.remove('show');
});
if (azanVoiceSelect) {
    azanVoiceSelect.addEventListener('change', function () {
        selectedAzanVoice = azanVoiceSelect.value;
    });
}

// =========================================================================
// 11. INITIALIZATION & LIVE CLOCK TICKER
// =========================================================================
function init() {
    loadSurah(0, false);
    renderSurahs();
    updatePrayerTimes();

    setInterval(function () {
        var now = new Date();
        var hh  = String(now.getHours() % 12 || 12).padStart(2, '0');
        var mm  = String(now.getMinutes()).padStart(2, '0');
        statusClock.textContent = hh + ':' + mm;
        updatePrayerTimes();
    }, 1000);
}

document.addEventListener('DOMContentLoaded', init);