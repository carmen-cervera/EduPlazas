package com.eduplazas.backend.service;

import com.eduplazas.backend.model.*;
import com.eduplazas.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    @Mock private SolicitudRepository solicitudRepository;
    @Mock private SolicitanteRepository solicitanteRepository;
    @Mock private ConvocatoriaRepository convocatoriaRepository;
    @Mock private OfertaRepository ofertaRepository;
    @Mock private NotaAsignaturaRepository notaAsignaturaRepository;

    @InjectMocks
    private SolicitudService solicitudService;

    private Convocatoria convocatoriaAbierta;
    private Solicitante solicitante;
    private Oferta oferta1;
    private Oferta oferta2;

    @BeforeEach
    void setUp() {
        convocatoriaAbierta = new Convocatoria();
        convocatoriaAbierta.setId(1L);
        convocatoriaAbierta.setNombre("EvAU Junio 2026");
        convocatoriaAbierta.setEstado("ABIERTA");

        solicitante = new Solicitante();
        solicitante.setId(1L);
        solicitante.setNombre("Ana");

        oferta1 = new Oferta();
        oferta1.setId(10L);
        oferta1.setGrado("Medicina");
        oferta1.setConvocatoria(convocatoriaAbierta);

        oferta2 = new Oferta();
        oferta2.setId(11L);
        oferta2.setGrado("Biología");
        oferta2.setConvocatoria(convocatoriaAbierta);
    }

    // --- crearSolicitud ---

    @Test
    void crearSolicitud_sinSolicitante_lanzaExcepcion() {
        Solicitud solicitud = new Solicitud();
        solicitud.setConvocatoria(convocatoriaAbierta);
        solicitud.setPreferencias(List.of(oferta1));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> solicitudService.crearSolicitud(solicitud));
        assertTrue(ex.getMessage().contains("solicitante"));
    }

    @Test
    void crearSolicitud_sinConvocatoria_lanzaExcepcion() {
        Solicitud solicitud = new Solicitud();
        solicitud.setSolicitante(solicitante);
        solicitud.setPreferencias(List.of(oferta1));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> solicitudService.crearSolicitud(solicitud));
        assertTrue(ex.getMessage().contains("convocatoria"));
    }

    @Test
    void crearSolicitud_sinPreferencias_lanzaExcepcion() {
        Solicitud solicitud = new Solicitud();
        solicitud.setSolicitante(solicitante);
        solicitud.setConvocatoria(convocatoriaAbierta);
        solicitud.setPreferencias(List.of());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> solicitudService.crearSolicitud(solicitud));
        assertTrue(ex.getMessage().contains("oferta"));
    }

    @Test
    void crearSolicitud_convocatoriaCerrada_lanzaExcepcion() {
        convocatoriaAbierta.setEstado("CERRADA");

        Solicitud solicitud = new Solicitud();
        solicitud.setSolicitante(solicitante);
        solicitud.setConvocatoria(convocatoriaAbierta);
        solicitud.setPreferencias(List.of(oferta1));

        when(solicitanteRepository.findById(1L)).thenReturn(Optional.of(solicitante));
        when(convocatoriaRepository.findById(1L)).thenReturn(Optional.of(convocatoriaAbierta));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> solicitudService.crearSolicitud(solicitud));
        assertTrue(ex.getMessage().contains("abierta"));
    }

    @Test
    void crearSolicitud_solicitudDuplicada_lanzaExcepcion() {
        Solicitud solicitud = new Solicitud();
        solicitud.setSolicitante(solicitante);
        solicitud.setConvocatoria(convocatoriaAbierta);
        solicitud.setPreferencias(List.of(oferta1));

        when(solicitanteRepository.findById(1L)).thenReturn(Optional.of(solicitante));
        when(convocatoriaRepository.findById(1L)).thenReturn(Optional.of(convocatoriaAbierta));
        when(solicitudRepository.findBySolicitanteIdAndConvocatoriaId(1L, 1L))
                .thenReturn(Optional.of(new Solicitud()));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> solicitudService.crearSolicitud(solicitud));
        assertTrue(ex.getMessage().contains("más de una solicitud"));
    }

    @Test
    void crearSolicitud_ofertasRepetidas_lanzaExcepcion() {
        Solicitud solicitud = new Solicitud();
        solicitud.setSolicitante(solicitante);
        solicitud.setConvocatoria(convocatoriaAbierta);
        solicitud.setPreferencias(List.of(oferta1, oferta1)); // duplicado

        when(solicitanteRepository.findById(1L)).thenReturn(Optional.of(solicitante));
        when(convocatoriaRepository.findById(1L)).thenReturn(Optional.of(convocatoriaAbierta));
        when(solicitudRepository.findBySolicitanteIdAndConvocatoriaId(1L, 1L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> solicitudService.crearSolicitud(solicitud));
        assertTrue(ex.getMessage().contains("repetidos"));
    }

    @Test
    void crearSolicitud_ofertaDeOtraConvocatoria_lanzaExcepcion() {
        Convocatoria otraConv = new Convocatoria();
        otraConv.setId(99L);
        oferta2.setConvocatoria(otraConv);

        Solicitud solicitud = new Solicitud();
        solicitud.setSolicitante(solicitante);
        solicitud.setConvocatoria(convocatoriaAbierta);
        solicitud.setPreferencias(List.of(oferta1, oferta2));

        when(solicitanteRepository.findById(1L)).thenReturn(Optional.of(solicitante));
        when(convocatoriaRepository.findById(1L)).thenReturn(Optional.of(convocatoriaAbierta));
        when(solicitudRepository.findBySolicitanteIdAndConvocatoriaId(1L, 1L))
                .thenReturn(Optional.empty());
        when(ofertaRepository.findAllById(any())).thenReturn(List.of(oferta1, oferta2));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> solicitudService.crearSolicitud(solicitud));
        assertTrue(ex.getMessage().contains("convocatoria"));
    }

    @Test
    void crearSolicitud_correcta_guardaConEstadoEnviada() {
        Solicitud solicitud = new Solicitud();
        solicitud.setSolicitante(solicitante);
        solicitud.setConvocatoria(convocatoriaAbierta);
        solicitud.setPreferencias(List.of(oferta1, oferta2));

        when(solicitanteRepository.findById(1L)).thenReturn(Optional.of(solicitante));
        when(convocatoriaRepository.findById(1L)).thenReturn(Optional.of(convocatoriaAbierta));
        when(solicitudRepository.findBySolicitanteIdAndConvocatoriaId(1L, 1L))
                .thenReturn(Optional.empty());
        when(ofertaRepository.findAllById(any())).thenReturn(List.of(oferta1, oferta2));
        when(solicitudRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Solicitud resultado = solicitudService.crearSolicitud(solicitud);

        assertEquals("ENVIADA", resultado.getEstado());
        verify(solicitudRepository).save(any());
    }

    // --- guardarNotas ---

    @Test
    void guardarNotas_calculaNotaBaseCorrectamente() {
        // bach(9)*0.6 + Lengua(8)*0.1 + Historia(7)*0.1 + Inglés(8)*0.1 + Matemáticas(9)*0.1
        // = 5.4 + 0.8 + 0.7 + 0.8 + 0.9 = 8.6
        when(solicitanteRepository.findById(1L)).thenReturn(Optional.of(solicitante));
        doNothing().when(notaAsignaturaRepository).deleteBySolicitanteId(1L);
        when(notaAsignaturaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(solicitanteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        List<NotaAsignatura> notas = List.of(
                nota("Bachillerato", 9.0),
                nota("Lengua Castellana", 8.0),
                nota("Historia de España", 7.0),
                nota("Inglés", 8.0),
                nota("Matemáticas", 9.0)
        );

        solicitudService.guardarNotas(1L, notas);

        verify(solicitanteRepository).save(argThat(s -> Math.abs(s.getNotaBase() - 8.6) < 0.01));
    }

    @Test
    void guardarNotas_asignaturasEspecificas_noSumanANotaBase() {
        // Las asignaturas específicas (Biología, Física...) no se suman a notaBase
        // Solo bach*0.6 = 7*0.6 = 4.2
        when(solicitanteRepository.findById(1L)).thenReturn(Optional.of(solicitante));
        doNothing().when(notaAsignaturaRepository).deleteBySolicitanteId(1L);
        when(notaAsignaturaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(solicitanteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        List<NotaAsignatura> notas = List.of(
                nota("Bachillerato", 7.0),
                nota("Biología", 9.0) // específica, no cuenta en notaBase
        );

        solicitudService.guardarNotas(1L, notas);

        verify(solicitanteRepository).save(argThat(s -> Math.abs(s.getNotaBase() - 4.2) < 0.01));
    }

    @Test
    void guardarNotas_solicitanteInexistente_lanzaExcepcion() {
        when(solicitanteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> solicitudService.guardarNotas(99L, List.of()));
    }

    private NotaAsignatura nota(String asignatura, double valor) {
        NotaAsignatura n = new NotaAsignatura();
        n.setAsignatura(asignatura);
        n.setNota(valor);
        return n;
    }
}
