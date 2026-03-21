package com.eduplazas.backend.config;

import com.eduplazas.backend.model.*;
import com.eduplazas.backend.repository.*;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    ApplicationRunner loadData(
            ConvocatoriaRepository convocatoriaRepo,
            UniversidadRepository universidadRepo,
            OfertaRepository ofertaRepo,
            SolicitanteRepository solicitanteRepo,
            SolicitudRepository solicitudRepo,
            UsuarioRepository usuarioRepo) {

        return args -> {

            // Usuario admin
            Usuario admin = new Usuario();
            admin.setEmail("admin");
            admin.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("admin1234"));
            admin.setRol("ADMIN");
            admin.setNombre("Admin");
            admin.setApellidos("Admin");
            universidadRepo.findAll().stream().findFirst().ifPresent(admin::setUniversidad);
            usuarioRepo.save(admin);

            // Convocatoria
            Convocatoria conv = new Convocatoria();
            conv.setNombre("EvAU Madrid 2025");
            conv.setFechaInicio(LocalDate.of(2025, 6, 1));
            conv.setFechaFin(LocalDate.of(2025, 6, 30));
            conv.setEstado("ABIERTA");
            convocatoriaRepo.save(conv);

            // Universidades
            Universidad ucm = new Universidad();
            ucm.setNombre("Universidad Complutense de Madrid");
            ucm.setProvincia("Madrid");
            universidadRepo.save(ucm);

            Universidad upm = new Universidad();
            upm.setNombre("Universidad Politécnica de Madrid");
            upm.setProvincia("Madrid");
            universidadRepo.save(upm);

            Universidad uam = new Universidad();
            uam.setNombre("Universidad Autónoma de Madrid");
            uam.setProvincia("Madrid");
            universidadRepo.save(uam);

            Universidad uc3m = new Universidad();
            uc3m.setNombre("Universidad Carlos III de Madrid");
            uc3m.setProvincia("Madrid");
            universidadRepo.save(uc3m);

            Universidad urjc = new Universidad();
            urjc.setNombre("Universidad Rey Juan Carlos");
            urjc.setProvincia("Madrid");
            universidadRepo.save(urjc);

            Universidad uah = new Universidad();
            uah.setNombre("Universidad de Alcalá");
            uah.setProvincia("Madrid");
            universidadRepo.save(uah);

            Universidad uned = new Universidad();
            uned.setNombre("Universidad Nacional de Educación a Distancia");
            uned.setProvincia("Madrid");
            universidadRepo.save(uned);

            // Ofertas con criterios
            Oferta oferta1 = new Oferta();
            oferta1.setGrado("Ingeniería Informática");
            oferta1.setPlazas(120);
            oferta1.setUniversidad(upm);
            oferta1.setConvocatoria(conv);

            CriterioAdmision c1 = new CriterioAdmision();
            c1.setAsignatura("Matemáticas II");
            c1.setPeso(0.2);
            c1.setOferta(oferta1);

            CriterioAdmision c2 = new CriterioAdmision();
            c2.setAsignatura("Física");
            c2.setPeso(0.1);
            c2.setOferta(oferta1);

            oferta1.setCriterios(List.of(c1, c2));
            ofertaRepo.save(oferta1);

            Oferta oferta2 = new Oferta();
            oferta2.setGrado("Medicina");
            oferta2.setPlazas(80);
            oferta2.setUniversidad(ucm);
            oferta2.setConvocatoria(conv);

            CriterioAdmision c3 = new CriterioAdmision();
            c3.setAsignatura("Biología");
            c3.setPeso(0.2);
            c3.setOferta(oferta2);

            CriterioAdmision c4 = new CriterioAdmision();
            c4.setAsignatura("Química");
            c4.setPeso(0.2);
            c4.setOferta(oferta2);

            oferta2.setCriterios(List.of(c3, c4));
            ofertaRepo.save(oferta2);

            // Solicitantes
            Solicitante s1 = new Solicitante();
            s1.setNombre("Ana");
            s1.setApellidos("García López");
            s1.setEmail("ana.garcia@email.com");
            s1.setNotaBase(12.5);

            NotaAsignatura n1 = new NotaAsignatura();
            n1.setAsignatura("Matemáticas II");
            n1.setNota(9.5);
            n1.setSolicitante(s1);

            NotaAsignatura n2 = new NotaAsignatura();
            n2.setAsignatura("Física");
            n2.setNota(8.0);
            n2.setSolicitante(s1);

            s1.setNotas(List.of(n1, n2));
            solicitanteRepo.save(s1);

            Solicitante s2 = new Solicitante();
            s2.setNombre("Carlos");
            s2.setApellidos("Martínez Ruiz");
            s2.setEmail("carlos.martinez@email.com");
            s2.setNotaBase(11.0);

            NotaAsignatura n3 = new NotaAsignatura();
            n3.setAsignatura("Biología");
            n3.setNota(9.0);
            n3.setSolicitante(s2);

            NotaAsignatura n4 = new NotaAsignatura();
            n4.setAsignatura("Química");
            n4.setNota(8.5);
            n4.setSolicitante(s2);

            s2.setNotas(List.of(n3, n4));
            solicitanteRepo.save(s2);

            // Solicitudes
            Solicitud sol1 = new Solicitud();
            sol1.setSolicitante(s1);
            sol1.setConvocatoria(conv);
            sol1.setEstado("ENVIADA");
            sol1.setPreferencias(List.of(oferta1, oferta2));
            solicitudRepo.save(sol1);

            Solicitud sol2 = new Solicitud();
            sol2.setSolicitante(s2);
            sol2.setConvocatoria(conv);
            sol2.setEstado("ENVIADA");
            sol2.setPreferencias(List.of(oferta2, oferta1));
            solicitudRepo.save(sol2);

            System.out.println("✅ Datos de ejemplo cargados correctamente");
        };
    }
}