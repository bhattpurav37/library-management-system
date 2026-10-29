"""
Exam Schedule & Syllabus PDF generator
Renders the exam timetable (subject, date, time, syllabus) as a polished PDF.
"""

from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import mm
from reportlab.lib.colors import HexColor, white
from reportlab.lib.enums import TA_LEFT, TA_CENTER
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, KeepTogether
)
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
import os

# Register a Devanagari-capable font for the Hindi section
# (ReportLab's built-in Helvetica has no Devanagari glyphs)
HINDI_FONT = None
for name, path in [
    ("Nirmala", r"C:\Windows\Fonts\Nirmala.ttf"),
    ("Mangal", r"C:\Windows\Fonts\Mangal.ttf"),
]:
    if os.path.exists(path):
        try:
            pdfmetrics.registerFont(TTFont(name, path))
            HINDI_FONT = name
            break
        except Exception:
            pass

OUTPUT = os.path.join(os.path.dirname(__file__), "Exam_Schedule_Syllabus.pdf")

# ── Colours ──────────────────────────────────────────────
C_PRIMARY  = HexColor("#1B3A5C")
C_ACCENT   = HexColor("#2E86C1")
C_LIGHT_BG = HexColor("#EBF5FB")
C_DARK     = HexColor("#1C2833")
C_MID      = HexColor("#2C3E50")

# ── Styles ───────────────────────────────────────────────
base = getSampleStyleSheet()

sTitle = ParagraphStyle("TitleX", parent=base["Title"],
    fontSize=20, leading=24, textColor=C_PRIMARY,
    spaceAfter=2*mm, alignment=TA_CENTER)

sSubtitle = ParagraphStyle("Sub", parent=base["Normal"],
    fontSize=11, leading=14, textColor=C_ACCENT,
    spaceAfter=8*mm, alignment=TA_CENTER)

sHeaderCell = ParagraphStyle("HC", parent=base["Normal"],
    fontName="Helvetica-Bold", fontSize=10, leading=13,
    textColor=white, alignment=TA_LEFT)

sBody = ParagraphStyle("Body2", parent=base["Normal"],
    fontSize=9.5, leading=13, textColor=C_DARK)

sSyl = ParagraphStyle("Syl", parent=base["Normal"],
    fontSize=9.5, leading=13.5, textColor=C_MID)

sSylHindi = ParagraphStyle("SylHi", parent=sSyl,
    fontName=HINDI_FONT or "Helvetica")

