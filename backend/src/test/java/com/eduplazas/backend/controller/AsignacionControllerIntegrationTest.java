package com.eduplazas.backend.controller;

import com.eduplazas.backend.model.*;
import com.eduplazas.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AsignacionControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired UniversidadRepository universidadRepository;
    @Autowired OfertaRepository ofertaRepository;
    @Autowired ConvocatoriaRepository convocatoriaRepository;
    @Autowired SolicitanteRepository solicitanteRepository;
    @Autowired SolicitudRepository solicitudRepository;
    @Autowired AsignacionRepository asignacionRepository;
    @Autowired UsuarioRepository usuarioRepository;

    private Universidad univ;
    private Convocatoria convocatoria;
    private Oferta ofertaMedicina;
    private Oferta ofertaBiologia;
    private Solicitante ana;
    private Solicitante carlos;

    @BeforeEach
    void setUp() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        convocatoria = new Convocatoria();
        convocatoria.setNombre("Test EvAU");
        convocatoria.setFechaInicio(LocalDate.now());
        convocatoria.setFechaFin(LocalDate.now().plusDays(30));
        convocatoria.setEstado("ABIERTA");
        convocatoriaRepository.save(convocatoria);

        univ = new Universidad();
        univ.setNombre("Universidad Test");
        univ.setProvincia("Madrid");
        universidadRepository.save(univ);

        ofertaMedicina = new Oferta();
        ofertaMedicina.setGrado("Medicina");
        ofertaMedicina.setPlazas(1);
        ofertaMedicina.setUniversidad(univ);
        ofertaMedicina.setConvocatoria(convocatoria);
        ofertaMedicina.setCriterios(List.of());
        ofertaRepository.save(ofertaMedicina);

        ofertaBiologia = new Oferta();
        ofertaBiologia.setGrado("Biología");
        ofertaBiologia.setPlazas(1);
        ofertaBiologia.setUniversidad(univ);
        ofertaBiologia.setConvocatoria(convocatoria);
        ofertaBiologia.setCriterios(List.of());
        ofertaRepository.save(ofertaBiologia);

        Usuario uAna = new Usuario();
        uAna.setNombre("Ana"); uAna.setApellidos("Test");
        uAna.setEmail("ana.test@eduplazas.es");
        uAna.setPassword(encoder.encode("1234"));
        uAna.setDni("99999999A"); uAna.setIdEvau("TEST-001");
        uAna.setRol("ESTUDIANTE");
        usuarioRepository.save(uAna);

        Usuario uCarlos = new Usuario();
        uCarlos.setNombre("Carlos"); uCarlos.setApellidos("Test");
        uCarlos.setEmail("carlos.test@eduplazas.es");
        uCarlos.setPassword(encoder.encode("1234"));
        uCarlos.setDni("99999999B"); uCarlos.setIdEvau("TEST-002");
        uCarlos.setRol("ESTUDIANTE");
        usuarioRepository.save(uCarlos);

        ana = new Solicitante();
        ana.setNombre("Ana"); ana.setApellidos("Test");
        ana.setEmail("ana.test@eduplazas.es");
        ana.setNotaBase(8.0);
        ana.setNotas(List.of());
        ana.setUsuario(uAna);
        solicitanteRepository.save(ana);

        carlos = new Solicitante();
        carlos.setNombre("Carlos"); carlos.setApellidos("Test");
        carlos.setEmail("carlos.test@eduplazas.es");
        carlos.setNotaBase(6.0);
        carlos.setNotas(List.of());
        carlos.setUsuario(uCarlos);
        solicitanteRepository.save(carlos);

        Solicitud solAna = new Solicitud();
        solAna.setSolicitante(ana);
        solAna.setConvocatoria(convocatoria);
        solAna.setEstado("ENVIADA");
        solAna.setPreferencias(List.of(ofertaMedicina, ofertaBiologia));
        solicitudRepository.save(solAna);

        Solicitud solCarlos = new Solicitud();
        solCarlos.setSolicitante(carlos);
        solCarlos.setConvocatoria(convocatoria);
        solCarlos.setEstado("ENVIADA");
        solCarlos.setPreferencias(List.of(ofertaMedicina, ofertaBiologia));
        solicitudRepository.save(solCarlos);
    }

    @Test
    void procesarAsignaciones_retornaOk() throws Exception {
        mockMvc.perform(post("/asignaciones/procesar")
                .param("universidadId", univ.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("correctamente")));
    }

    @Test
    void procesarAsignaciones_anaObtieneMedicina() throws Exception {
        mockMvc.perform(post("/asignaciones/procesar")
                .param("universidadId", univ.getId().toString()));

        mockMvc.perform(get("/asignaciones/estudiante/" + ana.getUsuario().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.oferta.grado").value("Medicina"));
    }

    @Test
    void procesarAsignaciones_carlosObtieneBiologia() throws Exception {
        mockMvc.perform(post("/asignaciones/procesar")
                .param("universidadId", univ.getId().toString()));

        mockMvc.perform(get("/asignaciones/estudiante/" + carlos.getUsuario().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.oferta.grado").value("Biología"));
    }

    @Test
    void obtenerAsignacion_usuarioSinAsignacion_retorna404() throws Exception {
        mockMvc.perform(get("/asignaciones/estudiante/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerTodas_retornaLista() throws Exception {
        mockMvc.perform(post("/asignaciones/procesar")
                .param("universidadId", univ.getId().toString()));

        mockMvc.perform(get("/asignaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    void procesarDosVeces_noGeneraDuplicados() throws Exception {
        mockMvc.perform(post("/asignaciones/procesar")
                .param("universidadId", univ.getId().toString()));
        mockMvc.perform(post("/asignaciones/procesar")
                .param("universidadId", univ.getId().toString()));

        mockMvc.perform(get("/asignaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))); // solo 2 asignaciones, no 4
    }
}
