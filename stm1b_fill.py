"""Fill LHDN STM1B (Lampiran C - List of Movement In/Out of Malaysia) for Purav Bhatt.

Generates one form per year of assessment (2019-2025). Trips spanning a
year boundary are split at 31 Dec: the earlier year shows the entry with a
31.12.YYYY exit, the later year shows a 01.01.YYYY entry with the real exit.
Page numbers refer to the passport holding the stamp (old L5086277 until
Dec 2022, then Z6777485).
"""
import pymupdf
from datetime import date

SRC = r"C:\Users\Purav Bhatt\Downloads\STM1B_07012020.pdf"
OUT_TMPL = r"C:\Users\Purav Bhatt\Downloads\STM1B_YA{ya}_Purav_Bhatt.pdf"

NAME = "PURAV BHAGIRATHBHAI BHATT"
PASSPORT = "L5086277 / Z6777485"

# (entry, entry page, exit, exit page)  exit "" = still in Malaysia
TRIPS = [
    ("28.02.2019", "24", "28.06.2019", "22"),
    ("07.07.2019", "22", "31.07.2019", "22"),
    ("13.08.2019", "26", "25.10.2019", "26"),
    ("04.11.2019", "26", "14.02.2020", "27"),
    ("17.02.2020", "23", "01.07.2021", "30"),
    ("06.01.2022", "30", "20.04.2022", "32"),
    ("12.05.2022", "32", "03.12.2022", "4"),
    ("13.12.2022", "7", "13.01.2023", "7"),
    ("20.02.2023", "8", "23.02.2024", "8"),
    ("16.03.2024", "8", "03.07.2024", "10"),
    ("20.07.2024", "10", "15.11.2024", "10"),
    ("23.11.2024", "12", "", ""),   # ongoing as of last passport scan
]

YEARS = range(2019, 2026)


def d(s):
    return date(*map(int, s.split(".")[::-1]))


def year_rows(ya):
    """Rows (entry, epg, exit, xpg, dur) visible in year ya.

    Trips spanning a year boundary are NOT split with derived dates: the
    earlier year shows the real entry (exit blank), the later year shows the
    real exit (entry blank). Only the duration column carries the day count
    for the portion of the trip falling in that year.
    """
    rows = []
    for entry, epg, exit_, xpg in TRIPS:
        ey = d(entry).year
        xy = d(exit_).year if exit_ else 9999
        if ey == ya and xy == ya:
            rows.append((entry, epg, exit_, xpg, duration(entry, exit_)))
        elif ey == ya and xy > ya:
            # entered this year, still present at year end
            days = (date(ya, 12, 31) - d(entry)).days + 1
            rows.append((entry, epg, "", "", f"{days} days"))
        elif ey < ya == xy:
            # entered an earlier year, exits this year
            days = (d(exit_) - date(ya, 1, 1)).days + 1
            rows.append(("", "", exit_, xpg, f"{days} days"))
        elif ey < ya < xy or (ey < ya and not exit_):
            # middle year of a long trip, or open-ended trip's later year:
            # present the whole year, no stamped dates apply
            days = 366 if (ya % 4 == 0 and (ya % 100 != 0 or ya % 400 == 0)) else 365
            rows.append(("", "", "", "", f"{days} days"))
    return rows


def duration(entry, exit_):
    if not entry or not exit_:
        return ""
    return f"{(d(exit_) - d(entry)).days + 1} days"


def put(page, text, x0, x1, y_center, size=8.5):
    if not text:
        return
    rect = pymupdf.Rect(x0, y_center - 8, x1, y_center + 8)
    page.insert_textbox(rect, text, fontsize=size, fontname="helv",
                        align=pymupdf.TEXT_ALIGN_CENTER)


# Table geometry: 12 data rows from y=318.7 to y=732.7
TOP, BOT = 318.7, 732.7
row_h = (BOT - TOP) / 12
COLS = [(74, 163), (166, 241), (244, 305), (308, 384), (387, 440), (442, 525)]

for ya in YEARS:
    rows = year_rows(ya)
    if not rows:
        continue
    doc = pymupdf.open(SRC)
    page = doc[0]
    page.insert_text((338, 151), NAME, fontsize=9, fontname="helv")
    page.insert_text((242, 181), PASSPORT, fontsize=9, fontname="helv")

    for i, (entry, epg, exit_, xpg, dur) in enumerate(rows):
        yc = TOP + row_h * i + row_h / 2
        for text, (x0, x1) in zip((str(ya), entry, epg, exit_, xpg, dur), COLS):
            put(page, text, x0, x1, yc)

    out = OUT_TMPL.format(ya=ya)
    doc.save(out)
    doc.close()
    print(f"YA {ya}:")
    for r in rows:
        print("  ", r)