# ── Data ─────────────────────────────────────────────────
# (subject, date, start, end, syllabus lines, style key)
EXAMS = [
    (
        "General Knowledge", "11-09-2026", "10:00", "11:30",
        [
            "<b>General Knowledge — New Find Out (Animix)</b>",
            "Home, Sweet Home — Page 8",
            "All About Me — Page 9",
            "What Did She Give Away — Page 10",
            "Think Quick — Page 11",
            "Time to Rhyme — Page 12",
            "Fairy Tales — Page 13",
            "Think Think — Page 14",
            "Odd One Out — Page 15",
            "Animal Sounds — Page 16",
            "Good Manners, Good Words — Page 17",
            "Fruit Salad — Page 18",
            "Animal Food — Page 19",
            "Headwear — Page 20",
            "They Work Hard — Page 21",
            "A Thinking Game — Page 22",
            "At Home — Page 23",
            "A Flower Quiz — Page 24",
            "Vegetables on the Table — Page 25",
            "I Love My India — Page 26",
            "The Clothes We Wear — Page 27",
            "Know the Sport — Page 28",
            "Toon Time — Page 29",
            "Our Festivals — Page 30",
            "Places of Prayer — Page 31",
            "A Jungle — Page 32",
        ],
        "default",
    ),
    (
        "Environmental Studies", "17-09-2026", "10:00", "12:00",
        [
            "<b>EVS — My Learning Hub: World Around Us — Part A</b>",
            "Ch 1: My Body — Pages 18–24",
            "Ch 2: My Family and Home — Pages 26–32",
            "Ch 3: My Neighbourhood — Pages 36–42",
            "Ch 4: My School — Pages 44–48",
            "Ch 5: Staying Safe — Pages 50–56",
            "Ch 6: Food We Eat — Pages 59–63",
            "Ch 7: Clothes We Wear — Pages 65–69",
            "Ch 8: Plants Around Us — Pages 73–78",
            "Ch 9: Animals Around Us — Pages 79–86",
            "<b>EVS — My Practice Hub: World Around Us</b>",
            "Ch 1: My Body — Pages 11–15",
            "Ch 2: My Family and Home — Pages 16–19",
            "Ch 3: My Neighbourhood — Pages 20–23",
            "Ch 4: My School — Pages 24–26",
            "Ch 5: Staying Safe — Pages 27–29",
            "Ch 6: Food We Eat — Pages 30–33",
            "Ch 7: Clothes We Wear — Pages 34–37",
            "Ch 8: Plants Around Us — Pages 38–40",
            "Ch 9: Animals Around Us — Pages 41–44",
            "<i>Refer all worksheets, My Learning Hub – Part A, My Practice Hub and notebook.</i>",
        ],
        "default",
    ),
    (
        "Hindi", "18-09-2026", "10:00", "12:00",
        [
            "<b>Hindi Textbook — वाणी (हिंदी पाठमाला – 1)</b>",
            "वर्णमाला (Alphabet) — Textbook Pages 7–8",
            "Ch 5: स्वर (Vowels) — Pages 13–26",
            "Ch 6: व्यंजन (Consonants) — Pages 30–44, 48–53, 55–60 and 62–71",
            "Counting 1–10 — Textbook Page 82",
            "मात्रा",
            "<i>Refer all worksheets and classwork done in the notebook.</i>",
        ],
        "hindi",
    ),
    (
        "Mathematics", "21-09-2026", "10:00", "12:00",
        [
            "<b>Maths — My Learning Hub Part A</b>",
            "Prep Time — Pages 9–21",
            "Theme 1: I Am Special",
            "&nbsp;&nbsp;• 1. Counting Tales (Numbers up to 50) — Pages 23–40",
            "&nbsp;&nbsp;• 2. How Many More? (Addition) — Pages 41–64",
            "Theme 2: Our Community",
            "&nbsp;&nbsp;• 3. The Power of Minus (Subtraction) — Pages 66–87",
            "Theme 3: Our Basic Needs",
            "&nbsp;&nbsp;• 4. The Great Maths Race (Numbers up to 100) — Pages 89–106",
            "Theme 4: Living World",
            "&nbsp;&nbsp;• 5. Rangoli ka Khel (Shapes and Patterns) — Pages 108–120",
            "<b>Maths — My Practice Hub Part A</b>",
            "Prep Time — Pages 7–14",
            "Theme 1: I Am Special",
            "&nbsp;&nbsp;• 1. Counting Tales (Numbers up to 50) — Pages 15–19",
            "&nbsp;&nbsp;• 2. How Many More? (Addition) — Pages 20–26",
            "Theme 2: Our Community",
            "&nbsp;&nbsp;• 3. The Power of Minus (Subtraction) — Pages 27–33",
            "Theme 3: Our Basic Needs",
            "&nbsp;&nbsp;• 4. The Great Maths Race (Numbers up to 100) — Pages 34–38",
            "Theme 4: Living World",
            "&nbsp;&nbsp;• 5. Rangoli ka Khel (Shapes and Patterns) — Pages 39–44",
            "<i>Refer all worksheets, My Learning Hub – Part A, My Practice Hub and notebook.</i>",
        ],
        "default",
    ),
    (
        "English", "22-09-2026", "10:00", "12:00",
        [
            "<b>English — Oxford Advantage: My Learning Hub – Part A</b>",
            "Theme 1: I Am Special",
            "&nbsp;&nbsp;• Ch 1: Mom and I — Pages 17–25",
            "Theme 2: Our Community",
            "&nbsp;&nbsp;• Ch 2: Rani's First Day at School — Pages 29–39",
            "&nbsp;&nbsp;• Ch 3: If I Were — Pages 42–44",
            "Theme 3: Our Basic Needs",
            "&nbsp;&nbsp;• Ch 4: Click and the Kids — Pages 49–61",
            "Theme 4: Living World",
            "&nbsp;&nbsp;• Ch 5: Glub! — Pages 65–74",
            "&nbsp;&nbsp;• Ch 6: A Little Seed — Pages 77–79",
            "<b>English — Oxford Advantage: My Practice Hub</b>",
            "Theme 1: I Am Special",
            "&nbsp;&nbsp;• Ch 1: Mom and I — Pages 13–19",
            "Theme 2: Our Community",
            "&nbsp;&nbsp;• Ch 2: Rani's First Day at School — Pages 23–28",
            "&nbsp;&nbsp;• Ch 3: If I Were — Pages 30–31",
            "Theme 3: Our Basic Needs",
            "&nbsp;&nbsp;• Ch 4: Click and the Kids — Pages 34–40",
            "Theme 4: Living World",
            "&nbsp;&nbsp;• Ch 5: Glub! — Pages 41–49",
            "&nbsp;&nbsp;• Ch 6: A Little Seed — Pages 50–51",
            "<i>Refer all worksheets and notebook also.</i>",
        ],
        "default",
    ),
    (
        "ICT (Computer Science)", "23-09-2026", "10:00", "12:00",
        [
            "<b>Computer Science — MegaByte</b>",
            "Unit 1: General Computing",
            "&nbsp;&nbsp;• Ch 1: What are Computers? — Pages 7–14",
            "&nbsp;&nbsp;• Ch 2: Using a Computer — Pages 15–28",
            "&nbsp;&nbsp;• Worksheet 1 — Page 29",
            "Unit 2: Office Software",
            "&nbsp;&nbsp;• Ch 3: Fun with Tux Typing — Pages 30–38",
            "&nbsp;&nbsp;• Worksheet 2 — Page 40",
            "<i>Refer all the worksheets.</i>",
        ],
        "default",
    ),
]


