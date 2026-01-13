from __future__ import annotations

from dataclasses import dataclass
from datetime import date
from pathlib import Path

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import cm
from reportlab.platypus import (
    SimpleDocTemplate,
    Paragraph,
    Spacer,
    PageBreak,
    Table,
    TableStyle,
)


@dataclass(frozen=True)
class BudgetLine:
    item: str
    unit: str
    qty: float
    rate_eur: float

    @property
    def total_eur(self) -> float:
        return round(self.qty * self.rate_eur, 2)


def eur(v: float) -> str:
    return f"{v:,.2f} €".replace(",", "X").replace(".", ",").replace("X", ".")


def build_pdf(output_path: Path) -> None:
    output_path.parent.mkdir(parents=True, exist_ok=True)

    styles = getSampleStyleSheet()
    h1 = ParagraphStyle("H1", parent=styles["Heading1"], spaceAfter=10)
    h2 = ParagraphStyle("H2", parent=styles["Heading2"], spaceAfter=8)
    body = ParagraphStyle("Body", parent=styles["BodyText"], leading=14, spaceAfter=6)
    small = ParagraphStyle("Small", parent=styles["BodyText"], fontSize=9, leading=11, textColor=colors.grey)
    title = ParagraphStyle(
        "Title",
        parent=styles["Title"],
        alignment=TA_CENTER,
        spaceAfter=18,
    )

    doc = SimpleDocTemplate(
        str(output_path),
        pagesize=A4,
        leftMargin=2.0 * cm,
        rightMargin=2.0 * cm,
        topMargin=1.8 * cm,
        bottomMargin=1.8 * cm,
        title="Plan de Pruebas, Especificaciones, Informe y Presupuesto - PepeLink",
        author="Equipo de Desarrollo / QA",
    )

    story: list = []

    story.append(Paragraph("PepeLink — Plan de Pruebas, Especificaciones, Informe y Presupuesto", title))
    story.append(Paragraph(f"Versión del documento: 1.0 — Fecha: {date.today().isoformat()}", small))
    story.append(Spacer(1, 12))

    story.append(Paragraph("1. Alcance y contexto", h1))
    story.append(
        Paragraph(
            "Este documento define el plan de pruebas, la especificación de casos de prueba, un informe de ejecución "
            "y un presupuesto desglosado para la aplicación <b>PepeLink</b> (Swing), incluyendo: autenticación, roles "
            "(ADMIN/USER), gestión de proyectos/etapas/entregas/permisos, whitelist, logs, FTP y SMTP/IMAP.",
            body,
        )
    )
    story.append(
        Paragraph(
            "<b>Suposiciones</b>: entorno Windows 10/11, MySQL accesible, conectividad a servicios externos (FTP/IMAP) "
            "variable. El alcance del presupuesto es orientativo y puede ajustarse según requisitos finales.",
            body,
        )
    )

    story.append(PageBreak())

    # =======================
    # PLAN DE PRUEBAS
    # =======================
    story.append(Paragraph("2. Plan de pruebas", h1))
    story.append(Paragraph("2.1 Objetivos", h2))
    story.append(
        Paragraph(
            "- Validar que los flujos críticos (login/logout, navegación por rol, CRUD, whitelist, logs) funcionen correctamente.<br/>"
            "- Detectar regresiones en funcionalidades recientemente modificadas (password en usuarios, fechas en stage, whitelist, logs enriquecidos).<br/>"
            "- Confirmar que el modo USER respeta las restricciones/visibilidad definidas.",
            body,
        )
    )

    story.append(Paragraph("2.2 Estrategia", h2))
    story.append(
        Paragraph(
            "<b>Tipos de prueba</b>: funcional (manual), regresión, smoke, pruebas de seguridad básicas (roles/permiso de acceso), "
            "pruebas de robustez ante fallos (FTP/IMAP).<br/>"
            "<b>Criterio de entrada</b>: build ejecutable, base de datos disponible, usuarios ADMIN/USER existentes.<br/>"
            "<b>Criterio de salida</b>: 0 fallos bloqueantes, 0 críticos, y >95% de casos P0/P1 ejecutados.",
            body,
        )
    )

    story.append(Paragraph("2.3 Entorno de prueba", h2))
    story.append(
        Paragraph(
            "- OS: Windows 10/11<br/>"
            "- Java: 17+ (o el LTS usado por el proyecto)<br/>"
            "- DB: MySQL (schema.sql + data.sql aplicados)<br/>"
            "- Servicios externos: FTP e IMAP (pueden fallar; la app debe degradar sin impedir login)",
            body,
        )
    )

    story.append(Paragraph("2.4 Matriz de módulos / riesgos", h2))
    risk_rows = [
        ["Módulo", "Riesgo", "Impacto", "Mitigación de prueba"],
        ["Login/Logout", "Estado inconsistente entre sesiones", "Alto", "Repetir login→logout→login; validar limpieza de estado"],
        ["Usuarios", "Password/token no gestionados", "Alto", "Alta/edición con password y token; login posterior"],
        ["Stages (fechas)", "Autocompletado no deseado/validación", "Medio", "Initial auto; Final opcional y persistente vacío"],
        ["Whitelist", "CRUD por clave email", "Medio", "Alta/edición/baja con emails duplicados y validación básica"],
        ["Logs", "Información incompleta del autor", "Bajo", "Ver columnas id/email; validar consistencia con usuarios"],
        ["FTP/IMAP", "Fallo de servicio bloquea app", "Alto", "Simular caída; login no debe fallar; mensaje de error controlado"],
    ]
    t = Table(risk_rows, colWidths=[4.0 * cm, 5.3 * cm, 2.2 * cm, 5.5 * cm])
    t.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#2f687d")),
                ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
                ("FONTNAME", (0, 0), (-1, 0), "Helvetica-Bold"),
                ("GRID", (0, 0), (-1, -1), 0.5, colors.grey),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.whitesmoke, colors.lightgrey]),
                ("LEFTPADDING", (0, 0), (-1, -1), 6),
                ("RIGHTPADDING", (0, 0), (-1, -1), 6),
            ]
        )
    )
    story.append(t)

    story.append(PageBreak())

    # =======================
    # ESPECIFICACIÓN DE PRUEBAS
    # =======================
    story.append(Paragraph("3. Especificaciones y casos de prueba", h1))
    story.append(Paragraph("3.1 Convenciones", h2))
    story.append(
        Paragraph(
            "<b>Severidad</b>: P0 (bloqueante), P1 (crítico), P2 (medio), P3 (bajo).<br/>"
            "<b>Formato</b>: ID, Objetivo, Precondiciones, Pasos, Resultado esperado.",
            body,
        )
    )

    cases = [
        ["TC-LOGIN-01 (P0)", "Login con credenciales válidas", "Existe usuario; DB accesible",
         "1) Abrir app<br/>2) Email y password válidos<br/>3) Click Login",
         "Se muestra dashboard según rol (Admin/User)"],
        ["TC-LOGIN-02 (P1)", "Login inválido", "DB accesible",
         "1) Email correcto, password incorrecto<br/>2) Login",
         "Mensaje de error y formulario reseteado"],
        ["TC-LOGOUT-01 (P0)", "Logout y re-login", "Sesión iniciada",
         "1) Ir a tab Logout<br/>2) Volver a login<br/>3) Iniciar sesión de nuevo",
         "Permite re-login; no se queda en loop ni falla por FTP/IMAP"],
        ["TC-USER-01 (P0)", "Alta de usuario exige password", "Rol Admin",
         "1) Users → Add<br/>2) Completar campos<br/>3) Password obligatorio",
         "Se crea usuario y puede loguear"],
        ["TC-USER-02 (P1)", "Editar usuario: password opcional", "Rol Admin",
         "1) Users → Modify<br/>2) Dejar New Password vacío<br/>3) Guardar",
         "Se mantiene password; user puede seguir logueando"],
        ["TC-USER-03 (P1)", "Token editable", "Rol Admin",
         "1) Users → Modify<br/>2) Cambiar Email Token<br/>3) Guardar",
         "Se persiste el token (visible en operaciones SMTP)"],
        ["TC-STAGE-01 (P1)", "Stage: initial_date auto y final_date opcional", "Rol Admin o User con proyecto",
         "1) Stages → Add<br/>2) Ver Initial Date<br/>3) Dejar Final Date vacío<br/>4) Guardar",
         "Initial Date se setea; Final Date queda null"],
        ["TC-WL-01 (P1)", "Whitelist: alta email", "Rol Admin",
         "1) Whitelist → Add<br/>2) Email válido<br/>3) Guardar",
         "Se inserta email, aparece en tabla"],
        ["TC-LOG-01 (P2)", "Logs muestran autor", "Existen logs",
         "1) Logs view<br/>2) Ver columnas User ID y User Email",
         "Columnas visibles y consistentes con users"],
    ]

    case_table = Table([["ID", "Objetivo", "Precondiciones", "Pasos", "Esperado"]] + cases,
                       colWidths=[3.0 * cm, 4.0 * cm, 3.5 * cm, 6.0 * cm, 4.5 * cm])
    case_table.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#2f687d")),
                ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
                ("FONTNAME", (0, 0), (-1, 0), "Helvetica-Bold"),
                ("GRID", (0, 0), (-1, -1), 0.5, colors.grey),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("FONTSIZE", (0, 0), (-1, -1), 8.5),
                ("LEADING", (0, 0), (-1, -1), 10),
                ("LEFTPADDING", (0, 0), (-1, -1), 5),
                ("RIGHTPADDING", (0, 0), (-1, -1), 5),
            ]
        )
    )
    story.append(case_table)

    story.append(PageBreak())

    # =======================
    # INFORME DE PRUEBAS
    # =======================
    story.append(Paragraph("4. Informe de pruebas (plantilla y ejemplo)", h1))
    story.append(Paragraph("4.1 Resumen ejecutivo", h2))
    story.append(
        Paragraph(
            "Estado global: <b>EN PROGRESO</b>. Este informe sirve como plantilla. "
            "Una vez ejecutadas las pruebas en el entorno objetivo, actualizar: fecha, build, resultados, defectos.",
            body,
        )
    )
    story.append(Paragraph("4.2 Resultados (ejemplo)", h2))
    results_rows = [
        ["Métrica", "Valor"],
        ["Casos ejecutados", "0 / 9 (pendiente)"],
        ["Pasados", "0"],
        ["Fallados", "0"],
        ["Bloqueados", "0"],
        ["Defectos P0/P1 abiertos", "0"],
    ]
    rt = Table(results_rows, colWidths=[6.0 * cm, 10.0 * cm])
    rt.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#2f687d")),
                ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
                ("FONTNAME", (0, 0), (-1, 0), "Helvetica-Bold"),
                ("GRID", (0, 0), (-1, -1), 0.5, colors.grey),
                ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.whitesmoke, colors.lightgrey]),
                ("LEFTPADDING", (0, 0), (-1, -1), 6),
                ("RIGHTPADDING", (0, 0), (-1, -1), 6),
            ]
        )
    )
    story.append(rt)
    story.append(Spacer(1, 10))
    story.append(Paragraph("4.3 Registro de defectos (plantilla)", h2))
    story.append(
        Paragraph(
            "Campos recomendados: <b>ID</b>, <b>Título</b>, <b>Severidad</b>, <b>Módulo</b>, <b>Pasos</b>, "
            "<b>Esperado</b>, <b>Actual</b>, <b>Evidencia</b>, <b>Estado</b>, <b>Asignado</b>.",
            body,
        )
    )

    story.append(PageBreak())

    # =======================
    # PRESUPUESTO
    # =======================
    story.append(Paragraph("5. Presupuesto desglosado (estimación)", h1))
    story.append(
        Paragraph(
            "Presupuesto orientativo para evolución, QA y entrega. Ajustar según alcance final "
            "(número de iteraciones, soporte, hardening de seguridad, automatización, etc.).",
            body,
        )
    )

    lines = [
        BudgetLine("Análisis y refinamiento de requisitos", "h", 10, 45),
        BudgetLine("Desarrollo (UI/CRUD/roles/logout)", "h", 50, 45),
        BudgetLine("Integración DB / migraciones (schema/data)", "h", 10, 45),
        BudgetLine("Mejoras de robustez servicios externos (FTP/IMAP)", "h", 10, 45),
        BudgetLine("QA manual (plan + ejecución + reporte)", "h", 24, 35),
        BudgetLine("Documentación y handover", "h", 8, 35),
        BudgetLine("Gestión de proyecto / coordinación", "h", 8, 50),
    ]

    subtotal = round(sum(l.total_eur for l in lines), 2)
    contingency_rate = 0.15
    contingency = round(subtotal * contingency_rate, 2)
    total = round(subtotal + contingency, 2)

    budget_rows = [["Partida", "Unidad", "Cantidad", "Tarifa", "Total"]]
    for l in lines:
        budget_rows.append([l.item, l.unit, f"{l.qty:g}", eur(l.rate_eur), eur(l.total_eur)])
    budget_rows.append(["", "", "", "Subtotal", eur(subtotal)])
    budget_rows.append(["", "", "", f"Contingencia ({int(contingency_rate*100)}%)", eur(contingency)])
    budget_rows.append(["", "", "", "TOTAL", eur(total)])

    bt = Table(budget_rows, colWidths=[7.0 * cm, 1.6 * cm, 2.0 * cm, 2.8 * cm, 3.0 * cm])
    bt.setStyle(
        TableStyle(
            [
                ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#2f687d")),
                ("TEXTCOLOR", (0, 0), (-1, 0), colors.white),
                ("FONTNAME", (0, 0), (-1, 0), "Helvetica-Bold"),
                ("GRID", (0, 0), (-1, -1), 0.5, colors.grey),
                ("VALIGN", (0, 0), (-1, -1), "TOP"),
                ("FONTSIZE", (0, 0), (-1, -1), 9),
                ("ROWBACKGROUNDS", (0, 1), (-1, -4), [colors.whitesmoke, colors.lightgrey]),
                ("BACKGROUND", (0, -3), (-1, -1), colors.HexColor("#f2f2f2")),
                ("FONTNAME", (0, -1), (-1, -1), "Helvetica-Bold"),
                ("ALIGN", (1, 1), (-1, -1), "RIGHT"),
                ("LEFTPADDING", (0, 0), (-1, -1), 6),
                ("RIGHTPADDING", (0, 0), (-1, -1), 6),
            ]
        )
    )
    story.append(bt)
    story.append(Spacer(1, 10))
    story.append(
        Paragraph(
            "<b>No incluido</b>: infraestructura/hosting, licencias de terceros, certificación, pruebas automatizadas avanzadas, "
            "hardening de seguridad, soporte 24/7.",
            body,
        )
    )

    def add_page_number(canvas, _doc):
        canvas.saveState()
        canvas.setFont("Helvetica", 9)
        canvas.setFillColor(colors.grey)
        canvas.drawRightString(A4[0] - 2.0 * cm, 1.2 * cm, f"Página {canvas.getPageNumber()}")
        canvas.restoreState()

    doc.build(story, onFirstPage=add_page_number, onLaterPages=add_page_number)


if __name__ == "__main__":
    repo_root = Path(__file__).resolve().parents[1]
    out = repo_root / "docs" / "PepeLink_PlanPruebas_Informe_Presupuesto.pdf"
    build_pdf(out)
    print(f"PDF generado: {out}")


