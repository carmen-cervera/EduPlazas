package com.eduplazas.backend.service;

import com.eduplazas.backend.model.*;
import com.eduplazas.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AsignacionServiceTest {

    @Mock private SolicitudRepository solicitudRepository;
    @Mock private AsignacionRepository asignacionRepository;
    @Mock private OfertaRepository ofertaRepository;
    @Mock private UniversidadRepository universidadRepository;

    @InjectMocks
    private AsignacionService asignacionService;

    private Universidad univA;
    private Oferta ofertaMedicina;   // 1 plaza — univA
    private Oferta ofertaBiologia;   // 2 plazas — univA
    private Solicitante ana;
    private Solicitante carlos;

    @BeforeEach
    void setUp() {
        univA = new Universidad();
        univA.setId(1L);
        univA.setNombre("Universidad A");
        univA.setListaParaAsignar(false);

        ofertaMedicina = oferta(10L, "Medicina", 1, univA, List.of());
        ofertaBiologia = oferta(11L, "Biología", 2, univA, List.of());

        ana = solicitante(1L, "Ana", 8.0);
        carlos = solicitante(2L, "Carlos", 6.0);
    }

    // --- Prioridades ---

    @Test
    void alumnoConMayorNotaObtienePrimeraOpcion() {
        // Ana (8.0) y Carlos (6.0) quieren Medicina (1 plaza) como prioridad 1
        // Ana debe quedarse Medicina, Carlos pasar a Biología
        Solicitud solAna = solicitud(ana, List.of(ofertaMedicina, ofertaBiologia));
        Solicitud solCarlos = solicitud(carlos, List.of(ofertaMedicina, ofertaBiologia));

        configurarMocks(univA, List.of(ofertaMedicina, ofertaBiologia),
                List.of(solAna, solCarlos), List.of());

        asignacionService.procesarAsignaciones(1L);

        ArgumentCaptor<Asignacion> captor = ArgumentCaptor.forClass(Asignacion.class);
        verify(asignacionRepository, times(2)).save(captor.capture());

        List<Asignacion> asignadas = captor.getAllValues();
        Asignacion asignAna = asignadas.stream()
                .filter(a -> a.getSolicitante().getId().equals(1L)).findFirst().orElseThrow();
        Asignacion asignCarlos = asignadas.stream()
                .filter(a -> a.getSolicitante().getId().equals(2L)).findFirst().orElseThrow();

        assertEquals("Medicina", asignAna.getOferta().getGrado());
        assertEquals("Biología", asignCarlos.getOferta().getGrado());
    }

    @Test
    void alumnoSinPlazaDisponible_noRecibePlaza() {
        // Medicina tiene 1 plaza, solo ana puede entrar
        // Carlos no tiene más preferencias en esta universidad
        Solicitud solAna = solicitud(ana, List.of(ofertaMedicina));
        Solicitud solCarlos = solicitud(carlos, List.of(ofertaMedicina));

        configurarMocks(univA, List.of(ofertaMedicina, ofertaBiologia),
                List.of(solAna, solCarlos), List.of());

        asignacionService.procesarAsignaciones(1L);

        ArgumentCaptor<Asignacion> captor = ArgumentCaptor.forClass(Asignacion.class);
        verify(asignacionRepository, times(1)).save(captor.capture());

        assertEquals("Ana", captor.getValue().getSolicitante().getNombre());
    }

    @Test
    void alumnoConCriterioEspecifico_sumaNotaPonderada() {
        // Criterio: Biología con peso 0.2
        // Ana tiene Biología=10 → notaFinal = 8.0 + 10*0.2 = 10.0
        // Carlos tiene Biología=5 → notaFinal = 6.0 + 5*0.2 = 7.0
        CriterioAdmision criterio = new CriterioAdmision();
        criterio.setAsignatura("Biología");
        criterio.setPeso(0.2);

        Oferta ofertaConCriterio = oferta(12L, "Veterinaria", 1, univA, List.of(criterio));

        NotaAsignatura notaAna = new NotaAsignatura();
        notaAna.setAsignatura("Biología");
        notaAna.setNota(10.0);
        ana.setNotas(List.of(notaAna));

        NotaAsignatura notaCarlos = new NotaAsignatura();
        notaCarlos.setAsignatura("Biología");
        notaCarlos.setNota(5.0);
        carlos.setNotas(List.of(notaCarlos));

        Solicitud solAna = solicitud(ana, List.of(ofertaConCriterio));
        Solicitud solCarlos = solicitud(carlos, List.of(ofertaConCriterio));

        configurarMocks(univA, List.of(ofertaConCriterio),
                List.of(solAna, solCarlos), List.of());

        asignacionService.procesarAsignaciones(1L);

        ArgumentCaptor<Asignacion> captor = ArgumentCaptor.forClass(Asignacion.class);
        verify(asignacionRepository, times(1)).save(captor.capture());

        Asignacion asignada = captor.getValue();
        assertEquals("Ana", asignada.getSolicitante().getNombre());
        assertEquals(10.0, asignada.getNotaFinal(), 0.01);
    }

    @Test
    void reprocesarAsignaciones_borraPreviasYRecalcula() {
        // Al reprocesar se deben borrar las asignaciones existentes de esta universidad
        Asignacion previa = new Asignacion();
        previa.setOferta(ofertaMedicina);

        Solicitud solAna = solicitud(ana, List.of(ofertaMedicina));

        configurarMocks(univA, List.of(ofertaMedicina, ofertaBiologia),
                List.of(solAna), List.of(previa));

        asignacionService.procesarAsignaciones(1L);

        verify(asignacionRepository).deleteAll(argThat(lista ->
                ((List<?>) lista).size() == 1));
    }

    @Test
    void dosAlumnosDosPlazas_ambosAsignados() {
        // Biología tiene 2 plazas — ambos deben entrar
        Solicitud solAna = solicitud(ana, List.of(ofertaBiologia));
        Solicitud solCarlos = solicitud(carlos, List.of(ofertaBiologia));

        configurarMocks(univA, List.of(ofertaMedicina, ofertaBiologia),
                List.of(solAna, solCarlos), List.of());

        asignacionService.procesarAsignaciones(1L);

        verify(asignacionRepository, times(2)).save(any());
    }

    @Test
    void alumnoExpulsadoPorMejorCandidato_pasaASiguienteOpcion() {
        // Carlos entra primero en Medicina (llega antes en la cola).
        // Ana propone Medicina y tiene mejor nota → expulsa a Carlos.
        // Carlos pasa a Biología.
        Solicitante laura = solicitante(3L, "Laura", 7.5);
        Solicitud solAna = solicitud(ana, List.of(ofertaMedicina));
        Solicitud solCarlos = solicitud(carlos, List.of(ofertaMedicina, ofertaBiologia));
        Solicitud solLaura = solicitud(laura, List.of(ofertaMedicina, ofertaBiologia));

        configurarMocks(univA, List.of(ofertaMedicina, ofertaBiologia),
                List.of(solAna, solCarlos, solLaura), List.of());

        asignacionService.procesarAsignaciones(1L);

        ArgumentCaptor<Asignacion> captor = ArgumentCaptor.forClass(Asignacion.class);
        verify(asignacionRepository, atLeast(2)).save(captor.capture());

        List<Asignacion> asignadas = captor.getAllValues();
        // Ana debe estar en Medicina
        assertTrue(asignadas.stream().anyMatch(a ->
                a.getSolicitante().getId().equals(1L) &&
                a.getOferta().getGrado().equals("Medicina")));
        // Carlos (nota más baja) debe estar en Biología
        assertTrue(asignadas.stream().anyMatch(a ->
                a.getSolicitante().getId().equals(2L) &&
                a.getOferta().getGrado().equals("Biología")));
    }

    // --- Helpers ---

    private void configurarMocks(Universidad univ,
                                  List<Oferta> ofertasAll,
                                  List<Solicitud> solicitudes,
                                  List<Asignacion> asignacionesPrevias) {
        when(universidadRepository.findById(univ.getId())).thenReturn(Optional.of(univ));
        when(universidadRepository.findAll()).thenReturn(List.of(univ));
        when(ofertaRepository.findAll()).thenReturn(ofertasAll);
        when(solicitudRepository.findAll()).thenReturn(solicitudes);
        when(asignacionRepository.findAll()).thenReturn(asignacionesPrevias);
        when(asignacionRepository.findAllBySolicitanteId(any())).thenReturn(List.of());
        doNothing().when(asignacionRepository).deleteAll(any());
        doNothing().when(asignacionRepository).flush();
        when(asignacionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(universidadRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    private Oferta oferta(Long id, String grado, int plazas, Universidad univ, List<CriterioAdmision> criterios) {
        Oferta o = new Oferta();
        o.setId(id);
        o.setGrado(grado);
        o.setPlazas(plazas);
        o.setUniversidad(univ);
        o.setCriterios(criterios);
        return o;
    }

    private Solicitante solicitante(Long id, String nombre, double notaBase) {
        Solicitante s = new Solicitante();
        s.setId(id);
        s.setNombre(nombre);
        s.setNotaBase(notaBase);
        s.setNotas(new ArrayList<>());
        return s;
    }

    private Solicitud solicitud(Solicitante solicitante, List<Oferta> preferencias) {
        Solicitud s = new Solicitud();
        s.setSolicitante(solicitante);
        s.setPreferencias(new ArrayList<>(preferencias));
        return s;
    }
}