def build():
    doc = SimpleDocTemplate(
        OUTPUT, pagesize=A4,
        leftMargin=15*mm, rightMargin=15*mm,
        topMargin=15*mm, bottomMargin=15*mm,
        title="Exam Schedule & Syllabus",
    )

    story = [
        Paragraph("Exam Schedule &amp; Syllabus", sTitle),
        Paragraph("September 2026", sSubtitle),
    ]

    # Summary timetable
    header = [
        Paragraph("Subject", sHeaderCell),
        Paragraph("Date", sHeaderCell),
        Paragraph("Day", sHeaderCell),
        Paragraph("Time", sHeaderCell),
    ]
    import datetime
    rows = [header]
    for subj, date_s, start, end, _, _style in EXAMS:
        d = datetime.datetime.strptime(date_s, "%d-%m-%Y")
        rows.append([
            Paragraph("<b>{}</b>".format(subj), sBody),
            Paragraph(date_s, sBody),
            Paragraph(d.strftime("%A"), sBody),
            Paragraph("{} – {}".format(start, end), sBody),
        ])

    tt = Table(rows, colWidths=[62*mm, 30*mm, 30*mm, 38*mm], repeatRows=1)
    tt.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, 0), C_PRIMARY),
        ("ROWBACKGROUNDS", (0, 1), (-1, -1), [white, C_LIGHT_BG]),
        ("GRID", (0, 0), (-1, -1), 0.5, HexColor("#AEB6BF")),
        ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
        ("TOPPADDING", (0, 0), (-1, -1), 3*mm),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 3*mm),
        ("LEFTPADDING", (0, 0), (-1, -1), 3*mm),
    ]))
    story.append(tt)
    story.append(Spacer(1, 8*mm))

    # Detailed syllabus per subject
    for subj, date_s, start, end, lines, style_key in EXAMS:
        head = Paragraph(
            "{} &nbsp;|&nbsp; {} &nbsp;|&nbsp; {} – {}".format(subj, date_s, start, end),
            ParagraphStyle("SH", parent=base["Normal"],
                fontName="Helvetica-Bold", fontSize=12, leading=15,
                textColor=white))
        style = sSylHindi if style_key == "hindi" else sSyl
        body = [Paragraph(ln, style) for ln in lines]

        box = Table(
            [[head], [body]],
            colWidths=[180*mm],
        )
        box.setStyle(TableStyle([
            ("BACKGROUND", (0, 0), (0, 0), C_PRIMARY),
            ("BACKGROUND", (0, 1), (0, 1), C_LIGHT_BG),
            ("BOX", (0, 0), (-1, -1), 0.75, C_ACCENT),
            ("LEFTPADDING", (0, 0), (-1, -1), 4*mm),
            ("RIGHTPADDING", (0, 0), (-1, -1), 4*mm),
            ("TOPPADDING", (0, 0), (0, 0), 2.5*mm),
            ("BOTTOMPADDING", (0, 0), (0, 0), 2.5*mm),
            ("TOPPADDING", (0, 1), (0, 1), 3*mm),
            ("BOTTOMPADDING", (0, 1), (0, 1), 3*mm),
        ]))
        story.append(KeepTogether(box))
        story.append(Spacer(1, 6*mm))

    doc.build(story)
    print("Written:", OUTPUT)


if __name__ == "__main__":
    build()
