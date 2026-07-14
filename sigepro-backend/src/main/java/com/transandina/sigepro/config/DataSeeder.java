package com.transandina.sigepro.config;

import com.transandina.sigepro.entity.*;
import com.transandina.sigepro.enums.EstadoProyecto;
import com.transandina.sigepro.enums.RolUsuario;
import com.transandina.sigepro.enums.TipoDependencia;
import com.transandina.sigepro.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final TareaRepository tareaRepository;
    private final DependenciaTareaRepository dependenciaRepository;
    private final RecursoTareaRepository recursoTareaRepository;
    private final AuditoriaRepository auditoriaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UsuarioRepository usuarioRepository,
                      ProyectoRepository proyectoRepository,
                      TareaRepository tareaRepository,
                      DependenciaTareaRepository dependenciaRepository,
                      RecursoTareaRepository recursoTareaRepository,
                      AuditoriaRepository auditoriaRepository,
                      PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.tareaRepository = tareaRepository;
        this.dependenciaRepository = dependenciaRepository;
        this.recursoTareaRepository = recursoTareaRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (proyectoRepository.count() > 0) {
            System.out.println(">>> Datos de prueba ya existen, se omite seed");
            return;
        }

        Usuario admin = usuarioRepository.findByEmail("admin@transandina.com")
                .orElseGet(() -> usuarioRepository.save(
                        new Usuario("Admin Principal", "admin@transandina.com",
                                passwordEncoder.encode("123456"), RolUsuario.ADMINISTRADOR)));

        Usuario jefe = usuarioRepository.findByEmail("cmendoza@transandina.com")
                .orElseGet(() -> usuarioRepository.save(
                        new Usuario("Carlos Mendoza", "cmendoza@transandina.com",
                                passwordEncoder.encode("123456"), RolUsuario.JEFE_PROYECTO)));

        Usuario ana = usuarioRepository.findByEmail("alopez@transandina.com")
                .orElseGet(() -> usuarioRepository.save(
                        new Usuario("Ana Lopez", "alopez@transandina.com",
                                passwordEncoder.encode("123456"), RolUsuario.USUARIO)));

        Usuario pedro = usuarioRepository.findByEmail("pgarcia@transandina.com")
                .orElseGet(() -> usuarioRepository.save(
                        new Usuario("Pedro Garcia", "pgarcia@transandina.com",
                                passwordEncoder.encode("123456"), RolUsuario.USUARIO)));

        Usuario maria = usuarioRepository.findByEmail("mtorres@transandina.com")
                .orElseGet(() -> usuarioRepository.save(
                        new Usuario("Maria Torres", "mtorres@transandina.com",
                                passwordEncoder.encode("123456"), RolUsuario.USUARIO)));

        Proyecto erp = new Proyecto();
        erp.setNombre("Implementacion ERP");
        erp.setDescripcion("Implementacion del sistema ERP corporativo");
        erp.setFechaInicio(LocalDate.of(2026, 1, 15));
        erp.setFechaFin(LocalDate.of(2026, 6, 30));
        erp.setEstado(EstadoProyecto.EN_CURSO);
        erp.setJefeProyecto(jefe);
        erp.setPresupuestoTotal(new BigDecimal("150000.00"));
        erp.setCostoRealTotal(new BigDecimal("48000.00"));
        erp = proyectoRepository.save(erp);

        Proyecto cloud = new Proyecto();
        cloud.setNombre("Migracion Cloud");
        cloud.setDescripcion("Migracion de infraestructura a la nube");
        cloud.setFechaInicio(LocalDate.of(2026, 3, 1));
        cloud.setFechaFin(LocalDate.of(2026, 8, 15));
        cloud.setEstado(EstadoProyecto.PLANIFICADO);
        cloud.setJefeProyecto(jefe);
        cloud.setPresupuestoTotal(new BigDecimal("200000.00"));
        cloud.setCostoRealTotal(BigDecimal.ZERO);
        cloud = proyectoRepository.save(cloud);

        Proyecto app = new Proyecto();
        app.setNombre("App Clientes");
        app.setDescripcion("Desarrollo de aplicacion movil para clientes");
        app.setFechaInicio(LocalDate.of(2026, 2, 1));
        app.setFechaFin(LocalDate.of(2026, 5, 15));
        app.setEstado(EstadoProyecto.EN_CURSO);
        app.setJefeProyecto(jefe);
        app.setPresupuestoTotal(new BigDecimal("85000.00"));
        app.setCostoRealTotal(new BigDecimal("35000.00"));
        app = proyectoRepository.save(app);

        Tarea tareaAnalisis = crearTarea(erp, null, "Analisis de Requerimientos",
                LocalDate.of(2026, 1, 15), LocalDate.of(2026, 2, 15),
                100, new BigDecimal("15000.00"), new BigDecimal("14500.00"), ana);

        Tarea tareaDiseno = crearTarea(erp, null, "Diseno de Arquitectura",
                LocalDate.of(2026, 2, 16), LocalDate.of(2026, 3, 15),
                80, new BigDecimal("20000.00"), new BigDecimal("16000.00"), pedro);

        Tarea tareaDesarrollo = crearTarea(erp, null, "Desarrollo Modulos",
                LocalDate.of(2026, 3, 16), LocalDate.of(2026, 5, 15),
                30, new BigDecimal("70000.00"), new BigDecimal("12000.00"), ana);

        Tarea tareaPruebas = crearTarea(erp, null, "Pruebas y QA",
                LocalDate.of(2026, 5, 16), LocalDate.of(2026, 6, 15),
                0, new BigDecimal("25000.00"), BigDecimal.ZERO, maria);

        Tarea tareaDespliegue = crearTarea(erp, null, "Despliegue y Capacitacion",
                LocalDate.of(2026, 6, 16), LocalDate.of(2026, 6, 30),
                0, new BigDecimal("20000.00"), BigDecimal.ZERO, maria);

        Tarea tareaModConta = crearTarea(erp, tareaDesarrollo, "Modulo Contabilidad",
                LocalDate.of(2026, 3, 16), LocalDate.of(2026, 4, 15),
                50, new BigDecimal("25000.00"), new BigDecimal("6000.00"), ana);

        Tarea tareaModRRHH = crearTarea(erp, tareaDesarrollo, "Modulo RRHH",
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30),
                20, new BigDecimal("25000.00"), new BigDecimal("3000.00"), pedro);

        Tarea tareaModLog = crearTarea(erp, tareaDesarrollo, "Modulo Logistica",
                LocalDate.of(2026, 4, 16), LocalDate.of(2026, 5, 15),
                10, new BigDecimal("20000.00"), new BigDecimal("3000.00"), ana);

        Tarea tareaUX = crearTarea(app, null, "Diseno UX/UI",
                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28),
                100, new BigDecimal("10000.00"), new BigDecimal("9500.00"), pedro);

        Tarea tareaFrontend = crearTarea(app, null, "Desarrollo Frontend",
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 4, 15),
                60, new BigDecimal("35000.00"), new BigDecimal("18000.00"), ana);

        Tarea tareaBackend = crearTarea(app, null, "Desarrollo Backend",
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 4, 30),
                40, new BigDecimal("25000.00"), new BigDecimal("7500.00"), pedro);

        Tarea tareaTesting = crearTarea(app, null, "Testing",
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 15),
                0, new BigDecimal("15000.00"), BigDecimal.ZERO, maria);

        crearDependencia(tareaAnalisis, tareaDiseno, TipoDependencia.FIN_INICIO);
        crearDependencia(tareaDiseno, tareaDesarrollo, TipoDependencia.FIN_INICIO);
        crearDependencia(tareaDesarrollo, tareaPruebas, TipoDependencia.FIN_INICIO);
        crearDependencia(tareaPruebas, tareaDespliegue, TipoDependencia.FIN_INICIO);
        crearDependencia(tareaUX, tareaFrontend, TipoDependencia.FIN_INICIO);
        crearDependencia(tareaUX, tareaBackend, TipoDependencia.FIN_INICIO);
        crearDependencia(tareaFrontend, tareaTesting, TipoDependencia.FIN_INICIO);
        crearDependencia(tareaBackend, tareaTesting, TipoDependencia.FIN_INICIO);

        crearAsignacion(tareaAnalisis, ana, new BigDecimal("120.00"), new BigDecimal("115.00"));
        crearAsignacion(tareaDiseno, pedro, new BigDecimal("100.00"), new BigDecimal("85.00"));
        crearAsignacion(tareaDesarrollo, ana, new BigDecimal("200.00"), new BigDecimal("60.00"));
        crearAsignacion(tareaDesarrollo, pedro, new BigDecimal("150.00"), new BigDecimal("40.00"));
        crearAsignacion(tareaPruebas, maria, new BigDecimal("80.00"), BigDecimal.ZERO);
        crearAsignacion(tareaModConta, ana, new BigDecimal("80.00"), new BigDecimal("25.00"));
        crearAsignacion(tareaModRRHH, pedro, new BigDecimal("80.00"), new BigDecimal("15.00"));
        crearAsignacion(tareaModLog, ana, new BigDecimal("60.00"), new BigDecimal("10.00"));
        crearAsignacion(tareaUX, pedro, new BigDecimal("60.00"), new BigDecimal("58.00"));
        crearAsignacion(tareaFrontend, ana, new BigDecimal("120.00"), new BigDecimal("70.00"));
        crearAsignacion(tareaBackend, pedro, new BigDecimal("100.00"), new BigDecimal("35.00"));

        crearAuditoria(admin, "CREAR", "Proyecto", erp.getId(), LocalDateTime.of(2026, 1, 10, 9, 0));
        crearAuditoria(admin, "CREAR", "Proyecto", cloud.getId(), LocalDateTime.of(2026, 2, 20, 10, 30));
        crearAuditoria(admin, "CREAR", "Proyecto", app.getId(), LocalDateTime.of(2026, 1, 25, 14, 0));
        crearAuditoria(jefe, "ACTUALIZAR", "Proyecto", erp.getId(), LocalDateTime.of(2026, 3, 1, 11, 0));
        crearAuditoria(jefe, "CREAR", "Usuario", ana.getId(), LocalDateTime.of(2026, 1, 5, 8, 30));
        crearAuditoria(jefe, "CREAR", "Usuario", pedro.getId(), LocalDateTime.of(2026, 1, 5, 8, 35));
        crearAuditoria(jefe, "CREAR", "Usuario", maria.getId(), LocalDateTime.of(2026, 1, 5, 8, 40));

        System.out.println(">>> Datos de prueba creados correctamente! Proyectos, tareas, dependencias y asignaciones.");
    }

    private Tarea crearTarea(Proyecto proyecto, Tarea tareaPadre, String nombre,
                              LocalDate inicio, LocalDate fin, int avance,
                              BigDecimal presupuesto, BigDecimal costo, Usuario responsable) {
        Tarea t = new Tarea();
        t.setProyecto(proyecto);
        t.setTareaPadre(tareaPadre);
        t.setNombre(nombre);
        t.setFechaInicio(inicio);
        t.setFechaFin(fin);
        t.setPorcentajeAvance(avance);
        t.setPresupuestoEstimado(presupuesto);
        t.setCostoEjecutado(costo);
        t.setResponsable(responsable);
        return tareaRepository.save(t);
    }

    private void crearDependencia(Tarea origen, Tarea destino, TipoDependencia tipo) {
        DependenciaTarea d = new DependenciaTarea();
        d.setTareaOrigen(origen);
        d.setTareaDestino(destino);
        d.setTipo(tipo);
        dependenciaRepository.save(d);
    }

    private void crearAsignacion(Tarea tarea, Usuario usuario, BigDecimal hrsEst, BigDecimal hrsReales) {
        RecursoTarea r = new RecursoTarea();
        r.setTarea(tarea);
        r.setUsuario(usuario);
        r.setHorasEstimadas(hrsEst);
        r.setHorasReales(hrsReales);
        recursoTareaRepository.save(r);
    }

    private void crearAuditoria(Usuario usuario, String accion, String entidad, Integer idEntidad, LocalDateTime fecha) {
        Auditoria a = new Auditoria(usuario, accion, entidad, idEntidad);
        a.setFecha(fecha);
        auditoriaRepository.save(a);
    }
}
